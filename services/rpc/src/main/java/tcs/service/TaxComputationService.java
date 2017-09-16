package tcs.service;

import javax.jws.WebParam;
import javax.jws.WebResult;
import javax.jws.WebService;

import tcs.data.*;

@WebService(name="TaxComputation")
public interface TaxComputationService {


	@WebResult(name="simple_result")
	TaxComputation simple(@WebParam(name="simpleTaxInfo") SimpleTaxRequest request);

	@WebResult(name="complex_result")
	TaxComputation complex(@WebParam(name="complexTaxInfo") AdvancedTaxRequest request);

}
