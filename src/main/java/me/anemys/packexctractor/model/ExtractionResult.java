package me.anemys.packexctractor.model;

import java.time.Duration;

public class ExtractionResult {
    private final int successfulFiles;
    private final int failedFiles;
    private final int skippedFiles;
    private final long totalExtractedSize;
    private final Duration duration;
    private final String outputPath;

    public ExtractionResult(int successfulFiles, int failedFiles, int skippedFiles,
                            long totalExtractedSize, Duration duration, String outputPath) {
        this.successfulFiles = successfulFiles;
        this.failedFiles = failedFiles;
        this.skippedFiles = skippedFiles;
        this.totalExtractedSize = totalExtractedSize;
        this.duration = duration;
        this.outputPath = outputPath;
    }

    public int getSuccessfulFiles() { return successfulFiles; }
    public int getFailedFiles() { return failedFiles; }
    public int getSkippedFiles() { return skippedFiles; }
    public long getTotalExtractedSize() { return totalExtractedSize; }
    public Duration getDuration() { return duration; }
    public String getOutputPath() { return outputPath; }
}