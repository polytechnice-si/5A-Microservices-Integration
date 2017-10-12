package fr.unice.polytech.esb.flows.data;

import java.io.Serializable;

public class TaxForm implements Serializable {

    private String ssn;
    private String email;
    private int income;
    private int assets;
    private String phone;

    public String getSsn() { return ssn; }
    public void setSsn(String ssn) { this.ssn = ssn; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public int getIncome() { return income; }
    public void setIncome(int income) { this.income = income; }

    public int getAssets() { return assets; }
    public void setAssets(int assets) { this.assets = assets; }

    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        TaxForm taxForm = (TaxForm) o;

        if (income != taxForm.income) return false;
        if (assets != taxForm.assets) return false;
        if (ssn != null ? !ssn.equals(taxForm.ssn) : taxForm.ssn != null) return false;
        if (email != null ? !email.equals(taxForm.email) : taxForm.email != null) return false;
        return phone != null ? phone.equals(taxForm.phone) : taxForm.phone == null;
    }

    @Override
    public int hashCode() {
        int result = ssn != null ? ssn.hashCode() : 0;
        result = 31 * result + (email != null ? email.hashCode() : 0);
        result = 31 * result + income;
        result = 31 * result + assets;
        result = 31 * result + (phone != null ? phone.hashCode() : 0);
        return result;
    }
}
