package conferenceadmin.conference.Repository;

import conferenceadmin.conference.Entity.roboticsVenue;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface roboticsVenueRepository extends JpaRepository<roboticsVenue, Long> {

    List<roboticsVenue> findByConferencecode(String conferencecode);

}
