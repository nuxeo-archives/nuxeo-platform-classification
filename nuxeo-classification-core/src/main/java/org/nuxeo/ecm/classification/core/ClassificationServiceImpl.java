/*
 * (C) Copyright 2006-2009 Nuxeo SAS (http://nuxeo.com/) and contributors.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Lesser General Public License
 * (LGPL) version 2.1 which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/lgpl.html
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * Contributors:
 *     ldoguin
 *
 * $Id$
 */

package org.nuxeo.ecm.classification.core;

import java.io.Serializable;
import java.security.Principal;
import java.util.*;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.nuxeo.ecm.classification.api.ClassificationConstants;
import org.nuxeo.ecm.classification.api.ClassificationResult;
import org.nuxeo.ecm.classification.api.ClassificationService;
import org.nuxeo.ecm.classification.api.adapter.Classification;
import org.nuxeo.ecm.core.api.*;
import org.nuxeo.ecm.core.api.event.CoreEventConstants;
import org.nuxeo.ecm.core.api.event.DocumentEventCategories;
import org.nuxeo.ecm.core.api.impl.UserPrincipal;
import org.nuxeo.ecm.core.event.Event;
import org.nuxeo.ecm.core.event.EventProducer;
import org.nuxeo.ecm.core.event.impl.DocumentEventContext;
import org.nuxeo.ecm.core.schema.DocumentType;
import org.nuxeo.ecm.core.schema.SchemaManager;
import org.nuxeo.runtime.api.Framework;
import org.nuxeo.runtime.model.ComponentContext;
import org.nuxeo.runtime.model.ComponentInstance;
import org.nuxeo.runtime.model.DefaultComponent;

import static org.nuxeo.ecm.classification.api.ClassificationConstants.CLASSIFY;
import static org.nuxeo.ecm.classification.api.ClassificationConstants.EVENT_CLASSIFICATION_DONE;
import static org.nuxeo.ecm.classification.api.ClassificationService.CLASSIFY_STATE.*;
import static org.nuxeo.ecm.classification.api.ClassificationService.UNCLASSIFY_STATE.NOT_CLASSIFIED;
import static org.nuxeo.ecm.classification.api.ClassificationService.UNCLASSIFY_STATE.UNCLASSIFIED;

