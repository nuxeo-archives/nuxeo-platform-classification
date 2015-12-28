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
 * $Id: ClassificationConstants.java 58610 2008-11-04 17:29:03Z atchertchian $
 */

package org.nuxeo.ecm.classification.api;

/**
 * Classification constants
 *
 * @author Anahide Tchertchian
 */
public class ClassificationConstants {

    /**
     * Permission to classify
     */
    public static final String CLASSIFY = "Classify";

    public static final String CLASSIFIABLE_FACET = "Classifiable";

    public static final String CLASSIFICATION_SCHEMA_NAME = "classification";

    public static final String CLASSIFICATION_TARGETS_PROPERTY_NAME = "classification:targets";

    public static final String CLASSIFICATION_RESOLVERS_PROPERTY_NAME = "classification:resolvers";

    public static final String EVENT_CLASSIFICATION_DONE = "ClassificationDone";

    public static final String EVENT_UNCLASSIFICATION_DONE = "UnclassificationDone";

    /**
     * Classification core types
     */
    public static final String CLASSIFICATION_ROOT = "ClassificationRoot";

    public static final String CLASSIFICATION_FOLDER = "ClassificationFolder";

}
