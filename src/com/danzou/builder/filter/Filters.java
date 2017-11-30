package com.danzou.builder.filter;

import java.util.*;

public class Filters {
    private Map<String, Set<Filter>> filters = new HashMap<>();

    public void add(String courseId, Filter filter) {
        if (!filters.containsKey(courseId)) filters.put(courseId, new HashSet<>());
        filters.get(courseId).add(filter);
    }

    public void add(Filter filter) {
        add(filter.getCourseId(), filter);
    }

    public void addAll(Collection<Filter> filters) {
        for (Filter filter : filters) {
            add(filter);
        }
    }

    public boolean containsCourseId(String courseId) {
        return filters.containsKey(courseId);
    }

    public Set<Filter> getFilters(String courseId) {
        if (filters.containsKey(courseId)) return filters.get(courseId);
        return new HashSet<>();
    }

    public Set<String> getCourses() {
        return filters.keySet();
    }
}
