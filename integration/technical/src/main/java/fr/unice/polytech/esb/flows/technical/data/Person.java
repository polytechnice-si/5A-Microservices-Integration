package fr.unice.polytech.esb.flows.technical.data;

import java.io.Serializable;

public class Person implements Serializable {

    private String ssid;
    private String lastName;
    private String firstName;
    private String birthYear;
    private String zipCode;
    private String address;


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

}
