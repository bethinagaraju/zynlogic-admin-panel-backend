package conferenceadmin.conference.Service;

import conferenceadmin.conference.Entity.roboticsSponsor;
import conferenceadmin.conference.Repository.roboticsSponsorRepository;
import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.time.Instant;
import java.util.List;
import org.apache.commons.net.ftp.FTPClient;

@Service
public class roboticsSponsorService {

    private final roboticsSponsorRepository repository;

    @Value("${hostinger.ftp.host:}")
    private String ftpHost;

    @Value("${hostinger.ftp.port:21}")
    private int ftpPort;

    @Value("${hostinger.ftp.username:}")
    private String ftpUser;

    @Value("${hostinger.ftp.password:}")
    private String ftpPassword;

    @Value("${hostinger.ftp.sponsers-upload-path:/sponsersimages}")
    private String sponsersUploadPath;

    @Value("${hostinger.public-url:}")
    private String publicUrl;

    public roboticsSponsorService(roboticsSponsorRepository repository) {
        this.repository = repository;
    }

    public roboticsSponsor uploadSponsor(String name, String type, String conferencecode, MultipartFile file) throws IOException {
        if (file == null || file.isEmpty()) {
            throw new IllegalArgumentException("File is required");
        }

        String originalFilename = file.getOriginalFilename();
        String ext = "";
        if (originalFilename != null && originalFilename.contains(".")) {
            ext = originalFilename.substring(originalFilename.lastIndexOf('.'));
        }
        String filename = "sponsor_" + Instant.now().toEpochMilli() + ext;

        FTPClient ftp = new FTPClient();
        try (InputStream input = file.getInputStream()) {
            ftp.connect(ftpHost, ftpPort);
            boolean logged = ftp.login(ftpUser, ftpPassword);
            if (!logged) {
                throw new IOException("FTP login failed");
            }
            ftp.enterLocalPassiveMode();
            ftp.setFileType(FTP.BINARY_FILE_TYPE);

            // Ensure directory
            if (sponsersUploadPath != null && !sponsersUploadPath.isEmpty()) {
                ftp.changeWorkingDirectory(sponsersUploadPath);
            }

            boolean stored = ftp.storeFile(filename, input);
            if (!stored) {
                throw new IOException("Failed to store file on FTP server");
            }

            String remotePath = sponsersUploadPath;
            if (!remotePath.endsWith("/")) remotePath = remotePath + "/";
            remotePath = remotePath + filename;

            // try to build a public URL if possible
            String imagePath = remotePath;
            if (publicUrl != null && !publicUrl.isBlank()) {
                // publicUrl may point to speakers path; replace speakersimages with sponsers path if present
                if (publicUrl.contains("speakersimages")) {
                    imagePath = publicUrl.replace("speakersimages", sponsersUploadPath.replaceFirst("^/", "")) + "/" + filename;
                } else {
                    // fallback: append remote path
                    imagePath = publicUrl.replaceAll("/+$", "") + remotePath;
                }
            }

            roboticsSponsor sponsor = new roboticsSponsor(name, type, conferencecode, imagePath);
            return repository.save(sponsor);
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

    public roboticsSponsor updateSponsor(Long id, String name, String type, String conferencecode, MultipartFile file) throws IOException {
        roboticsSponsor sponsor = repository.findById(id).orElseThrow(() -> new IllegalArgumentException("Sponsor not found"));

        if (name != null && !name.isBlank()) {
            sponsor.setName(name);
        }
        if (type != null && !type.isBlank()) {
            sponsor.setType(type);
        }
        if (conferencecode != null && !conferencecode.isBlank()) {
            sponsor.setConferencecode(conferencecode);
        }

        if (file != null && !file.isEmpty()) {
            String originalFilename = file.getOriginalFilename();
            String ext = "";
            if (originalFilename != null && originalFilename.contains(".")) {
                ext = originalFilename.substring(originalFilename.lastIndexOf('.'));
            }
            String filename = "sponsor_" + Instant.now().toEpochMilli() + ext;

            FTPClient ftp = new FTPClient();
            try (InputStream input = file.getInputStream()) {
                ftp.connect(ftpHost, ftpPort);
                boolean logged = ftp.login(ftpUser, ftpPassword);
                if (!logged) {
                    throw new IOException("FTP login failed");
                }
                ftp.enterLocalPassiveMode();
                ftp.setFileType(FTP.BINARY_FILE_TYPE);

                if (sponsersUploadPath != null && !sponsersUploadPath.isEmpty()) {
                    ftp.changeWorkingDirectory(sponsersUploadPath);
                }

                boolean stored = ftp.storeFile(filename, input);
                if (!stored) {
                    throw new IOException("Failed to store file on FTP server");
                }

                String remotePath = sponsersUploadPath;
                if (!remotePath.endsWith("/")) remotePath = remotePath + "/";
                remotePath = remotePath + filename;

                String imagePath = remotePath;
                if (publicUrl != null && !publicUrl.isBlank()) {
                    if (publicUrl.contains("speakersimages")) {
                        imagePath = publicUrl.replace("speakersimages", sponsersUploadPath.replaceFirst("^/", "")) + "/" + filename;
                    } else {
                        imagePath = publicUrl.replaceAll("/+$", "") + remotePath;
                    }
                }

                sponsor.setImagePath(imagePath);
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

        return repository.save(sponsor);
    }

    public List<roboticsSponsor> getAllSponsors() {
        return repository.findAll();
    }

    public List<roboticsSponsor> getSponsorsByConferencecode(String conferencecode) {
        return repository.findByConferencecode(conferencecode);
    }

    public roboticsSponsor getSponsorById(Long id) {
        return repository.findById(id).orElseThrow(() -> new IllegalArgumentException("Sponsor not found"));
    }

    public void deleteSponsor(Long id) throws IOException {
        roboticsSponsor sponsor = repository.findById(id).orElseThrow(() -> new IllegalArgumentException("Sponsor not found"));

        String imagePath = sponsor.getImagePath();
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
            FTPClient ftp = new FTPClient();
            try {
                ftp.connect(ftpHost, ftpPort);
                boolean logged = ftp.login(ftpUser, ftpPassword);
                if (logged) {
                    ftp.enterLocalPassiveMode();
                    if (sponsersUploadPath != null && !sponsersUploadPath.isEmpty()) {
                        ftp.changeWorkingDirectory(sponsersUploadPath);
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

        repository.delete(sponsor);
    }
}
