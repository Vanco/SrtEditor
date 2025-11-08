package io.vanstudio.srt;

public class EnvironmentDiagnostics {
    public static void logEnvironment() {
        System.out.println("=== Environment Diagnostics ===");
        System.out.println("Working Directory: " + System.getProperty("user.dir"));
        System.out.println("Home Directory: " + System.getProperty("user.home"));
        System.out.println("Java Home: " + System.getProperty("java.home"));

        FileLogger.log("=== Environment Diagnostics ===");
        FileLogger.log("Working Directory: " + System.getProperty("user.dir"));
        FileLogger.log("Home Directory: " + System.getProperty("user.home"));
        FileLogger.log("Java Home: " + System.getProperty("java.home"));

        // 检查关键环境变量
        String[] importantVars = {"PATH", "HOME", "HTTP_PROXY", "HTTPS_PROXY"};
        for (String var : importantVars) {
            String value = System.getenv(var);
            FileLogger.log("Env " + var + ": " + (value != null ? value : "NOT SET"));
        }

        // 检查网络连接
        testNetworkConnectivity();
    }

    private static void testNetworkConnectivity() {
        try {
            AzureSDKTranslator.getInstance().connect();
            FileLogger.log("Network connectivity: OK");
        } catch (Exception e) {
            FileLogger.log("Network connectivity: FAILED - " + e.getMessage());
        }
    }
}
