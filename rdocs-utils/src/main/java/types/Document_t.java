package types;

public class Document_t extends Type_t {

    private String docID;
    private String title;
    private String content;
    private String owner;

    public Document_t(String docID, String title, String owner) {
        this.docID = docID;
        this.title = title;
        this.owner = owner;
        this.content = "";
    }


    @Override
    public void print() {
        System.out.println("----Doc Info-----");
        System.out.println("Doc ID: " + docID);
        System.out.println("Title:  " + title);
        System.out.println("Owner:  " + owner);
        System.out.println("Content:\n" + content);
    }

    @Override
    public Object getValue() {
        return null;
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
}
