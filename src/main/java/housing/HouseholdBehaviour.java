package housing;

import org.apache.commons.math3.distribution.LogNormalDistribution;
import org.apache.commons.math3.random.MersenneTwister;

import collectors.HousingMarketStats;
import collectors.RentalMarketStats;

import utilities.BinnedDataDouble;
import utilities.Pdf;

/**************************************************************************************************
 * Class to implement the behavioural decisions made by households
 *
 * @author daniel, Adrian Carro
 *
 *************************************************************************************************/
public class HouseholdBehaviour {

	//------------------//
	//----- Fields -----//
	//------------------//

	private static Config                   config = Model.config; // Passes the Model's configuration parameters object to a private static field
	private static MersenneTwister	        prng = Model.prng; // Passes the Model's random number generator to a private static field
	private static HousingMarketStats       housingMarketStats = Model.housingMarketStats; // Passes the Model's housing market stats object to a private static field
	private static RentalMarketStats        rentalMarketStats = Model.rentalMarketStats; // Passes the Model's rental market stats object to a private static field
	private static Pdf                      saleMarkUpPdf = new Pdf(config.DATA_INITIAL_SALE_MARKUP_DIST); // Read initial sale price mark-up distribution from file
	private static Pdf                      rentMarkUpPdf = new Pdf(config.DATA_INITIAL_RENT_MARKUP_DIST); // Read initial rent price mark-up distribution from file
	private static LogNormalDistribution    downpaymentDistFTB = new LogNormalDistribution(prng,
			config.DOWNPAYMENT_FTB_SCALE, config.DOWNPAYMENT_FTB_SHAPE); // Size distribution for downpayments of first-time-buyers
	private static LogNormalDistribution    downpaymentDistOO = new LogNormalDistribution(prng,
			config.DOWNPAYMENT_OO_SCALE, config.DOWNPAYMENT_OO_SHAPE); // Size distribution for downpayments of owner-occupiers
	private boolean                         BTLInvestor;
	private double                          BTLCapGainCoefficient; // Sensitivity of BTL investors to capital gain, 0.0 cares only about rental yield, 1.0 cares only about cap gain
	private double                          propensityToSave;
	// PAUL
	private boolean 				airBnBInvestor; // is the BTL investor an airBnB investor

	//------------------------//
	//----- Constructors -----//
	//------------------------//

	/**
	 * Initialise behavioural variables for a new household: propensity to save, whether the household will have the BTL
	 * investor "gene" (provided its income percentile is above a certain minimum), and whether the household will be a
	 * fundamentalist or a trend follower investor (provided it has received the BTL investor gene)
	 *
	 * @param incomePercentile Fixed income percentile for the household (assumed constant over a lifetime)
	 */
	HouseholdBehaviour(double incomePercentile) {
		// Compute propensity to save, so that it is constant for a given household
		propensityToSave = prng.nextDouble();
		// Decide if household is a BTL investor and, if so, its tendency to seek capital gains or rental yields
		BTLCapGainCoefficient = 0.0;
		if(incomePercentile > config.MIN_INVESTOR_PERCENTILE &&
				prng.nextDouble() < config.getPInvestor()/config.MIN_INVESTOR_PERCENTILE) {
			BTLInvestor = true;
			if(prng.nextDouble() < config.P_FUNDAMENTALIST) {
				BTLCapGainCoefficient = config.FUNDAMENTALIST_CAP_GAIN_COEFF;
			} else {
				BTLCapGainCoefficient = config.TREND_CAP_GAIN_COEFF;
			}
			//            
			//            // PAUL if household is investor, decide if he is an AirBnB investor
			//            if(prng.nextDouble() < config.p_airbnb) {
			//            	airBnBInvestor = true;
			//            } else {
			//            	// PAUL otherwise household is not an airbnbinvestor
			//                airBnBInvestor = false;
			//            }
			//            
		} else {
			BTLInvestor = false;
		}
	}

	//-------------------//
	//----- Methods -----//
	//-------------------//

	//----- General behaviour -----//

