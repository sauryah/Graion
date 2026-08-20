package com.sauryah.lihas.calculator

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.sauryah.lihas.calculator.theme.LihasCalculatorTheme
import com.sauryah.lihas.calculator.ui.calculator.CalculatorViewModel
import com.sauryah.lihas.calculator.ui.calculator.CalculatorViewModelFactory

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            val viewModel: CalculatorViewModel = viewModel(
                factory = CalculatorViewModelFactory(applicationContext)
            )
            val preferences by viewModel.userPreferences.collectAsStateWithLifecycle()

            LihasCalculatorTheme(themeMode = preferences.themeMode) {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    MainNavigation(viewModel = viewModel)
                }
            }
        }
    }
}
