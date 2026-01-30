package conferenceadmin.conference.Controller;

import conferenceadmin.conference.Entity.roboticsImportantDate;
import conferenceadmin.conference.Service.roboticsImportantDateService;
import conferenceadmin.conference.Service.AuditService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/robotics/important-dates")
public class roboticsImportantDateController {

    private final roboticsImportantDateService dateService;
    private final AuditService auditService;

    public roboticsImportantDateController(roboticsImportantDateService dateService, AuditService auditService) {
        this.dateService = dateService;
        this.auditService = auditService;
    }

    @PostMapping
    public ResponseEntity<?> addImportantDate(@RequestParam("date") String date, @RequestParam("conferencecode") String conferencecode, @RequestParam("dateType") String dateType, @RequestParam("username") String username, HttpServletRequest request) {
        try {
            roboticsImportantDate saved = dateService.saveImportantDate(date, conferencecode, dateType);
            String newValues = String.format("Date: %s, Type: %s, Conference: %s",
                                           date, dateType, conferencecode);
            auditService.logCreate(username, "RoboticsImportantDate", saved.getId().toString(), date, newValues, request, conferencecode);
            return ResponseEntity.status(HttpStatus.CREATED).body(saved);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping
    public ResponseEntity<?> getAllImportantDates() {
        return ResponseEntity.ok(dateService.getAllImportantDates());
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getImportantDateById(@PathVariable("id") Long id) {
        try {
            roboticsImportantDate d = dateService.getImportantDateById(id);
            return ResponseEntity.ok(d);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }

    @GetMapping("/by-conferencecode/{conferencecode}")
    public ResponseEntity<?> getImportantDatesByConferencecode(@PathVariable("conferencecode") String conferencecode) {
        return ResponseEntity.ok(dateService.getImportantDatesByConferencecode(conferencecode));
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateImportantDate(@PathVariable("id") Long id, @RequestParam("date") String date, @RequestParam(value = "conferencecode", required = false) String conferencecode, @RequestParam(value = "dateType", required = false) String dateType, @RequestParam("username") String username, HttpServletRequest request) {
        try {
            // Get the important date before update for comparison
            roboticsImportantDate existingDate = dateService.getImportantDateById(id);

            // Update the important date
            roboticsImportantDate updated = dateService.updateImportantDate(id, date, conferencecode, dateType);

            // Always log the update attempt with details of what was requested to change
            String requestedChanges = buildRequestedChangesString(date, conferencecode, dateType);
            String currentValues = buildCurrentValuesString(existingDate);
            String updatedValues = buildUpdatedValuesString(updated);

            // Detect actual changes by comparing what was requested vs what actually changed
            String actualChanges = buildActualChangesDescription(existingDate, updated, date, conferencecode, dateType);

            auditService.logUpdate(username, "RoboticsImportantDate", id.toString(), updated.getDate(),
                                 actualChanges,
                                 currentValues,
                                 updatedValues, request, updated.getConferencecode());

            return ResponseEntity.ok(updated);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteImportantDate(@PathVariable("id") Long id, @RequestParam("username") String username, HttpServletRequest request) {
        try {
            roboticsImportantDate date = dateService.getImportantDateById(id);
            dateService.deleteImportantDate(id);
            auditService.logDelete(username, "RoboticsImportantDate", id.toString(), date.getDate(), request, date.getConferencecode());
            return ResponseEntity.noContent().build();
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }

    private String buildRequestedChangesString(String date, String conferencecode, String dateType) {
        StringBuilder requested = new StringBuilder("Update requested for: ");
        boolean hasChanges = false;

        if (date != null) {
            requested.append("Date");
            hasChanges = true;
        }
        if (conferencecode != null) {
            if (hasChanges) requested.append(", ");
            requested.append("Conference Code");
            hasChanges = true;
        }
        if (dateType != null) {
            if (hasChanges) requested.append(", ");
            requested.append("Date Type");
            hasChanges = true;
        }

        return hasChanges ? requested.toString() : "Update requested - no specific fields provided";
    }

    private String buildCurrentValuesString(roboticsImportantDate date) {
        return String.format("Date: %s, Type: %s, Conference: %s",
                           date.getDate(), date.getDateType(),
                           date.getConferencecode());
    }

    private String buildUpdatedValuesString(roboticsImportantDate date) {
        return String.format("Date: %s, Type: %s, Conference: %s",
                           date.getDate(), date.getDateType(),
                           date.getConferencecode());
    }

    private String buildActualChangesDescription(roboticsImportantDate existing, roboticsImportantDate updated, String requestedDate, String requestedConferencecode, String requestedDateType) {
        StringBuilder description = new StringBuilder();
        boolean hasRequests = false;

        // Check date
        if (requestedDate != null && !requestedDate.isBlank()) {
            hasRequests = true;
            if (!existing.getDate().equals(updated.getDate())) {
                description.append("Date changed from '").append(existing.getDate()).append("' to '").append(updated.getDate()).append("'");
            } else {
                description.append("Date  updated to '").append(requestedDate).append("' ");
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

        // Check dateType
        if (requestedDateType != null && !requestedDateType.isBlank()) {
            if (hasRequests) description.append("; ");
            hasRequests = true;
            if (!existing.getDateType().equals(updated.getDateType())) {
                description.append("Date Type changed from '").append(existing.getDateType()).append("' to '").append(updated.getDateType()).append("'");
            } else {
                description.append("Date Type  updated to '").append(requestedDateType).append("' ");
            }
        }

        return hasRequests ? description.toString() : "Update requested - no specific fields provided";
    }
}
