package clientemu;

import java.util.List;
import java.util.Random;
import java.util.concurrent.CountDownLatch;

public class ClientSimulation implements Runnable {
    private final CourseRegistryCustom customRegistry;
    private final CourseRegistryStandard standardRegistry;
    private final List<Course> courseList;
    private final Random random = new Random();
    private final CountDownLatch latch;
    private final int operationType;

    /**
     * Constructs a ClientSimulation with the given registries, course list, latch, and operation type.
     *
     * @param customRegistry    A custom implementation of a course registry with explicit locking.
     * @param standardRegistry  A standard implementation of a course registry using ConcurrentHashMap.
     * @param courseList        A list of courses to be used during the simulation.
     * @param latch             A CountDownLatch to signal the completion of the task.
     * @param operationType     An integer specifying the type of operation (0: Add, 1: Get, 2: Update, 3: Remove).
     */
    public ClientSimulation(CourseRegistryCustom customRegistry, CourseRegistryStandard standardRegistry, List<Course> courseList, CountDownLatch latch, int operationType) {
        this.customRegistry = customRegistry;
        this.standardRegistry = standardRegistry;
        this.courseList = courseList;
        this.latch = latch;
        this.operationType = operationType;
    }

    /**
     * Executes the client simulation, performing random operations on the course registries based on the specified operation type.
     * Once all operations are complete, the latch count is decremented.
     */
    @Override
    public void run() {
        try {
            for (int i = 0; i < courseList.size(); i++) {
                int courseIndex = random.nextInt(courseList.size());
                Course course = courseList.get(courseIndex);
                switch (operationType) {
                    case 0: // Add a course
                        customRegistry.addCourse(course);
                        standardRegistry.addCourse(course);
                        break;
                    case 1: // Get a course
                        customRegistry.getCourse(course.getCode());
                        standardRegistry.getCourse(course.getCode());
                        break;
                    case 2: // Update a course name
                        String newName = course.getName() + " Updated";
                        customRegistry.updateCourse(course.getCode(), newName);
                        standardRegistry.updateCourse(course.getCode(), newName);
                        break;
                    case 3: // Remove a course
                        customRegistry.removeCourse(course.getCode());
                        standardRegistry.removeCourse(course.getCode());
                        break;
                }
            }
        } finally {
            latch.countDown();
        }
    }
}