	/**
	 * Compute the monthly consumption by a household. It is calibrated so that the output
	 * wealth distribution fits the ONS wealth data for Great Britain.
	 *
	 * @param bankBalance Household's liquid wealth after adding disposable income
	 * @param annualGrossTotalIncome Household's annual gross total income
	 * @param incomePercentile Household's income percentile given at "birth" to determine its MPCs
	 * @param disposableIncome Household's income after taxes, insurance and housing consumption
	 * @param monthlyNetTotalIncome Household's income after taxes and insurance
	 */
	public double getDesiredConsumption(Household me, double bankBalance, double incomePercentile,
			double disposableIncome, double monthlyNetTotalIncome) {
	double annualGrossTotalIncome = me.getAnnualGrossTotalIncome();
		double propertyValues = me.getPropertyValue();
		double totalDebt = me.getTotalDebt();
		double equityPosition = me.getEquityPosition();		
		double consumption;
		double saving;

		// if alternate consumption is active, use the following way to calculate it
		if(config.ALTERNATE_CONSUMPTION_FUNCTION) {
			double consumptionFraction;
			// these are monthly values! 
			double wealthEffect;
			double netWealthConsumptionCoefficient = config.consumptionNetHousingWealth;
			double liquidityPreference = config.liquidityPreference;
			double savingForDeleveraging = 0.0;
			// when the function is called Bank Balance of the household consists of deposits and its disposable income of the
			// current month. This can be confusing when reading the code, so deposits are defined as the deposits before the househod
			// earned this months income
			double deposits = bankBalance - disposableIncome; 
			// set the wealth effect according to the employment income position of the household, so that 
			// households with higher employment income consume less out of their wealth
			if(incomePercentile<0.25) {
				wealthEffect = config.wealthEffectQ1;
				consumptionFraction = config.consumptionFractionQ1;
			}
			else if(0.25 <= incomePercentile && incomePercentile < 0.5) {
				wealthEffect = config.wealthEffectQ2;
				consumptionFraction = config.consumptionFractionQ2;
			}
			else if(0.5 <= incomePercentile && incomePercentile <0.75) {
				wealthEffect = config.wealthEffectQ3;
				consumptionFraction = config.consumptionFractionQ3;
			}
			else if(0.75 <= incomePercentile && incomePercentile <0.90) {
				wealthEffect = config.wealthEffectQ4;
				consumptionFraction = config.consumptionFractionQ4;
			}
			else if(0.9 <= incomePercentile && incomePercentile <0.99) {
				wealthEffect = config.wealthEffectTop10;
				consumptionFraction = config.consumptionFractionTop10;
			}
			else {
				wealthEffect = config.wealthEffectTop1;
				consumptionFraction = config.consumptionFractionTop1;
			}
			// First-time buyers have lower MPCs out of income and no wealth effect to save for a downpayment 
			if(config.FTBSavingMotive && me.isFirstTimeBuyer() && incomePercentile > config.minIncomePercentile) { // && incomePercentile > 0.25) {
				wealthEffect = 0;
				// check if the MPC would actually be lower with the FTB saving motive, if not the household keeps the lower MPC
				// (otherwise consumption out of income would increase for households with high income percentile)
				if(consumptionFraction > config.MPCForFirstTimeBuyers) {
					consumptionFraction = config.MPCForFirstTimeBuyers;
				}
			}

			// implementing a quasi-collateral channel (following Aladangady (2017)) 
			// households with DSRs above median have a housing wealth effect of 0.127 (yearly), 
			// households below 0
			double DSR = (me.getPrincipalPaidBack() + me.getInterestPaidBack())/me.returnMonthlyGrossTotalIncome();
			if(DSR >= Model.householdStats.getMedianDebtServiceRatio()) {
				consumption = consumptionFraction * disposableIncome 
						+ wealthEffect * deposits
						+ config.consumptionNetHousingWealth * (propertyValues+ totalDebt);
			} else {
				consumption = consumptionFraction * disposableIncome 
						+ wealthEffect * deposits;
			}

			// calculate the different parts of consumption in order to extract this data
			double incomeConsumption = consumptionFraction * disposableIncome;
			double financialWealthConsumption = wealthEffect * deposits; 		
			double housingWealthConsumption = 0.0;
			double debtConsumption = 0.0;
			// from the quasi-collateral channel follows that only households with above-median
			// DSRs have a positive net housing wealth effect and NO liquidity preference
			if(DSR > Model.householdStats.getMedianDebtServiceRatio()) {
				housingWealthConsumption = config.consumptionNetHousingWealth * propertyValues;
				debtConsumption = config.consumptionNetHousingWealth * totalDebt;
				liquidityPreference = 0.0;
			}

//
//			// bankBalance includes the disposable income of this month (where housing consumtpion and essential
//			// consumption are already substracted) -> the first term gives the bank balance after consumption ...
//			if(bankBalance-consumption < 
//					// ...if this bank balance is smaller than liquidity preference times the disposable income (after taxes
//					// and housing consumption, but BEFORE essential consumption)...
//					liquidityPreference * (disposableIncome ))  {
//				//... then the non-essential consumption will only be out of income (times the MPC of income)
//				consumption = incomeConsumption;
//			}
//
//			// TEST this is to test an alternative minimum consumption based on the poverty line approach
//			if(consumption  < 
//					2750 *  0.4) { // config.povertyLinePercentMedianIncome) {
//				consumption = 2750 * 0.4 ;
//			}
//			
//			// if HH wants to consume more than it has in cash, then limit to cash (otherwise bankrupt)
//			// as disposable income is already added to the bankBalance before the method is called, the HH
//			// effectively consumes all its disposable income
//			if(consumption > bankBalance) { 
//				consumption = bankBalance;
//			}
//
//			// if consumption is negative (due to high debt), consume at least either essential consumption
//			// but not more than the actual bank balance to avoid bankruptcy
//			// as essential consumption is already subtracted from disposable income, consumption here would be zero
//			if(consumption < 0) {
//				//they never consume negative (which could happen when bank balance negative
//				consumption = Math.max(Math.min(0, bankBalance), 0);
//			}
//
////			// Households with negative disposable income can consume consume some minimal amount, if they can (with deposits), 
////			// hold on, they could consume a large amount if they have the deposits... 
////			
////			
			// -------- liquidity preference constraint -------
			// Households with below-median DSRs have a liquidity preference being a multiple of their disposable income.
			// the first term gives the bank balance after consumption ...
			if(deposits + disposableIncome - consumption < 
					// ...if this bank balance is smaller than liquidity preference times net income (after taxes) ...
					// ... one reason to use net income, not disposable income, is that disposable income can become negative.
					liquidityPreference * monthlyNetTotalIncome)  {
				//... then overall consumption will only be out of income (times the MPC of income) 
				//... and in case of negative disposable income, this will be dealt with below.
				consumption = Math.max(incomeConsumption, 0);
			}
	

			// -------- minimum consumption constraint ---------
			// This is a different minimum consumption than Tarne, Bezemer, Theobald (2021) based on the poverty line approach.
			// If desired consumption falls below the basic living costs (= 40% of median income), 
			// households consume the basic living costs. 
			if(consumption  < 0.4 * Model.householdStats.getMonthlyMedianIncome()) { 
				// Except: their disposable income is lower than the basic living costs
				// AND they have not enough deposits to cover the basic living costs (if their liquidity buffer would be reduced below the 
				// desired threshold or even go bankrupt. In this case reduce consumption to the disposable income.
				// Additionally, control for potentially negative disposable income (when BTL investors can't rent out 
				// their property and their mortgage payments become larger than their  income from employment and rent).
				if(disposableIncome < 0.4 * Model.householdStats.getMonthlyMedianIncome() & 
//						deposits + disposableIncome - consumption < liquidityPreference * monthlyNetTotalIncome) {
						// if disposable income is lower than minimal consumption check if deposits are enough -> no liquidity preference here
					deposits + disposableIncome - 0.4 * Model.householdStats.getMonthlyMedianIncome() >= 0 ) {
//					consumption = Math.max(disposableIncome, 0); 
					consumption = 0.4 * Model.householdStats.getMonthlyMedianIncome();
//					// if disposable income in combination with minimum consumption would make the household bankrupt, 
//					// consume as much as possible but never negative
				} else if(disposableIncome < 0.4 * Model.householdStats.getMonthlyMedianIncome() & 
						deposits + disposableIncome - 0.4 * Model.householdStats.getMonthlyMedianIncome() < 0 ) {
					consumption = Math.max((deposits + disposableIncome), 0) ;
				} else {
					consumption = 0.4 * Model.householdStats.getMonthlyMedianIncome();
				}
			}
			
//			// ----- explicit FTB saving motive ---------
//			if(config.FTBSavingMotive && me.isFirstTimeBuyer() && incomePercentile > config.minIncomePercentile) {
//				// check if the MPC would actually be lower with the FTB saving motive, if not the household keeps the lower MPC
//				// (otherwise consumption out of income would increase for households with high income percentile)
//				if(totalDebt < 0) {
//					System.out.println("Weird, this FTB holds debt");
//				}
//				if(disposableIncome < 492.7 - 329.76 ) { // social rent 
//					System.out.println("Weird, this FTB's disposable income is lower than minimum income");
//				}
//				if(consumptionFraction > config.MPCForFirstTimeBuyers) {
//					// FTB do not hold any debt, nor housing wealth (when FTB inherit a house they cease to be FTB)
//					consumptionFraction = config.MPCForFirstTimeBuyers;
//					consumption = consumptionFraction * disposableIncome;
//					incomeConsumption = consumption; 
//					financialWealthConsumption = 0.0; 		
//					housingWealthConsumption = 0.0;
//					debtConsumption = 0.0;					
//				} else {
//					// use the lower MPC of the higher percentile household
//					consumption = consumptionFraction * disposableIncome; 
//					incomeConsumption = consumption; 
//					financialWealthConsumption = 0.0; 		
//					housingWealthConsumption = 0.0;
//					debtConsumption = 0.0;
//				}
//			}
//
////			// -------- handle special cases ---------
////			// In cases of very high net housing wealth and comparatively low deposits households could become bankrupt.
////			// To avoid these rather unrealistic consumption patterns, the HH effectively consumes all its 
////			// disposable income + deposits, but not more, leaving her with 0 money units.
////			// Additionally, the consumption equation can still yield negative values due to large negative disposable incomes 
////			// which are not caught by the code above. In this case, households consume nothing and
////			// go bankrupt, as their disposable income depletes their deposits.
////			if(consumption > deposits + disposableIncome) { 
////				consumption = Math.max((deposits + disposableIncome), 0);
////			}

			saving = disposableIncome-consumption;
			if(consumption < 0) {
				System.out.println("weird, consumption is negative, exactly: " + consumption + "in Time: " + Model.getTime());
			}
			
			// adjust the shares of desired consumption so that it is equal to actual consumption. 
			// otherwise, the parts themselves can add up to more, if desired consumption is higher than
			// actual consumption 
			double adjustmentParameter = 1.0;
			if(!Double.isNaN(consumption /(incomeConsumption + financialWealthConsumption + housingWealthConsumption + debtConsumption)))
			{
				adjustmentParameter = consumption /
						(incomeConsumption + financialWealthConsumption + housingWealthConsumption + debtConsumption);
			}
			// in case the households consumption components are 0 - set income consumption to total consumption 
			// i.e. do not change the adjustment parameter
			if(incomeConsumption + financialWealthConsumption + housingWealthConsumption + debtConsumption == 0) {
				adjustmentParameter = 1.0;
				incomeConsumption = consumption;
				financialWealthConsumption = 0;
				housingWealthConsumption = 0;
				debtConsumption = 0;
			}
			if (adjustmentParameter!=1.0) {
				incomeConsumption = incomeConsumption*adjustmentParameter;
				financialWealthConsumption = financialWealthConsumption*adjustmentParameter;
				housingWealthConsumption = housingWealthConsumption*adjustmentParameter;
				debtConsumption = debtConsumption*adjustmentParameter;
			}
			if((consumption - (incomeConsumption+financialWealthConsumption+housingWealthConsumption+debtConsumption)) < -0.001) {
				System.out.println("weird, consumption factors do not add up to total consumption. difference: " + 
						(consumption-(incomeConsumption+financialWealthConsumption+housingWealthConsumption+debtConsumption)));
			}
			// TEST RUBEN - some become NaNs
			if(Double.isNaN(financialWealthConsumption)) {
				System.out.println("weird, consumption value is NaN, agentID: " + me.id + "; period: " + Model.getTime());
			}
			// record the consumption contributors for the aggregate recorders
			Model.householdStats.countIncomeAndWealthConsumption(saving, consumption, incomeConsumption, 
					financialWealthConsumption, housingWealthConsumption, debtConsumption, savingForDeleveraging);

			// record the single consumption components
			me.setIncomeConsumption(incomeConsumption);
			me.setFinancialWealthConsumption(financialWealthConsumption);
			me.setHousingWealthConsumption(housingWealthConsumption);
			me.setDebtConsumption(debtConsumption);
			me.setSavingForDeleveraging(savingForDeleveraging);

//			// if uncommented, this string prints important consumption parameters to the console
//			if(Model.getTime() > 1000) {
//				System.out.println(consumption + "	" + disposableIncome + "	" + deposits + "	" +  (propertyValues+totalDebt) + "	" + 
//			"	" + liquidityPreference + "	" +	consumptionFraction + "	" + wealthEffect + "	" + (housingWealthConsumption + debtConsumption) +
//			"	" + monthlyNetTotalIncome + "	" + adjustmentParameter);
//			}
			
			return consumption;
		}

		else{			
			annualGrossTotalIncome = me.getAnnualGrossTotalIncome();
			consumption = config.ESSENTIAL_CONSUMPTION_FRACTION * config.GOVERNMENT_MONTHLY_INCOME_SUPPORT +
					config.CONSUMPTION_FRACTION*Math.max(bankBalance
					- data.Wealth.getDesiredBankBalance(annualGrossTotalIncome, propensityToSave), 0.0);
			saving = disposableIncome-consumption;
			Model.householdStats.countIncomeAndWealthConsumption(saving, consumption, 0.0, 0.0, 0.0, 0.0, 0.0);
			return consumption;
		}
	}

