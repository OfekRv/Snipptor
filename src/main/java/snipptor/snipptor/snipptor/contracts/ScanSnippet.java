package snipptor.snipptor.snipptor.contracts;

public class ScanSnippet {
    private String content;

    public ScanSnippet() {
    }

    public ScanSnippet(String content) {
        this.content = content;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }
}
