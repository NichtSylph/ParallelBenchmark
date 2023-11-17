/**
 * A thread-safe implementation of a course registry using ReentrantReadWriteLock for concurrency control.
 * This class ensures that multiple threads can safely access and modify the courses HashMap without causing data races. 
 * @author Joel Santos
 * @version 1.0
 * @since 10-20-2023
 */

package clientemu;

import java.util.HashMap;
import java.util.concurrent.locks.ReentrantReadWriteLock;
public class CourseRegistryCustom {
    private final HashMap<String, Course> courses = new HashMap<>();
    private final ReentrantReadWriteLock lock = new ReentrantReadWriteLock();

    /**
     * Adds a new course to the registry or replaces an existing one.
     * Write lock is employed to ensure exclusive access during the operation.
     *
     * @param course The course object to be added to the registry.
     */
    public void addCourse(Course course) {
        lock.writeLock().lock();
        try {
            courses.put(course.getCode(), course);
        } finally {
            lock.writeLock().unlock();
        }
    }

    /**
     * Retrieves a course from the registry based on the course code.
     * Read lock is employed to allow concurrent reads while ensuring data integrity.
     *
     * @param code The course code.
     * @return The course associated with the code or null if not found.
     */
    public Course getCourse(String code) {
        lock.readLock().lock();
        try {
            return courses.get(code);
        } finally {
            lock.readLock().unlock();
        }
    }

    /**
     * Updates the name of an existing course in the registry.
     * Write lock is employed to ensure exclusive access during the update operation.
     *
     * @param code    The course code of the course to update.
     * @param newName The new name for the course.
     */
    public void updateCourse(String code, String newName) {
        lock.writeLock().lock();
        try {
            Course course = courses.get(code);
            if (course != null) {
                course.setName(newName);
            }
        } finally {
            lock.writeLock().unlock();
        }
    }

    /**
     * Removes a course from the registry based on the course code.
     * Write lock is employed to ensure exclusive access during the removal operation.
     *
     * @param code The course code of the course to remove.
     */
    public void removeCourse(String code) {
        lock.writeLock().lock();
        try {
            courses.remove(code);
        } finally {
            lock.writeLock().unlock();
        }
    }
}
