package se.ade.housingcosts

sealed class CalcUiEvent {
    data class EditPurchasePrice(val value: String): CalcUiEvent()
    data class EditSalePrice(val value: String): CalcUiEvent()
    data class EditLoanRemaining(val value: String): CalcUiEvent()
    data class EditDeposit(val value: String): CalcUiEvent()
    data class EditIncomePreTax(val value: String): CalcUiEvent()
    data class EditIncomeAfterTax(val value: String): CalcUiEvent()
    data class ChangeInterestRate(val value: Float) : CalcUiEvent()
    data class EditObjectPrice(val value: String) : CalcUiEvent()
    data class EditPantbrevSum(val value: String) : CalcUiEvent()
}