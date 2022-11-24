package collectors;

import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.math.RoundingMode;

import housing.Config;
import housing.Model;

public class MicroDataRecorder {

    //------------------//
    //----- Fields -----//
    //------------------//

    private String 		outputFolder;

    private PrintWriter outfileBankBalance;
    private PrintWriter outfileHousingWealth;
    private PrintWriter outfileNHousesOwned;
    private PrintWriter outfileSavingRate;
    private PrintWriter outfileMonthlyGrossTotalIncome;
    private PrintWriter outfileMonthlyGrossEmploymentIncome;
    private PrintWriter outfileMonthlyGrossRentalIncome;
    private PrintWriter outfileMonthlyDisposableIncome;
    private PrintWriter outfileMonthlyMortgagePayments;
    private PrintWriter outfileDebt;
    private PrintWriter outfileConsumption;
    private PrintWriter outfileIncomeConsumption;
    private PrintWriter outfileFinancialWealthConsumption;
    private PrintWriter outfileHousingWealthConsumption;
    private PrintWriter outfileDebtConsumption;
    private PrintWriter outfileSavingForDeleveraging;
    private PrintWriter outfileBTL;
    private PrintWriter outfileFTB;
    private PrintWriter outfileInFirstHome;
    private PrintWriter outfileAge;
    private PrintWriter outfileTransactionRevenue;
    private PrintWriter outfileId;
    private PrintWriter outfileNewCredit;
    private PrintWriter outfilePrincipalRepRegular;
    private PrintWriter outfilePrincipalRepIrregular;
    private PrintWriter outfilePrincipalRepSale;
    private PrintWriter outfileBankcuptcyCashInjection;
    private PrintWriter outfilePrincipalPaidBackInheritance;
    private PrintWriter outfileFinancialVulnerabilityReason;
    private PrintWriter outfileFinancialVulnerabilitySince;
    private PrintWriter outfileShockedMonthlyDisposableIncome;
    

    //------------------------//
    //----- Constructors -----//
    //------------------------//

    public MicroDataRecorder(String outputFolder) { this.outputFolder = outputFolder; }

    //-------------------//
    //----- Methods -----//
    //-------------------//

