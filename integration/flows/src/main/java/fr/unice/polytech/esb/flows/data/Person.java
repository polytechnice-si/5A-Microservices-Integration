package fr.unice.polytech.esb.flows.data;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.io.Serializable;

public class Person implements Serializable {

    @JsonProperty("ssn") private String ssid;
    @JsonProperty("last_name") private String lastName;
    @JsonProperty("first_name")private String firstName;
    @JsonProperty("birth_year") private String birthYear;
    @JsonProperty("zip_code") private String zipCode;
    @JsonProperty private String address;

    public String getSsid() { return ssid; }
    public void setSsid(String ssid) { this.ssid = ssid;  }

    public String getLastName() { return lastName; }
    public void setLastName(String lastName) { this.lastName = lastName; }

    public String getFirstName() { return firstName; }
    public void setFirstName(String firstName) { this.firstName = firstName; }

    public String getBirthYear() { return birthYear; }
    public void setBirthYear(String birthYear) { this.birthYear = birthYear; }

    public String getZipCode() { return zipCode; }
    public void setZipCode(String zipCode) { this.zipCode = zipCode; }

    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Person person = (Person) o;

        if (ssid != null ? !ssid.equals(person.ssid) : person.ssid != null) return false;
        if (lastName != null ? !lastName.equals(person.lastName) : person.lastName != null) return false;
        if (firstName != null ? !firstName.equals(person.firstName) : person.firstName != null) return false;
        if (birthYear != null ? !birthYear.equals(person.birthYear) : person.birthYear != null) return false;
        if (zipCode != null ? !zipCode.equals(person.zipCode) : person.zipCode != null) return false;
        return address != null ? address.equals(person.address) : person.address == null;
    }

    @Override
    public int hashCode() {
        int result = ssid != null ? ssid.hashCode() : 0;
        result = 31 * result + (lastName != null ? lastName.hashCode() : 0);
        result = 31 * result + (firstName != null ? firstName.hashCode() : 0);
        result = 31 * result + (birthYear != null ? birthYear.hashCode() : 0);
        result = 31 * result + (zipCode != null ? zipCode.hashCode() : 0);
        result = 31 * result + (address != null ? address.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "Person{" +
                "ssid='" + ssid + '\'' +
                ", lastName='" + lastName + '\'' +
                ", firstName='" + firstName + '\'' +
                ", birthYear='" + birthYear + '\'' +
                ", zipCode='" + zipCode + '\'' +
                ", address='" + address + '\'' +
                '}';
    }
}
