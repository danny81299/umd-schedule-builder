package generator.filter;

import generator.Section;
import com.google.common.collect.Range;

import java.util.HashSet;

public class SeatFilter extends Filter {

    private Range<Integer> seats;

    private SeatFilter(String course, Range<Integer> range) {
        super(course, new HashSet<>(), new HashSet<>());
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

    public boolean has(Section section) {
        return seats.contains(section.getOpenSeats());
    }
}
