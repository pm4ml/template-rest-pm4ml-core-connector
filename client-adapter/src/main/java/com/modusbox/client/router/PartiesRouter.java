package com.modusbox.client.router;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.component.cxf.common.message.CxfConstants;

import java.util.Base64;

public class PartiesRouter extends RouteBuilder {

	private String host  = "https://mceasy.sandbox.mambu.com/api";
	private String username = "modusboxapi";
	private String password = "modusbox123";
	private String authorizationHeader = "Basic " + Base64.getEncoder().encodeToString((username + ":" + password).getBytes());

    public void configure() {

    	// In this case the GET parties will return the loan account
        from("direct:getParties")
	        .log("Account lookup API called (loan account)")
			.log("GET on /${header.idType}/${header.idValue} called")
//	        .setProperty("inboundIdType", header("idType"))
//	        .setProperty("inboundIdValue", header("idValue"))

			.setBody(simple("{}"))
//			.bean("getPartiesRequest")

			.removeHeaders("CamelHttp*")
			.setHeader("Content-Type", constant("application/json"))
			.setHeader("Accept", constant("application/json"))
			.setHeader(Exchange.HTTP_METHOD, constant("GET"))
			.setHeader("Authorization", constant(authorizationHeader))
			.toD(host + "/loans/${header.idValue}")
			// Using Camel authentication query parameters
//			.toD(host + "/clients/${header.accountId}" +
//				"?authMethod=Basic&authUsername=" + username + "&authPassword=" + password); //.to() doesn't work here

//	        .setHeader(CxfConstants.OPERATION_NAME, constant("AccountLookup"))
//	        .to("direct:callT24EndPoint")

			// Breakpoint processor
//			.unmarshal().json()
			.process(new Processor() {
				public void process(Exchange exchange) throws Exception {
					System.out.println();
				}
			})
	        .bean("getPartiesResponse")
		;


		// API call to Mambu for client information by ID
		from("direct:getClientById")
			.log("Get Client by ID route called")
			.setBody(simple("{}"))

			.removeHeaders("CamelHttp*")
			.setHeader("Content-Type", constant("application/json"))
			.setHeader("Accept", constant("application/json"))
			.setHeader(Exchange.HTTP_METHOD, constant("GET"))
			.setHeader("Authorization", constant(authorizationHeader))
			.toD(host + "/clients/${header.idValue}")

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
