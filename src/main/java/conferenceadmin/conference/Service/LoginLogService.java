package conferenceadmin.conference.Service;

import conferenceadmin.conference.Entity.LoginLog;
import conferenceadmin.conference.Repository.LoginLogRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class LoginLogService {

    private final LoginLogRepository loginLogRepository;

    public LoginLogService(LoginLogRepository loginLogRepository) {
        this.loginLogRepository = loginLogRepository;
    }

    public void logLoginAttempt(String username, String ipAddress, boolean success, String failureReason) {
        LoginLog loginLog = new LoginLog(username, LocalDateTime.now(), ipAddress, success, failureReason, null);
        loginLogRepository.save(loginLog);
    }

    public List<LoginLog> getAllLoginLogs() {
        return loginLogRepository.findAllByOrderByLoginTimeDesc();
    }

    public List<LoginLog> getLoginLogsByUsername(String username) {
        return loginLogRepository.findByUsernameOrderByLoginTimeDesc(username);
    }

    public List<LoginLog> getSuccessfulLogins() {
        return loginLogRepository.findBySuccessOrderByLoginTimeDesc(true);
    }

    public List<LoginLog> getFailedLogins() {
        return loginLogRepository.findBySuccessOrderByLoginTimeDesc(false);
    }

    public List<LoginLog> getLoginLogsBetweenDates(LocalDateTime startDate, LocalDateTime endDate) {
        return loginLogRepository.findByLoginTimeBetween(startDate, endDate);
    }
}