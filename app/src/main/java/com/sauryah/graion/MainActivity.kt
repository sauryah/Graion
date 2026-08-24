package com.sauryah.graion

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
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.viewmodel.compose.viewModel
import com.sauryah.graion.domain.engine.python.PythonEngine
import com.sauryah.graion.theme.GraionTheme
import com.sauryah.graion.ui.calculator.CalculatorViewModel
import com.sauryah.graion.ui.calculator.CalculatorViewModelFactory
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        lifecycleScope.launch(Dispatchers.IO) {
            PythonEngine.init(applicationContext)
        }

        setContent {
            val viewModel: CalculatorViewModel = viewModel(
                factory = CalculatorViewModelFactory(applicationContext)
            )
            val preferences by viewModel.userPreferences.collectAsStateWithLifecycle()

            GraionTheme(themeMode = preferences.themeMode) {
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
