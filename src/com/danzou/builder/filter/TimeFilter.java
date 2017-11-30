package com.danzou.builder.filter;

import com.danzou.builder.Meeting;
import com.danzou.builder.Schedule;
import com.danzou.builder.Section;
import com.google.common.collect.Range;
import com.google.common.collect.RangeSet;
import com.google.common.collect.TreeRangeSet;

import java.time.LocalTime;
import java.util.HashSet;
import java.util.Set;

public class TimeFilter extends Filter {
    private final RangeSet<LocalTime> includedTimes = TreeRangeSet.create();
//    private final RangeSet<LocalTime> excludedTimes = TreeRangeSet.create();

    // TODO implement ability to arbitrarily filter by endpoints in Range

    private TimeFilter(String applicableCourse, Set<Range<LocalTime>> include, Set<Range<LocalTime>> exclude) {
        super(applicableCourse, include, exclude);
        this.includedTimes.addAll(include);
//        this.excludedTimes.addAll(exclude);
    }

    private TimeFilter(String courseId, RangeSet<LocalTime> includedTimes) {
        super(courseId, new HashSet<>(), new HashSet<>());
        this.includedTimes.addAll(includedTimes);
//        this.excludedTimes.addAll(excludedTimes);
    }

    public static TimeFilter startAfter(String courseId, LocalTime time) {
        RangeSet<LocalTime> timeRange = TreeRangeSet.create();
        timeRange.add(Range.atLeast(time));
        return new TimeFilter(courseId, timeRange);
    }

    public static TimeFilter endBefore(String courseId, LocalTime time) {
        RangeSet<LocalTime> timeRange = TreeRangeSet.create();
        timeRange.add(Range.lessThan(time));
        return new TimeFilter(courseId, timeRange);
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
        return includedTimes.encloses(meeting.getRange());
    }
}
