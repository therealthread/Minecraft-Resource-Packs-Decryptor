package me.anemys.packexctractor.core;

import me.anemys.packexctractor.model.ExtractionResult;
import me.anemys.packexctractor.model.ExtractionStats;
import me.anemys.packexctractor.ui.ConsoleUI;
import me.anemys.packexctractor.ui.ProgressTracker;
import me.anemys.packexctractor.utils.FileType;
import me.anemys.packexctractor.utils.Sender;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.*;
import java.time.Duration;
import java.time.Instant;
import java.util.Enumeration;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.Collections;

import org.apache.commons.compress.archivers.zip.ZipArchiveEntry;

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

    @SuppressWarnings("deprecation")
    public void extract() throws IOException {
        Instant startTime = Instant.now();
        ExtractionStats stats = new ExtractionStats();
        AtomicLong totalExtractedSize = new AtomicLong(0);

        boolean usedApacheFallback = false;

        try {

            try (ZipFile zipFile = new ZipFile(inputFile)) {
                int totalEntries = zipFile.size();
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
            }
        } catch (IOException javaZipError) {

            usedApacheFallback = true;
            Sender.sendError("Java ZipFile failed: " + javaZipError.getMessage());
            Sender.sendInfo("Trying Apache Commons Compress fallback...");

            try (org.apache.commons.compress.archivers.zip.ZipFile apacheZip =
                         new org.apache.commons.compress.archivers.zip.ZipFile(inputFile)) {

                Enumeration<ZipArchiveEntry> enumeration = apacheZip.getEntries();
                List<ZipArchiveEntry> entryList = Collections.list(enumeration);
                int totalEntries = entryList.size();

                Sender.sendInfo("Starting extraction of " + totalEntries + " files (Apache)...");
                progressTracker.initialize(totalEntries);

                for (ZipArchiveEntry entry : entryList) {
                    try {
                        extractEntryApache(apacheZip, entry, stats, totalExtractedSize);
                    } catch (IOException e) {
                        stats.incrementFailedFiles();
                        Sender.sendError("Failed to extract " + entry.getName() + ": " + e.getMessage());
                    }
                }
            }
        }

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

        if (usedApacheFallback) {
            Sender.sendInfo("Extraction completed using Apache Commons Compress fallback.");
        } else {
            Sender.sendInfo("Extraction completed using Java ZipFile.");
        }

        ui.displayExtractionSummary(result);
    }

    private void extractEntry(ZipFile zipFile, ZipEntry entry,
                              ExtractionStats stats, AtomicLong totalSize) throws IOException {
        if (entry.isDirectory()) {
            createDirectory(entry.getName(), stats);
            return;
        }

        Path targetPath = Paths.get(outputDirectory.getAbsolutePath(), entry.getName());
        Files.createDirectories(targetPath.getParent());

        try (InputStream is = zipFile.getInputStream(entry)) {
            long copied = Files.copy(is, targetPath, StandardCopyOption.REPLACE_EXISTING);
            long size = entry.getSize();
            long fileSize = (size >= 0) ? size : copied;
            totalSize.addAndGet(fileSize);
            stats.incrementSuccessfulFiles();

            FileType fileType = FileType.getFileType(entry.getName());
            progressTracker.updateProgress(entry.getName(), "", fileSize);
            Sender.send(fileType, entry.getName());
        } catch (FileAlreadyExistsException e) {
            stats.incrementSkippedFiles();
            progressTracker.updateProgress(entry.getName(), "already exists", 0);
        }
    }

    private void extractEntryApache(org.apache.commons.compress.archivers.zip.ZipFile zipFile,
                                    ZipArchiveEntry entry,
                                    ExtractionStats stats, AtomicLong totalSize) throws IOException {
        if (entry.isDirectory()) {
            createDirectory(entry.getName(), stats);
            return;
        }

        Path targetPath = Paths.get(outputDirectory.getAbsolutePath(), entry.getName());
        Files.createDirectories(targetPath.getParent());

        try (InputStream is = zipFile.getInputStream(entry)) {
            long copied = Files.copy(is, targetPath, StandardCopyOption.REPLACE_EXISTING);
            long size = entry.getSize();
            long fileSize = (size >= 0) ? size : copied;
            totalSize.addAndGet(fileSize);
            stats.incrementSuccessfulFiles();

            FileType fileType = FileType.getFileType(entry.getName());
            progressTracker.updateProgress(entry.getName(), "", fileSize);
            Sender.send(fileType, entry.getName());
        } catch (FileAlreadyExistsException e) {
            stats.incrementSkippedFiles();
            progressTracker.updateProgress(entry.getName(), "already exists", 0);
        }
    }

    private void createDirectory(String entryName, ExtractionStats stats) {
        try {
            Path dirPath = Paths.get(outputDirectory.getAbsolutePath(), entryName);
            Files.createDirectories(dirPath);
            stats.incrementSuccessfulFiles();
        } catch (IOException e) {
            stats.incrementFailedFiles();
            Sender.sendError("Failed to create directory: " + entryName);
        }
    }
}
