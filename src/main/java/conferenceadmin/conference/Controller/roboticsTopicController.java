package conferenceadmin.conference.Controller;

import conferenceadmin.conference.Entity.roboticsTopic;
import conferenceadmin.conference.Service.roboticsTopicService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/robotics/topics")
public class roboticsTopicController {

    private final roboticsTopicService topicService;

    public roboticsTopicController(roboticsTopicService topicService) {
        this.topicService = topicService;
    }

    @PostMapping
    public ResponseEntity<?> addTopic(@RequestParam("topicName") String topicName, @RequestParam("conferencecode") String conferencecode) {
        try {
            roboticsTopic saved = topicService.saveTopic(topicName, conferencecode);
            return ResponseEntity.status(HttpStatus.CREATED).body(saved);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateTopic(@PathVariable("id") Long id, @RequestParam(value = "topicName", required = false) String topicName, @RequestParam(value = "conferencecode", required = false) String conferencecode) {
        try {
            roboticsTopic updated = topicService.updateTopic(id, topicName, conferencecode);
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
    public ResponseEntity<?> deleteTopic(@PathVariable("id") Long id) {
        try {
            topicService.deleteTopic(id);
            return ResponseEntity.noContent().build();
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }

    @PutMapping("/reorder")
    public ResponseEntity<?> reorderTopics(@RequestBody List<Long> topicIdsInOrder) {
        try {
            topicService.reorderTopics(topicIdsInOrder);
            return ResponseEntity.ok("Topics reordered successfully");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to reorder topics: " + e.getMessage());
        }
    }
}