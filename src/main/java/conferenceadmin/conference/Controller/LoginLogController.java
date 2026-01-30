package conferenceadmin.conference.Controller;

import conferenceadmin.conference.Entity.LoginLog;
import conferenceadmin.conference.Service.LoginLogService;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/admin/logs")
public class LoginLogController {

    private final LoginLogService loginLogService;

    public LoginLogController(LoginLogService loginLogService) {
        this.loginLogService = loginLogService;
    }

    @GetMapping
    public ResponseEntity<List<LoginLog>> getAllLoginLogs() {
        List<LoginLog> logs = loginLogService.getAllLoginLogs();
        return ResponseEntity.ok(logs);
    }

    @GetMapping("/user/{username}")
    public ResponseEntity<List<LoginLog>> getLoginLogsByUsername(@PathVariable String username) {
        List<LoginLog> logs = loginLogService.getLoginLogsByUsername(username);
        return ResponseEntity.ok(logs);
    }

    @GetMapping("/successful")
    public ResponseEntity<List<LoginLog>> getSuccessfulLogins() {
        List<LoginLog> logs = loginLogService.getSuccessfulLogins();
        return ResponseEntity.ok(logs);
    }

    @GetMapping("/failed")
    public ResponseEntity<List<LoginLog>> getFailedLogins() {
        List<LoginLog> logs = loginLogService.getFailedLogins();
        return ResponseEntity.ok(logs);
    }

    @GetMapping("/date-range")
    public ResponseEntity<List<LoginLog>> getLoginLogsBetweenDates(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate) {
        List<LoginLog> logs = loginLogService.getLoginLogsBetweenDates(startDate, endDate);
        return ResponseEntity.ok(logs);
    }
}