	//----- Owner-Occupier behaviour -----//

	/**
	 * Desired purchase price used to decide whether to buy a house and how much to bid for it
	 *
	 * @param annualGrossEmploymentIncome Annual gross employment income of the household
	 */
	double getDesiredPurchasePrice(double annualGrossEmploymentIncome) {
		if(config.NDLVersion) {
			// this fits the Dutch HFCS data to a log-linear model
			double exponent = config.BUY_SCALE + 
					config.BUY_MU * Math.log(annualGrossEmploymentIncome) + 
					config.BUY_SIGMA * prng.nextGaussian();
			double desiredPP = Math.exp(exponent);
			return desiredPP;
		} 
		// Note the capping of the HPA factor to a arbitrary maximum level (0.9) to avoid dividing by zero as well as
		// unrealistically large desired budgets
		double HPAFactor = Math.min(config.BUY_WEIGHT_HPA*getLongTermHPAExpectation(), 0.9);
		return config.BUY_SCALE * Math.pow(annualGrossEmploymentIncome, config.BUY_EXPONENT)
				* Math.exp(config.BUY_MU + config.BUY_SIGMA*prng.nextGaussian())
				/ (1.0 - HPAFactor);
	}

	/**
	 * Initial sale price of a house to be listed. This is modelled as the exponentially moving average sale price of
	 * houses of the same quality times a mark-up which is drawn from a real distribution of mark-ups, calibrated using
	 * a combination of Zoopla and HPI data.
	 *
	 * @param quality Quality of the house to be sold
	 * @param principal Amount of principal left on any mortgage on this house
	 */
	double getInitialSalePrice(int quality, double principal) {
		return Math.max(saleMarkUpPdf.nextDouble(prng) * housingMarketStats.getExpAvSalePriceForQuality(quality),
				principal);
	}

