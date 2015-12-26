/*
 * (C) Copyright 2012 Nuxeo SA (http://nuxeo.com/) and others.
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
 *     Arnaud Kervern
 */
package org.nuxeo.ecm.platform.classification;

import org.nuxeo.ecm.classification.api.adapter.Classification;
import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.ecm.core.api.DocumentNotFoundException;
import org.nuxeo.ecm.core.api.IdRef;
import org.nuxeo.ecm.platform.query.api.AbstractPageProvider;

import java.io.Serializable;
import java.util.List;

/**
 * Page Provider using for classified documents module inside the currentDocument property. Do not handle paging right
 * now.
 *
 * @since 5.7
 */
public class ClassifiedPageProvider extends AbstractPageProvider<DocumentModel> {
    @Override
    public List<DocumentModel> getCurrentPage() {
        Classification classification = getClassification();
        if (classification != null) {
            return classification.getClassifiedDocuments();
        }
        return null;
    }

    protected Classification getClassification() {
        DocumentModel currentDocument = getCurrentDocument();
        return currentDocument.getAdapter(Classification.class);
    }

    protected DocumentModel getCurrentDocument() {
        CoreSession coreSession = (CoreSession) getProperties().get("coreSession");
        String docId = (String) getProperties().get("docId");
        try {
            return coreSession.getDocument(new IdRef(docId));
        } catch (DocumentNotFoundException e) {
            return null;
        }
    }
}
