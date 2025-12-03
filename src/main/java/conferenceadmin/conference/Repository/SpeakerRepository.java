package conferenceadmin.conference.Repository;

import org.springframework.data.jpa.repository.JpaRepository;
import conferenceadmin.conference.Entity.Speaker;
import java.util.List;

public interface SpeakerRepository extends JpaRepository<Speaker, Long> {

    List<Speaker> findByConferencecode(String conferencecode);
}
