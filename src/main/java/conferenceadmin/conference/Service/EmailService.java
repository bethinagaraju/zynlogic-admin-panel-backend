package conferenceadmin.conference.Service;

import conferenceadmin.conference.Service.AdminService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

@Service
public class EmailService {

    private final JavaMailSender mailSender;
    private final JwtUtil jwtUtil;
    private final AdminService adminService;
    private final Map<String, String> otpStorage = new HashMap<>();

    @Autowired
    public EmailService(JavaMailSender mailSender, JwtUtil jwtUtil, AdminService adminService) {
        this.mailSender = mailSender;
        this.jwtUtil = jwtUtil;
        this.adminService = adminService;
    }

    public void sendEmail(String to, String subject, String body) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(to);
        message.setSubject(subject);
        message.setText(body);
        message.setFrom("smtp@roboticsaisummit.com");
        mailSender.send(message);
    }

    public void sendOtp(String username) {
        String otp = generateOtp();
        otpStorage.put(username, otp);
        // String managerEmail = "rayavarapujaswanth123@gmail.com"; // Manager's email for OTP
        // String managerEmail = "bethinagaraju57@gmail.com";
        String subject = "Login OTP Code for " + username;
        String body = "OTP code for user '" + username + "' is: " + otp + ". It will expire in 5 minutes.";
        sendEmail(username, subject, body);
    }

    public String verifyOtp(String username, String otp, String ipAddress) {
        String storedOtp = otpStorage.get(username);
        if (storedOtp != null && storedOtp.equals(otp)) {
            otpStorage.remove(username);
            // Log successful login after OTP verification
            adminService.logSuccessfulLogin(username, ipAddress);
            return jwtUtil.generateToken(username);
        } else {
            // Log failed OTP verification
            adminService.logOtpFailure(username, ipAddress, otp);
            return null;
        }
    }

    private String generateOtp() {
        Random random = new Random();
        int otp = 100000 + random.nextInt(900000);
        return String.valueOf(otp);
    }
}