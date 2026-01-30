package conferenceadmin.conference.Controller;

import conferenceadmin.conference.Entity.roboticsSponsor;
import conferenceadmin.conference.Service.roboticsSponsorService;
import conferenceadmin.conference.Service.AuditService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RestController
@RequestMapping("/api/robotics/sponsors")
public class roboticsSponsorController {

    private final roboticsSponsorService sponsorService;
    private final AuditService auditService;

    public roboticsSponsorController(roboticsSponsorService sponsorService, AuditService auditService) {
        this.sponsorService = sponsorService;
        this.auditService = auditService;
    }

    @PostMapping(consumes = {"multipart/form-data"})
    public ResponseEntity<?> uploadSponsor(
            @RequestParam("name") String name,
            @RequestParam("type") String type,
            @RequestParam("conferencecode") String conferencecode,
            @RequestParam("file") MultipartFile file,
            @RequestParam("username") String username,
            HttpServletRequest request
    ) {
        try {
            roboticsSponsor saved = sponsorService.uploadSponsor(name, type, conferencecode, file);
            String newValues = String.format("Name: %s, Type: %s, Conference: %s",
                                           name, type, conferencecode);
            auditService.logCreate(username, "RoboticsSponsor", saved.getId().toString(), name, newValues, request, conferencecode);
            return ResponseEntity.status(HttpStatus.CREATED).body(saved);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Upload failed: " + e.getMessage());
        }
    }

    @PutMapping(value = "/{id}", consumes = {"multipart/form-data"})
    public ResponseEntity<?> updateSponsor(
            @PathVariable("id") Long id,
            @RequestParam(value = "name", required = false) String name,
            @RequestParam(value = "type", required = false) String type,
            @RequestParam(value = "conferencecode", required = false) String conferencecode,
            @RequestParam(value = "file", required = false) MultipartFile file,
            @RequestParam("username") String username,
            HttpServletRequest request
    ) {
        try {
            // Get the sponsor before update for comparison
            roboticsSponsor existingSponsor = sponsorService.getSponsorById(id);

            // Update the sponsor
            roboticsSponsor updated = sponsorService.updateSponsor(id, name, type, conferencecode, file);

            // Always log the update attempt with details of what was requested to change
            String requestedChanges = buildRequestedChangesString(name, type, conferencecode, file);
            String currentValues = buildCurrentValuesString(existingSponsor);
            String updatedValues = buildUpdatedValuesString(updated);

            // Detect actual changes by comparing what was requested vs what actually changed
            String actualChanges = buildActualChangesDescription(existingSponsor, updated, name, type, conferencecode, file);

            auditService.logUpdate(username, "RoboticsSponsor", id.toString(), updated.getName(),
                                 actualChanges,
                                 currentValues,
                                 updatedValues, request, updated.getConferencecode());

            return ResponseEntity.ok(updated);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Update failed: " + e.getMessage());
        }
    }

    @GetMapping
    public ResponseEntity<?> getAllSponsors() {
        return ResponseEntity.ok(sponsorService.getAllSponsors());
    }

