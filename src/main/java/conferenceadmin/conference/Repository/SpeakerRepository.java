package conferenceadmin.conference.Repository;

import org.springframework.data.jpa.repository.JpaRepository;
import conferenceadmin.conference.Entity.Speaker;

public interface SpeakerRepository extends JpaRepository<Speaker, Long> {

}
