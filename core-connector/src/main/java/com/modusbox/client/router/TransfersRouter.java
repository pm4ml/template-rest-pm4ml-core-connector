package com.modusbox.client.router;

import com.modusbox.client.exception.RouteExceptionHandlingConfigurer;
import com.modusbox.client.processor.EncodeAuthHeader;
import com.modusbox.client.processor.TrimMFICode;
import io.prometheus.client.Counter;
import io.prometheus.client.Histogram;
import org.apache.camel.Exchange;
import org.apache.camel.builder.RouteBuilder;


public class TransfersRouter extends RouteBuilder {

    public static final Counter reqCounterPost = Counter.build()
            .name("counter_post_transfers_requests_total")
            .help("Total requests for POST /transfers.")
            .register();
    public static final Counter reqCounterPut = Counter.build()
            .name("counter_put_transfers_requests_total")
            .help("Total requests for PUT /transfers.")
            .register();
    private static final String TIMER_NAME_POST = "histogram_post_transfers_timer";
    private static final String TIMER_NAME_PUT = "histogram_put_transfers_timer";
    private static final Histogram reqLatencyPost = Histogram.build()
            .name("histogram_post_transfers_request_latency")
            .help("Request latency in seconds for POST /transfers.")
            .register();

    private static final Histogram reqLatencyPut = Histogram.build()
            .name("histogram_put_transfers_request_latency")
            .help("Request latency in seconds for PUT /transfers.")
            .register();

    private final EncodeAuthHeader encodeAuthHeader = new EncodeAuthHeader();
    private final TrimMFICode trimMFICode = new TrimMFICode();
    private final RouteExceptionHandlingConfigurer exceptionHandlingConfigurer = new RouteExceptionHandlingConfigurer();

    public void configure() {

        // Add our global exception handling strategy
        exceptionHandlingConfigurer.configureExceptionHandling(this);

        from("direct:postTransfers").routeId("com.modusbox.postTransfers").doTry()
                .process(exchange -> {
                    reqCounterPost.inc(1); // increment Prometheus Counter metric
                    exchange.setProperty(TIMER_NAME_POST, reqLatencyPost.startTimer()); // initiate Prometheus Histogram metric
                })
                /*
                 * BEGIN processing
                 */
                .to("bean:customJsonMessage?method=logJsonMessage('info', ${header.X-CorrelationId}, " +
                        "'Request received, POST /transfers', " +
                        "null, null, 'Input Payload: ${body}')")
                .setHeader("idType", simple("${body.getTo().getIdType()}"))
                .setHeader("idValue", simple("${body.getTo().getIdValue()}"))
                .process(trimMFICode)
                .setProperty("origPayload", simple("${body}"))
                .transform(datasonnet("resource:classpath:mappings/postTransfersRequest.ds"))
                .setBody(simple("${body.content}"))
                .marshal().json()
                .removeHeaders("CamelHttp*")
                .setHeader("Content-Type", constant("application/json"))
                .setHeader(Exchange.HTTP_METHOD, constant("POST"))
                .setProperty("authHeader", simple("${properties:dfsp.username}:${properties:dfsp.password}"))
                .process(encodeAuthHeader)
//            .toD("{{dfsp.host}}/loans/"+ simple("${exchangeProperty.origPayload?.getTo().getIdValue()}").getText() +"/repayment-transactions")
//            .toD("https://{{dfsp.host}}/loans/${exchangeProperty.origPayload?.getTo().getIdValue()}/repayment-transactions")
                .to("bean:customJsonMessage?method=logJsonMessage('info', ${header.X-CorrelationId}, " +
                        "'Calling DFSP API, getLoanRepaymentTransactions, " +
                        "POST https://{{dfsp.host}}/loans/${header.idValueTrimmed}/repayment-transactions', " +
                        "'Tracking the request', 'Track the response', 'Input Payload: ${body}')")
                .toD("https://{{dfsp.host}}/loans/${header.idValueTrimmed}/repayment-transactions")
                .to("bean:customJsonMessage?method=logJsonMessage('info', ${header.X-CorrelationId}, " +
                        "'Response from DFSP API, getLoanRepaymentTransactions: ${body}', " +
                        "'Tracking the response', 'Verify the response', null)")
                .transform(datasonnet("resource:classpath:mappings/postTransfersResponse.ds"))
                .setBody(simple("${body.content}"))
                .marshal().json()
                .to("bean:customJsonMessage?method=logJsonMessage('info', ${header.X-CorrelationId}, " +
                        "'Final Response: ${body}', " +
                        "null, null, 'Response of POST /transfers API')")
                /*
                 * END processing
                 */
                .doFinally().process(exchange -> {
                    ((Histogram.Timer) exchange.getProperty(TIMER_NAME_POST)).observeDuration(); // stop Prometheus Histogram metric
                }).end()
        ;

        from("direct:putTransfersByTransferId").routeId("com.modusbox.putTransfersByTransferId")
                .process(exchange -> {
                    reqCounterPut.inc(1); // increment Prometheus Counter metric
                    exchange.setProperty(TIMER_NAME_PUT, reqLatencyPut.startTimer()); // initiate Prometheus Histogram metric
                })
                .to("bean:customJsonMessage?method=logJsonMessage('info', ${header.X-CorrelationId}, " +
                        "'Request received, PUT /transfers/${header.transferId}', " +
                        "null, null, null)")
                /*
                 * BEGIN processing
                 */

                .setProperty("origPayload", simple("${body}"))
                .setHeader(Exchange.HTTP_RESPONSE_CODE, constant(200))
                .setHeader(Exchange.CONTENT_TYPE, constant("application/json"))

                // Set idType and idValue as headers
                .setHeader("idType", simple("${body.getQuote().getInternalRequest().getTo().getIdType().toString()}"))
                .setHeader("idValue", simple("${body.getQuote().getInternalRequest().getTo().getIdValue()}"))
                .to("direct:loginCitizens")

                .marshal().json()
                .transform(datasonnet("resource:classpath:mappings/putTransfersByTransferId.ds"))
                .setBody(simple("${body.content}"))
                .marshal().json()

                .removeHeaders("CamelHttp*")
                .setHeader(Exchange.HTTP_METHOD, constant("POST"))
                .setHeader("Content-Type", constant("application/json"))
                .setHeader("apikey", simple("${properties:dfsp.api-key}"))

                // Call login API which does deposit confirm run transfer and returns fee/commission
                .toD("{{dfsp.host}}/emoney/deposit/confirm/{{dfsp.api-version}}?bridgeEndpoint=true")
                .unmarshal().json()

//                .marshal().json()
//                .transform(datasonnet("resource:classpath:mappings/putTransfersResponse.ds"))
//                .setBody(simple("${body.content}"))
//                .marshal().json()

                .setHeader(Exchange.HTTP_RESPONSE_CODE, constant(200))
                .setBody(constant(""))

                /*
                 * END processing
                 */
                .to("bean:customJsonMessage?method=logJsonMessage('info', ${header.X-CorrelationId}, " +
                        "'Final Response: ${body}', " +
                        "null, null, 'Response of PUT /transfers/${header.transferId} API')")
                .process(exchange -> {
                    ((Histogram.Timer) exchange.getProperty(TIMER_NAME_PUT)).observeDuration(); // stop Prometheus Histogram metric
                }).end()
        ;

    }
}
