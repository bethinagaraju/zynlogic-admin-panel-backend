package conferenceadmin.conference.Service;

import conferenceadmin.conference.Entity.Testimonials;
import conferenceadmin.conference.Repository.TestimonialsRepository;
import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Service
public class TestimonialsService {

    private final TestimonialsRepository repository;

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

    public TestimonialsService(TestimonialsRepository repository) {
        this.repository = repository;
    }

    public Testimonials saveTestimonial(MultipartFile image, String name, String university, String description, Integer rating, String conferencecode) throws IOException {
        String imagePath = null;

        if (image != null && !image.isEmpty()) {
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

            imagePath = publicUrl;
            if (!publicUrl.endsWith("/")) {
                imagePath += "/";
            }
            imagePath += filename;
        }

        Testimonials testimonial = new Testimonials(imagePath, name, university, description, rating, conferencecode);
        return repository.save(testimonial);
    }

    public Testimonials saveTestimonial(String imagePath, String name, String university, String description, Integer rating, String conferencecode) {
        Testimonials testimonial = new Testimonials(imagePath, name, university, description, rating, conferencecode);
        return repository.save(testimonial);
    }

    public Testimonials updateTestimonial(Long id, MultipartFile image, String name, String university, String description, Integer rating, String conferencecode) throws IOException {
        Optional<Testimonials> opt = repository.findById(id);
        if (opt.isEmpty()) {
            throw new IllegalArgumentException("Testimonial not found");
        }
        Testimonials t = opt.get();

        // Handle image upload if provided
        if (image != null && !image.isEmpty()) {
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
            t.setImagePath(imagePath);
        }

        if (name != null && !name.isBlank()) {
            t.setName(name);
        }
        if (university != null) {
            t.setUniversity(university);
        }
        if (description != null) {
            t.setDescription(description);
        }
        if (rating != null) {
            t.setRating(rating);
        }
        if (conferencecode != null && !conferencecode.isBlank()) {
            t.setConferencecode(conferencecode);
        }
        return repository.save(t);
    }

    public Testimonials updateTestimonial(Long id, String imagePath, String name, String university, String description, Integer rating, String conferencecode) {
        Optional<Testimonials> opt = repository.findById(id);
        if (opt.isEmpty()) {
            throw new IllegalArgumentException("Testimonial not found");
        }
        Testimonials t = opt.get();
        if (imagePath != null && !imagePath.isBlank()) {
            t.setImagePath(imagePath);
        }
        if (name != null && !name.isBlank()) {
            t.setName(name);
        }
        if (university != null) {
            t.setUniversity(university);
        }
        if (description != null) {
            t.setDescription(description);
        }
        if (rating != null) {
            t.setRating(rating);
        }
        if (conferencecode != null && !conferencecode.isBlank()) {
            t.setConferencecode(conferencecode);
        }
        return repository.save(t);
    }

    public Testimonials getTestimonialById(Long id) {
        return repository.findById(id).orElseThrow(() -> new IllegalArgumentException("Testimonial not found"));
    }

    public List<Testimonials> getAllTestimonials() {
        return repository.findAll();
    }

    public List<Testimonials> getTestimonialsByConferencecode(String conferencecode) {
        return repository.findByConferencecode(conferencecode);
    }

    public void deleteTestimonial(Long id) {
        if (!repository.existsById(id)) {
            throw new IllegalArgumentException("Testimonial not found");
        }
        repository.deleteById(id);
    }
}