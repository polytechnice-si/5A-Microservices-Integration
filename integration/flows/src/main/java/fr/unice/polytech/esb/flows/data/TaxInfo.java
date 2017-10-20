package fr.unice.polytech.esb.flows.data;

import java.io.Serializable;

public class TaxInfo implements Serializable {

    private Person person;
    private TaxForm form;
    private float taxAmount;
    private String timeStamp;

    public TaxInfo(Person person, TaxForm form) {
        this.person = person;
        this.form = form;
    }

    public TaxInfo(TaxInfo origin) {
        this.person = origin.person;
        this.form = origin.form;
        this.taxAmount = origin.taxAmount;
        this.timeStamp = origin.timeStamp;
    }

    public TaxInfo() {}

    public Person getPerson() { return person; }
    public void setPerson(Person person) { this.person = person; }

    public TaxForm getForm() { return form; }
    public void setForm(TaxForm form) { this.form = form; }

    public String getTimeStamp() { return timeStamp; }
    public void setTimeStamp(String timeStamp) { this.timeStamp = timeStamp; }

    public float getTaxAmount() { return taxAmount; }
    public void setTaxAmount(float taxAmount) { this.taxAmount = taxAmount; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        TaxInfo taxInfo = (TaxInfo) o;

        if (Float.compare(taxInfo.taxAmount, taxAmount) != 0) return false;
        if (person != null ? !person.equals(taxInfo.person) : taxInfo.person != null) return false;
        if (form != null ? !form.equals(taxInfo.form) : taxInfo.form != null) return false;
        return timeStamp != null ? timeStamp.equals(taxInfo.timeStamp) : taxInfo.timeStamp == null;
    }

    @Override
    public int hashCode() {
        int result = person != null ? person.hashCode() : 0;
        result = 31 * result + (form != null ? form.hashCode() : 0);
        result = 31 * result + (taxAmount != +0.0f ? Float.floatToIntBits(taxAmount) : 0);
        result = 31 * result + (timeStamp != null ? timeStamp.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "TaxInfo{" +
                "person=" + person +
                ", form=" + form +
                ", taxAmount=" + taxAmount +
                ", timeStamp='" + timeStamp + '\'' +
                '}';
    }
}
