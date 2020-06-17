package com.modusbox.client.router;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.component.cxf.common.message.CxfConstants;

import java.util.Base64;

public class TransfersRouter extends RouteBuilder {

    private String host  = "https://mceasy.sandbox.mambu.com/api";
    private String username = "modusboxapi";
    private String password = "modusbox123";
    private String authorizationHeader = "Basic " + Base64.getEncoder().encodeToString((username + ":" + password).getBytes());

    public void configure() {

        from("direct:postTransfers")
            .log("Request transfer API called (loan repayment)")
            .log("POST on /transfers called")
//	        .setProperty("inboundIdType", header("idType"))
//	        .setProperty("inboundIdValue", header("idValue"))

			.bean("postTransfersRequest")

            .removeHeaders("CamelHttp*")
            .setHeader("Content-Type", constant("application/json"))
            .setHeader("Accept", constant("application/json"))
            .setHeader(Exchange.HTTP_METHOD, constant("POST"))
            .setHeader("Authorization", constant(authorizationHeader))
            .toD(host + "/loans/"+ simple("${body?.to?.idValue?}") +"/repayment-transactions/")

            // Breakpoint processor
//			.unmarshal().json()
                .process(new Processor() {
                    public void process(Exchange exchange) throws Exception {
                        System.out.println();
                    }
                })

            .bean("postTransfersResponse")
        ;

    }
}
