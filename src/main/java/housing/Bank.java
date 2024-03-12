package housing;

import java.util.HashSet;

/**************************************************************************************************
 * Class to represent a mortgage-lender (i.e. a bank or building society), whose only function is
 * to approve/decline mortgage requests, so this is where mortgage-lending policy is encoded
 *
 * @author daniel, davidrpugh, Adrian Carro
 *
 *************************************************************************************************/
public class Bank {

    //------------------//
    //----- Fields -----//
    //------------------//

	// General fields
	private Config	                    config = Model.config; // Passes the Model's configuration parameters object to a private field
    private CentralBank                 centralBank; // Connection to the central bank to ask for policy

    // Bank fields
    public HashSet<MortgageAgreement>	mortgages; // all unpaid mortgage contracts supplied by the bank
    public double		                interestSpread; // current mortgage interest spread above base rate (monthly rate*12)
    private double                      monthlyPaymentFactor; // Monthly payment as a fraction of the principal for non-BTL mortgages
    private double                      monthlyPaymentFactorBTL; // Monthly payment as a fraction of the principal for BTL (interest-only) mortgages

    // Credit supply strategy fields
    private double		                supplyTarget; // target supply of mortgage lending (pounds)
    private double		                supplyVal; // monthly supply of mortgage loans (pounds)
    private int                         nOOMortgagesOverLTI; // Number of mortgages for owner-occupying that go over the LTI cap this time step
    private int                         nOOMortgages; // Total number of mortgages for owner-occupying
    private int							nMortgages; // Total number of all mortgages
    private int							nFTBMortgagesOverLTV; // Total number of FTB mortgages over the LTV limit
    private int							nMoverMortgagesOverLTV; // Total number of mortgages of movers (owner-occupiers, that are not first-time buyers)
    private int							nBTLMortgagesOverLTV; // Total number of mortgages of BTL-investors over the LTV limit

    // LTV internal policy thresholds
    private double                      firstTimeBuyerLTVLimit; // Loan-To-Value upper limit for first-time buyer mortgages
    private double                      ownerOccupierLTVLimit; // Loan-To-Value upper limit for owner-occupying mortgages
    private double                      buyToLetLTVLimit; // Loan-To-Value upper limit for buy-to-let mortgages

    // LTI internal policy thresholds
    private double                      firstTimeBuyerLTILimit; // Loan-To-Income internal upper limit for first-time buyer mortgages
    private double                      ownerOccupierLTILimit; // Loan-To-Income internal upper limit for owner-occupying mortgages

    //------------------------//
    //----- Constructors -----//
    //------------------------//

	public Bank(CentralBank centralBank) {
	    this.centralBank = centralBank;
		mortgages = new HashSet<>();
	}

    //-------------------//
    //----- Methods -----//
    //-------------------//

	void init() {
		mortgages.clear();
        setMortgageInterestRate(config.BANK_INITIAL_RATE); // Central Bank must already by initiated at this point!
		resetMonthlyCounters();
        // Setup initial LTV internal policy thresholds
        firstTimeBuyerLTVLimit = config.BANK_MAX_FTB_LTV;
        ownerOccupierLTVLimit= config.BANK_MAX_OO_LTV;
        buyToLetLTVLimit = config.BANK_MAX_BTL_LTV;
        // Setup initial LTI internal policy thresholds
        firstTimeBuyerLTILimit = config.BANK_MAX_FTB_LTI;
        ownerOccupierLTILimit = config.BANK_MAX_OO_LTI;
    }
	
	/**
	 * Redo all necessary monthly calculations and reset counters.
     *
     * @param totalPopulation Current population in the model, needed to scale the target amount of credit
	 */
	public void step(int totalPopulation) {
		if (config.fixedInterestRates) {
			setMortgageInterestRate(config.bankMortgageInterestSpread + centralBank.getBaseRate());
		} else {
			supplyTarget = creditSupplyTarget(totalPopulation);
			setMortgageInterestRate(recalculateInterestRate());
		}
		resetMonthlyCounters();
	}
	
