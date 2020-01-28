package core;

import com.google.gson.Gson;
import dtupay.Config;
import dtupay.DtuPayCustomerRepresentation;
import dtupay.DtuPayMerchantRepresentation;
import dtupay.TokenVerify;

import java.util.HashMap;
import java.util.Map;

import static dtupay.Communication.sendRequest;

public class DataVerification
{
    //private static String HOST = "127.0.0.1";
    private static String HOST = "fastmoney-22.compute.dtu.dk";

    public static DtuPayMerchantRepresentation verifyMerchant(String uuid) throws Exception
    {
        String uri = String.format("http://%s:%d/api/merchant/get", HOST, Config.MERCHANT_PORT);
        Map<String, String> params = new HashMap<>();
        params.put("id", uuid);
        String response = sendRequest(uri, "GET", params);
        System.out.println(response);
        if (response != null && !response.isEmpty())
        {
            DtuPayMerchantRepresentation merchant = (new Gson()).fromJson(response, DtuPayMerchantRepresentation.class);
            if (merchant.getUuid().equals(uuid))
            {
                return merchant;
            }
        }
        return null;
    }

    public static DtuPayCustomerRepresentation verifyCustomer(String cpr) throws Exception
    {
        String uri = String.format("http://%s:%d/api/customer/get", HOST, Config.CUSTOMER_PORT);
        Map<String, String> params = new HashMap<>();
        params.put("id", cpr);
        String response = sendRequest(uri, "GET", params);
        System.out.println(response);
        if (response != null && !response.isEmpty())
        {
            DtuPayCustomerRepresentation customer = (new Gson()).fromJson(response, DtuPayCustomerRepresentation.class);
            if (customer.getCprNumber().equals(cpr))
            {
                return customer;
            }
        }
        return null;
    }

    public static boolean verifyToken(String cpr, String token) throws Exception
    {
        String uri = String.format("http://%s:%d/api/token/verify", HOST, Config.TOKEN_PORT);
        TokenVerify verify = new TokenVerify(cpr, token);
        String response = sendRequest(uri, "POST", verify);
        System.out.println(response);
        if (response != null && !response.isEmpty())
        {
            return (new Gson()).fromJson(response, boolean.class);
        }
        return false;

    }
}
