package org.nuxeo.ecm.classification.core.adapter;

import org.nuxeo.ecm.core.api.ClientException;
import org.nuxeo.ecm.core.api.DocumentModel;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import static org.nuxeo.ecm.classification.api.ClassificationConstants.CLASSIFICATION_TARGETS_PROPERTY_NAME;

public class Classification {
    protected DocumentModel document;

    public Classification(DocumentModel doc) {
        document = doc;
    }

    public void setClassifiedDocument(List<String> documents)
            throws ClientException {
        document.setPropertyValue(CLASSIFICATION_TARGETS_PROPERTY_NAME,
                (Serializable) documents);
    }

    public List<String> getClassifiedDocument() throws ClientException {
        List<String> classified = (List<String>) document.getPropertyValue(CLASSIFICATION_TARGETS_PROPERTY_NAME);
        if (classified == null) {
            classified = new ArrayList<String>();
            setClassifiedDocument(classified);
        }
        return classified;
    }

    public void add(DocumentModel doc) throws ClientException {
        add(doc.getId());
    }

    public void add(String docId) throws ClientException {
        List<String> classifiedDocument = getClassifiedDocument();
        classifiedDocument.add(docId);

        setClassifiedDocument(classifiedDocument);
    }

    public boolean remove(DocumentModel doc) throws ClientException {
        return getClassifiedDocument().remove(doc.getId());
    }

    public boolean remove(String docId) throws ClientException {
        List<String> classifiedDocument = getClassifiedDocument();
        boolean removed = classifiedDocument.remove(docId);
        setClassifiedDocument(classifiedDocument);
        return removed;
    }

    public boolean contains(DocumentModel documentModel) throws ClientException {
        return contains(documentModel.getId());
    }

    public boolean contains(String docId) throws ClientException {
        return getClassifiedDocument().contains(docId);
    }

    public DocumentModel getDocument() {
        return document;
    }
}
