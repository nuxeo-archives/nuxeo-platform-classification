package org.nuxeo.ecm.classification;

import org.nuxeo.ecm.classification.api.ClassificationResolver;
import org.nuxeo.ecm.core.api.CoreSession;

import static org.junit.Assert.assertNotNull;

public class FakerClassificationResolver implements ClassificationResolver {

    public static final String FAKE_ID = "00-00-00";

    @Override
    public String resolve(CoreSession session, String docId) {
        assertNotNull(session);
        assertNotNull(docId);

        return FAKE_ID;
    }

}
