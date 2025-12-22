
// // package conferenceadmin.conference.Service;

// // import conferenceadmin.conference.Entity.Registration;
// // import conferenceadmin.conference.Repository.RegistrationRepository;
// // import org.springframework.beans.factory.annotation.Autowired;
// // import org.springframework.stereotype.Service;

// // import java.util.UUID;

// // @Service
// // public class RegistrationService {

// //     @Autowired
// //     private RegistrationRepository repo;

// //     // Create registration
// //     public Registration create(Registration r) {
// //         r.setStateId(UUID.randomUUID().toString()); // 🔑 important
// //         r.setPaymentStatus("PENDING");
// //         return repo.save(r);
// //     }




// // public boolean updateStatusByStateIdSafe(String stateId, String status) {
// //     return repo.findByStateId(stateId).map(reg -> {
// //         reg.setPaymentStatus(status);
// //         repo.save(reg);
// //         return true;
// //     }).orElse(false);
// // }


// // }


// package conferenceadmin.conference.Service;

// import conferenceadmin.conference.Entity.Registration;
// import conferenceadmin.conference.Repository.RegistrationRepository;
// import org.springframework.beans.factory.annotation.Autowired;
// import org.springframework.stereotype.Service;
// import java.util.UUID;

// @Service
// public class RegistrationService {

//     @Autowired
//     private RegistrationRepository repo;

//     public Registration create(Registration r) {
//         r.setStateId(UUID.randomUUID().toString());
//         r.setPaymentStatus("PENDING");
//         return repo.save(r);
//     }

//     public boolean updateStatusByStateIdSafe(String stateId, String status) {
//         return repo.findByStateId(stateId).map(reg -> {
//             reg.setPaymentStatus(status);
//             repo.save(reg);
//             return true;
//         }).orElse(false);
//     }

//     // 🆕 Add this method for Webhook support
//     public boolean updateStatusByEmail(String email, String status) {
//         // This finds the registration by email. 
//         // Note: If a user registers multiple times, this might pick the first one found.
//         // For stricter logic, you might want to find the latest 'PENDING' one.
//         return repo.findByEmail(email).map(reg -> {
//             // Only update if it's not already completed to prevent overwrites
//             if (!"COMPLETED".equals(reg.getPaymentStatus())) {
//                 reg.setPaymentStatus(status);
//                 repo.save(reg);
//                 System.out.println("✅ Payment status updated to " + status + " for " + email);
//                 return true;
//             }
//             return false;
//         }).orElseGet(() -> {
//             System.out.println("❌ No registration found for email: " + email);
//             return false;
//         });
//     }

// }

package conferenceadmin.conference.Service;

import conferenceadmin.conference.Entity.Registration;
import conferenceadmin.conference.Repository.RegistrationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class RegistrationService {

    @Autowired
    private RegistrationRepository repo;

    // Create registration
    public Registration create(Registration r) {
        // Check if registration with this email already exists
        Optional<Registration> existing = repo.findByEmail(r.getEmail());
        if (existing.isPresent()) {
            Registration existingReg = existing.get();
            if ("COMPLETED".equals(existingReg.getPaymentStatus())) {
                // User already registered and paid, throw exception
                throw new RuntimeException("User already registered with this email");
            } else {
                // Update existing registration with new data, keep stateId
                existingReg.setPlanId(r.getPlanId());
                existingReg.setConferenceCode(r.getConferenceCode());
                existingReg.setTitle(r.getTitle());
                existingReg.setFullName(r.getFullName());
                existingReg.setPhone(r.getPhone());
                existingReg.setInstitute(r.getInstitute());
                existingReg.setCountry(r.getCountry());
                existingReg.setRegistrationType(r.getRegistrationType());
                existingReg.setPresentationType(r.getPresentationType());
                existingReg.setNumberOfNights(r.getNumberOfNights());
                existingReg.setNumberOfGuests(r.getNumberOfGuests());
                existingReg.setPaymentStatus("PENDING"); // Reset to pending for new attempt
                return repo.save(existingReg);
            }
        } else {
            // New registration
            r.setStateId(UUID.randomUUID().toString());
            r.setPaymentStatus("PENDING");
            return repo.save(r);
        }
    }

    // Get all registrations
    public List<Registration> getAllRegistrations() {
        return repo.findAll();
    }

    // ✅ THIS IS THE MISSING METHOD CAUSING THE ERROR
    public boolean updateStatusByEmail(String email, String status) {
        return repo.findByEmail(email).map(reg -> {
            // Only update if not already completed
            if (!"COMPLETED".equals(reg.getPaymentStatus())) {
                reg.setPaymentStatus(status);
                repo.save(reg);
                System.out.println("✅ Payment status updated to " + status + " for " + email);
                return true;
            }
            return false;
        }).orElseGet(() -> {
            System.out.println("❌ No registration found for email: " + email);
            return false;
        });
    }

    public boolean updateStatusByStateIdSafe(String stateId, String status) {
        return repo.findByStateId(stateId).map(reg -> {
            reg.setPaymentStatus(status);
            repo.save(reg);
            return true;
        }).orElse(false);
    }
}