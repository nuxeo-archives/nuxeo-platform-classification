/*
 * (C) Copyright 2006-2009 Nuxeo SA (http://nuxeo.com/) and others.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * Contributors:
 *     ldoguin
 *
 * $Id$
 */

package org.nuxeo.ecm.classification.core;

import static org.nuxeo.ecm.classification.api.ClassificationConstants.CLASSIFY;
import static org.nuxeo.ecm.classification.api.ClassificationConstants.EVENT_CLASSIFICATION_DONE;
import static org.nuxeo.ecm.classification.api.ClassificationService.CLASSIFY_STATE.ALREADY_CLASSIFIED;
import static org.nuxeo.ecm.classification.api.ClassificationService.CLASSIFY_STATE.CLASSIFIED;
import static org.nuxeo.ecm.classification.api.ClassificationService.CLASSIFY_STATE.INVALID;
import static org.nuxeo.ecm.classification.api.ClassificationService.UNCLASSIFY_STATE.NOT_CLASSIFIED;
import static org.nuxeo.ecm.classification.api.ClassificationService.UNCLASSIFY_STATE.NOT_ENOUGH_RIGHTS;
import static org.nuxeo.ecm.classification.api.ClassificationService.UNCLASSIFY_STATE.UNCLASSIFIED;

import java.io.Serializable;
import java.security.Principal;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.nuxeo.ecm.classification.api.ClassificationConstants;
import org.nuxeo.ecm.classification.api.ClassificationResolver;
import org.nuxeo.ecm.classification.api.ClassificationResult;
import org.nuxeo.ecm.classification.api.ClassificationService;
import org.nuxeo.ecm.classification.api.adapter.Classification;
import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.ecm.core.api.DocumentSecurityException;
import org.nuxeo.ecm.core.api.IdRef;
import org.nuxeo.ecm.core.api.NuxeoException;
import org.nuxeo.ecm.core.api.UnrestrictedSessionRunner;
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

public class ClassificationServiceImpl extends DefaultComponent implements ClassificationService {
    public static final String NAME = "org.nuxeo.ecm.classification.core.ClassificationService";

    public static final String TYPES_XP = "types";

    public static final String RESOLVER_XP = "resolvers";

