// // package conferenceadmin.conference.Controller;

// // import conferenceadmin.conference.Service.RegistrationService;
// // import org.springframework.beans.factory.annotation.Autowired;
// // import org.springframework.web.bind.annotation.GetMapping;
// // import org.springframework.web.bind.annotation.RequestMapping;
// // import org.springframework.web.bind.annotation.RequestParam;
// // import org.springframework.web.bind.annotation.RestController;

// // import jakarta.servlet.http.HttpServletResponse;
// // import java.io.IOException;

// // @RestController
// // @RequestMapping("/payment")
// // public class WhopRedirectController {

// //     @Autowired
// //     private RegistrationService service;

// //     @GetMapping("/whop-redirect")
// //     public void whopRedirect(
// //             @RequestParam String ref,
// //             HttpServletResponse response
// //     ) throws IOException {

// //         service.updateStatus(ref, "COMPLETED");

// //         response.sendRedirect("http://localhost:5173/payment-success?ref=" + ref);
// //     }
// // }



// package conferenceadmin.conference.Controller;

// import conferenceadmin.conference.Service.RegistrationService;
// import jakarta.servlet.http.HttpServletResponse;
// import org.springframework.beans.factory.annotation.Autowired;
// import org.springframework.web.bind.annotation.*;

// import java.io.IOException;

// @RestController
// @RequestMapping("/payment")
// public class WhopRedirectController {

//     @Autowired
//     private RegistrationService service;

//     // @GetMapping("/whop-redirect")
//     // public void whopRedirect(
//     //         @RequestParam("state_id") String stateId,
//     //         @RequestParam(value = "status", required = false) String status,
//     //         HttpServletResponse response
//     // ) throws IOException {

//     //     if ("success".equalsIgnoreCase(status)) {
//     //         service.updateStatusByStateId(stateId, "COMPLETED");
//     //     } else {
//     //         service.updateStatusByStateId(stateId, "FAILED");
//     //     }

//     //     // Optional: redirect to frontend success page
//     //     response.sendRedirect(
//     //             "http://localhost:5173/payment-success"
//     //     );
//     // }
// // @GetMapping("/whop-redirect")
// // public void whopRedirect(
// //         @RequestParam("state_id") String stateId,
// //         @RequestParam(value = "status", required = false) String status,
// //         HttpServletResponse response
// // ) throws IOException {

// //     if ("success".equalsIgnoreCase(status)) {
// //         service.updateStatusByStateIdSafe(stateId, "COMPLETED");
// //     } else {
// //         service.updateStatusByStateIdSafe(stateId, "FAILED");
// //     }

// //     response.sendRedirect("http://localhost:5173/payment-success");
// // }




// @GetMapping("/whop-redirect")
// public void whopRedirect(
//         @RequestParam("state_id") String stateId,
//         @RequestParam("status") String status,
//         HttpServletResponse response
// ) throws IOException {

//     repo.findByStateId(stateId).ifPresent(reg -> {
//         reg.setPaymentStatus(
//             "success".equalsIgnoreCase(status) ? "COMPLETED" : "FAILED"
//         );
//         repo.save(reg);
//     });

//     response.sendRedirect("http://localhost:5173/payment-success");
// }




// }




package conferenceadmin.conference.Controller;

import conferenceadmin.conference.Service.RegistrationService;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;

@RestController
@RequestMapping("/payment")
public class WhopRedirectController {

    @Autowired
    private RegistrationService service;

    @GetMapping("/whop-redirect")
    public void whopRedirect(
            @RequestParam("state_id") String stateId,
            @RequestParam(value = "status", required = false) String status,
            HttpServletResponse response
    ) throws IOException {

        boolean updated = service.updateStatusByStateIdSafe(
                stateId,
                "success".equalsIgnoreCase(status) ? "COMPLETED" : "FAILED"
        );

        if (!updated) {
            System.out.println("⚠️ Unknown state_id received from Whop: " + stateId);
        }

        response.sendRedirect("http://localhost:5173/payment-success");
    }
}
