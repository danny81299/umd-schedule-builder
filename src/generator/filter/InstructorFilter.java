package generator.filter;

import generator.Section;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

public class InstructorFilter extends Filter {

    private InstructorFilter(String applicableCourse, Set<String> include, Set<String> exclude) {
        super(applicableCourse, include, exclude);
    }

    public static InstructorFilter include(String courseId, Set<String> include) {
        return new InstructorFilter(courseId, include, new HashSet<>());
    }

    public static Filter include(String courseId, String include) {
        return include(courseId, new HashSet<>(Collections.singleton(include)));
    }

    public static Filter exclude(String courseId, Set<String> exclude) {
        return new InstructorFilter(courseId, new HashSet<>(), exclude);
    }

    public static Filter exclude(String courseId, String exclude) {
        return exclude(courseId, new HashSet<>(Collections.singleton(exclude)));
    }

    @Override
    public boolean has(Section section) {
        Set<String> instructors = section.getInstructors();
        if (exclude.size() == 0) {
            for (String instructor : instructors) {
                if (include.contains(instructor)) {
                    return true;
                }
            }
            return false;
        } else {
            for (String instructor : instructors) {
                if (exclude.contains(instructor)) {
                    return false;
                }
            }
            return true;
        }
    }

}
