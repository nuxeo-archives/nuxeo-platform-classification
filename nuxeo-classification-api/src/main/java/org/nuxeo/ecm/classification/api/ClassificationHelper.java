/*
 * (C) Copyright 2006-2008 Nuxeo SAS (http://nuxeo.com/) and contributors.
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
 *     <a href="mailto:at@nuxeo.com">Anahide Tchertchian</a>
 *
 * $Id: ClassificationHelper.java 58597 2008-11-03 16:53:58Z atchertchian $
 */

package org.nuxeo.ecm.classification.api;

import org.nuxeo.ecm.classification.api.adapter.Classification;
import org.nuxeo.ecm.core.api.ClientException;
import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.ecm.core.api.DocumentModelList;
import org.nuxeo.ecm.core.api.DocumentRef;
import org.nuxeo.ecm.core.api.IdRef;
import org.nuxeo.ecm.core.api.impl.DocumentModelListImpl;
import org.nuxeo.ecm.core.api.security.SecurityConstants;

/**
 * Classification helper.
 *
 * @author Anahide Tchertchian
 */
public class ClassificationHelper {

    /**
     * @see org.nuxeo.ecm.classification.api.adapter.Classification
     * @deprecated use the expected Adapter now
     */
    public static DocumentModelList getClassifiedDocuments(
            DocumentModel container, CoreSession session)
            throws ClientException {
        Classification adapter = container.getAdapter(Classification.class);
        return adapter.getClassifiedDocuments();
    }
}
