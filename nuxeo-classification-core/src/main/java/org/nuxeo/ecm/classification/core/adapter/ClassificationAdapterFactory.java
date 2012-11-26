package org.nuxeo.ecm.classification.core.adapter;

import org.nuxeo.ecm.classification.api.adapter.Classification;
import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.ecm.core.api.adapter.DocumentAdapterFactory;

import static org.nuxeo.ecm.classification.api.ClassificationConstants.CLASSIFICATION_SCHEMA_NAME;

public class ClassificationAdapterFactory implements DocumentAdapterFactory {

    @Override
    public Object getAdapter(DocumentModel doc, Class<?> itf) {
        if (doc.getDocumentType().hasSchema(CLASSIFICATION_SCHEMA_NAME)) {
            return new Classification(doc);
        }

        return null;
    }
}
