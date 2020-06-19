package com.modusbox.client.exception;

import com.fasterxml.jackson.databind.exc.MismatchedInputException;
import com.fasterxml.jackson.databind.exc.UnrecognizedPropertyException;

import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

@Provider
public class CustomExceptionMapper implements ExceptionMapper<MismatchedInputException> {

    @Override
    public Response toResponse(MismatchedInputException exception) {
        return Response.status(Response.Status.BAD_REQUEST)
                .header("Conflict-Reason", exception.getMessage())
                .header("Content-Type", "application/json")
                .entity("{ \"error\": \"Bad Request\" }")
                .build();
    }

}
