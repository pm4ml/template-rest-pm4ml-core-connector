package com.modusbox.client.jaxrs;

import com.modusbox.client.api.SendmoneyApi;
import com.modusbox.client.model.TransferContinuationAccept;
import com.modusbox.client.model.TransferRequest;
import com.modusbox.client.model.TransferResponse;

import javax.validation.constraints.Pattern;

public class SendmoneyApiImpl implements SendmoneyApi {

    @Override
    public TransferResponse postSendmoney(TransferRequest transferRequest) {
        return null;
    }

    @Override
    public TransferResponse putSendmoneyById(@Pattern(regexp = "^[0-9a-f]{8}-[0-9a-f]{4}-[1-5][0-9a-f]{3}-[89ab][0-9a-f]{3}-[0-9a-f]{12}$") String transferId,TransferContinuationAccept transferContinuationAccept) {
        return null;
    }
}
