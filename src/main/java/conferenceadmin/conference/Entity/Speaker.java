package conferenceadmin.conference.Entity;

import jakarta.persistence.*;

@Entity
@Table(name = "speakers")
public class Speaker {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    private String university;

    private String conferencecode;

    private String imagePath;

    public Speaker() {
    }

    public Speaker(String name, String university, String conferencecode, String imagePath) {
        this.name = name;
        this.university = university;
        this.conferencecode = conferencecode;
        this.imagePath = imagePath;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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

    public String getConferencecode() {
        return conferencecode;
    }

    public void setConferencecode(String conferencecode) {
        this.conferencecode = conferencecode;
    }

    public String getImagePath() {
        return imagePath;
    }

    public void setImagePath(String imagePath) {
        this.imagePath = imagePath;
    }
}
