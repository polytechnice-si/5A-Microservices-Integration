package fr.unice.polytech.esb.flows.data;

import java.io.Serializable;

public class TaxInfo implements Serializable {

    private Person person;
    private TaxForm form;

    public TaxInfo(Person person, TaxForm form) {
        this.person = person;
        this.form = form;
    }

    public Person getPerson() { return person; }
    public void setPerson(Person person) { this.person = person; }

    public TaxForm getForm() { return form; }
    public void setForm(TaxForm form) { this.form = form; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        TaxInfo taxInfo = (TaxInfo) o;

        if (person != null ? !person.equals(taxInfo.person) : taxInfo.person != null) return false;
        return form != null ? form.equals(taxInfo.form) : taxInfo.form == null;
    }

    @Override
    public int hashCode() {
        int result = person != null ? person.hashCode() : 0;
        result = 31 * result + (form != null ? form.hashCode() : 0);
        return result;
    }
}