	public double creditSupplyTarget(int totalPopulation) {
		if(config.trend && config.periodTrendStarting >= Model.getTime()){
			// adjust the credit supply by banks according to the income growth rate
			return config.BANK_CREDIT_SUPPLY_TARGET*totalPopulation * (Model.getTime()-config.periodTrendStarting) * config.yearlyIncreaseEmploymentIncome; 
		}else {
			return config.BANK_CREDIT_SUPPLY_TARGET*totalPopulation;
		}
	}
	
	/**
	 *  Reset counters for the next month
	 */
	private void resetMonthlyCounters() {
		supplyVal = 0.0;
        nOOMortgagesOverLTI = 0;
        nOOMortgages = 0;
        nMortgages = 0;
        nFTBMortgagesOverLTV = 0;
        nMoverMortgagesOverLTV = 0;
        nBTLMortgagesOverLTV = 0;
	}
	
	/**
	 * Calculate the mortgage interest rate for next month based on the rate for this month and the resulting demand.
     * This assumes a linear relationship between interest rate and demand, and aims to halve the difference between
     * current demand and the target supply
	 */
	private double recalculateInterestRate() {
	    // TODO: Need to decide whether to keep and calibrate the 1/2 factor or to get rid of it
		double rate = getMortgageInterestRate() + 0.5 * (supplyVal - supplyTarget)/config.BANK_D_DEMAND_D_INTEREST;
		if (rate < centralBank.getBaseRate()) rate = centralBank.getBaseRate();
		return rate;
	}
	
	/**
	 * Get the interest rate on mortgages.
	 */
	private double getMortgageInterestRate() { return centralBank.getBaseRate() + interestSpread; }
	

	/**
	 * Set the interest rate on mortgages
	 */
	private void setMortgageInterestRate(double rate) {
		interestSpread = rate - centralBank.getBaseRate();
        recalculateMonthlyPaymentFactor();
	}

    /**
     * Compute the monthly payment factor, i.e., the monthly payment on a mortgage as a fraction of the mortgage
     * principal for both BTL (interest-only) and non-BTL mortgages.
     */
	private void recalculateMonthlyPaymentFactor() {
		double r = getMortgageInterestRate()/config.constants.MONTHS_IN_YEAR;
		monthlyPaymentFactor = r/(1.0 - Math.pow(1.0 + r, -config.derivedParams.N_PAYMENTS));
        monthlyPaymentFactorBTL = r;
	}
//
//	/**
//	 * Get the monthly payment factor, i.e., the monthly payment on a mortgage as a fraction of the mortgage principal.
//	 */
//	private double getMonthlyPaymentFactor(boolean isHome, double Age) {  // Age is only included here as I test if to use Adrians age limitation proposition
////		 TEST BTL receive normal credit where they pay off the principal as well.
//		if(!config.BTLinterestOnly) { return monthlyPaymentFactor;}
//		if (isHome) {
//			return monthlyPaymentFactor; // Monthly payment factor to pay off the principal in N_PAYMENTS
//		} else {
//			return monthlyPaymentFactorBTL; // Monthly payment factor for interest-only mortgages
//		}
//	}

    /**
     * Get the monthly payment factor, i.e., the monthly payment on a mortgage as a fraction of the mortgage principal.
     * This takes into account age-based restrictions for non-BTL mortgages via the number of payments.
     */
	private double getMonthlyPaymentFactor(boolean isHome, double age) {
		double r = getMortgageInterestRate() / config.constants.MONTHS_IN_YEAR;
		// For non-BTL purchases, compute payment factor to pay off the principal in the agreed number of payments,
		// coherent with any mortgage length age-based restrictions
		if (isHome) {
			if (getNPayments(true, age) > 0) {
				return r / (1.0 - Math.pow(1.0 + r, -getNPayments(true, age)));
			} else {
				throw new RuntimeException("Trying to find monthly payment factor for a zero payments mortgage");
			}
			// For BTL purchases, compute interest-only payment factor (age-based restrictions applied elsewhere)
		} else if(!config.BTLinterestOnly){ return monthlyPaymentFactor;
		}else {	return monthlyPaymentFactorBTL;
		}
	}
	
