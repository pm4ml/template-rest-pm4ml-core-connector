//package com.modus.client;
//
//import com.modus.camel.datasonnet.DatasonnetProcessor;
//import com.temenos.t24.AccountLookupRequest;
//import org.apache.camel.Exchange;
//import org.apache.camel.Message;
//import org.junit.Test;
//import org.mockito.ArgumentCaptor;
//import org.mockito.Mockito;
//
//import java.util.HashMap;
//import java.util.Map;
//
//import static org.junit.Assert.assertEquals;
//
//public class DatasonnetMappingTests {
//
//    @Test
//    public void testPartiesMappingMonolithic() throws Exception {
//        DatasonnetProcessor datasonnetProcessor = new DatasonnetProcessor();
//        Exchange mockExchange = Mockito.mock(Exchange.class);
//        Message mockMessage = Mockito.mock(Message.class);
//
//        Map<String, Object> inputHeaders = new HashMap<String, Object>();
//        inputHeaders.put("idValue", "123");
//
//        String testRequest = "{}";
////        String expectedResult = "{\"type\":\"CONSUMER\",\"idType\":\"ACCOUNT_ID\",\"idValue\":\"123\"}";
//
//        Mockito.when(mockExchange.getMessage()).thenReturn(mockMessage);
//        Mockito.when(mockExchange.getIn()).thenReturn(mockMessage);
//        Mockito.when(mockMessage.getBody(Mockito.any())).thenReturn(testRequest);
//        Mockito.when(mockMessage.getHeaders()).thenReturn(inputHeaders);
//
//        datasonnetProcessor.setInputMimeType("application/json");
//        datasonnetProcessor.setOutputMimeType("application/java");
//        datasonnetProcessor.setDatasonnetFile("mappings/getPartiesRequest.ds");
//        datasonnetProcessor.init(); // This will take care of imports of libsonnet libraries from classpath
//
//        datasonnetProcessor.process(mockExchange);
//
//        ArgumentCaptor<Object> resultCaptor = ArgumentCaptor.forClass(Object.class);
//        Mockito.verify(mockMessage).setBody(resultCaptor.capture());
//
//        AccountLookupRequest response = (AccountLookupRequest) resultCaptor.getValue();
//        System.out.println(">>> Result of mapping is: " + response);
//
//        checkAssertionsAccountLookupRequest(response);
//    }
//
//    public void checkAssertionsAccountLookupRequest (AccountLookupRequest result) {
//        assertEquals("ACCOUNT.NO", result.getACCTVIEWType().getEnquiryInputCollection().getColumnName());
//        assertEquals("123", result.getACCTVIEWType().getEnquiryInputCollection().getCriteriaValue());
//        assertEquals("EQ", result.getACCTVIEWType().getEnquiryInputCollection().getOperand());
//
//        assertEquals("MB", result.getWebRequestCommon().getCompany());
//        assertEquals("Test", result.getWebRequestCommon().getPassword());
//        assertEquals("Test", result.getWebRequestCommon().getUserName());
//    }
//
//}