    public void openSingleRunSingleVariableFiles(int nRun, boolean recordBankBalance, boolean recordInitTotalWealth,
                                                 boolean recordNHousesOwned, boolean recordSavingRate,
                                                 boolean recordMonthlyGrossTotalIncome, boolean recordMonthlyGrossEmploymentIncome,
                                                 boolean recordMonthlyGrossRentalIncome, boolean recordMonthlyDisposableIncome,
                                                 boolean recordMonthlyMortgagePayments, boolean recordDebt,
                                                 boolean recordConsumption, boolean recordIncomeConsumption,
                                                 boolean recordFinancialWealthConsumption, boolean recordHousingWealthConsumption,
                                                 boolean recordDebtConsumption, boolean recordSavingForDeleveraging, boolean recordBTL, 
                                                 boolean recordFTB, boolean recordInFirstHome, boolean recordAge, boolean recordTransactionRevenue,
                                                 boolean recordId, boolean recordNewCredit, boolean recordPrincipalRepRegular,
                                                 boolean recordPrincipalRepIrregular, boolean recordprincipalRepSale,
                                                 boolean recordBankcuptcyCashInjection, boolean recordPrincipalPaidBackInheritance,
                                                 boolean recordFinancialVulnerability, boolean recordShockedMonthlyDisposableIncome) {
        if (recordBankBalance) {
            try {
                outfileBankBalance = new PrintWriter(outputFolder + "BankBalance-run" + nRun
                        + ".csv", "UTF-8");
            } catch (FileNotFoundException | UnsupportedEncodingException e) {
                e.printStackTrace();
            }
        }
        if (recordInitTotalWealth) {
            try {
                outfileHousingWealth = new PrintWriter(outputFolder + "NetHousingWealth-run" + nRun
                        + ".csv", "UTF-8");
            } catch (FileNotFoundException | UnsupportedEncodingException e) {
                e.printStackTrace();
            }
        }
        if (recordNHousesOwned) {
            try {
                outfileNHousesOwned = new PrintWriter(outputFolder + "NHousesOwned-run" + nRun
                        + ".csv", "UTF-8");
            } catch (FileNotFoundException | UnsupportedEncodingException e) {
                e.printStackTrace();
            }
        }
        if (recordSavingRate) {
            try {
                outfileSavingRate = new PrintWriter(outputFolder + "SavingRate-run" + nRun
                        + ".csv", "UTF-8");
            } catch (FileNotFoundException | UnsupportedEncodingException e) {
                e.printStackTrace();
            }
        }
        if(recordMonthlyGrossTotalIncome) {
        	try {
        		outfileMonthlyGrossTotalIncome = new PrintWriter(outputFolder + 
        				"MonthlyGrossTotalIncome-run" + nRun + ".csv", "UTF-8");
        	} catch(FileNotFoundException | UnsupportedEncodingException e) {
        		e.printStackTrace();
        	}
        }
        if(recordMonthlyGrossEmploymentIncome) {
        	try {
        		outfileMonthlyGrossEmploymentIncome = new PrintWriter(outputFolder + 
        				"MonthlyGrossEmploymentIncome-run" + nRun + ".csv", "UTF-8");
        	} catch(FileNotFoundException | UnsupportedEncodingException e) {
        		e.printStackTrace();
        	}
        }
        if(recordMonthlyDisposableIncome) {
        	try {
        		outfileMonthlyDisposableIncome = new PrintWriter(outputFolder + 
        				"MonthlyDisposableIncome-run" + nRun + ".csv", "UTF-8");
        	} catch(FileNotFoundException | UnsupportedEncodingException e) {
        		e.printStackTrace();
        	}
        }
        if(recordMonthlyMortgagePayments) {
        	try {
        		outfileMonthlyMortgagePayments = new PrintWriter(outputFolder + 
        				"MonthlyMortgagePayments-run" + nRun + ".csv", "UTF-8");
        	} catch(FileNotFoundException | UnsupportedEncodingException e) {
        		e.printStackTrace();
        	}
        }
        if(recordMonthlyGrossRentalIncome) {
        	try {
        		outfileMonthlyGrossRentalIncome = new PrintWriter(outputFolder + 
        				"MonthlyGrossRentalIncome-run" + nRun + ".csv", "UTF-8");
        	} catch(FileNotFoundException | UnsupportedEncodingException e) {
        		e.printStackTrace();
        	}
        }
        if(recordDebt) {
        	try {
        		outfileDebt = new PrintWriter(outputFolder + 
        				"Debt-run" + nRun + ".csv", "UTF-8");
        	} catch(FileNotFoundException | UnsupportedEncodingException e) {
        		e.printStackTrace();
        	}
        }
        if(recordConsumption) {
        	try {
        		outfileConsumption = new PrintWriter(outputFolder + 
        				"Consumption-run" + nRun + ".csv", "UTF-8");
        	} catch(FileNotFoundException | UnsupportedEncodingException e) {
        		e.printStackTrace();
        	}
        }
        if(recordIncomeConsumption) {
        	try {
        		outfileIncomeConsumption = new PrintWriter(outputFolder + 
        				"IncomeConsumption-run" + nRun + ".csv", "UTF-8");
        	} catch(FileNotFoundException | UnsupportedEncodingException e) {
        		e.printStackTrace();
        	}
        }
        if(recordFinancialWealthConsumption) {
        	try {
        		outfileFinancialWealthConsumption = new PrintWriter(outputFolder + 
        				"FinancialWealthConsumption-run" + nRun + ".csv", "UTF-8");
        	} catch(FileNotFoundException | UnsupportedEncodingException e) {
        		e.printStackTrace();
        	}
        }
        if(recordHousingWealthConsumption) {
        	try {
        		outfileHousingWealthConsumption = new PrintWriter(outputFolder + 
        				"HousingWealthConsumption-run" + nRun + ".csv", "UTF-8");
        	} catch(FileNotFoundException | UnsupportedEncodingException e) {
        		e.printStackTrace();
        	}
        }
        if(recordDebtConsumption) {
        	try {
        		outfileDebtConsumption = new PrintWriter(outputFolder + 
        				"DebtConsumption-run" + nRun + ".csv", "UTF-8");
        	} catch(FileNotFoundException | UnsupportedEncodingException e) {
        		e.printStackTrace();
        	}
        }
        if(recordSavingForDeleveraging) {
        	try {
        		outfileSavingForDeleveraging = new PrintWriter(outputFolder + 
        				"SavingForDeleveraging-run" + nRun + ".csv", "UTF-8");
        	} catch(FileNotFoundException | UnsupportedEncodingException e) {
        		e.printStackTrace();
        	}
        }
        if(recordBTL) {
        	try {
        		outfileBTL = new PrintWriter(outputFolder + 
        				"isBTL-run" + nRun + ".csv", "UTF-8");
        	} catch(FileNotFoundException | UnsupportedEncodingException e) {
        		e.printStackTrace();
        	}
        }
        if(recordFTB) {
        	try {
        		outfileFTB = new PrintWriter(outputFolder + 
        				"isFTB-run" + nRun + ".csv", "UTF-8");
        	} catch(FileNotFoundException | UnsupportedEncodingException e) {
        		e.printStackTrace();
        	}
        }
        if(recordInFirstHome) {
        	try {
        		outfileInFirstHome = new PrintWriter(outputFolder + 
        				"isInFirstHome-run" + nRun + ".csv", "UTF-8");
        	} catch(FileNotFoundException | UnsupportedEncodingException e) {
        		e.printStackTrace();
        	}
        }
        if(recordAge) {
        	try {
        		outfileAge = new PrintWriter(outputFolder + 
        				"Age-run" + nRun + ".csv", "UTF-8");
        	} catch(FileNotFoundException | UnsupportedEncodingException e) {
        		e.printStackTrace();
        	}
        }
        if(recordTransactionRevenue) {
        	try {
        		outfileTransactionRevenue = new PrintWriter(outputFolder + 
        				"TransactionRevenue-run" + nRun + ".csv", "UTF-8");
        	} catch(FileNotFoundException | UnsupportedEncodingException e) {
        		e.printStackTrace();
        	}
        }
        if(recordId) {
        	try {
        		outfileId = new PrintWriter(outputFolder + 
        				"Id-run" + nRun + ".csv", "UTF-8");
        	} catch(FileNotFoundException | UnsupportedEncodingException e) {
        		e.printStackTrace();
        	}
        }
        if(recordNewCredit) {
        	try {
        		outfileNewCredit = new PrintWriter(outputFolder + 
        				"NewCredit-run" + nRun + ".csv", "UTF-8");
        	} catch(FileNotFoundException | UnsupportedEncodingException e) {
        		e.printStackTrace();
        	}
        }
        if(recordPrincipalRepRegular) {
        	try {
        		outfilePrincipalRepRegular = new PrintWriter(outputFolder + 
        				"PrincipalRepRegular-run" + nRun + ".csv", "UTF-8");
        	} catch(FileNotFoundException | UnsupportedEncodingException e) {
        		e.printStackTrace();
        	}
        }
        if(recordPrincipalRepIrregular) {
        	try {
        		outfilePrincipalRepIrregular = new PrintWriter(outputFolder + 
        				"PrincipalRepIrregular-run" + nRun + ".csv", "UTF-8");
        	} catch(FileNotFoundException | UnsupportedEncodingException e) {
        		e.printStackTrace();
        	}
        }
        if(recordprincipalRepSale) {
        	try {
        		outfilePrincipalRepSale = new PrintWriter(outputFolder + 
        				"PrincipalRepSale-run" + nRun + ".csv", "UTF-8");
        	} catch(FileNotFoundException | UnsupportedEncodingException e) {
        		e.printStackTrace();
        	}
        }
        if(recordBankcuptcyCashInjection) {
        	try {
        		outfileBankcuptcyCashInjection = new PrintWriter(outputFolder + 
        				"BankcuptcyCashInjection-run" + nRun + ".csv", "UTF-8");
        	} catch(FileNotFoundException | UnsupportedEncodingException e) {
        		e.printStackTrace();
        	}
        }
        if(recordPrincipalPaidBackInheritance) {
        	try {
        		outfilePrincipalPaidBackInheritance = new PrintWriter(outputFolder + 
        				"PrincipalPaidBackInheritance-run" + nRun + ".csv", "UTF-8");
        	} catch(FileNotFoundException | UnsupportedEncodingException e) {
        		e.printStackTrace();
        	}
        }
        if(recordFinancialVulnerability) {
        	try {
        		outfileFinancialVulnerabilityReason = new PrintWriter(outputFolder + 
        				"FinVulReason-run" + nRun + ".csv", "UTF-8");
        		outfileFinancialVulnerabilitySince = new PrintWriter(outputFolder + 
        				"FinVulSince-run" + nRun + ".csv", "UTF-8");
        	} catch(FileNotFoundException | UnsupportedEncodingException e) {
        		e.printStackTrace();
        	}
        }
        if(recordShockedMonthlyDisposableIncome) {
        	try {
        		outfileShockedMonthlyDisposableIncome = new PrintWriter(outputFolder + 
        				"ShockedMonthlyDisposableIncome-run" + nRun + ".csv", "UTF-8");
        		outfileShockedMonthlyDisposableIncome = new PrintWriter(outputFolder + 
        				"ShockedMonthlyDisposableIncome-run" + nRun + ".csv", "UTF-8");
        	} catch(FileNotFoundException | UnsupportedEncodingException e) {
        		e.printStackTrace();
        	}
        }
    }

