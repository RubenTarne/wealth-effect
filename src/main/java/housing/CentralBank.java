package housing;

/**************************************************************************************************
 * Class to represent the mortgage policy regulator or Central Bank. It reads a number of policy
 * thresholds from the config object into local variables with the purpose of allowing for dynamic
 * policies varying those thresholds over time.
 *
 * @author daniel, Adrian Carro
 *
 *************************************************************************************************/

public class CentralBank {

    //------------------//
    //----- Fields -----//
    //------------------//

    // General fields
	private Config      config = Model.config;	// Passes the Model's configuration parameters object to a private field

    // Monetary policy
    private double		baseRate;

    // LTI policy thresholds
    private double      firstTimeBuyerLTILimit; // Loan-To-Income upper limit for first-time buying mortgages
    private double      ownerOccupierLTILimit; // Loan-To-Income upper limit for owner-occupying mortgages
    private double      maxFractionOOMortgagesOverLTILimit; // Fraction of owner-occupying mortgages allowed to exceed the Loan-To-Income limit

    // ICR policy thresholds
    private double interestCoverRatioLimit; // Ratio of expected rental yield over interest monthly payment under stressed interest conditions
    private double interestCoverRatioStressedRate; // Stressed interest rate used for Interest-Cover-Ratio assessments
    
    // LTV policy thresholds
    private double firstTimeBuyerLTVLimit; // Loan-To-Value upper limit for first-time buying mortgages
    private double ownerOccupierLTVLimit; // Loan-To-Value upper limit for owner-occupying mortgages
    private double BTLLTVLimit; // Loan-To-Value upper limit for BTL mortgages
    private double maxFractionMortgagesOverLTVLimit;// Fraction of all mortgages allowed to exceed the Loan-To-Value limit
    
    
    private boolean macroprudentialActive; // true, if the caps of the CB are active, false, if only the commercial banks LTVs are active

    //-------------------//
    //----- Methods -----//
    //-------------------//

    void init() {
        // Set initial monetary policy
        baseRate = config.CENTRAL_BANK_INITIAL_BASE_RATE;
        // Set initial LTI policy thresholds
        firstTimeBuyerLTILimit = config.CENTRAL_BANK_MAX_FTB_LTI;
        ownerOccupierLTILimit = config.CENTRAL_BANK_MAX_OO_LTI;
        maxFractionOOMortgagesOverLTILimit = config.CENTRAL_BANK_FRACTION_OVER_MAX_LTI;
        // Set initial ICR policy thresholds
        interestCoverRatioLimit = config.CENTRAL_BANK_MAX_ICR;
        interestCoverRatioStressedRate = config.CENTRAL_BANK_BTL_STRESSED_INTEREST;
        // Setup initial LTV policy thresholds
        firstTimeBuyerLTVLimit = config.centralBankFirstTimeBuyerLTVLimit;
        ownerOccupierLTVLimit = config.centralBankOwnerOccupierLTVLimit;
        BTLLTVLimit = config.centralBankBTLLTVLimit;
        maxFractionMortgagesOverLTVLimit = config.centralBankMaxFractionMortgagesOverLTVLimit;
        
        macroprudentialActive = false;
    }

	/**
	 * This method implements the policy strategy of the Central Bank
     *
	 * @param coreIndicators The current value of the core indicators
	 */
    public void step(collectors.CoreIndicators coreIndicators) {
    	//		 Use this method to express the policy strategy of the central bank by setting the value of the various limits
    	//		 in response to the current value of the core indicators.
    	//
    	//		 Example policy: if house price growth is greater than 0.001 then FTB LTV limit is 0.75 otherwise (if house
    	//		 price growth is less than or equal to  0.001) FTB LTV limit is 0.95
    	//		 Example code:
    	//		 	if(Model.housingMarketStats.getLongTermHPA() > 0.001 && Model.getTime() >1000) {
    	//		 		firstTimeBuyerLTVLimit = 0.75;
    	//		 		ownerOccupierLTVLimit = 0.75;
    	//		 		BTLLTVLimit = 0.75;
    	//		 	} else {
    	//		 		firstTimeBuyerLTVLimit = 0.999;
    	//		 		ownerOccupierLTVLimit = 0.999;
    	//		 		BTLLTVLimit = 0.999;
    	//		 	}
    	
    	if(config.anticyclicalCBLTVs) {
    		if(macroprudentialActive == true & Model.housingMarketStats.getAnnualHPA() < -0.2) {
    			macroprudentialActive = false;
    		} else if(macroprudentialActive == false & Model.housingMarketStats.getQoQHousePriceGrowth() > 0.0){
    			macroprudentialActive = true;
    		}

    		if(macroprudentialActive == true) {
    			firstTimeBuyerLTVLimit = config.centralBankFirstTimeBuyerLTVLimit;
    			ownerOccupierLTVLimit = config.centralBankOwnerOccupierLTVLimit;
    			BTLLTVLimit = config.centralBankBTLLTVLimit;
    		} else {
    			firstTimeBuyerLTVLimit = 0.999;
    			ownerOccupierLTVLimit = 0.999;
    			BTLLTVLimit = 0.999;
    		}
    	}

    }

