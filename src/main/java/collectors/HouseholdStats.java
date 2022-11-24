package collectors;

import housing.*;
import org.apache.commons.math3.stat.descriptive.DescriptiveStatistics;

import java.util.Map;

/**************************************************************************************************
 * Class to collect regional household statistics
 *
 * @author daniel, Adrian Carro
 *
 *************************************************************************************************/
public class HouseholdStats {

	//------------------//
	//----- Fields -----//
	//------------------//

	// General fields
	private Config  config = Model.config; // Passes the Model's configuration parameters object to a private field

	// Fields for counting numbers of the different types of households and household conditions
	private int     nBTL; // Number of buy-to-let (BTL) households, i.e., households with the BTL gene (includes both active and inactive)
	private int     nActiveBTL; // Number of BTL households with, at least, one BTL property
	private int     nBTLOwnerOccupier; // Number of BTL households owning their home but without any BTL property
	private int     nBTLHomeless; // Number of homeless BTL households
    private int		nSSB; // number of second and subsequent buyers, defined as homeowners that are not in their first home anymore
    private int		nInFirstHome; // numbers of owner occupiers in their first home (bought as First-time buyer)
	private int     nBTLBankruptcies; // Number of BTL households going bankrupt in a given time step
    
    private int 	nBTLRentalProperty; // Rental Property in the hands of BTL investors. Not counting rental property held by households inheriting property and renting it out
    
    
    // PAUL AIRBNB values 
    private int 	nAirBnBBTL;
	private double	airBnBRentalIncome;
	private int 	nAirBnBs;
	private double  rentingMonthlyDisposableIncome;

	public int      nNonBTLOwnerOccupier; // Number of non-BTL households owning their home
	private int     nRenting; // Number of (by definition, non-BTL) households renting their home
	private int     nNonBTLHomeless; // Number of homeless non-BTL households
	private int		nFTBinSocialHousing; // number of FTB in social housing (homeless)
    private int     nNonBTLBankruptcies; // Number of non-BTL households going bankrupt in a given time step

	// Fields for summing annualised total incomes
	private double  activeBTLAnnualisedTotalIncome;
	private double  ownerOccupierAnnualisedTotalIncome;
	private double  rentingAnnualisedTotalIncome;
	private double  homelessAnnualisedTotalIncome;
	
	// fields for summing annualised net incomes
	private double  activeBTLMonthlyNetIncome;
	private double  ownerOccupierMonthlyNetIncome;
	private double  rentingMonthlyNetIncome;
	private double  homelessMonthlyNetIncome;
	
	// fields of summing monthly employment incomes
	private double activeBTLMonthlyGrossEmploymentIncome;
	private double ownerOccupierMonthlyGrossEmploymentIncome;
	private double rentingMonthlyGrossEmploymentIncome;
	private double homelessMonthlyGrossEmploymentIncome;
	
	// Other fields
	private double  sumStockYield; // Sum of stock gross rental yields of all currently occupied rental properties
    private int     nNonBTLBidsAboveExpAvSalePrice; // Number of normal (non-BTL) bids with desired housing expenditure above the exponential moving average sale price
    private int     nBTLBidsAboveExpAvSalePrice; // Number of BTL bids with desired housing expenditure above the exponential moving average sale price
    private int     nNonBTLBidsAboveExpAvSalePriceCounter; // Counter for the number of normal (non-BTL) bids with desired housing expenditure above the exp. mov. av. sale price
    private int     nBTLBidsAboveExpAvSalePriceCounter; // Counter for the number of BTL bids with desired housing expenditure above the exp. mov. av. sale price

    //RUBEN additional variable totalConsumption and Savings
    private double totalMonthlyDisposableIncome;
	private double totalMonthlyDisposableIncomeCounter;
    private double totalBankBalancesEndPeriod;
    private double totalBankBalancesEndPeriodCounter;
    private double totalSocialHousingRent;
    private double totalSocialHousingRentCounter;
    private double totalConsumption;
    private double totalConsumptionCounter;
    private double totalSaving;
    private double totalSavingCounter;
    private double totalBankBalancesBeforeConsumption;
    private double totalBankBalancesBeforeConsumptionCounter;
    private double totalBankBalancesVeryBeginningOfPeriod;
    private double totalBankBalancesVeryBeginningOfPeriodCounter;
    private double totalBankBalanceEndowment;
    private double totalBankBalanceEndowmentCounter;
    private double totalPrincipalRepayments;
    private double totalPrincipalRepaymentsCounter;
    private double totalPrincipalRepaymentsDueToHouseSale;
    private double totalPrincipalRepaymentsDueToHouseSaleCounter;
    private double totalPrincipalPaidBackForInheritance;
    private double totalPrincipalPaidBackForInheritanceCounter;
    private double totalInterestRepayments;
    private double totalInterestRepaymentsCounter;
    private double totalRentalPayments;
    private double totalRentalPaymentsCounter;
    private double totalMonthlyTaxesPaid;
    private double totalMonthlyTaxesPaidCounter;
    private double totalMonthlyNICPaid;
    private double totalMonthlyNICPaidCounter;
    private double totalBankruptcyCashInjection;
    private double totalBankruptcyCashInjectionCounter;
    private double totalDebtReliefOfDeceasedHouseholds; // when households die, they pass on their wealth, if they cannot pay back all their credit, it is forgiven
    private double totalDebtReliefOfDeceasedHouseholdsCounter; 
    private double totalPrincipalRepaymentDeceasedHousehold; // when households die, they pay off as much of their debt as possible
    private double totalPrincipalRepaymentDeceasedHouseholdCounter; 
    // number of households that have a total negative equity position
    private int nNegativeEquity;
    
    private double totalIncomeConsumption; // sum all consumption out of income
    private double totalFinancialWealthConsumption; // sum all consumption out of wealth
    private double totalIncomeConsumptionCounter; // counter for the sum of all consumption out of income
    private double totalFinancialWealthConsumptionCounter; // counter for the sum of all consumption out of wealth
    private double totalHousingWealthConsumption;
    private double totalHousingWealthConsumptionCounter;
    private double totalDebtConsumption;
    private double totalDebtConsumptionCounter;
    private double totalSavingForDeleveraging;
    private double totalSavingForDeleveragingCounter;
    private double totalDividendIncome;
    private double totalDividendIncomeCounter;
    
    // agent-specific consumption parameters
    private double activeBTLIncomeConsumptionCounter;
    private double activeBTLFinancialWealthConsumptionCounter;
	private double activeBTLNetHousingWealthConsumptionCounter;
	private double SSBIncomeConsumptionCounter;
	private double SSBFinancialWealthConsumptionCounter;
	private double SSBNetHousingWealthConsumptionCounter;
	private double inFirstHomeIncomeConsumptionCounter;
	private double inFirstHomeFinancialWealthConsumptionCounter;
	private double inFirstHomeNetHousingWealthConsumptionCounter;
	private double renterIncomeConsumptionCounter;
	private double renterFinancialWealthConsumptionCounter;
	private double renterNetHousingWealthConsumptionCounter;
	
    private double activeBTLIncomeConsumption;
    private double activeBTLFinancialWealthConsumption;
	private double activeBTLNetHousingWealthConsumption;
	private double SSBIncomeConsumption;
	private double SSBFinancialWealthConsumption;
	private double SSBNetHousingWealthConsumption;
	private double inFirstHomeIncomeConsumption;
	private double inFirstHomeFinancialWealthConsumption;
	private double inFirstHomeNetHousingWealthConsumption;
	private double renterIncomeConsumption;
	private double renterFinancialWealthConsumption;
	private double renterNetHousingWealthConsumption;
    
    
    // For sensitivity analysis
    public DescriptiveStatistics totalNetWealth; 
    
    // for calculation of median income
    private DescriptiveStatistics grossTotalIncome;
    private double medianIncome; 
    // for a quasi-collateral channel 
    private DescriptiveStatistics debtServiceRatios;
    private double medianDSR;
    private DescriptiveStatistics vulnerableHouseholdsDSR;
    private double medianDSRVulnerableHouseholds;
    private DescriptiveStatistics vulnerableHouseholdsAge;
    private double medianAgeVulnerableHouseholds;
    private DescriptiveStatistics nonVulnerableHouseholdsAge;
    private double medianAgeNonVulnerableHouseholds;
    private int indebtedHouseholds;
    private int indebtedHouseholdsCounter;
    
    // adjust these income dependent values of households with a buffer used
    // to calibrate the model's vulnerable households to that of the WAS, specifically
    // reduce the income by 20% of median income (the difference between 40% and 60%)
    
    private DescriptiveStatistics debtServiceRatiosAdjusted;
    private double medianDSRAdjusted;
    private DescriptiveStatistics vulnerableHouseholdsDSRAdjusted;
    private double medianDSRVulnerableHouseholdsAdjusted;
    
    // fields for caluclating exposures at default
    private double ExposureAtDefaultDSR30; 
    private double ExposureAtDefaultDSR30Counter;
	private double ExposureAtDefaultDSR35;
	private double ExposureAtDefaultDSR35Counter; 
	private double ExposureAtDefaultDSR70; 
	private double ExposureAtDefaultDSR70Counter;
	private double ExposureAtDefaultFinancialMarginBLC20;
	private double ExposureAtDefaultFinancialMarginBLC20Counter;
	private double ExposureAtDefaultFinancialMarginBLC40;
	private double ExposureAtDefaultFinancialMarginBLC40Counter;
	private double ExposureAtDefaultFinancialMarginBLC70;
	private double ExposureAtDefaultFinancialMarginBLC70Counter;
	
	private double ExposureAtDefaultAmpudiaMeasure1;
	private double ExposureAtDefaultAmpudiaMeasure1Counter;
	private double ExposureAtDefaultAmpudiaMeasure2;
	private double ExposureAtDefaultAmpudiaMeasure2Counter;
	
	private int HouseholdsWithLessThan1500p;
	private int HouseholdsWithLessThan1500pCounter;
	private double lowDepositHouseholdConsumption ;
	private double lowDepositHouseholdConsumptionCounter ;
	private double lowDepositHouseholdSaving; 
	private double lowDepositHouseholdSavingCounter; 
	private int householdsVulnerableAmpudiaMeasure2;
	private int householdsVulnerableAmpudiaMeasure2Counter;
	private int activeBTLVulnerable; 
	private int activeBTLVulnerableCounter;
	private int SSBVulnerable;
	private int SSBVulnerableCounter;
	private int inFirstHomeVulnerable;
	private int inFirstHomeVulnerableCounter;
	private double activeBTLEAD ; 
	private double activeBTLEADCounter;
	private double SSBEAD; 
	private double SSBEADCounter; 
	private double inFirstHomeEAD;
	private double inFirstHomeEADCounter;
    
	private double unemploymentExposureAtDefaultAmpudiaMeasure2 ;
	private double unemploymentExposureAtDefaultAmpudiaMeasure2Counter ;
	private int unemploymentHouseholdsVulnerableAmpudiaMeasure2 ;
	private int unemploymentHouseholdsVulnerableAmpudiaMeasure2Counter ;

	private int unemploymentActiveBTLVulnerable;
	private int unemploymentActiveBTLVulnerableCounter ;
	private double unemploymentActiveBTLEAD ;
	private double unemploymentActiveBTLEADCounter; 

