package fr.unice.polytech.esb.flows.utils;

public class Endpoints {

    // file inputs
    public static final String CSV_INPUT_FILE_TAXES    = "file:/servicemix/camel/input?fileName=taxes.csv";
    public static final String CSV_INPUT_FILE_CITIZENS = "file:/servicemix/camel/input?fileName=citizens.csv";

    // file outputs
    public static final String LETTER_OUTPUT_DIR = "file:/servicemix/camel/output";

    // Internal message queues
    public static final String BUILD_TAX_INFO       = "activemq:handle-tax-form";
    public static final String GET_CITIZEN_INFO     = "activemq:get-citizen-info";
    public static final String REGISTER_A_CITIZEN   = "activemq:register-citizen";
    public static final String GET_ANONYMOUS_ID     = "activemq:anonymous-gen-get-id";
    public static final String CREATE_ANONYMOUS_GEN = "activemq:anonymous-gen-create";
    public static final String TAX_COMPUTE_SIMPLE   = "activemq:tax-computation:simple";
    public static final String TAX_COMPUTE_COMPLEX  = "activemq:tax-computation:complex";
    public static final String MESSAGE_GENERATION   = "activemq:letter-generation";
    public static final String TAX_FORM_TO_COMPUTE  = "activemq:tax-compute-from-tax-form";

    // Direct endpoints (flow modularity without a message queue overhead)
    public static final String COMPUTE_TAXES    = "direct:handle-a-citizen";
    public static final String SNAIL_MAIL_PRINT = "direct:snail-mail-printing";
    public static final String EMAIL_SENDING    = "direct:send-email";

    // External partners as plain HTTP endpoints
    public static final String REGISTRATION_ENDPOINT = "http:tcs-citizens:8080/tcs-service-document/registry";
    public static final String GENERATOR_ENDPOINT    = "http:tcs-generator:8080/tcs-service-rest/generators";

    // External partners as SOAP web services
    public static final String TAX_COMPUTATION =
            "spring-ws:http://tcs-computation:8080/tcs-service-rpc/ExternalTaxComputerService";

    // Dead letter channel
    public static final String DEATH_POOL  = "activemq:global:dead";

    // Error channel (that does not goes to dead letter)
    public static final String BAD_CITIZEN = "activemq:badCitizens";

}
