package conferenceadmin.conference.Repository;

import conferenceadmin.conference.Entity.roboticsSponsor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface roboticsSponsorRepository extends JpaRepository<roboticsSponsor, Long> {

}
