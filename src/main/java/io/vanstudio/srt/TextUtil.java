package io.vanstudio.srt;

public class TextUtil {
    public static final String LINE_BREAK = "\n";
    public static final String CONV_START = "- ";
    public static final String SP = " ";

    public static String normalize(String sub) {
        StringBuilder sb = new StringBuilder();
        String striped = strip(sub);
        String[] split = striped.split(LINE_BREAK);
        for (String line : split) {
            if (striped.startsWith(CONV_START)) {
                if (!sb.isEmpty()) sb.append(LINE_BREAK);
                sb.append(line);
            } else {
                if (!sb.isEmpty()) sb.append(SP);
                sb.append(line);
            }
        }

        return sb.toString();
    }

    private static String strip(String sub) {
        return stripObj(stripXml(sub.trim()));
    }

    private static String stripXml(String xml) {
        return xml.replaceAll("<[^>]+>", "");
    }

    private static String stripObj(String xml) {
        return xml.replaceAll("\\{[^}]+}", "");
    }
}