	/**
	 * Initial rent price of a house to be listed on the rental market. This is modelled as the exponentially moving
	 * average rent price of houses of the same quality times a mark-up which is drawn from a real distribution of
	 * rental mark-ups, calibrated using a combination of Zoopla and HPI data.
	 *
	 * @param quality Quality of the house to be rented out
	 */
	double getInitialRentPrice(int quality) {
		return rentMarkUpPdf.nextDouble(prng) * rentalMarketStats.getExpAvSalePriceForQuality(quality);
		//        return rentalMarketStats.getExpAvSalePriceForQuality(quality);
	}

	/**
	 * This method implements a household's decision to sell their owner-occupied property. On average, households sell
	 * owner-occupied houses every 11 years, due to exogenous reasons not addressed in the model.
	 *
	 * @return True if the owner-occupier decides to sell the house and false otherwise.
	 */
	boolean decideToSellHome() {
		// TODO: This if implies BTL agents never sell their homes, need to explain in paper!
		return !isPropertyInvestor() && (prng.nextDouble() < config.derivedParams.MONTHLY_P_SELL);
	}

	/**
	 * Decide amount to pay as initial downpayment
	 *
	 * @param me the household
	 * @param housePrice the price of the house
	 */
	double decideDownPayment(Household me, double housePrice) {
		if (me.getBankBalance() > housePrice*config.DOWNPAYMENT_BANK_BALANCE_FOR_CASH_SALE) {
			return housePrice;
		}
		double downpayment;
		if (me.isFirstTimeBuyer()) {
			// Since the function of the HPI is to move the down payments distribution upwards or downwards to
			// accommodate current price levels, and the distribution is itself aggregate, we use the aggregate HPI
          downpayment = housingMarketStats.getHPI()
          * downpaymentDistFTB.inverseCumulativeProbability(me.incomePercentile);
		} else if (isPropertyInvestor()) {
			//TODO: by Ruben, this method also gets called by the completeTransaction method (via the requestLoan method)
			// Does this mean the random number generator uses a different number here than before the household is making 
			// its bid on the housing market? Do the two calculated downpayments then differ?
			downpayment = housePrice*(Math.max(0.0,
					config.DOWNPAYMENT_BTL_MEAN + config.DOWNPAYMENT_BTL_EPSILON * prng.nextGaussian()));
		} else {
            downpayment = housingMarketStats.getHPI()
                    * downpaymentDistOO.inverseCumulativeProbability(me.incomePercentile);
		}
		if (downpayment > me.getBankBalance()) {
			//System.out.println("bankBalance restricts downpayment, desired downpayment " + downpayment/me.getBankBalance()+ "% bigger");
			downpayment = me.getBankBalance();
		}

		//System.out.println("the desired downpayment is "+ downpayment + ", Bank balance: " + me.getBankBalance() + ", monthly disposable income: " + me.getMonthlyDisposableIncome() );
		return downpayment;
	}

