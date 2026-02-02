package conferenceadmin.conference.Entity;

import jakarta.persistence.*;

/**
 * Entity representing a robotics conference venue.
 */
@Entity
@Table(name = "venues")
public class roboticsVenue {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String venue;

    private String conferencecode;

    private String description;

    public roboticsVenue() {
    }

    public roboticsVenue(String venue, String conferencecode) {
        this.venue = venue;
        this.conferencecode = conferencecode;
    }

    public roboticsVenue(String venue, String conferencecode, String description) {
        this.venue = venue;
        this.conferencecode = conferencecode;
        this.description = description;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getVenue() {
        return venue;
    }

    public void setVenue(String venue) {
        this.venue = venue;
    }

    public String getConferencecode() {
        return conferencecode;
    }

    public void setConferencecode(String conferencecode) {
        this.conferencecode = conferencecode;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
