package com.modusbox.client.router;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.component.cxf.common.message.CxfConstants;

import java.util.Base64;
import java.util.LinkedList;

public class PartiesRouter extends RouteBuilder {

	private String host  = "https://mceasy.sandbox.mambu.com/api";
	private String username = "modusboxapi";
	private String password = "modusbox123";
	private String authorizationHeader = "Basic " + Base64.getEncoder().encodeToString((username + ":" + password).getBytes());

    public void configure() {

//			 Using Camel authentication query parameters
//			.toD(host + "/clients/${header.accountId}" +
//				"?authMethod=Basic&authUsername=" + username + "&authPassword=" + password); //.to() doesn't work here

//	        .setHeader(CxfConstants.OPERATION_NAME, constant("AccountLookup"))
//	        .to("direct:callT24EndPoint")

//			.unmarshal().json()

		// In this case the GET parties will return the loan account with client details
		from("direct:getParties")
			.log("Account lookup API called")
			.log("GET on /${header.idType}/${header.idValue} called")
			// First fetch the loan account by ID
			.to("direct:getLoanById")
			.process(new Processor() {
				public void process(Exchange exchange) throws Exception {
					System.out.println();
				}
			})
			// Now fetch the client information for the user the loan acc belongs to
			.to("direct:getClientById")
			.process(new Processor() {
				public void process(Exchange exchange) throws Exception {
					System.out.println();
				}
			})
			// Format the 2 responses
			.bean("getPartiesResponse")
        ;

		// API call to Mambu for loan information by ID
		from("direct:getLoanById")
			.log("Get loan account by ID route called")
			.setBody(simple("{}"))

			.removeHeaders("CamelHttp*")
			.setHeader("Content-Type", constant("application/json"))
			.setHeader("Accept", constant("application/json"))
			.setHeader(Exchange.HTTP_METHOD, constant("GET"))
			.setHeader("Authorization", constant(authorizationHeader))
			.toD(host + "/loans/${header.idValue}")

			.unmarshal().json()
			.setProperty("getLoanByIdResponse", body())
			.setHeader("getLoanByIdResponse", body())

//			.bean("getLoanByIdResponse")
		;


		// API call to Mambu for client information by ID
		from("direct:getClientById")
			.log("Get client by ID route called")
			.setBody(simple("{}"))

			.removeHeaders("CamelHttp*")
			.setHeader("Content-Type", constant("application/json"))
			.setHeader("Accept", constant("application/json"))
			.setHeader(Exchange.HTTP_METHOD, constant("GET"))
			.setHeader("Authorization", constant(authorizationHeader))
			.toD(host + "/clients/" + simple("${exchangeProperty.getLoanByIdResponse?.get('accountHolderKey')}").getText())
//			.toD(host + "/clients/" + exchange.getProperty("getLoanByIdResponse").get("accountHolderKey"))
//			.toD(host + "/clients/" + exchangeProperty("getLoanByIdResponse"))

			.unmarshal().json()
			.setProperty("getClientByIdResponse", body())
			.setHeader("getClientByIdResponse", body())
//			.bean("getClientByIdResponse")
		;


		// API call to Mambu for fetching all loans for a client ID
		from("direct:getLoansForClient")
			.log("Get Loans for a client ID route called")
			.setBody(simple("{}"))

			.removeHeaders("CamelHttp*")
			.setHeader("Content-Type", constant("application/json"))
			.setHeader("Accept", constant("application/json"))
			.setHeader(Exchange.HTTP_METHOD, constant("GET"))
			.setHeader("Authorization", constant(authorizationHeader))
			.toD(host + "/clients/${header.idValue}/loans")

//			.bean("getLoansForClientResponse")
		;
	}
}
