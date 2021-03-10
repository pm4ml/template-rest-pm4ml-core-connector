package com.modusbox.client.jaxrs;

import com.modusbox.client.api.PartiesApi;
import com.modusbox.client.model.TransferPartyInbound;

import javax.validation.constraints.Size;

public class PartiesApiImpl implements PartiesApi {

    @Override
    public TransferPartyInbound getParties(String idType, @Size(min = 1, max = 128) String idValue) {
        return null;
    }
}
