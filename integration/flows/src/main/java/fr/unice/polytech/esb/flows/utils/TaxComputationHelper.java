package fr.unice.polytech.esb.flows.utils;

import fr.unice.polytech.esb.flows.data.TaxInfo;
import org.xml.sax.InputSource;

import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathFactory;
import java.io.StringReader;

public class TaxComputationHelper {

    private XPath xpath = XPathFactory.newInstance().newXPath();

    public String buildSimpleRequest(TaxInfo i, String uuid) {
        StringBuilder builder = new StringBuilder();
        builder.append("<cook:simple xmlns:cook=\"http://informatique.polytech.unice.fr/soa1/cookbook/\">\n");
        builder.append("  <simpleTaxInfo>\n");
        builder.append("    <id>"     + uuid          + "</id>\n");
        builder.append("    <income>" + i.getForm().getIncome() + "</income>\n");
        builder.append("  </simpleTaxInfo>\n");
        builder.append("</cook:simple>");
        return builder.toString();
    }

    public String buildAdvancedRequest(TaxInfo i, String uuid) {
        StringBuilder builder = new StringBuilder();
        builder.append("<cook:complex xmlns:cook=\"http://informatique.polytech.unice.fr/soa1/cookbook/\">\n");
        builder.append("  <complexTaxInfo>\n");
        builder.append("    <id>"     + uuid           + "</id>\n");
        builder.append("    <income>" + i.getForm().getIncome()  + "</income>\n");
        builder.append("    <assets>" + i.getForm().getAssets()  + "</assets>\n");
        builder.append("    <zone>"   + i.getPerson().getZipCode() + "</zone>\n");
        builder.append("  </complexTaxInfo>\n");
        builder.append("</cook:complex>");
        return builder.toString();
    }

    public TaxInfo consolidateResponse(String response, TaxInfo partial) throws Exception {
        XPath xpath = XPathFactory.newInstance().newXPath();
        TaxInfo result = new TaxInfo(partial);

        InputSource src =  new InputSource(new StringReader(response));
        result.setTaxAmount(Float.parseFloat(xpath.evaluate("//amount/text()", src)));

        src =  new InputSource(new StringReader(response));
        result.setTimeStamp(xpath.evaluate("//date/text()", src));

        return result;
    }


}
