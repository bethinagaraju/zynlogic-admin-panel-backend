// // package conferenceadmin.conference.Controller;

// // import com.fasterxml.jackson.databind.JsonNode;
// // import com.fasterxml.jackson.databind.ObjectMapper;
// // import conferenceadmin.conference.Service.RegistrationService;
// // import org.springframework.beans.factory.annotation.Autowired;
// // import org.springframework.http.HttpStatus;
// // import org.springframework.http.ResponseEntity;
// // import org.springframework.web.bind.annotation.*;

// // @RestController
// // @RequestMapping("/webhook")
// // public class WebhookController {

// //     @Autowired
// //     private RegistrationService service;

// //     // Your actual secret from Whop settings
// //     private final String WEBHOOK_SECRET = "ws_11e19a31112a0fd80c1fa99f894e873d1afbbb9495a1ca6d753c784a3317354d"; 

// //     @PostMapping("/whop")
// //     public ResponseEntity<String> handleWhopWebhook(@RequestBody String rawPayload) {
// //         try {
// //             ObjectMapper mapper = new ObjectMapper();
// //             JsonNode root = mapper.readTree(rawPayload);

// //             // 1. Extract Event Type
// //             String action = root.path("action").asText(); 
            
// //             // 2. Extract Data
// //             JsonNode data = root.path("data");
            
// //             // Whop sends the customer email in the data object
// //             // We will use this to find the pending registration
// //             String email = data.path("email").asText();

// //             if (email == null || email.isEmpty()) {
// //                 System.out.println("⚠️ Webhook received without email.");
// //                 return ResponseEntity.ok("Ignored: No email found");
// //             }

// //             System.out.println("🔔 Webhook received: " + action + " for " + email);

// //             // 3. Handle Events
// //             switch (action) {
// //                 case "payment_succeeded":
// //                     service.updateStatusByEmail(email, "COMPLETED");
// //                     break;
                    
// //                 case "payment_failed":
// //                     service.updateStatusByEmail(email, "FAILED");
// //                     break;
                    
// //                 case "payment_created":
// //                     // Optional: Log that payment started
// //                     break;
                    
// //                 default:
// //                     // Ignore other events like 'membership_updated' etc.
// //                     return ResponseEntity.ok("Ignored event type");
// //             }

// //             return ResponseEntity.ok("Webhook processed successfully");

// //         } catch (Exception e) {
// //             e.printStackTrace();
// //             return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error processing webhook");
// //         }
// //     }
// // }





























// package conferenceadmin.conference.Controller;

// import com.fasterxml.jackson.databind.JsonNode;
// import com.fasterxml.jackson.databind.ObjectMapper;
// import conferenceadmin.conference.Service.RegistrationService;
// import org.springframework.beans.factory.annotation.Autowired;
// import org.springframework.http.HttpStatus;
// import org.springframework.http.ResponseEntity;
// import org.springframework.web.bind.annotation.*;

// @RestController
// @RequestMapping("/webhook")
// public class WebhookController {

//     @Autowired
//     private RegistrationService service;

//     // Your actual secret from Whop settings
//     private final String WEBHOOK_SECRET = "ws_11e19a31112a0fd80c1fa99f894e873d1afbbb9495a1ca6d753c784a3317354d";

//     @PostMapping("/whop")
//     public ResponseEntity<String> handleWhopWebhook(@RequestBody String rawPayload) {
//         try {
//             // 🔍 Log the raw payload to see exactly what Whop is sending
//             System.out.println("📩 Raw Webhook Payload: " + rawPayload);

//             ObjectMapper mapper = new ObjectMapper();
//             JsonNode root = mapper.readTree(rawPayload);

//             // 1. Extract Event Type
//             String action = root.path("action").asText();
            
//             // 2. Extract Data
//             JsonNode data = root.path("data");
            
//             // 3. Robust Email Extraction
//             // Whop payloads vary; we check all likely places for the email
//             String email = null;
            
//             if (data.has("email") && !data.get("email").isNull()) {
//                 email = data.get("email").asText();
//             } else if (data.has("user") && data.get("user").has("email")) {
//                 email = data.path("user").path("email").asText();
//             } else if (data.has("member") && data.get("member").has("email")) {
//                 email = data.path("member").path("email").asText();
//             } else if (data.has("payer_email")) {
//                 email = data.get("payer_email").asText();
//             }

//             // 4. Validate Email
//             if (email == null || email.isEmpty() || "null".equals(email)) {
//                 System.out.println("⚠️ Webhook received without valid email. Action: " + action);
//                 return ResponseEntity.ok("Ignored: No email found in payload");
//             }

//             System.out.println("🔔 Webhook Validated: " + action + " for " + email);

//             // 5. Handle Events
//             switch (action) {
//                 case "payment_succeeded":
//                     boolean updatedSuccess = service.updateStatusByEmail(email, "COMPLETED");
//                     if (!updatedSuccess) {
//                         System.out.println("❌ Could not update status. Email not found in DB: " + email);
//                     }
//                     break;
                    
//                 case "payment_failed":
//                     service.updateStatusByEmail(email, "FAILED");
//                     break;
                    
//                 case "payment_created":
//                     // Optional: Log that payment started
//                     System.out.println("ℹ️ Payment created for: " + email);
//                     break;
                    
//                 default:
//                     // Ignore other events like 'membership_updated' etc.
//                     return ResponseEntity.ok("Ignored event type: " + action);
//             }

//             return ResponseEntity.ok("Webhook processed successfully");

//         } catch (Exception e) {
//             e.printStackTrace();
//             return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error processing webhook");
//         }
//     }
// }





