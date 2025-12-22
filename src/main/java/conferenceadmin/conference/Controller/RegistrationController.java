
// package conferenceadmin.conference.Controller;

// import conferenceadmin.conference.Entity.Registration;
// import conferenceadmin.conference.Service.RegistrationService;
// import org.springframework.beans.factory.annotation.Autowired;
// import org.springframework.web.bind.annotation.*;

// import java.util.HashMap;
// import java.util.Map;

// @RestController
// @RequestMapping("/api/registration")
// @CrossOrigin("*")
// public class RegistrationController {

//     @Autowired
//     private RegistrationService service;

//     @PostMapping("/create")
//     public Map<String, String> create(@RequestBody Registration reg) {

//         Registration saved = service.create(reg);

//         // 🔑 REAL Whop product / checkout URL
//         String whopCheckoutUrl =
//                 // "https://whop.com/clothing-dea5/qw-a4/?state_id=" + saved.getStateId();
//                 // "https://whop.com/checkout/plan_HcVxJS4fF80A2J?state_id=" + saved.getStateId();
//                 "https://whop.com/conference-55ba/test-68-f94b/?state_id=" + saved.getStateId();

//         Map<String, String> res = new HashMap<>();
//         res.put("stateId", saved.getStateId());
//         res.put("checkoutUrl", whopCheckoutUrl);

//         return res;
//     }
// }



// package conferenceadmin.conference.Controller;

// import conferenceadmin.conference.Entity.Registration;
// import conferenceadmin.conference.Service.RegistrationService;
// import org.springframework.beans.factory.annotation.Autowired;
// import org.springframework.http.ResponseEntity;
// import org.springframework.web.bind.annotation.*;

// import java.net.URLEncoder;
// import java.nio.charset.StandardCharsets;
// import java.util.HashMap;
// import java.util.Map;

// @RestController
// @RequestMapping("/api/registration")
// @CrossOrigin("*")
// public class RegistrationController {

//     @Autowired
//     private RegistrationService service;

//     @PostMapping("/create")
//     public ResponseEntity<Map<String, String>> createRegistration(@RequestBody Registration reg) {
        
//         // 1. Save user to DB (Status: PENDING)
//         Registration saved = service.create(reg);

//         // 2. Construct the Direct Checkout URL
//         // We use the specific URL you provided: plan_HcVxJS4fF80A2
//         String baseUrl = "https://whop.com/checkout/plan_HcVxJS4fF80A2";
        
//         // 3. Append parameters
//         // 'state_id' allow us to track this specific transaction if needed
//         // 'email' pre-fills the checkout form for the user
//         String whopCheckoutUrl = baseUrl 
//                 + "?state_id=" + saved.getStateId()
//                 + "&email=" + URLEncoder.encode(saved.getEmail(), StandardCharsets.UTF_8);

//         // 4. Return the URL to the frontend
//         Map<String, String> response = new HashMap<>();
//         response.put("message", "Registration initialized");
//         response.put("stateId", saved.getStateId());
//         response.put("checkoutUrl", whopCheckoutUrl);

//         return ResponseEntity.ok(response);
//     }
// }




package conferenceadmin.conference.Controller;

import conferenceadmin.conference.Entity.Registration;
import conferenceadmin.conference.Service.RegistrationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/registration")
@CrossOrigin("*")
public class RegistrationController {

    @Autowired
    private RegistrationService service;

    @PostMapping("/create")
    public ResponseEntity<Map<String, String>> createRegistration(@RequestBody Registration reg) {
        
        try {
            // 1. Save user to DB
            Registration saved = service.create(reg);

            // 2. Use the Plan ID sent from the frontend
            // If frontend didn't send one, fall back to a default or handle error
            String planId = saved.getPlanId();
            
            // if (planId == null || planId.isEmpty()) {
            //     // Optional: Default Plan ID if none provided
            //     planId = "plan_HcVxJS4fF80A2"; 
            // }

            // 3. Construct the Dynamic Checkout URL
            String baseUrl = "https://whop.com/checkout/" + planId;
            
            // 4. Append parameters
            String whopCheckoutUrl = baseUrl 
                    + "?state_id=" + saved.getStateId()
                    + "&email=" + URLEncoder.encode(saved.getEmail(), StandardCharsets.UTF_8);

            // 5. Return Response
            Map<String, String> response = new HashMap<>();
            response.put("message", "Registration initialized");
            response.put("stateId", saved.getStateId());
            response.put("checkoutUrl", whopCheckoutUrl);

            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(errorResponse);
        }
    }

    @GetMapping("/all")
    public ResponseEntity<List<Registration>> getAllRegistrations() {
        List<Registration> registrations = service.getAllRegistrations();
        return ResponseEntity.ok(registrations);
    }

    @GetMapping("/status/{email}")
    public ResponseEntity<Map<String, Object>> getPaymentStatusByEmail(@PathVariable String email) {
        Optional<String> paymentStatus = service.getPaymentStatusByEmail(email);
        
        Map<String, Object> response = new HashMap<>();
        if (paymentStatus.isPresent()) {
            response.put("email", email);
            response.put("paymentStatus", paymentStatus.get());
            return ResponseEntity.ok(response);
        } else {
            response.put("error", "No registration found for email: " + email);
            return ResponseEntity.notFound().build();
        }
    }
}