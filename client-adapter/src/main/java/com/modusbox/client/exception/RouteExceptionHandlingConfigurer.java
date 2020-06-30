package com.modusbox.client.exception;

import org.apache.camel.builder.RouteBuilder;

public class RouteExceptionHandlingConfigurer {
    private CamelErrorProcessor errorProcessor = new CamelErrorProcessor();

    public void configureExceptionHandling(RouteBuilder routeBuilder) {
        // NOTE: because "this" is not a RouteBuilder, short-cuts like "simple(...)" won't work here
        // However, routeBuilder.simple(...) works fine as an alternative
        routeBuilder.onException(Exception.class)
                .handled(true)
                .log("-- processing error")
                .process(errorProcessor)
                .log("-- error processing complete")
        ;
    }
}