    /**
     * Compute the number of payments, taking into account differentiated age-based restrictions for BTL and non-BTL
     * bids. In particular, BTL mortgages always have full maturity, but they can only be approved before the household
     * reaches the age limit. On the contrary, non-BTL mortgages start seeing their maturities reduced before the age
     * limit, in such a way that the full amount is repaid by the time the household reaches this limit.
     */
    private int getNPayments(boolean isHome, double age) {
        // For non-BTL purchases, any mortgage principal must be repaid when the household turns 65
        if (isHome) {
            if (age <= config.BANK_AGE_LIMIT - config.MORTGAGE_DURATION_YEARS) {
//            	if (age <= 75 - config.MORTGAGE_DURATION_YEARS) {
                return config.MORTGAGE_DURATION_YEARS * config.constants.MONTHS_IN_YEAR;
            } else if (age <= config.BANK_AGE_LIMIT) {
//            } else if (age <= 75) {
            	// as the rentOrPurchase method is based on yearly costs for house 
            	return Math.min(1, (int) ((config.BANK_AGE_LIMIT - age) * config.constants.MONTHS_IN_YEAR));
            } else {
                return 0;
            }
        // For BTL purchases, a mortgage can only be approved before the household turns 65
        } else {
            if (age <= 65) {
                return config.MORTGAGE_DURATION_YEARS * config.constants.MONTHS_IN_YEAR;
            } else {
                return 0;
            }
        }
    }
	
	
	/**
	 * Method to arrange a Mortgage and get a MortgageAgreement object.
	 * 
	 * @param h The household requesting the mortgage
	 * @param housePrice The price of the house that household h wants to buy
	 * @param isHome True if household h plans to live in the house (non-BTL mortgage)
	 * @return The MortgageApproval object, or NULL if the mortgage is declined
	 */
	MortgageAgreement requestLoan(Household h, double housePrice, double desiredDownPayment, boolean isHome) {
		MortgageAgreement approval = requestApproval(h, housePrice, desiredDownPayment, isHome, true);
		if(approval == null) return(null);
		// --- if all's well, go ahead and arrange mortgage
		supplyVal += approval.principal;
		if(approval.principal > 0.0) {
			mortgages.add(approval);
			Model.creditSupply.recordLoan(h, approval);
//			// TEST RUBEN - age limit - only for BTL investors, the others are accounted for by the 
//			// getMonthlyPaymentFactor() function
//			if(!isHome & h.getAge() >65) {
//				approval.principal = 0;
//				return approval;
//			}
			// check if loans would be over LTI and/or LTV limit and count them
			if(isHome) {
				// .. first, record OO mortgage for movers and first-time buyers and check if over LTI limit
				++nOOMortgages;
				if(approval.principal/h.getAnnualGrossEmploymentIncome() >
				Model.centralBank.getLoanToIncomeLimit(h.isFirstTimeBuyer(), isHome)) {
					++nOOMortgagesOverLTI;
					// .. second, check if it was a first-time buyer loan over the LTV limit
					if(h.isFirstTimeBuyer()) {
						if(approval.principal/housePrice > Model.centralBank.getLoanToValueLimit(true, true)){
							++nFTBMortgagesOverLTV;
						}
						// .. if not, it has to be a loan for a mover
					} else {
						if(approval.principal/housePrice > Model.centralBank.getLoanToValueLimit(false, true)) {
							++nMoverMortgagesOverLTV;
						}
					}
				}
			} else {
				//.. if the loan is not for the households home, it has to be a BTL loan
				if(approval.principal/housePrice >
				Model.centralBank.getLoanToValueLimit(false, false)) {
					++nBTLMortgagesOverLTV;
				}
			}
			
			++nMortgages;
		} else if (approval.principal == 0.0){
			Model.creditSupply.recordCashPayment(housePrice);
		}
		return approval;
	}

