package conferenceadmin.conference.Repository;

import conferenceadmin.conference.Entity.SpeakerSection;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SpeakerSectionRepository extends JpaRepository<SpeakerSection, Long> {
    List<SpeakerSection> findBySpeakerId(Long speakerId);

    @org.springframework.data.jpa.repository.Query("SELECT ss FROM SpeakerSection ss JOIN FETCH ss.speaker s WHERE s.slug = :slug")
    List<SpeakerSection> findBySpeakerSlug(@org.springframework.data.repository.query.Param("slug") String slug);
}
