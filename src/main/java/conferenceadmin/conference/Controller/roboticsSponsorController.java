package conferenceadmin.conference.Controller;

import conferenceadmin.conference.Entity.roboticsSponsor;
import conferenceadmin.conference.Service.roboticsSponsorService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RestController
@RequestMapping("/api/robotics/sponsors")
public class roboticsSponsorController {

    private final roboticsSponsorService sponsorService;

    public roboticsSponsorController(roboticsSponsorService sponsorService) {
        this.sponsorService = sponsorService;
    }

    @PostMapping(consumes = {"multipart/form-data"})
    public ResponseEntity<?> uploadSponsor(
            @RequestParam("name") String name,
            @RequestParam("type") String type,
            @RequestParam("file") MultipartFile file
    ) {
        try {
            roboticsSponsor saved = sponsorService.uploadSponsor(name, type, file);
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
            @RequestParam(value = "file", required = false) MultipartFile file
    ) {
        try {
            roboticsSponsor updated = sponsorService.updateSponsor(id, name, type, file);
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
    public ResponseEntity<?> deleteSponsor(@PathVariable("id") Long id) {
        try {
            sponsorService.deleteSponsor(id);
            return ResponseEntity.noContent().build();
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Delete failed: " + e.getMessage());
        }
    }
}
