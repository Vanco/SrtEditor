package io.vanstudio.srt;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;

public class FileLogger {
    private static final String LOG_FILE = "srteditor.log";
    private static final String APP_HOME = ".srteditor/";

    public static void log(String message) {
        try {
            Path path = Paths.get(System.getProperty("user.home"), APP_HOME);
            if (!Files.exists(path)) {
                Files.createDirectories(path);
            }
            Path logPath = Paths.get(System.getProperty("user.home"), APP_HOME + LOG_FILE);
            Files.write(logPath, (message + "\n").getBytes(),
                    StandardOpenOption.CREATE, StandardOpenOption.APPEND);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