	private int unemploymentSSBVulnerable ;
	private int unemploymentSSBVulnerableCounter;
	private double unemploymentSSBEAD ;
	private double unemploymentSSBEADCounter ;

	private int unemploymentinFirstHomeVulnerable;
	private int unemploymentinFirstHomeVulnerableCounter;
	private double unemploymentinFirstHomeEAD ;
	private double unemploymentinFirstHomeEADCounter ;
	
	private int vulnerableByPurchase ;
	private int vulnerableByPurchaseCounter ;
	private int vulnerableByConsumption ;
	private int vulnerableByConsumptionCounter ;
	private int vulnerableByOther ;
	private int vulnerableByOtherCounter ;
	
	private int notVulnerableBecauseSale ;
	private int notVulnerableBecauseSaleCounter ;
	private int notVulnerableBecauseSaving ;
	private int notVulnerableBecauseSavingCounter ;
	private int notVulnerableBecauseOther ;
	private int notVulnerableBecauseOtherCounter;
	
	private int vulnerableByPurchaseBTL ;
	private int vulnerableByPurchaseBTLCounter ;
	private int vulnerableByPurchaseSSB ;
	private int vulnerableByPurchaseSSBCounter ;
	private int vulnerableByPurchaseInFirstHome ;
	private int vulnerableByPurchaseInFirstHomeCounter ;
	private int vulnerableByConsumptionBTL ;
	private int vulnerableByConsumptionBTLCounter ;
	private int vulnerableByConsumptionSSB ;
	private int vulnerableByConsumptionSSBCounter ;
	private int vulnerableByConsumptionInFirstHome ;
	private int vulnerableByConsumptionInFirstHomeCounter ;
	private int vulnerableByOtherBTL ;
	private int vulnerableByOtherBTLCounter ;
	private int vulnerableByOtherSSB ;
	private int vulnerableByOtherSSBCounter ;
	private int vulnerableByOtherInFirstHome ;
	private int vulnerableByOtherInFirstHomeCounter ;

	private int notVulnerableBecauseSaleBTL ;
	private int notVulnerableBecauseSaleBTLCounter ;
	private int notVulnerableBecauseSaleSSB ;
	private int notVulnerableBecauseSaleSSBCounter ;
	private int notVulnerableBecauseSaleInFirstHome ;
	private int notVulnerableBecauseSaleInFirstHomeCounter ;
	private int notVulnerableBecauseSaleOthers ;
	private int notVulnerableBecauseSaleOthersCounter ;
	private int notVulnerableBecauseSavingBTL ;
	private int notVulnerableBecauseSavingBTLCounter ;
	private int notVulnerableBecauseSavingSSB ;
	private int notVulnerableBecauseSavingSSBCounter ;
	private int notVulnerableBecauseSavingInFirstHome ;
	private int notVulnerableBecauseSavingInFirstHomeCounter ;
	private int notVulnerableBecauseSavingOthers ;
	private int notVulnerableBecauseSavingOthersCounter ;
	private int notVulnerableBecauseOtherBTL ;
	private int notVulnerableBecauseOtherBTLCounter ;
	private int notVulnerableBecauseOtherSSB ;
	private int notVulnerableBecauseOtherSSBCounter ;
	private int notVulnerableBecauseOtherInFirstHome ;
	private int notVulnerableBecauseOtherInFirstHomeCounter ;
	private int notVulnerableBecauseOtherOthers ;
	private int notVulnerableBecauseOtherOthersCounter ;
	
	private int nowVulnerableByPurchase;
	private int	nowVulnerableByPurchaseCounter;
	private int nowVulnerableByPurchaseBTL;
	private int nowVulnerableByPurchaseBTLCounter;
	private int nowVulnerableByPurchaseSSB;
	private int nowVulnerableByPurchaseSSBCounter;
	private int nowVulnerableByPurchaseFTB;
	private int nowVulnerableByPurchaseFTBCounter;
	
	private int nowVulnerableByDissaving;
	private int	nowVulnerableByDissavingCounter;
	private int nowVulnerableByDissavingBTL;
	private int nowVulnerableByDissavingBTLCounter;
	private int nowVulnerableByDissavingSSB;
	private int nowVulnerableByDissavingSSBCounter;
	private int nowVulnerableByDissavingFTB;
	private int nowVulnerableByDissavingFTBCounter;
	
	private int nowVulnerableByOther;
	private int	nowVulnerableByOtherCounter;
	private int nowVulnerableByOtherBTL;
	private int nowVulnerableByOtherBTLCounter;
	private int nowVulnerableByOtherSSB;
	private int nowVulnerableByOtherSSBCounter;
	private int nowVulnerableByOtherFTB;
	private int nowVulnerableByOtherFTBCounter;
	
    //-------------------//
    //----- Methods -----//
    //-------------------//


	/**
     * Sets initial values for all relevant variables to enforce a controlled first measure for statistics
     */
    public void init() {
        nBTL = 0;
        nActiveBTL = 0;
        nBTLOwnerOccupier = 0;
        nBTLHomeless = 0;
        nSSB = 0; 
        nInFirstHome = 0; 

        nBTLBankruptcies = 0;
        
        // PAUL AIRBNB values
        nAirBnBBTL = 0;
    	airBnBRentalIncome = 0.0;
    	nAirBnBs = 0;
    	rentingMonthlyDisposableIncome = 0.0;
    	
        nNonBTLOwnerOccupier = 0;
        nRenting = 0;
        nNonBTLHomeless = 0;
        nFTBinSocialHousing = 0;
        nNonBTLBankruptcies = 0;
        activeBTLAnnualisedTotalIncome = 0.0;
        ownerOccupierAnnualisedTotalIncome = 0.0;
        rentingAnnualisedTotalIncome = 0.0;
        homelessAnnualisedTotalIncome = 0.0;
        activeBTLMonthlyNetIncome = 0.0;
    	ownerOccupierMonthlyNetIncome = 0.0;
    	rentingMonthlyNetIncome = 0.0;
    	homelessMonthlyNetIncome = 0.0;
    	activeBTLMonthlyGrossEmploymentIncome = 0.0;
    	ownerOccupierMonthlyGrossEmploymentIncome = 0.0;
    	rentingMonthlyGrossEmploymentIncome = 0.0;
    	homelessMonthlyGrossEmploymentIncome = 0.0;
        sumStockYield = 0.0;
        nNonBTLBidsAboveExpAvSalePrice = 0;
        nBTLBidsAboveExpAvSalePrice = 0;
        nNonBTLBidsAboveExpAvSalePriceCounter = 0;
        nBTLBidsAboveExpAvSalePriceCounter = 0;
        //RUBEN initialise totalConsumption and Savings, etc
        totalMonthlyDisposableIncome = 0.0;
        totalBankBalancesEndPeriod = 0.0;
        totalSocialHousingRent = 0.0;
        totalConsumption = 0.0;
        totalSaving = 0.0;
        totalBankBalancesBeforeConsumption = 0.0;
        totalBankBalancesVeryBeginningOfPeriod = 0.0;
        totalBankBalanceEndowment = 0.0;
        totalPrincipalRepayments = 0.0;
        totalPrincipalRepaymentsDueToHouseSale = 0.0;
        totalPrincipalPaidBackForInheritance = 0.0;
        totalInterestRepayments = 0.0;
        totalRentalPayments = 0.0;
        totalMonthlyTaxesPaid = 0.0;
        totalMonthlyNICPaid = 0.0;
        totalBankruptcyCashInjection = 0.0;
        totalDebtReliefOfDeceasedHouseholds = 0.0;
        totalPrincipalRepaymentDeceasedHousehold = 0.0;
        nNegativeEquity = 0;
        totalIncomeConsumption = 0.0;
        totalFinancialWealthConsumption = 0.0;
        totalHousingWealthConsumption = 0.0;
        totalDebtConsumption = 0.0;
        totalSavingForDeleveraging = 0.0;
        totalNetWealth = new DescriptiveStatistics();
        grossTotalIncome = new DescriptiveStatistics();
        debtServiceRatios = new DescriptiveStatistics();
        vulnerableHouseholdsDSR = new DescriptiveStatistics();
        vulnerableHouseholdsAge = new DescriptiveStatistics();
        nonVulnerableHouseholdsAge = new DescriptiveStatistics();
        
        debtServiceRatiosAdjusted = new DescriptiveStatistics();
        vulnerableHouseholdsDSRAdjusted = new DescriptiveStatistics();
        
        totalDividendIncome = 0.0;
    }