    void timeStampSingleRunSingleVariableFiles(int time, boolean recordBankBalance, boolean recordHousingWealth,
                                               boolean recordNHousesOwned, boolean recordSavingRate, 
                                               boolean recordMonthlyGrossTotalIncome, boolean recordMonthlyGrossEmploymentIncome,
                                               boolean recordMonthlyGrossRentalIncome, boolean recordMonthlyDisposableIncome,
                                               boolean recordMonthlyMortgagePayments, boolean recordDebt,
                                               boolean recordConsumption, boolean recordIncomeConsumption,
                                               boolean recordFinancialWealthConsumption, boolean recordHousingWealthConsumption,
                                               boolean recordDebtConsumption, boolean recordSavingForDeleveraging,
                                               boolean recordBTL, boolean recordFTB, boolean recordInFirstHome, boolean recordAge, 
                                               boolean recordTransactionRevenue, boolean recordId, boolean recordNewCredit, 
                                               boolean recordPrincipalRepRegular, boolean recordPrincipalRepIrregular, boolean recordprincipalRepSale,
                                               boolean recordBankcuptcyCashInjection, boolean recordPrincipalPaidBackInheritance,
                                               boolean recordFinancialVulnerability, boolean recordShockedMonthlyDisposableIncome
                                               ) {
        if (time % Model.config.microDataRecordIntervall == 0 && time >= Model.config.TIME_TO_START_RECORDING) {
            if (recordBankBalance) {
                if (time != 0) {
                    outfileBankBalance.println("");
                }
                outfileBankBalance.print(time);
            }
            if (recordHousingWealth) {
                if (time != 0) {
                    outfileHousingWealth.println("");
                }
                outfileHousingWealth.print(time);
            }
            if (recordNHousesOwned) {
                if (time != 0) {
                    outfileNHousesOwned.println("");
                }
                outfileNHousesOwned.print(time);
            }
            if (recordSavingRate) {
            	if (time != 0) {
            		outfileSavingRate.println("");
            	}
            	outfileSavingRate.print(time);
            }
            if (recordMonthlyGrossTotalIncome) {
            	if(time!=0) {
            		outfileMonthlyGrossTotalIncome.println("");
            	}
            	outfileMonthlyGrossTotalIncome.print(time);
            }
            if (recordMonthlyGrossEmploymentIncome) {
            	if(time!=0) {
            		outfileMonthlyGrossEmploymentIncome.println("");
            	}
            	outfileMonthlyGrossEmploymentIncome.print(time);
            }
            if (recordMonthlyGrossRentalIncome) {
            	if(time!=0) {
            		outfileMonthlyGrossRentalIncome.println("");
            	}
            	outfileMonthlyGrossRentalIncome.print(time);
            }
            if (recordMonthlyDisposableIncome) {
            	if (time != 0) {
            		outfileMonthlyDisposableIncome.println("");
            	}
            	outfileMonthlyDisposableIncome.print(time);
            }
            if (recordMonthlyMortgagePayments) {
            	if (time != 0) {
            		outfileMonthlyMortgagePayments.println("");
            	}
            	outfileMonthlyMortgagePayments.print(time);
            }
            if (recordDebt) {
            	if (time != 0) {
            		outfileDebt.println("");
            	}
            	outfileDebt.print(time);
            }
            if (recordConsumption) {
            	if (time != 0) {
            		outfileConsumption.println("");
            	}
            	outfileConsumption.print(time);
            }
            if (recordIncomeConsumption) {
            	if (time != 0) {
            		outfileIncomeConsumption.println("");
            	}
            	outfileIncomeConsumption.print(time);
            }
            if (recordFinancialWealthConsumption) {
            	if (time != 0) {
            		outfileFinancialWealthConsumption.println("");
            	}
            	outfileFinancialWealthConsumption.print(time);
            }
            if (recordHousingWealthConsumption) {
            	if (time != 0) {
            		outfileHousingWealthConsumption.println("");
            	}
            	outfileHousingWealthConsumption.print(time);
            }
            if (recordDebtConsumption) {
            	if (time != 0) {
            		outfileDebtConsumption.println("");
            	}
            	outfileDebtConsumption.print(time);
            }
            if (recordSavingForDeleveraging) {
            	if (time != 0) {
            		outfileSavingForDeleveraging.println("");
            	}
            	outfileSavingForDeleveraging.print(time);
            }
            if (recordBTL) {
            	if (time != 0) {
            		outfileBTL.println("");
            	}
            	outfileBTL.print(time);
            }
            if (recordFTB) {
            	if (time != 0) {
            		outfileFTB.println("");
            	}
            	outfileFTB.print(time);
            }
            if (recordInFirstHome) {
            	if (time != 0) {
            		outfileInFirstHome.println("");
            	}
            	outfileInFirstHome.print(time);
            }            
            if (recordAge) {
            	if (time != 0) {
            		outfileAge.println("");
            	}
            	outfileAge.print(time);
            }
            if (recordTransactionRevenue) {
            	if (time != 0) {
            		outfileTransactionRevenue.println("");
            	}
            	outfileTransactionRevenue.print(time);
            }
            if (recordId) {
            	if (time != 0) {
            		outfileId.println("");
            	}
            	outfileId.print(time);
            }
            if (recordNewCredit) {
            	if (time != 0) {
            		outfileNewCredit.println("");
            	}
            	outfileNewCredit.print(time);
            }
            if (recordPrincipalRepRegular) {
            	if (time != 0) {
            		outfilePrincipalRepRegular.println("");
            	}
            	outfilePrincipalRepRegular.print(time);
            }
            if (recordPrincipalRepIrregular) {
            	if (time != 0) {
            		outfilePrincipalRepIrregular.println("");
            	}
            	outfilePrincipalRepIrregular.print(time);
            }
            if (recordprincipalRepSale) {
            	if (time != 0) {
            		outfilePrincipalRepSale.println("");
            	}
            	outfilePrincipalRepSale.print(time);
            }
            if (recordBankcuptcyCashInjection) {
            	if (time != 0) {
            		outfileBankcuptcyCashInjection.println("");
            	}
            	outfileBankcuptcyCashInjection.print(time);
            }
            if (recordPrincipalPaidBackInheritance) {
            	if (time != 0) {
            		outfilePrincipalPaidBackInheritance.println("");
            	}
            	outfilePrincipalPaidBackInheritance.print(time);
            }
            if (recordFinancialVulnerability) {
            	if (time != 0) {
            		outfileFinancialVulnerabilityReason.println("");
            		outfileFinancialVulnerabilitySince.println("");
            	}
            	outfileFinancialVulnerabilityReason.print(time);
        		outfileFinancialVulnerabilitySince.print(time);
            }
            if (recordShockedMonthlyDisposableIncome) {
            	if (time != 0) {
            		outfileShockedMonthlyDisposableIncome.println("");
            	}
            	outfileShockedMonthlyDisposableIncome.print(time);
            }
        }
    }
	
