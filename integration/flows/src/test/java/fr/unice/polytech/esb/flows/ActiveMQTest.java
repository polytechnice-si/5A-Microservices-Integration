package fr.unice.polytech.esb.flows;

import fr.unice.polytech.esb.flows.utils.Endpoints;
import org.apache.activemq.broker.BrokerService;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.component.mock.MockEndpoint;
import org.apache.camel.test.junit4.CamelTestSupport;
import org.junit.AfterClass;
import org.junit.BeforeClass;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.HashMap;
import java.util.Map;

public abstract class ActiveMQTest  extends CamelTestSupport {

    /**
     * Handling ActiveMQ
     */

    private static BrokerService brokerSvc;
    @BeforeClass public static void setUpClass() throws Exception {
        brokerSvc = new BrokerService();
        brokerSvc.setBrokerName("TestBroker");
        brokerSvc.addConnector("tcp://localhost:61616");
        brokerSvc.start();
    }

    @AfterClass public static void tearDownClass() throws Exception { brokerSvc.stop(); }


    @Override protected RouteBuilder createRouteBuilder() throws Exception {
        DeathPool deathPool = new DeathPool();
        return new RouteBuilder() {
            @Override public void configure() throws Exception {
                this.includeRoutes(deathPool);
                this.setErrorHandlerBuilder(deathPool.getErrorHandlerBuilder());
                this.includeRoutes(new HandleTaxForms());
                this.includeRoutes(new CallExternalPartners());
                this.includeRoutes(new IoRoutes());
                this.includeRoutes(new FillCitizenRegistry());
            }
        };
    }


    /**
     * Handling Mocks endpoints automatically
     */
    private static Map<String,String> mocks = new HashMap<>();
    @BeforeClass public static void loadEndpointsAsMocks() throws Exception {
        // Automatically loads all the constants defined in the Endpoints class
        Field[] fields = Endpoints.class.getDeclaredFields();
        for(Field f: fields) {
            if (Modifier.isStatic(f.getModifiers()) && f.getType().equals(String.class))
                mocks.put(""+f.get(""), "mock://"+f.get(""));
        }
    }

    protected void isAvailableAndMocked(String name) {
        assertNotNull(context.hasEndpoint(name));
        assertNotNull(context.hasEndpoint( mocks.get(name)));
    }

    protected MockEndpoint mock(String name) {
        return getMockEndpoint(mocks.get(name));
    }

}
