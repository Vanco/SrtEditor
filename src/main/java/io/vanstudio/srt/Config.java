package io.vanstudio.srt;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;

public class Config {
    private static final Properties properties = new Properties();

    static {
        try {
            // 尝试从多个位置加载配置
            loadConfig();
        } catch (IOException e) {
            System.err.println("Failed to load configuration: " + e.getMessage());
        }
    }

    private static void loadConfig() throws IOException {
        String configFile = "config.properties";
        List<Path> possiblePaths = Arrays.asList(
                // 开发环境
                Paths.get(configFile),
                // 打包后应用中的配置
                getAppResourcePath(configFile),
                // 用户主目录配置
                Paths.get(System.getProperty("user.home"), ".srteditor", configFile)
        );

        for (Path path : possiblePaths) {
            if (Files.exists(path)) {
                try (InputStream input = Files.newInputStream(path)) {
                    properties.load(input);
                    System.out.println("Loaded config from: " + path);
                    break;
                }
            }
        }
    }

    private static Path getAppResourcePath(String filename) {
        try {
            String userDir = System.getProperty("user.dir");
            if (userDir.contains(".app/Contents/MacOS")) {
                Path macosDir = Paths.get(userDir);
                return macosDir.getParent().resolve("Resources").resolve(filename);
            }
        } catch (Exception e) {
            // 忽略错误，回退到其他路径
        }
        return Paths.get(filename);
    }

    public static String getTranslatorKey() {
        // 优先使用环境变量，然后使用配置文件
        String key = System.getenv("BING_TRANSLATOR_KEY");
        if (key != null && !key.trim().isEmpty()) {
            return key;
        }
        return properties.getProperty("bing.translator.key");
    }

    public static String getTranslatorRegion() {
        String region = System.getenv("BING_TRANSLATOR_REGION");
        if (region != null && !region.trim().isEmpty()) {
            return region;
        }
        return properties.getProperty("bing.translator.region");
    }
}
