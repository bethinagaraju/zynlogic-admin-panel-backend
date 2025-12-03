package conferenceadmin.conference.Service;

import conferenceadmin.conference.Entity.roboticsAbstractSubmission;
import conferenceadmin.conference.Repository.roboticsAbstractSubmissionRepository;
import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.time.Instant;
import java.util.List;

@Service
public class roboticsAbstractSubmissionService {

    private final roboticsAbstractSubmissionRepository repository;

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

    public roboticsAbstractSubmissionService(roboticsAbstractSubmissionRepository repository) {
        this.repository = repository;
    }

    public roboticsAbstractSubmission submitAbstract(String conferencecode, String title, String fullName, String phoneNumber, String emailAddress, String organization, String country, MultipartFile file) throws IOException {
        if (file == null || file.isEmpty()) {
            throw new IllegalArgumentException("Abstract file is required");
        }

        String originalFilename = file.getOriginalFilename();
        String ext = "";
        if (originalFilename != null && originalFilename.contains(".")) {
            ext = originalFilename.substring(originalFilename.lastIndexOf('.'));
        }
        String filename = "abstract_" + Instant.now().toEpochMilli() + ext;

        FTPClient ftp = new FTPClient();
        try (InputStream input = file.getInputStream()) {
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

            roboticsAbstractSubmission submission = new roboticsAbstractSubmission(conferencecode, title, fullName, phoneNumber, emailAddress, organization, country, filePath);
            return repository.save(submission);
        } finally {
            if (ftp.isConnected()) {
                try {
                    ftp.logout();
                    ftp.disconnect();
                } catch (IOException ignored) {
                }
            }
        }
    }

    public List<roboticsAbstractSubmission> getAbstractSubmissionsByConferencecode(String conferencecode) {
        return repository.findByConferencecode(conferencecode);
    }
}