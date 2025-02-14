package ro.go.stecker.hideandseek

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import ro.go.stecker.hideandseek.ui.theme.HideAndSeekTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            HideAndSeekTheme {
                HideAndSeekApp()
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun HideAndSeekPreview() {
    HideAndSeekTheme {
        HideAndSeekApp()
    }
}