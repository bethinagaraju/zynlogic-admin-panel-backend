package conferenceadmin.conference.Entity;

import jakarta.persistence.*;

@Entity
@Table(name = "important_dates")
public class roboticsImportantDate {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String date;

    private String conferencecode;

    public roboticsImportantDate() {
    }

    public roboticsImportantDate(String date, String conferencecode) {
        this.date = date;
        this.conferencecode = conferencecode;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getConferencecode() {
        return conferencecode;
    }

    public void setConferencecode(String conferencecode) {
        this.conferencecode = conferencecode;
    }
}