    void recordBankBalance(int time, double bankBalance) {
    	outfileBankBalance.print(", " + round(bankBalance, 3));
    }

    void recordHousingWealth(int time, double housingWealth) {
    	outfileHousingWealth.print(", " + round(housingWealth,3));
    }

    void recordNHousesOwned(int time, int nHousesOwned) {
        outfileNHousesOwned.print(", " + nHousesOwned);
    }

    void recordSavingRate(int time, double savingRate) {
        outfileSavingRate.print(", " + savingRate);
    }
    
    void recordMonthlyGrossTotalIncome(int time, double monthlyGrossTotalIncome) {
    	outfileMonthlyGrossTotalIncome.print(", " + round(monthlyGrossTotalIncome,3));
    }
    
    void recordMonthlyGrossEmploymentIncome(int time, double monthlyGrossEmploymentIncome) {
    	outfileMonthlyGrossEmploymentIncome.print(", " + round(monthlyGrossEmploymentIncome,3));
    }
    
    void recordMonthlyGrossRentalIncome(int time, double monthlyGrossRentalIncome) {
    	outfileMonthlyGrossRentalIncome.print(", " + round(monthlyGrossRentalIncome,3));
    }
    
    void recordMonthlyDisposableIncome(int time, double monthlyDisposableIncome) {
    	outfileMonthlyDisposableIncome.print(", " + round(monthlyDisposableIncome,3));
    }
    
