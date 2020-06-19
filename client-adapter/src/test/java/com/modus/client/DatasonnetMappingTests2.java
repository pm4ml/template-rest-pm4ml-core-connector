package com.modus.client;

import com.datasonnet.Mapper;
import com.datasonnet.document.Document;
import com.datasonnet.document.StringDocument;
import org.apache.commons.io.IOUtils;
import org.json.JSONException;
import org.junit.Test;
import org.skyscreamer.jsonassert.JSONAssert;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

public class DatasonnetMappingTests2 {

    @Test
    public void testPartiesMappingCustom() throws IOException, JSONException {
        // This libsonnet is not needed for this mapping it's just to show how to import it
        Map<String, String> namedImports = new HashMap<String, String>();
//        namedImports.put("mappings/commonRequest.libsonnet", "{\n" +
//                "  getCommonRequest()::\n" +
//                "  {\n" +
//                "      company: \"MB\",\n" +
//                "      password: \"Test\",\n" +
//                "      userName: \"Test\"\n" +
//                "  }\n" +
//                "}");

        String mappingFilePath = "mappings/getPartiesResponse.ds";
        InputStream mappingStream = this.getClass().getClassLoader().getResourceAsStream(mappingFilePath);
        String mapping = IOUtils.toString(mappingStream);

        String headersJson = "{\"accountNo\":\"123\"}";
        StringDocument headersDocument = new StringDocument(headersJson, "application/json");

        Map<String, Document> jsonnetVars = new HashMap<String, Document>();
        jsonnetVars.put("headers", headersDocument);
        jsonnetVars.put("header", headersDocument);

        String inputData = "[{\"Status\":{\"transactionId\":null,\"messageId\":null,\"successIndicator\":\"Success\",\"application\":null},\"ACCTVIEWType\":{\"enquiryInputCollection\":null,\"gACCTVIEWDetailType\":{\"mACCTVIEWDetailType\":{\"ACCOUNTTITLE1\":\"Test Account Name\"}}}}]";
        StringDocument payload = new StringDocument(inputData, "application/json");

        Mapper mapper = new Mapper(mapping, jsonnetVars.keySet(), namedImports, true, true);
        Document mappedDoc = mapper.transform(payload, jsonnetVars, "application/json");
        Object mappedBody = mappedDoc.canGetContentsAs(String.class) ? mappedDoc.getContentsAsString() : mappedDoc.getContentsAsObject();

        String expectedOutput = "{\"type\":\"CONSUMER\",\"idType\":\"ACCOUNT_ID\",\"idValue\":\"123\",\"displayName\":\"Test Account Name\"}";
        JSONAssert.assertEquals(expectedOutput, mappedBody.toString(), true);
    }

}
