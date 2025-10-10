package me.anemys.packexctractor;

import me.anemys.packexctractor.core.ResourcePackExtractor;
import me.anemys.packexctractor.ui.ConsoleUI;
import me.anemys.packexctractor.utils.FileValidator;
import me.anemys.packexctractor.utils.Sender;

import java.io.File;

public class Main {
    private static final ConsoleUI ui = new ConsoleUI();

    public static void main(String[] args) {
        ui.displayWelcomeScreen();

        try {
            if (args.length == 0) {
                ui.displayUsageHelp();
                return;
            }

            File inputFile = new File(args[0]);
            FileValidator.validateInputFile(inputFile);

            String outputPath = determineOutputPath(args, inputFile);
            File outputDir = new File(outputPath);
            FileValidator.validateOutputDirectory(outputDir);

            ui.displayFileInfo(inputFile, outputDir);

            ResourcePackExtractor extractor = new ResourcePackExtractor(inputFile, outputDir);
            extractor.extract();

        } catch (Exception e) {
            Sender.sendError("Unexpected error: " + e.getMessage());
            e.printStackTrace();
            System.exit(1);
        }
    }

    private static String determineOutputPath(String[] args, File inputFile) {
        if (args.length > 1) {
            return args[1];
        }

        String fileName = inputFile.getName();
        int lastDotIndex = fileName.lastIndexOf('.');
        return lastDotIndex > 0 ? fileName.substring(0, lastDotIndex) : fileName + "_extracted";
    }
}
