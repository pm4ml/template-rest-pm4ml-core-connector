package com.modusbox.client.exception;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.apache.camel.component.bean.validator.BeanValidationException;
import org.apache.camel.support.processor.validation.SchemaValidationException;
import org.apache.camel.http.base.HttpOperationFailedException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.net.SocketTimeoutException;

@Component("errorProcessor")
public class CamelErrorProcessor implements Processor {

    private static final Logger DEFAULT_LOGGER = LoggerFactory.getLogger(CamelErrorProcessor.class);

    private Logger log = DEFAULT_LOGGER;

    public Logger getLog() {
        return log;
    }

    public void setLog(Logger log) {
        this.log = log;
    }

    @Override
    public void process(Exchange exchange) throws Exception {

        String reasonText = "{ \"error\": \"Unknown\" }";
        int status = 500;

        // The exception may be in 1 of 2 places
        Exception exception = exchange.getException();
        if (exception == null) {
            exception = exchange.getProperty(Exchange.EXCEPTION_CAUGHT, Exception.class);
        }

        this.log.error("processing route exception", exception.getMessage());

        if (exception != null) {
            if (exception instanceof BeanValidationException) {
                // Bad Request
                status = 400;
                reasonText = "{ \"error\": \"Bad Request\" }";
            } else if (exception instanceof CustomBadRequestException) {
                // Bad Request
                status = 400;
                reasonText = exception.getMessage();
            } else if (exception instanceof SocketTimeoutException) {
                status = 408;
                reasonText = "{ \"error\": \"Time Out\" }";
            } else if (exception instanceof HttpOperationFailedException) {
                HttpOperationFailedException e = (HttpOperationFailedException) exception;
//                status = 500;
                status = e.getStatusCode();
                reasonText = "{ \"error\": \"Downstream request failed\", " +
                            "\"response_code\": " + e.getStatusCode() + "," +
                            "\"response_body\": " + e.getResponseBody() + "}";
            } else if (exception instanceof SchemaValidationException) {
                SchemaValidationException e = (SchemaValidationException) exception;
                status = 400;
                reasonText = e.getErrors().get(0).getMessage();
            } else if (exception instanceof IllegalArgumentException) {
                IllegalArgumentException e = (IllegalArgumentException) exception;
                if (e.getMessage().contains("Problem executing map")) {
                    status = 500;
                    reasonText = "{ \"error\": \"Datasonnet mapping failed\", " +
                            "\"response_body\": \"" + e.getMessage() + "\"}";
                }
            }
        }

        exchange.getMessage().setHeader(Exchange.HTTP_RESPONSE_CODE, status);
        exchange.getMessage().setHeader(Exchange.CONTENT_TYPE, "application/json");
        exchange.getMessage().setBody(reasonText);

    }
}
