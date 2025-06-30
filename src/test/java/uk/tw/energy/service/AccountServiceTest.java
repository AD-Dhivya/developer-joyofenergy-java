package uk.tw.energy.service;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.HashMap;
import java.util.Map;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class AccountServiceTest {

    private static final String PRICE_PLAN_ID = "price-plan-id";
    private static final String SMART_METER_ID = "smart-meter-id";

    private AccountService accountService;

    @BeforeEach
    public void setUp() {
        Map<String, String> smartMeterToPricePlanAccounts = new HashMap<>();
        smartMeterToPricePlanAccounts.put(SMART_METER_ID, PRICE_PLAN_ID);

        accountService = new AccountService(smartMeterToPricePlanAccounts);
    }

    @Test
    public void givenTheSmartMeterIdReturnsThePricePlanId() throws Exception {
        assertThat(accountService.getPricePlanIdForSmartMeterId(SMART_METER_ID)).isEqualTo(PRICE_PLAN_ID);
    }

    @Test
    public void shouldReturnAllAccounts() {
        Map<String, String> accounts = accountService.getAllAccounts();
        assertThat(accounts).containsEntry(SMART_METER_ID, PRICE_PLAN_ID);
    }

    @Test
    public void shouldUpdatePricePlanForSmartMeter() {
        boolean updated = accountService.updatePricePlanForSmartMeter(SMART_METER_ID, "new-plan-id");
        assertThat(updated).isTrue();
        assertThat(accountService.getPricePlanIdForSmartMeterId(SMART_METER_ID)).isEqualTo("new-plan-id");
    }

    @Test
    public void shouldNotUpdatePricePlanForUnknownSmartMeter() {
        boolean updated = accountService.updatePricePlanForSmartMeter("unknown-meter", "plan-x");
        assertThat(updated).isFalse();
    }
}
