package com.valtech.demo;

public class TestCase {
    private String dataStructure;
    private int numberOfPartitions;
    private int numberOfThreads;
    private boolean wasProcessed;

    public TestCase(String dataStructure, int numberOfPartitions, int numberOfThreads) {
        this.dataStructure = dataStructure;
        this.numberOfPartitions = numberOfPartitions;
        this.numberOfThreads = numberOfThreads;
    }

    public boolean wasProcessed() {
        return this.wasProcessed;
    }

    public String getDataStructure() {
        return dataStructure;
    }

    public void setDataStructure(String dataStructure) {
        this.dataStructure = dataStructure;
    }

    public int getNumberOfPartitions() {
        return numberOfPartitions;
    }

    public void setNumberOfPartitions(int numberOfPartitions) {
        this.numberOfPartitions = numberOfPartitions;
    }

    public int getNumberOfThreads() {
        return numberOfThreads;
    }

    public void setNumberOfThreads(int numberOfThreads) {
        this.numberOfThreads = numberOfThreads;
    }

    public boolean isWasProcessed() {
        return wasProcessed;
    }

    public void setWasProcessed(boolean wasProcessed) {
        this.wasProcessed = wasProcessed;
    }
}
