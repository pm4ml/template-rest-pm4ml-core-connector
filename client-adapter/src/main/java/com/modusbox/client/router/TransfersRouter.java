package com.modusbox.client.router;

import com.modusbox.client.processor.EncodeAuthHeader;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.apache.camel.builder.RouteBuilder;


public class TransfersRouter extends RouteBuilder {

    private EncodeAuthHeader encodeAuthHeader = new EncodeAuthHeader();

    public void configure() {

        from("direct:postTransfers")
            .log("Request transfer API called (loan repayment)")
            .log("POST on /transfers called")
            .setProperty("origPayload", simple("${body}"))

			.bean("postTransfersRequest")

            .removeHeaders("CamelHttp*")
            .setHeader("Content-Type", constant("application/json"))
            .setHeader("Accept", constant("application/vnd.mambu.v2+json"))
            .setHeader(Exchange.HTTP_METHOD, constant("POST"))
            .setProperty("authHeader", simple("${properties:easy.mambu.username}:${properties:easy.mambu.password}"))
            .process(encodeAuthHeader)
//            .toD("{{easy.mambu.host}}/loans/"+ simple("${exchangeProperty.origPayload?.getTo().getIdValue()}").getText() +"/repayment-transactions")
            .toD("https://{{easy.mambu.host}}/loans/${exchangeProperty.origPayload?.getTo().getIdValue()}/repayment-transactions")

            .bean("postTransfersResponse")
        ;

    }
}
