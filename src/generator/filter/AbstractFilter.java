package generator.filter;

import java.util.Set;

public abstract class AbstractFilter implements Filter {
    protected final String courseId;

    protected AbstractFilter(String courseId) {
        this.courseId = courseId;
    }

    public String getCourseId() {
        return courseId;
    }
}
