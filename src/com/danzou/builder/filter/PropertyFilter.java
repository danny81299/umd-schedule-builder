package com.danzou.builder.filter;

import com.danzou.builder.Section;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

public class PropertyFilter extends Filter {
    private final String property;

    protected PropertyFilter(String applicableCourse, String property, Set<String> include, Set<String> exclude) {
        super(applicableCourse, include, exclude);
        this.property = property;
    }

    public static PropertyFilter include(String courseId, String property, Set<String> include) {
        return new PropertyFilter(courseId, property, include, new HashSet<>());
    }

    public static PropertyFilter exclude(String courseId, String property, Set<String> exclude) {
        return new PropertyFilter(courseId, property, new HashSet<>(), exclude);
    }

    public static PropertyFilter exclude(String courseId, String property, String exclude) {
        return new PropertyFilter(courseId, property, new HashSet<>(), new HashSet<>(Collections.singletonList(exclude)));
    }

    @Override
    public boolean has(Section section) {
        String value = section.getInternalString(property);
        if (exclude.size() == 0) {
            return include.contains(value);
        } else return !exclude.contains(value);
    }
}
