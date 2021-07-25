//package com.modus.client;
//
//import com.datasonnet.Mapper;
//import com.datasonnet.document.Document;
//import com.datasonnet.document.StringDocument;
//import com.modus.camel.datasonnet.DatasonnetProcessor;
//import com.temenos.t24.ACCTVIEWType;
//import com.temenos.t24.AccountLookupRequest;
//import com.temenos.t24.EnquiryInputCollection;
//import com.temenos.t24.WebRequestCommon;
//import org.apache.camel.Exchange;
//import org.apache.camel.Message;
//import org.apache.commons.io.IOUtils;
//import org.junit.Before;
//import org.junit.Test;
//import org.mockito.ArgumentCaptor;
//import org.mockito.Mockito;
//
//import java.io.IOException;
//import java.io.InputStream;
//import java.util.HashMap;
//import java.util.Map;
//
//import static org.junit.Assert.assertEquals;
//
//public class DatasonnetMappingTestsAll {
//
//    private DatasonnetProcessor datasonnetProcessor;
//
//    private Exchange mockExchange;
//    private Message mockMessage;
//    private String testRequest;
//    private Map<String, Object> inputHeaders;
//
//    @Before
//    public void setup() throws Exception{
//        this.datasonnetProcessor = new DatasonnetProcessor();
//        this.mockExchange = Mockito.mock(Exchange.class);
//        this.mockMessage = Mockito.mock(Message.class);
////        this.testRequest = new Object();
////        this.testRequest = "{}";
////        this.mockMessage.setHeader("idValue", "123");
//
//        this.inputHeaders = new HashMap<String, Object>();
//
//
//        Mockito.when(this.mockExchange.getMessage()).thenReturn(this.mockMessage);
//        Mockito.when(this.mockExchange.getIn()).thenReturn(this.mockMessage);
//
////        Mockito.when(this.mockMessage.getBody()).thenReturn(this.testRequest);
////        Mockito.when(this.mockMessage.getBody(String.class)).thenReturn(this.testRequest);
////        Mockito.when(this.mockMessage.getMandatoryBody(String.class)).thenReturn(this.testRequest);
//    }
//
//    @Test
//    public void testPartiesMapping() throws Exception {
//        this.testRequest = "{}";
//        this.inputHeaders.put("idValue", "123");
//        Mockito.when(this.mockMessage.getBody(Mockito.any())).thenReturn(this.testRequest);
//        Mockito.when(this.mockMessage.getHeaders()).thenReturn(inputHeaders);
//
//        setupDatasonnetProcessor("application/json", "application/java", "mappings/getPartiesRequest.ds");
////        String expectedResult = "{\"type\":\"CONSUMER\",\"idType\":\"ACCOUNT_ID\",\"idValue\":\"123\"}";
//        executeDatasonnetProcessor(createGetPartiesExpectedResult());
//    }
//
//    public AccountLookupRequest createGetPartiesExpectedResult() {
//        AccountLookupRequest expectedResult = new AccountLookupRequest();
//        ACCTVIEWType expectedACCTVIEWType = new ACCTVIEWType();
//        WebRequestCommon expectedWebRequestCommon = new WebRequestCommon();
//        EnquiryInputCollection expectedEnquiryInputCollection = new EnquiryInputCollection();
//
//        expectedEnquiryInputCollection.setColumnName("ACCOUNT.NO");
//        expectedEnquiryInputCollection.setCriteriaValue("123");
//        expectedEnquiryInputCollection.setOperand("EQ");
//
//        expectedWebRequestCommon.setCompany("MB");
//        expectedWebRequestCommon.setPassword("Test");
//        expectedWebRequestCommon.setUserName("Test");
//
//        expectedACCTVIEWType.setEnquiryInputCollection(expectedEnquiryInputCollection);
//        expectedResult.setACCTVIEWType(expectedACCTVIEWType);
//        expectedResult.setWebRequestCommon(expectedWebRequestCommon);
//
//        return expectedResult;
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
//    @Test
//    public void initMappet() throws IOException {
//        Map<String, String> namedImports = new HashMap<String, String>();
//        namedImports.put("mappings/commonRequest.libsonnet", "{\n" +
//                "  getCommonRequest()::\n" +
//                "  {\n" +
//                "      company: \"MB\",\n" +
//                "      password: \"Test\",\n" +
//                "      userName: \"Test\"\n" +
//                "  }\n" +
//                "}");
//
//        String mappingFilePath = "mappings/getPartiesResponse.ds";
//        InputStream mappingStream = this.getClass().getClassLoader().getResourceAsStream(mappingFilePath);
//        String mapping = IOUtils.toString(mappingStream);
//
////        Map<String, Object> inputHeaders = new HashMap<String, Object>();
////        inputHeaders.put("idValue", "123");
//        String headersJson = "{\"accountNo\":\"123\"}";
//        StringDocument headersDocument = new StringDocument(headersJson, "application/json");
//
//        Map<String, Document> jsonnetVars = new HashMap<String, Document>();
//        jsonnetVars.put("headers", headersDocument);
//        jsonnetVars.put("header", headersDocument);
//
//        String inputData = "[{\"Status\":{\"transactionId\":null,\"messageId\":null,\"successIndicator\":\"Success\",\"application\":null},\"ACCTVIEWType\":{\"enquiryInputCollection\":null,\"gACCTVIEWDetailType\":{\"mACCTVIEWDetailType\":{\"ACCOUNTTITLE1\":\"Test Account Name\"}}}}]";
//        StringDocument payload = new StringDocument(inputData, "application/json");
//
//        Mapper mapper = new Mapper(mapping, jsonnetVars.keySet(), namedImports, true, true);
//        Document mappedDoc = mapper.transform(payload, jsonnetVars, "application/json");
//        Object mappedBody = mappedDoc.canGetContentsAs(String.class) ? mappedDoc.getContentsAsString() : mappedDoc.getContentsAsObject();
//
//        System.out.println();
////        JSONAssert.assertEquals();
////        return mappedBody;
//    }
//
//    @Test
//    public void testPartiesMappingMonolithic() throws Exception {
//        DatasonnetProcessor datasonnetProcessor = new DatasonnetProcessor();
//        Exchange mockExchange = Mockito.mock(Exchange.class);
//        Message mockMessage = Mockito.mock(Message.class);
//
//        // Mock this
//        // exchange.getMessage().getHeaders().entrySet().iterator()544
//        // or rather this: exchange.getMessage().getHeaders()
//        Map<String, Object> inputHeaders = new HashMap<String, Object>();
//        inputHeaders.put("idValue", "123");
//
////        mockMessage.setHeader("idValue", "123");
//        String testRequest = "{}";
////        String expectedResult = "{\"type\":\"CONSUMER\",\"idType\":\"ACCOUNT_ID\",\"idValue\":\"123\"}";
//
//        Mockito.when(mockExchange.getMessage()).thenReturn(mockMessage);
//        Mockito.when(mockExchange.getIn()).thenReturn(mockMessage);
//        Mockito.when(mockMessage.getHeaders()).thenReturn(inputHeaders);
//        Mockito.when(mockMessage.getBody(Mockito.any())).thenReturn(testRequest);
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
//
//        System.out.println(">>> Result of mapping is: " + response);
//
//        // Instead use getters to check individual values, or alternatively a .toString()
//        // or perhaps create an t24 object and compare it to that (but maybe they'll need a .equals method)
////        assertEquals(expectedResult, response);
//        checkAssertionsAccountLookupRequest(response);
//    }
//
////    @Test
////    public void testQuotesMapping() throws Exception {
////        setupDatasonnetProcessor("application/json", "application/json", "mappings/postQuoterequestsResponse.ds");
////        this.testRequest = "{}";
////        Mockito.when(this.mockMessage.getBody(Mockito.any())).thenReturn(this.testRequest);
////        String expectedResult = "{\"quoteId\":\"123\",\"transactionId\":\"456\",\"transferAmount\":\"10.00\",\"transferAmountCurrency\":\"USD\"}";
////        executeDatasonnetProcessor(expectedResult);
////    }
////
////    @Test
////    public void testTransfersMapping() throws Exception {
////        setupDatasonnetProcessor("application/json", "application/json", "mappings/postTransfersResponse.ds");
////        this.testRequest = "{}";
////        Mockito.when(this.mockMessage.getBody(Mockito.any())).thenReturn(this.testRequest);
////        String expectedResult = "{\"homeTransactionId\":\"123\"}";
////        executeDatasonnetProcessor(expectedResult);
////    }
//
//    // ---------------------------
//    // Helper classes
//    // ---------------------------
//    public void setupDatasonnetProcessor(String inputType, String outputType, String mappingFile){
//        this.datasonnetProcessor.setInputMimeType(inputType);
//        this.datasonnetProcessor.setOutputMimeType(outputType);
//        this.datasonnetProcessor.setDatasonnetFile(mappingFile);
//        this.datasonnetProcessor.init();
//    }
//
//    public void executeDatasonnetProcessor(Object expectedResult) throws Exception {
//        this.datasonnetProcessor.process(this.mockExchange);
//
//        ArgumentCaptor<Object> resultCaptor = ArgumentCaptor.forClass(Object.class);
//        Mockito.verify(this.mockMessage).setBody(resultCaptor.capture());
//
//        String response = resultCaptor.getValue().toString();
//        System.out.println(">>> Result of mapping is: " + response);
//
//        assertEquals(response, expectedResult);
//    }
//}
