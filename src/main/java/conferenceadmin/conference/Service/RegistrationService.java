
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

import java.util.UUID;

@Service
public class RegistrationService {

    @Autowired
    private RegistrationRepository repo;

    // Create registration
    public Registration create(Registration r) {
        r.setStateId(UUID.randomUUID().toString());
        r.setPaymentStatus("PENDING");
        return repo.save(r);
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