    void recordMonthlyMortgagePayments(int time, double monthlyMortgagePayments) {
    	outfileMonthlyMortgagePayments.print(", " + round(monthlyMortgagePayments,3));
    }
    
    void recordDebt(int time, double debt) {
    	outfileDebt.print(", " + round(debt,3));
    }
    
    void recordConsumption(int time, double consumption) {
		outfileConsumption.print(", " + round(consumption,3));
	}
    
    void recordIncomeConsumption(int time, double consumption) {
		outfileIncomeConsumption.print(", " + round(consumption,3));
    }
    
    void recordFinancialWealthConsumption(int time, double consumption) {
		outfileFinancialWealthConsumption.print(", " + round(consumption,3));
    }
    
    void recordHousingWealthConsumption(int time, double consumption) {
    	outfileHousingWealthConsumption.print(", " + round(consumption,3));
    }
    
    void recordDebtConsumption(int time, double consumption) {
		outfileDebtConsumption.print(", " + round(consumption,3));
    }
    
    void recordSavingForDeleveraging(int time, double savingForDeleveraging) {
    	outfileSavingForDeleveraging.print(", " + round(savingForDeleveraging,3));
    }
    
    void recordBTL(int time, boolean isBTL) {
		if(isBTL)outfileBTL.print(", " + 1);
		else outfileBTL.print(", " + 0);
    }
    void recordFTB(int time, boolean isFTB) {
		if(isFTB)outfileFTB.print(", " + 1);
		else outfileFTB.print(", " + 0);
    }
    void recordInFirstHome(int time, boolean inFirstHome) {
		if(inFirstHome)outfileInFirstHome.print(", " + 1);
		else outfileInFirstHome.print(", " + 0);
    }
    void recordAge(int time, double Age) {
    	outfileAge.print(", " + round(Age,3));
    }
    void recordTransactionRevenue(int time, double transactionRevenue) {
    	outfileTransactionRevenue.print(", " + round(transactionRevenue,3));
    }
    void recordId(int time, int id) {
    	outfileId.print(", " + id);
    }
    void recordNewCredit(int time, double newCredit) {
    	outfileNewCredit.print(", " + round(newCredit,3));
    }
    void recordPrincipalRepRegular(int time, double principalRepRegular) {
    	outfilePrincipalRepRegular.print(", " + round(principalRepRegular,3));
    }
    void recordPrincipalRepIrregular(int time, double principalRepIrregular) {
    	outfilePrincipalRepIrregular.print(", " + round(principalRepIrregular,3));
    }
    void recordPrincipalRepSale(int time, double principalRepSale) {
    	outfilePrincipalRepSale.print(", " + round(principalRepSale,3));
    }
    void recordBankcuptcyCashInjection(int time, double cashInjection) {
    	outfileBankcuptcyCashInjection.print(", " + round(cashInjection,3));
    }
    void recordPrincipalPaidBackInheritance(int time, double principalPaidBackInheritance) {
    	outfilePrincipalPaidBackInheritance.print(", " + round(principalPaidBackInheritance,3));
    }
    void recordFinancialVulnerability(int time, String vulCause, int vulSince) {
    	outfileFinancialVulnerabilityReason.print(", " + vulCause);
		outfileFinancialVulnerabilitySince.print(", " + (time - vulSince));
    }
    void recordShockedMonthlyDisposableIncome(int time, double shockedMonthlyDisposableIncome) {
    	outfileShockedMonthlyDisposableIncome.print(", " + round(shockedMonthlyDisposableIncome,3));
    }
    


