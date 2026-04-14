package br.com.ibk.check


import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatDelegate
import br.com.ibk.check.ui.screens.MainScreen
import br.com.ibk.check.ui.theme.IBKCheckTheme
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        installSplashScreen()
        super.onCreate(savedInstanceState)
        setContent {
            IBKCheckTheme {
                // Aqui chamamos a tela que você criou
                MainScreen()
            }
        }
    }
}