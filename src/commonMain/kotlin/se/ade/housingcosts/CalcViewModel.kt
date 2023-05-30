package se.ade.housingcosts

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import java.math.RoundingMode
import java.text.DecimalFormat

class CalcViewModel {
    private val _uiState = MutableStateFlow(CalcUiState(
        input = CalcInputUiState(
            purchasePriceText = "1500000",
            salePriceText = "2000000",
            deferSalesTax = true,
            loansRemainingText = "1000000",
            extraDepositText = "100000",
            householdIncomeAfterTax = "54000",
            householdIncomePreTax = "85500",
            interestRate = 4.50f,
            objectPriceText = "6000000",
            objectPantbrev = "0",
        ),
        result = CalcResultUiState()
    ))
    val uiState = _uiState.asStateFlow()

    init {
        refresh()
    }

    fun onEvent(event: CalcUiEvent) {
        when(event) {
            is CalcUiEvent.EditDeposit -> _uiState.update {
                it.copy(input = it.input.copy(extraDepositText = event.value))
            }
            is CalcUiEvent.EditPurchasePrice -> _uiState.update {
                it.copy(input = it.input.copy(purchasePriceText = event.value))
            }
            is CalcUiEvent.EditSalePrice -> _uiState.update {
                it.copy(input = it.input.copy(salePriceText = event.value))
            }
            is CalcUiEvent.EditIncomeAfterTax -> _uiState.update {
                it.copy(input = it.input.copy(householdIncomeAfterTax = event.value))
            }
            is CalcUiEvent.EditIncomePreTax -> _uiState.update {
                it.copy(input = it.input.copy(householdIncomePreTax = event.value))
            }
            is CalcUiEvent.EditLoanRemaining -> _uiState.update {
                it.copy(input = it.input.copy(loansRemainingText = event.value))
            }
            is CalcUiEvent.ChangeInterestRate -> _uiState.update {
                it.copy(input = it.input.copy(interestRate = event.value))
            }
            is CalcUiEvent.EditObjectPrice -> _uiState.update {
                it.copy(input = it.input.copy(objectPriceText = event.value))
            }
            is CalcUiEvent.EditPantbrevSum -> _uiState.update {
                it.copy(input = it.input.copy(objectPantbrev = event.value))
            }
            is CalcUiEvent.ToggleDeferSalesTax -> _uiState.update {
                it.copy(input = it.input.copy(deferSalesTax = !it.input.deferSalesTax))
            }
        }

        refresh()
    }

