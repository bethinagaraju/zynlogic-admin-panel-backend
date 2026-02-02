package conferenceadmin.conference.Repository;

import conferenceadmin.conference.Entity.Testimonials;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TestimonialsRepository extends JpaRepository<Testimonials, Long> {

    List<Testimonials> findByConferencecode(String conferencecode);

}