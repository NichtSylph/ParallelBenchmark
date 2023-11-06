/**
 * This class contains benchmarks for testing the performance of different course registries.
 * It sets up the environment for JMH benchmarks, executes the benchmarks, and tears down the environment afterwards.
 * @author Joel Santos
 * @version 1.0
 * @since 10-20-2023
 */

package clientemu;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
import org.openjdk.jmh.results.format.ResultFormatType;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@State(Scope.Benchmark)
public class CourseSchedulerBenchmark {

    private CourseRegistryCustom customRegistry;
    private CourseRegistryStandard standardRegistry;
    private List<Course> courseList;
    private ExecutorService executor;

    /**
     * Sets up the benchmarking environment by loading the course list,
     * initializing registries, and setting up a thread pool equal to the number of available processors.
     */
    @Setup
    public void setup() throws IOException {
        courseList = Files.lines(Paths.get("CSC375\\ClientEmulator-JS\\clientemulator\\src\\main\\java\\clientemu\\SUNYOswegoCourses.txt"))
                .map(line -> line.split(" - "))
                .filter(parts -> parts.length == 2)
                .map(parts -> new Course(parts[0].trim(), parts[1].trim()))
                .collect(Collectors.toList());

        customRegistry = new CourseRegistryCustom();
        standardRegistry = new CourseRegistryStandard();

        for (Course course : courseList) {
            customRegistry.addCourse(course);
            standardRegistry.addCourse(course);
        }

        // Utilizes available processors for the thread pool size
        executor = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());
    }

    /**
     * Tears down the benchmarking environment by shutting down the executor service immediately.
     */
    @TearDown
    public void tearDown() {
        executor.shutdownNow();
    }

    /**
     * Benchmarks the custom course registry implementation.
     *
     * @param blackhole A Blackhole to consume results and prevent dead code elimination.
     * @throws InterruptedException If the thread is interrupted while waiting.
     */
    @Benchmark
    @Warmup(iterations = 100, time = 1, timeUnit = TimeUnit.SECONDS)
    @Measurement(iterations = 100, time = 1, timeUnit = TimeUnit.SECONDS)
    @Fork(1)
    public void testCustomRegistry(Blackhole blackhole) throws InterruptedException {
        executeBenchmark(customRegistry, blackhole);
    }

    /**
     * Benchmarks the standard course registry implementation using ConcurrentHashMap.
     *
     * @param blackhole A Blackhole to consume results and prevent dead code elimination.
     * @throws InterruptedException If the thread is interrupted while waiting.
     */
    @Benchmark
    @Warmup(iterations = 100, time = 1, timeUnit = TimeUnit.SECONDS)
    @Measurement(iterations = 100, time = 1, timeUnit = TimeUnit.SECONDS)
    @Fork(1)
    public void testStandardRegistry(Blackhole blackhole) throws InterruptedException {
        executeBenchmark(standardRegistry, blackhole);
    }

    /**
     * Executes the benchmark for the given custom course registry.
     *
     * @param registry   The custom course registry to test.
     * @param blackhole  A Blackhole to consume results and prevent dead code elimination.
     * @throws InterruptedException If the thread is interrupted while waiting.
     */
    private void executeBenchmark(CourseRegistryCustom registry, Blackhole blackhole) throws InterruptedException {
        CountDownLatch latch = new CountDownLatch(10);
        for (int i = 0; i < 10; i++) {
            executor.submit(new ClientSimulation(registry, standardRegistry, courseList, latch));
        }
        latch.await();
        blackhole.consume(latch.getCount());
    }

    /**
     * Executes the benchmark for the given standard course registry.
     *
     * @param registry   The standard course registry to test.
     * @param blackhole  A Blackhole to consume results and prevent dead code elimination.
     * @throws InterruptedException If the thread is interrupted while waiting.
     */
    private void executeBenchmark(CourseRegistryStandard registry, Blackhole blackhole) throws InterruptedException {
        CountDownLatch latch = new CountDownLatch(10);
        for (int i = 0; i < 10; i++) {
            executor.submit(new ClientSimulation(customRegistry, registry, courseList, latch));
        }
        latch.await();
        blackhole.consume(latch.getCount());
    }

    /**
     * The main method to execute the benchmarks.
     *
     * @param args Command line arguments (not used).
     * @throws Exception If there is a problem executing the benchmarks.
     */
    public static void main(String[] args) throws Exception {
        Options opt = new OptionsBuilder()
                .include(CourseSchedulerBenchmark.class.getSimpleName())
                .shouldFailOnError(true)
                .result("CSC375\\ClientEmulator-JS\\clientemulator\\src\\main\\java\\clientemu\\benchmark_results.json")
                .resultFormat(ResultFormatType.JSON)
                .build();

        new Runner(opt).run();
    }
}