	///////////////////////////////////////////////////////////
	///////////////////////// REVISED /////////////////////////
	///////////////////////////////////////////////////////////

	/********************************************************
	 * Decide how much to drop the list-price of a house if
	 * it has been on the market for (another) month and hasn't
	 * sold. Calibrated against Zoopla dataset in Bank of England
	 *
	 * @param sale The HouseOfferRecord of the house that is on the market.
	 ********************************************************/
	double rethinkHouseSalePrice(HouseOfferRecord sale) {
		if(prng.nextDouble() < config.P_SALE_PRICE_REDUCE) {
			double logReduction = config.REDUCTION_MU + (prng.nextGaussian()*config.REDUCTION_SIGMA);
			return(sale.getPrice()*(1.0 - Math.exp(logReduction)/100.0));
		}
		return(sale.getPrice());
	}

	///////////////////////////////////////////////////////////////////////////////////////////////
	// Renter behaviour
	///////////////////////////////////////////////////////////////////////////////////////////////
	
	// For the Dutch model version households in social housing only enter the markets when prices fall below
	// their social housing rent
	double lowestMonthlyHousingCost(Household h) {
		// Find household's desired housing expenditure
		double desiredPurchasePrice = h.behaviour.getDesiredPurchasePrice(h.getAnnualGrossEmploymentIncome());
		// Cap this expenditure to the maximum mortgage available to the household
		double price = Math.min(desiredPurchasePrice, Model.bank.getMaxMortgage(h, true, false));

		MortgageAgreement mortgageApproval = Model.bank.requestApproval(h, price,
				decideDownPayment(h, price), true, false);
		int newHouseQuality = Model.housingMarketStats.getMaxQualityForPrice(price);
		if (newHouseQuality < 0) {
			return Double.POSITIVE_INFINITY; // can't afford a house anyway, stay in social housing   
		}
		double costOfHouse = mortgageApproval.monthlyPayment * config.constants.MONTHS_IN_YEAR
				- ( 1 / config.HOLD_PERIOD ) * price * getLongTermHPAExpectation();
		double costOfRent = Model.rentalMarketStats.getExpAvSalePriceForQuality(newHouseQuality) * config.constants.MONTHS_IN_YEAR;
		return(Math.min(costOfHouse, costOfRent));
	}

