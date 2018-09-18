package generator;

import generator.schedule.Course;
import generator.schedule.Schedule;
import generator.filter.*;
import org.json.JSONArray;
import org.junit.Test;

import java.time.LocalTime;
import java.util.*;

import static org.junit.Assert.assertEquals;

public class GeneratorTests {

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

        Set<Course> courses = generator.getCourses();
        OptionalInt optionalSize =
                courses.stream().mapToInt(c -> generator.getSections(c).size()).reduce((x, y) -> x * y);
        int size = 179712;
        if (optionalSize.isPresent()) size = optionalSize.getAsInt();
        generator.generateAllCombinations();
        assertEquals(size, generator.getPermutations().size());
    }

    @Test
    public void testConsistentScheduleTrim() throws Exception {
        Generator generator = new Generator("201801", getCourseIdsLong());

        generator.generateAllCombinations();
        OptionalInt optionalSize =
                generator.getCourses()
                        .stream()
                        .mapToInt(c -> generator.getSections(c).size()).reduce((x, y) -> x * y);
        int size = 179712;
        if (optionalSize.isPresent()) size = optionalSize.getAsInt();
        assertEquals(size, generator.getPermutations().size());
        generator.trimInvalidSchedules();
        assertEquals(99546, generator.getPermutations().size());
    }

    @Test
    public void testSmallPermutations() throws Exception {
        Generator generator = new Generator("201801", getCourseIdsShort());

        generator.generateAllCombinations();
        assertEquals(32, generator.getPermutations().size());
        generator.trimInvalidSchedules();
        assertEquals(25, generator.getPermutations().size());
    }

    @Test
    public void testTeacherFilter() throws Exception {
        Generator generator = new Generator("201801", getCourseIdsShort());
        Filter instructorFilter = InstructorFilter.include("MATH341", "Wiseley Wong");

        generator.generateAllCombinations();
        generator.trimInvalidSchedules();
        generator.filterSchedules(instructorFilter);
        assertEquals(12, generator.getPermutations().size());
    }

    @Test
    public void testTimeFilter() throws Exception {
        Generator generator = new Generator("201801", getCourseIdsShort());
        Filter timeFilter = TimeFilter.startAfter("CMSC250", LocalTime.of(10, 0));
        Filter instructorFilter = InstructorFilter.include("MATH341", "Wiseley Wong");

        generator.generateAllCombinations();
        generator.trimInvalidSchedules();
        generator.filterSchedules(timeFilter);
        generator.filterSchedules(instructorFilter);
        assertEquals(4, generator.getPermutations().size());
    }

    @Test
    public void testLargeTeacherFilter() throws Exception {
        Generator generator = new Generator("201801", getCourseIdsLong());

        Set<Filter> instructorFilters = new HashSet<>();
        instructorFilters.addAll(Arrays.asList(
                InstructorFilter.include("CMSC216", "Nelson Padua-Perez"),
                InstructorFilter.include("CMSC250", "Iason Filippou"),
                InstructorFilter.include("INAG110", "Anthony Pagnotti"),
                InstructorFilter.include("STAT400", "John Millson")
        ));
        Filters filters = new Filters();
        filters.addAll(instructorFilters);

        generator.generateAllCombinations();
        assertEquals(179_712, generator.getPermutations().size());

        generator.filterSections(filters);
        generator.generateCombinations();
        assertEquals(1_920, generator.getPermutations().size());

        instructorFilters = new HashSet<>();
        instructorFilters.addAll(Arrays.asList(
                InstructorFilter.include("CMSC256", "Nelson Padua-Perez"),
                InstructorFilter.include("CMSC250", "Iason Filippou"),
                InstructorFilter.include("INAG110", "Anthony Pagnotti"),
                InstructorFilter.include("STAT400", "John Millson")
        ));
        filters = new Filters();
        filters.addAll(instructorFilters);

        generator.filterSections(filters);
        generator.generateCombinations();
        assertEquals(3_840, generator.getPermutations().size());

        generator.trimInvalidSchedules();

        System.out.println(generator.getPermutations().size());
    }

    @Test
    public void makeMySchedule() throws Exception {
        Set<String> courseIds = new HashSet<>(Arrays.asList("ENEE222", "ENEE244", "CMSC250", "PHYS260", "PHYS261"));

        Set<Filter> filterSet = new HashSet<>(Arrays.asList(
                InstructorFilter.include("CMSC250", "Jason Filippou"),
                InstructorFilter.include("ENEE222", "Adrianos Papamarcou"),
                InstructorFilter.exclude("ENEE222", "Gilmer Blankenship"),
                PropertyFilter.exclude("CMSC250", "number", new HashSet<>(Arrays.asList("0201", "0208"))),
                PropertyFilter.include("PHYS261", "number", new HashSet<>(Collections.singleton("0213"))),
                InstructorFilter.include("PHYS260", new HashSet<>(Arrays.asList("Rabindra Mohapatra", "Michelle Girvan")))
        ));

        courseIds.forEach(id -> {
            filterSet.add(TimeFilter.startAfter(id, LocalTime.of(11, 0)));
            filterSet.add(TimeFilter.endBefore(id, LocalTime.of(17, 0)));
        });

        courseIds.add("HONR219L");

        Generator generator = new Generator("201808", courseIds);

        Filters filters = new Filters();
        filters.addAll(filterSet);

        generator.filterSections(filters);
        generator.generateCombinations();
        generator.trimInvalidSchedules();

        int size = generator.getPermutations().size();

        assertEquals(size, generator.getPermutations().size());

        System.out.println(size);
        if (size <= 200) {
            for (Schedule schedule : generator.getPermutations()) {
                System.out.println(schedule);
            }
        }
    }

    @Test
    public void makeASchedule() throws Exception {
        Set<String> courseIds = new HashSet<>();
        courseIds.addAll(Arrays.asList("CMSC132", "PHYS260", "PHYS261", "MATH246H", "CHIN102", "ENES100", "CHIN103"));
        Generator generator = new Generator("201801", courseIds);

        Set<Filter> filterSet = new HashSet<>();
        for (String Id : courseIds) {
            filterSet.add(TimeFilter.startAfter(Id, LocalTime.of(9, 0)));
            filterSet.add(TimeFilter.endBefore(Id, LocalTime.of(20, 0)));
        }
        filterSet.add(InstructorFilter.include("CMSC132", "Pedram Sadeghian"));
//        filterSet.add(PropertyFilter.include("CMSC132", "number", Collections.singleton("0403")));
        filterSet.add(PropertyFilter.include("CHIN103", "number", Collections.singleton("0101")));
//        filterSet.add(PropertyFilter.include("PHYS261", "number", Collections.singleton("0107")));
        filterSet.add(InstructorFilter.exclude("PHYS260", "Rabindra Mohapatra"));

        Filters filters = new Filters();
        filters.addAll(filterSet);

        generator.filterSections(filters);
        generator.generateCombinations();
        generator.trimInvalidSchedules();

        Object o = generator.getPermutations();
        System.out.println(generator.getPermutations().size());
        if (generator.getPermutations().size() <= 500) {
            for (Schedule schedule : generator.getPermutations()) {
                System.out.println(schedule);
            }
        }
    }




}