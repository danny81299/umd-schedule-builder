package generator;

import org.jetbrains.annotations.NotNull;
import org.json.JSONObject;

import java.time.DayOfWeek;
import java.util.*;

public class Section implements Comparable<Section>, Iterable<Meeting> {
    private static final JSONObject REFERENCE_SECTION_JSON = generateReferenceSection();
    private final JSONObject section;
    private final String sectionId;
    private final String courseId;
    private final String number;
    private final Set<String> instructors = new HashSet<>();
    private final int seats;
    private final String semester;
    private final Map<DayOfWeek, Set<Meeting>> meetingsByDay = new HashMap<>();
    private final Map<ClassType, Meeting> meetingsByClassType = new HashMap<>();
    private final Set<Meeting> meetings = new HashSet<>();
    private final int openSeats;
    private final int waitlist;
    private Course course;
    private static JSONObject generateReferenceSection() {
        Map<String, Object> referenceSection = new HashMap<>();
        referenceSection.put("section_id", "");
        referenceSection.put("course", "");
        referenceSection.put("number", "");
        referenceSection.put("instructors", new String[0]);
        referenceSection.put("seats", 0);
        referenceSection.put("semester", "");
        referenceSection.put("meetingsByClassType", new Map[0]);
        referenceSection.put("open_seats", 0);
        referenceSection.put("waitlist", 0);
        System.out.println(referenceSection.keySet());
        return new JSONObject(referenceSection);
    }

    public Section(JSONObject section) {
        // TODO replace with isSimilar
        // TODO fix reference keyset and inconsistent JSON
//        if (!section.keySet().equals(REFERENCE_SECTION_JSON.keySet())) {
//            throw new RuntimeException(section.toString() + "JSONObject did not match REFERENCE_SECTION_JSON");
//        }
        this.section = section;
        this.sectionId = section.getString("section_id");
        this.courseId = section.getString("course");
        this.number = section.getString("number");
        for (Object o : section.getJSONArray("instructors")) {
            this.instructors.add((String) o);
        }
        this.seats = section.getInt("seats");
        this.semester = section.getString("semester");
        for (Object o : section.getJSONArray("meetings")) {
            JSONObject meetingJson = (JSONObject) o;
            ClassType type = ClassType.parse(meetingJson.getString("classtype"));
            if (meetingJson.getString("room").equals("ONLINE")) continue;
            if (meetingJson.getString("room").equals("")) continue;
            Meeting meeting = new Meeting(meetingJson);
            this.meetings.add(meeting);
            this.meetingsByClassType.put(type, meeting);
            for (DayOfWeek day : meeting.getDays()) {
                if (!meetingsByDay.containsKey(day)) meetingsByDay.put(day, new HashSet<>());
                meetingsByDay.get(day).add(meeting);
            }
        }
        this.openSeats = section.optInt("open_seats");
        this.waitlist = section.optInt("waitlist");
    }


/*    String getSectionId() {
        return section.getString("section_id");
    }

    String getCourseId() {
        return section.getString("course");
    }

    String getNumber() {
        return section.getString("number");
    }

    int getSeats() {
        return section.getInt("seats");
    }

    int getOpenSeats() {
        return section.getInt("open_seats");
    }*/
    public String getInternalString(String s) {
        return section.getString(s);
    }

    public String getSectionId() {
        return sectionId;
    }

    public String getCourseId() {
        return courseId;
    }

    public String getNumber() {
        return number;
    }

    public Set<String> getInstructors() {
        return instructors;
    }

    public int getSeats() {
        return seats;
    }

    public String getSemester() {
        return semester;
    }

    public Set<Meeting> getMeetings() {
        return meetings;
    }

    public Map<ClassType, Meeting> getMeetingsByClassType() {
        return meetingsByClassType;
    }

    public Map<DayOfWeek, Set<Meeting>> getMeetingsByDay() {
        return meetingsByDay;
    }

    public int getOpenSeats() {
        return openSeats;
    }

    public int getWaitlist() {
        return waitlist;
    }

    void setCourse(Course course) {
        if (!course.getCourseId().equals(this.getCourseId())) {
            throw new RuntimeException("Bad Course given to Section");
        }
        this.course = course;
    }

    // might be null
    Course getCourse() {
        return this.course;
    }

//    public boolean satisfies(Filter filter) {
//        if filter.getCourseId()
//        return false;
//    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Section section1 = (Section) o;

        return section.equals(section1.section);
    }

    @Override
    public int hashCode() {
        return section.hashCode();
    }

    @Override
    public String toString() {
        return this.getSectionId();
    }

    @Override
    public int compareTo(Section s) {
        return this.getSectionId().compareTo(s.getSectionId());
    }

    @Override
    @NotNull
    public Iterator<Meeting> iterator() {
        return meetings.iterator();
    }
}
