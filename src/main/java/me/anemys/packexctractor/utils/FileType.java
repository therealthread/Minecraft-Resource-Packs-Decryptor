package me.anemys.packexctractor.utils;

@SuppressWarnings("SpellCheckingInspection")
public enum FileType {
    IMAGE("[Image]", ".png", ".jpeg", ".jpg", ".gif", ".bmp", ".tiff", ".avif"),
    JSON("[Json]", ".json", ".mcmeta"),
    SHADER("[Shader]", ".glsl", ".fsh", ".vsh", ".vert", ".frag"),
    SOUND("[Audio]", ".ogg", ".wav", ".mp3", ".flac"),
    TEXT("[Text]", ".txt", ".md", ".yml", ".yaml", ".properties"),
    MODEL("[Model]", ".bbmodel", ".geo.json"),
    ARCHIVE("[Archive]", ".zip", ".jar", ".rar"),
    UNKNOWN("[File]");

    private final String prefix;
    private final String[] extensions;

    FileType(String prefix, String... extensions) {
        this.prefix = prefix;
        this.extensions = extensions;
    }

    public static FileType getFileType(String fileName) {
        String lowerFileName = fileName.toLowerCase();

        for (FileType fileType : values()) {
            if (fileType != UNKNOWN && fileType.extensions != null) {
                for (String extension : fileType.extensions) {
                    if (lowerFileName.endsWith(extension)) {
                        return fileType;
                    }
                }
            }
        }
        return UNKNOWN;
    }

    @Override
    public String toString() {
        return prefix;
    }
}
