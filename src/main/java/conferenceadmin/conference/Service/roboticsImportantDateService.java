package conferenceadmin.conference.Service;

import conferenceadmin.conference.Entity.roboticsImportantDate;
import conferenceadmin.conference.Repository.roboticsImportantDateRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class roboticsImportantDateService {

    private final roboticsImportantDateRepository repository;

    public roboticsImportantDateService(roboticsImportantDateRepository repository) {
        this.repository = repository;
    }

    public roboticsImportantDate saveImportantDate(String date, String conferencecode) {
        if (date == null || date.isBlank()) {
            throw new IllegalArgumentException("Date is required");
        }
        roboticsImportantDate d = new roboticsImportantDate(date, conferencecode);
        return repository.save(d);
    }

    public List<roboticsImportantDate> getAllImportantDates() {
        return repository.findAll();
    }

    public roboticsImportantDate getImportantDateById(Long id) {
        return repository.findById(id).orElseThrow(() -> new IllegalArgumentException("Important date not found"));
    }

    public roboticsImportantDate updateImportantDate(Long id, String date, String conferencecode) {
        roboticsImportantDate existing = repository.findById(id).orElseThrow(() -> new IllegalArgumentException("Important date not found"));
        if (date == null || date.isBlank()) {
            throw new IllegalArgumentException("Date is required");
        }
        existing.setDate(date);
        if (conferencecode != null && !conferencecode.isBlank()) {
            existing.setConferencecode(conferencecode);
        }
        return repository.save(existing);
    }

    public List<roboticsImportantDate> getImportantDatesByConferencecode(String conferencecode) {
        return repository.findByConferencecode(conferencecode);
    }
}