	public void finishRun(boolean recordBankBalance, boolean recordHousingWealth, boolean recordNHousesOwned,
                          boolean recordSavingRate, boolean recordMonthlyGrossTotalIncome, boolean recordMonthlyGrossEmploymentIncome,
                          boolean recordMonthlyGrossRentalIncome, boolean recordMonthlyDisposableIncome,
                          boolean recordMonthlyMortgagePayments, boolean recordDebt, boolean recordConsumption, 
                          boolean recordIncomeConsumption, boolean recordFinancialWealthConsumption, boolean recordHousingWealthConsumption,
                          boolean recordDebtConsumption, boolean recordSavingForDeleveraging, boolean recordBTL,
                          boolean recordFTB, boolean recordInFirstHome, boolean recordAge, boolean recordTransactionRevenue,
                          boolean recordId, boolean recordNewCredit, boolean recordPrincipalRepRegular,
                          boolean recordPrincipalRepIrregular, boolean recordprincipalRepSale,
                          boolean recordBankcuptcyCashInjection, boolean recordPrincipalPaidBackInheritance,
                          boolean recordFinancialVulnerability, boolean recordShockedMonthlyDisposableIncome) {
        if (recordBankBalance) {
            outfileBankBalance.close();
        }
        if (recordHousingWealth) {
            outfileHousingWealth.close();
        }
        if (recordNHousesOwned) {
            outfileNHousesOwned.close();
        }
        if (recordSavingRate) {
            outfileSavingRate.close();
        }
        if (recordMonthlyGrossTotalIncome) {
        	outfileMonthlyGrossTotalIncome.close();
        }
        if (recordMonthlyGrossEmploymentIncome) {
        	outfileMonthlyGrossEmploymentIncome.close();
        }
        if (recordMonthlyGrossRentalIncome) {
        	outfileMonthlyGrossRentalIncome.close();
        }
        if (recordMonthlyDisposableIncome) {
        	outfileMonthlyDisposableIncome.close();
        }
        if (recordMonthlyMortgagePayments) {
        	outfileMonthlyMortgagePayments.close();
        }
        if (recordDebt) {
        	outfileDebt.close();
        }
        if (recordConsumption) {
        	outfileConsumption.close();
        }
        if (recordIncomeConsumption) {
        	outfileIncomeConsumption.close();
        }
        if (recordFinancialWealthConsumption) {
        	outfileFinancialWealthConsumption.close();
        }
        if (recordHousingWealthConsumption) {
        	outfileHousingWealthConsumption.close();
        }
        if (recordDebtConsumption) {
        	outfileDebtConsumption.close();
        }
        if (recordSavingForDeleveraging) {
        	outfileSavingForDeleveraging.close();
        }
        if (recordBTL) {
        	outfileBTL.close();
        }
        if (recordFTB) {
        	outfileFTB.close();
        }
        if (recordInFirstHome) {
        	outfileInFirstHome.close();
        }
        if (recordAge) {
        	outfileAge.close();
        }
        if (recordTransactionRevenue) {
        	outfileTransactionRevenue.close();
        }
        if (recordId) {
        	outfileId.close();
        }
        if (recordNewCredit) {
        	outfileNewCredit.close();
        }
        if (recordPrincipalRepRegular) {
        	outfilePrincipalRepIrregular.close();
        }
        if (recordPrincipalRepIrregular) {
        	outfilePrincipalRepIrregular.close();
        }
        if (recordprincipalRepSale) {
        	outfilePrincipalRepSale.close();
        }
        if (recordBankcuptcyCashInjection) {
        	outfileBankcuptcyCashInjection.close();
        }
        if (recordPrincipalPaidBackInheritance) {
        	outfilePrincipalPaidBackInheritance.close();
        }
        if (recordFinancialVulnerability) {
        	outfileFinancialVulnerabilityReason.close();
    		outfileFinancialVulnerabilitySince.close();
        }
        if (recordShockedMonthlyDisposableIncome) {
        	outfileShockedMonthlyDisposableIncome.close();
        }
	}
	
	public static double round(double value, int places) {
	    if (places < 0) throw new IllegalArgumentException();
	    BigDecimal bd = new BigDecimal(value);
	    bd = bd.setScale(places, RoundingMode.HALF_UP);
	    return bd.doubleValue();
	}
}
