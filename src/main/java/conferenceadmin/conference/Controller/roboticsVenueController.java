package conferenceadmin.conference.Controller;

import conferenceadmin.conference.Entity.roboticsVenue;
import conferenceadmin.conference.Service.roboticsVenueService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/robotics/venues")
public class roboticsVenueController {

    private final roboticsVenueService venueService;

    public roboticsVenueController(roboticsVenueService venueService) {
        this.venueService = venueService;
    }

    @PostMapping
    public ResponseEntity<?> addVenue(@RequestParam("venue") String venue, @RequestParam("conferencecode") String conferencecode) {
        try {
            roboticsVenue saved = venueService.saveVenue(venue, conferencecode);
            return ResponseEntity.status(HttpStatus.CREATED).body(saved);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateVenue(@PathVariable("id") Long id, @RequestParam(value = "venue", required = false) String venue, @RequestParam(value = "conferencecode", required = false) String conferencecode) {
        try {
            roboticsVenue updated = venueService.updateVenue(id, venue, conferencecode);
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
    public ResponseEntity<?> deleteVenue(@PathVariable("id") Long id) {
        try {
            venueService.deleteVenue(id);
            return ResponseEntity.noContent().build();
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }
}
