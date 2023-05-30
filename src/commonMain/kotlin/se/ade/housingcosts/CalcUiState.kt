package se.ade.housingcosts

import kotlinx.serialization.Serializable

data class CalcUiState(
    val input: CalcInputUiState,
    val result: CalcResultUiState
)

@Serializable
data class CalcInputUiState(
    val purchasePriceText: String,
    val salePriceText: String,
    val deferSalesTax: Boolean,
    val loansRemainingText: String,
    val extraDepositText: String,
    val objectPriceText: String,
    val householdIncomePreTax: String,
    val householdIncomeAfterTax: String,
    val interestRate: Float,
    val objectPantbrev: String
)

data class CalcResultUiState(
    val profitText: String = "",
    val salesTaxText: String = "",
    val carryOverAfterTax: String = "",
    val totalDepositText: String = "",
    val loanAmountText: String = "",
    val yearlyInterest: String = "",
    val monthlyPaymentInterest: String = "",
    val loanQuotaMarketvalueAmortizationMonthly: String = "",
    val loanQuotaIncomeAmortizationMonthly: String = "",
    val totalMonthlyPayment: String = "",
    val leftToLiveOn: String = "",
    val lagfart: String = "",
    val pantbrevFee: String = "",
    val oneTimeFees: String = "",
    val incomeQuotaAmortizationBreakPoint: String = "",
    val taxRebateYearly: String = "",
    val loanRatioPercent: String = "",
    val minimumDeposit: String = "",
    val problems: List<CalcUiProblems> = listOf()
)