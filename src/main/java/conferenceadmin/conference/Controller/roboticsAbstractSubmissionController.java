package conferenceadmin.conference.Controller;

import conferenceadmin.conference.Entity.roboticsAbstractSubmission;
import conferenceadmin.conference.Service.roboticsAbstractSubmissionService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RestController
@RequestMapping("/api/robotics/abstract-submissions")
public class roboticsAbstractSubmissionController {

    private final roboticsAbstractSubmissionService submissionService;

    public roboticsAbstractSubmissionController(roboticsAbstractSubmissionService submissionService) {
        this.submissionService = submissionService;
    }

    @PostMapping(consumes = {"multipart/form-data"})
    public ResponseEntity<?> submitAbstract(
            @RequestParam("conferencecode") String conferencecode,
            @RequestParam("title") String title,
            @RequestParam("fullName") String fullName,
            @RequestParam("phoneNumber") String phoneNumber,
            @RequestParam("emailAddress") String emailAddress,
            @RequestParam("organization") String organization,
            @RequestParam("country") String country,
            @RequestParam("file") MultipartFile file
    ) {
        try {
            roboticsAbstractSubmission saved = submissionService.submitAbstract(conferencecode, title, fullName, phoneNumber, emailAddress, organization, country, file);
            return ResponseEntity.status(HttpStatus.CREATED).body(saved);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Submission failed: " + e.getMessage());
        }
    }

    @GetMapping("/by-conferencecode/{conferencecode}")
    public ResponseEntity<?> getAbstractSubmissionsByConferencecode(@PathVariable("conferencecode") String conferencecode) {
        return ResponseEntity.ok(submissionService.getAbstractSubmissionsByConferencecode(conferencecode));
    }
}