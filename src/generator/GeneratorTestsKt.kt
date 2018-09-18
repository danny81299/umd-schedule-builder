package generator

import generator.Generator.sendGet
import generator.schedule.Course
import generator.schedule.Section
import org.json.JSONArray
import org.json.JSONObject
import org.junit.Test

class GeneratorTestsKt {

    @Test
    fun getIRBSections() {
        //        String COURSE_BASE_URL = "https://api.umd.io/v0/courses/";
        val rawJsonCourses = sendGet("https://api.umd.io/v0/courses?dept_id=CMSC")
        val coursesJsonArray = JSONArray(rawJsonCourses)

        val sectionIds = HashSet<String>()
        for (o in coursesJsonArray) {
            val course = Course(o as JSONObject)
            sectionIds.addAll(course.sectionNames)
        }

        val sectionNamesString = sectionIds.joinToString(",")
        val rawSectionJsonArray = sendGet("https://api.umd.io/v0/courses/sections/$sectionNamesString")
        val sectionJsonArray = JSONArray(rawSectionJsonArray)

        for (o in sectionJsonArray) {
            val section = Section(o as JSONObject)
            section.meetings.forEach {
                println(it.building)
            }
        }
    }

}