/*
 * (C) Copyright 2006-2009 Nuxeo SAS (http://nuxeo.com/) and contributors.
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
 *     ldoguin
 *
 * $Id$
 */

package org.nuxeo.ecm.classification.core;

import java.util.LinkedList;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.nuxeo.ecm.classification.api.ClassificationService;
import org.nuxeo.runtime.model.ComponentContext;
import org.nuxeo.runtime.model.ComponentInstance;
import org.nuxeo.runtime.model.DefaultComponent;

public class ClassificationServiceImpl extends DefaultComponent implements
        ClassificationService {
    public static final String NAME = "org.nuxeo.ecm.classification.core.ClassificationService";

    public static final String TYPES_XP = "types";

    private static final Log log = LogFactory.getLog(ClassificationServiceImpl.class);

    private static List<String> typeList;

    @Override
    public void activate(ComponentContext context) {
        typeList = new LinkedList<String>();
    }

    @Override
    public void deactivate(ComponentContext context) {
        typeList = new LinkedList<String>();
    }

    @Override
    public void registerContribution(Object contribution,
            String extensionPoint, ComponentInstance contributor) {
        if (extensionPoint.equals(TYPES_XP)) {
            ClassificationDescriptor classificationDesc = (ClassificationDescriptor) contribution;
            String typeName = classificationDesc.getType();
            if (classificationDesc.isEnabled()) {
                typeList.add(typeName);
            } else {
                if (typeList.contains(typeName)) {
                    typeList.remove(typeName);
                }
            }
        } else {
            log.error("Extension point " + extensionPoint + "is unknown");
        }
    }

    public List<String> getClassifiableDocumentTypes() {
        return typeList;
    }

    public boolean isClassifiable(String docType) {
        return typeList.contains(docType);
    }

}
