package core;

import dtupay.DtuPayCustomerRepresentation;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.UUID;

public class Utilities
{
    public static boolean isLessThanOrEqualTo(BigDecimal wantedAmount, BigDecimal givenAmount)
    {
        return wantedAmount.compareTo(givenAmount) < 0 || wantedAmount.compareTo(givenAmount) == 0;
    }

    public static Date getToday()
    {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        Date date = new Date();
        dateFormat.format(date);
        return date;
    }

    public static boolean isWithinRange(Date from, Date to, Date current) {
        return (current.after(from) && current.before(to));
    }

    public static boolean isDateValid(Date from, Date to, Date transactionTime)
    {
        return !transactionTime.before(from) && !transactionTime.after(to);
    }

    public static void printCustomerInformation(DtuPayCustomerRepresentation customer)
    {
        System.out.println("Customer information");
        System.out.println("====================");
        System.out.println("First name: " + customer.getFirstName());
        System.out.println("Last name:  " + customer.getLastName());
        System.out.println("CPR:        " + customer.getCprNumber());
    }

    public static UUID generateNewToken()
    {
        return UUID.randomUUID();
    }
}
