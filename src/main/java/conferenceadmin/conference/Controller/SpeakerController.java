package conferenceadmin.conference.Controller;

import conferenceadmin.conference.Entity.Speaker;
import conferenceadmin.conference.Service.SpeakerService;
import conferenceadmin.conference.Service.AuditService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RestController
@RequestMapping("/api/speakers")
public class SpeakerController {

    private final SpeakerService speakerService;
    private final AuditService auditService;

    public SpeakerController(SpeakerService speakerService, AuditService auditService) {
        this.speakerService = speakerService;
        this.auditService = auditService;
    }

    @PostMapping("/robotics")
    @CacheEvict(value = "conferenceData", allEntries = true)
    public ResponseEntity<?> uploadSpeaker(
            @RequestParam("image") MultipartFile image,
            @RequestParam("name") String name,
            @RequestParam("university") String university,
            @RequestParam("conferencecode") String conferencecode,
            @RequestParam("speakerType") String speakerType,
            @RequestParam(value = "visible", defaultValue = "true") boolean visible,
            @RequestParam("username") String username,
            HttpServletRequest request
    ) {
        try {
            Speaker saved = speakerService.saveSpeaker(image, name, university, conferencecode, speakerType, visible);
            String newValues = String.format("Name: %s, University: %s, Conference: %s, Type: %s, Visible: %s",
                                           name, university, conferencecode, speakerType, visible);
            auditService.logCreate(username, "Speaker", saved.getId().toString(), name, newValues, request, conferencecode);
            return ResponseEntity.status(HttpStatus.CREATED).body(saved);
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to save speaker: " + e.getMessage());
        }
    }

    @org.springframework.web.bind.annotation.GetMapping
    public ResponseEntity<java.util.List<Speaker>> getAllSpeakers() {
        java.util.List<Speaker> list = speakerService.getAllSpeakers();
        return ResponseEntity.ok(list);
    }

    @PutMapping("/{id}")
    @CacheEvict(value = "conferenceData", allEntries = true)
    public ResponseEntity<?> updateSpeaker(
            @PathVariable("id") Long id,
            @RequestParam(value = "image", required = false) MultipartFile image,
            @RequestParam(value = "imageUrl", required = false) String imageUrl,
            @RequestParam(value = "name", required = false) String name,
            @RequestParam(value = "university", required = false) String university,
            @RequestParam(value = "conferencecode", required = false) String conferencecode,
            @RequestParam(value = "speakerType", required = false) String speakerType,
            @RequestParam(value = "visible", required = false) Boolean visible,
            @RequestParam("username") String username,
            HttpServletRequest request
    ) {
        try {
            // Get the speaker before update for comparison
            Speaker existingSpeaker = speakerService.getSpeakerById(id);

            // uploaded file takes precedence over provided URL
            Speaker updated = speakerService.updateSpeaker(id, image, imageUrl, name, university, conferencecode, speakerType, visible);

            // Always log the update attempt with details of what was requested to change
            String requestedChanges = buildRequestedChangesString(name, university, conferencecode, speakerType, image, imageUrl, visible);
            String currentValues = buildCurrentValuesString(existingSpeaker);
            String updatedValues = buildUpdatedValuesString(updated);

            // Detect actual changes by comparing what was requested vs what actually changed
            String actualChanges = buildActualChangesDescription(existingSpeaker, updated, name, university, conferencecode, speakerType, image, imageUrl, visible);

            auditService.logUpdate(username, "Speaker", id.toString(), updated.getName(),
                                 actualChanges,
                                 currentValues,
                                 updatedValues, request, updated.getConferencecode());

            return ResponseEntity.ok(updated);
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to update speaker: " + e.getMessage());
        }
    }

    @DeleteMapping("/{id}")
    @CacheEvict(value = "conferenceData", allEntries = true)
    public ResponseEntity<?> deleteSpeaker(@PathVariable("id") Long id, @RequestParam("username") String username, HttpServletRequest request) {
        try {
            Speaker speaker = speakerService.getSpeakerById(id);
            boolean deleted = speakerService.deleteSpeaker(id);
            if (!deleted) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Speaker not found");
            }
            auditService.logDelete(username, "Speaker", id.toString(), speaker.getName(), request, speaker.getConferencecode());
            return ResponseEntity.noContent().build();
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }

    @org.springframework.web.bind.annotation.GetMapping("/{id}")
    public ResponseEntity<?> getSpeakerById(@PathVariable("id") Long id) {
        try {
            Speaker speaker = speakerService.getSpeakerById(id);
            return ResponseEntity.ok(speaker);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }

    @GetMapping("/conference/{conferencecode}")
    public ResponseEntity<java.util.List<Speaker>> getSpeakersByConferencecode(@PathVariable String conferencecode) {
        java.util.List<Speaker> list = speakerService.getSpeakersByConferencecode(conferencecode);
        return ResponseEntity.ok(list);
    }

    @PutMapping("/reorder")
    @CacheEvict(value = "conferenceData", allEntries = true)
    public ResponseEntity<?> reorderSpeakers(@RequestBody java.util.List<Long> speakerIdsInOrder) {
        try {
            speakerService.reorderSpeakers(speakerIdsInOrder);
            return ResponseEntity.ok("Speakers reordered successfully");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to reorder speakers: " + e.getMessage());
        }
    }

    private String buildRequestedChangesString(String name, String university, String conferencecode, String speakerType, MultipartFile image, String imageUrl, Boolean visible) {
        StringBuilder requested = new StringBuilder("Update requested for: ");
        boolean hasChanges = false;

        if (name != null) {
            requested.append("Name");
            hasChanges = true;
        }
        if (university != null) {
            if (hasChanges) requested.append(", ");
            requested.append("University");
            hasChanges = true;
        }
        if (conferencecode != null) {
            if (hasChanges) requested.append(", ");
            requested.append("Conference Code");
            hasChanges = true;
        }
        if (speakerType != null) {
            if (hasChanges) requested.append(", ");
            requested.append("Speaker Type");
            hasChanges = true;
        }
        if (image != null || imageUrl != null) {
            if (hasChanges) requested.append(", ");
            requested.append("Image");
            hasChanges = true;
        }
        if (visible != null) {
            if (hasChanges) requested.append(", ");
            requested.append("Visible");
            hasChanges = true;
        }

        return hasChanges ? requested.toString() : "Update requested - no specific fields provided";
    }

    private String buildCurrentValuesString(Speaker speaker) {
        return String.format("Name: %s, University: %s, Conference: %s, Type: %s, Visible: %s",
                           speaker.getName(), speaker.getUniversity(),
                           speaker.getConferencecode(), speaker.getSpeakerType(), speaker.isVisible());
    }

    private String buildUpdatedValuesString(Speaker speaker) {
        return String.format("Name: %s, University: %s, Conference: %s, Type: %s, Visible: %s",
                           speaker.getName(), speaker.getUniversity(),
                           speaker.getConferencecode(), speaker.getSpeakerType(), speaker.isVisible());
    }

    private String buildActualChangesString(Speaker oldSpeaker, Speaker newSpeaker) {
        StringBuilder actual = new StringBuilder("Actually changed: ");
        boolean hasChanges = false;

        if (!oldSpeaker.getName().equals(newSpeaker.getName())) {
            actual.append("Name");
            hasChanges = true;
        }
        if (!oldSpeaker.getUniversity().equals(newSpeaker.getUniversity())) {
            if (hasChanges) actual.append(", ");
            actual.append("University");
            hasChanges = true;
        }
        if (!oldSpeaker.getConferencecode().equals(newSpeaker.getConferencecode())) {
            if (hasChanges) actual.append(", ");
            actual.append("Conference Code");
            hasChanges = true;
        }
        if (!oldSpeaker.getSpeakerType().equals(newSpeaker.getSpeakerType())) {
            if (hasChanges) actual.append(", ");
            actual.append("Speaker Type");
            hasChanges = true;
        }
        if (!oldSpeaker.getImagePath().equals(newSpeaker.getImagePath())) {
            if (hasChanges) actual.append(", ");
            actual.append("Image");
            hasChanges = true;
        }

        return hasChanges ? actual.toString() : "No actual changes made - values were already up to date";
    }

    private String buildActualChangesDescription(Speaker existing, Speaker updated, String requestedName, String requestedUniversity, String requestedConferencecode, String requestedSpeakerType, MultipartFile requestedImage, String requestedImageUrl, Boolean requestedVisible) {
        StringBuilder description = new StringBuilder();
        boolean hasRequests = false;

        // Check each requested field
        if (requestedName != null && !requestedName.isBlank()) {
            hasRequests = true;
            if (!existing.getName().equals(updated.getName())) {
                description.append("Name changed from '").append(existing.getName()).append("' to '").append(updated.getName()).append("'");
            } else {
                description.append("Name  updated to '").append(requestedName);
            }
        }

        if (requestedUniversity != null && !requestedUniversity.isBlank()) {
            if (hasRequests) description.append("; ");
            hasRequests = true;
            if (!existing.getUniversity().equals(updated.getUniversity())) {
                description.append("University changed from '").append(existing.getUniversity()).append("' to '").append(updated.getUniversity()).append("'");
            } else {
                description.append("University  updated to '").append(requestedUniversity).append("' ");
            }
        }

        if (requestedConferencecode != null && !requestedConferencecode.isBlank()) {
            if (hasRequests) description.append("; ");
            hasRequests = true;
            if (!existing.getConferencecode().equals(updated.getConferencecode())) {
                description.append("Conference Code changed from '").append(existing.getConferencecode()).append("' to '").append(updated.getConferencecode()).append("'");
            } else {
                description.append("Conference Code  updated to '").append(requestedConferencecode).append("' ");
            }
        }

        if (requestedSpeakerType != null && !requestedSpeakerType.isBlank()) {
            if (hasRequests) description.append("; ");
            hasRequests = true;
            if (!existing.getSpeakerType().equals(updated.getSpeakerType())) {
                description.append("Speaker Type changed from '").append(existing.getSpeakerType()).append("' to '").append(updated.getSpeakerType()).append("'");
            } else {
                description.append("Speaker Type  updated to '").append(requestedSpeakerType).append("' ");
            }
        }

        if (requestedImage != null && !requestedImage.isEmpty()) {
            if (hasRequests) description.append("; ");
            hasRequests = true;
            if (!existing.getImagePath().equals(updated.getImagePath())) {
                description.append("Image was updated");
            } else {
                description.append("Image  updated ");
            }
        } else if (requestedImageUrl != null && !requestedImageUrl.isBlank()) {
            if (hasRequests) description.append("; ");
            hasRequests = true;
            if (!existing.getImagePath().equals(updated.getImagePath())) {
                description.append("Image URL was updated");
            } else {
                description.append("Image URL  updated ");
            }
        }

        if (requestedVisible != null) {
            if (hasRequests) description.append("; ");
            hasRequests = true;
            if (existing.isVisible() != updated.isVisible()) {
                description.append("Visible changed from '").append(existing.isVisible()).append("' to '").append(updated.isVisible()).append("'");
            } else {
                description.append("Visible  updated to '").append(requestedVisible).append("' ");
            }
        }

        return hasRequests ? description.toString() : "Update requested - no specific fields provided";
    }
}