    public void record() {
        // Initialise variables to sum
        nBTL = 0;
        nActiveBTL = 0;
        nBTLOwnerOccupier = 0;
        nBTLHomeless = 0;
        nInFirstHome = 0;
        nSSB = 0;
        nBTLBankruptcies = 0;
        
     // PAUL AIRBNB values
        nAirBnBBTL = 0;
    	airBnBRentalIncome = 0.0;
    	nAirBnBs = 0;
    	rentingMonthlyDisposableIncome = 0.0;
    	
    	nBTLRentalProperty = 0;
        
        nNonBTLOwnerOccupier = 0;
        nRenting = 0;
        nNonBTLHomeless = 0;
        nFTBinSocialHousing = 0;
        nNonBTLBankruptcies = 0;
        activeBTLAnnualisedTotalIncome = 0.0;
        ownerOccupierAnnualisedTotalIncome = 0.0;
        rentingAnnualisedTotalIncome = 0.0;
        homelessAnnualisedTotalIncome = 0.0;
        activeBTLMonthlyNetIncome = 0.0;
    	ownerOccupierMonthlyNetIncome = 0.0;
    	rentingMonthlyNetIncome = 0.0;
    	homelessMonthlyNetIncome = 0.0;
    	activeBTLMonthlyGrossEmploymentIncome = 0.0;
    	ownerOccupierMonthlyGrossEmploymentIncome = 0.0;
    	rentingMonthlyGrossEmploymentIncome = 0.0;
    	homelessMonthlyGrossEmploymentIncome = 0.0;
        sumStockYield = 0.0;
        totalPrincipalRepayments = 0.0;
        totalPrincipalRepaymentsDueToHouseSale = 0.0;
        totalPrincipalPaidBackForInheritance = 0.0;
        totalInterestRepayments = 0.0;
        totalRentalPayments = 0.0;
        totalMonthlyTaxesPaid = 0.0;
        totalMonthlyNICPaid = 0.0;
        totalBankruptcyCashInjection = 0.0;
        //RUBEN initialise nNegativeEquity
        nNegativeEquity = 0;
        totalNetWealth.clear();
        totalDividendIncome = 0.0;
        medianIncome = grossTotalIncome.getPercentile(50);
        grossTotalIncome.clear();
        medianDSR = debtServiceRatios.getPercentile(50);
        debtServiceRatios.clear();
        medianDSRVulnerableHouseholds = vulnerableHouseholdsDSR.getPercentile(50);
        vulnerableHouseholdsDSR.clear();
        medianAgeVulnerableHouseholds = vulnerableHouseholdsAge.getPercentile(50);
        vulnerableHouseholdsAge.clear();
        medianAgeNonVulnerableHouseholds = nonVulnerableHouseholdsAge.getPercentile(50);
        nonVulnerableHouseholdsAge.clear();
        
        medianDSRAdjusted = debtServiceRatiosAdjusted.getPercentile(50);
        debtServiceRatiosAdjusted.clear();
        medianDSRVulnerableHouseholdsAdjusted = vulnerableHouseholdsDSRAdjusted.getPercentile(50);
        vulnerableHouseholdsDSRAdjusted.clear();
        // Time stamp householdStats mesoRecorders
        Model.microDataRecorder.timeStampSingleRunSingleVariableFiles(Model.getTime(), config.recordBankBalance,
                config.recordHousingWealth, config.recordNHousesOwned, config.recordSavingRate, config.recordMonthlyGrossTotalIncome,
                config.recordMonthlyGrossEmploymentIncome, config.recordMonthlyGrossRentalIncome, config.recordMonthlyDisposableIncome,
                config.recordMonthlyMortgagePayments, config.recordDebt, config.recordConsumption, config.recordIncomeConsumption, 
                config.recordFinancialWealthConsumption, config.recordHousingWealthConsumption, config.recordDebtConsumption, 
                config.recordSavingForDeleveraging, config.recordBTL, config.recordFTB, config.recordInFirstHome, config.recordAge,
                config.recordTransactionRevenue, config.recordId, config.recordNewCredit, config.recordPrincipalRepRegular
                , config.recordPrincipalRepIrregular, config.recordPrincipalRepSale, config.recordBankcuptcyCashInjection, 
        		config.recordPrincipalPaidBackInheritance, config.recordFinancialVulnerability, config.recordShockedMonthlyDisposableIncome);
        // Run through all households counting population in each type and summing their gross incomes
        for (Household h : Model.households) {
        	
        	// only start the following code when the recorder starts
        	if(Model.getTime()>=(config.TIME_TO_START_RECORDING)) {
        		// This records the agent-specific consumption and number of SSB and inFirstHome agents
            	// (as agent classes are divided here different than in the main method)
            	recordAgentSpecificConsumption(h);
        	}
        	
        	// record the exposure at default for each household with different measures
        	countExposureAtDefault(h);
        	
        	// record the age of non-vulnerable but indebted households (debt is a negative value!)
        	if (!h.isVulnerable() && h.getTotalDebt() < 0){
        		nonVulnerableHouseholdsAge.addValue(h.getAge());
        	}
        	 if(h.isVulnerable()) {
        		 vulnerableHouseholdsAge.addValue(h.getAge());
        		 countCurrentlyVulnerableHouseholds(h); 
        	 } 

        	        	
            totalMonthlyDisposableIncomeCounter += h.returnMonthlyDisposableIncome();
            totalBankBalancesEndPeriodCounter += h.getBankBalance();
            totalSocialHousingRentCounter += h.getSocialHousingRent();
        	
        	//TODO Ruben: check if removable, as I implemented totalPrincipalRepaymentDeceasedHousehold
        	// record household fields containing credit repayments, rent payments and cash injections
            totalPrincipalRepaymentsCounter += h.getPrincipalPaidBack();
            totalPrincipalRepaymentsDueToHouseSaleCounter += h.getPrincipalDueToHouseSale();
            //totalPrincipalPaidBackForInheritanceCounter += h.getPrincipalPaidBackForInheritance();
            // the principal repayments due to inheritance are recorded before households are managed..
            //... therefore they are set back to zero here and not in the household.step() method
            //h.setPrincipalPaidBackForInheritance(0.0);
            totalInterestRepaymentsCounter += h.getInterestPaidBack();
            totalRentalPaymentsCounter += h.getRentalPayment();
            totalMonthlyTaxesPaidCounter += h.getMonthlyTaxesPaid();
            totalMonthlyNICPaidCounter += h.getMonthlyNICPaid();
            totalBankruptcyCashInjectionCounter += h.getCashInjection();
            totalDividendIncomeCounter += h.recordMonthlyDividendIncome();
        	
            // count if the household had a negative equity position at the beginning of the period
            if (h.getEquityPosition() < 0) {
//            	if(h.getSavingForDeleveraging()<0.01 && Model.getTime()>2600) {
//            		System.out.println("stop, this is weird, equity position is: " + h.getEquityPosition() + " and they saved: " + h.getSavingForDeleveraging());
//            	}
            	nNegativeEquity++;
            }
            
        	if (h.behaviour.isPropertyInvestor()) {
                ++nBTL;
                if (h.isBankrupt()) nBTLBankruptcies += 1;
                // Active BTL investors
                if (h.getNProperties() > 1) {
                    ++nActiveBTL;
                    nBTLRentalProperty += h.getNProperties()-1; 
                    activeBTLAnnualisedTotalIncome += h.returnMonthlyGrossTotalIncome();
                    activeBTLMonthlyNetIncome += h.returnMonthlyNetTotalIncome();
                    activeBTLMonthlyGrossEmploymentIncome += h.getMonthlyGrossEmploymentIncome();
                // Inactive BTL investors who own their house
                } else if (h.getNProperties() == 1) {
                    ++nBTLOwnerOccupier;
                    ownerOccupierAnnualisedTotalIncome += h.returnMonthlyGrossTotalIncome();
                    ownerOccupierMonthlyNetIncome += h.returnMonthlyNetTotalIncome();
                    ownerOccupierMonthlyGrossEmploymentIncome += h.getMonthlyGrossEmploymentIncome();
                    // Inactive BTL investors in social housing
                } else {
                    ++nBTLHomeless;
                    homelessAnnualisedTotalIncome += h.returnMonthlyGrossTotalIncome();
                    homelessMonthlyNetIncome += h.returnMonthlyNetTotalIncome();
                    homelessMonthlyGrossEmploymentIncome += h.getMonthlyGrossEmploymentIncome();
                }
                
                // PAUL additional recordings for AirBnB investors
                if(h.behaviour.isAirBnBInvestor()) {
                	++nAirBnBBTL;
                	airBnBRentalIncome += h.getAirBnBRentalIncome();
                	nAirBnBs += h.getnAirBnBRentedOut();
                }
            } else {
                if (h.isBankrupt()) nNonBTLBankruptcies += 1;
                // Non-BTL investors who own their house
                if (h.isHomeowner()) {
                    ++nNonBTLOwnerOccupier;
                    ownerOccupierAnnualisedTotalIncome += h.returnMonthlyGrossTotalIncome();
                    ownerOccupierMonthlyNetIncome += h.returnMonthlyNetTotalIncome();
                    ownerOccupierMonthlyGrossEmploymentIncome += h.getMonthlyGrossEmploymentIncome();
                    // Non-BTL investors renting
                } else if (h.isRenting()) {
                    ++nRenting;
                    rentingAnnualisedTotalIncome += h.returnMonthlyGrossTotalIncome();
                    rentingMonthlyNetIncome += h.returnMonthlyNetTotalIncome();
                    rentingMonthlyGrossEmploymentIncome += h.getMonthlyGrossEmploymentIncome();
                    // PAUL
                    rentingMonthlyDisposableIncome += h.returnMonthlyDisposableIncome();
                    
                    if (Model.housingMarketStats.getExpAvSalePriceForQuality(h.getHome().getQuality()) > 0) {
                        sumStockYield += h.getHousePayments().get(h.getHome()).monthlyPayment
                                *config.constants.MONTHS_IN_YEAR
                                /Model.housingMarketStats.getExpAvSalePriceForQuality(h.getHome().getQuality());
                    }
                    // Non-BTL investors in social housing
                } else if (h.isInSocialHousing()) {
                	if(h.isFirstTimeBuyer()) { ++nFTBinSocialHousing;}
                    ++nNonBTLHomeless;
                    homelessAnnualisedTotalIncome += h.returnMonthlyGrossTotalIncome();
                    homelessMonthlyNetIncome += h.returnMonthlyNetTotalIncome();
                    homelessMonthlyGrossEmploymentIncome += h.getMonthlyGrossEmploymentIncome();
                }
            }
        	
        	// record the total net wealth into the descriptive statistics
        	if(Model.getTime()>=config.TIME_TO_START_RECORDING) {
        		totalNetWealth.addValue(h.getEquityPosition());
        	}
        	// start recording of median income earlier, as in t the value for t-3 is used
        	// this way the vulnerability measures can be calculated before the recording starts

        	grossTotalIncome.addValue(h.returnMonthlyGrossTotalIncome());

        	
        	// implement to capture median DSR for a quasi-collateral channel
        	double debtPayments = h.getPrincipalPaidBack() + h.getInterestPaidBack();
        	if(debtPayments > 0) {
        		debtServiceRatios.addValue(debtPayments/h.returnMonthlyGrossTotalIncome());
        		debtServiceRatiosAdjusted.addValue(debtPayments / (h.returnMonthlyGrossTotalIncome() - 
        				0.2 * medianIncome));
        		indebtedHouseholdsCounter += 1;
        	}
        	
        	
            // Record household micro-data 
        	if(Model.getTime()>=config.TIME_TO_START_RECORDING) {
        		if (config.recordBankBalance) {
        			Model.microDataRecorder.recordBankBalance(Model.getTime(), h.getBankBalance());
        		}
        		if (config.recordHousingWealth) {
        			// Housing wealth is computed as mark-to-market net housing wealth, thus looking at current average
        			// prices for houses of the same quality
        			double housingWealth = 0.0;
        			for (Map.Entry<House, PaymentAgreement> entry : h.getHousePayments().entrySet()) {
        				House house = entry.getKey();
        				PaymentAgreement payment = entry.getValue();
        				if (payment instanceof MortgageAgreement && house.owner == h) {
        					housingWealth += Model.housingMarketStats.getExpAvSalePriceForQuality(house.getQuality())
        							- ((MortgageAgreement) payment).principal;
        				}
        			}
        			Model.microDataRecorder.recordHousingWealth(Model.getTime(), housingWealth);
        		}

        		if (config.recordNHousesOwned) {
        			Model.microDataRecorder.recordNHousesOwned(Model.getTime(), h.getNProperties());
        		}
        		if (config.recordSavingRate) {
        			Model.microDataRecorder.recordSavingRate(Model.getTime(), h.getSavingRate());
        		}
        		if(config.recordMonthlyGrossTotalIncome) {
        			Model.microDataRecorder.recordMonthlyGrossTotalIncome(Model.getTime(), h.returnMonthlyGrossTotalIncome());
        		}
        		if(config.recordMonthlyGrossEmploymentIncome) {
        			Model.microDataRecorder.recordMonthlyGrossEmploymentIncome(Model.getTime(), h.getMonthlyGrossEmploymentIncome());
        		}
        		if(config.recordMonthlyGrossRentalIncome) {
        			Model.microDataRecorder.recordMonthlyGrossRentalIncome(Model.getTime(), h.returnMonthlyGrossRentalIncome());
        		}
        		if(config.recordMonthlyDisposableIncome) {
        			Model.microDataRecorder.recordMonthlyDisposableIncome(Model.getTime(), h.returnMonthlyDisposableIncome());
        		}
        		if(config.recordMonthlyMortgagePayments) {
        			Model.microDataRecorder.recordMonthlyMortgagePayments(Model.getTime(), (h.getPrincipalPaidBack()+h.getInterestPaidBack()));
        		}
        		if(config.recordDebt) {
        			Model.microDataRecorder.recordDebt(Model.getTime(), h.getTotalDebt());
        		}
        		if(config.recordConsumption) {
        			// record non-essential and essential consumption
        			Model.microDataRecorder.recordConsumption(Model.getTime(), (h.getConsumption()));
        		}
        		if(config.recordIncomeConsumption) {
        			// record non-essential income consumption and essential consumption
        			Model.microDataRecorder.recordIncomeConsumption(Model.getTime(), (h.getIncomeConsumption()));
        		}
        		if(config.recordFinancialWealthConsumption) {
        			// record consumption induced by financial wealth
        			Model.microDataRecorder.recordFinancialWealthConsumption(Model.getTime(), (h.getFinancialWealthConsumption()));
        		}
        		if(config.recordHousingWealthConsumption) {
        			// record consumption induced by housing wealth
        			Model.microDataRecorder.recordHousingWealthConsumption(Model.getTime(), (h.getHousingWealthConsumption()));
        		}
        		if(config.recordDebtConsumption) {
        			// record consumption induced by debt
        			Model.microDataRecorder.recordDebtConsumption(Model.getTime(), (h.getDebtConsumption()));
        		}
        		if(config.recordSavingForDeleveraging) {
        			// record consumption reduction induced by negative equity position of the household
        			Model.microDataRecorder.recordSavingForDeleveraging(Model.getTime(), (h.getSavingForDeleveraging()));
        		}
        		if(config.recordBTL) {
        			Model.microDataRecorder.recordBTL(Model.getTime(), h.behaviour.isPropertyInvestor());
        		}
        		if(config.recordFTB) {
        			Model.microDataRecorder.recordFTB(Model.getTime(), h.isFirstTimeBuyer());
        		}
        		if(config.recordInFirstHome){
        			Model.microDataRecorder.recordInFirstHome(Model.getTime(), h.isInFirstHome());
        		}
        		if(config.recordAge) {
        			Model.microDataRecorder.recordAge(Model.getTime(), h.getAge());
        		}
        		if(config.recordTransactionRevenue) {
        			Model.microDataRecorder.recordTransactionRevenue(Model.getTime(), h.getNetHouseTransactionRevenue());
        		}
        		if(config.recordId) {
        			Model.microDataRecorder.recordId(Model.getTime(), h.getId());
        		}
        		if(config.recordNewCredit) {
        			Model.microDataRecorder.recordNewCredit(Model.getTime(), h.getNewCredit());
        		}
         		if(config.recordPrincipalRepRegular) {
        			// principal paid back for inheritance is positive, debt relief is positive (negative NEGATIVE bankBalance value is the input
        			Model.microDataRecorder.recordPrincipalRepRegular(Model.getTime(), h.getPrincipalPaidBack());
        		}
        		if(config.recordPrincipalRepIrregular) {
        			// principal paid back for inheritance is positive, debt relief is positive (negative NEGATIVE bankBalance value is the input
        			Model.microDataRecorder.recordPrincipalRepIrregular(Model.getTime(), (h.getPrincipalPaidBackForInheritance()
        					 + h.getDebtReliefForBequeather()));
        			// the values stored when a household died in the beginning of the period have to be set back to zero here at the end of the period
        			// TODO so far, this leads to too high values in the first period, as they have not been reset until then
        			// but only reset to zero here, if recordPrincipalPaidBackInheritance is inactive
        			if(!config.recordPrincipalPaidBackInheritance)h.resetPrincipalPaidBackForInheritance();
        			h.resetDebtReliefForBequeather();
        		}
        		if(config.recordPrincipalRepSale) {
        			Model.microDataRecorder.recordPrincipalRepSale(Model.getTime(), h.getPrincipalDueToHouseSale());
        		} 
        		if(config.recordBankcuptcyCashInjection) {
        			Model.microDataRecorder.recordBankcuptcyCashInjection(Model.getTime(), h.getCashInjection());
        		}    
        		if(config.recordPrincipalPaidBackInheritance) {
        			Model.microDataRecorder.recordPrincipalPaidBackInheritance(Model.getTime(), h.getPrincipalPaidBackForInheritance());
        			h.resetPrincipalPaidBackForInheritance();
        		}
        		if(config.recordFinancialVulnerability) {
        			Model.microDataRecorder.recordFinancialVulnerability(Model.getTime(), h.getVulnerableBecause(), h.getVulnerableSince());
        		}
        		if(config.recordShockedMonthlyDisposableIncome) {
        			Model.microDataRecorder.recordShockedMonthlyDisposableIncome(Model.getTime(), h.getShockedMonthlyDisposableIncome());
        		}
        	}
        }
        
        
    	//
		if(Model.getTime()>= (config.TIME_TO_START_RECORDING-1)) recordExposureAtDefault();
		
             
        
        // Annualise monthly income data
        activeBTLAnnualisedTotalIncome *= config.constants.MONTHS_IN_YEAR;
        ownerOccupierAnnualisedTotalIncome *= config.constants.MONTHS_IN_YEAR;
        rentingAnnualisedTotalIncome *= config.constants.MONTHS_IN_YEAR;
        homelessAnnualisedTotalIncome *= config.constants.MONTHS_IN_YEAR;
        // Pass number of bidders above the exponential moving average sale price to persistent variable and
        // re-initialise to zero the counter
        nNonBTLBidsAboveExpAvSalePrice = nNonBTLBidsAboveExpAvSalePriceCounter;
        nBTLBidsAboveExpAvSalePrice = nBTLBidsAboveExpAvSalePriceCounter;
        nNonBTLBidsAboveExpAvSalePriceCounter = 0;
        nBTLBidsAboveExpAvSalePriceCounter = 0;
        
        // Ruben additional counters - pass counter number to aggregate double then reset counter
        
        totalMonthlyDisposableIncome = totalMonthlyDisposableIncomeCounter;
        totalMonthlyDisposableIncomeCounter = 0.0;
        totalBankBalancesEndPeriod = totalBankBalancesEndPeriodCounter;
        totalBankBalancesEndPeriodCounter = 0.0;
        
        totalSocialHousingRent = totalSocialHousingRentCounter;
        totalSocialHousingRentCounter = 0.0;
        
        totalDividendIncome = totalDividendIncomeCounter;
        totalDividendIncomeCounter = 0.0;
        
        totalConsumption = totalConsumptionCounter;
        totalSaving = totalSavingCounter;
        totalConsumptionCounter = 0.0;
        totalSavingCounter = 0.0;
        totalBankBalancesBeforeConsumption = totalBankBalancesBeforeConsumptionCounter;
        totalBankBalancesVeryBeginningOfPeriod = totalBankBalancesVeryBeginningOfPeriodCounter;
        totalBankBalanceEndowment = totalBankBalanceEndowmentCounter;
        totalBankBalancesBeforeConsumptionCounter = 0.0;
        totalBankBalancesVeryBeginningOfPeriodCounter = 0.0;
        totalBankBalanceEndowmentCounter = 0.0;
        
        totalPrincipalRepayments = totalPrincipalRepaymentsCounter;
        totalPrincipalRepaymentsDueToHouseSale = totalPrincipalRepaymentsDueToHouseSaleCounter;
        totalPrincipalPaidBackForInheritance = totalPrincipalPaidBackForInheritanceCounter;
        totalInterestRepayments = totalInterestRepaymentsCounter;
        totalRentalPayments = totalRentalPaymentsCounter;
        totalMonthlyTaxesPaid = totalMonthlyTaxesPaidCounter;
        totalMonthlyNICPaid = totalMonthlyNICPaidCounter;
        totalBankruptcyCashInjection = totalBankruptcyCashInjectionCounter;
        totalPrincipalRepaymentsCounter = 0.0;
        totalPrincipalRepaymentsDueToHouseSaleCounter = 0.0;
        totalPrincipalPaidBackForInheritanceCounter = 0.0;
        totalInterestRepaymentsCounter = 0.0;
        totalRentalPaymentsCounter = 0.0;
        totalMonthlyTaxesPaidCounter = 0.0;
        totalMonthlyNICPaidCounter = 0.0;
        totalBankruptcyCashInjectionCounter = 0.0;
        
        totalDebtReliefOfDeceasedHouseholds = totalDebtReliefOfDeceasedHouseholdsCounter;
        totalDebtReliefOfDeceasedHouseholdsCounter = 0.0;
        totalPrincipalRepaymentDeceasedHousehold = totalPrincipalRepaymentDeceasedHouseholdCounter;
        totalPrincipalRepaymentDeceasedHouseholdCounter = 0.0;
        
        totalIncomeConsumption = totalIncomeConsumptionCounter;
        totalFinancialWealthConsumption = totalFinancialWealthConsumptionCounter;
        totalHousingWealthConsumption = totalHousingWealthConsumptionCounter;
        totalDebtConsumption = totalDebtConsumptionCounter;
        totalSavingForDeleveraging = totalSavingForDeleveragingCounter;
        totalIncomeConsumptionCounter = 0.0;
        totalFinancialWealthConsumptionCounter = 0.0;
        totalHousingWealthConsumptionCounter = 0.0;
        totalDebtConsumptionCounter = 0.0;
        totalSavingForDeleveragingCounter = 0.0;
        
        activeBTLIncomeConsumption = activeBTLIncomeConsumptionCounter;
        activeBTLFinancialWealthConsumption = activeBTLFinancialWealthConsumptionCounter;
        activeBTLNetHousingWealthConsumption = activeBTLNetHousingWealthConsumptionCounter;
        SSBIncomeConsumption = SSBIncomeConsumptionCounter;
        SSBFinancialWealthConsumption = SSBFinancialWealthConsumptionCounter;
        SSBNetHousingWealthConsumption = SSBNetHousingWealthConsumptionCounter;
        inFirstHomeIncomeConsumption = inFirstHomeIncomeConsumptionCounter;
        inFirstHomeFinancialWealthConsumption = inFirstHomeFinancialWealthConsumptionCounter;
        inFirstHomeNetHousingWealthConsumption = inFirstHomeNetHousingWealthConsumptionCounter;
        renterIncomeConsumption = renterIncomeConsumptionCounter;
        renterFinancialWealthConsumption = renterFinancialWealthConsumptionCounter;
        renterNetHousingWealthConsumption = renterNetHousingWealthConsumptionCounter;
    	
    	activeBTLIncomeConsumptionCounter = 0.0;
        activeBTLFinancialWealthConsumptionCounter = 0.0;
    	activeBTLNetHousingWealthConsumptionCounter = 0.0;
    	SSBIncomeConsumptionCounter = 0.0;
    	SSBFinancialWealthConsumptionCounter = 0.0;
    	SSBNetHousingWealthConsumptionCounter = 0.0;
    	inFirstHomeIncomeConsumptionCounter = 0.0;
    	inFirstHomeFinancialWealthConsumptionCounter = 0.0;
    	inFirstHomeNetHousingWealthConsumptionCounter = 0.0;
    	renterIncomeConsumptionCounter = 0.0;
    	renterFinancialWealthConsumptionCounter = 0.0;
    	renterNetHousingWealthConsumptionCounter = 0.0;
        
    	indebtedHouseholds = indebtedHouseholdsCounter;
    	indebtedHouseholdsCounter = 0;


    }