public class ClassificationServiceImpl extends DefaultComponent implements
        ClassificationService {
    public static final String NAME = "org.nuxeo.ecm.classification.core.ClassificationService";

    public static final String TYPES_XP = "types";

    private static final Log log = LogFactory.getLog(ClassificationServiceImpl.class);

    private static List<String> typeList;

    @Override
    public void activate(ComponentContext context) {
        typeList = new LinkedList<String>();
    }

    @Override
    public void deactivate(ComponentContext context) {
        typeList = new LinkedList<String>();
    }

    @Override
    public void registerContribution(Object contribution,
            String extensionPoint, ComponentInstance contributor) {
        if (extensionPoint.equals(TYPES_XP)) {
            ClassificationDescriptor classificationDesc = (ClassificationDescriptor) contribution;
            String typeName = classificationDesc.getType();
            if (classificationDesc.isEnabled()) {
                typeList.add(typeName);
            } else {
                if (typeList.contains(typeName)) {
                    typeList.remove(typeName);
                }
            }
        } else {
            log.error("Extension point " + extensionPoint + "is unknown");
        }
    }

    public List<String> getClassifiableDocumentTypes() {
        return typeList;
    }

    public boolean isClassifiable(String docType) {
        if (typeList.contains(docType)) {
            return true;
        }

        SchemaManager schemaManager = Framework.getLocalService(SchemaManager.class);
        DocumentType documentType = schemaManager.getDocumentType(docType);
        if (documentType == null) {
            log.warn("Trying to access an unregistered DocType: " + docType);
            return false;
        }

        Set<String> facets = documentType.getFacets();
        return facets.contains(ClassificationConstants.CLASSIFIABLE_FACET);
    }

    public boolean isClassifiable(DocumentModel doc) {
        return doc.hasFacet(ClassificationConstants.CLASSIFIABLE_FACET)
                || typeList.contains(doc.getType());
    }

    @Override
    public ClassificationResult<CLASSIFY_STATE> classify(
            DocumentModel classificationFolder,
            Collection<DocumentModel> targetDocs) throws ClientException {
        ClassificationResult<CLASSIFY_STATE> result = new ClassificationResult<CLASSIFY_STATE>();
        CoreSession session = classificationFolder.getCoreSession();
        if (session == null) {
            throw new ClientException(
                    "Unable to get session from classification folder");
        }

        if (!session.hasPermission(classificationFolder.getRef(), CLASSIFY)) {
            throw new DocumentSecurityException(
                    "Not enough rights to classify doc");
        }

        String targetNotificationComment = String.format("%s:%s",
                session.getRepositoryName(), classificationFolder.getId());

        Classification classification = classificationFolder.getAdapter(Classification.class);
        for (DocumentModel targetDoc : targetDocs) {
            if (!isClassifiable(targetDoc)) {
                result.add(INVALID, targetDoc.getId());
                continue;
            }

            if (classification.contains(targetDoc.getId())) {
                result.add(ALREADY_CLASSIFIED, targetDoc.getId());
                continue;
            }

            result.add(CLASSIFIED, targetDoc.getId());
            classification.add(targetDoc.getId());

            // notify on classification folder
            String comment = String.format("%s:%s",
                    session.getRepositoryName(), targetDoc.getId());
            notifyEvent(session, EVENT_CLASSIFICATION_DONE,
                    classificationFolder, null, comment, null, null);
            // notify on each classified document
            notifyEvent(session, EVENT_CLASSIFICATION_DONE, targetDoc, null,
                    targetNotificationComment, null, null);
        }

        session.saveDocument(classification.getDocument());

        return result;
    }

    @Override
    public ClassificationResult<UNCLASSIFY_STATE> unClassify(
            DocumentModel classificationFolder, Collection<String> targetDocs)
            throws ClientException {
        ClassificationResult<UNCLASSIFY_STATE> result = new ClassificationResult<UNCLASSIFY_STATE>();
        CoreSession session = classificationFolder.getCoreSession();
        if (session == null) {
            throw new ClientException(
                    "Unable to get session from classification folder");
        }

        if (!session.hasPermission(classificationFolder.getRef(), CLASSIFY)) {
            throw new DocumentSecurityException(
                    "Not enough rights to unclassify on document " + classificationFolder.getPathAsString());
        }

        Classification classification = classificationFolder.getAdapter(Classification.class);
        for (String docId : targetDocs) {
            if (classification.contains(docId)) {
                classification.remove(docId);
                result.add(UNCLASSIFIED, docId);

                String comment = String.format("%s:%s",
                        session.getRepositoryName(), docId);
                notifyEvent(session,
                        ClassificationConstants.EVENT_UNCLASSIFICATION_DONE,
                        classificationFolder, null, comment, null, null);
                // notify on each classified document
                DocumentModel targetDoc = session.getDocument(new IdRef(docId));
                if (targetDoc != null) {
                    String targetNotificationComment = String.format("%s:%s",
                            session.getRepositoryName(),
                            classificationFolder.getId());
                    notifyEvent(
                            session,
                            ClassificationConstants.EVENT_UNCLASSIFICATION_DONE,
                            targetDoc, null, targetNotificationComment, null,
                            null);
                }
            } else {
                result.add(NOT_CLASSIFIED, docId);
            }
        }

        session.saveDocument(classification.getDocument());

        return result;
    }

    protected static void notifyEvent(CoreSession coreSession, String eventId,
            DocumentModel source, String category, String comment,
            String author, Map<String, Serializable> options)
            throws ClientException {

        // Default category
        if (category == null) {
            category = DocumentEventCategories.EVENT_DOCUMENT_CATEGORY;
        }

        if (options == null) {
            options = new HashMap<String, Serializable>();
        }

        // Name of the current repository
        options.put(CoreEventConstants.REPOSITORY_NAME,
                coreSession.getRepositoryName());

        // Document life cycle
        if (source != null) {
            String currentLifeCycleState = null;
            try {
                currentLifeCycleState = source.getCurrentLifeCycleState();
            } catch (ClientException err) {
                // FIXME no lifecycle -- this shouldn't generated an
                // exception (and ClientException logs the spurious error)
            }
            options.put(CoreEventConstants.DOC_LIFE_CYCLE,
                    currentLifeCycleState);
        }
        // Add the session ID
        options.put(CoreEventConstants.SESSION_ID, coreSession.getSessionId());

        Principal principal;
        if (author != null) {
            // make fake principal for logs
            principal = new UserPrincipal(author);
        } else {
            principal = coreSession.getPrincipal();
        }

        DocumentEventContext ctx = new DocumentEventContext(coreSession,
                principal, source);
        ctx.setCategory(category);
        ctx.setComment(comment);
        ctx.setProperties(options);
        Event event = ctx.newEvent(eventId);

        try {
            EventProducer evtProducer = Framework.getService(EventProducer.class);
            log.debug("Notify RepositoryEventListener listeners list for event="
                    + eventId);
            evtProducer.fireEvent(event);
        } catch (Exception e) {
            log.error("Impossible to notify core events ! "
                    + "EventProducer service is missing...");
        }
    }

}