    protected Map<String, ClassificationResolver> resolvers = new HashMap<String, ClassificationResolver>();

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
    public void registerContribution(Object contribution, String extensionPoint, ComponentInstance contributor) {
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
        } else if (RESOLVER_XP.equals(extensionPoint)) {
            ClassificationResolverDescriptor desc = (ClassificationResolverDescriptor) contribution;
            resolvers.put(desc.getName(), desc.getResolverInstance());
        } else {
            log.error("Extension point " + extensionPoint + "is unknown");
        }
    }

    @Override
    public String resolveClassification(CoreSession session, final String name, final String targetDocId)
            {
        if (!resolvers.containsKey(name)) {
            log.warn("reference to a missing resolver (" + name + "); returning the original doc id");
            return targetDocId;
        }

        final String[] realTargetDocId = new String[1];
        new UnrestrictedSessionRunner(session) {
            @Override
            public void run() {
                realTargetDocId[0] = resolvers.get(name).resolve(session, targetDocId);
            }
        }.runUnrestricted();

        return realTargetDocId[0];
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
        return doc.hasFacet(ClassificationConstants.CLASSIFIABLE_FACET) || typeList.contains(doc.getType());
    }

    @Override
    public ClassificationResult<CLASSIFY_STATE> classify(DocumentModel classificationFolder, String resolver,
            Collection<DocumentModel> targetDocs) {
        ClassificationResult<CLASSIFY_STATE> classify = classify(classificationFolder, targetDocs);
        Classification adapter = classificationFolder.getAdapter(Classification.class);

        for (String docId : classify.get(CLASSIFIED)) {
            adapter.addResolver(resolver, docId);
        }

        for (String docId : classify.get(ALREADY_CLASSIFIED)) {
            adapter.addResolver(resolver, docId);
        }

        classificationFolder.getCoreSession().saveDocument(adapter.getDocument());

        return classify;
    }

    @Override
    public ClassificationResult<CLASSIFY_STATE> classify(DocumentModel classificationFolder,
            Collection<DocumentModel> targetDocs) {
        ClassificationResult<CLASSIFY_STATE> result = new ClassificationResult<CLASSIFY_STATE>();
        CoreSession session = classificationFolder.getCoreSession();
        if (session == null) {
            throw new NuxeoException("Unable to get session from classification folder");
        }

        if (!session.hasPermission(classificationFolder.getRef(), CLASSIFY)) {
            throw new DocumentSecurityException("Not enough rights to classify doc");
        }

        String targetNotificationComment = String.format("%s:%s", session.getRepositoryName(),
                classificationFolder.getId());

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
            String comment = String.format("%s:%s", session.getRepositoryName(), targetDoc.getId());
            notifyEvent(session, EVENT_CLASSIFICATION_DONE, classificationFolder, null, comment, null, null);
            // notify on each classified document
            notifyEvent(session, EVENT_CLASSIFICATION_DONE, targetDoc, null, targetNotificationComment, null, null);
        }

        session.saveDocument(classification.getDocument());

        return result;
    }

    @Override
    public ClassificationResult<UNCLASSIFY_STATE> unClassify(DocumentModel classificationFolder,
            Collection<String> targetDocs) {
        ClassificationResult<UNCLASSIFY_STATE> result = new ClassificationResult<UNCLASSIFY_STATE>();
        CoreSession session = classificationFolder.getCoreSession();
        if (session == null) {
            throw new NuxeoException("Unable to get session from classification folder");
        }

        if (!session.hasPermission(classificationFolder.getRef(), CLASSIFY)) {
            throw new DocumentSecurityException("Not enough rights to unclassify on document "
                    + classificationFolder.getPathAsString());
        }

        Classification classification = classificationFolder.getAdapter(Classification.class);
        for (String docId : targetDocs) {
            if (classification.contains(docId)) {
                classification.remove(docId);
                result.add(UNCLASSIFIED, docId);

                String comment = String.format("%s:%s", session.getRepositoryName(), docId);
                notifyEvent(session, ClassificationConstants.EVENT_UNCLASSIFICATION_DONE, classificationFolder, null,
                        comment, null, null);
                // notify on each classified document
                DocumentModel targetDoc = session.getDocument(new IdRef(docId));
                if (targetDoc != null) {
                    String targetNotificationComment = String.format("%s:%s", session.getRepositoryName(),
                            classificationFolder.getId());
                    notifyEvent(session, ClassificationConstants.EVENT_UNCLASSIFICATION_DONE, targetDoc, null,
                            targetNotificationComment, null, null);
                }
            } else {
                result.add(NOT_CLASSIFIED, docId);
            }
        }

        session.saveDocument(classification.getDocument());

        return result;
    }

    @Override
    public ClassificationResult<UNCLASSIFY_STATE> unClassifyFrom(Collection<DocumentModel> classificationFolders,
            String targetId) {
        ClassificationResult<UNCLASSIFY_STATE> result = new ClassificationResult<UNCLASSIFY_STATE>();
        if (classificationFolders == null || classificationFolders.isEmpty()) {
            throw new NuxeoException("Empty classification folders list");
        }

        CoreSession session = ((DocumentModel) classificationFolders.toArray()[0]).getCoreSession();
        Principal principal = session.getPrincipal();

        for (DocumentModel folder : classificationFolders) {
            if (!session.hasPermission(principal, folder.getRef(), CLASSIFY)) {
                result.add(NOT_ENOUGH_RIGHTS, folder.getId());
                continue;
            }

            Classification adapter = folder.getAdapter(Classification.class);
            if (adapter == null) {
                result.add(NOT_CLASSIFIED, folder.getId());
                continue;
            }

            if (!adapter.contains(targetId)) {
                result.add(NOT_CLASSIFIED, folder.getId());
                continue;
            }

            adapter.remove(targetId);
            session.saveDocument(adapter.getDocument());
        }

        return result;
    }

    protected static void notifyEvent(CoreSession coreSession, String eventId, DocumentModel source, String category,
            String comment, String author, Map<String, Serializable> options) {

        // Default category
        if (category == null) {
            category = DocumentEventCategories.EVENT_DOCUMENT_CATEGORY;
        }

        if (options == null) {
            options = new HashMap<String, Serializable>();
        }

        // Name of the current repository
        options.put(CoreEventConstants.REPOSITORY_NAME, coreSession.getRepositoryName());

        // Document life cycle
        if (source != null) {
            String currentLifeCycleState = source.getCurrentLifeCycleState();
            options.put(CoreEventConstants.DOC_LIFE_CYCLE, currentLifeCycleState);
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

        DocumentEventContext ctx = new DocumentEventContext(coreSession, principal, source);
        ctx.setCategory(category);
        ctx.setComment(comment);
        ctx.setProperties(options);
        Event event = ctx.newEvent(eventId);

        try {
            EventProducer evtProducer = Framework.getService(EventProducer.class);
            log.debug("Notify RepositoryEventListener listeners list for event=" + eventId);
            evtProducer.fireEvent(event);
        } catch (Exception e) {
            log.error("Impossible to notify core events ! " + "EventProducer service is missing...");
        }
    }

}
