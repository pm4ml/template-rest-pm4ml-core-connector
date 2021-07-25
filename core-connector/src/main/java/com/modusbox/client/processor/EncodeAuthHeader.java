package com.modusbox.client.processor;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;

import java.util.Base64;

public class EncodeAuthHeader implements Processor {

    public void process(Exchange exchange) throws Exception {
        String s = exchange.getProperty("authHeader").toString();
        String authorizationHeader = "Basic " + Base64.getEncoder().encodeToString((s).getBytes());
        exchange.getIn().setHeader("Authorization", authorizationHeader);
    }

}
