package org.nuxeo.ecm.classification.api.adapter;

import org.nuxeo.ecm.core.api.*;
import org.nuxeo.ecm.core.api.impl.DocumentModelListImpl;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import static org.nuxeo.ecm.classification.api.ClassificationConstants.CLASSIFICATION_TARGETS_PROPERTY_NAME;
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
    public DocumentModelList getClassifiedDocuments()
            throws ClientException {
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

    public void setClassifiedDocumentIds(List<String> documents)
            throws ClientException {
        document.setPropertyValue(CLASSIFICATION_TARGETS_PROPERTY_NAME,
                (Serializable) documents);
    }

    public List<String> getClassifiedDocumentIds() throws ClientException {
        List<String> classified = (List<String>) document.getPropertyValue(CLASSIFICATION_TARGETS_PROPERTY_NAME);
        if (classified == null) {
            classified = new ArrayList<String>();
            setClassifiedDocumentIds(classified);
        }
        return classified;
    }

    public void add(DocumentModel doc) throws ClientException {
        add(doc.getId());
    }

    public void add(String docId) throws ClientException {
        List<String> classifiedDocument = getClassifiedDocumentIds();
        classifiedDocument.add(docId);

        setClassifiedDocumentIds(classifiedDocument);
    }

    public boolean remove(DocumentModel doc) throws ClientException {
        return getClassifiedDocumentIds().remove(doc.getId());
    }

    public boolean remove(String docId) throws ClientException {
        List<String> classifiedDocument = getClassifiedDocumentIds();
        boolean removed = classifiedDocument.remove(docId);
        setClassifiedDocumentIds(classifiedDocument);
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
