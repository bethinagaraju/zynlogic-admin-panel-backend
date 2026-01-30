package conferenceadmin.conference.Controller;

import conferenceadmin.conference.Service.EmailService;
import conferenceadmin.conference.Service.JwtUtil;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/emails")
public class EmailController {

    private final EmailService emailService;
    private final JwtUtil jwtUtil;

    public EmailController(EmailService emailService, JwtUtil jwtUtil) {
        this.emailService = emailService;
        this.jwtUtil = jwtUtil;
    }

    @PostMapping
    public ResponseEntity<?> sendEmail(
            @RequestParam("to") String to,
            @RequestParam("subject") String subject,
            @RequestParam("body") String body
    ) {
        try {
            emailService.sendEmail(to, subject, body);
            return ResponseEntity.ok("Email sent successfully to " + to);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to send email: " + e.getMessage());
        }
    }

    // @PostMapping("/send-otp")
    // public ResponseEntity<?> sendOtp(@RequestParam("username") String username) {
    //     try {
    //         emailService.sendOtp(username);
    //         return ResponseEntity.ok("OTP sent successfully to manager email for user: " + username);
    //     } catch (Exception e) {
    //         return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to send OTP: " + e.getMessage());
    //     }
    // }

    @PostMapping("/verify-otp")
    public ResponseEntity<?> verifyOtp(@RequestParam("username") String username, @RequestParam("otp") String otp, HttpServletRequest request) {
        String ipAddress = getClientIpAddress(request);
        String token = emailService.verifyOtp(username, otp, ipAddress);
        if (token != null) {
            return ResponseEntity.ok(Map.of("message", "OTP verified successfully", "token", token));
        } else {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid OTP");
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

    @PostMapping("/validate-token")
    public ResponseEntity<?> validateToken(@RequestParam("token") String token) {
        try {
            String username = jwtUtil.extractEmail(token); // Note: method name is extractEmail but now contains username
            boolean isValid = jwtUtil.validateToken(token, username);
            if (isValid) {
                return ResponseEntity.ok(Map.of("valid", true, "username", username, "message", "Token is valid"));
            } else {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("valid", false, "message", "Token is invalid or expired"));
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("valid", false, "message", "Invalid token format"));
        }
    }
}