    /**
     * Count number of normal (non-BTL) bidders with desired expenditures above the (minimum quality, q=0) exponential
     * moving average sale price
     */
    public void countNonBTLBidsAboveExpAvSalePrice(double price) {
        if (price >= Model.housingMarketStats.getExpAvSalePriceForQuality(0)) {
            nNonBTLBidsAboveExpAvSalePriceCounter++;
        }
    }
    
    /**
     * Count number of BTL bidders with desired expenditures above the (minimum quality, q=0) exponential moving average
     * sale price
     */
    public void countBTLBidsAboveExpAvSalePrice(double price) {
        if (price >= Model.housingMarketStats.getExpAvSalePriceForQuality(0)) {
            nBTLBidsAboveExpAvSalePriceCounter++;
        }
    }
    
    // count consumption out of wealth and consumption out of income
    public void countIncomeAndWealthConsumption(double saving, double consumption, double incomeConsumption, double financialWealthConsumption, 
    											double housingWealthConsumption, double debtConsumption, double savingForDeleveraging) {
    	totalSavingCounter += saving;
    	totalConsumptionCounter += consumption;
    	totalIncomeConsumptionCounter += incomeConsumption;
    	totalFinancialWealthConsumptionCounter += financialWealthConsumption;
    	totalHousingWealthConsumptionCounter += housingWealthConsumption;
    	totalDebtConsumptionCounter += debtConsumption;
    	totalSavingForDeleveragingCounter += savingForDeleveraging;
    }
    
