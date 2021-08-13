package com.modusbox.client.router;

import com.modusbox.client.exception.RouteExceptionHandlingConfigurer;
import com.modusbox.client.processor.EncodeAuthHeader;
import com.modusbox.client.processor.TrimMFICode;
import io.prometheus.client.Counter;
import io.prometheus.client.Histogram;
import org.apache.camel.Exchange;
import org.apache.camel.builder.RouteBuilder;


public class PartiesRouter extends RouteBuilder {

    public static final Counter reqCounter = Counter.build()
            .name("counter_get_parties_requests_total")
            .help("Total requests for GET /parties.")
            .register();
    private static final String TIMER_NAME = "histogram_get_parties_timer";
    private static final Histogram reqLatency = Histogram.build()
            .name("histogram_get_parties_request_latency")
            .help("Request latency in seconds for GET /parties.")
            .register();

    private final EncodeAuthHeader encodeAuthHeader = new EncodeAuthHeader();
    private final TrimMFICode trimMFICode = new TrimMFICode();
    private final RouteExceptionHandlingConfigurer exceptionHandlingConfigurer = new RouteExceptionHandlingConfigurer();

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
        from("direct:getParties").routeId("com.modusbox.getParties").doTry()
                .process(exchange -> {
                    reqCounter.inc(1); // increment Prometheus Counter metric
                    exchange.setProperty(TIMER_NAME, reqLatency.startTimer()); // initiate Prometheus Histogram metric
                })
                /*
                 * BEGIN processing
                 */
                .to("bean:customJsonMessage?method=logJsonMessage('info', ${header.X-CorrelationId}, " +
                        "'Request received, GET /parties/${header.idType}/${header.idValue}', " +
                        "null, null, 'fspiop-source: ${header.fspiop-source}')")
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
                .transform(datasonnet("resource:classpath:mappings/getPartiesResponse.ds"))
                .setBody(simple("${body.content}"))
                .marshal().json()
                .removeHeaders("getLoanByIdResponse")
                .removeHeaders("getLoanScheduleByIdResponse")
                .removeHeaders("getClientByIdResponse")
                .to("bean:customJsonMessage?method=logJsonMessage('info', ${header.X-CorrelationId}, " +
                        "'Final Response: ${body}', " +
                        "null, null, 'Response of GET parties/${header.idType}/${header.idValue} API')")
                /*
                 * END processing
                 */
                .doFinally().process(exchange -> {
                    ((Histogram.Timer) exchange.getProperty(TIMER_NAME)).observeDuration(); // stop Prometheus Histogram metric
                }).end()

        ;

        // API call to DFSP for loan information by ID
        from("direct:getLoanById")
                .routeId("getLoanById")
                .log("Get loan account by ID route called")
                .setBody(simple("{}"))

                .removeHeaders("CamelHttp*")
                .setHeader("Content-Type", constant("application/json"))
                .setHeader("Accept", constant("application/json"))
                .setHeader(Exchange.HTTP_METHOD, constant("GET"))
                .setProperty("authHeader", simple("${properties:dfsp.username}:${properties:dfsp.password}"))
                .process(encodeAuthHeader)
                .to("bean:customJsonMessage?method=logJsonMessage('info', ${header.X-CorrelationId}, " +
                        "'Calling DFSP API, getLoanById', " +
                        "'Tracking the request', 'Track the response', " +
                        "'Request sent to, GET https://{{dfsp.host}}/loans/${header.idValueTrimmed}')")
                .toD("https://{{dfsp.host}}/loans/${header.idValueTrimmed}")

                .unmarshal().json()
                .to("bean:customJsonMessage?method=logJsonMessage('info', ${header.X-CorrelationId}, " +
                        "'Response from DFSP API, getLoanById: ${body}', " +
                        "'Tracking the response', 'Verify the response', null)")
                // Save response as property to use later
                .setProperty("getLoanByIdResponse", body())
        ;

        // API call to DFSP for loan information by ID
        from("direct:getLoanScheduleById")
                .routeId("getLoanScheduleById")
                .log("Get loan account schedule by ID route called")
                .setBody(simple("{}"))

                .removeHeaders("CamelHttp*")
                .setHeader("Content-Type", constant("application/json"))
                .setHeader(Exchange.HTTP_METHOD, constant("GET"))
                .setProperty("authHeader", simple("${properties:dfsp.username}:${properties:dfsp.password}"))
                .process(encodeAuthHeader)
                .to("bean:customJsonMessage?method=logJsonMessage('info', ${header.X-CorrelationId}, " +
                        "'Calling DFSP API, getLoanScheduleById', " +
                        "'Tracking the request', 'Track the response', " +
                        "'Request sent to, GET https://{{dfsp.host}}/loans/${header.idValueTrimmed}/schedule')")
                .toD("https://{{dfsp.host}}/loans/${header.idValueTrimmed}/schedule")

                .unmarshal().json()
                .to("bean:customJsonMessage?method=logJsonMessage('info', ${header.X-CorrelationId}, " +
                        "'Response from DFSP API, getLoanScheduleById: ${body}', " +
                        "'Tracking the response', 'Verify the response', null)")
                // Save response as property to use later
                .setProperty("getLoanScheduleByIdResponse", body())
        ;

        // API call to DFSP for client information by ID
        from("direct:getClientById")
                .routeId("getClientById")
                .log("Get client by ID route called")
                .setBody(simple("{}"))

                .removeHeaders("CamelHttp*")
                .setHeader("Content-Type", constant("application/json"))
                .setHeader("Accept", constant("application/json"))
                .setHeader(Exchange.HTTP_METHOD, constant("GET"))
                .setProperty("authHeader", simple("${properties:dfsp.username}:${properties:dfsp.password}"))
                .process(encodeAuthHeader)
                //.toD("{{dfsp.host}}/clients/" + simple("${exchangeProperty.getLoanByIdResponse?.get('accountHolderKey')}").getText())
                .to("bean:customJsonMessage?method=logJsonMessage('info', ${header.X-CorrelationId}, " +
                        "'Calling DFSP API, getClientByLoanId', " +
                        "'Tracking the request', 'Track the response', " +
                        "'Request sent to, GET https://{{dfsp.host}}/clients/${exchangeProperty.getLoanByIdResponse?.get('accountHolderKey')}')")
                .toD("https://{{dfsp.host}}/clients/${exchangeProperty.getLoanByIdResponse?.get('accountHolderKey')}")
                .unmarshal().json()
                .to("bean:customJsonMessage?method=logJsonMessage('info', ${header.X-CorrelationId}, " +
                        "'Response from DFSP API, getClientByLoanId: ${body}', " +
                        "'Tracking the response', 'Verify the response', null)")
                // Save response as property to use later
                .setProperty("getClientByIdResponse", body())
        ;

		/*
		// API call to DFSP for fetching all loans for a client ID
		from("direct:getLoansForClient")
			.routeId("getLoansForClient")
			.log("Get Loans for a client ID route called")
			.setBody(simple("{}"))
			.removeHeaders("CamelHttp*")
			.setHeader("Content-Type", constant("application/json"))
			.setHeader("Accept", constant("application/json"))
			.setHeader(Exchange.HTTP_METHOD, constant("GET"))
			.setProperty("authHeader", simple("${properties:dfsp.username}:${properties:dfsp.password}"))
			.process(encodeAuthHeader)
			.toD("https://{{dfsp.host}}/clients/${header.idValue}/loans")
		;
		 */
    }
}