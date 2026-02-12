package conferenceadmin.conference.Controller;

import conferenceadmin.conference.Entity.SpeakerSpeakingSection;
import conferenceadmin.conference.Service.SpeakerSpeakingSectionService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.cache.annotation.CacheEvict;
import java.util.stream.Collectors;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import java.util.List;
import conferenceadmin.conference.Service.AuditService;
import jakarta.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/api/speaker-speaking-sections")
public class SpeakerSpeakingSectionController {

    private final SpeakerSpeakingSectionService service;
    private final AuditService auditService;

    public SpeakerSpeakingSectionController(SpeakerSpeakingSectionService service, AuditService auditService) {
        this.service = service;
        this.auditService = auditService;
    }

    // Create
    @PostMapping("/speaker/{speakerId}")
    @CacheEvict(value = "conferenceData", allEntries = true)
    public ResponseEntity<?> createSection(@PathVariable Long speakerId,
                                           @RequestParam("title") String title,
                                           @RequestParam("description") String description,
                                           @RequestParam("date") String date,
                                           @RequestParam("username") String username,
                                           HttpServletRequest request) {
        try {
            SpeakerSpeakingSection section = service.createSection(speakerId, title, description, date);
            String newValues = String.format("Speaker ID: %d, Title: %s, Description: %s, Date: %s", speakerId, title, description, date);
            auditService.logCreate(username, "Speaking Section", section.getId().toString(), 
                                 section.getTitle(), newValues, request, section.getSpeaker().getConferencecode());
            return ResponseEntity.status(HttpStatus.CREATED).body(new EnhancedSpeakerSpeakingSectionDTO(section));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }

    // Get All by Speaker ID
    @GetMapping("/speaker/{speakerId}")
    public ResponseEntity<List<EnhancedSpeakerSpeakingSectionDTO>> getSectionsBySpeakerId(@PathVariable Long speakerId) {
        List<SpeakerSpeakingSection> sections = service.getSectionsBySpeakerId(speakerId);
        List<EnhancedSpeakerSpeakingSectionDTO> dtos = sections.stream()
                .map(EnhancedSpeakerSpeakingSectionDTO::new)
                .collect(Collectors.toList());
        return ResponseEntity.ok(dtos);
    }

    // Get All by Slug
    @GetMapping("/speaker/slug/{slug}")
    public ResponseEntity<List<EnhancedSpeakerSpeakingSectionDTO>> getSectionsBySlug(@PathVariable String slug) {
        List<SpeakerSpeakingSection> sections = service.getSectionsBySlug(slug);
        List<EnhancedSpeakerSpeakingSectionDTO> dtos = sections.stream()
                .map(EnhancedSpeakerSpeakingSectionDTO::new)
                .collect(Collectors.toList());
        return ResponseEntity.ok(dtos);
    }

    // Get All Sections
    @GetMapping
    public ResponseEntity<List<EnhancedSpeakerSpeakingSectionDTO>> getAllSections() {
        List<SpeakerSpeakingSection> sections = service.getAllSections();
        List<EnhancedSpeakerSpeakingSectionDTO> dtos = sections.stream()
                .map(EnhancedSpeakerSpeakingSectionDTO::new)
                .collect(Collectors.toList());
        return ResponseEntity.ok(dtos);
    }

    // Get Section By ID
    @GetMapping("/{id}")
    public ResponseEntity<?> getSectionById(@PathVariable Long id) {
        try {
            SpeakerSpeakingSection section = service.getSectionById(id);
            return ResponseEntity.ok(new EnhancedSpeakerSpeakingSectionDTO(section));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }

    // Update
    @PutMapping("/{id}")
    @CacheEvict(value = "conferenceData", allEntries = true)
    public ResponseEntity<?> updateSection(@PathVariable Long id,
                                           @RequestParam(value = "title", required = false) String title,
                                           @RequestParam(value = "description", required = false) String description,
                                           @RequestParam(value = "date", required = false) String date,
                                           @RequestParam("username") String username,
                                           HttpServletRequest request) {
        try {
            // Get current section before update for logging
            SpeakerSpeakingSection existingSection = service.getSectionById(id);
            String oldValues = String.format("Title: %s, Description: %s, Date: %s",
                                           existingSection.getTitle(), existingSection.getDescription(), existingSection.getDate());
            
            SpeakerSpeakingSection section = service.updateSection(id, title, description, date);
            
            String newValues = String.format("Title: %s, Description: %s, Date: %s", 
                                           section.getTitle(), section.getDescription(), section.getDate());
            
            String changes = "Updated speaker speaking section fields";
            auditService.logUpdate(username, "SpeakerSpeakingSection", id.toString(), section.getTitle(),
                                 changes, oldValues, newValues, request, section.getSpeaker().getConferencecode());
            return ResponseEntity.ok(new EnhancedSpeakerSpeakingSectionDTO(section));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }

    // Delete
    @DeleteMapping("/{id}")
    @CacheEvict(value = "conferenceData", allEntries = true)
    public ResponseEntity<?> deleteSection(@PathVariable Long id, @RequestParam("username") String username, HttpServletRequest request) {
        try {
            // Get section before deleting for logging
            SpeakerSpeakingSection section = service.getSectionById(id);
            String conferenceCode = section.getSpeaker().getConferencecode();
            
            service.deleteSection(id);
            auditService.logDelete(username, "SpeakerSpeakingSection", id.toString(), section.getTitle(), request, conferenceCode);
            return ResponseEntity.ok("Deleted successfully");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }

    // DTO for response
    public static class EnhancedSpeakerSpeakingSectionDTO {
        private Long id;
        private String title;
        private String description;
        private String date;
        private String speakerName;
        private String speakerUniversity;
        private String speakerImage;
        private String speakerType;
        private String partnerLogo;
        private String linkedin;

        public EnhancedSpeakerSpeakingSectionDTO(SpeakerSpeakingSection section) {
            this.id = section.getId();
            this.title = section.getTitle();
            this.description = section.getDescription();
            this.date = section.getDate();
            if (section.getSpeaker() != null) {
                this.speakerName = section.getSpeaker().getName();
                this.speakerUniversity = section.getSpeaker().getUniversity();
                this.speakerImage = section.getSpeaker().getImagePath();
                this.speakerType = section.getSpeaker().getSpeakerType();
                this.partnerLogo = section.getSpeaker().getPartnerLogo();
                this.linkedin = section.getSpeaker().getLinkedin();
            }
        }

        public Long getId() { return id; }
        public String getTitle() { return title; }
        public String getDescription() { return description; }
        public String getDate() { return date; }
        public String getSpeakerName() { return speakerName; }
        public String getSpeakerUniversity() { return speakerUniversity; }
        public String getSpeakerImage() { return speakerImage; }
        public String getSpeakerType() { return speakerType; }
        public String getPartnerLogo() { return partnerLogo; }
        public String getLinkedin() { return linkedin; }
    }
}
