package generator.filter;

import generator.Generator;
import generator.schedule.Section;
import org.json.JSONObject;
import org.junit.Test;

import java.util.Arrays;
import java.util.HashSet;

import static org.junit.Assert.*;

public class FilterTests {

    private Section getTestSection() throws Exception {
        String sectionStr = "CMSC131-0101";
        JSONObject sectionJson = new JSONObject(Generator.sendGet("http://api.umd.io/v0/courses/sections/" +
                sectionStr));
        return new Section(sectionJson);
    }

    @Test
    public void testInstructorHas() throws Exception {
        Filter filter0 = InstructorFilter.include("CMSC131", new HashSet<>(Arrays.asList("Fawzi Emad")));
        assertTrue(filter0.has(getTestSection()));
    }

    @Test
    public void testPropertyHas() throws Exception {
        Filter filter0 = PropertyFilter.include("CMSC131", "number", new HashSet<>(Arrays.asList("0101",
                "0102")));
        Filter filter1 = PropertyFilter.include("CMSC131", "number", new HashSet<>(Arrays.asList("0102")));
        Filter filter2 = PropertyFilter.exclude("CMSC131", "number", new HashSet<>(Arrays.asList("0101")));
        Filter filter3 = PropertyFilter.exclude("CMSC131", "number", new HashSet<>(Arrays.asList("0102")));
        Section section = getTestSection();

        assertTrue(filter0.has(section));
        assertFalse(filter1.has(section));
        assertFalse(filter2.has(section));
        assertTrue(filter3.has(section));

    }

}