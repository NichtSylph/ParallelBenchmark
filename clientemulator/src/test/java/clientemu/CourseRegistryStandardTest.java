package clientemu;

import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

public class CourseRegistryStandardTest {

    private CourseRegistryStandard registry;

    @Before
    public void setUp() {
        // This method is called before each test.
        registry = new CourseRegistryStandard();
    }

    @Test
    public void testAddGetCourse() {
        // Test adding and retrieving a course.
        Course course = new Course("CSC201", "Software Engineering");
        registry.addCourse(course);
        Course retrieved = registry.getCourse("CSC201");
        assertNotNull("Course should not be null", retrieved);
        assertEquals("Software Engineering", retrieved.getName());
    }

    @Test
    public void testUpdateCourse() {
        // Test updating a course.
        Course course = new Course("CSC202", "Operating Systems");
        registry.addCourse(course);
        registry.updateCourse("CSC202", "Advanced Operating Systems");
        Course updatedCourse = registry.getCourse("CSC202");
        assertEquals("Advanced Operating Systems", updatedCourse.getName());
    }

    @Test
    public void testRemoveCourse() {
        // Test removing a course.
        Course course = new Course("CSC203", "Computer Networks");
        registry.addCourse(course);
        assertNotNull(registry.getCourse("CSC203"));
        registry.removeCourse("CSC203");
        assertNull(registry.getCourse("CSC203"));
    }
}
