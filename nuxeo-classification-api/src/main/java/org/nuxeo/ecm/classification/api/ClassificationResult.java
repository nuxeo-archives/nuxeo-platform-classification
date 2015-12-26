/*
 * (C) Copyright 2012 Nuxeo SA (http://nuxeo.com/) and others.
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
 *     Arnaud Kervern
 */
package org.nuxeo.ecm.classification.api;

import java.util.*;

/**
 * Classification Result to know how each document is classified.
 *
 * @param <T> Expected enumeration
 * @since 5.7
 */
public class ClassificationResult<T extends Enum> {
    Map<T, Set<String>> results = new HashMap<T, Set<String>>();

    public ClassificationResult() {
    }

    public void add(T key, String value) {
        getValues(key).add(value);
    }

    public void add(T key, String... values) {
        getValues(key).addAll(Arrays.asList(values));
    }

    public void add(T key, Collection<String> values) {
        getValues(key).addAll(values);
    }

    public Set<String> get(T key) {
        return new HashSet<String>(getValues(key));
    }

    public boolean contains(T key) {
        return results.containsKey(key);
    }

    protected Set<String> getValues(T key) {
        if (!results.containsKey(key)) {
            results.put(key, new HashSet<String>());
        }
        return results.get(key);
    }
}
