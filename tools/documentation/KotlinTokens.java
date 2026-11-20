// Usa il lexer del compilatore Kotlin già scaricato da Gradle, senza compilare l'app.
import java.nio.file.*;
import java.util.*;
import java.security.MessageDigest;
import java.nio.charset.StandardCharsets;
import org.jetbrains.kotlin.lexer.KotlinLexer;

class KotlinTokens {
    public static void main(String[] paths) throws Exception {
        for (String path : paths) {
            String text = Files.readString(Path.of(path));
            KotlinLexer lexer = new KotlinLexer();
            lexer.start(text);
            List<Integer> lines = new ArrayList<>();
            lines.add(0);
            for (int i = 0; i < text.length(); i++) if (text.charAt(i) == '\n') lines.add(i + 1);
            Set<Integer> protectedLines = new TreeSet<>();
            Map<Integer, Integer> closes = new TreeMap<>();
            Deque<Integer> stack = new ArrayDeque<>();
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            while (lexer.getTokenType() != null) {
                String kind = lexer.getTokenType().toString();
                int start = lexer.getTokenStart(), end = lexer.getTokenEnd();
                if (!kind.contains("COMMENT") && !kind.equals("WHITE_SPACE")) {
                    digest.update((kind + "\u0000" + text.substring(start, end) + "\u0000").getBytes(StandardCharsets.UTF_8));
                }
                int line = Collections.binarySearch(lines, start);
                line = line < 0 ? -line - 1 : line + 1;
                if (kind.equals("LBRACE")) stack.push(line);
                if (kind.equals("RBRACE") && !stack.isEmpty()) closes.put(line, stack.pop());
                if (kind.contains("STRING") || kind.contains("COMMENT")) {
                    for (int n = line + 1; n <= lines.size() && lines.get(n - 1) < end; n++) protectedLines.add(n);
                }
                lexer.advance();
            }
            StringJoiner safe = new StringJoiner(",");
            for (int n : protectedLines) safe.add(String.valueOf(n));
            StringJoiner pairs = new StringJoiner(",");
            for (var item : closes.entrySet()) pairs.add("\"" + item.getKey() + "\":" + item.getValue());
            String escaped = path.replace("\\", "\\\\").replace("\"", "\\\"");
            System.out.println("{\"path\":\"" + escaped + "\",\"tokenSha256\":\"" + HexFormat.of().formatHex(digest.digest()) + "\",\"protectedLines\":[" + safe + "],\"closes\":{" + pairs + "}}");
        }
    }
}
