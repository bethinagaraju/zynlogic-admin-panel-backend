package conferenceadmin.conference.Controller;

import conferenceadmin.conference.Entity.SpeakerSection;
import conferenceadmin.conference.Service.SpeakerSectionService;
import conferenceadmin.conference.Service.SpeakerSectionService.SpeakerSectionDTO;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import conferenceadmin.conference.Service.AuditService;
import jakarta.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/api/speaker-sections")
public class SpeakerSectionController {

    private final SpeakerSectionService speakerSectionService;
    private final AuditService auditService;

    public SpeakerSectionController(SpeakerSectionService speakerSectionService, AuditService auditService) {
        this.speakerSectionService = speakerSectionService;
        this.auditService = auditService;
    }

    /**
     * Create a single section for a speaker
     * POST /api/speaker-sections/speaker/{speakerId}
     */
    @PostMapping("/speaker/{speakerId}")
    @CacheEvict(value = "conferenceData", allEntries = true)
    public ResponseEntity<?> createSection(
            @PathVariable Long speakerId,
            @RequestParam("content") String content,
            @RequestParam("username") String username,
            HttpServletRequest request
    ) {
        try {
            SpeakerSection section = speakerSectionService.createSection(speakerId, content);
            String newValues = String.format("Speaker ID: %d, Content: %s", speakerId, content);
            auditService.logAction(username, "CREATE_SPEAKER_SECTION", newValues, request);
            return ResponseEntity.status(HttpStatus.CREATED).body(section);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Failed to create section: " + e.getMessage());
        }
    }

    /**
     * Create multiple sections for a speaker at once
     * POST /api/speaker-sections/speaker/{speakerId}/bulk
     */
    @PostMapping("/speaker/{speakerId}/bulk")
    @CacheEvict(value = "conferenceData", allEntries = true)
    public ResponseEntity<?> createMultipleSections(
            @PathVariable Long speakerId,
            @RequestBody List<SpeakerSectionDTO> sections,
            @RequestParam("username") String username,
            HttpServletRequest request
    ) {
        try {
            List<SpeakerSection> createdSections = speakerSectionService.createMultipleSections(speakerId, sections);
            String newValues = String.format("Speaker ID: %d, Sections Count: %d", speakerId, sections.size());
            auditService.logAction(username, "CREATE_MULTIPLE_SPEAKER_SECTIONS", newValues, request);
            return ResponseEntity.status(HttpStatus.CREATED).body(createdSections);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Failed to create sections: " + e.getMessage());
        }
    }

    /**
     * Get all sections
     * GET /api/speaker-sections
     */
    @GetMapping
    public ResponseEntity<List<SpeakerSection>> getAllSections() {
        List<SpeakerSection> sections = speakerSectionService.getAllSections();
        return ResponseEntity.ok(sections);
    }

    /**
     * Get section by ID
     * GET /api/speaker-sections/{id}
     */
    @GetMapping("/{id}")
    public ResponseEntity<?> getSectionById(@PathVariable Long id) {
        try {
            SpeakerSection section = speakerSectionService.getSectionById(id);
            return ResponseEntity.ok(section);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }

    /**
     * Get all sections for a specific speaker
     * GET /api/speaker-sections/speaker/{speakerId}
     */
    @GetMapping("/speaker/{speakerId}")
    public ResponseEntity<List<SpeakerSection>> getSectionsBySpeakerId(@PathVariable Long speakerId) {
        List<SpeakerSection> sections = speakerSectionService.getSectionsBySpeakerId(speakerId);
        return ResponseEntity.ok(sections);
    }

    /**
     * Get all sections for a specific speaker by slug
     * GET /api/speaker-sections/speaker/slug/{slug}
     */
    /**
     * Get all sections for a specific speaker by slug with enhanced speaker info
     * GET /api/speaker-sections/speaker/slug/{slug}
     */
    @GetMapping("/speaker/slug/{slug}")
    public ResponseEntity<List<EnhancedSpeakerSectionDTO>> getSectionsBySlug(@PathVariable String slug) {
        List<SpeakerSection> sections = speakerSectionService.getSectionsBySpeakerSlug(slug);
        List<EnhancedSpeakerSectionDTO> response = sections.stream()
                .map(EnhancedSpeakerSectionDTO::new)
                .collect(java.util.stream.Collectors.toList());
        return ResponseEntity.ok(response);
    }

    // DTO class for enhanced response
    public static class EnhancedSpeakerSectionDTO {
        private Long id;
        private String content;
        private String priorities;
        private String currentFocus;
        private String futureFocus;
        private String name;
        private String university;
        private String imagePath;
        private String speakerType;
        private String partnerLogo;

        public EnhancedSpeakerSectionDTO(SpeakerSection section) {
            this.id = section.getId();
            this.content = section.getContent();
            this.priorities = section.getPriorities();
            this.currentFocus = section.getCurrentFocus();
            this.futureFocus = section.getFutureFocus();
            if (section.getSpeaker() != null) {
                this.name = section.getSpeaker().getName();
                this.university = section.getSpeaker().getUniversity();
                this.imagePath = section.getSpeaker().getImagePath();
                this.speakerType = section.getSpeaker().getSpeakerType();
                this.partnerLogo = section.getSpeaker().getPartnerLogo();
            }
        }

        // Getters
        public Long getId() { return id; }
        public String getContent() { return content; }
        public String getPriorities() { return priorities; }
        public String getCurrentFocus() { return currentFocus; }
        public String getFutureFocus() { return futureFocus; }
        public String getName() { return name; }
        public String getUniversity() { return university; }
        public String getImagePath() { return imagePath; }
        public String getSpeakerType() { return speakerType; }
        public String getPartnerLogo() { return partnerLogo; }
    }

    /**
     * Update a section
     * PUT /api/speaker-sections/{id}
     */
    @PutMapping("/{id}")
    @CacheEvict(value = "conferenceData", allEntries = true)
    public ResponseEntity<?> updateSection(
            @PathVariable Long id,
            @RequestParam(value = "content", required = false) String content,
            @RequestParam(value = "priorities", required = false) String priorities,
            @RequestParam(value = "currentFocus", required = false) String currentFocus,
            @RequestParam(value = "futureFocus", required = false) String futureFocus,
            @RequestParam("username") String username,
            HttpServletRequest request
    ) {
        try {
            SpeakerSection section = speakerSectionService.updateSection(id, content, 
                                                                           priorities, currentFocus, futureFocus);
            String newValues = String.format("Section ID: %d, Content: %s, Priorities: %s, CurrentFocus: %s, FutureFocus: %s", 
                                             id, content, priorities, currentFocus, futureFocus);
            auditService.logAction(username, "UPDATE_SPEAKER_SECTION", newValues, request);
            return ResponseEntity.ok(section);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Failed to update section: " + e.getMessage());
        }
    }

    /**
     * Delete a section
     * DELETE /api/speaker-sections/{id}
     */
    @DeleteMapping("/{id}")
    @CacheEvict(value = "conferenceData", allEntries = true)
    public ResponseEntity<?> deleteSection(@PathVariable Long id, @RequestParam("username") String username, HttpServletRequest request) {
        try {
            speakerSectionService.deleteSection(id);
            String newValues = String.format("Section ID: %d", id);
            auditService.logAction(username, "DELETE_SPEAKER_SECTION", newValues, request);
            return ResponseEntity.noContent().build();
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Failed to delete section: " + e.getMessage());
        }
    }
}
