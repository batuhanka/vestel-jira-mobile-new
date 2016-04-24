package adapter;

public class CommentModel {

    private String author;
    private String authorURL;
    private String body;
    private String created;

    public CommentModel(String author, String authorURL, String body, String created){
        this.author     = author;
        this.authorURL  = authorURL;
        this.body       = body;
        this.created    = created;
    }

    public String getAuthor() {
        return author;
    }

    public String getAuthorURL() {
        return authorURL;
    }

    public String getBody() {
        return body;
    }

    public String getCreated() {
        return created;
    }
}
