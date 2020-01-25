package collectors;

import housing.Config;
import housing.Model;
import housing.Household;
import housing.MortgageAgreement;
import org.apache.commons.math3.stat.descriptive.DescriptiveStatistics;

/**************************************************************************************************
 * Class to record mortgage data
 *
 * @author daniel, Adrian Carro
 *
 *************************************************************************************************/
public class CreditSupply {

    //------------------//
    //----- Fields -----//
    //------------------//

    private Config config = Model.config;       // Passes the Model's configuration parameters object to a private field
    private DescriptiveStatistics oo_lti;
    private DescriptiveStatistics oo_ltv;
    private DescriptiveStatistics btl_ltv;
    private DescriptiveStatistics btl_icr;
    private DescriptiveStatistics downpayments; // TODO: This quantity only includes downpayments when the principal of the loan is > 0
    public double totalBTLCredit = 0.0;        // Buy to let mortgage credit
    public double totalOOCredit = 0.0;         // Owner-occupier mortgage credit
    private double netCreditGrowth;             // Rate of change of credit per month as percentage
    private double affordability = 0.0;         // Affordability coefficient
    private int mortgageCounter;                // Counter for total number of new mortgages
    private int nApprovedMortgages;             // total number of new mortgages
    private int ftbCounter;                     // Counter for total number of new first time buyer mortgages
    private int nFTBMortgages;                  // Total number of new first time buyer mortgages
    private int btlCounter;                     // Counter for total number of new buy to let mortgages
    private int nBTLMortgages;                  // Total number of new buy to let mortgages

    public double oldTotalCredit;
	
	public double totalBTLDownPayment = 0.0;
	public double totalOODownPayment = 0.0;
	public double netDownPaymentGrowth;
	public double oldTotalDownPayment;
	public double newDownPayment;
	public double newDownPaymentsApproved;
	private double newPrincipalIssuedCounter;
	private double newPrincipalIssued;	

    //------------------------//
    //----- Constructors -----//
    //------------------------//

	public CreditSupply() {
		mortgageCounter = 0;
		ftbCounter = 0;
		btlCounter = 0;
		newDownPayment = 0.0;
		// TODO: This limit in the number of events taken into account to build statistics is not explained in the paper
        // TODO: (affects oo_lti, oo_ltv, btl_ltv, btl_icr, downpayments)
		setArchiveLength(10000);
	}

    //-------------------//
    //----- Methods -----//
    //-------------------//

	/**
     * Collect information for this time step
	 */
	public void step() {
        oldTotalCredit = totalOOCredit + totalBTLCredit;
        oldTotalDownPayment = totalBTLDownPayment + totalOODownPayment;
        totalOOCredit = 0.0;
        totalBTLCredit = 0.0;
        totalBTLDownPayment = 0.0;
        totalOODownPayment = 0.0;
        for(MortgageAgreement m : Model.bank.mortgages) {
        	if(m.isBuyToLet) {
            	totalBTLCredit += m.principal;
            	totalBTLDownPayment += m.downPayment;
        	} else {
        		totalOOCredit += m.principal;
        		totalOODownPayment += m.downPayment;
        	}
        }
        if (oldTotalCredit > 0.0) {
            netCreditGrowth = (totalOOCredit + totalBTLCredit - oldTotalCredit)/oldTotalCredit;
            netDownPaymentGrowth = (totalBTLDownPayment + totalOODownPayment - oldTotalDownPayment)/oldTotalDownPayment;
        } else {
            netCreditGrowth = 0;
            netDownPaymentGrowth = 0;
        }
        nApprovedMortgages = mortgageCounter;
        nFTBMortgages = ftbCounter;
        nBTLMortgages = btlCounter;
        mortgageCounter = 0;
        ftbCounter = 0;
        btlCounter = 0;
        newDownPaymentsApproved = newDownPayment;
        newDownPayment = 0.0;
        newPrincipalIssued = newPrincipalIssuedCounter;
        newPrincipalIssuedCounter = 0.0;
	}
	//TODO this is not newly issued credit, but total credit in the simulation at time 't'
	public double getNewlyIssuedCredit() {
		return newPrincipalIssued;
	}
	//TODO 
	public double getNewlyPaidDownPayments() {
		return newDownPaymentsApproved;
	}
	

	/**
	 * Record information for a newly issued mortgage
	 * @param h Household being awarded the loan
	 * @param approval Mortgage agreement
	 */
	public void recordLoan(Household h, MortgageAgreement approval) {
		double housePrice;
		housePrice = approval.principal + approval.downPayment;
		// TODO: Check with Arzu, Marc if monthly gross income used here should include total income or just employment income (as of now)
		affordability = config.derivedParams.getAffordabilityDecay()*affordability +
				(1.0-config.derivedParams.getAffordabilityDecay())*approval.monthlyPayment/
				(h.getMonthlyGrossEmploymentIncome());
		// TODO: This condition is redundant, as the method is only called when approval.principal > 0
		if(approval.principal > 0.0) {
			if(approval.isBuyToLet) {
				btl_ltv.addValue(100.0*approval.principal/housePrice);
				double icr = Model.rentalMarketStats.getExpAvFlowYield()*approval.purchasePrice/
						(approval.principal*Model.centralBank.getInterestCoverRatioStressedRate(false));
				btl_icr.addValue(icr);
			} else {
				oo_ltv.addValue(100.0*approval.principal/housePrice);
				oo_lti.addValue(approval.principal/h.getAnnualGrossEmploymentIncome());
			}
			downpayments.addValue(approval.downPayment);
		}
		mortgageCounter += 1;
		newDownPayment += approval.downPayment;
		newPrincipalIssuedCounter += approval.principal;

		if(approval.isFirstTimeBuyer) ftbCounter += 1;
		if(approval.isBuyToLet) btlCounter += 1;

		downpayments.addValue(approval.downPayment);
	}

	private void setArchiveLength(int archiveLength) {
		oo_lti = new DescriptiveStatistics(archiveLength);
		oo_ltv = new DescriptiveStatistics(archiveLength);
		btl_ltv = new DescriptiveStatistics(archiveLength);
		btl_icr = new DescriptiveStatistics(archiveLength);
		downpayments = new DescriptiveStatistics(archiveLength);
	}

    //----- Getter/setter methods -----//

    DescriptiveStatistics getOO_lti() { return oo_lti; }

    DescriptiveStatistics getOO_ltv() { return oo_ltv; }

    DescriptiveStatistics getBTL_ltv() { return btl_ltv; }

    int getnRegisteredMortgages() { return Model.bank.mortgages.size(); }

    int getnApprovedMortgages() { return nApprovedMortgages; }

    int getnFTBMortgages() { return nFTBMortgages; }

    int getnBTLMortgages() { return nBTLMortgages; }

    double getTotalBTLCredit() { return totalBTLCredit; }

    double getTotalOOCredit() { return totalOOCredit; }

    double getNetCreditGrowth() { return netCreditGrowth; }
}
