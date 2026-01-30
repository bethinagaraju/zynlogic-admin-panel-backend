package conferenceadmin.conference.Controller;

import conferenceadmin.conference.Entity.Admin;
import conferenceadmin.conference.Service.AdminService;
import conferenceadmin.conference.Service.EmailService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/admin")
public class AdminController {

    private final AdminService adminService;
    private final EmailService emailService;

    public AdminController(AdminService adminService, EmailService emailService) {
        this.adminService = adminService;
        this.emailService = emailService;
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
}