    @GetMapping("/by-conferencecode/{conferencecode}")
    public ResponseEntity<?> getSponsorsByConferencecode(@PathVariable("conferencecode") String conferencecode) {
        return ResponseEntity.ok(sponsorService.getSponsorsByConferencecode(conferencecode));
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getSponsorById(@PathVariable("id") Long id) {
        try {
            roboticsSponsor sponsor = sponsorService.getSponsorById(id);
            return ResponseEntity.ok(sponsor);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteSponsor(@PathVariable("id") Long id, @RequestParam("username") String username, HttpServletRequest request) {
        try {
            roboticsSponsor sponsor = sponsorService.getSponsorById(id);
            sponsorService.deleteSponsor(id);
            auditService.logDelete(username, "RoboticsSponsor", id.toString(), sponsor.getName(), request, sponsor.getConferencecode());
            return ResponseEntity.noContent().build();
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Delete failed: " + e.getMessage());
        }
    }

    private String buildRequestedChangesString(String name, String type, String conferencecode, MultipartFile file) {
        StringBuilder requested = new StringBuilder("Update requested for: ");
        boolean hasChanges = false;

        if (name != null) {
            requested.append("Name");
            hasChanges = true;
        }
        if (type != null) {
            if (hasChanges) requested.append(", ");
            requested.append("Type");
            hasChanges = true;
        }
        if (conferencecode != null) {
            if (hasChanges) requested.append(", ");
            requested.append("Conference Code");
            hasChanges = true;
        }
        if (file != null) {
            if (hasChanges) requested.append(", ");
            requested.append("Image");
            hasChanges = true;
        }

        return hasChanges ? requested.toString() : "Update requested - no specific fields provided";
    }

    private String buildCurrentValuesString(roboticsSponsor sponsor) {
        return String.format("Name: %s, Type: %s, Conference: %s",
                           sponsor.getName(), sponsor.getType(),
                           sponsor.getConferencecode());
    }

    private String buildUpdatedValuesString(roboticsSponsor sponsor) {
        return String.format("Name: %s, Type: %s, Conference: %s",
                           sponsor.getName(), sponsor.getType(),
                           sponsor.getConferencecode());
    }

    private String buildActualChangesString(roboticsSponsor oldSponsor, roboticsSponsor newSponsor) {
        StringBuilder actual = new StringBuilder("Actually changed: ");
        boolean hasChanges = false;

        if (!oldSponsor.getName().equals(newSponsor.getName())) {
            actual.append("Name");
            hasChanges = true;
        }
        if (!oldSponsor.getType().equals(newSponsor.getType())) {
            if (hasChanges) actual.append(", ");
            actual.append("Type");
            hasChanges = true;
        }
        if (!oldSponsor.getConferencecode().equals(newSponsor.getConferencecode())) {
            if (hasChanges) actual.append(", ");
            actual.append("Conference Code");
            hasChanges = true;
        }
        if (!oldSponsor.getImagePath().equals(newSponsor.getImagePath())) {
            if (hasChanges) actual.append(", ");
            actual.append("Image");
            hasChanges = true;
        }

        return hasChanges ? actual.toString() : "No actual changes made - values were already up to date";
    }

    private String buildActualChangesDescription(roboticsSponsor existing, roboticsSponsor updated, String requestedName, String requestedType, String requestedConferencecode, MultipartFile requestedFile) {
        StringBuilder description = new StringBuilder();
        boolean hasRequests = false;

        // Check name
        if (requestedName != null && !requestedName.isBlank()) {
            hasRequests = true;
            if (!existing.getName().equals(updated.getName())) {
                description.append("Name changed from '").append(existing.getName()).append("' to '").append(updated.getName()).append("'");
            } else {
                description.append("Name  updated to '").append(requestedName);
            }
        }

        // Check type
        if (requestedType != null && !requestedType.isBlank()) {
            if (hasRequests) description.append("; ");
            hasRequests = true;
            if (!existing.getType().equals(updated.getType())) {
                description.append("Type changed from '").append(existing.getType()).append("' to '").append(updated.getType()).append("'");
            } else {
                description.append("Type  updated to '").append(requestedType).append("' ");
            }
        }

        // Check conferencecode
        if (requestedConferencecode != null && !requestedConferencecode.isBlank()) {
            if (hasRequests) description.append("; ");
            hasRequests = true;
            if (!existing.getConferencecode().equals(updated.getConferencecode())) {
                description.append("Conference Code changed from '").append(existing.getConferencecode()).append("' to '").append(updated.getConferencecode()).append("'");
            } else {
                description.append("Conference Code  updated to '").append(requestedConferencecode).append("' ");
            }
        }

        // Check file
        if (requestedFile != null && !requestedFile.isEmpty()) {
            if (hasRequests) description.append("; ");
            hasRequests = true;
            if (!existing.getImagePath().equals(updated.getImagePath())) {
                description.append("Image was updated");
            } else {
                description.append("Image  updated ");
            }
        }

        return hasRequests ? description.toString() : "Update requested - no specific fields provided";
    }
}