    private fun refresh() {
        val snapshot = uiState.value.input
        val purchasePrice = snapshot.purchasePriceText.toIntOrNull()
            ?: return

        val salesPrice = snapshot.salePriceText.toIntOrNull()
            ?: return

        val savingsCapital = snapshot.extraDepositText.toIntOrNull()
            ?: return

        val objectPrice = snapshot.objectPriceText.toIntOrNull()
            ?: return

        val loanRemaining = snapshot.loansRemainingText.toIntOrNull()
            ?: return

        val incomeMonthlyPreTax = snapshot.householdIncomePreTax.toIntOrNull()
            ?: return

        val incomeMonthlyAfterTax = snapshot.householdIncomeAfterTax.toIntOrNull()
            ?: return

        val existingPantbrev = snapshot.objectPantbrev.toIntOrNull()
            ?: return

        val deferSalesTax = snapshot.deferSalesTax

        val interestRate = snapshot.interestRate

        val profits = if(salesPrice > purchasePrice) salesPrice - purchasePrice else 0
        val adjustedProfits = 22.0 / 30.0 * profits.toDouble()
        val salesTax = if(deferSalesTax) 0.0 else adjustedProfits * 0.3
        val carryOverAfterTax = salesPrice - salesTax - loanRemaining

        /*
            Regel: Lagfart
            Att få lagfart kostar 1,5 procent av antingen köpeskillingen eller taxeringsvärdet (det värde som är högst) i stämpelskatt.
         */
        val lagfart = objectPrice * 0.015

        val depositBeforePantBrev = savingsCapital + carryOverAfterTax - lagfart

        /*
            Pantbrev
            Pantbrev kostar 2 procent av pantbrevets belopp i stämpelskatt. Finns det redan uttagna pantbrev i fastigheten kan du använda dem.
        */
        val pantbrevFee = when {
            objectPrice - depositBeforePantBrev - existingPantbrev > 0 -> (objectPrice - depositBeforePantBrev - existingPantbrev) * 0.02
            else -> 0.0
        }

        val totalDeposit = depositBeforePantBrev - pantbrevFee
        val loanAmount = objectPrice - totalDeposit

        val loanRatioPercent = (loanAmount / objectPrice) * 100

        val problems = mutableListOf<CalcUiProblems>()

        if(loanRatioPercent > 85)
            problems.add(CalcUiProblems.LoanMoreThan85Percent)

        val minDeposit = objectPrice * 0.15

        val yearlyInterest = loanAmount * (interestRate.toDouble() / 100)
        val monthlyPaymentInterest = yearlyInterest/12


        val df = DecimalFormat("###,###,###,###.##")
        df.roundingMode = RoundingMode.HALF_UP

        /*
        Regel: Belåningsgrad
        Om lånet överstiger 50 eller 70 procent av bostadens marknadsvärde behöver man
        amortera 1 resp 2 procent av lånet per år.
        */
        val marketValueToLoanSizeQuotaPercentAmortization = when {
            loanAmount > objectPrice * 0.7 -> 2.0
            loanAmount > objectPrice * 0.5 -> 1.0
            else -> 0.0
        }

        val amortizationMonthlyMarketValueRule = (marketValueToLoanSizeQuotaPercentAmortization / 100 * loanAmount) / 12

        /*
            Regel: Lån kontra bruttoinkomst
            Om du lånar mer än 4,5 gånger din bruttoårsinkomst måste du amortera 1 % per år på bolånet.
            När du räknar ut om ditt lån ska omfattas av detta amorteringskrav jämför du din årsinkomst före skatt
            med det nya lånet inklusive eventuella bolån som du redan har.
         */
        val incomeQuotaRuleBreakpoint = (incomeMonthlyPreTax*12) * 4.5

        val incomeToLoanSizeQuotaPercentAmortizationPercent = when {
            loanAmount > incomeQuotaRuleBreakpoint -> 1.0
            else -> 0.0
        }

        val amortizationMonthlyIncomeRule = (incomeToLoanSizeQuotaPercentAmortizationPercent / 100 * loanAmount) / 12


        val totalMonthlyPayment = monthlyPaymentInterest + amortizationMonthlyIncomeRule + amortizationMonthlyMarketValueRule
        val leftToLiveOn = incomeMonthlyAfterTax - totalMonthlyPayment

        val oneTimeFees = lagfart + pantbrevFee

        /*
         * Skatteavdrag
         * skatteavdrag med 30 % för räntekostnader upp till 100 000 kr per person och år. På överskjutande belopp är avdraget 21%.
         */
        val taxRebateBaseAmount = Math.min(yearlyInterest, 100_000.0)
        val taxRebateExtraAmount = Math.max(0.0, yearlyInterest - 100_000.0)
        val taxRebate = taxRebateBaseAmount * 0.3 + taxRebateExtraAmount * 0.21

        val result = CalcResultUiState(
            profitText = df.format(profits),
            salesTaxText = df.format(salesTax),
            carryOverAfterTax = df.format(carryOverAfterTax),
            totalDepositText = df.format(totalDeposit),
            loanAmountText = df.format(loanAmount),
            yearlyInterest = df.format(yearlyInterest),
            monthlyPaymentInterest = df.format(monthlyPaymentInterest),
            loanQuotaMarketvalueAmortizationMonthly = df.format(amortizationMonthlyMarketValueRule),
            loanQuotaIncomeAmortizationMonthly = df.format(amortizationMonthlyIncomeRule),
            totalMonthlyPayment = df.format(totalMonthlyPayment),
            leftToLiveOn = df.format(leftToLiveOn),
            lagfart = df.format(lagfart),
            pantbrevFee = df.format(pantbrevFee),
            oneTimeFees = df.format(oneTimeFees),
            incomeQuotaAmortizationBreakPoint = df.format(incomeQuotaRuleBreakpoint),
            taxRebateYearly = df.format(taxRebate),
            loanRatioPercent = df.format(loanRatioPercent),
            minimumDeposit = df.format(minDeposit),
            problems = problems
        )

        _uiState.update {
            it.copy(result = result)
        }
    }
}