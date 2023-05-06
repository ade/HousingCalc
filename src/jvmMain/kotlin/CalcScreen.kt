import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import se.ade.housingcosts.CalcUiEvent
import se.ade.housingcosts.CalcUiState
import java.math.RoundingMode
import java.text.DecimalFormat
import kotlin.math.roundToInt

val COLUMN_WIDTH = 400.dp

@Composable
fun CalcScreen(state: CalcUiState, eventSink: (event: CalcUiEvent) -> Unit) {
    Column(modifier = Modifier.padding(16.dp)) {
        PreviousPlace(state, eventSink)
        Spacer(modifier = Modifier.height(16.dp))
        Economy(state, eventSink)
        Spacer(modifier = Modifier.height(16.dp))
        ObjectInfo(state, eventSink)
        Spacer(modifier = Modifier.height(16.dp))
        Results(state, eventSink)
    }
}

@Composable
fun Economy(state: CalcUiState, eventSink: (event: CalcUiEvent) -> Unit) {
    val context = LocalContext.current

    Column {
        Text(context.strings["economy"], style = MaterialTheme.typography.h5)
        Spacer(modifier = Modifier.height(16.dp))

        Row {
            Column(modifier = Modifier.weight(1f)) {
                Textual("deposit", state.input.extraDepositText) {
                    eventSink(CalcUiEvent.EditDeposit(it))
                }
                Spacer(modifier = Modifier.height(16.dp))
                Textual("household_income_before_tax", state.input.householdIncomePreTax) {
                    eventSink(CalcUiEvent.EditIncomePreTax(it))
                }
                Spacer(modifier = Modifier.height(16.dp))
                Textual("household_income_after_tax", state.input.householdIncomeAfterTax) {
                    eventSink(CalcUiEvent.EditIncomeAfterTax(it))
                }
                Spacer(modifier = Modifier.height(16.dp))
                InterestRate(state, eventSink)
            }
            Spacer(modifier = Modifier.width(16.dp))
            Column(modifier = Modifier.weight(1f)) {
                Card {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(context.strings["total_deposit"] + ": " + state.result.totalDepositText)
                        Text(context.strings["income_quota_breakpoint"] + ": " + state.result.incomeQuotaAmortizationBreakPoint)
                    }
                }
            }
        }
    }
}

@Composable
fun PreviousPlace(state: CalcUiState, eventSink: (event: CalcUiEvent) -> Unit) {
    val context = LocalContext.current

    Column {
        Text(context.strings["previous_residence"], style = MaterialTheme.typography.h5)
        Spacer(modifier = Modifier.height(16.dp))

        Row() {
            Column(modifier = Modifier.weight(1f)) {
                Textual("purchase_price", state.input.purchasePriceText) {
                    eventSink(CalcUiEvent.EditPurchasePrice(it))
                }
                Spacer(modifier = Modifier.height(16.dp))
                Textual("sale_price", state.input.salePriceText) {
                    eventSink(CalcUiEvent.EditSalePrice(it))
                }
                Spacer(modifier = Modifier.height(16.dp))
                Textual("loan_remaining", state.input.loansRemainingText) {
                    eventSink(CalcUiEvent.EditLoanRemaining(it))
                }
            }
            Spacer(modifier = Modifier.width(16.dp))

            Column(modifier = Modifier.weight(1f)) {
                Card {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(context.strings["profit"] + ": " + state.result.profitText)
                        Text(context.strings["tax"] + ": " + state.result.salesTaxText)
                        Text(context.strings["capital_after_taxes"] + ": " + state.result.carryOverAfterTax)
                    }
                }
            }
        }
    }

}

@Composable
fun ObjectInfo(state: CalcUiState, eventSink: (event: CalcUiEvent) -> Unit) {
    val context = LocalContext.current

    Column {
        Text(context.strings["object"], style = MaterialTheme.typography.h5)
        Spacer(modifier = Modifier.height(16.dp))

        Row() {
            Column(modifier = Modifier.weight(1f)) {
                Textual("purchase_price", state.input.objectPriceText) {
                    eventSink(CalcUiEvent.EditObjectPrice(it))
                }
                Spacer(modifier = Modifier.height(16.dp))
                Textual("pantbrev_sum", state.input.objectPantbrev) {
                    eventSink(CalcUiEvent.EditPantbrevSum(it))
                }
            }
        }
    }
}

@Composable
fun Results(state: CalcUiState, eventSink: (event: CalcUiEvent) -> Unit) {
    val context = LocalContext.current

    Column {
        Text(context.strings["results"], style = MaterialTheme.typography.h5)
        Spacer(modifier = Modifier.height(16.dp))

        Row() {
            Column(modifier = Modifier.weight(1f)) {
                Card {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(context.strings["loan_amount"] + ": " + state.result.loanAmountText)
                        Text(context.strings["interest_sum_yearly"] + ": " + state.result.yearlyInterest)
                        Text(context.strings["monthly_payment_interest"] + ": " + state.result.monthlyPaymentInterest)
                        Text(context.strings["tax_rebate_yearly"] + ": " + state.result.taxRebateYearly)
                        Text(context.strings["loan_quota_marketvalue_amortization"] + ": " + state.result.loanQuotaMarketvalueAmortizationMonthly)
                        Text(context.strings["loan_quota_income_amortization"] + ": " + state.result.loanQuotaIncomeAmortizationMonthly)
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(context.strings["total_monthly_payment"] + ": " + state.result.totalMonthlyPayment)
                        Text(context.strings["left_to_live_on"] + ": " + state.result.leftToLiveOn)
                    }
                }
            }
            Spacer(modifier = Modifier.width(16.dp))
            Column(modifier = Modifier.weight(1f)) {
                Card {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(context.strings["lagfart_fee"] + ": " + state.result.lagfart)
                        Text(context.strings["pantbrev_fee"] + ": " + state.result.pantbrevFee)
                        Text(context.strings["one_time_fees_total"] + ": " + state.result.oneTimeFees)
                    }
                }
            }
        }
    }
}

@Composable
fun Textual(label: String, value: String, onChange: (String) -> Unit) {
    val context = LocalContext.current
    val labelText: String = context.strings[label] as? String ?: ""
    Card(modifier = Modifier.fillMaxWidth()) {
        Box(modifier = Modifier.padding(8.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(text = labelText)
                Spacer(modifier = Modifier.weight(1f))
                TextField(modifier = Modifier.width(120.dp), value = value, onValueChange = { onChange(it) })
            }
        }
    }
}

@Composable
fun InterestRate(state: CalcUiState, eventSink: (event: CalcUiEvent) -> Unit) {
    val context = LocalContext.current
    val labelText: String = context.strings["interest_rate"] as? String ?: ""

    val displayValue = (state.input.interestRate / 0.25f).roundToInt() * 0.25

    val df = DecimalFormat("#.##")
    df.roundingMode = RoundingMode.HALF_UP
    val text = df.format(displayValue)

    Card {
        Box(modifier = Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(text = labelText)
                    Slider(
                        //modifier = Modifier.weight(1f),
                        value = state.input.interestRate,
                        valueRange = 0f..10f,
                        onValueChange = {
                            eventSink(CalcUiEvent.ChangeInterestRate(it))
                        },
                        onValueChangeFinished = {
                            val finalValue = (state.input.interestRate / 0.25f).roundToInt() * 0.25
                            eventSink(CalcUiEvent.ChangeInterestRate(finalValue.toFloat()))
                        }
                    )
                }
                Column() {
                    TextField(modifier = Modifier.width(120.dp), value = text, onValueChange = { })
                }
            }
        }
    }
}