    public void recordBankBalanceBeforeConsumption(double bankBalance) {
    	totalBankBalancesBeforeConsumptionCounter += bankBalance;
    }
    
    public void recordBankBalanceVeryBeginningOfPeriod(double bankBalance) {
    	totalBankBalancesVeryBeginningOfPeriodCounter += bankBalance;
    }
    
    public void recordBankBalanceEndowment(double bankBalance) {
    	totalBankBalanceEndowmentCounter += bankBalance;
    }

	public void recordDebtReliefDeceasedHousehold(double principal) {
		// record the debt relief of a deceased household
		totalDebtReliefOfDeceasedHouseholdsCounter += principal;
	}
	
	public void recordPrincipalRepaymentDeceasedHousehold(double principal) {
		totalPrincipalRepaymentDeceasedHouseholdCounter += principal;
	}
	
	
	// method to record agent-specific consumption in a more condensed way
	private void recordAgentSpecificConsumption(Household h) {
		// active BTL investors
		if (h.behaviour.isPropertyInvestor() & h.getNProperties() > 1) {
			activeBTLIncomeConsumptionCounter += h.getIncomeConsumption();
			activeBTLFinancialWealthConsumptionCounter += h.getFinancialWealthConsumption();
			activeBTLNetHousingWealthConsumptionCounter += h.getHousingWealthConsumption() + h.getDebtConsumption();
			
			// SSB -> homeowner and not in First Home
		} else if (h.isHomeowner() & !h.isInFirstHome()){
			++nSSB; 
			SSBIncomeConsumptionCounter += h.getIncomeConsumption();
			SSBFinancialWealthConsumptionCounter += h.getFinancialWealthConsumption();
			SSBNetHousingWealthConsumptionCounter += h.getHousingWealthConsumption() + h.getDebtConsumption();
			
			// inFirstHome 
		} else if (h.isHomeowner() & h.isInFirstHome()){
			++nInFirstHome;
			inFirstHomeIncomeConsumptionCounter += h.getIncomeConsumption();
			inFirstHomeFinancialWealthConsumptionCounter += h.getFinancialWealthConsumption();
			inFirstHomeNetHousingWealthConsumptionCounter += h.getHousingWealthConsumption() + h.getDebtConsumption();

			// Renting (BTL, SSB and FTB)	
		} else if (h.isRenting() | h.isInSocialHousing()) {
			renterIncomeConsumptionCounter += h.getIncomeConsumption();
			renterFinancialWealthConsumptionCounter += h.getFinancialWealthConsumption();
			// there could be cases where households have inherited houses and move out of their home before
			// selling off all their inherited houses 
			renterNetHousingWealthConsumptionCounter += h.getHousingWealthConsumption() + h.getDebtConsumption();
		} else {
			System.out.println("weird, not a household that I know (consumption recorder)");
		}

	}
	
	// Method to record the Exposure at default (i.e. summing all debt of households fitting the 
	// conditions) (following Ampudia et al. 2016) when looking at 
	// several indicators, like Debt-Service ratios, negative financial margins, and Ampudias
	// measure of financial vulnerability (with certain calibrations)
	// using household net income, as it is closest to Ampudias disposable income
	// (while not being directly defined in their paper)
	private void countExposureAtDefault(Household h) {
		//		System.out.println("countExposureAtDefault at time: " + Model.getTime() + " with id: " + h.getId());
		// debt is recorded in negative values, but for the purpose of this method use positive value
		double householdDebt = - h.getTotalDebt();
		double debtPayments = h.getPrincipalPaidBack() + h.getInterestPaidBack();
		double deposits = h.getBankBalance();

		// count the households with less than X dollars in their bank account
		if (deposits < 1500) { 
			HouseholdsWithLessThan1500pCounter += 1; 
			lowDepositHouseholdConsumptionCounter += h.getConsumption();
			lowDepositHouseholdSavingCounter += h.returnMonthlyDisposableIncome() - h.getConsumption();
		}

		// TODO record these measures at the beginning of the period in the household.java file to be consistent
		
		
		// calculate the financial margins with different basic-living cost calculations
		// fin. margin is hh net income less mortgage payments (i.e. disposable income)
		// only households holding debt are considered as we are interested in the exposure at default.
		double financialMargin20BLC = h.returnMonthlyDisposableIncome() - 
				0.2*medianIncome; 
		double financialMargin40BLC = h.returnMonthlyDisposableIncome() - 
				0.4*medianIncome;
		double financialMargin70BLC = h.returnMonthlyDisposableIncome() - 
				0.7*medianIncome;
		double financialMargin;
    	// if gross total income is not shocked extra, then only shock according to "povertyLinePercentMedianIncome". If it is 0.4 it is effectively not shocked
    	// 0.6 means a shock of 20% of median income
    	if(config.incomeShock == 0) {
    		financialMargin = 
    				h.returnMonthlyDisposableIncome() - config.povertyLinePercentMedianIncome * medianIncome;
    	} else {
        	// to arrive a the shocked disposable income, the new shocked net income is reduced 
        	// by the difference between original net income and disposable income
    		financialMargin = 
    				h.calculateShockedDisposableIncomeForVulnerability() - 
    				config.povertyLinePercentMedianIncome * medianIncome;
    	}
		if (financialMargin20BLC < 0) { ExposureAtDefaultFinancialMarginBLC20Counter +=  householdDebt;	}
		if (financialMargin40BLC < 0) { ExposureAtDefaultFinancialMarginBLC40Counter +=  householdDebt;	}
		if (financialMargin70BLC < 0) { ExposureAtDefaultFinancialMarginBLC70Counter +=  householdDebt;	}

		// calculate the Ampudia et al. (2016) measure.
		double monthsCoveredByDepositsVar = deposits / financialMargin;
		// test the negative financial margin is at least X times the deposits
		if(financialMargin < 0 & (- config.finVulMonthsToCover * financialMargin) > deposits) {
			// do not simply add all debt, but between 0 and 100% of it, according to a linear function 
			// with 100% for no deposits and 0% for deposits being equal to the month threshold
			double factor = monthsCoveredByDepositsVar / 6  + 1; 
			ExposureAtDefaultAmpudiaMeasure1Counter += factor * householdDebt;
		}
		if(h.isVulnerable()) {
			double factor = h.getEADFactor(); 
			ExposureAtDefaultAmpudiaMeasure2Counter += factor * householdDebt;
			householdsVulnerableAmpudiaMeasure2Counter += 1;
			// active BTL
			if (h.behaviour.isPropertyInvestor() & h.getNProperties() > 1) {
				activeBTLVulnerableCounter += 1;
				activeBTLEADCounter += factor * householdDebt;
				// record cause for vulnerability if the household became newly vulnerable
				if(!h.isVulnerableTMinus1()) vulnerabilityCause(h, "BTL");
				// SSB -> homeowner and not in First Home
				// households need not to be homeowners right now (at the end of period t)
				// because they might just have sold their home. Which will render them 
				// non-vulnerable by the beginning of t+1, where their reason for becoming
				// non-vulnerable will be recorded (sales)
			} else if (!h.isInFirstHome()){
				SSBVulnerableCounter += 1;
				SSBEADCounter += factor * householdDebt;
				if(!h.isVulnerableTMinus1()) vulnerabilityCause(h, "SSB");
				// inFirstHome 
			} else if (h.isInFirstHome()){
				inFirstHomeVulnerableCounter += 1;
				inFirstHomeEADCounter += factor * householdDebt;
				if(!h.isVulnerableTMinus1()) vulnerabilityCause(h, "inFirstHome");
				// Renting (BTL, SSB and FTB)	
			} else if (!h.isVulnerableTMinus1()){
				System.out.println("weird, not a (newly vulnerable) household that I know (vulnerability recorder)");
			}
		}


		// check if household used to be vulnerable, but is not this period anymore
		// then record the reason why it isn't anymore
		if(!h.isVulnerable() && h.isVulnerableTMinus1()) {
			notVulnerableBecause(h);
			// reset the period where the households vulnerability started
			h.setVulnerableSince(-1);
		}

		// only count households with mortgage debt that already pay their debt (so the period after they bought a house)
		if(householdDebt > 0 && debtPayments > 0) { 
			double DSR = debtPayments/h.returnMonthlyNetTotalIncome();
			// record Debt of households matching the following conditions:
			// debt-service ratios above...
			if(DSR >= 0.3)  { ExposureAtDefaultDSR30Counter += householdDebt; }
			if(DSR >= 0.35) { ExposureAtDefaultDSR35Counter += householdDebt; }
			if(DSR >= 0.7)  { ExposureAtDefaultDSR70Counter += householdDebt; }

			// TODO WARNING this code is adopted, the variable names are not coherent
			if(DSR > 0.35) {
				unemploymentExposureAtDefaultAmpudiaMeasure2Counter += householdDebt;
				unemploymentHouseholdsVulnerableAmpudiaMeasure2Counter += 1;
				// active BTL
				if (h.behaviour.isPropertyInvestor() && h.getNProperties() > 1) {
					unemploymentActiveBTLVulnerableCounter += 1;
					unemploymentActiveBTLEADCounter += householdDebt;
					// SSB -> homeowner and not in First Home
				} else if (h.isHomeowner() && !h.isInFirstHome()){
					unemploymentSSBVulnerableCounter += 1;
					unemploymentSSBEADCounter += householdDebt;
					// inFirstHome 
				} else if (h.isHomeowner() && h.isInFirstHome()){
					unemploymentinFirstHomeVulnerableCounter += 1;
					unemploymentinFirstHomeEADCounter += householdDebt;
					// Renting (BTL, SSB and FTB)	
				} else {
					System.out.println("weird, not a household that I know (vulnerability recorder)");
				}
			}
		}
	}
	
