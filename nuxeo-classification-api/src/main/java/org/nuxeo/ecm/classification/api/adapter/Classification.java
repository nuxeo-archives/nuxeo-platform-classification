/*
 * (C) Copyright 2006-2012 Nuxeo SAS (http://nuxeo.com/) and contributors.
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
 *     Arnaud Kervern <akervern@nuxeo.com>
 */

package org.nuxeo.ecm.classification.api.adapter;

import org.apache.commons.lang.StringUtils;
import org.nuxeo.ecm.classification.api.ClassificationService;
import org.nuxeo.ecm.core.api.*;
import org.nuxeo.ecm.core.api.impl.DocumentModelListImpl;
import org.nuxeo.runtime.api.Framework;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.nuxeo.ecm.classification.api.ClassificationConstants.CLASSIFICATION_TARGETS_PROPERTY_NAME;
import static org.nuxeo.ecm.classification.api.ClassificationConstants.CLASSIFICATION_RESOLVERS_PROPERTY_NAME;
import static org.nuxeo.ecm.core.api.security.SecurityConstants.READ;

/**
 * Classification Adapter
 * 
 * @since 5.7
 * @author akervern
 */
public class Classification {
    protected DocumentModel document;

    public Classification(DocumentModel doc) {
        document = doc;
    }

    /**
     * Returns resolved classified documents using given session.
     * <p>
     * Classified documents are kept on a specific property in the container.
     * 
     * @throws org.nuxeo.ecm.core.api.ClientException
     */
    public DocumentModelList getClassifiedDocuments() throws ClientException {
        DocumentModelList targets = new DocumentModelListImpl();
        CoreSession session = getCoreSession();

        for (String docId : getClassifiedDocumentIds()) {
            DocumentRef documentRef = new IdRef(docId);
            if (session.exists(documentRef) && session.hasPermission(documentRef, READ)) {
                targets.add(session.getDocument(documentRef));
            }
        }

        return targets;
    }

    public void setClassifiedDocumentIds(List<String> documents) throws ClientException {
        document.setPropertyValue(CLASSIFICATION_TARGETS_PROPERTY_NAME, (Serializable) documents);
    }

    protected List<String> getTargetsPropertyValue() throws ClientException {
        List<String> classified = (List<String>) document.getPropertyValue(CLASSIFICATION_TARGETS_PROPERTY_NAME);
        if (classified == null) {
            classified = new ArrayList<String>();
            setClassifiedDocumentIds(classified);
        }
        return classified;
    }

    public List<String> getClassifiedDocumentIds() throws ClientException {
        List<String> classified = getTargetsPropertyValue();

        ClassificationService service = Framework.getLocalService(ClassificationService.class);
        List<String> classifiedResolved = new ArrayList<String>();
        for (String docId : classified) {
            String resolver = getResolver(docId);
            if (!StringUtils.isEmpty(resolver)) {

                String resolved = service.resolveClassification(getCoreSession(), resolver, docId);
                classifiedResolved.add(resolved);
            } else {
                classifiedResolved.add(docId);
            }
        }

        return classifiedResolved;
    }

    public void add(DocumentModel doc) throws ClientException {
        add(doc.getId());
    }

    public void add(String resolver, String docId) throws ClientException {
        add(docId);
        addResolver(resolver, docId);
    }

    public void addResolver(String resolver, String docId) throws ClientException {
        removeResolver(docId);

        List<Map<String, String>> resolvers = getResolversDocuments();

        Map<String, String> entry = new HashMap<String, String>();
        entry.put("target", docId);
        entry.put("resolver", resolver);
        resolvers.add(entry);

        document.setPropertyValue(CLASSIFICATION_RESOLVERS_PROPERTY_NAME, (Serializable) resolvers);
    }

    protected List<Map<String, String>> getResolversDocuments() throws ClientException {
        List<Map<String, String>> value = (List<Map<String, String>>) document.getPropertyValue(CLASSIFICATION_RESOLVERS_PROPERTY_NAME);
        if (value == null) {
            value = new ArrayList<Map<String, String>>();
        }

        return value;
    }

    protected String getResolver(String docId) throws ClientException {
        List<Map<String, String>> classifiedDocument = getResolversDocuments();
        for (Map<String, String> resolver : classifiedDocument) {
            if (docId.equals(resolver.get("target"))) {
                return resolver.get("resolver");
            }
        }

        return null;
    }

    protected boolean removeResolver(String docId) throws ClientException {
        List<Map<String, String>> classifiedDocument = getResolversDocuments();

        // remove existing resolver for docId
        for (int i = 0; i < classifiedDocument.size(); i++) {
            Map<String, String> storedResolver = classifiedDocument.get(i);
            if (docId.equals(storedResolver.get("target"))) {
                classifiedDocument.remove(i);
                document.setPropertyValue(CLASSIFICATION_RESOLVERS_PROPERTY_NAME, (Serializable) classifiedDocument);
                return true;
            }
        }

        return false;
    }

    public void add(String docId) throws ClientException {
        List<String> classifiedDocument = getTargetsPropertyValue();
        classifiedDocument.add(docId);

        setClassifiedDocumentIds(classifiedDocument);
    }

    public boolean remove(DocumentModel doc) throws ClientException {
        return remove(doc.getId());
    }

    public boolean remove(String docId) throws ClientException {
        List<String> classifiedDocument = getTargetsPropertyValue();
        boolean removed = classifiedDocument.remove(docId);
        setClassifiedDocumentIds(classifiedDocument);

        if (removed) {
            removeResolver(docId);
        }

        return removed;
    }

    public boolean contains(DocumentModel documentModel) throws ClientException {
        return contains(documentModel.getId());
    }

    public boolean contains(String docId) throws ClientException {
        return getClassifiedDocumentIds().contains(docId);
    }

    public DocumentModel getDocument() {
        return document;
    }

    protected CoreSession getCoreSession() throws ClientException {
        CoreSession session = document.getCoreSession();
        if (session == null) {
            throw new ClientException("Trying to resolve classified document with an offline document");
        }
        return session;
    }
}
