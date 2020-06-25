package com.modusbox.client.router;

import org.apache.camel.Exchange;
import org.apache.camel.builder.RouteBuilder;

public class QuotesRouter extends RouteBuilder {

    public void configure() {

        from("direct:postQuoterequests")
			.log("POST Quotes API called")
			.setProperty("origPayload", simple("${body}"))

			.setHeader(Exchange.HTTP_RESPONSE_CODE, constant(200))
			.setHeader(Exchange.CONTENT_TYPE, constant("application/json"))
			.bean("postQuoterequestsResponseMock")

//			.bean("postQuoterequestsRequest")

//			.setBody(simple("${exchangeProperty.origPayload}")) //Need to know how this exchange property can be accessed in datasonnet file.
//			.setHeader(Exchange.HTTP_RESPONSE_CODE, constant(501))
//			.setHeader(Exchange.CONTENT_TYPE, constant("application/json"))
//			.setBody(constant("NOT IMPLEMENTED"))

//          .bean("postQuoterequestsResponse")
		;
    }
}
