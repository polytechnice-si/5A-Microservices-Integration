package fr.unice.polytech.esb.flows.technical.data;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.io.Serializable;

public class Request implements Serializable {

    @JsonProperty private String event;
    @JsonProperty private Person citizen;

    public Request(String event, Person citizen) {
        this.event = event;
        this.citizen = citizen;
    }

    public Request() {}

    public String getEvent() { return event; }
    public void setEvent(String event) { this.event = event; }

    public Person getCitizen() { return citizen; }
    public void setCitizen(Person citizen) { this.citizen = citizen; }
}
