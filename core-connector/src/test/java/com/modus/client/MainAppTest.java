//package com.modus.client;
//
//import com.modus.camel.datasonnet.DatasonnetProcessor;
//import com.modusbox.client.router.PartiesRouter;
//import org.apache.camel.CamelContext;
//import org.apache.camel.Exchange;
//import org.apache.camel.Message;
//import org.apache.camel.ProducerTemplate;
//import org.apache.camel.builder.RouteBuilder;
//import org.apache.camel.support.DefaultExchange;
//import org.apache.camel.test.spring.CamelSpringBootRunner;
//import org.junit.Test;
//import org.junit.runner.RunWith;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.beans.factory.annotation.Qualifier;
//import org.springframework.boot.test.context.SpringBootTest;
//
//import static org.junit.Assert.assertEquals;
//
////import com.modusbox.client.Application;
//
//@RunWith(CamelSpringBootRunner.class)
//@SpringBootTest(classes = Application.class)
////@SpringBootTest(classes = Main.class)
//public class MainAppTest {
//
//    @Autowired
//    private CamelContext context;
//
//    @Autowired
//    private ProducerTemplate template;
//
//    @Autowired
//    @Qualifier("getPartiesResponse")
//    private DatasonnetProcessor mappingBean;
//
//    protected RouteBuilder createRouteBuilder() throws Exception {
//        return new PartiesRouter();
////        return new RouteBuilder() {
////            public void configure() throws Exception {
////                from("direct:getParties").to("mock:party");
////            }
////        };
//    }
//
//    protected void populateExchange(Exchange exchange) {
//        Message in = exchange.getIn();
////        in.setHeader("foo", "abc");
////        in.setHeader("bar", 123);
////        in.setBody("<hello id='m123'>world!</hello>");
//        in.setBody("{}");
////
////        exchange.setProperty("foobar", "cba");
//    }
//
//    @Test
//    public void testBean() throws Exception{
//        Exchange ex = new DefaultExchange(context);
//        populateExchange(ex);
//        mappingBean.process(ex);
//
//        String result = ex.getIn().getBody(String.class);
//        String expectedResult = "{\"type\":\"CONSUMER\",\"idType\":\"ACCOUNT_ID\",\"idValue\":\"123\"}";
//        System.out.println(">>>>>Result after mapping bean is done: " + result);
//        assertEquals(result, expectedResult);
//    }
//
////    @Test
////    public void testDatasonnet() throws Exception {
////        template.sendBody("direct:getParties", "test");
////        Thread.sleep(2000);
////        MockEndpoint quote = getMockEndpoint("mock:quote");
////        quote.expectedMessageCount(1);
////
////        context.bean
////
//////        template.sendBodyAndHeader("file://target/inbox", "Hello World", Exchange.FILE_NAME, "hello.txt");
//////        Thread.sleep(2000);
//////        File target = new File("target/outbox/hello.txt");
//////        assertTrue("File not moved", target.exists());
//////        String content = context.getTypeConverter().convertTo(String.class, target);
//////        assertEquals("Hello World", content);
////    }
//
//}
