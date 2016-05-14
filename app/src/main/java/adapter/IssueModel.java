package adapter;

public class IssueModel {

    private String issueKey;
    private String issueSummary;
    private String issueStatus;
    private String issueType;
    private String typeIconURL;
    private String createdDate;

    public IssueModel(String issueKey, String issueSummary, String issueStatus, String issueType, String typeIconURL, String createdDate){
        this.issueKey       = issueKey;
        this.issueSummary   = issueSummary;
        this.issueStatus    = issueStatus;
        this.issueType      = issueType;
        this.typeIconURL    = typeIconURL;
        this.createdDate    = createdDate;
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

    public String getCreatedDate() {
        return createdDate;
    }
}
