package com.danzou.builder;

import com.google.common.collect.Range;
import com.google.common.collect.RangeSet;
import com.google.common.collect.TreeRangeSet;
import org.jetbrains.annotations.NotNull;

import java.time.DayOfWeek;
import java.time.LocalTime;
import java.util.*;

public class Schedule implements /*Comparable<Schedule>, */Iterable<Section> {

    private final Set<Section> sections = new TreeSet<>();
    private final Map<Course, Section> classes = new HashMap<>();
    private final Set<Meeting> meetings = new HashSet<>();
    private final Map<DayOfWeek, Set<Meeting>> meetingsByDay = new HashMap<>();
//    private final Map<DayOfWeek, RangeSet<LocalTime>> masterRange = new HashMap<>();
//    private final boolean isValid;

    public Schedule(Map<Course, Section> classes) {
        this.classes.putAll(classes);
        this.sections.addAll(classes.values());
        for (Section section : this.sections) {
            meetings.addAll(section.getMeetings());
            Map<DayOfWeek, Set<Meeting>> meetingsByDay = section.getMeetingsByDay();
            for (DayOfWeek day : meetingsByDay.keySet()) {
                if (!this.meetingsByDay.containsKey(day)) {
                    this.meetingsByDay.put(day, new HashSet<>());
//                    this.masterRange.put(day, TreeRangeSet.create());
                }
                /*for (Meeting m : meetingsByDay.get(day)) {
                    this.meetingsByDay.get(day).add(m);
                    if (masterRange.get(day).intersects(m.getRange())) isValid = false;
                    this.masterRange.get(day).add(m.getRange());
                }*/
                this.meetingsByDay.get(day).addAll(meetingsByDay.get(day));

            }
        }
//        isValid = true;
    }

//    public boolean isValid() {
//        return isValid;
//    }

    public boolean hasConflicts() {
        for (DayOfWeek day : meetingsByDay.keySet()) {
            RangeSet<LocalTime> masterRange = TreeRangeSet.create();
            for (Meeting m : meetingsByDay.get(day)) {
                if (masterRange.intersects(m.getRange())) return true;
                masterRange.add(m.getRange());
            }
        }
        return false;
    }

    @Deprecated // since it doesn't fucking work
    public static boolean hasConflicts(Collection<Section> sections) {
        RangeSet<LocalTime> masterRange = TreeRangeSet.create();
        for (Section s : sections) {
            for (ClassType cT : s.getMeetingsByClassType().keySet()) {
                Meeting m = s.getMeetingsByClassType().get(cT);
                Range<LocalTime> subRange = Range.closed(m.getStartTime(), m.getEndTime());
                if (masterRange.intersects(subRange)) return true; // does it intersect !?!?!?!?! ahhhhhhhhhh
                masterRange.add(subRange);
            }
        }
        return false;
    }

    @Deprecated
    public static boolean hasConflicts(Map<Course, Section> classes) {
        Set<Section> sections = new HashSet<>();
        sections.addAll(classes.values());
        return hasConflicts(sections);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Schedule schedule = (Schedule) o;

        return sections.equals(schedule.sections);
    }

    @Override
    public int hashCode() {
        return sections.hashCode();
    }

    @Override
    public String toString() {
        return "Schedule{" +
                "sections=" + sections +
                '}';
    }

    @Override
    @NotNull
    public Iterator<Section> iterator() {
        return sections.iterator();
    }

//    @Override
//    public int compareTo(@NotNull Schedule o) {
//        return sections.compareTo(o);
//    }
}