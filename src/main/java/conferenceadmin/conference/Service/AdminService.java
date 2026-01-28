package conferenceadmin.conference.Service;

import conferenceadmin.conference.Entity.Admin;
import conferenceadmin.conference.Repository.AdminRepository;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class AdminService {

    private final AdminRepository repository;

    public AdminService(AdminRepository repository) {
        this.repository = repository;
    }

    public Admin registerAdmin(String username, String password) {
        if (repository.findByUsername(username).isPresent()) {
            throw new IllegalArgumentException("Username already exists");
        }
        Admin admin = new Admin(username, password);
        return repository.save(admin);
    }

    public boolean login(String username, String password) {
        Optional<Admin> opt = repository.findByUsername(username);
        if (opt.isPresent()) {
            Admin admin = opt.get();
            return password.equals(admin.getPassword());
        }
        return false;
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
}