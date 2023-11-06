package clientemu;

import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

public class CourseRegistryCustomTest {

    private CourseRegistryCustom registry;

    @Before
    public void setUp() {
        // This method is called before each test.
        registry = new CourseRegistryCustom();
    }

    @Test
    public void testAddGetCourse() {
        // Test adding and retrieving a course.
        Course course = new Course("CSC101", "Intro to Computer Science");
        registry.addCourse(course);
        Course retrieved = registry.getCourse("CSC101");
        assertNotNull("Course should not be null", retrieved);
        assertEquals("Intro to Computer Science", retrieved.getName());
    }

    @Test
    public void testUpdateCourse() {
        // Test updating a course.
        Course course = new Course("CSC102", "Data Structures");
        registry.addCourse(course);
        registry.updateCourse("CSC102", "Advanced Data Structures");
        Course updatedCourse = registry.getCourse("CSC102");
        assertEquals("Advanced Data Structures", updatedCourse.getName());
    }

    @Test
    public void testRemoveCourse() {
        // Test removing a course.
        Course course = new Course("CSC103", "Algorithms");
        registry.addCourse(course);
        assertNotNull(registry.getCourse("CSC103"));
        registry.removeCourse("CSC103");
        assertNull(registry.getCourse("CSC103"));
    }
}
