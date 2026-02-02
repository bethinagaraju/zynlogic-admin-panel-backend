package conferenceadmin.conference.Controller;

import conferenceadmin.conference.Entity.Testimonials;
import conferenceadmin.conference.Service.TestimonialsService;
import conferenceadmin.conference.Service.AuditService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RestController
@RequestMapping("/api/testimonials")
public class TestimonialsController {

    private final TestimonialsService testimonialsService;
    private final AuditService auditService;

    public TestimonialsController(TestimonialsService testimonialsService, AuditService auditService) {
        this.testimonialsService = testimonialsService;
        this.auditService = auditService;
    }

    @PostMapping(consumes = {"multipart/form-data"})
    public ResponseEntity<?> addTestimonial(
            @RequestParam("name") String name,
            @RequestParam("university") String university,
            @RequestParam("description") String description,
            @RequestParam(value = "rating", required = false) Integer rating,
            @RequestParam("conferencecode") String conferencecode,
            @RequestParam(value = "image", required = false) MultipartFile image,
            @RequestParam("username") String username,
            HttpServletRequest request) {
        try {
            Testimonials saved = testimonialsService.saveTestimonial(image, name, university, description, rating, conferencecode);
            String newValues = String.format("Name: %s, University: %s, Description: %s, Rating: %d, Conference: %s",
                                           name, university, description, rating, conferencecode);
            auditService.logCreate(username, "Testimonials", saved.getId().toString(), name, newValues, request, conferencecode);
            return ResponseEntity.status(HttpStatus.CREATED).body(saved);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to save testimonial: " + e.getMessage());
        }
    }

