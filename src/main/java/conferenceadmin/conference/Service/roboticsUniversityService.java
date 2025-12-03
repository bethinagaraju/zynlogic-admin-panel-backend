package conferenceadmin.conference.Service;

import conferenceadmin.conference.Entity.roboticsUniversity;
import conferenceadmin.conference.Repository.roboticsUniversityRepository;
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
public class roboticsUniversityService {

    private final roboticsUniversityRepository repository;

    @Value("${hostinger.ftp.host:}")
    private String ftpHost;

    @Value("${hostinger.ftp.port:21}")
    private int ftpPort;

    @Value("${hostinger.ftp.username:}")
    private String ftpUser;

    @Value("${hostinger.ftp.password:}")
    private String ftpPassword;

    @Value("${hostinger.ftp.universitiesimages-upload-path:/universitiesimages}")
    private String universitiesUploadPath;

    @Value("${hostinger.public-url:}")
    private String publicUrl;

    public roboticsUniversityService(roboticsUniversityRepository repository) {
        this.repository = repository;
    }

    public roboticsUniversity saveUniversity(MultipartFile image, String name, String conferencecode) throws IOException {
        if (image == null || image.isEmpty()) {
            throw new IllegalArgumentException("Image file is required");
        }

        String originalFilename = image.getOriginalFilename();
        String ext = "";
        if (originalFilename != null && originalFilename.contains(".")) {
            ext = originalFilename.substring(originalFilename.lastIndexOf('.'));
        }
        String filename = "university_" + Instant.now().toEpochMilli() + ext;

        FTPClient ftp = new FTPClient();
        try (InputStream input = image.getInputStream()) {
            ftp.connect(ftpHost, ftpPort);
            boolean logged = ftp.login(ftpUser, ftpPassword);
            if (!logged) {
                throw new IOException("FTP login failed");
            }
            ftp.enterLocalPassiveMode();
            ftp.setFileType(FTP.BINARY_FILE_TYPE);

            if (universitiesUploadPath != null && !universitiesUploadPath.isEmpty()) {
                ftp.changeWorkingDirectory(universitiesUploadPath);
            }

            boolean stored = ftp.storeFile(filename, input);
            if (!stored) {
                throw new IOException("Failed to store file on FTP server");
            }

            String remotePath = universitiesUploadPath;
            if (!remotePath.endsWith("/")) remotePath = remotePath + "/";
            remotePath = remotePath + filename;

            String imagePath = remotePath;
            if (publicUrl != null && !publicUrl.isBlank()) {
                if (publicUrl.contains("speakersimages")) {
                    imagePath = publicUrl.replace("speakersimages", universitiesUploadPath.replaceFirst("^/", "")) + "/" + filename;
                } else {
                    imagePath = publicUrl.replaceAll("/+$", "") + remotePath;
                }
            }

            roboticsUniversity uni = new roboticsUniversity(name, conferencecode, imagePath);
            return repository.save(uni);
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

    public List<roboticsUniversity> getAllUniversities() {
        return repository.findAll();
    }

    public List<roboticsUniversity> getUniversitiesByConferencecode(String conferencecode) {
        return repository.findByConferencecode(conferencecode);
    }

    public roboticsUniversity updateUniversity(Long id, String name, String conferencecode, org.springframework.web.multipart.MultipartFile image) throws IOException {
        roboticsUniversity uni = repository.findById(id).orElseThrow(() -> new IllegalArgumentException("University not found"));

        if (name != null && !name.isBlank()) {
            uni.setName(name);
        }

        if (conferencecode != null && !conferencecode.isBlank()) {
            uni.setConferencecode(conferencecode);
        }

        if (image != null && !image.isEmpty()) {
            String originalFilename = image.getOriginalFilename();
            String ext = "";
            if (originalFilename != null && originalFilename.contains(".")) {
                ext = originalFilename.substring(originalFilename.lastIndexOf('.'));
            }
            String filename = "university_" + Instant.now().toEpochMilli() + ext;

            FTPClient ftp = new FTPClient();
            try (InputStream input = image.getInputStream()) {
                ftp.connect(ftpHost, ftpPort);
                boolean logged = ftp.login(ftpUser, ftpPassword);
                if (!logged) {
                    throw new IOException("FTP login failed");
                }
                ftp.enterLocalPassiveMode();
                ftp.setFileType(FTP.BINARY_FILE_TYPE);

                if (universitiesUploadPath != null && !universitiesUploadPath.isEmpty()) {
                    ftp.changeWorkingDirectory(universitiesUploadPath);
                }

                boolean stored = ftp.storeFile(filename, input);
                if (!stored) {
                    throw new IOException("Failed to store file on FTP server");
                }

                // build remote path and public imagePath similar to save
                String remotePath = universitiesUploadPath;
                if (!remotePath.endsWith("/")) remotePath = remotePath + "/";
                remotePath = remotePath + filename;

                String imagePath = remotePath;
                if (publicUrl != null && !publicUrl.isBlank()) {
                    if (publicUrl.contains("speakersimages")) {
                        imagePath = publicUrl.replace("speakersimages", universitiesUploadPath.replaceFirst("^/", "")) + "/" + filename;
                    } else {
                        imagePath = publicUrl.replaceAll("/+$", "") + remotePath;
                    }
                }

                // attempt to delete old file (best-effort)
                String oldImage = uni.getImagePath();
                if (oldImage != null && !oldImage.isBlank()) {
                    try {
                        String oldFilename = oldImage.substring(oldImage.lastIndexOf('/') + 1);
                        if (oldFilename != null && !oldFilename.isBlank()) {
                            try {
                                ftp.deleteFile(oldFilename);
                            } catch (Exception ignored) {
                            }
                        }
                    } catch (Exception ignored) {
                    }
                }

                uni.setImagePath(imagePath);
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

        return repository.save(uni);
    }

    public roboticsUniversity getUniversityById(Long id) {
        return repository.findById(id).orElseThrow(() -> new IllegalArgumentException("University not found"));
    }

    public void deleteUniversity(Long id) throws IOException {
        roboticsUniversity uni = repository.findById(id).orElseThrow(() -> new IllegalArgumentException("University not found"));

        String imagePath = uni.getImagePath();
        String filename = null;
        if (imagePath != null && !imagePath.isBlank()) {
            int idx = imagePath.lastIndexOf('/');
            if (idx >= 0 && idx < imagePath.length() - 1) {
                filename = imagePath.substring(idx + 1);
            } else {
                filename = imagePath;
            }
        }

        if (filename != null && !filename.isBlank()) {
            org.apache.commons.net.ftp.FTPClient ftp = new org.apache.commons.net.ftp.FTPClient();
            try {
                ftp.connect(ftpHost, ftpPort);
                boolean logged = ftp.login(ftpUser, ftpPassword);
                if (logged) {
                    ftp.enterLocalPassiveMode();
                    if (universitiesUploadPath != null && !universitiesUploadPath.isEmpty()) {
                        ftp.changeWorkingDirectory(universitiesUploadPath);
                    }
                    try {
                        ftp.deleteFile(filename);
                    } catch (IOException ignored) {
                    }
                    try {
                        ftp.logout();
                    } catch (IOException ignored) {
                    }
                }
            } finally {
                if (ftp.isConnected()) {
                    try {
                        ftp.disconnect();
                    } catch (IOException ignored) {
                    }
                }
            }
        }

        repository.delete(uni);
    }
}
