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

package org.nuxeo.ecm.classification.core.resolver;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.nuxeo.ecm.classification.api.ClassificationResolver;
import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.ecm.core.api.IdRef;

/**
 * Resolver (@see org.nuxeo.ecm.classification.api.ClassificationResolver) to get the last version when getting
 * classified document from a Classification adapter.
 *
 * @since 5.7
 */
public class LastVersionResolver implements ClassificationResolver {

    private static final Log log = LogFactory.getLog(LastVersionResolver.class);

    @Override
    public String resolve(CoreSession session, String docId) {
        DocumentModel lastDocumentVersion = session.getLastDocumentVersion(new IdRef(docId));
        if (lastDocumentVersion == null) {
            log.info("Any version found, returning the docId");
            return docId;
        }
        return lastDocumentVersion.getId();
    }
}
