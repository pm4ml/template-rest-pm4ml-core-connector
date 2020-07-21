package com.modusbox.client.processor;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;


public class TrimMFICode implements Processor {

    public void process(Exchange exchange) throws Exception {
        String idValueTrimmed = (String) exchange.getIn().getHeader("idValue");
        // MFI code
        String mfiCode = idValueTrimmed.substring(0, 3);
        exchange.getIn().setHeader("mfiCode", mfiCode);
        // Trim off first 3 chars
        idValueTrimmed = idValueTrimmed.substring(3);
        exchange.getIn().setHeader("idValueTrimmed", idValueTrimmed);
    }

}
