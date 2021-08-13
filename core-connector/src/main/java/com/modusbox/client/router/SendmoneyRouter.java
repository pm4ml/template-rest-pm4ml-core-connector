package com.modusbox.client.router;

import com.modusbox.client.exception.RouteExceptionHandlingConfigurer;
import io.prometheus.client.Counter;
import io.prometheus.client.Histogram;
import org.apache.camel.Exchange;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.model.dataformat.JsonLibrary;

public class SendmoneyRouter extends RouteBuilder {

    public static final Counter reqCounterPost = Counter.build()
            .name("counter_post_sendmoney_requests_total")
            .help("Total requests for POST /sendmoney.")
            .register();
    public static final Counter reqCounterPut = Counter.build()
            .name("counter_put_sendmoney_by_id_requests_total")
            .help("Total requests for PUT /sendmoney.")
            .register();
    private static final String TIMER_NAME_POST = "histogram_post_sendmoney_timer";
    private static final String TIMER_NAME_PUT = "histogram_put_sendmoney_by_id_timer";
    private static final Histogram reqLatencyPost = Histogram.build()
            .name("histogram_post_sendmoney_request_latency")
            .help("Request latency in seconds for POST /sendmoney.")
            .register();

    private static final Histogram reqLatencyPut = Histogram.build()
            .name("histogram_put_sendmoney_by_id_request_latency")
            .help("Request latency in seconds for PUT /sendmoney.")
            .register();

    private final RouteExceptionHandlingConfigurer exceptionHandlingConfigurer = new RouteExceptionHandlingConfigurer();

    public void configure() {
        // Add our global exception handling strategy
        exceptionHandlingConfigurer.configureExceptionHandling(this);

        from("direct:postSendmoney").routeId("com.modusbox.postSendmoney").doTry()
                .process(exchange -> {
                    reqCounterPost.inc(1); // increment Prometheus Counter metric
                    exchange.setProperty(TIMER_NAME_POST, reqLatencyPost.startTimer()); // initiate Prometheus Histogram metric
                })
                /*
                 * BEGIN processing
                 */
                .to("bean:customJsonMessage?method=logJsonMessage('info', ${header.X-CorrelationId}, " +
                        "'Request received, POST /sendmoney', " +
                        "null, null, 'Input Payload: ${body}')")
                .setProperty("origPayload", simple("${body}"))
                .removeHeaders("CamelHttp*")
                .setHeader(Exchange.HTTP_METHOD, constant("POST"))
                .setHeader("Content-Type", constant("application/json"))
                .to("bean:customJsonMessage?method=logJsonMessage('info', ${header.X-CorrelationId}, " +
                        "'Calling outbound API, postTransfers, " +
                        "POST {{ml-conn.outbound.host}}', " +
                        "'Tracking the request', 'Track the response', 'Input Payload: ${body}')")
                .marshal().json(JsonLibrary.Gson)
                .toD("{{ml-conn.outbound.host}}/transfers?bridgeEndpoint=true&throwExceptionOnFailure=false")
                .unmarshal().json(JsonLibrary.Gson)
                .to("bean:customJsonMessage?method=logJsonMessage('info', ${header.X-CorrelationId}, " +
                        "'Response from outbound API, postTransfers: ${body}', " +
                        "'Tracking the response', 'Verify the response', null)")
                /*
                 * END processing
                 */
                .doFinally().process(exchange -> {
                    ((Histogram.Timer) exchange.getProperty(TIMER_NAME_POST)).observeDuration(); // stop Prometheus Histogram metric
                }).end()
        ;

        from("direct:putSendmoneyById").routeId("com.modusbox.putSendmoneyById").doTry()
                .process(exchange -> {
                    reqCounterPut.inc(1); // increment Prometheus Counter metric
                    exchange.setProperty(TIMER_NAME_PUT, reqLatencyPut.startTimer()); // initiate Prometheus Histogram metric
                })
                /*
                 * BEGIN processing
                 */
                .to("bean:customJsonMessage?method=logJsonMessage('info', ${header.X-CorrelationId}, " +
                        "'Request received, PUT /sendmoney/${header.transferId}', " +
                        "null, null, 'Input Payload: ${body}')")
                .setProperty("origPayload", simple("${body}"))
                .removeHeaders("CamelHttp*")
                .setHeader(Exchange.HTTP_METHOD, constant("PUT"))
                .setHeader("Content-Type", constant("application/json"))
                .to("bean:customJsonMessage?method=logJsonMessage('info', ${header.X-CorrelationId}, " +
                        "'Calling outbound API, putTransfersById', " +
                        "'Tracking the request', 'Track the response', " +
                        "'Request sent to PUT https://{{ml-conn.outbound.host}}/transfers/${header.transferId}')")
                .marshal().json(JsonLibrary.Gson)
                .toD("{{ml-conn.outbound.host}}/transfers/${header.transferId}?bridgeEndpoint=true&throwExceptionOnFailure=false")
                .unmarshal().json(JsonLibrary.Gson)
                .to("bean:customJsonMessage?method=logJsonMessage('info', ${header.X-CorrelationId}, " +
                        "'Response from outbound API, putTransfersById: ${body}', " +
                        "'Tracking the response', 'Verify the response', null)")
                /*
                 * END processing
                 */
                .doFinally().process(exchange -> {
                    ((Histogram.Timer) exchange.getProperty(TIMER_NAME_PUT)).observeDuration(); // stop Prometheus Histogram metric
                }).end()
        ;
    }
}
