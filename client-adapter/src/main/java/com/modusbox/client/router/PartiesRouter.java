package com.modusbox.client.router;

import com.modusbox.client.exception.RouteExceptionHandlingConfigurer;
import com.modusbox.client.processor.EncodeAuthHeader;
import com.modusbox.client.processor.TrimMFICode;
import org.apache.camel.Exchange;
import org.apache.camel.builder.RouteBuilder;


public class PartiesRouter extends RouteBuilder {

	private EncodeAuthHeader encodeAuthHeader = new EncodeAuthHeader();
	private TrimMFICode trimMFICode = new TrimMFICode();
	private RouteExceptionHandlingConfigurer exceptionHandlingConfigurer = new RouteExceptionHandlingConfigurer();

    public void configure() {

//			 Using Camel authentication query parameters
//			.toD(host + "/clients/${header.accountId}" +
//				"?authMethod=Basic&authUsername=" + username + "&authPassword=" + password); //.to() doesn't work here

//	        .setHeader(CxfConstants.OPERATION_NAME, constant("AccountLookup"))
//	        .to("direct:callT24EndPoint")

//			.unmarshal().json()
//			.process(new Processor() {
//				public void process(Exchange exchange) throws Exception {
//					System.out.println();
//				}
//			})
//			.process(exchange -> System.out.println())

		// Add our global exception handling strategy
		exceptionHandlingConfigurer.configureExceptionHandling(this);

		// In this case the GET parties will return the loan account with client details
		from("direct:getParties")
			.routeId("getParties")
			.log("Account lookup API called")
			.log("GET on /${header.idType}/${header.idValue} called")
			// Trim MFI code from id
			.process(trimMFICode)
			// Fetch the loan account by ID so we can find customer ID
			.to("direct:getLoanById")
			// Fetch the loan schedule account by ID to find latest installment which is due
			.to("direct:getLoanScheduleById")
			// Now fetch the client information for the user the loan acc belongs to and get name
			.to("direct:getClientById")
			// Format the 2 responses
			// Also save data as a header variable since the camel datasonnet processor imports JSON exchange properties only as string
			.setHeader("getLoanByIdResponse", exchangeProperty("getLoanByIdResponse"))
			.setHeader("getLoanScheduleByIdResponse", exchangeProperty("getLoanScheduleByIdResponse"))
			.setHeader("getClientByIdResponse", exchangeProperty("getClientByIdResponse"))
			.bean("getPartiesResponse")
			.removeHeaders("getLoanByIdResponse")
			.removeHeaders("getLoanScheduleByIdResponse")
			.removeHeaders("getClientByIdResponse")
        ;

		// API call to Mambu for loan information by ID
		from("direct:getLoanById")
			.routeId("getLoanById")
			.log("Get loan account by ID route called")
			.setBody(simple("{}"))

			.removeHeaders("CamelHttp*")
			.setHeader("Content-Type", constant("application/json"))
			.setHeader("Accept", constant("application/json"))
			.setHeader(Exchange.HTTP_METHOD, constant("GET"))
			.setProperty("authHeader", simple("${properties:easy.mambu.username}:${properties:easy.mambu.password}"))
			.process(encodeAuthHeader)
			.toD("https://{{easy.mambu.host}}/loans/${header.idValueTrimmed}")

			.unmarshal().json()
			// Save response as property to use later
			.setProperty("getLoanByIdResponse", body())
//			.bean("getLoanByIdResponse")
		;

		// API call to Mambu for loan information by ID
		from("direct:getLoanScheduleById")
				.routeId("getLoanScheduleById")
				.log("Get loan account schedule by ID route called")
				.setBody(simple("{}"))

				.removeHeaders("CamelHttp*")
				.setHeader("Content-Type", constant("application/json"))
				.setHeader("Accept", constant("application/vnd.mambu.v2+json"))
				.setHeader(Exchange.HTTP_METHOD, constant("GET"))
				.setProperty("authHeader", simple("${properties:easy.mambu.username}:${properties:easy.mambu.password}"))
				.process(encodeAuthHeader)
				.toD("https://{{easy.mambu.host}}/loans/${header.idValueTrimmed}/schedule")

				.unmarshal().json()
				// Save response as property to use later
				.setProperty("getLoanScheduleByIdResponse", body())
		;

		// API call to Mambu for client information by ID
		from("direct:getClientById")
			.routeId("getClientById")
			.log("Get client by ID route called")
			.setBody(simple("{}"))

			.removeHeaders("CamelHttp*")
			.setHeader("Content-Type", constant("application/json"))
			.setHeader("Accept", constant("application/json"))
			.setHeader(Exchange.HTTP_METHOD, constant("GET"))
			.setProperty("authHeader", simple("${properties:easy.mambu.username}:${properties:easy.mambu.password}"))
			.process(encodeAuthHeader)
//			.toD("{{easy.mambu.host}}/clients/" + simple("${exchangeProperty.getLoanByIdResponse?.get('accountHolderKey')}").getText())
			.toD("https://{{easy.mambu.host}}/clients/${exchangeProperty.getLoanByIdResponse?.get('accountHolderKey')}")
			.unmarshal().json()
			// Save response as property to use later
			.setProperty("getClientByIdResponse", body())
//			.bean("getClientByIdResponse")
		;

		/*
		// API call to Mambu for fetching all loans for a client ID
		from("direct:getLoansForClient")
			.routeId("getLoansForClient")
			.log("Get Loans for a client ID route called")
			.setBody(simple("{}"))

			.removeHeaders("CamelHttp*")
			.setHeader("Content-Type", constant("application/json"))
			.setHeader("Accept", constant("application/json"))
			.setHeader(Exchange.HTTP_METHOD, constant("GET"))
			.setProperty("authHeader", simple("${properties:easy.mambu.username}:${properties:easy.mambu.password}"))
			.process(encodeAuthHeader)
			.toD("https://{{easy.mambu.host}}/clients/${header.idValue}/loans")
//			.bean("getLoansForClientResponse")
		;
		 */
	}
}
