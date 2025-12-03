package conferenceadmin.conference.Controller;

import conferenceadmin.conference.Entity.roboticsImportantDate;
import conferenceadmin.conference.Service.roboticsImportantDateService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/robotics/important-dates")
public class roboticsImportantDateController {

    private final roboticsImportantDateService dateService;

    public roboticsImportantDateController(roboticsImportantDateService dateService) {
        this.dateService = dateService;
    }

    @PostMapping
    public ResponseEntity<?> addImportantDate(@RequestParam("date") String date, @RequestParam("conferencecode") String conferencecode) {
        try {
            roboticsImportantDate saved = dateService.saveImportantDate(date, conferencecode);
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
    public ResponseEntity<?> updateImportantDate(@PathVariable("id") Long id, @RequestParam("date") String date, @RequestParam(value = "conferencecode", required = false) String conferencecode) {
        try {
            roboticsImportantDate updated = dateService.updateImportantDate(id, date, conferencecode);
            return ResponseEntity.ok(updated);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }
}
