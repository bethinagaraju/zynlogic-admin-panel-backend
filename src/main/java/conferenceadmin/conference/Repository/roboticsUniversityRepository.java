package conferenceadmin.conference.Repository;

import conferenceadmin.conference.Entity.roboticsUniversity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface roboticsUniversityRepository extends JpaRepository<roboticsUniversity, Long> {

}
