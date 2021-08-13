package com.modusbox.client.router;

import com.modusbox.client.exception.RouteExceptionHandlingConfigurer;
import io.prometheus.client.Counter;
import io.prometheus.client.Histogram;
import org.apache.camel.Exchange;
import org.apache.camel.builder.RouteBuilder;

public class QuotesRouter extends RouteBuilder {

    public static final Counter reqCounter = Counter.build()
            .name("counter_post_quoterequests_requests_total")
            .help("Total requests for POST /quoterequests.")
            .register();
    private static final String TIMER_NAME = "histogram_post_quoterequests_timer";
    private static final Histogram reqLatency = Histogram.build()
            .name("histogram_post_quoterequests_request_latency")
            .help("Request latency in seconds for POST /quoterequests.")
            .register();

    private final RouteExceptionHandlingConfigurer exceptionHandlingConfigurer = new RouteExceptionHandlingConfigurer();

    public void configure() {
        // Add our global exception handling strategy
        exceptionHandlingConfigurer.configureExceptionHandling(this);

        from("direct:postQuoterequests").routeId("com.modusbox.postQuoterequests").doTry()
				.process(exchange -> {
					reqCounter.inc(1); // increment Prometheus Counter metric
					exchange.setProperty(TIMER_NAME, reqLatency.startTimer()); // initiate Prometheus Histogram metric
				})
				/*
				 * BEGIN processing
				 */
                .log("POST Quotes API called")
				.to("bean:customJsonMessage?method=logJsonMessage('info', ${header.X-CorrelationId}, " +
						"'Request received, POST /quoterequests', " +
						"null, null, 'Input Payload: ${body}')")
                .setProperty("origPayload", simple("${body}"))
                .setHeader(Exchange.HTTP_RESPONSE_CODE, constant(200))
                .setHeader(Exchange.CONTENT_TYPE, constant("application/json"))
				.transform(datasonnet("resource:classpath:mappings/postQuoterequestsResponseMock.ds"))
				.setBody(simple("${body.content}"))
				.marshal().json()
				.to("bean:customJsonMessage?method=logJsonMessage('info', ${header.X-CorrelationId}, " +
						"'Final Response: ${body}', " +
						"null, null, 'Response of POST /quoterequests API')")
				/*
				 * END processing
				 */
				.doFinally().process(exchange -> {
					((Histogram.Timer) exchange.getProperty(TIMER_NAME)).observeDuration(); // stop Prometheus Histogram metric
				}).end()
        ;
    }
}