	/**
	 * Method to request a mortgage approval but not actually sign a mortgage contract. This is useful if you want to
     * explore the details of the mortgage contract before deciding whether to actually go ahead and sign it.
	 *
     * @param h The household requesting the mortgage
     * @param housePrice The price of the house that household h wants to buy
     * @param isHome True if household h plans to live in the house (non-BTL mortgage)
     * @return The MortgageApproval object, or NULL if the mortgage is declined
	 */
	MortgageAgreement requestApproval(Household h, double housePrice, double desiredDownPayment,
			boolean isHome, boolean methodCalledFromRequestLoan) {
		MortgageAgreement approval = new MortgageAgreement(h, !isHome);
		double r = getMortgageInterestRate()/config.constants.MONTHS_IN_YEAR; // monthly interest rate
		double lti_principal = 0.0;
		double affordable_principal = 0.0;
		double icr_principal = 0.0;
		double liquidWealth = h.getBankBalance();

		if(isHome) liquidWealth += h.getHomeEquity(); // I can probably remove this, as homeowners always sell their property before they buy a new home

		// --- LTV constraint
		double ltv_principal = housePrice * getLoanToValueLimit(h.isFirstTimeBuyer(), isHome); //Math.min(0.99, (getLoanToValueLimit(h.isFirstTimeBuyer(), isHome)+ 15*h.behaviour.getLongTermHPAExpectation()));
		approval.principal = ltv_principal;

		if (getNPayments(isHome, h.getAge()) > 0) {

			//		// DSTI constraint
			//		double dsti = 0.25;
			//		double dsti_principal = (h.getMonthlyGrossEmploymentIncome() * dsti) / getMonthlyPaymentFactor(isHome, h.getAge());
			//		if(dsti_principal > ltv_principal ) {
			//			System.out.println("stop!");
			//		}
			//		approval.principal = Math.min(dsti_principal, ltv_principal);

			/*
			 * Constraints specific to non-BTL mortgages
			 */

			if(config.allCreditConstraintsActive) {
				if(isHome) {
					// Affordability constraint: it sets a maximum value for the monthly mortgage payment divided by the
					// household's monthly gross employment income
					affordable_principal = config.CENTRAL_BANK_AFFORDABILITY_COEFF * h.getMonthlyGrossEmploymentIncome()
							/ getMonthlyPaymentFactor(true, h.getAge());
					
					if (getMonthlyPaymentFactor(true, h.getAge()) == 1.0) affordable_principal = 0.0;
					approval.principal = Math.min(approval.principal, affordable_principal);
					// Loan-To-Income (LTI) constraint: it sets a maximum value for the principal divided by the household's
					// annual gross employment income. 
					// Not active in the Dutch version of the model
					if(!config.NDLVersion) {
						lti_principal = h.getAnnualGrossEmploymentIncome() * getLoanToIncomeLimit(h.isFirstTimeBuyer(), isHome);
						approval.principal = Math.min(approval.principal, lti_principal);

					}

					/*
					 * Constraints specific to BTL mortgages
					 */

				} else {
					// BTL agents have no further credit limit in the NDL version, except the LTV 
					if(!config.NDLVersion) {
						// Interest Coverage Ratio (ICR) constraint: it sets a minimum value for the expected annual rental
						// income divided by the annual interest expenses
						icr_principal = Model.rentalMarketStats.getExpAvFlowYield() * housePrice
								/ (Model.centralBank.getInterestCoverRatioLimit(isHome) * r); 
						approval.principal = Math.min(approval.principal, icr_principal);
					}

				}
			}
		} else {
			approval.principal = 0.0;
		}


		approval.downPayment = housePrice - approval.principal;

		if(liquidWealth < approval.downPayment) {
			System.out.println("Failed down-payment constraint: bank balance = " + liquidWealth + " downpayment = "
					+ approval.downPayment);
			System.exit(0);
		}
		// --- allow larger downpayments
		if(desiredDownPayment < 0.0) desiredDownPayment = 0.0;
		if(desiredDownPayment > liquidWealth) desiredDownPayment = liquidWealth;
		if(desiredDownPayment > housePrice) desiredDownPayment = housePrice;
		if(desiredDownPayment > approval.downPayment) {
			approval.downPayment = desiredDownPayment;
			approval.principal = housePrice - desiredDownPayment;
		}
		//
		//		approval.monthlyPayment = approval.principal* getMonthlyPaymentFactor(isHome, h.getAge());
		//		approval.nPayments = config.derivedParams.N_PAYMENTS;
		//		approval.monthlyInterestRate = r;
		//		approval.purchasePrice = approval.principal + approval.downPayment;
		//		
		/*
		 * Set the rest of the variables of the MortgageAgreement object
		 */

		if (getNPayments(isHome, h.getAge()) > 0) {
			approval.monthlyPayment = approval.principal * getMonthlyPaymentFactor(isHome, h.getAge());
		} else {
			approval.monthlyPayment = 0.0;
		}
		approval.nPayments = getNPayments(isHome, h.getAge());
		approval.monthlyInterestRate = getMortgageInterestRate() / config.constants.MONTHS_IN_YEAR;
		approval.purchasePrice = approval.principal + approval.downPayment;
		// Throw error and stop program if requested mortgage has down-payment larger than household's liquid wealth
		if (approval.downPayment > liquidWealth) {
			System.out.println("Error at Bank.requestApproval(), down-payment larger than household's bank balance: "
					+ "downpayment = " + approval.downPayment + ", bank balance = " + liquidWealth);
			System.exit(0);
		}

		/*
		 * RECORDER ******************************************************
		 */
		// this records agents DECISION DATA SH, when they decide to rent or buy. 
		// the rest of the variables gets recorded in the behaviour.decideRentOrPurchase method
		if(!methodCalledFromRequestLoan 
				&& config.recordAgentDecisions 
				&& (Model.getTime() >= config.TIME_TO_START_RECORDING)) {
			if(isHome) {
				Model.agentDecisionRecorder.recordLoanRequestSH(ltv_principal, affordable_principal, lti_principal);		
			}
			// record agent data DECISION DATA BTL 
			else {
				Model.agentDecisionRecorder.recordLoanRequestBTL(ltv_principal, icr_principal);
			}
		}

		return approval;
	}

