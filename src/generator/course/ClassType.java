package generator.course;

public enum ClassType {
    LECTURE, DISCUSSION, ONLINE, UNKNOWN;

    public static ClassType parse(String s) {
        ClassType type;
        switch (s) {
            case "":
                type = ClassType.LECTURE;
                break;
            case "Discussion":
                type = ClassType.DISCUSSION;
                break;
            case "Online":
                type = ClassType.ONLINE;
                break;
            default:
                type = ClassType.UNKNOWN;
                break;
        }
        return type;
    }
}

