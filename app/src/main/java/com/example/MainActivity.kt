package com.example

import android.content.Context
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.data.AppDatabase
import com.example.data.WaterRepository
import com.example.ui.HeaderSection
import com.example.ui.WaterTrackerScreen
import com.example.ui.WaterViewModel
import com.example.ui.WaterViewModelFactory
import com.example.ui.theme.AppThemeMode
import com.example.ui.theme.AppThemeStyle
import com.example.ui.theme.MyApplicationTheme
import com.example.utils.NotificationHelper

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        // Initialize SharedPreferences, Room Local Database, DAO, and Repository
        val sharedPreferences = getSharedPreferences("hydro_prefs", Context.MODE_PRIVATE)
        val database = AppDatabase.getDatabase(this)
        val repository = WaterRepository(database.waterLogDao())
        val notificationHelper = NotificationHelper(applicationContext)
        val viewModelFactory = WaterViewModelFactory(repository, notificationHelper, sharedPreferences)

        setContent {
            // Get ViewModel using the factory first to read the theme states
            val viewModel: WaterViewModel = viewModel(factory = viewModelFactory)
            val themeStyle by viewModel.themeStyle.collectAsState()
            val themeMode by viewModel.themeMode.collectAsState()

            MyApplicationTheme(themeStyle = themeStyle, themeMode = themeMode) {
                // Dynamic permission request for POST_NOTIFICATIONS on Android 13+ (Tiramisu)
                val permissionLauncher = rememberLauncherForActivityResult(
                    contract = ActivityResultContracts.RequestPermission()
                ) { isGranted ->
                    // Permission status changed
                }

                LaunchedEffect(Unit) {
                    if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.TIRAMISU) {
                        permissionLauncher.launch(android.Manifest.permission.POST_NOTIFICATIONS)
                    }
                }

                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    // Render the WaterTrackerScreen
                    WaterTrackerScreen(
                        viewModel = viewModel,
                        modifier = Modifier.padding(innerPadding)
                    )
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun HeaderSectionPreview() {
    MyApplicationTheme(themeStyle = AppThemeStyle.TURQUOISE, themeMode = AppThemeMode.SYSTEM) {
        HeaderSection(onReset = {}, onThemeClick = {})
    }
}