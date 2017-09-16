package tcs.service;

import tcs.data.*;

import javax.jws.WebService;
import java.util.Date;


@WebService(targetNamespace   = "http://informatique.polytech.unice.fr/soa1/cookbook/",
		    portName          = "ExternalTaxComputerPort",
		    serviceName       = "ExternalTaxComputerService",
		    endpointInterface = "tcs.service.TaxComputationService")
public class TaxComputationImpl implements TaxComputationService {

	public TaxComputation simple(SimpleTaxRequest request) {
		float amount = (float) (request.getIncome() * 0.2);
		return buildResponse(request.getIdentifier(), amount);
	}

	public TaxComputation complex(AdvancedTaxRequest request) {
		float onIncome = computeIncome(request.getIncome(), request.getZone());
		float onAssets = computeAssets(request.getAssets(), request.getZone());
		float amount = onIncome + onAssets;
		return buildResponse(request.getIdentifier(), amount);
	}

	private TaxComputation buildResponse(String id, float amount) {
		TaxComputation result = new TaxComputation();
		result.setIdentifier(id);
		result.setDate(new Date().toString());
		result.setAmount(amount);
		return result;
	}

	/***************************************
	 ** Mock for the Business Logic Layer **
	 ***************************************/

	public float computeIncome(float i, String code) {
		float coeff = (float) (!code.startsWith("1") ? 0.2 : 0.18 );
		return i * coeff;
	}

	public float computeAssets(float a, String code) {
		float coeff = (float) (!code.startsWith("1") ? 0.12 : 0.1 );
		return a * coeff;
	}

}
