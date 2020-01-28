package dtu.services.rest.resources;

import core.DataVerification;
import core.DtuPay;
import core.Utilities;
import dtupay.*;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Path("/api/dtupay")
public class DtuPayResource
{
    @POST
    @Path("/pay")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response pay(PaymentRequest request) throws Exception
    {
        DtuPayMerchantRepresentation merchant = DataVerification.verifyMerchant(request.getMerchantUuid());
        DtuPayCustomerRepresentation customer = DataVerification.verifyCustomer(request.getCustomerCpr());

        if (merchant != null && customer != null
                && DataVerification.verifyToken(request.getCustomerCpr(), request.getToken()))
        {
            BigDecimal bigAmount = BigDecimal.valueOf(request.getAmount());
            if (DtuPay.pay(customer, merchant, request.getToken(), bigAmount, request.getDescription()))
            {
                return Response.status(Response.Status.OK).entity(true).build();
            }
        }
        return Response.status(Response.Status.CONFLICT).entity(false).build();
    }

    @POST
    @Path("/refund")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response refund(PaymentRequest request) throws Exception
    {
        DtuPayMerchantRepresentation merchant = DataVerification.verifyMerchant(request.getMerchantUuid());
        DtuPayCustomerRepresentation customer = DataVerification.verifyCustomer(request.getCustomerCpr());

        if (merchant != null && customer != null
                && DataVerification.verifyToken(request.getCustomerCpr(), request.getToken()))
        {
            BigDecimal bigAmount = BigDecimal.valueOf(request.getAmount());
            if (DtuPay.refund(customer, merchant, request.getToken(), bigAmount, request.getDescription()))
            {
                return Response.status(Response.Status.OK).entity(true).build();
            }
        }
        return Response.status(Response.Status.CONFLICT).entity(false).build();
    }

    @POST
    @Path("/report")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response report(ReportRequest request) throws Exception
    {

        String userId = request.getUserId();

        Date rangeBegin = new SimpleDateFormat("yyyy-MM-DD").parse(request.getStartDate());
        Date rangeEnd = new SimpleDateFormat("yyyy-MM-DD").parse(request.getEndDate());

        if (DataVerification.verifyMerchant(userId) != null || DataVerification.verifyCustomer(userId) != null)
        {
            List<Payment> allTransactions = DtuPay.getTransactions().get(request.getUserId());
            TransactionReport transactionReport = new TransactionReport();
            for (Payment singlePayment : allTransactions)
            {
                List<Payment> paymentsInRange = new ArrayList<>();


                if(Utilities.isWithinRange(rangeBegin, rangeEnd, singlePayment.getDate())) {
                    transactionReport.addToReport("Price: " + singlePayment.getAmount() + " | Merchant: " + singlePayment.getMerchant().getName() + " | Customer: "
                                                    + singlePayment.getCustomer().getFirstName() + singlePayment.getCustomer().getLastName() + " | Token: " + singlePayment.getToken()
                                                    + " | Date: " + singlePayment.getDate());
                }
            }
            if (transactionReport.getNumberOfPayments() != 0)
            {
                return Response.status(Response.Status.OK).entity(transactionReport).build();
            }
            else
            {
                return Response.status(Response.Status.NO_CONTENT).entity(transactionReport).build();
            }
        }
        else
        {
            return Response.status(Response.Status.CONFLICT).build();
        }
    }
}
