package conferenceadmin.conference.Controller;

import conferenceadmin.conference.Entity.roboticsTopic;
import conferenceadmin.conference.Service.roboticsTopicService;
import conferenceadmin.conference.Service.AuditService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/robotics/topics")
public class roboticsTopicController {

    private final roboticsTopicService topicService;
    private final AuditService auditService;

    public roboticsTopicController(roboticsTopicService topicService, AuditService auditService) {
        this.topicService = topicService;
        this.auditService = auditService;
    }

    @PostMapping
    @CacheEvict(value = "conferenceData", allEntries = true)
    public ResponseEntity<?> addTopic(@RequestParam("topicName") String topicName, @RequestParam("conferencecode") String conferencecode, @RequestParam("username") String username, HttpServletRequest request) {
        try {
            roboticsTopic saved = topicService.saveTopic(topicName, conferencecode);
            String newValues = String.format("Topic Name: %s, Conference: %s",
                                           topicName, conferencecode);
            auditService.logCreate(username, "RoboticsTopic", saved.getId().toString(), topicName, newValues, request, conferencecode);
            return ResponseEntity.status(HttpStatus.CREATED).body(saved);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PutMapping("/{id}")
    @CacheEvict(value = "conferenceData", allEntries = true)
    public ResponseEntity<?> updateTopic(@PathVariable("id") Long id, @RequestParam(value = "topicName", required = false) String topicName, @RequestParam(value = "conferencecode", required = false) String conferencecode, @RequestParam("username") String username, HttpServletRequest request) {
        try {
            // Get the topic before update for comparison
            roboticsTopic existingTopic = topicService.getTopicById(id);

            // Update the topic
            roboticsTopic updated = topicService.updateTopic(id, topicName, conferencecode);

            // Always log the update attempt with details of what was requested to change
            String requestedChanges = buildRequestedChangesString(topicName, conferencecode);
            String currentValues = buildCurrentValuesString(existingTopic);
            String updatedValues = buildUpdatedValuesString(updated);

            // Detect actual changes by comparing what was requested vs what actually changed
            String actualChanges = buildActualChangesDescription(existingTopic, updated, topicName, conferencecode);

            auditService.logUpdate(username, "RoboticsTopic", id.toString(), updated.getTopicName(),
                                 actualChanges,
                                 currentValues,
                                 updatedValues, request, updated.getConferencecode());

            return ResponseEntity.ok(updated);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getTopicById(@PathVariable("id") Long id) {
        try {
            roboticsTopic t = topicService.getTopicById(id);
            return ResponseEntity.ok(t);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }

    @GetMapping
    public ResponseEntity<?> getAllTopics() {
        return ResponseEntity.ok(topicService.getAllTopics());
    }

    @GetMapping("/by-conferencecode/{conferencecode}")
    public ResponseEntity<?> getTopicsByConferencecode(@PathVariable("conferencecode") String conferencecode) {
        return ResponseEntity.ok(topicService.getTopicsByConferencecode(conferencecode));
    }

    @DeleteMapping("/{id}")
    @CacheEvict(value = "conferenceData", allEntries = true)
    public ResponseEntity<?> deleteTopic(@PathVariable("id") Long id, @RequestParam("username") String username, HttpServletRequest request) {
        try {
            roboticsTopic topic = topicService.getTopicById(id);
            topicService.deleteTopic(id);
            auditService.logDelete(username, "RoboticsTopic", id.toString(), topic.getTopicName(), request, topic.getConferencecode());
            return ResponseEntity.noContent().build();
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }

    @PutMapping("/reorder")
    @CacheEvict(value = "conferenceData", allEntries = true)
    public ResponseEntity<?> reorderTopics(@RequestBody List<Long> topicIdsInOrder) {
        try {
            topicService.reorderTopics(topicIdsInOrder);
            return ResponseEntity.ok("Topics reordered successfully");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to reorder topics: " + e.getMessage());
        }
    }

    private String buildRequestedChangesString(String topicName, String conferencecode) {
        StringBuilder requested = new StringBuilder("Update requested for: ");
        boolean hasChanges = false;

        if (topicName != null) {
            requested.append("Topic Name");
            hasChanges = true;
        }
        if (conferencecode != null) {
            if (hasChanges) requested.append(", ");
            requested.append("Conference Code");
            hasChanges = true;
        }

        return hasChanges ? requested.toString() : "Update requested - no specific fields provided";
    }

    private String buildCurrentValuesString(roboticsTopic topic) {
        return String.format("Topic Name: %s, Conference: %s",
                           topic.getTopicName(), topic.getConferencecode());
    }

    private String buildUpdatedValuesString(roboticsTopic topic) {
        return String.format("Topic Name: %s, Conference: %s",
                           topic.getTopicName(), topic.getConferencecode());
    }

    private String buildActualChangesDescription(roboticsTopic existing, roboticsTopic updated, String requestedTopicName, String requestedConferencecode) {
        StringBuilder description = new StringBuilder();
        boolean hasRequests = false;

        // Check topicName
        if (requestedTopicName != null && !requestedTopicName.isBlank()) {
            hasRequests = true;
            if (!existing.getTopicName().equals(updated.getTopicName())) {
                description.append("Topic Name changed from '").append(existing.getTopicName()).append("' to '").append(updated.getTopicName()).append("'");
            } else {
                description.append("Topic Name  updated to '").append(requestedTopicName);
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