package conferenceadmin.conference.Service;

import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import conferenceadmin.conference.Repository.SpeakerRepository;
import conferenceadmin.conference.Entity.Speaker;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Optional;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Objects;

@Service
public class SpeakerService {

    private final SpeakerRepository speakerRepository;

    @Value("${hostinger.ftp.host}")
    private String ftpHost;

    @Value("${hostinger.ftp.port}")
    private int ftpPort;

    @Value("${hostinger.ftp.username}")
    private String ftpUser;

    @Value("${hostinger.ftp.password}")
    private String ftpPass;

    @Value("${hostinger.ftp.upload-path}")
    private String ftpUploadPath;

    @Value("${hostinger.public-url}")
    private String publicUrl;

    public SpeakerService(SpeakerRepository speakerRepository) {
        this.speakerRepository = speakerRepository;
    }

    public Speaker saveSpeaker(MultipartFile image, String name, String university) throws IOException {
        String original = Objects.requireNonNull(image.getOriginalFilename());
        String filename = System.currentTimeMillis() + "_" + original.replaceAll("[^a-zA-Z0-9._-]", "_");

        // upload via FTP
        FTPClient ftpClient = new FTPClient();
        try (InputStream input = image.getInputStream()) {
            ftpClient.connect(ftpHost, ftpPort);
            if (!ftpClient.login(ftpUser, ftpPass)) {
                throw new IOException("FTP login failed");
            }
            ftpClient.enterLocalPassiveMode();
            ftpClient.setFileType(FTP.BINARY_FILE_TYPE);

            String remotePath = ftpUploadPath;
            if (!remotePath.endsWith("/")) {
                remotePath = remotePath + "/";
            }
            String remoteFile = remotePath + filename;

            boolean stored = ftpClient.storeFile(remoteFile, input);
            ftpClient.logout();
            ftpClient.disconnect();

            if (!stored) {
                throw new IOException("Failed to store file on FTP server");
            }
        } catch (IOException ex) {
            // ensure disconnect
            if (ftpClient.isConnected()) {
                try {
                    ftpClient.logout();
                    ftpClient.disconnect();
                } catch (IOException ignore) {
                }
            }
            throw ex;
        }

        String imagePath = publicUrl;
        if (!publicUrl.endsWith("/")) {
            imagePath += "/";
        }
        imagePath += filename;

        Speaker speaker = new Speaker(name, university, imagePath);
        return speakerRepository.save(speaker);
    }

    public List<Speaker> getAllSpeakers() {
        return speakerRepository.findAll();
    }

    public Speaker getSpeakerById(Long id) {
        return speakerRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("Speaker not found"));
    }

    public Speaker updateSpeaker(Long id, MultipartFile image, String imageUrl, String name, String university) throws IOException {
        Optional<Speaker> opt = speakerRepository.findById(id);
        if (opt.isEmpty()) {
            throw new IOException("Speaker not found with id: " + id);
        }
        Speaker speaker = opt.get();

        // update simple fields
        if (name != null && !name.isBlank()) {
            speaker.setName(name);
        }
        if (university != null && !university.isBlank()) {
            speaker.setUniversity(university);
        }

        // if new image file provided, upload and replace imagePath
        if (image != null && !image.isEmpty()) {
            String original = Objects.requireNonNull(image.getOriginalFilename());
            String filename = System.currentTimeMillis() + "_" + original.replaceAll("[^a-zA-Z0-9._-]", "_");

            FTPClient ftpClient = new FTPClient();
            try (InputStream input = image.getInputStream()) {
                ftpClient.connect(ftpHost, ftpPort);
                if (!ftpClient.login(ftpUser, ftpPass)) {
                    throw new IOException("FTP login failed");
                }
                ftpClient.enterLocalPassiveMode();
                ftpClient.setFileType(FTP.BINARY_FILE_TYPE);

                String remotePath = ftpUploadPath;
                if (!remotePath.endsWith("/")) {
                    remotePath = remotePath + "/";
                }
                String remoteFile = remotePath + filename;

                boolean stored = ftpClient.storeFile(remoteFile, input);
                ftpClient.logout();
                ftpClient.disconnect();

                if (!stored) {
                    throw new IOException("Failed to store file on FTP server");
                }
            } catch (IOException ex) {
                if (ftpClient.isConnected()) {
                    try {
                        ftpClient.logout();
                        ftpClient.disconnect();
                    } catch (IOException ignore) {
                    }
                }
                throw ex;
            }

            String imagePath = publicUrl;
            if (!publicUrl.endsWith("/")) {
                imagePath += "/";
            }
            imagePath += filename;

            speaker.setImagePath(imagePath);
        } else if (imageUrl != null && !imageUrl.isBlank()) {
            // if imageUrl supplied (and no file), use it directly
            speaker.setImagePath(imageUrl);
        }

        return speakerRepository.save(speaker);
    }

    public boolean deleteSpeaker(Long id) {
        Optional<Speaker> opt = speakerRepository.findById(id);
        if (opt.isEmpty()) {
            return false;
        }
        Speaker speaker = opt.get();

        String imagePath = speaker.getImagePath();
        if (imagePath != null && !imagePath.isBlank()) {
            try {
                String filename = imagePath.substring(imagePath.lastIndexOf('/') + 1);
                FTPClient ftpClient = new FTPClient();
                try {
                    ftpClient.connect(ftpHost, ftpPort);
                    if (ftpClient.login(ftpUser, ftpPass)) {
                        ftpClient.enterLocalPassiveMode();
                        String remotePath = ftpUploadPath;
                        if (!remotePath.endsWith("/")) {
                            remotePath = remotePath + "/";
                        }
                        String remoteFile = remotePath + filename;
                        ftpClient.deleteFile(remoteFile);
                        ftpClient.logout();
                    }
                } finally {
                    if (ftpClient.isConnected()) {
                        try {
                            ftpClient.disconnect();
                        } catch (IOException ignore) {
                        }
                    }
                }
            } catch (Exception ignore) {
                // ignore errors deleting remote file — we still delete DB record
            }
        }

        speakerRepository.delete(speaker);
        return true;
    }
}
