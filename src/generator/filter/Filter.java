package generator.filter;

import generator.Schedule;
import generator.Section;

import java.util.Set;

public abstract class Filter {
    final String courseId;
    final Set<?> include;
    final Set<?> exclude;

    public Filter(String courseId, Set<?> include, Set<?> exclude) {
        this.courseId = courseId;
        this.include = include;
        this.exclude = exclude;
    }

    /**
     * Returns whether or not the section satisfies the filter.
     * If the filter is an inclusive filter, has() will return <tt>true</tt> if the
     * appropriate property value is equal to any element in include; otherwise
     * it will return <tt>false</tt>.
     * If the filter is an exclusive filter, has() will return <tt>true</tt> if the
     * appropriate property value never equals any element in exclude;
     * otherwise, it will return <tt>false</tt>. That is, if the property value does not
     * match anything in exclude, has() will return <tt>true</tt>.
     *
     * @param section the section to test on.
     * @return <tt>true</tt> if section satisfies the filter. Otherwise false.
     */
    public abstract boolean has(Section section);

    public boolean has(Schedule schedule) {
        for (Section s : schedule) {
            if (s.getCourseId().equals(courseId)) {
                if (exclude.size() == 0 && has(s)) return true;
                if (include.size() == 0 && !has(s)) return false;
            }
        }
        return include.size() == 0;
    }

    public String getCourseId() {
        return courseId;
    }
}
