package com.modusbox.client.router;

import com.modusbox.client.exception.RouteExceptionHandlingConfigurer;
import io.prometheus.client.Counter;
import io.prometheus.client.Histogram;
import org.apache.camel.Exchange;
import org.apache.camel.builder.RouteBuilder;

public class HealthRouter extends RouteBuilder {

	private static final String TIMER_NAME = "histogram_get_health_timer";

	public static final Counter reqCounter = Counter.build()
		.name("counter_get_health_requests_total")
		.help("Total requests for GET /health.")
		.register();

	private static final Histogram reqLatency = Histogram.build()
		.name("histogram_get_health_request_latency")
		.help("Request latency in seconds for GET /health.")
		.register();

	private RouteExceptionHandlingConfigurer exceptionHandlingConfigurer = new RouteExceptionHandlingConfigurer();

    public void configure() {

		// Add our global exception handling strategy
		exceptionHandlingConfigurer.configureExceptionHandling(this);

        from("direct:getHealth").routeId("com.modusbox.getHealth").doTry()
			.process(exchange -> {
				reqCounter.inc(1); // increment Prometheus Counter metric
				exchange.setProperty(TIMER_NAME, reqLatency.startTimer()); // initiate Prometheus Histogram metric
			})
			/*
			 * BEGIN processing
			 */
			.setHeader(Exchange.HTTP_RESPONSE_CODE, constant(200))
			.setHeader(Exchange.CONTENT_TYPE, constant("application/json"))
			.setBody(constant("OK"))
			/*
			 * END processing
			 */
			.doFinally().process(exchange -> {
				((Histogram.Timer) exchange.getProperty(TIMER_NAME)).observeDuration(); // stop Prometheus Histogram metric
			}).end()
		;
    }
}
