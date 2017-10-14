package fr.unice.polytech.esb.flows.utils;


import fr.unice.polytech.esb.flows.data.TaxInfo;

public class TaxMessageGenerator {

    public String write(TaxInfo info, String method) {
        StringBuilder b = new StringBuilder();

        b.append("Dear " + info.getPerson().getFirstName() + " " + info.getPerson().getLastName() + ", \n");
        b.append("\n");
        b.append("  Address: " + info.getPerson().getAddress() + " " + info.getPerson().getZipCode() + "\n");
        b.append("  ID: " + info.getPerson().getSsid() + "\n");
        b.append("\n\n");
        b.append("Taxes computed using the " + method + " method on " + info.getTimeStamp() + "\n");
        b.append("\n\n");
        b.append("Amount to pay: " + info.getTaxAmount() + "\n");
        b.append("\n");

        return b.toString();
    }
}
