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

package org.nuxeo.ecm.classification.core;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.nuxeo.common.xmap.annotation.XNode;
import org.nuxeo.common.xmap.annotation.XObject;
import org.nuxeo.ecm.classification.api.ClassificationResolver;

/**
 * Descriptor to contribute new classification resolver.
 * 
 * @since 5.7
 */
@XObject("resolver")
public class ClassificationResolverDescriptor {

    private static final Log log = LogFactory.getLog(ClassificationResolverDescriptor.class);

    @XNode("@name")
    protected String name;

    @XNode("@class")
    protected Class<?> clazz;

    public String getName() {
        return name;
    }

    public ClassificationResolver getResolverInstance() {
        if (ClassificationResolver.class.isAssignableFrom(clazz)) {
            try {
                return (ClassificationResolver) clazz.newInstance();
            } catch (InstantiationException e) {
                log.warn("Unable to instantiate " + clazz.getCanonicalName()
                        + " as "
                        + ClassificationResolver.class.getCanonicalName());
                log.debug(e, e);
            } catch (IllegalAccessException e) {
                log.warn("illegal access to " + clazz.getCanonicalName());
                log.debug(e, e);
            }
        }
        return null;
    }
}
