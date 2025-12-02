package conferenceadmin.conference.Entity;

import jakarta.persistence.*;

@Entity
@Table(name = "universities")
public class roboticsUniversity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    private String imagePath;

    public roboticsUniversity() {
    }

    public roboticsUniversity(String name, String imagePath) {
        this.name = name;
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

    public String getImagePath() {
        return imagePath;
    }

    public void setImagePath(String imagePath) {
        this.imagePath = imagePath;
    }
}