	/**
	 * Find, for a given household, the maximum house price that this mortgage-lender is willing to approve a mortgage
     * for.
	 * 
	 * @param h The household applying for the mortgage
     * @param isHome True if household h plans to live in the house (non-BTL mortgage)
	 * @return The maximum house price that this mortgage-lender is willing to approve a mortgage for
	 */
	public double getMaxMortgage(Household h, boolean isHome, boolean methodCallFromDecideToBuyInvestmentProperty) {
		double ltv_max_price =0.0;
		double max_price = 0.0;
		double affordability_max_price = 0.0; // Affordability (disposable income) constraint for maximum house price
		double lti_max_price = 0.0; // Loan to income constraint for maximum house price
		double icr_max_price = 0.0; // Interest cover ratio constraint for maximum house price
		double liquidWealth = h.getBankBalance(); // No home equity needs to be added here: households always sell their homes before trying to buy new ones
		double max_downpayment = liquidWealth - 0.01; // Maximum down-payment the household could make, where 1 cent is subtracted to avoid rounding errors

        // If number of payments is zero, then no principal is approved, purchase must be paid outright
        if (getNPayments(isHome, h.getAge()) == 0) return max_downpayment;
		
		// LTV constraint: maximum house price the household could pay with the maximum mortgage the bank could provide
		// to the household given the Loan-To-Value limit and the maximum down-payment the household could make
		// LTVFLEXIBLE
		// the maximum LTV ratio is 0.999

		ltv_max_price = max_downpayment/(1.0 - getLoanToValueLimit(h.isFirstTimeBuyer(), isHome));

//		// DSTI constraint
//		double dsti = 0.25;
//		double dsti_max_price = max_downpayment + (h.getMonthlyGrossEmploymentIncome() * dsti) / getMonthlyPaymentFactor(isHome, h.getAge());
//
//		max_price = Math.min(dsti_max_price, ltv_max_price);
		max_price = ltv_max_price;

		if(config.allCreditConstraintsActive) {
			if(isHome) { // No LTI nor affordability constraints for BTL investors
				// Affordability constraint
				affordability_max_price = max_downpayment + Math.max(0.0, config.CENTRAL_BANK_AFFORDABILITY_COEFF
						* h.getMonthlyGrossEmploymentIncome()) / getMonthlyPaymentFactor(isHome, h.getAge());
				max_price = Math.min(max_price, affordability_max_price);
				// Loan-To-Income constraint
				// not active in the Dutch version of the model
				if(!config.NDLVersion) {
					lti_max_price = h.getAnnualGrossEmploymentIncome() * getLoanToIncomeLimit(h.isFirstTimeBuyer(), isHome)
							+ max_downpayment;
					max_price = Math.min(max_price, lti_max_price);
				}
			} else {
				// not active in the NDL version of the model
				if(!config.NDLVersion) {
					// Interest-Cover-Ratio constraint
					icr_max_price = max_downpayment / (1.0 - Model.rentalMarketStats.getExpAvFlowYield() 
							/ (centralBank.getInterestCoverRatioLimit(isHome) * getMortgageInterestRate()));
					if (icr_max_price < 0.0) icr_max_price = Double.POSITIVE_INFINITY; // When rental yield is larger than interest rate times ICR, then ICR does never constrain
					max_price = Math.min(max_price,  icr_max_price);
				}

			}
		}
		// First part of the DECISION DATA SH output
		if (config.recordAgentDecisions && (Model.getTime() >= config.TIME_TO_START_RECORDING)) {
			if(isHome) {
				Model.agentDecisionRecorder.recordMaxMortgageSH(h, ltv_max_price, affordability_max_price, lti_max_price);
			}
			// First part of the DECISION DATA BTL output
			// agents 
			else if(methodCallFromDecideToBuyInvestmentProperty){
				Model.agentDecisionRecorder.recordMaxMortgageBTL(h, ltv_max_price, icr_max_price);
			}
		}
		return max_price;
	}

