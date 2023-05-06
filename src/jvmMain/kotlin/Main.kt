import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.WindowPosition
import androidx.compose.ui.window.application
import androidx.compose.ui.window.rememberWindowState
import se.ade.housingcosts.CalcViewModel

val LocalContext = compositionLocalOf<AppContext> { throw IllegalStateException("Not initialized") }

@Composable
@Preview
fun App() {
    val vm by remember { mutableStateOf(CalcViewModel()) }
    val uiState = vm.uiState.collectAsState()
    val eventSink = vm::onEvent

    MaterialTheme {
        CalcScreen(uiState.value, eventSink)
    }
}

fun main() = application {
    Window(
        onCloseRequest = ::exitApplication,
        state = rememberWindowState(
            position = WindowPosition(Alignment.Center),
            size = DpSize(1000.dp, 1400.dp)
        )) {

        CompositionLocalProvider(LocalContext provides AppContext()) {
            App()
        }
    }
}
