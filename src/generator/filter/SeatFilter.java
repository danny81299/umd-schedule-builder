package generator.filter;

import generator.Schedule;
import generator.course.Section;
import com.google.common.collect.Range;

public class SeatFilter extends AbstractFilter {

    private Range<Integer> seats;

    private SeatFilter(String courseId, Range<Integer> range) {
        super(courseId);
        this.seats = range;
    }

    public static SeatFilter atLeast(String course, int num) {
        return range(course, Range.atLeast(num));
    }

    public static SeatFilter greaterThan(String course, int num) {
        return range(course, Range.greaterThan(num));
    }

    public static SeatFilter available(String course) {
        return greaterThan(course, 0);
    }

    public static SeatFilter range(String course, Range<Integer> range) {
        return new SeatFilter(course, range);
    }

    @Override
    public boolean has(Schedule schedule) {
        for (Section section : schedule) {
            if (!has(section)) return false;
        }
        return true;
    }

    @Override
    public boolean has(Section section) {
        return seats.contains(section.getOpenSeats());
    }
}
