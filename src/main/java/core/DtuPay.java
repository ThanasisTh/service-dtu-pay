package core;

import dtu.ws.fastmoney.BankService;
import dtu.ws.fastmoney.BankServiceService;
import dtupay.DtuPayCustomerRepresentation;
import dtupay.DtuPayMerchantRepresentation;
import dtupay.Payment;

import java.math.BigDecimal;
import java.util.*;

public class DtuPay
{
    private static BankService bank = null;
    private static Map<String, List<Payment>> transactions = new HashMap<>();

    public static void initialize() throws Exception
    {
        bank = new BankServiceService().getBankServicePort();
    }

    public static boolean pay(DtuPayCustomerRepresentation customer, DtuPayMerchantRepresentation merchant,
                              String token, BigDecimal amount, String description) throws Exception
    {

        if ((customer.getAccountId() != null) && (merchant.getAccountId() != null))
        {
            try
            {
                bank.transferMoneyFromTo(customer.getAccountId(), merchant.getAccountId(), amount, description);
            }
            catch (Exception e)
            {
                System.out.println(e.getMessage());
                return false;
            }
            Date date = Utilities.getToday();
            createReceipt(customer, merchant, token, amount, date);
            return true;
        }
        return false;
    }

    public static boolean refund(DtuPayCustomerRepresentation customer, DtuPayMerchantRepresentation merchant,
                                 String token, BigDecimal amount, String description) throws Exception
    {

        if ((customer.getAccountId() != null) && (merchant.getAccountId() != null))
        {
            try
            {
                bank.transferMoneyFromTo(merchant.getAccountId(), customer.getAccountId(), amount, description);
            }
            catch (Exception e)
            {
                System.out.println(e.getMessage());
                return false;
            }
            Date date = Utilities.getToday();
            createReceipt(customer, merchant, token, amount, date);
            return true;
        }
        return false;
    }

    private static void createReceipt(DtuPayCustomerRepresentation customer, DtuPayMerchantRepresentation merchant, String token, BigDecimal amount, Date date)
    {
        Payment customerReceipt = new Payment(customer, merchant, amount, token, date);
        Payment merchantReceipt = new Payment(merchant, amount, token, date);
        if (!transactions.containsKey(customer.getCprNumber()))
        {
            transactions.put(customer.getCprNumber(), new ArrayList<>());
            transactions.put(merchant.getUuid(), new ArrayList<>());
        }
        transactions.get(customer.getCprNumber()).add(customerReceipt);
        transactions.get(merchant.getUuid()).add(merchantReceipt);
    }

    public static BankService getBank()
    {
        return bank;
    }

    public void setBank(BankService bank)
    {
        DtuPay.bank = bank;
    }

    public static Map<String, List<Payment>> getTransactions()
    {
        return transactions;
    }

    public void setTransactions(HashMap<String, List<Payment>> transactions)
    {
        DtuPay.transactions = transactions;
    }
}
