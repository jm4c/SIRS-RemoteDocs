package types;

public class Document_t extends Type_t {

    private final String docID;
    private final String title;
    private final String owner;
    private String content;

    public Document_t(String docID, String title, String owner) {
        this.docID = docID;
        this.title = title;
        this.owner = owner;
        this.content = "";
    }

    public String getDocID() {
        return docID;
    }

    public String getTitle() {
        return title;
    }

    public String getOwner() {
        return owner;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    @Override
    public void print() {
        System.out.println("----Document----");
        System.out.println("Doc ID: " + docID);
        System.out.println("Title:  " + title);
        System.out.println("Owner:  " + owner);
        System.out.println("Content:\n" + content);
    }

}
