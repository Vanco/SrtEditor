package io.vanstudio.srt;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class LLMTranslator implements Translator {

    private HttpClient httpClient;
    private String baseUrl;
    private String modelName;
    private String apiKey;
    private static final String LINE_PLACEHOLDER = "[BR]";
    private static final Pattern NUMBERED_LINE_PATTERN = Pattern.compile("^\\d+[.:\\s]+(.+)$", Pattern.MULTILINE);

    private static LLMTranslator _instance;

    public static LLMTranslator getInstance() {
        if (_instance == null) {
            _instance = new LLMTranslator();
        }
        return _instance;
    }

    public LLMTranslator() {
    }

    @Override
    public void connect() throws Exception {
        baseUrl = Config.getLLMBaseUrl();
        modelName = Config.getLLMModelName();
        apiKey = Config.getLLMApiKey();

        System.out.println("[LLMTranslator] Connecting to: " + baseUrl + " model: " + modelName);

        httpClient = HttpClient.newBuilder()
                .connectTimeout(Duration.ofSeconds(30))
                .build();

        System.out.println("[LLMTranslator] HTTP client created successfully");
    }

    @Override
    public String translateText(String text, String sourceLang, String targetLang) throws Exception {
        if (text == null || text.trim().isEmpty()) {
            return text;
        }

        String systemPrompt = buildSystemPrompt(sourceLang, targetLang);
        String encoded = encodeNewlines(text);
        String userMessage = "1: " + encoded;

        String responseText = callChatCompletion(systemPrompt, userMessage);
        List<String> parsed = parseTranslation(responseText, 1);
        if (!parsed.isEmpty()) {
            return decodeNewlines(parsed.getFirst());
        }
        return text;
    }

    @Override
    public List<String> translateText(List<String> textList, String sourceLang, String targetLang) throws Exception {
        if (textList == null || textList.isEmpty()) {
            return textList;
        }

        return translateBatch(textList, sourceLang, targetLang, 1);
    }

    private List<String> translateBatch(List<String> batch, String sourceLang, String targetLang, int startIndex) throws Exception {
        String systemPrompt = buildSystemPrompt(sourceLang, targetLang);

        System.out.println("[LLMTranslator] Translating batch: " + batch.size() + " lines, start=" + startIndex + ", target=" + targetLang);

        StringBuilder userContent = new StringBuilder();
        for (int j = 0; j < batch.size(); j++) {
            String encoded = encodeNewlines(batch.get(j));
            userContent.append(startIndex + j).append(": ").append(encoded);
            if (j < batch.size() - 1) {
                userContent.append("\n");
            }
        }

        String responseText = callChatCompletion(systemPrompt, userContent.toString());
        List<String> parsed = parseTranslation(responseText, batch.size());
        System.out.println("[LLMTranslator] Parsed " + parsed.size() + " lines (expected " + batch.size() + ")");

        // Decode [BR] back to \n, fill missing with originals
        List<String> result = new ArrayList<>(batch);
        for (int i = 0; i < Math.min(parsed.size(), batch.size()); i++) {
            result.set(i, decodeNewlines(parsed.get(i)));
        }
        return result;
    }

    private String callChatCompletion(String systemPrompt, String userMessage) throws Exception {
        String jsonBody = buildRequestBody(systemPrompt, userMessage);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(baseUrl + "/chat/completions"))
                .header("Content-Type", "application/json")
                .header("Authorization", "Bearer " + apiKey)
                .POST(HttpRequest.BodyPublishers.ofString(jsonBody))
                .timeout(Duration.ofSeconds(120))
                .build();

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

        if (response.statusCode() != 200) {
            throw new RuntimeException("API request failed with status " + response.statusCode() + ": " + response.body());
        }

        return extractContent(response.body());
    }

    private String buildRequestBody(String systemPrompt, String userMessage) {
        StringBuilder sb = new StringBuilder();
        sb.append("{\"model\":\"").append(escapeJson(modelName)).append("\",");
        sb.append("\"temperature\":0.3,");
        sb.append("\"messages\":[");
        sb.append("{\"role\":\"system\",\"content\":\"").append(escapeJson(systemPrompt)).append("\"},");
        sb.append("{\"role\":\"user\",\"content\":\"").append(escapeJson(userMessage)).append("\"}");
        sb.append("]}");
        return sb.toString();
    }

    private String extractContent(String responseBody) {
        // Extract "content" field from the first choice's message
        int contentIdx = responseBody.indexOf("\"content\"");
        if (contentIdx == -1) {
            throw new RuntimeException("No content field in response: " + responseBody);
        }

        int colonIdx = responseBody.indexOf(':', contentIdx);
        int quoteStart = responseBody.indexOf('"', colonIdx + 1);
        if (quoteStart == -1) {
            throw new RuntimeException("Malformed content field in response: " + responseBody);
        }

        int quoteEnd = findMatchingQuoteEnd(responseBody, quoteStart + 1);
        if (quoteEnd == -1) {
            throw new RuntimeException("Unterminated content string in response: " + responseBody);
        }

        String content = responseBody.substring(quoteStart + 1, quoteEnd);
        return unescapeJson(content);
    }

    private int findMatchingQuoteEnd(String s, int start) {
        for (int i = start; i < s.length(); i++) {
            char c = s.charAt(i);
            if (c == '\\') {
                i++; // skip escaped character
            } else if (c == '"') {
                return i;
            }
        }
        return -1;
    }

    private String escapeJson(String s) {
        return s.replace("\\", "\\\\")
                .replace("\"", "\\\"")
                .replace("\n", "\\n")
                .replace("\r", "\\r")
                .replace("\t", "\\t");
    }

    private String unescapeJson(String s) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < s.length(); i++) {
            char c = s.charAt(i);
            if (c == '\\' && i + 1 < s.length()) {
                char next = s.charAt(i + 1);
                switch (next) {
                    case '"': sb.append('"'); i++; break;
                    case '\\': sb.append('\\'); i++; break;
                    case 'n': sb.append('\n'); i++; break;
                    case 'r': sb.append('\r'); i++; break;
                    case 't': sb.append('\t'); i++; break;
                    default: sb.append(c); break;
                }
            } else {
                sb.append(c);
            }
        }
        return sb.toString();
    }

    private String buildSystemPrompt(String sourceLang, String targetLang) {
        String sourceName = getLanguageName(sourceLang);
        String targetName = getLanguageName(targetLang);

        return "You are a professional subtitle translator. "
                + "Translate subtitle lines from " + sourceName + " to " + targetName + ".\n"
                + "Rules:\n"
                + "- Each subtitle entry is prefixed with a number followed by a colon and a space (e.g., '1: text').\n"
                + "- Some entries contain [BR] which represents a line break within that entry. Preserve [BR] in your translation.\n"
                + "- Translate only the text part, keep the line numbers and [BR] markers.\n"
                + "- Output ONLY the numbered translated lines, no explanations.\n"
                + "- Preserve the meaning and tone of the original.\n"
                + "- Keep translations concise and natural for subtitles.\n"
                + "- Do NOT add or remove any lines.";
    }

    private List<String> parseTranslation(String response, int expectedCount) {
        List<String> result = new ArrayList<>();
        if (response == null || response.isBlank()) {
            return result;
        }

        String[] lines = response.split("\\n");
        for (String line : lines) {
            String trimmed = line.trim();
            if (trimmed.isEmpty()) continue;

            Matcher matcher = NUMBERED_LINE_PATTERN.matcher(trimmed);
            if (matcher.matches()) {
                result.add(matcher.group(1).trim());
            }
        }

        return result;
    }

    private String encodeNewlines(String text) {
        return text.replace("\n", LINE_PLACEHOLDER);
    }

    private String decodeNewlines(String text) {
        return text.replace(LINE_PLACEHOLDER, "\n");
    }

    private String getLanguageName(String code) {
        if (code == null || "auto".equals(code)) return "any language";
        return code;
    }

    @Override
    public boolean isMultiTranslateSupported() {
        return true;
    }

    @Override
    public void close() throws IOException {
        httpClient = null;
    }
}
