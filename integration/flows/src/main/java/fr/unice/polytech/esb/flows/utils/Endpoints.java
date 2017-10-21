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
    public static final String GET_ANONYMOUS_ID     = "activemq:anonymous-id-gen";
    public static final String CREATE_ANONYMOUS_GEN = "activemq:anonymous-gen";
    public static final String TAX_COMPUTE_SIMPLE   = "activemq:tax-computation:simple";
    public static final String TAX_COMPUTE_COMPLEX  = "activemq:tax-computation:complex";
    public static final String MESSAGE_GENERATION   = "activemq:letter-generation";

    // Direct endpoints (flow modularity without a message queue overhead)
    public static final String COMPUTE_TAXES    = "direct:handle-a-citizen";
    public static final String SNAIL_MAIL_PRINT = "direct:snail-mail-printing";
    public static final String EMAIL_SENDING    = "direct:send-email";

    // External partners
    public static final String REGISTRATION_ENDPOINT = "http:tcs-registry:8080/tcs-service-document/registry";
    public static final String GENERATOR_ENDPOINT    = "http:tcs-generator:8080/tcs-service-rest/generators";
    public static final String TAX_COMPUTATION       = "http:tcs-computation:8080/tcs-service-rpc/ExternalTaxComputerService";

    // Dead letters channel
    public static final String DEATH_POOL = "activemq:global:dead";
    public static final String BAD_CITIZEN = "activemq:badCitizens";

}
