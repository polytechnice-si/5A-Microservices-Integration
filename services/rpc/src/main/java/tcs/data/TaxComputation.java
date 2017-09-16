package tcs.data;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

@XmlType
public class TaxComputation {

	private String date;
	private float amount;
	private String identifier;

	@XmlElement
	public String getDate() { return date; }
	public void setDate(String date) { this.date = date; }

	@XmlElement
	public float getAmount() { return amount; }
	public void setAmount(float amount) { this.amount = amount; }

	@XmlElement
	public String getIdentifier() { return identifier; }
	public void setIdentifier(String identifier) { this.identifier = identifier; }

}
