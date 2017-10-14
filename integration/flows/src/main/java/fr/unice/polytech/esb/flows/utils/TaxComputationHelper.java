package fr.unice.polytech.esb.flows.utils;

import fr.unice.polytech.esb.flows.data.TaxInfo;

import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathFactory;

public class TaxComputationHelper {

    private XPath xpath = XPathFactory.newInstance().newXPath();

    public String buildSimpleRequest(TaxInfo i, String uuid) {
        StringBuilder builder = new StringBuilder();
        builder.append("<cook:simple xmlns:cook=\"http://cookbook.soa1.polytech.unice.fr/\">\n");
        builder.append("  <simpleTaxInfo>\n");
        builder.append("    <id>"     + uuid          + "</id>\n");
        builder.append("    <income>" + i.getForm().getIncome() + "</income>\n");
        builder.append("  </simpleTaxInfo>\n");
        builder.append("</cook:simple>");
        return builder.toString();
    }

    public String buildAdvancedRequest(TaxInfo i, String uuid) {
        StringBuilder builder = new StringBuilder();
        builder.append("<cook:complex xmlns:cook=\"http://cookbook.soa1.polytech.unice.fr/\">\n");
        builder.append("  <complexTaxInfo>\n");
        builder.append("    <id>"     + uuid           + "</id>\n");
        builder.append("    <income>" + i.getForm().getIncome()  + "</income>\n");
        builder.append("    <assets>" + i.getForm().getAssets()  + "</assets>\n");
        builder.append("    <zone>"   + i.getPerson().getZipCode() + "</zone>\n");
        builder.append("  </complexTaxInfo>\n");
        builder.append("</cook:complex>");
        return builder.toString();
    }

}
