import core.DtuPay;
import dtu.ws.fastmoney.User;
import dtupay.DtuPayCustomerRepresentation;
import dtupay.DtuPayMerchantRepresentation;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class TestDtuPay
{
    private DtuPayCustomerRepresentation customer =
            new DtuPayCustomerRepresentation("Test", "Customer", "080188-customer");

    private DtuPayMerchantRepresentation merchant =
            new DtuPayMerchantRepresentation("Test Merchant", "080188-merchant");

    private User bankCustomer = new User();
    private User bankMerchant = new User();

    @BeforeEach
    public void initiate() throws Exception
    {
        DtuPay.initialize();
        DtuPay.getTransactions().clear();
        bankCustomer.setFirstName(customer.getFirstName());
        bankCustomer.setLastName(customer.getLastName());
        bankCustomer.setCprNumber(customer.getCprNumber());
        bankMerchant.setFirstName(merchant.getName());
        bankMerchant.setLastName(merchant.getName());
        bankMerchant.setCprNumber(merchant.getUuid());
        String customerAccountId = DtuPay.getBank().createAccountWithBalance(bankCustomer, new BigDecimal(100));
        String merchantAccountId = DtuPay.getBank().createAccountWithBalance(bankMerchant, new BigDecimal(100));
        customer.setAccountId(customerAccountId);
        merchant.setAccountId(merchantAccountId);
    }

    @Test
    public void testPayment()
    {
        String token = UUID.randomUUID().toString();
        BigDecimal amount = new BigDecimal(50);
        try
        {
            boolean canPay = DtuPay.pay(customer, merchant, token, amount, "test payment");
            assertTrue(canPay);
        }
        catch (Exception e)
        {
            System.out.println(e.getMessage());
        }
    }

    @Test
    public void testRefund()
    {
        String token = UUID.randomUUID().toString();
        BigDecimal amount = new BigDecimal(50);
        try
        {
            boolean canRefund = DtuPay.refund(customer, merchant, token, amount, "test refund");
            assertTrue(canRefund);
        }
        catch (Exception e)
        {
            System.out.println(e.getMessage());
        }
    }

    @Test
    public void testNotEnoughMoney()
    {
        String token = UUID.randomUUID().toString();
        BigDecimal amount = new BigDecimal(200);
        try
        {
            boolean canPay = DtuPay.refund(customer, merchant, token, amount, "will fail");
            assertFalse(canPay);
        }
        catch (Exception e)
        {
            System.out.println(e.getMessage());
        }
    }

    @AfterEach
    public void deleteFromBank() throws Exception
    {
        if (customer.getAccountId() != null)
        {
            DtuPay.getBank().retireAccount(customer.getAccountId());
        }
        if (merchant.getAccountId() != null)
        {
            DtuPay.getBank().retireAccount(merchant.getAccountId());
        }
    }

}
