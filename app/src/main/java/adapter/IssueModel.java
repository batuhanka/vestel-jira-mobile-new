package adapter;

public class IssueModel {

    private String issueKey;
    private String issueSummary;
    private String issueStatus;
    private String issueType;
    private String typeIconURL;

    public IssueModel(String issueKey, String issueSummary, String issueStatus, String issueType, String typeIconURL){
        this.issueKey       = issueKey;
        this.issueSummary   = issueSummary;
        this.issueStatus    = issueStatus;
        this.issueType      = issueType;
        this.typeIconURL    = typeIconURL;
    }

    public String getIssueKey() {
        return issueKey;
    }

    public String getIssueStatus() {
        return issueStatus;
    }

    public String getIssueSummary() {
        return issueSummary;
    }

    public String getIssueType() {
        return issueType;
    }

    public String getTypeIconURL() {
        return typeIconURL;
    }
}
