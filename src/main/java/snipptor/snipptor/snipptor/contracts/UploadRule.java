package snipptor.snipptor.snipptor.contracts;

public class UploadRule {
    private String content;

    public UploadRule() {
    }

    public UploadRule(String content) {
        this.content = content;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }
}
