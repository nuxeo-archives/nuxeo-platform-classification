package org.nuxeo.ecm.classification.api;

import org.nuxeo.ecm.core.api.CoreSession;

/**
 * Interface used by Classification Resolver. Implementations should be able to
 * return expected target id. Passed session is unrestricted
 * 
 * @since 5.7
 */
public interface ClassificationResolver {
    public String resolve(CoreSession session, String docId);
}
