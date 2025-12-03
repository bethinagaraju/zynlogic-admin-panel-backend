package conferenceadmin.conference.Repository;

import conferenceadmin.conference.Entity.roboticsSponsor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface roboticsSponsorRepository extends JpaRepository<roboticsSponsor, Long> {

    List<roboticsSponsor> findByConferencecode(String conferencecode);

}