    /**
     * This method removes a mortgage contract by removing it from the HashSet of mortgages
     *
     * @param mortgage The MortgageAgreement object to be removed
     */
    void endMortgageContract(MortgageAgreement mortgage) { mortgages.remove(mortgage); }

    //----- Mortgage policy methods -----//

    /**
     * Get the Loan-To-Value ratio limit applicable by this private bank to a given household. Note that this limit is
     * self-imposed by the private bank.
     *
     * @param isFirstTimeBuyer True if the household is a first-time buyer
     * @param isHome True if the mortgage is to buy a home for the household (non-BTL mortgage)
     * @return The Loan-To-Value ratio limit applicable to the given household
     */
    public double getLoanToValueLimit(boolean isFirstTimeBuyer, boolean isHome) {
    	double limit;

    	// pro-cyclical credit -  loan-to-value limit
    	if(config.procyclicalCreditConstraints) {
    		// first compute the private bank self-imposed (hard) limit, which applies always
    		if(isHome) {
    			if(isFirstTimeBuyer) {
    				// return at least the set limit, but possibly higher, and at most 1.0
    				//return Math.max(Math.min(Model.housingMarketStats.getLongTermHPA()*0.2+firstTimeBuyerLTVLimit, 1.0), firstTimeBuyerLTVLimit);
    				limit = Math.min(Model.housingMarketStats.getLongTermHPA()*config.LTVAdjustmentFactor + firstTimeBuyerLTVLimit, 0.9999);
    			} else {
    				limit = Math.min(Model.housingMarketStats.getLongTermHPA()*config.LTVAdjustmentFactor + ownerOccupierLTVLimit, 0.9999);
    			}
    		} else {
    			limit = Math.min(Model.housingMarketStats.getLongTermHPA()*config.LTVAdjustmentFactor + buyToLetLTVLimit, 0.9999);
    		}
    		// If the fraction of mortgages already underwritten over the Central Bank LTV limit exceeds a certain
    		// maximum (regulated also by the Central Bank)...
    		if ((nFTBMortgagesOverLTV + nMoverMortgagesOverLTV + nBTLMortgagesOverLTV + 1.0)/(nMortgages + 1.0) >
    		Model.centralBank.getMaxFractionOOMortgagesOverLTVLimit()) {
    			// ... then compare the Central Bank LTI (soft) limit and that of the private bank (hard) and choose the smallest
    			limit = Math.min(limit, Model.centralBank.getLoanToValueLimit(isFirstTimeBuyer, isHome));
    		}
    		return limit;
    	}

    	// fixed credit constraints - loan-to-value limit
    	// first compute the private bank self-imposed (hard) limit, which applies always
    	if(isHome) {
    		if(isFirstTimeBuyer) {
    			// return at least the set limit, but possibly higher, and at most 1.0
    			//return Math.max(Math.min(Model.housingMarketStats.getLongTermHPA()*0.2+firstTimeBuyerLTVLimit, 1.0), firstTimeBuyerLTVLimit);
    			limit = firstTimeBuyerLTVLimit;
    		} else {
    			limit = ownerOccupierLTVLimit;
    		}
    	} else {
    		limit = buyToLetLTVLimit;
    	}
    	// If the fraction of mortgages already underwritten over the Central Bank LTV limit exceeds a certain
    	// maximum (regulated also by the Central Bank)...
    	if ((nFTBMortgagesOverLTV + nMoverMortgagesOverLTV + nBTLMortgagesOverLTV + 1.0)/(nMortgages + 1.0) >
    	Model.centralBank.getMaxFractionOOMortgagesOverLTVLimit()) {
    		// ... then compare the Central Bank LTV (soft) limit and that of the private bank (hard) and choose the smallest
    		limit = Math.min(limit, Model.centralBank.getLoanToValueLimit(isFirstTimeBuyer, isHome));
    	}
    	return limit;
    }

