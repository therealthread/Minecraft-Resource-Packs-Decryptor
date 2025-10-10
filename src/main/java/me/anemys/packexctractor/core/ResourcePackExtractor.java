package me.anemys.packexctractor.core;

import me.anemys.packexctractor.model.ExtractionResult;
import me.anemys.packexctractor.model.ExtractionStats;
import me.anemys.packexctractor.ui.ConsoleUI;
import me.anemys.packexctractor.ui.ProgressTracker;
import me.anemys.packexctractor.utils.FileType;
import me.anemys.packexctractor.utils.Sender;

import java.io.File;
import java.io.IOException;
import java.nio.file.*;
import java.time.Duration;
import java.time.Instant;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.concurrent.atomic.AtomicLong;

public class ResourcePackExtractor {
    private final File inputFile;
    private final File outputDirectory;
    private final ConsoleUI ui;
    private final ProgressTracker progressTracker;

    public ResourcePackExtractor(File inputFile, File outputDirectory) {
        this.inputFile = inputFile;
        this.outputDirectory = outputDirectory;
        this.ui = new ConsoleUI();
        this.progressTracker = new ProgressTracker();
    }

    public ExtractionResult extract() throws IOException {
        Instant startTime = Instant.now();
        ExtractionStats stats = new ExtractionStats();

        try (ZipFile zipFile = new ZipFile(inputFile)) {
            int totalEntries = zipFile.size();
            AtomicLong totalExtractedSize = new AtomicLong(0);

            Sender.sendInfo("Starting extraction of " + totalEntries + " files...");
            progressTracker.initialize(totalEntries);

            zipFile.stream().forEach(entry -> {
                try {
                    extractEntry(zipFile, entry, stats, totalExtractedSize);
                } catch (IOException e) {
                    stats.incrementFailedFiles();
                    Sender.sendError("Failed to extract " + entry.getName() + ": " + e.getMessage());
                }
            });

            Instant endTime = Instant.now();
            Duration duration = Duration.between(startTime, endTime);

            ExtractionResult result = new ExtractionResult(
                    stats.getSuccessfulFiles(),
                    stats.getFailedFiles(),
                    stats.getSkippedFiles(),
                    totalExtractedSize.get(),
                    duration,
                    outputDirectory.getAbsolutePath()
            );

            ui.displayExtractionSummary(result);
            return result;

        } catch (IOException e) {
            throw new IOException("Failed to open zip file: " + inputFile.getName(), e);
        }
    }

    private void extractEntry(ZipFile zipFile, ZipEntry entry, ExtractionStats stats, AtomicLong totalSize) throws IOException {
        if (entry.isDirectory()) {
            createDirectory(entry, stats);
            return;
        }

        Path targetPath = Paths.get(outputDirectory.getAbsolutePath(), entry.getName());

        try {
            Files.createDirectories(targetPath.getParent());

            if (Files.exists(targetPath)) {
                stats.incrementSkippedFiles();
                progressTracker.updateProgress(entry.getName(), "already exists", 0);
                return;
            }

            Files.copy(zipFile.getInputStream(entry), targetPath, StandardCopyOption.REPLACE_EXISTING);

            long fileSize = entry.getSize();
            totalSize.addAndGet(fileSize);
            stats.incrementSuccessfulFiles();

            FileType fileType = FileType.getFileType(entry.getName());

            progressTracker.updateProgress(entry.getName(),"", fileSize);
            Sender.send(fileType, entry.getName());

        } catch (FileAlreadyExistsException e) {
            stats.incrementSkippedFiles();
            progressTracker.updateProgress(entry.getName(), "already exists", 0);
        } catch (IOException e) {
            stats.incrementFailedFiles();
            throw e;
        }
    }

    private void createDirectory(ZipEntry entry, ExtractionStats stats) {
        try {
            Path dirPath = Paths.get(outputDirectory.getAbsolutePath(), entry.getName());
            Files.createDirectories(dirPath);
            stats.incrementSuccessfulFiles();
        } catch (IOException e) {
            stats.incrementFailedFiles();
            Sender.sendError("Failed to create directory: " + entry.getName());
        }
    }
}
