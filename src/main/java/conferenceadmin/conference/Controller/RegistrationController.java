
package conferenceadmin.conference.Controller;

import conferenceadmin.conference.Entity.Registration;
import conferenceadmin.conference.Service.RegistrationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/registration")
@CrossOrigin("*")
public class RegistrationController {

    @Autowired
    private RegistrationService service;

    @PostMapping("/create")
    public Map<String, String> create(@RequestBody Registration reg) {

        Registration saved = service.create(reg);

        // 🔑 REAL Whop product / checkout URL
        String whopCheckoutUrl =
                // "https://whop.com/clothing-dea5/qw-a4/?state_id=" + saved.getStateId();
                // "https://whop.com/checkout/plan_HcVxJS4fF80A2J?state_id=" + saved.getStateId();
                "https://whop.com/conference-55ba/test-68-f94b/?state_id=" + saved.getStateId();

        Map<String, String> res = new HashMap<>();
        res.put("stateId", saved.getStateId());
        res.put("checkoutUrl", whopCheckoutUrl);

        return res;
    }
}
