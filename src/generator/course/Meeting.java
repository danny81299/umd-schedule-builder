package generator.course;

import com.google.common.collect.Range;
import org.json.JSONObject;

import java.time.DayOfWeek;
import java.time.Duration;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.time.format.TextStyle;
import java.time.temporal.ChronoField;
import java.util.HashSet;
import java.util.Set;
import java.util.TreeSet;

public class Meeting {
    private final JSONObject meeting;
    private final Set<DayOfWeek> days;
    private final LocalTime startTime;
    private final LocalTime endTime;
    private final Duration duration;
    private final Range<LocalTime> range;
    private final ClassType type;

    public Meeting(JSONObject meeting) {
        this.meeting = meeting;
        if (meeting.getString("room").toLowerCase().equals("online")) {
            type = ClassType.ONLINE;
            days = new TreeSet<>();
            startTime = LocalTime.MIN;
            endTime = LocalTime.MIN;
            duration = Duration.ZERO;
            range = Range.all();
        } else {
            this.days = parseDaysString(meeting.getString("days"));
            DateTimeFormatterBuilder builder = new DateTimeFormatterBuilder();
            builder.appendValue(ChronoField.CLOCK_HOUR_OF_AMPM)
                    .appendLiteral(':')
                    .appendValue(ChronoField.MINUTE_OF_HOUR, 2)
                    .appendText(ChronoField.AMPM_OF_DAY, TextStyle.SHORT);
            DateTimeFormatter formatter = builder.toFormatter();
            this.startTime = LocalTime.parse(meeting.getString("start_time").toUpperCase(), formatter);
            this.endTime = LocalTime.parse(meeting.getString("end_time").toUpperCase(), formatter);
            this.duration = Duration.between(startTime, endTime);
            this.range = Range.closed(startTime, endTime);
            this.type = ClassType.parse(meeting.getString("classtype"));
        }
    }
    public String getInternalString(String s) {
        return meeting.getString(s);
    }

    public Set<DayOfWeek> getDays() {
        return days;
    }

    public LocalTime getStartTime() {
        return startTime;
    }

    public LocalTime getEndTime() {
        return endTime;
    }

    public Duration getDuration() {
        return duration;
    }

    public Range<LocalTime> getRange() {
        return range;
    }

    public String getBuilding() {
        if (type == ClassType.ONLINE) {
            return "ONLINE";
        } else {
            return meeting.getString("building");
        }
    }

    public String getRoom() {
        if (type == ClassType.ONLINE) {
            return "ONLINE";
        } else {
            return meeting.getString("room");
        }
    }

    public ClassType getType() {
        return type;
    }

    private Set<DayOfWeek> parseDaysString(String s) {
        Set<DayOfWeek> days = new TreeSet<>();
        if (s.contains("M")) days.add(DayOfWeek.MONDAY);
        if (s.contains("Tu")) days.add(DayOfWeek.TUESDAY);
        if (s.contains("W")) days.add(DayOfWeek.WEDNESDAY);
        if (s.contains("Th")) days.add(DayOfWeek.THURSDAY);
        if (s.contains("F")) days.add(DayOfWeek.FRIDAY);
        return days;
    }
}
