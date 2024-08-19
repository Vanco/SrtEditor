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

    public static String autoLine(String sub) {
        StringBuilder sb = new StringBuilder(sub);
        if (sb.length() > 30 && sb.indexOf(LINE_BREAK) == -1) {
            if (sb.length() > 60) {
                int pos = sb.length() / 26;
                for (int i = 1; i <= pos; i++) {
                     sb.insert((i * 26), LINE_BREAK);
                }
            } else {
                int half = sb.length() / 2;
                sb.insert(half, LINE_BREAK);
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
