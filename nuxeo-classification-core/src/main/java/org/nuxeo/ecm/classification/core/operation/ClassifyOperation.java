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

@Operation(id = ClassifyOperation.ID, category = CAT_DOCUMENT, label = "Classify a document", description = "Classify input document into the classification folder")
public class ClassifyOperation {
    public static final String ID = "Document.Classify";

    @Param(name = "Classification folder")
    protected DocumentModel folder;

    @Param(name = "Resolver", required = false)
    protected String resolver;

    @Context
    protected CoreSession session;

    @OperationMethod(collector = DocumentModelCollector.class)
    public DocumentModel run(DocumentModel doc) throws ClientException {
        ClassificationService classificationService = Framework.getLocalService(ClassificationService.class);
        if (StringUtils.isBlank(resolver)) {
            classificationService.classify(folder, Arrays.asList(doc));
        } else {
            classificationService.classify(folder, resolver, Arrays.asList(doc));
        }

        return doc;
    }
}