    /**
     * Get the Loan-To-Income ratio limit applicable to a given household. Note that Loan-To-Income constraints apply
     * only to non-BTL applicants. The private bank always imposes its own limit. Apart from this, it also imposes the
     * Central Bank regulated limit, which allows for a certain fraction of residential loans (mortgages for
     * owner-occupying) to go over it
     *
     * @param isFirstTimeBuyer True if the household is first-time buyer
     * @param isHome True if the mortgage is to buy a home for the household (non-BTL mortgage)
     * @return Loan-To-Income ratio limit applicable to the household
     */
	double getLoanToIncomeLimit(boolean isFirstTimeBuyer, boolean isHome) {
        if (isHome) {
            if (isFirstTimeBuyer) {
                return firstTimeBuyerLTILimit;
            } else {
                return ownerOccupierLTILimit;
            }
        } else {
            System.out.println("Strange: The Central Bank is trying to impose a Loan-To-Income limit on a Buy-To-Let" +
                        "investor!");
            return 0.0; // Dummy return statement
        }
	}

    /**
     * Get the maximum fraction of mortgages to owner-occupying households that can go over the Loan-To-Income limit
     */
    double getMaxFractionOOMortgagesOverLTILimit() { return maxFractionOOMortgagesOverLTILimit; }

    /**
     * Get the Interest-Cover-Ratio limit applicable to a particular household
     */
    double getInterestCoverRatioLimit(boolean isHome) {
        if (!isHome) {
            return interestCoverRatioLimit;
        } else {
            System.out.println("Strange: Interest-Cover-Ratio limit is being imposed on an owner-occupying buyer!");
            return 0.0; // Dummy return statement
        }
    }

    /**
     * Get the stressed interest rate for the Interest-Cover-Ratio assessment for a particular household
     */
    public double getInterestCoverRatioStressedRate(boolean isHome) {
        if (!isHome) {
            return interestCoverRatioStressedRate;
        } else {
            System.out.println("Strange: Interest-Cover-Ratio rate is being used for assessing an owner-occupying" +
                    "buyer!");
            return 0.0; // Dummy return statement
        }
    }
    
    /**
     * Get the Loan-To-Value ratio limit applicable to a given household. 
     * The private bank always imposes its own limit. Apart from this, it also imposes the
     * Central Bank regulated limit, which allows for a certain fraction of loans
     * to go over it
     *
     * @param isFirstTimeBuyer True if the household is first-time buyer
     * @param isHome True if the mortgage is to buy a home for the household (non-BTL mortgage)
     * @return Loan-To-Value ratio limit applicable to the household
     */
	double getLoanToValueLimit(boolean isFirstTimeBuyer, boolean isHome) {
        if (isHome) {
            if (isFirstTimeBuyer) {
                return firstTimeBuyerLTVLimit;
            } else {
                return ownerOccupierLTVLimit;
            }
        } else {
            return BTLLTVLimit;
        }
	}
	
    /**
     * Get the maximum fraction of mortgages to households that can go over the Loan-To-Value limit
     */
    double getMaxFractionOOMortgagesOverLTVLimit() { return maxFractionMortgagesOverLTVLimit; }


    //----- Getter/setter methods -----//

    double getBaseRate() { return baseRate; }
    
    public int getCentralBankLTVsOnOff() {
    	if(macroprudentialActive) return 1;
    	else return 0;
    }

}
