package generator.filter;

import generator.schedule.Schedule;
import generator.schedule.Section;

import java.util.HashSet;
import java.util.Set;

public abstract class DualFilter extends AbstractFilter {
    protected final Set<?> include;
    protected final Set<?> exclude;

    protected DualFilter(String courseId, Set<?> include, Set<?> exclude) {
        super(courseId);
        this.include = new HashSet<>(include);
        this.exclude = new HashSet<>(exclude);
    }

    @Override
    public boolean has(Schedule schedule) {
        for (Section s : schedule) {
            if (s.getCourseId().equals(courseId)) {
                if (exclude.size() == 0 && has(s)) return true;
                if (include.size() == 0 && !has(s)) return false;
            }
        }
        return include.size() == 0;
    }
}
