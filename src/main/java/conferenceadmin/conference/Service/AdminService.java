package conferenceadmin.conference.Service;

import conferenceadmin.conference.Entity.Admin;
import conferenceadmin.conference.Repository.AdminRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class AdminService {

    private final AdminRepository repository;
    private final LoginLogService loginLogService;

    public AdminService(AdminRepository repository, LoginLogService loginLogService) {
        this.repository = repository;
        this.loginLogService = loginLogService;
    }

    public Admin registerAdmin(String username, String password, String role) {
        if (repository.findByUsername(username).isPresent()) {
            throw new IllegalArgumentException("Username already exists");
        }
        Admin admin = new Admin(username, password, role);
        return repository.save(admin);
    }

    public boolean login(String username, String password, String ipAddress) {
        Optional<Admin> opt = repository.findByUsername(username);
        if (opt.isPresent()) {
            Admin admin = opt.get();
            boolean credentialsValid = password.equals(admin.getPassword());
            if (credentialsValid) {
                // Log as pending OTP verification
                loginLogService.logLoginAttempt(username, ipAddress, false, "Credentials valid - awaiting OTP verification");
                return true;
            } else {
                loginLogService.logLoginAttempt(username, ipAddress, false, "Invalid password");
                return false;
            }
        } else {
            loginLogService.logLoginAttempt(username, ipAddress, false, "User not found");
            return false;
        }
    }

    public void logSuccessfulLogin(String username, String ipAddress) {
        loginLogService.logLoginAttempt(username, ipAddress, true, null);
    }

    public void logOtpFailure(String username, String ipAddress, String otp) {
        loginLogService.logLoginAttempt(username, ipAddress, false, "Invalid OTP: " + otp);
    }

    public boolean changePassword(String username, String oldPassword, String newPassword) {
        Optional<Admin> opt = repository.findByUsername(username);
        if (opt.isPresent()) {
            Admin admin = opt.get();
            if (oldPassword.equals(admin.getPassword())) {
                admin.setPassword(newPassword);
                repository.save(admin);
                return true;
            }
        }
        return false;
    }

    public List<Admin> getAllAdmins() {
        return repository.findAll();
    }

    public Optional<Admin> getAdminByUsername(String username) {
        return repository.findByUsername(username);
    }

    public boolean deleteAdmin(String username) {
        Optional<Admin> opt = repository.findByUsername(username);
        if (opt.isPresent()) {
            repository.delete(opt.get());
            return true;
        }
        return false;
    }
}