	// households that became vulnerable enter here
	private void vulnerabilityCause(Household h, String AgentType) {
		// saving is calculated with the values from the beginning of the period
		double saving = h.returnMonthlyDisposableIncome() - h.getConsumption();
		int lastPurchasePeriod = (Model.getTime() - h.getLastHousePurchasePeriod());

		// first case: household just bought a home (in period t-1)
		if( lastPurchasePeriod == 1 ) { // normal: lastPurchasePeriod ==1 or  <= 120
			h.setVulnerableBecause("purchase");
			vulnerableByPurchaseCounter += 1;
			if (AgentType == "BTL") {
				vulnerableByPurchaseBTLCounter += 1;
			}
			else if (AgentType == "SSB") vulnerableByPurchaseSSBCounter += 1;
			else if (AgentType == "inFirstHome") vulnerableByPurchaseInFirstHomeCounter += 1;
		} 
		// second case: household didn't not just buy a home and was dissaving
		else if ( lastPurchasePeriod != 1 && saving < 0.0 ) { // normal: lastPurchasePeriod !=1 or > 120
			h.setVulnerableBecause("dissaving");
			vulnerableByConsumptionCounter +=1;
			if (AgentType == "BTL") {
				vulnerableByConsumptionBTLCounter += 1;
			}
			else if (AgentType == "SSB") vulnerableByConsumptionSSBCounter += 1;
			else if (AgentType == "inFirstHome") vulnerableByConsumptionInFirstHomeCounter += 1;
//			System.out.println("lastPurchase: " + lastPurchasePeriod);
		} 
		// third case: odd, if it didn't dissave and didn't just buy a home, how can
		// it become vulnerable? For instance lower income, turning financial margin negative
		else if ( lastPurchasePeriod != 1 && saving >= 0.0 ) { // normal: lastPurchasePeriod !=1 or > 120
//			System.out.println("weird, household became vulnerable without dissaving or buying a house");
			h.setVulnerableBecause("other");
			vulnerableByOtherCounter += 1;
			if (AgentType == "BTL") vulnerableByOtherBTLCounter += 1;
			else if (AgentType == "SSB") vulnerableByOtherSSBCounter += 1;
			else if (AgentType == "inFirstHome") vulnerableByOtherInFirstHomeCounter += 1;
		} else {
			System.out.println("weird, household became vulnerable but not caught by the functions before");
		}
		h.setVulnerableSince(Model.getTime());
	}
		
	// households that have been vulnerable the period before but are not anymore enter here
	// to record the reason they are not vulnerable anymore
	// additionally their agent-type has to be determined
	private void notVulnerableBecause(Household h) {
		// saving is calculated with the values from the beginning of the period
		double saving = h.returnMonthlyDisposableIncome() - h.getConsumption();
		int lastSalePeriod 	   = (Model.getTime() - h.getLastHouseSalePeriod());
				
		// first case: household sold property (thereby possibly reducing debt payments and increasing deposits)
		if( lastSalePeriod == 1 ) {
			notVulnerableBecauseSaleCounter += 1;
			if(h.behaviour.isPropertyInvestor() & h.getNProperties() > 1) notVulnerableBecauseSaleBTLCounter +=1;
			// I do not check for homeownership, as some households become renters, but the important information is what they were before
			else if (!h.isInFirstHome()) notVulnerableBecauseSaleSSBCounter +=1; 
			else if (h.isInFirstHome()) notVulnerableBecauseSaleInFirstHomeCounter +=1;
			else notVulnerableBecauseSaleOthersCounter +=1;
		} 
		// second case: household did not sell anything recently but saved 
		else if ( lastSalePeriod != 1 && saving > 0.0 ) {
			notVulnerableBecauseSavingCounter += 1;
			if(h.behaviour.isPropertyInvestor() & h.getNProperties() > 1) {
				notVulnerableBecauseSavingBTLCounter += 1;
//				System.out.println("have been vulnerable for: " + (Model.getTime()-h.getVulnerableSince()) + "  because: " + h.getVulnerableBecause());
			}
			else if (h.isHomeowner() & !h.isInFirstHome()) notVulnerableBecauseSavingSSBCounter +=1;
			else if (h.isHomeowner() & h.isInFirstHome()) notVulnerableBecauseSavingInFirstHomeCounter +=1;
			else notVulnerableBecauseSavingOthersCounter += 1;
		} 	
		else if ( lastSalePeriod != 1 && saving <= 0.0 ) {
//			System.out.println("weird, household ceased to be vulnerable without saving or selling a house");
			notVulnerableBecauseOtherCounter += 1;
			if(h.behaviour.isPropertyInvestor() & h.getNProperties() > 1) notVulnerableBecauseOtherBTLCounter += 1;
			else if (h.isHomeowner() & !h.isInFirstHome()) notVulnerableBecauseOtherSSBCounter += 1;
			else if (h.isHomeowner() & h.isInFirstHome()) notVulnerableBecauseOtherInFirstHomeCounter += 1;
			else notVulnerableBecauseOtherOthersCounter += 1;
		} else {
			System.out.println("weird, household ceased to be vulnerable but not caught by the functions before");
		}
		h.setVulnerableBecause("not vulnerable");
	}
	
	
	private void countCurrentlyVulnerableHouseholds(Household h){
		vulnerableHouseholdsDSR.addValue( ( h.getPrincipalPaidBack() + h.getInterestPaidBack() ) / h.returnMonthlyGrossTotalIncome() );
		vulnerableHouseholdsDSRAdjusted.addValue( ( h.getPrincipalPaidBack() + h.getInterestPaidBack() ) / 
				(h.returnMonthlyGrossTotalIncome() - 0.2 * medianIncome) );
		if(h.getVulnerableBecause() == "purchase") nowVulnerableByPurchaseCounter += 1;
		if(h.getVulnerableBecause() == "dissaving") nowVulnerableByDissavingCounter += 1;
		if(h.getVulnerableBecause() == "other") nowVulnerableByOtherCounter += 1;
		
		// agent-specific
		if (h.behaviour.isPropertyInvestor() & h.getNProperties() > 1) {
			if(h.getVulnerableBecause() == "purchase") nowVulnerableByPurchaseBTLCounter += 1;
			if(h.getVulnerableBecause() == "dissaving") nowVulnerableByDissavingBTLCounter += 1;
			if(h.getVulnerableBecause() == "other") nowVulnerableByOtherBTLCounter += 1;
		}
		else if (h.isHomeowner() & !h.isInFirstHome()) {
			if(h.getVulnerableBecause() == "purchase") nowVulnerableByPurchaseSSBCounter += 1;
			if(h.getVulnerableBecause() == "dissaving") nowVulnerableByDissavingSSBCounter += 1;
			if(h.getVulnerableBecause() == "other") nowVulnerableByOtherSSBCounter += 1;			
		}
		else if (h.isHomeowner() & h.isInFirstHome()) {
			if(h.getVulnerableBecause() == "purchase") nowVulnerableByPurchaseFTBCounter += 1;
			if(h.getVulnerableBecause() == "dissaving") nowVulnerableByDissavingFTBCounter += 1;
			if(h.getVulnerableBecause() == "other") nowVulnerableByOtherFTBCounter += 1;			
		}			
	}
	
		
	// Method to calculate the EaD by setting the nominal debt in relation to aggregate debt
	private void recordExposureAtDefault() {

		double aggregateDebt = Model.creditSupply.getTotalBTLCredit() + Model.creditSupply.getTotalOOCredit();
		ExposureAtDefaultDSR30 = ExposureAtDefaultDSR30Counter / aggregateDebt;
		ExposureAtDefaultDSR30Counter = 0;
		ExposureAtDefaultDSR35 = ExposureAtDefaultDSR35Counter / aggregateDebt;
		ExposureAtDefaultDSR35Counter = 0;
		ExposureAtDefaultDSR70 = ExposureAtDefaultDSR70Counter / aggregateDebt;
		ExposureAtDefaultDSR70Counter = 0;

		ExposureAtDefaultFinancialMarginBLC20= ExposureAtDefaultFinancialMarginBLC20Counter / aggregateDebt;
		ExposureAtDefaultFinancialMarginBLC20Counter = 0;
		ExposureAtDefaultFinancialMarginBLC40= ExposureAtDefaultFinancialMarginBLC40Counter / aggregateDebt;
		ExposureAtDefaultFinancialMarginBLC40Counter = 0;
		ExposureAtDefaultFinancialMarginBLC70= ExposureAtDefaultFinancialMarginBLC70Counter / aggregateDebt;
		ExposureAtDefaultFinancialMarginBLC70Counter = 0;

		ExposureAtDefaultAmpudiaMeasure1 = ExposureAtDefaultAmpudiaMeasure1Counter / aggregateDebt;
		ExposureAtDefaultAmpudiaMeasure1Counter = 0;
		ExposureAtDefaultAmpudiaMeasure2 = ExposureAtDefaultAmpudiaMeasure2Counter / aggregateDebt;
		ExposureAtDefaultAmpudiaMeasure2Counter = 0;

		HouseholdsWithLessThan1500p = HouseholdsWithLessThan1500pCounter;
		HouseholdsWithLessThan1500pCounter = 0;
		lowDepositHouseholdConsumption = lowDepositHouseholdConsumptionCounter ;
		lowDepositHouseholdConsumptionCounter = 0;
		lowDepositHouseholdSaving = lowDepositHouseholdSavingCounter;
		lowDepositHouseholdSavingCounter = 0; 


		householdsVulnerableAmpudiaMeasure2 = householdsVulnerableAmpudiaMeasure2Counter;
		householdsVulnerableAmpudiaMeasure2Counter = 0;

		activeBTLVulnerable = activeBTLVulnerableCounter; 
		activeBTLVulnerableCounter = 0;
		SSBVulnerable = SSBVulnerableCounter;
		SSBVulnerableCounter = 0;
		inFirstHomeVulnerable = inFirstHomeVulnerableCounter;
		inFirstHomeVulnerableCounter = 0;

		activeBTLEAD = activeBTLEADCounter / aggregateDebt; 
		activeBTLEADCounter = 0;
		SSBEAD = SSBEADCounter / aggregateDebt; 
		SSBEADCounter = 0; 
		inFirstHomeEAD = inFirstHomeEADCounter / aggregateDebt;
		inFirstHomeEADCounter = 0;

		// Unemployment simulation
		unemploymentExposureAtDefaultAmpudiaMeasure2 = unemploymentExposureAtDefaultAmpudiaMeasure2Counter / aggregateDebt;
		unemploymentExposureAtDefaultAmpudiaMeasure2Counter = 0;
		unemploymentHouseholdsVulnerableAmpudiaMeasure2 = unemploymentHouseholdsVulnerableAmpudiaMeasure2Counter ;
		unemploymentHouseholdsVulnerableAmpudiaMeasure2Counter = 0;

		unemploymentActiveBTLVulnerable = unemploymentActiveBTLVulnerableCounter;
		unemploymentActiveBTLVulnerableCounter = 0;
		unemploymentActiveBTLEAD = unemploymentActiveBTLEADCounter / aggregateDebt;
		unemploymentActiveBTLEADCounter = 0; 

		unemploymentSSBVulnerable = unemploymentSSBVulnerableCounter;
		unemploymentSSBVulnerableCounter = 0;
		unemploymentSSBEAD = unemploymentSSBEADCounter / aggregateDebt;
		unemploymentSSBEADCounter = 0;

		unemploymentinFirstHomeVulnerable = unemploymentinFirstHomeVulnerableCounter;
		unemploymentinFirstHomeVulnerableCounter = 0;
		unemploymentinFirstHomeEAD = unemploymentinFirstHomeEADCounter / aggregateDebt;
		unemploymentinFirstHomeEADCounter = 0;
		
		vulnerableByPurchase = vulnerableByPurchaseCounter;
		vulnerableByPurchaseCounter = 0;
		vulnerableByConsumption = vulnerableByConsumptionCounter;
		vulnerableByConsumptionCounter = 0;
		vulnerableByOther = vulnerableByOtherCounter;
		vulnerableByOtherCounter = 0;
		
		notVulnerableBecauseSale = notVulnerableBecauseSaleCounter;
		notVulnerableBecauseSaleCounter = 0;
		notVulnerableBecauseSaving = notVulnerableBecauseSavingCounter;
		notVulnerableBecauseSavingCounter = 0;
		notVulnerableBecauseOther = notVulnerableBecauseOtherCounter;
		notVulnerableBecauseOtherCounter = 0;
		
		vulnerableByPurchaseBTL = vulnerableByPurchaseBTLCounter;
		vulnerableByPurchaseBTLCounter = 0;
		vulnerableByPurchaseSSB = vulnerableByPurchaseSSBCounter;
		vulnerableByPurchaseSSBCounter = 0;
		vulnerableByPurchaseInFirstHome = vulnerableByPurchaseInFirstHomeCounter;
		vulnerableByPurchaseInFirstHomeCounter = 0;
		vulnerableByConsumptionBTL = vulnerableByConsumptionBTLCounter;
		vulnerableByConsumptionBTLCounter = 0;
		vulnerableByConsumptionSSB = vulnerableByConsumptionSSBCounter;
		vulnerableByConsumptionSSBCounter = 0;
		vulnerableByConsumptionInFirstHome = vulnerableByConsumptionInFirstHomeCounter;
		vulnerableByConsumptionInFirstHomeCounter = 0;
		vulnerableByOtherBTL = vulnerableByOtherBTLCounter;
		vulnerableByOtherBTLCounter = 0;
		vulnerableByOtherSSB = vulnerableByOtherSSBCounter;
		vulnerableByOtherSSBCounter = 0;
		vulnerableByOtherInFirstHome = vulnerableByOtherInFirstHomeCounter;
		vulnerableByOtherInFirstHomeCounter = 0;

		notVulnerableBecauseSaleBTL = notVulnerableBecauseSaleBTLCounter;
		notVulnerableBecauseSaleBTLCounter = 0;
		notVulnerableBecauseSaleSSB = notVulnerableBecauseSaleSSBCounter;
		notVulnerableBecauseSaleSSBCounter = 0;
		notVulnerableBecauseSaleInFirstHome = notVulnerableBecauseSaleInFirstHomeCounter;
		notVulnerableBecauseSaleInFirstHomeCounter = 0;
		notVulnerableBecauseSaleOthers = notVulnerableBecauseSaleOthersCounter;
		notVulnerableBecauseSaleOthersCounter = 0;
		notVulnerableBecauseSavingBTL = notVulnerableBecauseSavingBTLCounter;
		notVulnerableBecauseSavingBTLCounter = 0;
		notVulnerableBecauseSavingSSB = notVulnerableBecauseSavingSSBCounter;
		notVulnerableBecauseSavingSSBCounter = 0;
		notVulnerableBecauseSavingInFirstHome = notVulnerableBecauseSavingInFirstHomeCounter;
		notVulnerableBecauseSavingInFirstHomeCounter = 0;
		notVulnerableBecauseSavingOthers = notVulnerableBecauseSavingOthersCounter;
		notVulnerableBecauseSavingOthersCounter = 0;
		notVulnerableBecauseOtherBTL = notVulnerableBecauseOtherBTLCounter;
		notVulnerableBecauseOtherBTLCounter = 0;
		notVulnerableBecauseOtherSSB = notVulnerableBecauseOtherSSBCounter;
		notVulnerableBecauseOtherSSBCounter = 0;
		notVulnerableBecauseOtherInFirstHome = notVulnerableBecauseOtherInFirstHomeCounter;
		notVulnerableBecauseOtherInFirstHomeCounter = 0;
		notVulnerableBecauseOtherOthers = notVulnerableBecauseOtherOthersCounter;
		notVulnerableBecauseOtherOthersCounter = 0;
		
		nowVulnerableByPurchase = nowVulnerableByPurchaseCounter;
		nowVulnerableByPurchaseCounter = 0;
		nowVulnerableByPurchaseBTL = nowVulnerableByPurchaseBTLCounter;
		nowVulnerableByPurchaseBTLCounter = 0;
		nowVulnerableByPurchaseSSB = nowVulnerableByPurchaseSSBCounter;
		nowVulnerableByPurchaseSSBCounter = 0;
		nowVulnerableByPurchaseFTB = nowVulnerableByPurchaseFTBCounter;
		nowVulnerableByPurchaseFTBCounter = 0;
		
		nowVulnerableByDissaving = nowVulnerableByDissavingCounter;
		nowVulnerableByDissavingCounter = 0;
		nowVulnerableByDissavingBTL = nowVulnerableByDissavingBTLCounter;
		nowVulnerableByDissavingBTLCounter = 0;
		nowVulnerableByDissavingSSB = nowVulnerableByDissavingSSBCounter;
		nowVulnerableByDissavingSSBCounter = 0;
		nowVulnerableByDissavingFTB = nowVulnerableByDissavingFTBCounter;
		nowVulnerableByDissavingFTBCounter = 0;
		
		nowVulnerableByOther = nowVulnerableByOtherCounter;
		nowVulnerableByOtherCounter = 0;
		nowVulnerableByOtherBTL = nowVulnerableByOtherBTLCounter;
		nowVulnerableByOtherBTLCounter = 0;
		nowVulnerableByOtherSSB = nowVulnerableByOtherSSBCounter;
		nowVulnerableByOtherSSBCounter = 0;
		nowVulnerableByOtherFTB = nowVulnerableByOtherFTBCounter;
		nowVulnerableByOtherFTBCounter = 0;
		
	}
	
	
//	public void record
	
	
    //----- Getter/setter methods -----//


