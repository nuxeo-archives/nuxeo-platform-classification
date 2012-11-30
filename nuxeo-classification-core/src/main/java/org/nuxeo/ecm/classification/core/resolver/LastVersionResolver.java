/*
 * (C) Copyright 2006-2012 Nuxeo SAS (http://nuxeo.com/) and contributors.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Lesser General Public License
 * (LGPL) version 2.1 which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/lgpl.html
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * Contributors:
 *     Arnaud Kervern <akervern@nuxeo.com>
 */

package org.nuxeo.ecm.classification.core.resolver;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.nuxeo.ecm.classification.api.ClassificationResolver;
import org.nuxeo.ecm.core.api.ClientException;
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
        try {
            DocumentModel lastDocumentVersion = session.getLastDocumentVersion(new IdRef(docId));
            if (lastDocumentVersion == null) {
                log.info("Any version found, returning the docId");
                return docId;
            }
            return lastDocumentVersion.getId();
        } catch (ClientException e) {
            log.warn("Unable to resolve lastVersion of document" + docId);
            log.info(e, e);
            return docId;
        }
    }
}
