package conferenceadmin.conference.Entity;

import jakarta.persistence.*;

/**
 * Entity representing a conference testimonial.
 */
@Entity
@Table(name = "testimonials")
public class Testimonials {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String imagePath;

    @Column(nullable = false)
    private String name;

    private String university;

    @Column(length = 1000)
    private String description;

    private Integer rating;

    private String conferencecode;

    public Testimonials() {
    }

    public Testimonials(String imagePath, String name, String university, String description, Integer rating, String conferencecode) {
        this.imagePath = imagePath;
        this.name = name;
        this.university = university;
        this.description = description;
        this.rating = rating;
        this.conferencecode = conferencecode;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getImagePath() {
        return imagePath;
    }

    public void setImagePath(String imagePath) {
        this.imagePath = imagePath;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUniversity() {
        return university;
    }

    public void setUniversity(String university) {
        this.university = university;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Integer getRating() {
        return rating;
    }

    public void setRating(Integer rating) {
        this.rating = rating;
    }

    public String getConferencecode() {
        return conferencecode;
    }

    public void setConferencecode(String conferencecode) {
        this.conferencecode = conferencecode;
    }
}