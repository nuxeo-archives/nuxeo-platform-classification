package org.nuxeo.ecm.classification.core.operation;

import org.apache.commons.lang.StringUtils;
import org.nuxeo.ecm.automation.core.annotations.Context;
import org.nuxeo.ecm.automation.core.annotations.Operation;
import org.nuxeo.ecm.automation.core.annotations.OperationMethod;
import org.nuxeo.ecm.automation.core.annotations.Param;
import org.nuxeo.ecm.automation.core.collectors.DocumentModelCollector;
import org.nuxeo.ecm.classification.api.ClassificationService;
import org.nuxeo.ecm.core.api.ClientException;
import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.runtime.api.Framework;

import java.util.Arrays;

import static org.nuxeo.ecm.automation.core.Constants.CAT_DOCUMENT;

@Operation(id = UnclassifyOperation.ID, category = CAT_DOCUMENT, label = "Unclassify a document", description = "Classify input document into the classification folder")
public class UnclassifyOperation {
    public static final String ID = "Document.Unclassify";

    @Param(name = "Classification folder")
    protected DocumentModel folder;

    @OperationMethod(collector = DocumentModelCollector.class)
    public DocumentModel run(DocumentModel doc) throws ClientException {
        ClassificationService classificationService = Framework.getLocalService(ClassificationService.class);
        classificationService.unClassify(folder, Arrays.asList(doc.getId()));

        return doc;
    }
}
