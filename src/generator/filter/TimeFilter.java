package generator.filter;

import generator.course.Meeting;
import generator.Schedule;
import generator.course.Section;
import com.google.common.collect.Range;
import com.google.common.collect.RangeSet;
import com.google.common.collect.TreeRangeSet;

import java.time.LocalTime;

public class TimeFilter extends AbstractFilter {
    private final RangeSet<LocalTime> includedTimes = TreeRangeSet.create();
//    private final RangeSet<LocalTime> excludedTimes = TreeRangeSet.create();

    // TODO implement ability to arbitrarily filter by endpoints in Range

/*    private TimeFilter(String applicableCourse, Set<Range<LocalTime>> include, Set<Range<LocalTime>> exclude) {
        super(applicableCourse);
        this.includedTimes.addAll(include);
//        this.excludedTimes.addAll(exclude);
    }*/

    private TimeFilter(String courseId, RangeSet<LocalTime> includedTimes) {
        super(courseId);
        this.includedTimes.addAll(includedTimes);
    }

    private TimeFilter(String courseId, Range<LocalTime> time) {
        super(courseId);
        RangeSet<LocalTime> timeRange = TreeRangeSet.create();
        timeRange.add(time);
        this.includedTimes.addAll(timeRange);
    }

    public static TimeFilter startAfter(String courseId, LocalTime time) {
        return new TimeFilter(courseId, Range.atLeast(time));
    }

    public static TimeFilter startBefore(String courseId, LocalTime time) {
        return new TimeFilter(courseId, Range.atMost(time));
    }

    public static TimeFilter endBefore(String courseId, LocalTime time) {
        return new TimeFilter(courseId, Range.lessThan(time));
    }

    public static TimeFilter inRange(String courseId, Range<LocalTime> time) {
        return new TimeFilter(courseId, time);
    }

    public static TimeFilter inRange(String courseId, RangeSet<LocalTime> timeRange) {
        return new TimeFilter(courseId, timeRange);
    }

    @Override
    public boolean has(Schedule schedule) {
        for (Section section : schedule) {
            if (!has(section) && courseId.equals(section.getCourseId())) return false;
        }
        return true;
    }

    @Override
    public boolean has(Section section) {
        for (Meeting meeting : section) {
            if (!has(meeting)) return false;
        }
        return true;
    }

    public boolean has(Meeting meeting) {
        return meeting.getRange().equals(Range.all()) | includedTimes.encloses(meeting.getRange());
    }
}