	/*** renters or OO after selling home decide whether to rent or buy
	 * N.B. even though the HH may not decide to rent a house of the
	 * same quality as they would buy, the cash value of the difference in quality
	 *  is assumed to be the difference in rental price between the two qualities.
	 *  @return true if we should buy a house, false if we should rent
	 */
	boolean decideRentOrPurchase(Household me, double purchasePrice) {
		if(isPropertyInvestor()) {
			if (config.recordAgentDecisions && (Model.getTime() >= config.TIME_TO_START_RECORDING)) { 
				Model.agentDecisionRecorder.rentOrBuy.println("true");
			}
			return(true);
		}
		MortgageAgreement mortgageApproval = Model.bank.requestApproval(me, purchasePrice,
				decideDownPayment(me, purchasePrice), true, false);
		int newHouseQuality = Model.housingMarketStats.getMaxQualityForPrice(purchasePrice);
		if (newHouseQuality < 0) {
			// if house household can't afford a house, record some basic facts DECISION DATA SH
			if (config.recordAgentDecisions && (Model.getTime() >= config.TIME_TO_START_RECORDING)) {
				Model.agentDecisionRecorder.recordCantAffordHouseRentOrPurchase(me, mortgageApproval, 
						purchasePrice, newHouseQuality, decideDownPayment(me, purchasePrice));
			}
			return false; // can't afford a house anyway   
		}
		double costOfHouse = mortgageApproval.monthlyPayment * config.constants.MONTHS_IN_YEAR
				- (1 / config.HOLD_PERIOD) * purchasePrice*getLongTermHPAExpectation();
		double costOfRent = Model.rentalMarketStats.getExpAvSalePriceForQuality(newHouseQuality)
				* config.constants.MONTHS_IN_YEAR;
		double probabilityPlaceBidOnHousingMarket = sigma(config.SENSITIVITY_RENT_OR_PURCHASE * (costOfRent
				//				        		*(1.0 + config.PSYCHOLOGICAL_COST_OF_RENTING) 
				- costOfHouse));
		boolean placeBidOnHousingMarket = prng.nextDouble() < probabilityPlaceBidOnHousingMarket;
		//continue to record AgentDecision data here. DECISION DATA SH The first part (bank data) is written in the
		// bank.requestApproval method
		if(config.recordAgentDecisions && (Model.getTime() >= config.TIME_TO_START_RECORDING)) {
			Model.agentDecisionRecorder.recordDecisionRentOrPurchase(me, mortgageApproval, costOfHouse, costOfRent, 
					purchasePrice, decideDownPayment(me, purchasePrice), newHouseQuality, probabilityPlaceBidOnHousingMarket, 
					placeBidOnHousingMarket);
		}
		// allow for some burning-in period, as in the first few periods all houses are empty
		if(config.procyclicalRentalMarket && Model.getTime() > 50) {
			if(Model.householdStats.getnEmptyHouses() > config.nEmptyHousesAboveWhichBidForRent) { return false;}
		}
		return placeBidOnHousingMarket;

	}

	/********************************************************
	 * Decide how much to bid on the rental market
	 * Source: Zoopla rental prices 2008-2009 (at Bank of England)
	 ********************************************************/
	double desiredRent(double monthlyGrossEmploymentIncome) {
		if (config.NDLVersion) {
			// this fits the Dutch HFCS data to a log-linear model
			double exponent = config.DESIRED_RENT_SCALE + 
					config.DESIRED_RENT_MU * Math.log(12 * monthlyGrossEmploymentIncome) + 
					config.DESIRED_RENT_SIGMA * prng.nextGaussian();
			double desiredPP = Math.exp(exponent) / 12 ; // the parameters are estimated on yearly values
			return desiredPP;
		}
		
		// allow for some burning-in period, as in the first few periods all houses are empty
		if (config.procyclicalRentalMarket && Model.getTime() > 500) {
			double fraction;
			// in an example y = 0.001 * x + (0.33-0.001*100)
			fraction = config.elasticityDesiredRent * Model.householdStats.getnEmptyHouses() + (config.DESIRED_RENT_INCOME_FRACTION
					-config.elasticityDesiredRent*config.nEmptyHousesDesiredRent);
			if(fraction<config.DESIRED_RENT_INCOME_FRACTION)fraction=config.DESIRED_RENT_INCOME_FRACTION;
			if(fraction>config.maxShareIncomeDesiredRent)fraction=config.maxShareIncomeDesiredRent;
			//    		System.out.println("Fraction is: " + fraction + "; HPI: " + Model.housingMarketStats.getHPI() + "; nEmptyHouses: " + Model.householdStats.getnEmptyHouses() + 
			//    				"; Model Time is: " + Model.getTime());

			return monthlyGrossEmploymentIncome*fraction;
		} else {
			return monthlyGrossEmploymentIncome*config.DESIRED_RENT_INCOME_FRACTION;
//			return Math.min(2350.0, monthlyGrossEmploymentIncome*config.DESIRED_RENT_INCOME_FRACTION);
		}
	}

