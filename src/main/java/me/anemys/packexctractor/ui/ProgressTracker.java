package me.anemys.packexctractor.ui;

public class ProgressTracker {
    private int totalFiles;
    private int processedFiles;

    public void initialize(int totalFiles) {
        this.totalFiles = totalFiles;
        this.processedFiles = 0;
    }

    public void updateProgress(String fileName, String info, long fileSize) {
        processedFiles++;
        double percentage = (double) processedFiles / totalFiles * 100;

        String progressBar = createProgressBar(percentage);
        System.out.printf("\r[%s] %.1f%% [%d/%d] %s - %s",
                progressBar, percentage, processedFiles, totalFiles, fileName, info);

        if (processedFiles == totalFiles) {
            System.out.println();
        }
    }

    private String createProgressBar(double percentage) {
        int barLength = 20;
        int filledLength = (int) (barLength * percentage / 100);

        StringBuilder bar = new StringBuilder();
        for (int i = 0; i < barLength; i++) {
            if (i < filledLength) {
                bar.append("█");
            } else {
                bar.append("░");
            }
        }
        return bar.toString();
    }
}