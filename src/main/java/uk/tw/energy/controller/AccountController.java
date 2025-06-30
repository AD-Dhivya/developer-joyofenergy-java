package uk.tw.energy.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import uk.tw.energy.service.AccountService;

import java.util.Map;

@RestController
@RequestMapping("/accounts")
public class AccountController {
    private final AccountService accountService;

    public AccountController(AccountService accountService) {
        this.accountService = accountService;
    }

    @GetMapping("/{smartMeterId}/price-plan")
    public ResponseEntity<String> getPricePlanForSmartMeter(@PathVariable String smartMeterId) {
        String pricePlanId = accountService.getPricePlanIdForSmartMeterId(smartMeterId);
        if (pricePlanId == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(pricePlanId);
    }

    @GetMapping
    public ResponseEntity<Map<String, String>> getAllAccounts() {
        return ResponseEntity.ok(accountService.getAllAccounts());
    }

    @PutMapping("/{smartMeterId}/price-plan")
    public ResponseEntity<Void> updatePricePlanForSmartMeter(@PathVariable String smartMeterId,
                                                             @RequestBody Map<String, String> body) {
        String newPlanId = body.get("pricePlanId");
        boolean updated = accountService.updatePricePlanForSmartMeter(smartMeterId, newPlanId);
        if (!updated) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok().build();
    }
}
