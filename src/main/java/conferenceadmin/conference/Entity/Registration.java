// package conferenceadmin.conference.Entity;

// import jakarta.persistence.*;
// import lombok.AllArgsConstructor;
// import lombok.Data;
// import lombok.NoArgsConstructor;

// import java.time.LocalDateTime;

// @Entity
// @Table(name = "registrations")
// @Data
// @NoArgsConstructor
// @AllArgsConstructor
// public class Registration {

//     @Id
//     @GeneratedValue(strategy = GenerationType.IDENTITY)
//     private Long id;

//     private String title;
//     private String fullName;
//     private String phone;
//     private String email;
//     private String institute;
//     private String country;
//     private String registrationType;
//     private String presentationType;
//     private Integer numberOfNights;



//     private String referenceId; // IMPORTANT
//     private String paymentStatus; // PENDING / COMPLETED / FAILED

//     private LocalDateTime createdAt = LocalDateTime.now();
// }




package conferenceadmin.conference.Entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "registrations")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Registration {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;
    private String fullName;
    private String phone;
    private String email;
    private String institute;
    private String country;
    private String registrationType;
    private String presentationType;
    private Integer numberOfNights;

    // 🔑 Whop mapping key
    @Column(unique = true)
    private String stateId;

    // PENDING / COMPLETED / FAILED
    private String paymentStatus;

    private LocalDateTime createdAt = LocalDateTime.now();
}
