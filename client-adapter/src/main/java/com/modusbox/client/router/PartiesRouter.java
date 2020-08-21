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

			 /*Using Camel authentication query parameters
			.toD(host + "/clients/${header.accountId}" +
				"?authMethod=Basic&authUsername=" + username + "&authPassword=" + password); //.to() doesn't work here

	        .setHeader(CxfConstants.OPERATION_NAME, constant("AccountLookup"))
	        .to("direct:callT24EndPoint")

			.unmarshal().json()
			.process(new Processor() {
				public void process(Exchange exchange) throws Exception {
					System.out.println();
				}
			})*/

		// Add our global exception handling strategy
		exceptionHandlingConfigurer.configureExceptionHandling(this);

		// In this case the GET parties will return the loan account with client details
		from("direct:getParties")
				.to("bean:customJsonMessage?method=logJsonMessage('info', ${header.X-CorrelationId}, " +
						"'Request received, GET /parties/${header.idType}/${header.idValue}', " +
						"null, null, 'fspiop-source: ${header.fspiop-source}')")
				// Trim MFI code from id
				.process(trimMFICode)
				// First fetch the loan account by ID
				.to("direct:getLoanById")
				// Now fetch the client information for the user the loan acc belongs to
				.to("direct:getClientById")
				// Format the 2 responses
				.bean("getPartiesResponse")
				.to("bean:customJsonMessage?method=logJsonMessage('info', ${header.X-CorrelationId}, " +
						"'Final Response: ${body}', " +
						"null, null, 'Response of GET parties/${header.idType}/${header.idValue} API')")
		;

		// API call to Mambu for loan information by ID
		from("direct:getLoanById")
				.setBody(simple("{}"))

				.removeHeaders("CamelHttp*")
				.setHeader("Content-Type", constant("application/json"))
				.setHeader("Accept", constant("application/json"))
				.setHeader(Exchange.HTTP_METHOD, constant("GET"))
				.setProperty("authHeader", simple("${properties:easy.mambu.username}:${properties:easy.mambu.password}"))
				.process(encodeAuthHeader)
				.to("bean:customJsonMessage?method=logJsonMessage('info', ${header.X-CorrelationId}, " +
						"'Calling Mambu API, getLoanById', " +
						"'Tracking the request', 'Track the response', " +
						"'Request sent to, GET https://{{easy.mambu.host}}/loans/${header.idValueTrimmed}')")
				.toD("https://{{easy.mambu.host}}/loans/${header.idValueTrimmed}")

				.unmarshal().json()
				.to("bean:customJsonMessage?method=logJsonMessage('info', ${header.X-CorrelationId}, " +
						"'Response from Mambu API, getLoanById: ${body}', " +
						"'Tracking the response', 'Verify the response', null)")
				.setProperty("getLoanByIdResponse", body())
				.setHeader("getLoanByIdResponse", body())
//			.bean("getLoanByIdResponse")
		;


		// API call to Mambu for client information by ID
		from("direct:getClientById")
				.setBody(simple("{}"))

				.removeHeaders("CamelHttp*")
				.setHeader("Content-Type", constant("application/json"))
				.setHeader("Accept", constant("application/json"))
				.setHeader(Exchange.HTTP_METHOD, constant("GET"))
				.setProperty("authHeader", simple("${properties:easy.mambu.username}:${properties:easy.mambu.password}"))
				.process(encodeAuthHeader)
//			.toD("{{easy.mambu.host}}/clients/" + simple("${exchangeProperty.getLoanByIdResponse?.get('accountHolderKey')}").getText())
				.to("bean:customJsonMessage?method=logJsonMessage('info', ${header.X-CorrelationId}, " +
						"'Calling Mambu API, getClientByLoanId', " +
						"'Tracking the request', 'Track the response', " +
						"'Request sent to, GET https://{{easy.mambu.host}}/clients/${exchangeProperty.getLoanByIdResponse?.get('accountHolderKey')}')")
				.toD("https://{{easy.mambu.host}}/clients/${exchangeProperty.getLoanByIdResponse?.get('accountHolderKey')}")
				.unmarshal().json()
				.to("bean:customJsonMessage?method=logJsonMessage('info', ${header.X-CorrelationId}, " +
						"'Response from Mambu API, getClientByLoanId: ${body}', " +
						"'Tracking the response', 'Verify the response', null)")
				// Save response as property to use later
				.setProperty("getClientByIdResponse", body())
				// Also save it as a header variable since the camel datasonnet processor
				// imports JSON exchange properties only as string
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
				.setProperty("authHeader", simple("${properties:easy.mambu.username}:${properties:easy.mambu.password}"))
				.process(encodeAuthHeader)
				.to("bean:customJsonMessage?method=logJsonMessage('info', ${header.X-CorrelationId}, " +
						"'Calling Mambu API, getLoansForClient', " +
						"'Tracking the request', 'Track the response', " +
						"'Request sent to, GET https://{{easy.mambu.host}}/clients/${header.idValue}/loans')")
				.toD("https://{{easy.mambu.host}}/clients/${header.idValue}/loans")
				.unmarshal().json()
				.to("bean:customJsonMessage?method=logJsonMessage('info', ${header.X-CorrelationId}, " +
						"'Response from Mambu API, getLoansForClient: ${body}', " +
						"'Tracking the response', 'Verify the response', null)")
//			.bean("getLoansForClientResponse")
		;
	}
}
