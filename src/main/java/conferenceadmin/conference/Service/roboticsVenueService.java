package conferenceadmin.conference.Service;

import conferenceadmin.conference.Entity.roboticsVenue;
import conferenceadmin.conference.Repository.roboticsVenueRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class roboticsVenueService {

    private final roboticsVenueRepository repository;

    public roboticsVenueService(roboticsVenueRepository repository) {
        this.repository = repository;
    }

    public roboticsVenue saveVenue(String venue, String conferencecode) {
        roboticsVenue v = new roboticsVenue(venue, conferencecode);
        return repository.save(v);
    }

    public roboticsVenue updateVenue(Long id, String venue, String conferencecode) {
        Optional<roboticsVenue> opt = repository.findById(id);
        if (opt.isEmpty()) {
            throw new IllegalArgumentException("Venue not found");
        }
        roboticsVenue v = opt.get();
        if (venue != null && !venue.isBlank()) {
            v.setVenue(venue);
        }
        if (conferencecode != null && !conferencecode.isBlank()) {
            v.setConferencecode(conferencecode);
        }
        return repository.save(v);
    }

    public roboticsVenue getVenueById(Long id) {
        return repository.findById(id).orElseThrow(() -> new IllegalArgumentException("Venue not found"));
    }

    public List<roboticsVenue> getAllVenues() {
        return repository.findAll();
    }

    public List<roboticsVenue> getVenuesByConferencecode(String conferencecode) {
        return repository.findByConferencecode(conferencecode);
    }

    public void deleteVenue(Long id) {
        if (!repository.existsById(id)) {
            throw new IllegalArgumentException("Venue not found");
        }
        repository.deleteById(id);
    }
}
