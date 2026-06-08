package summarizer;

public class SummarizerException extends Exception {
    public SummarizerException(String message) {
        super(message);
    }

    public SummarizerException(String message, Throwable cause) {
        super(message, cause);
    }
}
