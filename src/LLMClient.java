/**
 * 任何「可以把 prompt 送給語言模型、拿回文字」的東西。
 * GeminiClient 是真的版本；關卡檢查用的是假的版本，
 * 所以沒有網路也能檢查。（這也是介面好用的地方！）
 */
public interface LLMClient {

    String generate(String prompt) throws Exception;
}
