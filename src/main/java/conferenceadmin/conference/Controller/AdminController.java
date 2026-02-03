package conferenceadmin.conference.Controller;

import conferenceadmin.conference.Entity.Admin;
import conferenceadmin.conference.Service.AdminService;
import conferenceadmin.conference.Service.EmailService;
import conferenceadmin.conference.Service.SpeakerService;
import conferenceadmin.conference.Service.TestimonialsService;
import conferenceadmin.conference.Service.roboticsVenueService;
import conferenceadmin.conference.Service.roboticsSponsorService;
import conferenceadmin.conference.Service.roboticsTopicService;
import conferenceadmin.conference.Service.AgendaService;
import conferenceadmin.conference.Service.CommitteeService;
import conferenceadmin.conference.Service.roboticsImportantDateService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.HashMap;

@RestController
@RequestMapping("/api/admin")
public class AdminController {

    private final AdminService adminService;
    private final EmailService emailService;
    private final SpeakerService speakerService;
    private final TestimonialsService testimonialsService;
    private final roboticsVenueService roboticsVenueService;
    private final roboticsSponsorService roboticsSponsorService;
    private final roboticsTopicService roboticsTopicService;
    private final AgendaService agendaService;
    private final CommitteeService committeeService;
    private final roboticsImportantDateService roboticsImportantDateService;

    public AdminController(AdminService adminService, EmailService emailService, SpeakerService speakerService, TestimonialsService testimonialsService, roboticsVenueService roboticsVenueService, roboticsSponsorService roboticsSponsorService, roboticsTopicService roboticsTopicService, AgendaService agendaService, CommitteeService committeeService, roboticsImportantDateService roboticsImportantDateService) {
        this.adminService = adminService;
        this.emailService = emailService;
        this.speakerService = speakerService;
        this.testimonialsService = testimonialsService;
        this.roboticsVenueService = roboticsVenueService;
        this.roboticsSponsorService = roboticsSponsorService;
        this.roboticsTopicService = roboticsTopicService;
        this.agendaService = agendaService;
        this.committeeService = committeeService;
        this.roboticsImportantDateService = roboticsImportantDateService;
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody Map<String, String> payload) {
        try {
            String username = payload.get("username");
            String password = payload.get("password");
            String role = payload.get("role");
            Admin admin = adminService.registerAdmin(username, password, role);
            return ResponseEntity.status(HttpStatus.CREATED).body("Admin registered successfully");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping
    public ResponseEntity<List<Admin>> getAllAdmins() {
        List<Admin> admins = adminService.getAllAdmins();
        return ResponseEntity.ok(admins);
    }

    @DeleteMapping("/{username}")
    public ResponseEntity<?> deleteAdmin(@PathVariable String username) {
        boolean deleted = adminService.deleteAdmin(username);
        if (deleted) {
            return ResponseEntity.noContent().build();
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Admin not found");
        }
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody Map<String, String> payload, HttpServletRequest request) {
        String username = payload.get("username");
        String password = payload.get("password");
        String ipAddress = getClientIpAddress(request);
        boolean credentialsValid = adminService.login(username, password, ipAddress);
        if (credentialsValid) {
            try {
                // Automatically send OTP to manager when credentials are valid
                emailService.sendOtp(username);
                Optional<Admin> adminOpt = adminService.getAdminByUsername(username);
                String role = adminOpt.isPresent() ? adminOpt.get().getRole() : "UNKNOWN";
                return ResponseEntity.ok(Map.of(
                    "message", "Credentials verified. OTP sent to manager email. Please verify OTP.",
                    "requiresOtp", true,
                    "otpSent", true,
                    "role", role
                ));
            } catch (Exception e) {
                Optional<Admin> adminOpt = adminService.getAdminByUsername(username);
                String role = adminOpt.isPresent() ? adminOpt.get().getRole() : "UNKNOWN";
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of(
                    "message", "Credentials verified but failed to send OTP: " + e.getMessage(),
                    "requiresOtp", true,
                    "otpSent", false,
                    "role", role
                ));
            }
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of(
                "message", "Invalid credentials",
                "requiresOtp", false,
                "otpSent", false
            ));
        }
    }

    private String getClientIpAddress(HttpServletRequest request) {
        String xForwardedFor = request.getHeader("X-Forwarded-For");
        if (xForwardedFor != null && !xForwardedFor.isEmpty()) {
            return xForwardedFor.split(",")[0].trim();
        }
        String xRealIp = request.getHeader("X-Real-IP");
        if (xRealIp != null && !xRealIp.isEmpty()) {
            return xRealIp;
        }
        return request.getRemoteAddr();
    }

    @PutMapping("/change-password")
    public ResponseEntity<?> changePassword(@RequestBody Map<String, String> payload) {
        String username = payload.get("username");
        String oldPassword = payload.get("oldPassword");
        String newPassword = payload.get("newPassword");
        boolean success = adminService.changePassword(username, oldPassword, newPassword);
        if (success) {
            return ResponseEntity.ok("Password changed successfully");
        } else {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Failed to change password");
        }
    }

    @GetMapping("/conference-data/{conferencecode}")
    @Cacheable(value = "conferenceData", key = "#conferencecode")
    public ResponseEntity<Map<String, Object>> getConferenceData(@PathVariable String conferencecode) {
        Map<String, Object> data = new HashMap<>();
        data.put("speakers", speakerService.getSpeakersByConferencecode(conferencecode));
        data.put("testimonials", testimonialsService.getTestimonialsByConferencecode(conferencecode));
        data.put("venues", roboticsVenueService.getVenuesByConferencecode(conferencecode));
        data.put("sponsors", roboticsSponsorService.getSponsorsByConferencecode(conferencecode));
        data.put("topics", roboticsTopicService.getTopicsByConferencecode(conferencecode));
        data.put("agenda", agendaService.getAgendasByConferencecode(conferencecode));
        data.put("committee", committeeService.getCommitteesByConferencecode(conferencecode));
        data.put("importantDates", roboticsImportantDateService.getImportantDatesByConferencecode(conferencecode));
        return ResponseEntity.ok(data);
    }
}