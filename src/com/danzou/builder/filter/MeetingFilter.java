package com.danzou.builder.filter;

import com.danzou.builder.ClassType;
import com.danzou.builder.Section;

import java.util.HashSet;
import java.util.Set;

public class MeetingFilter extends Filter {
    private final String property;

    private MeetingFilter(String applicableCourse, String property, Set<String> include, Set<String> exclude) {
        super(applicableCourse, include, exclude);
        this.property = property;
    }

    public static MeetingFilter fromInclude(String courseId, String property, Set<String> include) {
        return new MeetingFilter(courseId, property, include, new HashSet<>());
    }

    public static MeetingFilter fromExclude(String courseId, String property, Set<String> exclude) {
        return new MeetingFilter(courseId, property, new HashSet<>(), exclude);
    }

    @Override
    public boolean has(Section section) {
        Set<String> values = new HashSet<>();
        for (ClassType cT : section.getMeetingsByClassType().keySet()) {
            values.add(section.getMeetingsByClassType().get(cT).getInternalString(property));
        }
        if (exclude.size() == 0) {
            for (String value : values) {
                if (include.contains(value)) return true;
            }
            return false;
        } else {
            for (String value : values) {
                if (exclude.contains(value)) return false;
            }
            return true;
        }
    }
}
