package adapter;

/**
 * Created by Batuhan on 21.04.2016.
 */
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

    public void setIssueKey(String issueKey) {
        this.issueKey = issueKey;
    }

    public String getIssueStatus() {
        return issueStatus;
    }

    public void setIssueStatus(String issueStatus) {
        this.issueStatus = issueStatus;
    }

    public String getIssueSummary() {
        return issueSummary;
    }

    public void setIssueSummary(String issueSummary) {
        this.issueSummary = issueSummary;
    }

    public String getIssueType() {
        return issueType;
    }

    public void setIssueType(String issueType) {
        this.issueType = issueType;
    }

    public String getTypeIconURL() {
        return typeIconURL;
    }

    public void setTypeIconURL(String typeIconURL) {
        this.typeIconURL = typeIconURL;
    }
}
