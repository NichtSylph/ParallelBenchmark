/**
 * Represents a course with a unique code and a name.
 * This class is designed to be a simple data holder without any concurrency control mechanisms,
 * as it is assumed that the objects of this class will be accessed by a single thread at a time
 * or will be managed by thread-safe collections.
 * @author Joel Santos
 * @version 1.0
 * @since 10-20-2023
 */

package clientemu;

public class Course {
    private String code;
    private String name;

    /**
     * Constructs a new Course with the specified code and name.
     *
     * @param code The unique identifier for the course.
     * @param name The name of the course.
     */
    public Course(String code, String name) {
        this.code = code;
        this.name = name;
    }

    /**
     * Retrieves the course code.
     *
     * @return The course code.
     */
    public String getCode() {
        return code;
    }

    /**
     * Sets the course code.
     *
     * @param code The course code to set.
     */
    public void setCode(String code) {
        this.code = code;
    }

    /**
     * Retrieves the course name.
     *
     * @return The course name.
     */
    public String getName() {
        return name;
    }

    /**
     * Sets the course name.
     *
     * @param name The name to set for this course.
     */
    public void setName(String name) {
        this.name = name;
    }
}
