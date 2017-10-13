package fr.unice.polytech.esb.flows.utils;

public class Endpoints {

    // file inputs
    public static final String CSV_INPUT_FILE_TAXES    = "file:/servicemix/camel/input?fileName=taxes.csv";
    public static final String CSV_INPUT_FILE_CITIZENS = "file:/servicemix/camel/input?fileName=citizens.csv";


    // Internal message queues
    public static final String HANDLE_A_TAX_FORM   = "activemq:handleTaxForm";
    public static final String GET_CITIZEN_INFO    = "activemq:getCitizenInfo";
    public static final String REGISTER_A_CITIZEN  = "activemq:registerCitizen";

    // External partners
    public static final String REGISTRATION_ENDPOINT = "http:tcs-registry:8080/tcs-service-document/registry";

    // Dead letters channel
    public static final String CITIZEN_REGISTRY_DEATH = "activemq:citizen-registry:dead";

}
