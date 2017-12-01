package generator.filter;

import generator.Schedule;
import generator.course.Section;

public interface Filter {

    /**
     * Returns whether or not the section satisfies the filter.
     * If the filter is an inclusive filter, has() will return <tt>true</tt> if the
     * appropriate property value is equal to any element in include; otherwise
     * it will return <tt>false</tt>.
     * If the filter is an exclusive filter, has() will return <tt>true</tt> if the
     * appropriate property value never equals any element in exclude;
     * otherwise, it will return <tt>false</tt>. That is, if the property value does not
     * match anything in exclude, has() will return <tt>true</tt>.
     *
     * @param section the section to test on.
     * @return <tt>true</tt> if section satisfies the filter. Otherwise false.
     */
    boolean has(Section section);

    boolean has(Schedule schedule);

    /**/

}
