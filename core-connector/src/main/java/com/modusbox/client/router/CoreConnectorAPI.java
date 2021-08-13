package com.modusbox.client.router;

import com.modusbox.client.exception.CamelErrorProcessor;
import org.apache.camel.builder.RouteBuilder;

import javax.annotation.Generated;

/**
 * Generated from OpenApi specification by Camel REST DSL generator.
 */
@Generated("org.apache.camel.generator.openapi.PathGenerator")
public final class CoreConnectorAPI extends RouteBuilder {
    /**
     * Defines Apache Camel routes using REST DSL fluent API.
     */
    public void configure() {

//        restConfiguration().component("jetty").port(3000);

        CamelErrorProcessor errorProcessor = new CamelErrorProcessor();

        onException(Exception.class)
                //.logContinued(true).logRetryAttempted(true).logExhausted(true).logStackTrace(true)
                //.retryAttemptedLogLevel(LoggingLevel.INFO).retriesExhaustedLogLevel(LoggingLevel.INFO)
                //.maximumRedeliveries(3).redeliveryDelay(250).backOffMultiplier(2).useExponentialBackOff()

                .handled(true)
                .log("-- processing error")
                .process(errorProcessor)
                .log("-- error processing complete")
        ;

        from("cxfrs:bean:api-rs-server?bindingStyle=SimpleConsumer")
                .to("bean-validator://x")
                .toD("direct:${header.operationName}");

//        rest()
//                /*.get("/")
//                    .to("direct:rest1")
//                .get("/participants/{idType}/{idValue}")
//                    .produces("application/json,application/json,application/json")
//                    .param()
//                        .name("idType")
//                        .type(RestParamType.path)
//                        .required(true)
//                    .endParam()
//                    .param()
//                        .name("idValue")
//                        .type(RestParamType.path)
//                        .required(true)
//                    .endParam()
//                    .to("direct:rest2")*/
//                .get("/parties/{idType}/{idValue}")
//                .id("getParties")
//                .produces("application/json,application/json,application/json")
//                .param()
//                .name("idType")
//                .type(RestParamType.path)
//                .required(true)
//                .endParam()
//                .param()
//                .name("idValue")
//                .type(RestParamType.path)
//                .required(true)
//                .endParam()
//                .to("direct:getParties")
//                .post("/quoterequests")
//                .consumes("application/json")
//                .produces("application/json,application/json,application/json")
//                .param()
//                .name("body")
//                .type(RestParamType.body)
//                .required(true)
//                .description("Request for a transfer quotation")
//                .endParam()
//                .to("direct:postQuoterequests")
//                /*.post("/transactionrequests")
//                    .consumes("application/json")
//                    .produces("application/json,application/json,application/json")
//                    .param()
//                        .name("body")
//                        .type(RestParamType.body)
//                        .required(true)
//                        .description("Request for Transaction Request")
//                    .endParam()
//                    .to("direct:rest4")*/
//                .post("/transfers")
//                .consumes("application/json")
//                .produces("application/json,application/json,application/json")
//                .param()
//                .name("body")
//                .type(RestParamType.body)
//                .required(true)
//                .description("An incoming transfer request")
//                .endParam()
//                .to("direct:postTransfers")
//            /*.get("/otp/{requestToPayId}")
//                .produces("application/json,application/json,application/json")
//                .param()
//                    .name("requestToPayId")
//                    .type(RestParamType.path)
//                    .required(true)
//                .endParam()
//                .to("direct:rest6")*/;
    }
}
