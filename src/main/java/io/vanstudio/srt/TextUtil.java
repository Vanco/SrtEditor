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
                sb.append(line).append(LINE_BREAK);
            } else {
                sb.append(line).append(SP);
            }
        }
        // remove last LINE_BREAK or SP
        sb.deleteCharAt(sb.length() - 1);

        return sb.toString();
    }
}