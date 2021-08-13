package com.modusbox.client.router;

import com.modusbox.client.processor.CustomErrorProcessor;
import org.apache.camel.builder.RouteBuilder;

public final class CustomErrorRouter extends RouteBuilder {
    private CustomErrorProcessor customErrorProcessor = new CustomErrorProcessor();

    public void configure() {

        from("direct:extractCustomErrors")
            .process(customErrorProcessor)
        ;
    }
}
