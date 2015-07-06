package org.nuxeo.ecm.classification.core.operation;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.nuxeo.ecm.automation.AutomationService;
import org.nuxeo.ecm.automation.OperationContext;
import org.nuxeo.ecm.automation.test.AutomationFeature;
import org.nuxeo.ecm.classification.api.ClassificationService;
import org.nuxeo.ecm.classification.api.adapter.Classification;
import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.ecm.core.test.DefaultRepositoryInit;
import org.nuxeo.ecm.core.test.annotations.RepositoryConfig;
import org.nuxeo.runtime.test.runner.Deploy;
import org.nuxeo.runtime.test.runner.Features;
import org.nuxeo.runtime.test.runner.FeaturesRunner;
import org.nuxeo.runtime.test.runner.LocalDeploy;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import javax.inject.Inject;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.nuxeo.ecm.classification.FakerClassificationResolver.FAKE_ID;

@RunWith(FeaturesRunner.class)
@Features(AutomationFeature.class)
@RepositoryConfig(init = DefaultRepositoryInit.class)
@Deploy({ "org.nuxeo.ecm.platform.classification.api", "org.nuxeo.ecm.platform.classification.core" })
@LocalDeploy({ "org.nuxeo.ecm.platform.classification.api:OSGI-INF/classification-resolver-contrib.xml" })
public class OperationsTest {

    @Inject
    ClassificationService cs;

    @Inject
    AutomationService as;

    @Inject
    CoreSession session;

    DocumentModel root;

    DocumentModel child1;

    Classification classif;

    @Before
    public void beforeMethod() {
        root = session.createDocumentModel("/default-domain/workspaces/test", "classifRoot", "ClassificationRoot");
        root = session.createDocument(root);

        child1 = session.createDocumentModel("/", "file1", "File");
        child1 = session.createDocument(child1);

        classif = root.getAdapter(Classification.class);
        assertNotNull(classif);

        assertEquals(0, classif.getClassifiedDocuments().size());
    }

    @Test
    public void testClassifyOperationWithResolver() throws Exception {
        OperationContext ctx = new OperationContext(session);
        ctx.setInput(child1);

        Map<String, Object> params = new HashMap<String, Object>();
        params.put("Resolver", "fake");
        params.put("Classification folder", root.getPathAsString());

        as.run(ctx, ClassifyOperation.ID, params);
        session.save();

        root.refresh();

        Classification classif = root.getAdapter(Classification.class);
        assertEquals(1, classif.getClassifiedDocumentIds().size());
        assertTrue(classif.getClassifiedDocumentIds().contains(FAKE_ID));
    }

    @Test
    public void testClassifyOperation() throws Exception {
        OperationContext ctx = new OperationContext(session);
        ctx.setInput(child1);

        Map<String, Object> params = new HashMap<String, Object>();
        params.put("Classification folder", root.getPathAsString());

        as.run(ctx, ClassifyOperation.ID, params);
        session.save();

        root.refresh();

        Classification classif = root.getAdapter(Classification.class);
        assertEquals(1, classif.getClassifiedDocumentIds().size());
        assertTrue(classif.getClassifiedDocumentIds().contains(child1.getId()));
    }

    @Test
    public void testUnlassifyOperation() throws Exception {
        cs.classify(root, Arrays.asList(child1));
        session.save();
        root.refresh();

        Classification classif = root.getAdapter(Classification.class);
        assertEquals(1, classif.getClassifiedDocumentIds().size());
        assertTrue(classif.getClassifiedDocumentIds().contains(child1.getId()));

        OperationContext ctx = new OperationContext(session);
        ctx.setInput(child1);
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("Classification folder", root.getPathAsString());
        as.run(ctx, UnclassifyOperation.ID, params);

        session.save();
        root.refresh();

        classif = root.getAdapter(Classification.class);
        assertEquals(0, classif.getClassifiedDocumentIds().size());
    }
}
