package br.com.ibk.check


import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import br.com.ibk.check.ui.screens.MainScreen
import br.com.ibk.check.ui.theme.IBKCheckTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            IBKCheckTheme {
                // Aqui chamamos a tela que você criou
                MainScreen()
            }
        }
    }
}