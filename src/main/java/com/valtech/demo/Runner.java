package com.valtech.demo;

import io.github.mobility.university.concurrency.PartitionedBlockingQueue;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.toList;

public class Runner {

    private static final int STEP_WIDTH = 100;
    private static final int MAX_NUMBER_OF_THREADS = 1000;
    private static final int MAX_NUMBER_OF_PARTITIONS = 1000;
    private static final int NUMBER_OF_DATA_ITEMS = 1_000_000;

    private List<TestCase> testCases = new ArrayList<>();
    private Consumer consumer;
    private ExecutorService executorService;
    private Random random;
    List<Object> data;

    public Runner() {
        init();
    }

    private void init() {
        this.random = new Random();
        this.consumer = new Consumer();
        this.data = new ArrayList<>();

        for (int i = 0; i < NUMBER_OF_DATA_ITEMS; i++) {
            data.add(new Object());
        }
        System.out.println("Test data population done.");

       /* this.testCases.add(new TestCase("pbq", 1, 1));
        this.testCases.add(new TestCase("pbq", 10, 1));
        this.testCases.add(new TestCase("pbq", 10, 100));
        this.testCases.add(new TestCase("pbq", 1, 100));*/
        this.testCases.add(new TestCase("pbq", 100, 100));
        this.testCases.add(new TestCase("pbq", 1000, 100));
    }

    public void run() {
        while (testCasesExist()) {
            TestCase currentTestCase = chooseNextTestCase();
            try {
                executeTestCase(currentTestCase);
            } catch (InterruptedException e) {
                e.printStackTrace();
            } finally {
                this.executorService.shutdown();
            }
        }
    }

    private void executeTestCase(TestCase testCase) throws InterruptedException {

        PartitionedBlockingQueue<Integer, Object> queue = new PartitionedBlockingQueue<>(testCase.getNumberOfPartitions());
        int processedDataItems = 0;
        this.executorService = Executors.newFixedThreadPool(testCase.getNumberOfThreads());
        CountDownLatch latch = new CountDownLatch(NUMBER_OF_DATA_ITEMS);

        long start = System.currentTimeMillis();
        while (processedDataItems < NUMBER_OF_DATA_ITEMS) {
            executorService.submit(() -> {
                try {
                    this.produceAndConsumeData(queue, (int) latch.getCount());
                    latch.countDown();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            });
            processedDataItems = processedDataItems + 1;
        }
        latch.await();
        long end = System.currentTimeMillis();
        Duration duration = Duration.ofMillis(end - start);
        testCase.setWasProcessed(true);
        System.out.println(testCase.getDataStructure()
                + ", P: " + testCase.getNumberOfPartitions()
                + ", T: " + testCase.getNumberOfThreads()
                + ", D: " + duration.toMillis());
    }

    private void produceAndConsumeData(PartitionedBlockingQueue<Integer, Object> queue, int index) throws InterruptedException {
        var item = new Object();
        var key = item.hashCode();
        var enqueuedData = queue.acquire(key, item);
        consumer.consume(enqueuedData);
        queue.release(key);
    }

    private TestCase chooseNextTestCase() {
        final List<TestCase> remainingTestCases = testCases.stream().filter(testCase -> !testCase.wasProcessed()).collect(toList());
        return remainingTestCases.get(random.nextInt(remainingTestCases.size()));
    }

    private boolean testCasesExist() {
        final List<TestCase> remainingTestCases = testCases.stream().filter(testCase -> !testCase.wasProcessed()).collect(toList());
        return remainingTestCases.size() > 0;
    }

}
