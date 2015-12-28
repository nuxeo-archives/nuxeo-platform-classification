/*
 * (C) Copyright 2006-2012 Nuxeo SA (http://nuxeo.com/) and others.
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
 *     Arnaud Kervern <akervern@nuxeo.com>
 */

package org.nuxeo.ecm.classification.core.operation;

import org.nuxeo.ecm.automation.core.annotations.Operation;
import org.nuxeo.ecm.automation.core.annotations.OperationMethod;
import org.nuxeo.ecm.automation.core.annotations.Param;
import org.nuxeo.ecm.automation.core.collectors.DocumentModelCollector;
import org.nuxeo.ecm.classification.api.ClassificationService;
import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.runtime.api.Framework;

import java.util.Arrays;

import static org.nuxeo.ecm.automation.core.Constants.CAT_DOCUMENT;

/**
 * Operation to unclassify a Document.
 *
 * @since 5.7
 */
@Operation(id = UnclassifyOperation.ID, category = CAT_DOCUMENT, label = "Unclassify a document", description = "Classify input document into the classification folder")
public class UnclassifyOperation {
    public static final String ID = "Document.Unclassify";

    @Param(name = "Classification folder")
    protected DocumentModel folder;

    @OperationMethod(collector = DocumentModelCollector.class)
    public DocumentModel run(DocumentModel doc) {
        ClassificationService classificationService = Framework.getLocalService(ClassificationService.class);
        classificationService.unClassify(folder, Arrays.asList(doc.getId()));

        return doc;
    }
}
