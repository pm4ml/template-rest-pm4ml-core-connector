package com.modusbox.client.router;

import com.modusbox.client.exception.RouteExceptionHandlingConfigurer;
import com.modusbox.client.processor.EncodeAuthHeader;
import org.apache.camel.builder.RouteBuilder;

public class AuthRouter extends RouteBuilder {

    private RouteExceptionHandlingConfigurer exceptionHandlingConfigurer = new RouteExceptionHandlingConfigurer();
    private final EncodeAuthHeader encodeAuthHeader = new EncodeAuthHeader();

    public void configure() throws Exception {
        // Add our global exception handling strategy
        exceptionHandlingConfigurer.configureExceptionHandling(this);

        from("direct:getAuthHeader")
                .log("Prepare Downstream Call")
                .setProperty("authHeader", simple("{{dfsp.username}}:{{dfsp.password}}"))
                .process(encodeAuthHeader)
                .log("Get Auheader")
                .removeHeaders("CamelHttp*")
                .setHeader("Content-Type", constant("application/json"))
                .setHeader("X-Fineract-Platform-TenantId", constant("{{dfsp.tenant-id}}"))
                .setHeader("X-api-key",constant("{{dfsp.api-key}}"));


    }
}