	// Getters for numbers of households variables
    int getnBTL() { return nBTL; }
    int getnActiveBTL() { return nActiveBTL; }
    int getnBTLOwnerOccupier() { return nBTLOwnerOccupier; }
    int getnBTLHomeless() { return nBTLHomeless; }
    int getnBTLBankruptcies() { return nBTLBankruptcies; }
    //RUBEN changed to public
    public int getnNonBTLOwnerOccupier() { return nNonBTLOwnerOccupier; }
    int getnRenting() { return nRenting; }
    int getnNonBTLHomeless() { return nNonBTLHomeless; }
    int getnFTBinSocialHousing() {return nFTBinSocialHousing;}
    int getnNonBTLBankruptcies() { return nNonBTLBankruptcies; }
    int getnOwnerOccupier() { return nBTLOwnerOccupier + nNonBTLOwnerOccupier; }
    int getnHomeless() { return nBTLHomeless + nNonBTLHomeless; }
    int getnNonOwner() { return nRenting + getnHomeless(); }

    // Getters for annualised income variables
    double getActiveBTLAnnualisedTotalIncome() { return activeBTLAnnualisedTotalIncome; }
    double getOwnerOccupierAnnualisedTotalIncome() { return ownerOccupierAnnualisedTotalIncome; }
    double getRentingAnnualisedTotalIncome() { return rentingAnnualisedTotalIncome; }
    double getHomelessAnnualisedTotalIncome() { return homelessAnnualisedTotalIncome; }
    double getNonOwnerAnnualisedTotalIncome() { return rentingAnnualisedTotalIncome + homelessAnnualisedTotalIncome; }
    double getActiveMonthlyNetIncome() { return activeBTLMonthlyNetIncome; }
    double getOwnerOccupierMonthlyNetIncome() { return ownerOccupierMonthlyNetIncome; }
    double getRentingMonthlyNetIncome() { return rentingMonthlyNetIncome; }
    double getHomelessMonthlyNetIncome() { return homelessMonthlyNetIncome; }
    double getNonOwnerMonthlyNetIncome() { 
    	return rentingMonthlyNetIncome + homelessMonthlyNetIncome; 
    } 
    double getMonthlyGrossEmploymentIncome() { return 	
    		activeBTLMonthlyGrossEmploymentIncome +	
    		ownerOccupierMonthlyGrossEmploymentIncome + 
    		rentingMonthlyGrossEmploymentIncome + 
    		homelessMonthlyGrossEmploymentIncome;
    }


    // Getters for yield variables
    double getSumStockYield() { return sumStockYield; }
    double getAvStockYield() {
        if(nRenting > 0) {
            return sumStockYield/nRenting;
        } else {
            return 0.0;
        }
    }

    // Getters for other variables...
    // ... number of empty houses (total number of houses minus number of non-homeless households)
    public int getnEmptyHouses() {
        return Model.construction.getHousingStock() + nBTLHomeless + nNonBTLHomeless - Model.households.size();
    }
    // ... proportion of housing stock owned by buy-to-let investors (all rental properties, plus all empty houses not
    // owned by the construction sector)
    double getBTLStockFraction() {
//        return ((double)(getnEmptyHouses() - Model.housingMarketStats.getnUnsoldNewBuild()
//                + nRenting))/Model.construction.getHousingStock();
        return ((double)(nBTLRentalProperty))/Model.construction.getHousingStock(); // this now collects only rental property owned by investors, not heirs
    }
    // ... number of normal (non-BTL) bidders with desired housing expenditure above the exponential moving average sale price
    int getnNonBTLBidsAboveExpAvSalePrice() { return nNonBTLBidsAboveExpAvSalePrice; }
    // ... number of BTL bidders with desired housing expenditure above the exponential moving average sale price
    int getnBTLBidsAboveExpAvSalePrice() { return nBTLBidsAboveExpAvSalePrice; }
    
    
    // getters of different types of payments made as well as bank's cash injection and debt relief
    public double getTotalPrincipalRepayments() { return totalPrincipalRepayments;	}
    
    public double getTotalPrincipalRepaymentsDueToHouseSale() { return totalPrincipalRepaymentsDueToHouseSale; }
    
    public double getTotalPrincipalPaidBackForInheritance() { return totalPrincipalPaidBackForInheritance;}

	public double getTotalInterestRepayments() { return totalInterestRepayments;	}

	public double getTotalRentalPayments() { return totalRentalPayments; }

	public double getTotalMonthlyTaxesPaid() { return totalMonthlyTaxesPaid; }

	public double getTotalMonthlyNICPaid() { return totalMonthlyNICPaid; }

	public double getTotalBankruptcyCashInjection() { return totalBankruptcyCashInjection; }

	public double getTotalDebtReliefOfDeceasedHouseholds() { return totalDebtReliefOfDeceasedHouseholds; }

