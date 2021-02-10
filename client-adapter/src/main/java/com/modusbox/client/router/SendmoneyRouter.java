package com.modusbox.client.router;

import org.apache.camel.Exchange;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.model.dataformat.JsonLibrary;

public class SendmoneyRouter extends RouteBuilder {

    public void configure() {

        from("direct:postSendmoney")
            .routeId("com.modusbox.postSendmoney")
            .to("bean:customJsonMessage?method=logJsonMessage('info', ${header.X-CorrelationId}, " +
                    "'Request received, POST /sendmoney', " +
                    "null, null, 'Input Payload: ${body}')")
            .setProperty("origPayload", simple("${body}"))
            .removeHeaders("CamelHttp*")
            .setHeader(Exchange.HTTP_METHOD, constant("POST"))
            .setHeader("Content-Type", constant("application/json"))
            .to("bean:customJsonMessage?method=logJsonMessage('info', ${header.X-CorrelationId}, " +
                    "'Calling outbound API, postTransfers, " +
                    "POST {{outbound.endpoint}}', " +
                    "'Tracking the request', 'Track the response', 'Input Payload: ${body}')")
            .marshal().json(JsonLibrary.Gson)
            .toD("{{outbound.endpoint}}/transfers?bridgeEndpoint=true&throwExceptionOnFailure=false")
            .unmarshal().json(JsonLibrary.Gson)
            .to("bean:customJsonMessage?method=logJsonMessage('info', ${header.X-CorrelationId}, " +
                    "'Response from outbound API, postTransfers: ${body}', " +
                    "'Tracking the response', 'Verify the response', null)")
        ;

        from("direct:putSendmoneyById")
                .routeId("com.modusbox.putSendmoneyById")
                .to("bean:customJsonMessage?method=logJsonMessage('info', ${header.X-CorrelationId}, " +
                        "'Request received, PUT /sendmoney/${header.transferId}', " +
                        "null, null, 'Input Payload: ${body}')")
                .setProperty("origPayload", simple("${body}"))
                .removeHeaders("CamelHttp*")
                .setHeader(Exchange.HTTP_METHOD, constant("PUT"))
                .setHeader("Content-Type", constant("application/json"))
                .to("bean:customJsonMessage?method=logJsonMessage('info', ${header.X-CorrelationId}, " +
                        "'Calling outbound API, putTransfersById', " +
                        "'Tracking the request', 'Track the response', " +
                        "'Request sent to PUT https://{{outbound.endpoint}}/transfers/${header.transferId}')")
                .marshal().json(JsonLibrary.Gson)
                .toD("{{outbound.endpoint}}/transfers/${header.transferId}?bridgeEndpoint=true&throwExceptionOnFailure=false")
                .unmarshal().json(JsonLibrary.Gson)
                .to("bean:customJsonMessage?method=logJsonMessage('info', ${header.X-CorrelationId}, " +
                        "'Response from outbound API, putTransfersById: ${body}', " +
                        "'Tracking the response', 'Verify the response', null)")
        ;
    }
}
