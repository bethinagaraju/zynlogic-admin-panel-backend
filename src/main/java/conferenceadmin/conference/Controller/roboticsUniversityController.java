package conferenceadmin.conference.Controller;

import conferenceadmin.conference.Entity.roboticsUniversity;
import conferenceadmin.conference.Service.roboticsUniversityService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RestController
@RequestMapping("/api/robotics/universities")
public class roboticsUniversityController {

    private final roboticsUniversityService universityService;

    public roboticsUniversityController(roboticsUniversityService universityService) {
        this.universityService = universityService;
    }

    @PostMapping(consumes = {"multipart/form-data"})
    public ResponseEntity<?> addUniversity(
            @RequestParam("name") String name,
            @RequestParam("image") MultipartFile image
    ) {
        try {
            roboticsUniversity saved = universityService.saveUniversity(image, name);
            return ResponseEntity.status(HttpStatus.CREATED).body(saved);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Upload failed: " + e.getMessage());
        }
    }

    @GetMapping
    public ResponseEntity<?> getAllUniversities() {
        return ResponseEntity.ok(universityService.getAllUniversities());
    }

    @PutMapping(value = "/{id}", consumes = {"multipart/form-data"})
    public ResponseEntity<?> updateUniversity(
            @PathVariable("id") Long id,
            @RequestParam(value = "name", required = false) String name,
            @RequestParam(value = "image", required = false) MultipartFile image
    ) {
        try {
            roboticsUniversity updated = universityService.updateUniversity(id, name, image);
            return ResponseEntity.ok(updated);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Update failed: " + e.getMessage());
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getUniversityById(@PathVariable("id") Long id) {
        try {
            roboticsUniversity uni = universityService.getUniversityById(id);
            return ResponseEntity.ok(uni);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteUniversity(@PathVariable("id") Long id) {
        try {
            universityService.deleteUniversity(id);
            return ResponseEntity.noContent().build();
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Delete failed: " + e.getMessage());
        }
    }
}
