package com.modusbox.client.router;

import com.modusbox.client.exception.RouteExceptionHandlingConfigurer;
import com.modusbox.client.processor.EncodeAuthHeader;
import com.modusbox.client.processor.TrimMFICode;
import org.apache.camel.Exchange;
import org.apache.camel.builder.RouteBuilder;


public class TransfersRouter extends RouteBuilder {

    private final EncodeAuthHeader encodeAuthHeader = new EncodeAuthHeader();
    private final TrimMFICode trimMFICode = new TrimMFICode();
    private final RouteExceptionHandlingConfigurer exceptionHandlingConfigurer = new RouteExceptionHandlingConfigurer();

    public void configure() {
        // Add our global exception handling strategy
        exceptionHandlingConfigurer.configureExceptionHandling(this);

        from("direct:postTransfers")
                .routeId("com.modusbox.postTransfers")
                .to("bean:customJsonMessage?method=logJsonMessage('info', ${header.X-CorrelationId}, " +
                                                                "'Request received, POST /transfers', " +
                                                                "null, null, 'Input Payload: ${body}')")
            .setHeader("idType", simple("${body.getTo().getIdType()}"))
            .setHeader("idValue", simple("${body.getTo().getIdValue()}"))
            .process(trimMFICode)
            .setProperty("origPayload", simple("${body}"))

			.bean("postTransfersRequest")

            .removeHeaders("CamelHttp*")
            .setHeader("Content-Type", constant("application/json"))
            .setHeader("Accept", constant("application/vnd.mambu.v2+json"))
            .setHeader(Exchange.HTTP_METHOD, constant("POST"))
            .setProperty("authHeader", simple("${properties:easy.mambu.username}:${properties:easy.mambu.password}"))
            .process(encodeAuthHeader)
//            .toD("{{easy.mambu.host}}/loans/"+ simple("${exchangeProperty.origPayload?.getTo().getIdValue()}").getText() +"/repayment-transactions")
//            .toD("https://{{easy.mambu.host}}/loans/${exchangeProperty.origPayload?.getTo().getIdValue()}/repayment-transactions")
            .to("bean:customJsonMessage?method=logJsonMessage('info', ${header.X-CorrelationId}, " +
                                                                "'Calling Mambu API, getLoanRepaymentTransactions, " +
                                                                "POST https://{{easy.mambu.host}}/loans/${header.idValueTrimmed}/repayment-transactions', " +
                                                                "'Tracking the request', 'Track the response', 'Input Payload: ${body}')")
            .toD("https://{{easy.mambu.host}}/loans/${header.idValueTrimmed}/repayment-transactions")
            .to("bean:customJsonMessage?method=logJsonMessage('info', ${header.X-CorrelationId}, " +
                                                                "'Response from Mambu API, getLoanRepaymentTransactions: ${body}', " +
                                                                "'Tracking the response', 'Verify the response', null)")
            .bean("postTransfersResponse")
            .to("bean:customJsonMessage?method=logJsonMessage('info', ${header.X-CorrelationId}, " +
                                                                "'Final Response: ${body}', " +
                                                                "null, null, 'Response of POST /transfers API')")
        ;

    }
}
