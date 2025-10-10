package me.anemys.packexctractor.model;

public class ExtractionStats {
    private int successfulFiles = 0;
    private int failedFiles = 0;
    private int skippedFiles = 0;

    public void incrementSuccessfulFiles() {
        successfulFiles++;
    }

    public void incrementFailedFiles() {
        failedFiles++;
    }

    public void incrementSkippedFiles() {
        skippedFiles++;
    }

    public int getSuccessfulFiles() {
        return successfulFiles;
    }

    public int getFailedFiles() {
        return failedFiles;
    }

    public int getSkippedFiles() {
        return skippedFiles;
    }
}