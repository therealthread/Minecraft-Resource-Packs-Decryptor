package me.anemys.packexctractor.utils;

public class Sender {

    public static void send(FileType fileType, String message) {
        System.out.println(fileType + " " + message);
    }

    public static void sendInfo(String message) {
        System.out.println("[INFO]" + " " + message);
    }

    public static void sendWarning(String message) {
        System.out.println("[WARNING]" + " " + message);
    }

    public static void sendError(String message) {
        System.err.println("[ERROR]" + " " + message);
    }

    public static void sendSuccess(String message) {
        System.out.println("[SUCCESS]" + " " + message);
    }
}