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
        StringBuilder sb = new StringBuilder(
                removeEvenOccurrences(sub)
        );
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

    /**
     * 删除输入字符串中出现的第偶数个‘♪’字符
     *
     * @param input 输入的字符串
     * @return 处理后的字符串
     */
    private static String removeEvenOccurrences(String input) {
        StringBuilder result = new StringBuilder();
        int count = 0;
        for (char c : input.toCharArray()) {
            if (c == '♪') {
                count++;
                if (count % 2 != 0) {
                    result.append(c);
                }
            } else {
                result.append(c);
            }
        }
        return result.toString();
    }
}
