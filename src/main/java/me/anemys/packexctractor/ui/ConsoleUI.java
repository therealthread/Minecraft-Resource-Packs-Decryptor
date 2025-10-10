package me.anemys.packexctractor.ui;

import me.anemys.packexctractor.model.ExtractionResult;
import me.anemys.packexctractor.utils.Sender;

import java.io.File;

@SuppressWarnings("SpellCheckingInspection")
public class ConsoleUI {

    public void displayWelcomeScreen() {
        System.out.println("""

                ╔════════════════════════════════════════════════════════╗
                ║          Minecraft Resource Pack Extractor             ║
                ║                    v1.0 Superior                       ║
                ║          Developed by Anemys (therealthread)           ║
                ║          Discord: _thread                              ║
                ╚════════════════════════════════════════════════════════╝
                """);
    }

    public void displayUsageHelp() {
        System.out.println("""
                Usage:
                  java -jar rp-extractor.jar <input-file> [output-folder]
            
                Parameters:
                  input-file     : Path to the .zip resource pack file
                  output-folder  : (Optional) Output directory path
                                   If not specified, uses input filename without extension
            
                Examples:
                  java -jar rp-extractor.jar mypack.zip
                  java -jar rp-extractor.jar mypack.zip extracted_pack
                  java -jar rp-extractor.jar "C:\\packs\\mypack.zip" "C:\\output"
            
                Supported formats:
                  - ZIP files (.zip)
                  - JAR files (.jar)
                """);
    }

    public void displayFileInfo(File inputFile, File outputDir) {
        String info = String.format("""
        
                ═══════════════════════════════════════════════════════
        
                Input File Information:
                 * File: %s
                 * Path: %s
        
                Output Directory:
                 * Path: %s
        
                ═══════════════════════════════════════════════════════
        
                """,
                        inputFile.getName(),
                        inputFile.getAbsolutePath(),
                        outputDir.getAbsolutePath()
                );
                System.out.print(info);
    }


    public void displayExtractionSummary(ExtractionResult result) {
        String summary = String.format("""
                
                ╔════════════════════════════════════════════════════════╗
                ║                   EXTRACTION COMPLETE                  ║
                ╚════════════════════════════════════════════════════════╝
        
                Stats:
                 * Successfully extracted: %d files
                 * Failed: %d files
                 * Skipped: %d files
                 * Time: %s
                 * Output location: %s
        
                """,
                        result.getSuccessfulFiles(),
                        result.getFailedFiles(),
                        result.getSkippedFiles(),
                        formatDuration(result.getDuration()),
                        result.getOutputPath()
                );

        System.out.print(summary);

        if (result.getFailedFiles() == 0) {
            Sender.sendSuccess("All files extracted successfully!");
        } else if (result.getSuccessfulFiles() > 0) {
            Sender.sendWarning("Extraction completed with " + result.getFailedFiles() + " errors");
        } else {
            Sender.sendError("Extraction failed completely!");
        }
    }

    private String formatDuration(java.time.Duration duration) {
        long seconds = duration.getSeconds();
        long millis = duration.toMillis() % 1000;

        if (seconds > 0) {
            return String.format("%d.%03d seconds", seconds, millis);
        } else {
            return millis + " milliseconds";
        }
    }
}