package fr.unice.polytech.esb.flows;

import org.apache.activemq.broker.BrokerService;
import org.apache.camel.test.junit4.CamelTestSupport;
import org.junit.AfterClass;
import org.junit.BeforeClass;

public abstract class ActiveMQTest  extends CamelTestSupport {

    private static BrokerService brokerSvc;

    @BeforeClass
    public static void setUpClass() throws Exception {
        brokerSvc = new BrokerService();
        brokerSvc.setBrokerName("TestBroker");
        brokerSvc.addConnector("tcp://localhost:61616");
        brokerSvc.start();
    }

    @AfterClass
    public static void tearDownClass() throws Exception { brokerSvc.stop(); }


}
