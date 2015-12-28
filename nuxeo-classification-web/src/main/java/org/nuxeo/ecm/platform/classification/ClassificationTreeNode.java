/*
 * (C) Copyright 2006-2008 Nuxeo SA (http://nuxeo.com/) and others.
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
 *     <a href="mailto:at@nuxeo.com">Anahide Tchertchian</a>
 *
 * $Id: ClassificationTreeNode.java 58610 2008-11-04 17:29:03Z atchertchian $
 */

package org.nuxeo.ecm.platform.classification;

import java.util.Collections;
import java.util.LinkedHashMap;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.nuxeo.ecm.classification.api.adapter.Classification;
import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.ecm.core.api.DocumentModelList;
import org.nuxeo.ecm.core.api.Filter;
import org.nuxeo.ecm.core.api.Sorter;
import org.nuxeo.ecm.core.api.impl.DocumentModelListImpl;
import org.nuxeo.ecm.core.api.security.SecurityConstants;
import org.nuxeo.ecm.webapp.tree.DocumentTreeNodeImpl;

/**
 * Tree node taking care of classified documents within a document
 *
 * @author Anahide Tchertchian
 */
public class ClassificationTreeNode extends DocumentTreeNodeImpl {

    private static final long serialVersionUID = 1L;

    private static final Log log = LogFactory.getLog(ClassificationTreeNode.class);

    public ClassificationTreeNode(DocumentModel document, Filter filter, Sorter sorter) {
        super(document, filter, sorter);
    }

    @Override
    public void fetchChildren() {
        // fetch usual children (sub folders and saved searches)
        children = new LinkedHashMap<Object, DocumentTreeNodeImpl>();
        CoreSession session = getCoreSession();
        if (session == null) {
            log.error("Cannot retrieve CoreSession for " + document);
            return;
        }

        // get and filter
        DocumentModelList coreChildren = session.getChildren(document.getRef(), null, SecurityConstants.READ, filter,
                sorter);
        for (DocumentModel child : coreChildren) {
            String identifier = child.getId();
            children.put(identifier, new ClassificationTreeNode(child, filter, sorter));
        }

        // addResolver classified files as children, respecting PLE-252 (folders
        // first) as classified files are never folderish
        Classification adapter = document.getAdapter(Classification.class);
        DocumentModelList classifChildren = new DocumentModelListImpl();
        if (adapter != null) {
            classifChildren = adapter.getClassifiedDocuments();
        }

        // sort according to original sorter
        Collections.sort(classifChildren, sorter);
        for (DocumentModel child : classifChildren) {
            String identifier = child.getId();
            children.put(identifier, new ClassificationTreeNode(child, filter, sorter));
        }
    }

}
