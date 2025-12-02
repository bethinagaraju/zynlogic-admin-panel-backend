package conferenceadmin.conference.Controller;

import conferenceadmin.conference.Entity.Speaker;
import conferenceadmin.conference.Service.SpeakerService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RestController
@RequestMapping("/api/speakers")
public class SpeakerController {

    private final SpeakerService speakerService;

    public SpeakerController(SpeakerService speakerService) {
        this.speakerService = speakerService;
    }

    @PostMapping("/robotics")
    public ResponseEntity<?> uploadSpeaker(
            @RequestParam("image") MultipartFile image,
            @RequestParam("name") String name,
            @RequestParam("university") String university
    ) {
        try {
            Speaker saved = speakerService.saveSpeaker(image, name, university);
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
    public ResponseEntity<?> updateSpeaker(
            @PathVariable("id") Long id,
            @RequestParam(value = "image", required = false) MultipartFile image,
            @RequestParam(value = "imageUrl", required = false) String imageUrl,
            @RequestParam(value = "name", required = false) String name,
            @RequestParam(value = "university", required = false) String university
    ) {
        try {
            // uploaded file takes precedence over provided URL
            Speaker updated = speakerService.updateSpeaker(id, image, imageUrl, name, university);
            return ResponseEntity.ok(updated);
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to update speaker: " + e.getMessage());
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteSpeaker(@PathVariable("id") Long id) {
        boolean deleted = speakerService.deleteSpeaker(id);
        if (!deleted) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Speaker not found");
        }
        return ResponseEntity.noContent().build();
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
}
