package conferenceadmin.conference.dto;

public class SubmissionResponse {

    private String status;
    private String message;
    private Long submissionId;

    public SubmissionResponse() {
    }

    public SubmissionResponse(String status, String message, Long submissionId) {
        this.status = status;
        this.message = message;
        this.submissionId = submissionId;
    }

    public static SubmissionResponse success(String message, Long submissionId) {
        return new SubmissionResponse("SUCCESS", message, submissionId);
    }

    public static SubmissionResponse error(String message) {
        return new SubmissionResponse("FAILED", message, null);
    }

    // Getters and setters
    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Long getSubmissionId() {
        return submissionId;
    }

    public void setSubmissionId(Long submissionId) {
        this.submissionId = submissionId;
    }
}
