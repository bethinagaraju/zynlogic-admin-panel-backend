package conferenceadmin.conference.Service;

import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import conferenceadmin.conference.Service.SpeakerSpeakingSectionService;
import java.util.HashMap;
import java.util.Map;
import conferenceadmin.conference.Repository.SpeakerRepository;
import conferenceadmin.conference.Entity.Speaker;
import java.io.IOException;
import java.util.List;
import java.util.Objects;
import java.io.InputStream;
import java.util.Optional;
import conferenceadmin.conference.Entity.SpeakerSpeakingSection;

@Service
public class SpeakerService {

    private final SpeakerRepository speakerRepository;
    private final SpeakerSpeakingSectionService speakingSectionService;

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

    public SpeakerService(SpeakerRepository speakerRepository, SpeakerSpeakingSectionService speakingSectionService) {
        this.speakerRepository = speakerRepository;
        this.speakingSectionService = speakingSectionService;
    }

    public Speaker saveSpeaker(MultipartFile image, String name, String university, String conferencecode, String speakerType, boolean visible,
                               String slug, String linkedin, MultipartFile partnerLogo) throws IOException {
        String original = Objects.requireNonNull(image.getOriginalFilename());
        String filename = System.currentTimeMillis() + "_" + original.replaceAll("[^a-zA-Z0-9._-]", "_");

        // upload via FTP
        FTPClient ftpClient = new FTPClient();
        try {
            ftpClient.connect(ftpHost, ftpPort);
            if (!ftpClient.login(ftpUser, ftpPass)) {
                throw new IOException("FTP login failed");
            }
            ftpClient.enterLocalPassiveMode();
            ftpClient.setFileType(FTP.BINARY_FILE_TYPE);

            // Upload speaker image
            try (InputStream input = image.getInputStream()) {
                String remotePath = ftpUploadPath;
                if (!remotePath.endsWith("/")) {
                    remotePath = remotePath + "/";
                }
                String remoteFile = remotePath + filename;

                boolean stored = ftpClient.storeFile(remoteFile, input);
                if (!stored) {
                    throw new IOException("Failed to store file on FTP server");
                }
            }

            // Upload partner logo if present
            String logoPath = null;
            if (partnerLogo != null && !partnerLogo.isEmpty()) {
                String logoOriginal = Objects.requireNonNull(partnerLogo.getOriginalFilename());
                String logoFilename = "logo_" + System.currentTimeMillis() + "_" + logoOriginal.replaceAll("[^a-zA-Z0-9._-]", "_");
                
                try (InputStream logoInput = partnerLogo.getInputStream()) {
                    String remotePath = ftpUploadPath;
                    if (!remotePath.endsWith("/")) {
                        remotePath = remotePath + "/";
                    }
                    String remoteFile = remotePath + logoFilename;
                    
                    boolean stored = ftpClient.storeFile(remoteFile, logoInput);
                    if (stored) {
                        String publicLogoUrl = publicUrl;
                        if (!publicLogoUrl.endsWith("/")) {
                            publicLogoUrl += "/";
                        }
                        logoPath = publicLogoUrl + logoFilename;
                    }
                }
            }

            ftpClient.logout();
            ftpClient.disconnect();
            
            String imagePath = publicUrl;
            if (!publicUrl.endsWith("/")) {
                imagePath += "/";
            }
            imagePath += filename;

            Speaker speaker = new Speaker(name, university, conferencecode, imagePath, speakerType, getNextOrderIndex(conferencecode));
            speaker.setVisible(visible);
            if (slug != null && !slug.isBlank()) speaker.setSlug(slug);
            if (linkedin != null && !linkedin.isBlank()) speaker.setLinkedin(linkedin);
            if (logoPath != null) speaker.setPartnerLogo(logoPath);
            
            return speakerRepository.save(speaker);

        } catch (IOException ex) {
            if (ftpClient.isConnected()) {
                try {
                    ftpClient.logout();
                    ftpClient.disconnect();
                } catch (IOException ignore) {}
            }
            throw ex;
        }
    }

    public List<Speaker> getAllSpeakers() {
        return speakerRepository.findAll();
    }

    public Speaker getSpeakerById(Long id) {
        return speakerRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("Speaker not found"));
    }

    public Speaker updateSpeaker(Long id, MultipartFile image, String imageUrl, String name, String university, 
                                  String conferencecode, String speakerType, Boolean visible, 
                                  String slug, String linkedin, MultipartFile partnerLogo) throws IOException {
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
        if (conferencecode != null && !conferencecode.isBlank()) {
            speaker.setConferencecode(conferencecode);
        }
        if (speakerType != null && !speakerType.isBlank()) {
            speaker.setSpeakerType(speakerType);
        }

        if (visible != null) {
            speaker.setVisible(visible);
        }

        if (slug != null && !slug.isBlank()) {
            speaker.setSlug(slug);
        }

        if (linkedin != null && !linkedin.isBlank()) {
            speaker.setLinkedin(linkedin);
        }

        // Handle partner logo file upload
        if (partnerLogo != null && !partnerLogo.isEmpty()) {
            String original = Objects.requireNonNull(partnerLogo.getOriginalFilename());
            String filename = "logo_" + System.currentTimeMillis() + "_" + original.replaceAll("[^a-zA-Z0-9._-]", "_");

            FTPClient ftpClient = new FTPClient();
            try (InputStream input = partnerLogo.getInputStream()) {
                ftpClient.connect(ftpHost, ftpPort);
                if (!ftpClient.login(ftpUser, ftpPass)) {
                    throw new IOException("FTP login failed for partner logo");
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
                    throw new IOException("Failed to store partner logo on FTP server");
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

            String logoPath = publicUrl;
            if (!publicUrl.endsWith("/")) {
                logoPath += "/";
            }
            logoPath += filename;

            speaker.setPartnerLogo(logoPath);
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

    public List<Speaker> getSpeakersByConferencecode(String conferencecode) {
        return speakerRepository.findByConferencecodeOrderByOrderIndex(conferencecode);
    }

    private Integer getNextOrderIndex(String conferencecode) {
        List<Speaker> existing = speakerRepository.findByConferencecodeOrderByOrderIndex(conferencecode);
        return existing.isEmpty() ? 1 : existing.get(existing.size() - 1).getOrderIndex() + 1;
    }

    public void reorderSpeakers(List<Long> speakerIdsInOrder) {
        for (int i = 0; i < speakerIdsInOrder.size(); i++) {
            Long id = speakerIdsInOrder.get(i);
            Optional<Speaker> opt = speakerRepository.findById(id);
            if (opt.isPresent()) {
                Speaker s = opt.get();
                s.setOrderIndex(i + 1);
                speakerRepository.save(s);
            }
        }
    }

    public Speaker getSpeakerBySlug(String slug) {
        return speakerRepository.findBySlug(slug)
                .orElseThrow(() -> new IllegalArgumentException("Speaker not found with slug: " + slug));
    }

    public Map<String, Object> getSpeakerFullDetails(String slug) {
        Speaker speaker = getSpeakerBySlug(slug);
        List<SpeakerSpeakingSection> speakingSections = speakingSectionService.getSectionsBySlug(slug);
        
        Map<String, Object> result = new HashMap<>();
        result.put("speaker", speaker);
        result.put("speakingSections", speakingSections);
        return result;
    }

    public Speaker getSpeakerByName(String name) {
        return speakerRepository.findByName(name)
                .orElseThrow(() -> new IllegalArgumentException("Speaker not found with name: " + name));
    }
}
