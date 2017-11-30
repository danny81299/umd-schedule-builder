package generator;

import generator.filter.Filter;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSortedMap;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.*;

public class Generator {
    private static final String COURSE_BASE_URL = "http://api.umd.io/v0/courses/";
    private static final String SECTION_BASE_URL = "http://api.umd.io/v0/courses/sections/";

    private final String semesterString;
    private final String semester;
    private final ImmutableMap<Course, Set<Section>> allCourses;
    private final ImmutableMap<String, Course> courseLookup;
    private final ImmutableMap<String, Section> sectionLookup;

    private Set<Schedule> schedulePermutations = new HashSet<>();
    private Map<Course, Set<Section>> filteredCourses = new HashMap<>();

    Generator(String semester, Set<String> courseIds) throws Exception {
        this.semester = semester;
        this.semesterString = "?semester=" + semester;

        Map<Course, Set<Section>> allCourses = new HashMap<>();
        Map<String, Course> courseLookup = new HashMap<>();
        Map<String, Section> sectionLookup = new HashMap<>();

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

        this.allCourses = ImmutableSortedMap.copyOf(allCourses);
        this.courseLookup = ImmutableSortedMap.copyOf(courseLookup);
        this.sectionLookup = ImmutableSortedMap.copyOf(sectionLookup);
    }

/*    public Set<Course> getCourses() {
        return allCourses.keySet();
    }

    public Set<Section> getSections() {
        Set<Section> allSections = new HashSet<>();
        for (Set<Section> sections : allCourses.values()) allSections.addAll(sections);
        return allSections;
    }*/

/*    public void filterSections(Filters filters) {
        for (Course c : allCourses.keySet()) {
            filteredCourses.put(c, new HashSet<>());
            if (filters.containsCourseId(c.getCourseId())) {
                for (Section s : allCourses.get(c)) {
                    for (Filter f : filters.get(c.getCourseId())) {
                        if (!f.has(s)) break;
                    }
                    filteredCourses.get(c).add(s);
                }
            }
        }
    }*/

    Set<Schedule> getSchedulePermutations() {
        return schedulePermutations;
    }

    void trimInvalidSchedules() {
        Set<Schedule> trimmedPermutations = new HashSet<>();
        for (Schedule s : schedulePermutations) {
            if (!s.hasConflicts()) trimmedPermutations.add(s);
        }
        schedulePermutations = trimmedPermutations;
    }

    void filterSchedules(Filter filter) {
        Set<Schedule> filteredSchedules = new HashSet<>();
        for (Schedule s : schedulePermutations) {
            if (filter.has(s)) filteredSchedules.add(s);
        }
        schedulePermutations = filteredSchedules;
    }

    void generateSchedulePermutations() {
        int setSize = 1;
        for (Set<Section> set : this.allCourses.values()) setSize *= set.size();
        this.generateSchedulePermutations(new ArrayList<>(this.allCourses.values()), new HashSet<>(setSize),
                new HashMap<>());
    }

    // TODO deal with weirdness of going between lists and sets

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
    @Deprecated
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
    }

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