	public double getTotalPrincipalRepaymentDeceasedHouseholds() { return totalPrincipalRepaymentDeceasedHousehold; }
	//RUBEN getters for totalConsumption and Savings
    double getTotalConsumption() { return totalConsumption; }
    double getTotalSaving() {return totalSaving; }
    double getTotalBankBalancesBeforeConsumption() { return totalBankBalancesBeforeConsumption; }
    // public because when dividends are distributed household-class needs to access this
    public double getTotalBankBalancesVeryBeginningOfPeriod() { return totalBankBalancesVeryBeginningOfPeriod; }
    double getTotalBankBalanceEndowment() { return totalBankBalanceEndowment; }
    double getIncomeConsumption() { return totalIncomeConsumption; }
    double getFinancialWealthConsumption() { return totalFinancialWealthConsumption; }
    double getHousingWealthConsumption() { return totalHousingWealthConsumption; }
    double getDebtConsumption() { return totalDebtConsumption; }
    double getTotalSavingForDeleveraging() { return totalSavingForDeleveraging; }
    int getNNegativeEquity() { return nNegativeEquity; }

    // PAUL create three getters, one for nAirbNBbtl and one for their extra rental income, and number of airbnbs
	public int getnAirBnBBTL() {
		return nAirBnBBTL;
	}
	public double getAirBnBRentalIncome() {
		return airBnBRentalIncome;
	}
	public int getnAirBnBs() {
		return nAirBnBs;
	}

	public double getRentingMonthlyDisposableIncome() {
		return rentingMonthlyDisposableIncome;
	}
    
    public double getTotalMonthlyDisposableIncome() {
		return totalMonthlyDisposableIncome;
	}

	public double getTotalBankBalancesEndPeriod() {
		return totalBankBalancesEndPeriod;
	}
	
	public double getTotalSocialHousingRent() {
		return totalSocialHousingRent;
	}
	
	public double getMonthlyTotalDividendIncome() {
		return totalDividendIncome;
	}

	public int getnSSB() {
		return nSSB;
	}

	public int getnInFirstHome() {
		return nInFirstHome;
	}

	public double getActiveBTLIncomeConsumption() {
		return activeBTLIncomeConsumption;
	}

	public double getActiveBTLFinancialWealthConsumption() {
		return activeBTLFinancialWealthConsumption;
	}

	public double getActiveBTLNetHousingWealthConsumption() {
		return activeBTLNetHousingWealthConsumption;
	}

	public double getSSBIncomeConsumption() {
		return SSBIncomeConsumption;
	}

	public double getSSBFinancialWealthConsumption() {
		return SSBFinancialWealthConsumption;
	}

	public double getSSBNetHousingWealthConsumption() {
		return SSBNetHousingWealthConsumption;
	}

	public double getInFirstHomeIncomeConsumption() {
		return inFirstHomeIncomeConsumption;
	}

	public double getInFirstHomeFinancialWealthConsumption() {
		return inFirstHomeFinancialWealthConsumption;
	}

	public double getInFirstHomeNetHousingWealthConsumption() {
		return inFirstHomeNetHousingWealthConsumption;
	}

	public double getRenterIncomeConsumption() {
		return renterIncomeConsumption;
	}

	public double getRenterFinancialWealthConsumption() {
		return renterFinancialWealthConsumption;
	}
	
	public double getRenterNetHousingWealthConsumption() {
		return renterNetHousingWealthConsumption;
	}
	
	public double getExposureAtDefaultDSR30() {
		return ExposureAtDefaultDSR30;
	}

	public double getExposureAtDefaultDSR35() {
		return ExposureAtDefaultDSR35;
	}

	public double getExposureAtDefaultDSR70() {
		return ExposureAtDefaultDSR70;
	}

	public double getExposureAtDefaultFinancialMarginBLC20() {
		return ExposureAtDefaultFinancialMarginBLC20;
	}

	public double getExposureAtDefaultFinancialMarginBLC40() {
		return ExposureAtDefaultFinancialMarginBLC40;
	}

	public double getExposureAtDefaultFinancialMarginBLC70() {
		return ExposureAtDefaultFinancialMarginBLC70;
	}
	
    public double getExposureAtDefaultAmpudiaMeasure1() {
		return ExposureAtDefaultAmpudiaMeasure1;
	}

	public double getExposureAtDefaultAmpudiaMeasure2() {
		return ExposureAtDefaultAmpudiaMeasure2;
	}

	public int getHouseholdsWithLessThan1500p() {
		return HouseholdsWithLessThan1500p;
	}

	public int getHouseholdsVulnerableAmpudiaMeasure2() {
		return householdsVulnerableAmpudiaMeasure2;
	}

	public int getActiveBTLVulnerable() {
		return activeBTLVulnerable;
	}

	public int getSSBVulnerable() {
		return SSBVulnerable;
	}

	public int getInFirstHomeVulnerable() {
		return inFirstHomeVulnerable;
	}

	public double getActiveBTLEAD() {
		return activeBTLEAD;
	}

	public double getSSBEAD() {
		return SSBEAD;
	}

	public double getInFirstHomeEAD() {
		return inFirstHomeEAD;
	}

    public double getLowDepositHouseholdConsumption() {
		return lowDepositHouseholdConsumption;
	}

	public double getLowDepositHouseholdSaving() {
		return lowDepositHouseholdSaving;
	}

	public double getUnemploymentExposureAtDefaultAmpudiaMeasure2() {
		return unemploymentExposureAtDefaultAmpudiaMeasure2;
	}

	public int getUnemploymentHouseholdsVulnerableAmpudiaMeasure2() {
		return unemploymentHouseholdsVulnerableAmpudiaMeasure2;
	}

	public int getUnemploymentActiveBTLVulnerable() {
		return unemploymentActiveBTLVulnerable;
	}

	public double getUnemploymentActiveBTLEAD() {
		return unemploymentActiveBTLEAD;
	}

	public int getUnemploymentSSBVulnerable() {
		return unemploymentSSBVulnerable;
	}

	public double getUnemploymentSSBEAD() {
		return unemploymentSSBEAD;
	}

	public int getUnemploymentinFirstHomeVulnerable() {
		return unemploymentinFirstHomeVulnerable;
	}

	public double getUnemploymentinFirstHomeEAD() {
		return unemploymentinFirstHomeEAD;
	}
	
	public int getVulnerableByPurchase() {
		return vulnerableByPurchase;
	}

	public int getVulnerableByConsumption() {
		return vulnerableByConsumption;
	}
	
	public int getVulnerableByOther() {
		return vulnerableByOther;
	}

	public int getNotVulnerableBecauseSale() {
		return notVulnerableBecauseSale;
	}

	public int getNotVulnerableBecauseSaving() {
		return notVulnerableBecauseSaving;
	}
	
	public int getNotVulnerableBecauseOther() {
		return notVulnerableBecauseOther;
	}
	

	public int getVulnerableByPurchaseBTL() {
		return vulnerableByPurchaseBTL;
	}

	public int getVulnerableByPurchaseSSB() {
		return vulnerableByPurchaseSSB;
	}

	public int getVulnerableByPurchaseInFirstHome() {
		return vulnerableByPurchaseInFirstHome;
	}

	public int getVulnerableByConsumptionBTL() {
		return vulnerableByConsumptionBTL;
	}

	public int getVulnerableByConsumptionSSB() {
		return vulnerableByConsumptionSSB;
	}

	public int getVulnerableByConsumptionInFirstHome() {
		return vulnerableByConsumptionInFirstHome;
	}

	public int getVulnerableByOtherBTL() {
		return vulnerableByOtherBTL;
	}

	public int getVulnerableByOtherSSB() {
		return vulnerableByOtherSSB;
	}

	public int getVulnerableByOtherInFirstHome() {
		return vulnerableByOtherInFirstHome;
	}

	public int getNotVulnerableBecauseSaleBTL() {
		return notVulnerableBecauseSaleBTL;
	}

	public int getNotVulnerableBecauseSaleSSB() {
		return notVulnerableBecauseSaleSSB;
	}

	public int getNotVulnerableBecauseSaleInFirstHome() {
		return notVulnerableBecauseSaleInFirstHome;
	}

	public int getNotVulnerableBecauseSaleOthers() {
		return notVulnerableBecauseSaleOthers;
	}

	public int getNotVulnerableBecauseSavingBTL() {
		return notVulnerableBecauseSavingBTL;
	}

	public int getNotVulnerableBecauseSavingSSB() {
		return notVulnerableBecauseSavingSSB;
	}

	public int getNotVulnerableBecauseSavingInFirstHome() {
		return notVulnerableBecauseSavingInFirstHome;
	}

	public int getNotVulnerableBecauseSavingOthers() {
		return notVulnerableBecauseSavingOthers;
	}

	public int getNotVulnerableBecauseOtherBTL() {
		return notVulnerableBecauseOtherBTL;
	}

	public int getNotVulnerableBecauseOtherSSB() {
		return notVulnerableBecauseOtherSSB;
	}

	public int getNotVulnerableBecauseOtherInFirstHome() {
		return notVulnerableBecauseOtherInFirstHome;
	}

	public int getNotVulnerableBecauseOtherOthers() {
		return notVulnerableBecauseOtherOthers;
	}
	

	public int getNowVulnerableByPurchase() {
		return nowVulnerableByPurchase;
	}

	public int getNowVulnerableByPurchaseBTL() {
		return nowVulnerableByPurchaseBTL;
	}

	public int getNowVulnerableByPurchaseSSB() {
		return nowVulnerableByPurchaseSSB;
	}

	public int getNowVulnerableByPurchaseFTB() {
		return nowVulnerableByPurchaseFTB;
	}

	public int getNowVulnerableByDissaving() {
		return nowVulnerableByDissaving;
	}

	public int getNowVulnerableByDissavingBTL() {
		return nowVulnerableByDissavingBTL;
	}

	public int getNowVulnerableByDissavingSSB() {
		return nowVulnerableByDissavingSSB;
	}

	public int getNowVulnerableByDissavingFTB() {
		return nowVulnerableByDissavingFTB;
	}

	public int getNowVulnerableByOther() {
		return nowVulnerableByOther;
	}

	public int getNowVulnerableByOtherBTL() {
		return nowVulnerableByOtherBTL;
	}

	public int getNowVulnerableByOtherSSB() {
		return nowVulnerableByOtherSSB;
	}

	public int getNowVulnerableByOtherFTB() {
		return nowVulnerableByOtherFTB;
	}

	public double getMonthlyMedianIncome() {
		return medianIncome;
	}
	
	public double getMedianDebtServiceRatio() {
		return medianDSR;
	}
	
	public double getMedianDSRVulnerableHouseholds() {
		return medianDSRVulnerableHouseholds;
	}

	public double getMedianAgeVulnerableHouseholds() {
		return medianAgeVulnerableHouseholds;
	}
	
	public double getMedianAgeNonVulnerableHouseholds() {
		return medianAgeNonVulnerableHouseholds;
	}

	public int getIndebtedHouseholds() {
		return indebtedHouseholds;
	}
	
	public double getMedianDebtServiceRatioAdjusted() {
		return medianDSRAdjusted;
	}
	
	public double getMedianDSRVulnerableHouseholdsAdjusted() {
		return medianDSRVulnerableHouseholdsAdjusted;
	}
	
	
}
