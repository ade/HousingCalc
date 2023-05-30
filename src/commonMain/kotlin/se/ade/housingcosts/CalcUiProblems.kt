package se.ade.housingcosts

sealed interface CalcUiProblems {
    object LoanMoreThan85Percent: CalcUiProblems
}