package conferenceadmin.conference.Controller;

import conferenceadmin.conference.Entity.Committee;
import conferenceadmin.conference.Service.CommitteeService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/committees")
public class CommitteeController {

    private final CommitteeService committeeService;

    public CommitteeController(CommitteeService committeeService) {
        this.committeeService = committeeService;
    }

    @PostMapping
    public ResponseEntity<?> addCommittee(
            @RequestParam("conferencecode") String conferencecode,
            @RequestParam("name") String name,
            @RequestParam("university") String university) {
        try {
            Committee saved = committeeService.saveCommittee(conferencecode, name, university);
            return ResponseEntity.status(HttpStatus.CREATED).body(saved);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateCommittee(
            @PathVariable("id") Long id,
            @RequestParam(value = "conferencecode", required = false) String conferencecode,
            @RequestParam(value = "name", required = false) String name,
            @RequestParam(value = "university", required = false) String university) {
        try {
            Committee updated = committeeService.updateCommittee(id, conferencecode, name, university);
            return ResponseEntity.ok(updated);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getCommitteeById(@PathVariable("id") Long id) {
        try {
            Committee committee = committeeService.getCommitteeById(id);
            return ResponseEntity.ok(committee);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }

    @GetMapping
    public ResponseEntity<?> getAllCommittees() {
        return ResponseEntity.ok(committeeService.getAllCommittees());
    }

    @GetMapping("/by-conferencecode/{conferencecode}")
    public ResponseEntity<?> getCommitteesByConferencecode(@PathVariable("conferencecode") String conferencecode) {
        return ResponseEntity.ok(committeeService.getCommitteesByConferencecode(conferencecode));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteCommittee(@PathVariable("id") Long id) {
        try {
            committeeService.deleteCommittee(id);
            return ResponseEntity.noContent().build();
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }

    @PutMapping("/reorder")
    public ResponseEntity<?> reorderCommittees(@RequestBody List<Long> committeeIdsInOrder) {
        try {
            committeeService.reorderCommittees(committeeIdsInOrder);
            return ResponseEntity.ok("Committees reordered successfully");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Failed to reorder committees: " + e.getMessage());
        }
    }
}
