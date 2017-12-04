package generator;

import generator.filter.Filter;
import generator.filter.Filters;
import generator.schedule.Course;
import generator.schedule.Schedule;
import generator.schedule.Section;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.*;
import java.util.stream.Collectors;

public class Generator {
    private static final String COURSE_BASE_URL = "http://api.umd.io/v0/courses/";
    private static final String SECTION_BASE_URL = "http://api.umd.io/v0/courses/sections/";

    private final String semesterString;
    private final String semester;
    private final Map<Course, Set<Section>> allCourses = new HashMap<>();
    private final Map<String, Course> courseLookup = new HashMap<>();
    private final Map<String, Section> sectionLookup = new HashMap<>();

    private Set<Schedule> schedulePermutations = new HashSet<>();
    private Map<Course, Set<Section>> filteredCourses = allCourses;

    Generator(String semester, Set<String> courseIds) throws Exception {
        this.semester = semester;
        this.semesterString = "?semester=" + semester;

        // create JSONArray of courses
        String courseIdsString = String.join(",", courseIds);
        String rawCourseJsonArray = sendGet(COURSE_BASE_URL + courseIdsString + semesterString);
        JSONArray courseJsonArray = new JSONArray(rawCourseJsonArray);

        // get all sections from list of courses
        Set<String> sectionIds = new HashSet<>(); // Set<String of Section>
        for (Object o : courseJsonArray) {
            Course course = new Course((JSONObject) o);
            allCourses.put(course, new HashSet<>());
            courseLookup.put(course.getCourseId(), course);

            sectionIds.addAll(course.getSectionNames());
        }

        // create JSONArray of sections
        String sectionNamesString = String.join(",", sectionIds);
        String rawSectionJsonArray = sendGet(SECTION_BASE_URL + sectionNamesString + semesterString);
        JSONArray sectionJsonArray = new JSONArray(rawSectionJsonArray);

        // generate list of sections
        for (Object o : sectionJsonArray) {
            Section section = new Section((JSONObject) o);
            allCourses.get(courseLookup.get(section.getCourseId())).add(section);
            sectionLookup.put(section.getSectionId(), section);
        }
    }

    public Set<Course> getCourses() {
        return allCourses.keySet();
    }

    public Set<Section> getSections() {
        Set<Section> allSections = new HashSet<>();
        allCourses.values().forEach(allSections::addAll);
        return allSections;
    }

    public void filterSections(Filters filters) {
//        for (Course c : allCourses.keySet()) {
//            filteredCourses.put(c, new HashSet<>());
//            if (filters.containsCourseId(c.getCourseId())) {
//                for (Section s : allCourses.get(c)) {
//                    filteredCourses.get(c).add(s);
//                    for (Filter f : filters.getFilters(c.getCourseId())) {
//                        if (!f.has(s)) {
//                            filteredCourses.get(c).remove(s);
//                            break;
//                        }
//                    }
//                }
//            }
//        }

        // filteredCourses : Map<Course, Section>
        filteredCourses = new HashMap<>();
        allCourses.keySet()
                .forEach(course -> {
                    filteredCourses.put(course, new HashSet<>(allCourses.get(course)));
                    // TODO simplify the below :(
//                    allCourses.get(course)
//                            .stream()
//                            .filter(section -> {
//                                filters.getFilters(course.getCourseId())
//                                        .stream()
//                                        .mapToInt(f -> f.has(section))
//                            })

                    filters.getFilters(course.getCourseId()).forEach(f -> filteredCourses.get(course).retainAll(
                            filteredCourses.get(course)
                                    .stream()
                                    .filter(f::has)
                                    .collect(Collectors.toSet())
                    ));
                });
    }

    Set<Schedule> getPermutations() {
        return schedulePermutations;
    }

    void trimInvalidSchedules() {
        schedulePermutations = schedulePermutations
                .stream()
                .filter(Schedule::isValid)
                .collect(Collectors.toSet());
    }

    void filterSchedules(Filter filter) {
        schedulePermutations = schedulePermutations
                .stream()
                .filter(filter::has)
                .collect(Collectors.toSet());
    }

    void generateAllPermutations() {
        schedulePermutations.clear();

        int setSize = 1;
        for (Set<Section> set : this.allCourses.values()) setSize *= set.size();
        this.generateSchedulePermutations(new ArrayList<>(this.allCourses.values()), new HashSet<>(setSize),
                new HashMap<>());

        System.out.println(setSize);
    }

    void generatePermutations() {
        schedulePermutations.clear();

        int size = filteredCourses
                .values()
                .stream()
                .mapToInt(Set::size)
                .reduce(1, (a, b) -> a * b);
        System.out.println(size);

        this.generateSchedulePermutations(new ArrayList<>(filteredCourses.values()), new HashSet<>
                (size), new HashMap<>());
    }

    // TODO deal with weirdness of going between lists and sets
    // TODO change setList to filteredCourses?
    // TODO replace with Iterator?
    private void generateSchedulePermutations(List<Set<Section>> setList, Set<Map<Course, Section>> result,
                                              Map<Course, Section> current) {
        int depth = current.size();
        if (depth == setList.size()) {
            Map<Course, Section> schedule = new HashMap<>(current);
            result.add(schedule);
            schedulePermutations.add(new Schedule(schedule));
            return;
        }
        for (Section section : setList.get(depth)) {
            current.put(courseLookup.get(section.getCourseId()), section);
            generateSchedulePermutations(setList, result, current);
            current.remove(courseLookup.get(section.getCourseId()));
        }
    }
    // https://stackoverflow.com/questions/17192796/generate-all-combinations-from-multiple-lists
/*    @Deprecated
    private static void generatePermutations(List<Set<?>> setList, Set<Set<?>> result,
                                                     Set<Object> current) {
        int depth = current.size();
        if (depth == setList.size()) {
            result.add(new HashSet<>(current));
            return;
        }
        for (Object e : setList.get(depth)) {
            current.add(e);
            generatePermutations(setList, result, current);
            current.remove(e);
        }
    }*/

    public static String sendGet(String url) throws Exception {
        URL urlObj = new URL(url);
        HttpURLConnection con = (HttpURLConnection) urlObj.openConnection();

        con.setRequestMethod("GET");
//        con.setRequestProperty("User-Agent", USER_AGENT);
        int responseCode = con.getResponseCode();
        BufferedReader in = new BufferedReader(
                new InputStreamReader(con.getInputStream())
        );

        String inputLine;
        StringBuilder response = new StringBuilder();

        while ((inputLine = in.readLine()) != null) {
            response.append(inputLine);
        }
        in.close();

        return response.toString();
    }

}
