/**
 * A thread-safe course registry using ConcurrentHashMap.
 * This implementation leverages the thread-safe operations provided by ConcurrentHashMap
 * to handle concurrent data access without the need for explicit synchronization.
 * @author Joel Santos
 * @version 1.0
 * @since 10-20-2023
 */

package clientemu;

import java.util.concurrent.ConcurrentHashMap;

public class CourseRegistryStandard {
    private final ConcurrentHashMap<String, Course> courses = new ConcurrentHashMap<>();

    /**
     * Adds a new course to the registry or replaces an existing one.
     * ConcurrentHashMap handles thread safety internally.
     *
     * @param course The course object to be added to the registry.
     */
    public void addCourse(Course course) {
        courses.put(course.getCode(), course);
    }

    /**
     * Retrieves a course from the registry based on the course code.
     * ConcurrentHashMap ensures thread-safe read operations.
     *
     * @param code The course code.
     * @return The course associated with the code or null if not found.
     */
    public Course getCourse(String code) {
        return courses.get(code);
    }

    /**
     * Updates the name of an existing course in the registry.
     * If the course is present, its name is updated in a thread-safe manner using ConcurrentHashMap's atomic operations.
     *
     * @param code    The course code of the course to update.
     * @param newName The new name for the course.
     */
    public void updateCourse(String code, String newName) {
        Course course = courses.get(code);
        if (course != null) {
            course.setName(newName);
        }
    }

    /**
     * Removes a course from the registry based on the course code.
     * ConcurrentHashMap ensures thread-safe removal operations.
     *
     * @param code The course code of the course to remove.
     */
    public void removeCourse(String code) {
        courses.remove(code);
    }
}
