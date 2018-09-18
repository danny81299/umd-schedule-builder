package generator.schedule;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.*;

public class Course implements Comparable<Course> {
    private static final JSONObject REFERENCE_COURSE_JSON;
    private final JSONObject course;

    static {
        Map<String, Object> referenceCourse = new HashMap<>();
        referenceCourse.put("course_id", "");
        referenceCourse.put("name", "");
        referenceCourse.put("dept_id", "");
        referenceCourse.put("department", "");
        referenceCourse.put("semester", "");
        referenceCourse.put("credits", 0);
        referenceCourse.put("grading_method", new String[0]);
        referenceCourse.put("core", new String[0]);
        referenceCourse.put("gen_ed", new String[0]);
        referenceCourse.put("description", "");
        Map<String, String> relationships = new HashMap<>();
        relationships.put("prereqs", "");
        relationships.put("coreqs", "");
        relationships.put("restrictions", "");
        relationships.put("credit_granted_for", "");
        relationships.put("also_offered_as", "");
        relationships.put("formerly", "");
        relationships.put("additional_info", "");
        referenceCourse.put("relationships", relationships);
        referenceCourse.put("sections", new String[0]);
        REFERENCE_COURSE_JSON = new JSONObject(referenceCourse);
    }

    @Deprecated
    Course(String courseString) throws Exception {
        this(new JSONObject(courseString));
    }

    public Course(JSONObject course) {
        // TODO replace with JSONObject.isSimilar(Object o)
        if (!course.keySet().equals(REFERENCE_COURSE_JSON.keySet())) {
            throw new RuntimeException("JSONObject did not match REFERENCE_COURSE_JSON");
        }
        this.course = course;
    }

    public String getCourseId() {
        return course.getString("course_id");
    }

    public String getCourseName() {
        return course.getString("name");
    }

    public String getDeptId() {
        return course.getString("dept_id");
    }

    public String getDepartment() {
        return course.getString("department");
    }

    public List<String> getSectionNames() {
        JSONArray rawSectionsList = course.getJSONArray("sections");
        List<String> sections = new ArrayList<>();
        for (Object o : rawSectionsList) {
            sections.add(o.toString());
        }
        return sections;
    }

    static Course matchCourse(String name, Collection<Course> courses) {
        JSONObject modifiedReferenceJson = new JSONObject(REFERENCE_COURSE_JSON);
        modifiedReferenceJson.put("course_id", name);
        Course similarCourse = new Course(modifiedReferenceJson);

        for (Course course : courses) {
            if (course.isSimilar(similarCourse)) {
                return course;
            }
        }
        return null;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Course c = (Course) o;

        return course.equals(c.course);
    }

    public boolean isSimilar(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Course c = (Course) o;

        return this.getCourseId().equals(c.getCourseId());
    }

    @Override
    public int hashCode() {
        return course.hashCode();
    }

    @Override
    public String toString() {
        return this.getCourseId();
    }

    @Override
    public int compareTo(Course c) {
        return this.getCourseId().compareTo(c.getCourseId());
    }

}
