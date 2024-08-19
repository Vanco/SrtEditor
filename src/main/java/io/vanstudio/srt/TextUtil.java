package io.vanstudio.srt;

public class TextUtil {
    public static final String LINE_BREAK = "\n";
    public static final String CONV_START = "- ";
    public static final String SP = " ";

    public static String normalize(String sub) {
        StringBuilder sb = new StringBuilder();
        String[] split = sub.split(LINE_BREAK);
        for (String line : split) {
            if (line.trim().startsWith(CONV_START)) {
                if (!sb.isEmpty()) sb.append(LINE_BREAK);
                sb.append(line);
            } else {
                if (!sb.isEmpty()) sb.append(SP);
                sb.append(line);
            }
        }

        return sb.toString();
    }
}
