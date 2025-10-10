package me.anemys.packexctractor.utils;

import java.io.File;
import java.io.IOException;

public class FileValidator {

    public static void validateInputFile(File inputFile) throws IOException {
        if (!inputFile.exists()) {
            throw new IOException("Input file does not exist: " + inputFile.getAbsolutePath());
        }

        if (!inputFile.isFile()) {
            throw new IOException("Input path is not a file: " + inputFile.getAbsolutePath());
        }

        if (!inputFile.canRead()) {
            throw new IOException("Cannot read input file: " + inputFile.getAbsolutePath());
        }

        String fileName = inputFile.getName().toLowerCase();
        if (!fileName.endsWith(".zip") && !fileName.endsWith(".jar")) {
            throw new IOException("Unsupported file format. Only .zip and .jar files are supported.");
        }

        if (inputFile.length() == 0) {
            throw new IOException("Input file is empty: " + inputFile.getAbsolutePath());
        }
    }

    public static void validateOutputDirectory(File outputDir) throws IOException {
        if (outputDir.exists()) {
            if (!outputDir.isDirectory()) {
                throw new IOException("Output path exists but is not a directory: " + outputDir.getAbsolutePath());
            }

            if (!outputDir.canWrite()) {
                throw new IOException("Cannot write to output directory: " + outputDir.getAbsolutePath());
            }
        } else {
            try {
                if (!outputDir.mkdirs()) {
                    throw new IOException("Failed to create output directory: " + outputDir.getAbsolutePath());
                }
            } catch (SecurityException e) {
                throw new IOException("Permission denied creating output directory: " + outputDir.getAbsolutePath(), e);
            }
        }
    }
}