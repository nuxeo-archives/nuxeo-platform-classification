package org.nuxeo.ecm.classification.api;

import java.util.*;

public class ClassificationResult<T extends Enum> {
    Map<T, Set<String>> results = new HashMap<T, Set<String>>();

    public ClassificationResult() { }

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
