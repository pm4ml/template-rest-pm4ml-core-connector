package com.modusbox.client.router;

import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.component.cxf.common.message.CxfConstants;

public class QuotesRouter extends RouteBuilder {

    public void configure() {

        from("direct:postQuoterequests")
    	        .log("POST Quotes API called")
    	        .setProperty("origPayload", simple("${body}"))
    	        
    	        .bean("postQuoterequestsRequest")
    	        
    	        .setHeader(CxfConstants.OPERATION_NAME, constant("CalculateFees"))
    	        .to("direct:callT24EndPoint")
    	        
    	        .setBody(simple("${exchangeProperty.origPayload}")) //Need to know how this exchange property can be accessed in datasonnet file.
            .bean("postQuoterequestsResponse");

    }
}
