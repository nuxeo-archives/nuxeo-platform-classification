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

package org.nuxeo.ecm.classification.api;

import java.util.List;
/**
 * Classification service is used to register classifiable Document Types.
 *
 * @author ldoguin
 *
 */
public interface ClassificationService {

    /**
     * @return the list of registered Document Types as String.
     */
    List<String> getClassifiableDocumentTypes();

    /**
     * @param docType
     * @return true if the given doc type is registered.
     */
    boolean isClassifiable(String docType);

}
