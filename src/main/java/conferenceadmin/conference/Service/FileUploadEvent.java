package conferenceadmin.conference.Service;

import org.springframework.context.ApplicationEvent;

public class FileUploadEvent extends ApplicationEvent {
    private final Long submissionId;
    private final org.springframework.web.multipart.MultipartFile file;

    public FileUploadEvent(Object source, Long submissionId, org.springframework.web.multipart.MultipartFile file) {
        super(source);
        this.submissionId = submissionId;
        this.file = file;
    }

    public Long getSubmissionId() {
        return submissionId;
    }

    public org.springframework.web.multipart.MultipartFile getFile() {
        return file;
    }
}