    @PutMapping(value = "/{id}", consumes = {"multipart/form-data"})
    public ResponseEntity<?> updateTestimonial(
            @PathVariable("id") Long id,
            @RequestParam(value = "name", required = false) String name,
            @RequestParam(value = "university", required = false) String university,
            @RequestParam(value = "description", required = false) String description,
            @RequestParam(value = "rating", required = false) Integer rating,
            @RequestParam(value = "conferencecode", required = false) String conferencecode,
            @RequestParam(value = "image", required = false) MultipartFile image,
            @RequestParam("username") String username,
            HttpServletRequest request) {
        try {
            // Get the testimonial before update for comparison
            Testimonials existingTestimonial = testimonialsService.getTestimonialById(id);

            // Update the testimonial
            Testimonials updated = testimonialsService.updateTestimonial(id, image, name, university, description, rating, conferencecode);

            // Always log the update attempt with details of what was requested to change
            String requestedChanges = buildRequestedChangesString(name, university, description, rating, conferencecode, image != null);
            String currentValues = buildCurrentValuesString(existingTestimonial);
            String updatedValues = buildUpdatedValuesString(updated);

            // Detect actual changes by comparing what was requested vs what actually changed
            String actualChanges = buildActualChangesDescription(existingTestimonial, updated, name, university, description, rating, conferencecode, image != null);

            auditService.logUpdate(username, "Testimonials", id.toString(), updated.getName(),
                                 actualChanges,
                                 currentValues,
                                 updatedValues, request, updated.getConferencecode());

            return ResponseEntity.ok(updated);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to update testimonial: " + e.getMessage());
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getTestimonialById(@PathVariable("id") Long id) {
        try {
            Testimonials testimonial = testimonialsService.getTestimonialById(id);
            return ResponseEntity.ok(testimonial);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }

    @GetMapping
    public ResponseEntity<?> getAllTestimonials() {
        return ResponseEntity.ok(testimonialsService.getAllTestimonials());
    }

    @GetMapping("/by-conferencecode/{conferencecode}")
    public ResponseEntity<?> getTestimonialsByConferencecode(@PathVariable("conferencecode") String conferencecode) {
        return ResponseEntity.ok(testimonialsService.getTestimonialsByConferencecode(conferencecode));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteTestimonial(@PathVariable("id") Long id, @RequestParam("username") String username, HttpServletRequest request) {
        try {
            Testimonials testimonial = testimonialsService.getTestimonialById(id);
            testimonialsService.deleteTestimonial(id);
            auditService.logDelete(username, "Testimonials", id.toString(), testimonial.getName(), request, testimonial.getConferencecode());
            return ResponseEntity.noContent().build();
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }

    private String buildRequestedChangesString(String name, String university, String description, Integer rating, String conferencecode, boolean hasImage) {
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
        if (description != null) {
            if (hasChanges) requested.append(", ");
            requested.append("Description");
            hasChanges = true;
        }
        if (rating != null) {
            if (hasChanges) requested.append(", ");
            requested.append("Rating");
            hasChanges = true;
        }
        if (conferencecode != null) {
            if (hasChanges) requested.append(", ");
            requested.append("Conference Code");
            hasChanges = true;
        }
        if (hasImage) {
            if (hasChanges) requested.append(", ");
            requested.append("Image");
            hasChanges = true;
        }

        return hasChanges ? requested.toString() : "Update requested - no specific fields provided";
    }

    private String buildActualChangesDescription(Testimonials existing, Testimonials updated, String requestedName, String requestedUniversity, String requestedDescription, Integer requestedRating, String requestedConferencecode, boolean requestedImage) {
        StringBuilder description = new StringBuilder();
        boolean hasRequests = false;

        // Check name
        if (requestedName != null && !requestedName.isBlank()) {
            hasRequests = true;
            if (!existing.getName().equals(updated.getName())) {
                description.append("Name changed from '").append(existing.getName()).append("' to '").append(updated.getName()).append("'");
            } else {
                description.append("Name updated to '").append(requestedName).append("' ");
            }
        }

        // Check university
        if (requestedUniversity != null) {
            if (hasRequests) description.append("; ");
            hasRequests = true;
            String existingUni = existing.getUniversity() != null ? existing.getUniversity() : "";
            String updatedUni = updated.getUniversity() != null ? updated.getUniversity() : "";
            if (!existingUni.equals(updatedUni)) {
                description.append("University changed from '").append(existingUni).append("' to '").append(updatedUni).append("'");
            } else {
                description.append("University updated to '").append(requestedUniversity).append("' ");
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

        // Check rating
        if (requestedRating != null) {
            if (hasRequests) description.append("; ");
            hasRequests = true;
            if (!existing.getRating().equals(updated.getRating())) {
                description.append("Rating changed from '").append(existing.getRating()).append("' to '").append(updated.getRating()).append("'");
            } else {
                description.append("Rating updated to '").append(requestedRating).append("' ");
            }
        }

        // Check conferencecode
        if (requestedConferencecode != null && !requestedConferencecode.isBlank()) {
            if (hasRequests) description.append("; ");
            hasRequests = true;
            if (!existing.getConferencecode().equals(updated.getConferencecode())) {
                description.append("Conference Code changed from '").append(existing.getConferencecode()).append("' to '").append(updated.getConferencecode()).append("'");
            } else {
                description.append("Conference Code updated to '").append(requestedConferencecode).append("' ");
            }
        }

        // Check image
        if (requestedImage) {
            if (hasRequests) description.append("; ");
            hasRequests = true;
            description.append("Image updated");
        }

        return hasRequests ? description.toString() : "Update requested - no specific fields provided";
    }

    private String buildCurrentValuesString(Testimonials testimonial) {
        return String.format("Name: %s, University: %s, Description: %s, Rating: %d, Conference: %s",
                           testimonial.getName(),
                           testimonial.getUniversity() != null ? testimonial.getUniversity() : "N/A",
                           testimonial.getDescription() != null ? testimonial.getDescription() : "N/A",
                           testimonial.getRating(),
                           testimonial.getConferencecode());
    }

    private String buildUpdatedValuesString(Testimonials testimonial) {
        return String.format("Name: %s, University: %s, Description: %s, Rating: %d, Conference: %s",
                           testimonial.getName(),
                           testimonial.getUniversity() != null ? testimonial.getUniversity() : "N/A",
                           testimonial.getDescription() != null ? testimonial.getDescription() : "N/A",
                           testimonial.getRating(),
                           testimonial.getConferencecode());
    }
}