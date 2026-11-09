import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * 迷你 JSON 解析器，讓專案不需要額外函式庫（已完成，不用修改）。
 * 真正的專案通常會用 Gson 或 Jackson。
 *
 * parse() 會把 JSON 文字轉成：Map（物件）、List（陣列）、String、
 * Double（數字）、Boolean 或 null。
 */
public final class MiniJson {

    private final String s;
    private int i;

    private MiniJson(String s) {
        this.s = s;
    }

    public static Object parse(String json) {
        MiniJson p = new MiniJson(json);
        p.skipWs();
        Object v = p.value();
        p.skipWs();
        if (p.i != p.s.length()) throw p.error("Unexpected text after JSON value");
        return v;
    }

    /** Wraps a Java string as a JSON string literal, escaping quotes, newlines, etc. */
    public static String quote(String text) {
        StringBuilder sb = new StringBuilder("\"");
        for (int k = 0; k < text.length(); k++) {
            char c = text.charAt(k);
            switch (c) {
                case '"':  sb.append("\\\""); break;
                case '\\': sb.append("\\\\"); break;
                case '\n': sb.append("\\n"); break;
                case '\r': sb.append("\\r"); break;
                case '\t': sb.append("\\t"); break;
                case '\b': sb.append("\\b"); break;
                case '\f': sb.append("\\f"); break;
                default:
                    if (c < 0x20) sb.append(String.format("\\u%04x", (int) c));
                    else sb.append(c);
            }
        }
        return sb.append('"').toString();
    }

    private Object value() {
        if (i >= s.length()) throw error("Unexpected end of JSON");
        char c = s.charAt(i);
        switch (c) {
            case '{': return object();
            case '[': return array();
            case '"': return string();
            case 't': expect("true"); return Boolean.TRUE;
            case 'f': expect("false"); return Boolean.FALSE;
            case 'n': expect("null"); return null;
            default:
                if (c == '-' || (c >= '0' && c <= '9')) return number();
                throw error("Unexpected character '" + c + "'");
        }
    }

    private Map<String, Object> object() {
        Map<String, Object> map = new LinkedHashMap<>();
        i++; // {
        skipWs();
        if (peek() == '}') { i++; return map; }
        while (true) {
            skipWs();
            if (peek() != '"') throw error("Expected a key");
            String key = string();
            skipWs();
            if (peek() != ':') throw error("Expected ':'");
            i++;
            skipWs();
            map.put(key, value());
            skipWs();
            char c = peek();
            i++;
            if (c == '}') return map;
            if (c != ',') throw error("Expected ',' or '}'");
        }
    }

    private List<Object> array() {
        List<Object> list = new ArrayList<>();
        i++; // [
        skipWs();
        if (peek() == ']') { i++; return list; }
        while (true) {
            skipWs();
            list.add(value());
            skipWs();
            char c = peek();
            i++;
            if (c == ']') return list;
            if (c != ',') throw error("Expected ',' or ']'");
        }
    }

    private String string() {
        StringBuilder sb = new StringBuilder();
        i++; // opening quote
        while (true) {
            if (i >= s.length()) throw error("Unterminated string");
            char c = s.charAt(i++);
            if (c == '"') return sb.toString();
            if (c != '\\') { sb.append(c); continue; }
            if (i >= s.length()) throw error("Bad escape");
            char e = s.charAt(i++);
            switch (e) {
                case '"': sb.append('"'); break;
                case '\\': sb.append('\\'); break;
                case '/': sb.append('/'); break;
                case 'b': sb.append('\b'); break;
                case 'f': sb.append('\f'); break;
                case 'n': sb.append('\n'); break;
                case 'r': sb.append('\r'); break;
                case 't': sb.append('\t'); break;
                case 'u':
                    if (i + 4 > s.length()) throw error("Bad unicode escape");
                    sb.append((char) Integer.parseInt(s.substring(i, i + 4), 16));
                    i += 4;
                    break;
                default: throw error("Bad escape \\" + e);
            }
        }
    }

    private Double number() {
        int start = i;
        while (i < s.length() && "+-0123456789.eE".indexOf(s.charAt(i)) >= 0) i++;
        return Double.valueOf(s.substring(start, i));
    }

    private void expect(String word) {
        if (!s.startsWith(word, i)) throw error("Expected " + word);
        i += word.length();
    }

    private char peek() {
        if (i >= s.length()) throw error("Unexpected end of JSON");
        return s.charAt(i);
    }

    private void skipWs() {
        while (i < s.length() && Character.isWhitespace(s.charAt(i))) i++;
    }

    private IllegalArgumentException error(String msg) {
        return new IllegalArgumentException("JSON error at position " + i + ": " + msg);
    }
}
