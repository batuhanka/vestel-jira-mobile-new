package adapter;

import java.util.List;

public class ViewIssueModel {

    private String issueKey;
    private String projectIconURL;
    private String projectName;
    private String issueSummary;
    private String issueType;
    private String typeIconURL;
    private String issueStatus;
    private String statusIcon;
    private String issuePriority;
    private String assignee;
    private String assigneeURL;
    private String reporter;
    private String reporterURL;
    private String resolution;
    private String description;
    private List<CommentModel> comments;

    public ViewIssueModel(
            String issueKey,
            String projectIconURL,
            String projectName,
            String issueSummary,
            String issueType,
            String typeIconURL,
            String issueStatus,
            String statusIcon,
            String issuePriority,
            String assignee,
            String assigneeURL,
            String reporter,
            String reporterURL,
            String resolution,
            String description,
            List<CommentModel> comments) {

        this.issueKey       = issueKey;
        this.projectIconURL = projectIconURL;
        this.projectName    = projectName;
        this.issueSummary   = issueSummary;
        this.issueType      = issueType;
        this.typeIconURL    = typeIconURL;
        this.issueStatus    = issueStatus;
        this.statusIcon     = statusIcon;
        this.issuePriority  = issuePriority;
        this.assignee       = assignee;
        this.assigneeURL    = assigneeURL;
        this.reporter       = reporter;
        this.reporterURL    = reporterURL;
        this.resolution     = resolution;
        this.description    = description;
        this.comments       = comments;


    }

    public String getIssueKey() {
        return issueKey;
    }

    public String getProjectIconURL() {
        return projectIconURL;
    }

    public String getProjectName() {
        return projectName;
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

    public String getIssueStatus() {
        return issueStatus;
    }

    public String getStatusIcon() { return statusIcon;  }

    public String getIssuePriority() {
        return issuePriority;
    }

    public String getAssignee() {
        return assignee;
    }

    public String getAssigneeURL() {
        return assigneeURL;
    }

    public String getReporter() {
        return reporter;
    }

    public String getReporterURL() {
        return reporterURL;
    }

    public String getResolution() {
        return resolution;
    }

    public String getDescription() {
        return description;
    }

    public List<CommentModel> getComments() {
        return comments;
    }
}