	/**
	 * Get the Loan-To-Income ratio limit applicable by this private bank to a given household. Note that Loan-To-Income
     * constraints apply only to non-BTL applicants. The private bank always imposes its own (hard) limit. Apart from
     * this, it also imposes the Central Bank regulated limit, which allows for a certain fraction of residential loans
     * (mortgages for owner-occupying) to go over it (and thus it is considered here a soft limit).
	 *
	 * @param isFirstTimeBuyer true if the household is a first-time buyer
     * @param isHome True if the mortgage is to buy a home for the household (non-BTL mortgage)
	 * @return The Loan-To-Income ratio limit applicable to the given household
	 */
	private double getLoanToIncomeLimit(boolean isFirstTimeBuyer, boolean isHome) {
	    double limit;
	    // First compute the private bank self-imposed (hard) limit, which applies always
        if (isHome) {
            if (isFirstTimeBuyer) {
                limit = firstTimeBuyerLTILimit;
            } else {
                limit = ownerOccupierLTILimit;
            }
        } else {
            System.out.println("Strange: The bank is trying to impose a Loan-To-Income limit on a Buy-To-Let" +
                    "investor!");
            limit = 0.0; // Dummy limit value
        }
        // If the fraction of non-BTL mortgages already underwritten over the Central Bank LTI limit exceeds a certain
        // maximum (regulated also by the Central Bank)...
        if ((nOOMortgagesOverLTI + 1.0)/(nOOMortgages + 1.0) >
                centralBank.getMaxFractionOOMortgagesOverLTILimit()) {
            // ... then compare the Central Bank LTI (soft) limit and that of the private bank (hard) and choose the smallest
            limit = Math.min(limit, centralBank.getLoanToIncomeLimit(isFirstTimeBuyer, isHome));
        }
		return limit;
    }
}