// package conferenceadmin.conference.Controller;

// import com.fasterxml.jackson.databind.JsonNode;
// import com.fasterxml.jackson.databind.ObjectMapper;
// import conferenceadmin.conference.Service.RegistrationService;
// import org.springframework.beans.factory.annotation.Autowired;
// import org.springframework.http.HttpStatus;
// import org.springframework.http.ResponseEntity;
// import org.springframework.web.bind.annotation.*;

// @RestController
// @RequestMapping("/webhook")
// public class WebhookController {

//     @Autowired
//     private RegistrationService service;

//     // Your actual secret from Whop settings
//     private final String WEBHOOK_SECRET = "ws_11e19a31112a0fd80c1fa99f894e873d1afbbb9495a1ca6d753c784a3317354d";

//     @PostMapping("/whop")

//     public ResponseEntity<String> handleWhopWebhook(@RequestBody String rawPayload) {
//         try {
//             System.out.println("📩 Raw Webhook Payload: " + rawPayload);

//             ObjectMapper mapper = new ObjectMapper();
//             JsonNode root = mapper.readTree(rawPayload);

//             // 🔴 FIX 1: Change "action" to "type"
//             String eventType = root.path("type").asText(); 
            
//             JsonNode data = root.path("data");
            
//             // Extract Email
//             String email = null;
//             if (data.has("email") && !data.get("email").isNull()) {
//                 email = data.get("email").asText();
//             } else if (data.has("user") && data.get("user").has("email")) {
//                 email = data.path("user").path("email").asText();
//             } else if (data.has("member") && data.get("member").has("email")) {
//                 email = data.path("member").path("email").asText();
//             } else if (data.has("payer_email")) {
//                 email = data.get("payer_email").asText();
//             }

//             if (email == null || email.isEmpty() || "null".equals(email)) {
//                 System.out.println("⚠️ Webhook received without valid email.");
//                 return ResponseEntity.ok("Ignored: No email found");
//             }

//             System.out.println("🔔 Webhook Validated: " + eventType + " for " + email);

//             // 🔴 FIX 2: Update cases to use dots (.) instead of underscores (_)
//             switch (eventType) {
//                 case "payment.succeeded":  // Changed from payment_succeeded
//                     boolean success = service.updateStatusByEmail(email, "COMPLETED");
//                     if (success) System.out.println("✅ DB Updated: " + email + " is now COMPLETED");
//                     else System.out.println("❌ DB Update Failed: Email " + email + " not found");
//                     break;
                    
//                 case "payment.failed":     // Changed from payment_failed
//                     service.updateStatusByEmail(email, "FAILED");
//                     break;
                    
//                 case "payment.created":    // Changed from payment_created
//                     System.out.println("ℹ️ Payment initiated for: " + email);
//                     break;
                    
//                 default:
//                     System.out.println("⚠️ Ignored event type: " + eventType);
//                     return ResponseEntity.ok("Ignored event type");
//             }

//             return ResponseEntity.ok("Webhook processed successfully");

//         } catch (Exception e) {
//             e.printStackTrace();
//             return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error processing webhook");
//         }
//     }
// }



package conferenceadmin.conference.Controller;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import conferenceadmin.conference.Service.RegistrationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/webhook")
public class WebhookController {

    @Autowired
    private RegistrationService service;

    // Your actual secret from Whop settings
    private final String WEBHOOK_SECRET = "ws_11e19a31112a0fd80c1fa99f894e873d1afbbb9495a1ca6d753c784a3317354d";

    @PostMapping("/whop")
    public ResponseEntity<String> handleWhopWebhook(@RequestBody String rawPayload) {
        try {
            System.out.println("📩 Raw Webhook Payload: " + rawPayload);

            ObjectMapper mapper = new ObjectMapper();
            JsonNode root = mapper.readTree(rawPayload);

            // 1. FIXED: Changed "action" to "type" to match Whop's payload
            String eventType = root.path("type").asText(); 
            
            JsonNode data = root.path("data");
            
            // 2. Extract Email (Checking multiple locations)
            String email = null;
            if (data.has("email") && !data.get("email").isNull()) {
                email = data.get("email").asText();
            } else if (data.has("user") && data.get("user").has("email")) {
                email = data.path("user").path("email").asText();
            } else if (data.has("member") && data.get("member").has("email")) {
                email = data.path("member").path("email").asText();
            } else if (data.has("payer_email")) {
                email = data.get("payer_email").asText();
            }

            if (email == null || email.isEmpty() || "null".equals(email)) {
                System.out.println("⚠️ Webhook received without valid email.");
                return ResponseEntity.ok("Ignored: No email found");
            }

            System.out.println("🔔 Webhook Validated: " + eventType + " for " + email);

            // 3. FIXED: Switch cases now use dots (.) instead of underscores (_)
            switch (eventType) {
                case "payment.succeeded":
                    boolean success = service.updateStatusByEmail(email, "COMPLETED");
                    if (success) {
                        System.out.println("✅ DB Updated: " + email + " is now COMPLETED");
                    } else {
                        System.out.println("❌ DB Update Failed: Email " + email + " not found in database");
                    }
                    break;
                    
                case "payment.failed":
                    service.updateStatusByEmail(email, "FAILED");
                    break;
                    
                case "payment.created":
                    System.out.println("ℹ️ Payment initiated for: " + email);
                    break;
                    
                default:
                    System.out.println("⚠️ Ignored event type: " + eventType);
                    return ResponseEntity.ok("Ignored event type");
            }

            return ResponseEntity.ok("Webhook processed successfully");

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error processing webhook");
        }
    }
}