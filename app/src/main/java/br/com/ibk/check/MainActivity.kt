package br.com.ibk.check


import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import br.com.ibk.check.ui.screens.MainScreen
import br.com.ibk.check.ui.theme.IBKCheckTheme
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen()
        super.onCreate(savedInstanceState)
        setContent {
            // Forçamos o Light Mode diretamente no Tema do Compose
            IBKCheckTheme(darkTheme = false, dynamicColor = false) {
                MainScreen()
            }
        }
    }
}