	///////////////////////////////////////////////////////////////////////////////////////////////
	// Property investor behaviour
	///////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Decide whether to sell or not an investment property. Investor households with only one investment property do
	 * never sell it. A sale is never attempted when the house is occupied by a tenant. Households with at least two
	 * investment properties will calculate the expected yield of the property in question based on two contributions:
	 * rental yield and capital gain (with their corresponding weights which depend on the type of investor)
	 *
	 * @param h The house in question
	 * @param me The investor household
	 * @return True if investor me decides to sell investment property h
	 */
	boolean decideToSellInvestmentProperty(House h, Household me) {
		// Fast decisions...
		// ...always keep at least one investment property (i.e., at least two properties)
		if(me.getNProperties() < 3) {
			// if agent decisions are recorded, record basic information and reason for not selling
			if(config.recordAgentDecisions && (Model.getTime() >= config.TIME_TO_START_RECORDING)) {
				Model.agentDecisionRecorder.recordKeepOneProperty(me);
			}
			return false;
		}
		// ...don't sell while occupied by tenant
		if(!h.isOnRentalMarket()) return false;

		// Find the expected equity yield rate of this property as a weighted mix of both rental yield and capital gain
		// times the leverage
		// ...find the mortgage agreement for this property
		MortgageAgreement mortgage = me.mortgageFor(h);
		// ...find its current (fair market value) sale price
		double currentMarketPrice = housingMarketStats.getExpAvSalePriceForQuality(h.getQuality());
		// ...find equity, or assets minus liabilities
		double equity = Math.max(0.01, currentMarketPrice - mortgage.principal); // The 0.01 prevents possible divisions by zero later on
		// ...find the leverage on that mortgage (Assets divided by equity, or return on equity)
		double leverage = currentMarketPrice / equity;
		// ...find the expected rental yield of this property as its current rental price (under current average
		// occupancy) divided by its current (fair market value) sale price
		        double currentRentalYield = h.getRentalRecord().getPrice() * config.constants.MONTHS_IN_YEAR *
		//                 rentalMarketStats.getAvOccupancyForQuality(h.getQuality()) / currentMarketPrice;
//		double currentRentalYield = Model.rentalMarketStats.getAvFlowYieldForQuality(h.getQuality()) * 
				rentalMarketStats.getAvOccupancyForQuality(h.getQuality()) / currentMarketPrice;
		// ...find the mortgage rate (pounds paid a year per pound of equity)
		double mortgageRate = mortgage.nextPayment()*config.constants.MONTHS_IN_YEAR/equity;
		// ...finally, find expected equity yield, or yield on equity
		double expectedEquityYield = leverage*((1.0 - BTLCapGainCoefficient)*currentRentalYield
				+ BTLCapGainCoefficient*getLongTermHPAExpectation())
				- mortgageRate;
		// Compute a probability to keep the property as a function of the effective yield
		double pKeep = Math.pow(sigma(config.BTL_CHOICE_INTENSITY*expectedEquityYield),
				1.0/config.constants.MONTHS_IN_YEAR);

		boolean sell = prng.nextDouble() < (1.0 - pKeep);
		// if agent decision recorder is active, record decision parameters
		if(config.recordAgentDecisions && (Model.getTime() >= config.TIME_TO_START_RECORDING)) {
			Model.agentDecisionRecorder.recordDivestmentDecision(me, h, currentMarketPrice, equity, 
					leverage, currentRentalYield, mortgageRate, expectedEquityYield, pKeep, sell);
		}
		// Return true or false as a random draw from the computed probability
		return sell;
	}

