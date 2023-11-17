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

    @Param({"0", "1", "2", "3"}) // Parameter to specify the operation type (0: Add, 1: Get, 2: Update, 3: Remove)
    private int operationType;

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

        executor = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());
    }

    @TearDown
    public void tearDown() {
        executor.shutdownNow();
    }

    @Benchmark
    @Warmup(iterations = 20, time = 1, timeUnit = TimeUnit.SECONDS)
    @Measurement(iterations = 20, time = 1, timeUnit = TimeUnit.SECONDS)
    @Fork(1)
    public void testCustomRegistry(Blackhole blackhole) throws InterruptedException {
        executeBenchmark(customRegistry, blackhole);
    }

    @Benchmark
    @Warmup(iterations = 20, time = 1, timeUnit = TimeUnit.SECONDS)
    @Measurement(iterations = 20, time = 1, timeUnit = TimeUnit.SECONDS)
    @Fork(1)
    public void testStandardRegistry(Blackhole blackhole) throws InterruptedException {
        executeBenchmark(standardRegistry, blackhole);
    }

    private void executeBenchmark(CourseRegistryCustom registry, Blackhole blackhole) throws InterruptedException {
        CountDownLatch latch = new CountDownLatch(10);
        for (int i = 0; i < 10; i++) {
            executor.submit(new ClientSimulation(registry, standardRegistry, courseList, latch, operationType));
        }
        latch.await();
        blackhole.consume(latch.getCount());
    }

    private void executeBenchmark(CourseRegistryStandard registry, Blackhole blackhole) throws InterruptedException {
        CountDownLatch latch = new CountDownLatch(10);
        for (int i = 0; i < 10; i++) {
            executor.submit(new ClientSimulation(customRegistry, registry, courseList, latch, operationType));
        }
        latch.await();
        blackhole.consume(latch.getCount());
    }

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

