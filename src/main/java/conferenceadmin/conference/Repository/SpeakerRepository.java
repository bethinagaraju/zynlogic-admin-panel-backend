package conferenceadmin.conference.Repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import conferenceadmin.conference.Entity.Speaker;
import java.util.List;
import java.util.Optional;

public interface SpeakerRepository extends JpaRepository<Speaker, Long> {

    List<Speaker> findByConferencecode(String conferencecode);

    @Query("SELECT DISTINCT s FROM Speaker s LEFT JOIN FETCH s.sections WHERE s.conferencecode = :conferencecode ORDER BY s.orderIndex ASC")
    List<Speaker> findByConferencecodeOrderByOrderIndex(@Param("conferencecode") String conferencecode);

    @Query("SELECT s FROM Speaker s LEFT JOIN FETCH s.sections WHERE s.slug = :slug")
    Optional<Speaker> findBySlug(@Param("slug") String slug);

    @Query("SELECT s FROM Speaker s LEFT JOIN FETCH s.sections WHERE s.name = :name")
    Optional<Speaker> findByName(@Param("name") String name);

}