	/**
	 * Decide whether to buy or not a new investment property. Investor households with no investment properties always
	 * attempt to buy one. If the household's bank balance is below its desired bank balance, then no attempt to buy is
	 * made. If the resources available to the household (maximum mortgage) are below the average price for the lowest
	 * quality houses, then no attempt to buy is made. Households with at least one investment property will calculate
	 * the expected yield of a new property based on two contributions: rental yield and capital gain (with their
	 * corresponding weights which depend on the type of investor)
	 *
	 * @param me The investor household
	 * @return True if investor me decides to try to buy a new investment property
	 */
	boolean decideToBuyInvestmentProperty(Household me) {
		// Fast decisions...
		//... with alternative consumption function some BTL investors seem to buy too many houses. Therefore,
		// they cannot pay the "bills" and go bankrupt every month.
		// if payments make up more than X% of monthly Net Total Income, don't invest
		if(config.ALTERNATE_CONSUMPTION_FUNCTION) {
			if((me.getMonthlyPayments()) > config.paymentsToIncome*me.returnMonthlyNetTotalIncome()) {
				// record DECISION DATA BTL
				if(config.recordAgentDecisions && (Model.getTime() >= config.TIME_TO_START_RECORDING)) {
					Model.agentDecisionRecorder.recordTooHighMonthlyPaymentsBTL(me);
				}
				return false;
			}
		}
		// ...always decide to buy if owning no investment property yet
		//		if (me.getNProperties() < 2) { 
		//			// record some DECISION DATA BTL
		//			if(config.recordAgentDecisions && (Model.getTime() >= config.TIME_TO_START_RECORDING)) {
		//				Model.agentDecisionRecorder.recordNoInvestmentPropertyYet(me);
		//			}
		//			return true; 
		//		}
		// ...never buy (keep on saving) if bank balance is below the household's desired bank balance
		// TODO: This mechanism and its parameter are not declared in the article! Any reference for the value of the parameter?
		if(!config.procyclicalCreditConstraints && !config.ALTERNATE_CONSUMPTION_FUNCTION) {
			if (me.getBankBalance() < 
					data.Wealth.getDesiredBankBalance(me.getAnnualGrossTotalIncome(), me.behaviour.getPropensityToSave())
					//    			getDesiredBankBalance(me.getAnnualGrossTotalIncome())
					*config.BTL_CHOICE_MIN_BANK_BALANCE) { 
				// record DECISION DATA BTL
				if(config.recordAgentDecisions && (Model.getTime() >= config.TIME_TO_START_RECORDING)) {
					Model.agentDecisionRecorder.recordBankBalanceTooLow(me, false);
				}

				return false; }
		}
		// ...find maximum price (maximum mortgage) the household could pay
		double maxPrice = Model.bank.getMaxMortgage(me, false, true);
		// ...never buy if that maximum price is below the average price for the lowest quality
		if (maxPrice < Model.housingMarketStats.getExpAvSalePriceForQuality(0)) { 
			// write DECISION DATA BTL
			if(config.recordAgentDecisions && (Model.getTime() >= config.TIME_TO_START_RECORDING)) {
				Model.agentDecisionRecorder.recordHousesTooExpensive(me);
			}
			return false; 
		}
		// Find the expected equity yield rate for a hypothetical house maximising the leverage available to the
		// household and assuming an average rental yield (over all qualities). This is found as a weighted mix of both
		// rental yield and capital gain times the leverage
		// ...find mortgage with maximum leverage by requesting maximum mortgage with minimum downpayment
		MortgageAgreement mortgage = Model.bank.requestApproval(me, maxPrice, 0.0, false, false);
		// ...find equity, or assets minus liabilities (which, initially, is simply the downpayment)
		double equity = Math.max(0.01, mortgage.downPayment); // The 0.01 prevents possible divisions by zero later on
		// ...find the leverage on that mortgage (Assets divided by equity, or return on equity)
		double leverage = mortgage.purchasePrice/equity;
		// ...find the expected rental yield as an (exponential) average over all house qualities
		double rentalYield = Model.rentalMarketStats.getExpAvFlowYield();
		// ...find the mortgage rate (pounds paid a year per pound of equity)
		double mortgageRate = mortgage.nextPayment()*config.constants.MONTHS_IN_YEAR/equity;
		// ...finally, find expected equity yield, or yield on equity
		double expectedEquityYield = leverage*((1.0 - BTLCapGainCoefficient)*rentalYield
				+ BTLCapGainCoefficient*getLongTermHPAExpectation())
				- mortgageRate;
		// Compute the probability to decide to buy an investment property as a function of the expected equity yield
		double pBuy = 1.0 - Math.pow((1.0 - sigma(config.BTL_CHOICE_INTENSITY*expectedEquityYield)),
				1.0/config.constants.MONTHS_IN_YEAR);
		// Return true or false as a random draw from the computed probability
		boolean bidOnTheHousingMarket = prng.nextDouble() < pBuy;

		// last part of the DECISION DATA BTL output
		if(config.recordAgentDecisions && (Model.getTime() >= config.TIME_TO_START_RECORDING)) {
			Model.agentDecisionRecorder.recordInvestmentDecision(me, equity, leverage, 
					rentalYield, mortgageRate, expectedEquityYield, pBuy, bidOnTheHousingMarket);
		}
		return bidOnTheHousingMarket;
	}

	/**
	 * Update the demanded rent for a property
	 *
	 * @param sale the HouseOfferRecord of the property for rent
	 * @return the new rent
	 */
	double rethinkBuyToLetRent(HouseOfferRecord sale) { return (1.0 - config.RENT_REDUCTION)*sale.getPrice(); }

	/**
	 * Logistic function, sometimes called sigma function, 1/1+e^(-x)
	 *
	 * @param x Parameter of the sigma or logistic function
	 */
	private double sigma(double x) { return 1.0/(1.0 + Math.exp(-1.0*x)); }

	/**
	 * Expectations of future house price growth are based on previous trend (longTermHPA), times a dampening or
	 * multiplier factor (depending on its value being <1 or >1), plus a constant (which can be positive or negative),
	 * according to the equation HPI(t+DT) = HPI(t) + FACTOR*DT*dHPI/dt + CONST
	 *
	 * @return Expectation of HPI in one year's time divided by today's HPI
	 */
	public double getLongTermHPAExpectation() {
		return housingMarketStats.getLongTermHPA() * config.HPA_EXPECTATION_FACTOR + config.HPA_EXPECTATION_CONST;
	}

	public double getBTLCapGainCoefficient() { return BTLCapGainCoefficient; }

	public boolean isPropertyInvestor() { return BTLInvestor; }

	public double getPropensityToSave() { return propensityToSave; }

	// PAUL insert a getter for airBnBinvestor
	public boolean isAirBnBInvestor() {
		return airBnBInvestor;
	}


}
