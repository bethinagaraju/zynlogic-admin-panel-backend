package conferenceadmin.conference.Controller;

import conferenceadmin.conference.Entity.SpeakerSpeakingSection;
import conferenceadmin.conference.Service.SpeakerSpeakingSectionService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/speaker-speaking-sections")
public class SpeakerSpeakingSectionController {

    private final SpeakerSpeakingSectionService service;

    public SpeakerSpeakingSectionController(SpeakerSpeakingSectionService service) {
        this.service = service;
    }

    // Create
    @PostMapping("/speaker/{speakerId}")
    public ResponseEntity<?> createSection(@PathVariable Long speakerId,
                                           @RequestParam("title") String title,
                                           @RequestParam("description") String description,
                                           @RequestParam("date") String date) {
        try {
            SpeakerSpeakingSection section = service.createSection(speakerId, title, description, date);
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
    public ResponseEntity<?> updateSection(@PathVariable Long id,
                                           @RequestParam(value = "title", required = false) String title,
                                           @RequestParam(value = "description", required = false) String description,
                                           @RequestParam(value = "date", required = false) String date) {
        try {
            SpeakerSpeakingSection section = service.updateSection(id, title, description, date);
            return ResponseEntity.ok(new EnhancedSpeakerSpeakingSectionDTO(section));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }

    // Delete
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteSection(@PathVariable Long id) {
        try {
            service.deleteSection(id);
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
