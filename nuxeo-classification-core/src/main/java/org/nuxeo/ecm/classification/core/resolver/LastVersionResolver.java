package org.nuxeo.ecm.classification.core.resolver;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.nuxeo.ecm.classification.api.ClassificationResolver;
import org.nuxeo.ecm.core.api.ClientException;
import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.core.api.IdRef;

public class LastVersionResolver implements ClassificationResolver {

    private static final Log log = LogFactory.getLog(LastVersionResolver.class);

    @Override
    public String resolve(CoreSession session, String docId) {
        try {
            return session.getLastDocumentVersion(new IdRef(docId)).getId();
        } catch (ClientException e) {
            log.warn("Unable to resolve lastVersion of document" + docId);
            log.info(e, e);
            return docId;
        }
    }
}
