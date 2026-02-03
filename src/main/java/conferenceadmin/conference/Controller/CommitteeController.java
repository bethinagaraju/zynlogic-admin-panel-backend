package conferenceadmin.conference.Controller;

import conferenceadmin.conference.Entity.Committee;
import conferenceadmin.conference.Service.CommitteeService;
import conferenceadmin.conference.Service.AuditService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/committees")
public class CommitteeController {

    private final CommitteeService committeeService;
    private final AuditService auditService;

    public CommitteeController(CommitteeService committeeService, AuditService auditService) {
        this.committeeService = committeeService;
        this.auditService = auditService;
    }

    @PostMapping
    @CacheEvict(value = "conferenceData", allEntries = true)
    public ResponseEntity<?> addCommittee(
            @RequestParam("conferencecode") String conferencecode,
            @RequestParam("name") String name,
            @RequestParam("university") String university,
            @RequestParam("username") String username,
            HttpServletRequest request) {
        try {
            Committee saved = committeeService.saveCommittee(conferencecode, name, university);
            String newValues = String.format("Name: %s, University: %s, Conference: %s",
                                           name, university, conferencecode);
            auditService.logCreate(username, "Committee", saved.getId().toString(), name, newValues, request, conferencecode);
            return ResponseEntity.status(HttpStatus.CREATED).body(saved);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PutMapping("/{id}")
    @CacheEvict(value = "conferenceData", allEntries = true)
    public ResponseEntity<?> updateCommittee(
            @PathVariable("id") Long id,
            @RequestParam(value = "conferencecode", required = false) String conferencecode,
            @RequestParam(value = "name", required = false) String name,
            @RequestParam(value = "university", required = false) String university,
            @RequestParam("username") String username,
            HttpServletRequest request) {
        try {
            // Get the committee before update for comparison
            Committee existingCommittee = committeeService.getCommitteeById(id);

            // Update the committee
            Committee updated = committeeService.updateCommittee(id, conferencecode, name, university);

            // Always log the update attempt with details of what was requested to change
            String requestedChanges = buildRequestedChangesString(name, university, conferencecode);
            String currentValues = buildCurrentValuesString(existingCommittee);
            String updatedValues = buildUpdatedValuesString(updated);

            // Detect actual changes by comparing what was requested vs what actually changed
            String actualChanges = buildActualChangesDescription(existingCommittee, updated, name, university, conferencecode);

            auditService.logUpdate(username, "Committee", id.toString(), updated.getName(),
                                 actualChanges,
                                 currentValues,
                                 updatedValues, request, conferencecode);

            return ResponseEntity.ok(updated);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getCommitteeById(@PathVariable("id") Long id) {
        try {
            Committee committee = committeeService.getCommitteeById(id);
            return ResponseEntity.ok(committee);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }

    @GetMapping
    public ResponseEntity<?> getAllCommittees() {
        return ResponseEntity.ok(committeeService.getAllCommittees());
    }

    @GetMapping("/by-conferencecode/{conferencecode}")
    public ResponseEntity<?> getCommitteesByConferencecode(@PathVariable("conferencecode") String conferencecode) {
        return ResponseEntity.ok(committeeService.getCommitteesByConferencecode(conferencecode));
    }

    @DeleteMapping("/{id}")
    @CacheEvict(value = "conferenceData", allEntries = true)
    public ResponseEntity<?> deleteCommittee(@PathVariable("id") Long id, @RequestParam("username") String username, HttpServletRequest request) {
        try {
            Committee committee = committeeService.getCommitteeById(id);
            committeeService.deleteCommittee(id);
            auditService.logDelete(username, "Committee", id.toString(), committee.getName(), request, committee.getConferencecode());
            return ResponseEntity.noContent().build();
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }

    @PutMapping("/reorder")
    @CacheEvict(value = "conferenceData", allEntries = true)
    public ResponseEntity<?> reorderCommittees(@RequestBody List<Long> committeeIdsInOrder) {
        try {
            committeeService.reorderCommittees(committeeIdsInOrder);
            return ResponseEntity.ok("Committees reordered successfully");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Failed to reorder committees: " + e.getMessage());
        }
    }

    private String buildRequestedChangesString(String name, String university, String conferencecode) {
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

        return hasChanges ? requested.toString() : "Update requested - no specific fields provided";
    }

    private String buildCurrentValuesString(Committee committee) {
        return String.format("Name: %s, University: %s, Conference: %s",
                           committee.getName(), committee.getUniversity(),
                           committee.getConferencecode());
    }

    private String buildUpdatedValuesString(Committee committee) {
        return String.format("Name: %s, University: %s, Conference: %s",
                           committee.getName(), committee.getUniversity(),
                           committee.getConferencecode());
    }

    private String buildActualChangesString(Committee oldCommittee, Committee newCommittee) {
        StringBuilder actual = new StringBuilder("Actually changed: ");
        boolean hasChanges = false;

        if (!oldCommittee.getName().equals(newCommittee.getName())) {
            actual.append("Name");
            hasChanges = true;
        }
        if (!oldCommittee.getUniversity().equals(newCommittee.getUniversity())) {
            if (hasChanges) actual.append(", ");
            actual.append("University");
            hasChanges = true;
        }
        if (!oldCommittee.getConferencecode().equals(newCommittee.getConferencecode())) {
            if (hasChanges) actual.append(", ");
            actual.append("Conference Code");
            hasChanges = true;
        }

        return hasChanges ? actual.toString() : "No actual changes made - values were already up to date";
    }

    private String buildActualChangesDescription(Committee existing, Committee updated, String requestedName, String requestedUniversity, String requestedConferencecode) {
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

        // Check university
        if (requestedUniversity != null && !requestedUniversity.isBlank()) {
            if (hasRequests) description.append("; ");
            hasRequests = true;
            if (!existing.getUniversity().equals(updated.getUniversity())) {
                description.append("University changed from '").append(existing.getUniversity()).append("' to '").append(updated.getUniversity()).append("'");
            } else {
                description.append("University  updated to '").append(requestedUniversity).append("' ");
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

        return hasRequests ? description.toString() : "Update requested - no specific fields provided";
    }
}
