package conferenceadmin.conference.Service;

import org.springframework.context.ApplicationEvent;

public class FileUploadEvent extends ApplicationEvent {
    private final Long submissionId;
    private final byte[] fileBytes;
    private final String originalFilename;

    public FileUploadEvent(Object source, Long submissionId, byte[] fileBytes, String originalFilename) {
        super(source);
        this.submissionId = submissionId;
        this.fileBytes = fileBytes;
        this.originalFilename = originalFilename;
    }

    public Long getSubmissionId() {
        return submissionId;
    }

    public byte[] getFileBytes() {
        return fileBytes;
    }

    public String getOriginalFilename() {
        return originalFilename;
    }
}