package conferenceadmin.conference.Service;

import conferenceadmin.conference.Entity.roboticsAbstractSubmission;
import conferenceadmin.conference.Repository.roboticsAbstractSubmissionRepository;
import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.time.Instant;
import java.util.List;

@Service
public class roboticsAbstractSubmissionService {

    private final roboticsAbstractSubmissionRepository repository;
    private final ApplicationEventPublisher eventPublisher;

    @Value("${hostinger.ftp.host:}")
    private String ftpHost;

    @Value("${hostinger.ftp.port:21}")
    private int ftpPort;

    @Value("${hostinger.ftp.username:}")
    private String ftpUser;

    @Value("${hostinger.ftp.password:}")
    private String ftpPassword;

    @Value("${hostinger.ftp.abstractsfiles-upload-path:/abstractsfiles}")
    private String abstractsUploadPath;

    @Value("${hostinger.public-url:}")
    private String publicUrl;

    public roboticsAbstractSubmissionService(roboticsAbstractSubmissionRepository repository, ApplicationEventPublisher eventPublisher) {
        this.repository = repository;
        this.eventPublisher = eventPublisher;
    }

    public roboticsAbstractSubmission submitAbstract(String conferencecode, String title, String fullName, String phoneNumber, String emailAddress, String organization, String country, MultipartFile file) throws IOException {
        if (file == null || file.isEmpty()) {
            throw new IllegalArgumentException("Abstract file is required");
        }

        // Validate file type
        String contentType = file.getContentType();
        if (contentType == null || (!contentType.equals("application/pdf") && !contentType.equals("application/msword") && !contentType.equals("application/vnd.openxmlformats-officedocument.wordprocessingml.document"))) {
            throw new IllegalArgumentException("Only PDF and DOC/DOCX files are allowed");
        }

        // Save submission first with temporary path
        roboticsAbstractSubmission submission = new roboticsAbstractSubmission(conferencecode, title, fullName, phoneNumber, emailAddress, organization, country, "uploading...");
        roboticsAbstractSubmission saved = repository.save(submission);

        // Read file bytes immediately
        byte[] fileBytes = file.getBytes();
        String originalFilename = file.getOriginalFilename();

        // Publish event for async file upload
        eventPublisher.publishEvent(new FileUploadEvent(this, saved.getId(), fileBytes, originalFilename));

        return saved;
    }

    @Async
    @EventListener
    public void handleFileUpload(FileUploadEvent event) {
        try {
            roboticsAbstractSubmission submission = repository.findById(event.getSubmissionId()).orElse(null);
            if (submission == null) return;

            byte[] fileBytes = event.getFileBytes();
            String originalFilename = event.getOriginalFilename();
            String ext = "";
            if (originalFilename != null && originalFilename.contains(".")) {
                ext = originalFilename.substring(originalFilename.lastIndexOf('.'));
            }
            String filename = "abstract_" + Instant.now().toEpochMilli() + ext;

            FTPClient ftp = new FTPClient();
            try (java.io.ByteArrayInputStream input = new java.io.ByteArrayInputStream(fileBytes)) {
                ftp.connect(ftpHost, ftpPort);
                boolean logged = ftp.login(ftpUser, ftpPassword);
                if (!logged) {
                    throw new IOException("FTP login failed");
                }
                ftp.enterLocalPassiveMode();
                ftp.setFileType(FTP.BINARY_FILE_TYPE);

                if (abstractsUploadPath != null && !abstractsUploadPath.isEmpty()) {
                    ftp.changeWorkingDirectory(abstractsUploadPath);
                }

                boolean stored = ftp.storeFile(filename, input);
                if (!stored) {
                    throw new IOException("Failed to store file on FTP server");
                }

                String remotePath = abstractsUploadPath;
                if (!remotePath.endsWith("/")) remotePath = remotePath + "/";
                remotePath = remotePath + filename;

                String filePath = remotePath;
                if (publicUrl != null && !publicUrl.isBlank()) {
                    if (publicUrl.contains("speakersimages")) {
                        filePath = publicUrl.replace("speakersimages", abstractsUploadPath.replaceFirst("^/", "")) + "/" + filename;
                    } else {
                        filePath = publicUrl.replaceAll("/+$", "") + remotePath;
                    }
                }

                submission.setAbstractFilePath(filePath);
                repository.save(submission);
            } finally {
                if (ftp.isConnected()) {
                    try {
                        ftp.logout();
                        ftp.disconnect();
                    } catch (IOException ignored) {
                    }
                }
            }
        } catch (Exception e) {
            // Log the error, but don't throw since it's async
            System.err.println("Error uploading file: " + e.getMessage());
        }
    }

    public List<roboticsAbstractSubmission> getAbstractSubmissionsByConferencecode(String conferencecode) {
        return repository.findByConferencecode(conferencecode);
    }

    public List<roboticsAbstractSubmission> getAllAbstractSubmissions() {
        return repository.findAll();
    }
}