package com.danzou.builder;

import com.danzou.builder.filter.*;
import org.junit.Test;

import java.time.LocalTime;
import java.util.*;

import static org.junit.Assert.*;

public class GeneratorTest {

    private static Set<String> getCourseIdsLong() {
        Set<String> courseIds = new HashSet<>();
        courseIds.addAll(Arrays.asList("CMSC250", "CMSC216", "INAG110", "STAT400"));
        // cmsc250 jason
        // cmsc216 nelson
        // stat400 john
        // anthony inag
        return courseIds;
    }

    private static Set<String> getCourseIdsShort() {
        Set<String> courseIds = new HashSet<>();
        courseIds.addAll(Arrays.asList("CMSC250", "MATH341"));
        return courseIds;
    }

    @Test
    public void testGenerator() throws Exception {
        // courses
        Generator generator = new Generator("201801", getCourseIdsLong());
    }

    @Test
    public void testConsistentPermutations() throws Exception {
        Generator generator = new Generator("201801", getCourseIdsLong());

        generator.generateSchedulePermutations();
        assertEquals(179712, generator.getSchedulePermutations().size());
    }

    @Test
    public void testConsistentScheduleTrim() throws Exception {
        Generator generator = new Generator("201801", getCourseIdsLong());

        generator.generateSchedulePermutations();
        assertEquals(179712, generator.getSchedulePermutations().size());
        generator.trimInvalidSchedules();
        assertEquals(65459, generator.getSchedulePermutations().size());
    }

    @Test
    public void testSmallPermutations() throws Exception {
        Generator generator = new Generator("201801", getCourseIdsShort());

        generator.generateSchedulePermutations();
        assertEquals(32, generator.getSchedulePermutations().size());
        generator.trimInvalidSchedules();
        assertEquals(25, generator.getSchedulePermutations().size());
    }

    @Test
    public void testTeacherFilter() throws Exception {
        Generator generator = new Generator("201801", getCourseIdsShort());
        Filter instructorFilter = InstructorFilter.include("MATH341", "Wiseley Wong");

        generator.generateSchedulePermutations();
        generator.trimInvalidSchedules();
        generator.filterSchedules(instructorFilter);
        assertEquals(12, generator.getSchedulePermutations().size());
    }

    @Test
    public void testTimeFilter() throws Exception {
        Generator generator = new Generator("201801", getCourseIdsShort());
        Filter timeFilter = TimeFilter.startAfter("CMSC250", LocalTime.of(10, 0));
        Filter instructorFilter = InstructorFilter.include("MATH341", "Wiseley Wong");

        generator.generateSchedulePermutations();
        generator.trimInvalidSchedules();
        generator.filterSchedules(timeFilter);
        generator.filterSchedules(instructorFilter);
        assertEquals(4, generator.getSchedulePermutations().size());
    }

    @Test
    public void testLargeTeacherFilter() throws Exception {
        Generator generator = new Generator("201801", getCourseIdsLong());

        Set<Filter> instructorFilters = new HashSet<>();
        instructorFilters.addAll(Arrays.asList(
                InstructorFilter.include("CMSC216", "Nelson Padua-Perez"),
                InstructorFilter.include("CMSC250","Iason Filippou"),
                InstructorFilter.include("INAG110", "Anthony Pagnotti"),
                InstructorFilter.include("STAT400", "John Millson")
        ));

        generator.generateSchedulePermutations();
        generator.trimInvalidSchedules();
        for (Filter filter : instructorFilters) {
            generator.filterSchedules(filter);
        }

        Object o = generator.getSchedulePermutations();
        System.out.println(generator.getSchedulePermutations().size());
    }

    @Test
    public void makeMySchedule() throws Exception {
        Set<String> courseIds = new HashSet<>();
        courseIds.addAll(Arrays.asList("CMSC132", "CMSC250", "MATH341"));
        Generator generator = new Generator("201801", courseIds);

        Set<Filter> filterSet = new HashSet<>();
        filterSet.addAll(Arrays.asList(
                InstructorFilter.include("CMSC132", "Pedram Sadeghian"),
                InstructorFilter.exclude("MATH341", "Wiseley Wong"),
                TimeFilter.startAfter("CMSC132", LocalTime.of(10,0)),
                TimeFilter.startAfter("CMSC250", LocalTime.of(9,30)),
//                PropertyFilter.exclude("CMSC132", "number", new HashSet<>(Arrays.asList
//                        ("0402", "0203"))),
                PropertyFilter.include("CMSC250", "number", new HashSet<>(Arrays.asList
                        ("0107")))
        ));

        Filters filters = new Filters();
        filters.addAll(filterSet);

        generator.generateSchedulePermutations();
        generator.trimInvalidSchedules();
        for (Filter filter : filterSet) {
            generator.filterSchedules(filter);
        }

        Object o = generator.getSchedulePermutations();
        System.out.println(generator.getSchedulePermutations().size());
        if (generator.getSchedulePermutations().size() <= 20) {
            for (Schedule schedule : generator.getSchedulePermutations()) {
                System.out.println(schedule);
            }
        }
    }

    @Test
    public void makeASchedule() throws Exception {
        Set<String> courseIds = new HashSet<>();
        courseIds.addAll(Arrays.asList("CMSC132", "CMSC250", "CLAS289A", "HONR219Z"));
        Generator generator = new Generator("201801", courseIds);

        Set<Filter> filters = new HashSet<>();
        for (String Id : courseIds) {
            filters.add(TimeFilter.startAfter(Id, Id.equals("CMSC250") ? LocalTime.of(10,0) :
                    LocalTime.of(10,0)));
//            filters.add(TimeFilter.endBefore(Id, LocalTime.of(6,0)));
        }
        filters.add(InstructorFilter.include("CMSC132", "Pedram Sadeghian"));

        generator.generateSchedulePermutations();
        generator.trimInvalidSchedules();
        for (Filter filter : filters) {
            generator.filterSchedules(filter);
        }

        Object o = generator.getSchedulePermutations();
        System.out.println(generator.getSchedulePermutations().size());
        if (generator.getSchedulePermutations().size() <= 50) {
            for (Schedule schedule : generator.getSchedulePermutations()) {
                System.out.println(schedule);
            }
        }
    }
}