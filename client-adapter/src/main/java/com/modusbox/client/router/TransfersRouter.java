package com.modusbox.client.router;

import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.component.cxf.common.message.CxfConstants;

public class TransfersRouter extends RouteBuilder {

    public void configure() {

        from("direct:postTransfers")
                .log("POST transfers API called")
    	        
    	        .bean("postTransferMoneyRequest")
    	        
    	        .setHeader(CxfConstants.OPERATION_NAME, constant("TransferMoney"))
    	        .to("direct:callT24EndPoint")
    	        
            .bean("postTransfersResponse");

    }
}
