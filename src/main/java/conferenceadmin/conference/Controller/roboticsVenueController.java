package conferenceadmin.conference.Controller;

import conferenceadmin.conference.Entity.roboticsVenue;
import conferenceadmin.conference.Service.roboticsVenueService;
import conferenceadmin.conference.Service.AuditService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/robotics/venues")
public class roboticsVenueController {

    private final roboticsVenueService venueService;
    private final AuditService auditService;

    public roboticsVenueController(roboticsVenueService venueService, AuditService auditService) {
        this.venueService = venueService;
        this.auditService = auditService;
    }

    @PostMapping
    public ResponseEntity<?> addVenue(@RequestParam("venue") String venue, @RequestParam("conferencecode") String conferencecode, @RequestParam(value = "description", required = false) String description, @RequestParam("username") String username, HttpServletRequest request) {
        try {
            roboticsVenue saved = venueService.saveVenue(venue, conferencecode, description);
            String newValues = String.format("Venue: %s, Conference: %s, Description: %s",
                                           venue, conferencecode, description != null ? description : "N/A");
            auditService.logCreate(username, "RoboticsVenue", saved.getId().toString(), venue, newValues, request, conferencecode);
            return ResponseEntity.status(HttpStatus.CREATED).body(saved);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateVenue(@PathVariable("id") Long id, @RequestParam(value = "venue", required = false) String venue, @RequestParam(value = "conferencecode", required = false) String conferencecode, @RequestParam(value = "description", required = false) String description, @RequestParam("username") String username, HttpServletRequest request) {
        try {
            // Get the venue before update for comparison
            roboticsVenue existingVenue = venueService.getVenueById(id);

            // Update the venue
            roboticsVenue updated = venueService.updateVenue(id, venue, conferencecode, description);

            // Always log the update attempt with details of what was requested to change
            String requestedChanges = buildRequestedChangesString(venue, conferencecode, description);
            String currentValues = buildCurrentValuesString(existingVenue);
            String updatedValues = buildUpdatedValuesString(updated);

            // Detect actual changes by comparing what was requested vs what actually changed
            String actualChanges = buildActualChangesDescription(existingVenue, updated, venue, conferencecode, description);

            auditService.logUpdate(username, "RoboticsVenue", id.toString(), updated.getVenue(),
                                 actualChanges,
                                 currentValues,
                                 updatedValues, request, updated.getConferencecode());

            return ResponseEntity.ok(updated);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getVenueById(@PathVariable("id") Long id) {
        try {
            roboticsVenue v = venueService.getVenueById(id);
            return ResponseEntity.ok(v);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }

    @GetMapping
    public ResponseEntity<?> getAllVenues() {
        return ResponseEntity.ok(venueService.getAllVenues());
    }

    @GetMapping("/by-conferencecode/{conferencecode}")
    public ResponseEntity<?> getVenuesByConferencecode(@PathVariable("conferencecode") String conferencecode) {
        return ResponseEntity.ok(venueService.getVenuesByConferencecode(conferencecode));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteVenue(@PathVariable("id") Long id, @RequestParam("username") String username, HttpServletRequest request) {
        try {
            roboticsVenue venue = venueService.getVenueById(id);
            venueService.deleteVenue(id);
            auditService.logDelete(username, "RoboticsVenue", id.toString(), venue.getVenue(), request, venue.getConferencecode());
            return ResponseEntity.noContent().build();
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }

    private String buildRequestedChangesString(String venue, String conferencecode, String description) {
        StringBuilder requested = new StringBuilder("Update requested for: ");
        boolean hasChanges = false;

        if (venue != null) {
            requested.append("Venue");
            hasChanges = true;
        }
        if (conferencecode != null) {
            if (hasChanges) requested.append(", ");
            requested.append("Conference Code");
            hasChanges = true;
        }
        if (description != null) {
            if (hasChanges) requested.append(", ");
            requested.append("Description");
            hasChanges = true;
        }

        return hasChanges ? requested.toString() : "Update requested - no specific fields provided";
    }

    private String buildActualChangesString(roboticsVenue oldVenue, roboticsVenue newVenue) {
        StringBuilder actual = new StringBuilder("Actually changed: ");
        boolean hasChanges = false;

        if (!oldVenue.getVenue().equals(newVenue.getVenue())) {
            actual.append("Venue");
            hasChanges = true;
        }
        if (!oldVenue.getConferencecode().equals(newVenue.getConferencecode())) {
            if (hasChanges) actual.append(", ");
            actual.append("Conference Code");
            hasChanges = true;
        }

        return hasChanges ? actual.toString() : "No actual changes made - values were already up to date";
    }

    private String buildActualChangesDescription(roboticsVenue existing, roboticsVenue updated, String requestedVenue, String requestedConferencecode, String requestedDescription) {
        StringBuilder description = new StringBuilder();
        boolean hasRequests = false;

        // Check venue
        if (requestedVenue != null && !requestedVenue.isBlank()) {
            hasRequests = true;
            if (!existing.getVenue().equals(updated.getVenue())) {
                description.append("Venue changed from '").append(existing.getVenue()).append("' to '").append(updated.getVenue()).append("'");
            } else {
                description.append("Venue  updated to '").append(requestedVenue).append("' ");
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

        // Check description
        if (requestedDescription != null) {
            if (hasRequests) description.append("; ");
            hasRequests = true;
            String existingDesc = existing.getDescription() != null ? existing.getDescription() : "";
            String updatedDesc = updated.getDescription() != null ? updated.getDescription() : "";
            if (!existingDesc.equals(updatedDesc)) {
                description.append("Description changed from '").append(existingDesc).append("' to '").append(updatedDesc).append("'");
            } else {
                description.append("Description updated to '").append(requestedDescription).append("' ");
            }
        }

        return hasRequests ? description.toString() : "Update requested - no specific fields provided";
    }

    private String buildCurrentValuesString(roboticsVenue venue) {
        return String.format("Venue: %s, Conference: %s, Description: %s",
                           venue.getVenue(), venue.getConferencecode(), venue.getDescription() != null ? venue.getDescription() : "N/A");
    }

    private String buildUpdatedValuesString(roboticsVenue venue) {
        return String.format("Venue: %s, Conference: %s, Description: %s",
                           venue.getVenue(), venue.getConferencecode(), venue.getDescription() != null ? venue.getDescription() : "N/A");
    }
}
