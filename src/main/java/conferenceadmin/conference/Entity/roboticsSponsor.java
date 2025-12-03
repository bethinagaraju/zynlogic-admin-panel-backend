package conferenceadmin.conference.Entity;

import jakarta.persistence.*;

@Entity
@Table(name = "sponsers")
public class roboticsSponsor {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    private String type;

    private String conferencecode;

    private String imagePath;

    public roboticsSponsor() {
    }

    public roboticsSponsor(String name, String type, String conferencecode, String imagePath) {
        this.name = name;
        this.type = type;
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

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
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
