/**
 * Created by vaishnavi on 19/12/18.
 */

package com.customer.example;

import com.customer.example.model.Customer;
import com.customer.example.sdk.Customer_Data;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Map;

@RestController
@SpringBootApplication
public class Main {

    public static void main(String[] args) throws Exception {
        SpringApplication.run(Main.class, args);
    }

    @RequestMapping("/")
    String index() {
        return "Great, your backend is set up. Now you can configure the Customer authorisation example apps to point here.";
    }

    /**
     * This code simulates "loading the customer for your current session".
     * Your own logic will likely look very different.
     *
     * @return customer for your current session
     */
    Customer getAuthenticatedCustomer(String uid, HttpServletRequest request){
        Customer customer = new Customer(uid,
                "dev@customers.com",
                request.getRemoteAddr());
        return customer;
    }

    /**
     * This endpoint receives an uid and gives you all customer information.
     * Your own logic shouldn't Call customers on every request, instead, you should cache the Customer on your own servers.
     *
     * @param uid Customer identifier.
     *
     * @return a json with all the customer users
     */
    @RequestMapping(value = "/get-customer", method = RequestMethod.GET, produces = "application/json")
    String getCustomer(@RequestParam(value = "uid") String uid, HttpServletResponse response) {

        Map<String, String> mapResponse = Customer_Data.doGetRequest(Customer_Data.CUSTOMER_DEV_URL + "/v2/customer/list?uid="+uid);
        response.setStatus(Integer.parseInt(mapResponse.get(Customer_Data.RESPONSE_HTTP_CODE)));
        return mapResponse.get(Customer_Data.RESPONSE_JSON);
    }

    /**
     * This endpoint is used by Android/ios example app to create a user for a customer.
     *
     * @param uid Customer identifier. This is the identifier you use inside your application; you will receive it in notifications.
     * @param session_id  string used for fraud purposes.
     * @param token Customer token. This token is unique among all customers.
     * @param dev_reference Customer  reference.
     *
     * @return a json with the response
     */
    @RequestMapping(value = "/create-customer", method = RequestMethod.POST, produces = "application/json")
    String createCharge(@RequestParam(value = "uid") String uid,
                        @RequestParam(value = "session_id", required = false) String session_id,
                        @RequestParam(value = "token") String token,
                        @RequestParam(value = "users") double users,
                        @RequestParam(value = "dev_reference") String dev_reference,
                        HttpServletRequest request, HttpServletResponse response) {
        Customer customer = getAuthenticatedCustomer(uid, request);

        String jsonCustomers = Customer_Data.getCustomerJson(customer, session_id, token, users, dev_reference);

        Map<String, String> mapResponse = Customer_Data.doPostRequest(Customer_Data.CUSTOMER_DEV_URL + "/v2/customer/create", jsonCustomers);
        response.setStatus(Integer.parseInt(mapResponse.get(Customer_Data.RESPONSE_HTTP_CODE)));
        return mapResponse.get(Customer_Data.RESPONSE_JSON);
    }

    /**
     * This endpoint is used by Android/ios example app to delete a customer.
     *
     * @param uid Customer identifier. This is the identifier you use inside your application; you will receive it in notifications.
     * @param token customer identifier. This token is unique among all customers.
     *
     * @return a json with the response
     */
    @RequestMapping(value = "/delete-customer", method = RequestMethod.POST, produces = "application/json")
    String deleteCustomer(@RequestParam(value = "uid") String uid,
                          @RequestParam(value = "token") String token, HttpServletResponse response) {

        String jsonCustomerDelete = Customer_Data.deleteCustomerJson(uid, token);

        Map<String, String> mapResponse = Customer_Data.doPostRequest(Customer_Data.CUSTOMER_DEV_URL + "/v2/customer/delete", jsonCustomerDelete);
        response.setStatus(Integer.parseInt(mapResponse.get(Customer_Data.RESPONSE_HTTP_CODE)));
        return mapResponse.get(Customer_Data.RESPONSE_JSON);
    }



}
