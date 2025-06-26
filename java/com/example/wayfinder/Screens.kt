package com.example.wayfinder

import android.Manifest
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.*

@Composable
fun TreasureHuntApp(viewModel: TreasureHuntViewModel = viewModel()) {
    val navController = rememberNavController()

    NavHost(navController = navController, startDestination = "permissions") {
        composable("permissions") {
            PermissionsScreen(onPermissionGranted = { navController.navigate("start") })
        }
        composable("start") {
            StartScreen(onStartClicked = {
                viewModel.startTimer()
                navController.navigate("clue")
            })
        }
        composable("clue") {
            val locationViewModel: LocationViewModel = viewModel()
            val userLocation = locationViewModel.currentLocation.collectAsState().value
            val context = LocalContext.current

            ClueScreen(
                viewModel = viewModel,
                // pass lat long
                currentLat = userLocation?.latitude,
                currentLon = userLocation?.longitude,
                onFoundIt = { isCorrect ->
                    if (isCorrect) {
                        viewModel.pauseTimer()
                        if (viewModel.hasMoreClues()) {
                            navController.navigate("clueSolved")
                        } else {
                            navController.navigate("completed")
                        }
                    } else {
                        Toast.makeText(context, "Not at the correct location!", Toast.LENGTH_SHORT).show()
                    }
                },
                onQuit = { navController.navigate("start") }
            )
        }
        composable("clueSolved") {
            ClueSolvedScreen(viewModel = viewModel, onContinue = {
                viewModel.nextClue()
                viewModel.resumeTimer()
                navController.navigate("clue")
            })
        }
        composable("completed") {
            CompletedScreen(viewModel = viewModel, onHome = { navController.navigate("start") })
        }
    }
}

@Composable
fun PermissionsScreen(onPermissionGranted: () -> Unit) {
    val context = LocalContext.current
    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            onPermissionGranted()
        } else {
            Toast.makeText(context, "Location permission is required.", Toast.LENGTH_SHORT).show()
        }
    }

    // check perms
    val permissionAlreadyGranted = remember {
        androidx.core.content.ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.ACCESS_FINE_LOCATION
        ) == android.content.pm.PackageManager.PERMISSION_GRANTED
    }

    if (permissionAlreadyGranted) {
        // skip if
        onPermissionGranted()
    } else {
        // if not show perm screen
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text("This app needs location permissions.")
            Spacer(modifier = Modifier.height(16.dp))
            Button(onClick = { launcher.launch(Manifest.permission.ACCESS_FINE_LOCATION) }) {
                Text("Grant Permission")
            }
        }
    }
}

@Composable
fun StartScreen(onStartClicked: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text(
            "Treasure Hunt",
            style = MaterialTheme.typography.headlineLarge,
            modifier = Modifier.align(Alignment.CenterHorizontally)
        )
        Spacer(modifier = Modifier.height(16.dp))
        Box(modifier = Modifier.weight(1f)) {
            Column(modifier = Modifier.verticalScroll(rememberScrollState())) {
                Text(
                    "Rules for the game:\n\n" +
                            "1. Please follow the clues to the locations to complete the treasure hunt.\n" +
                            "2. If you need to, click the hint button to get a tip for the provided clue.\n" +
                            "3. When you think you are near the location, tap the 'Found It!' button to see if you are correct.\n" +
                            "4. Keep in mind that Android GPS accuracy is around 5 meters for relative location.\n\n" +
                            "Good luck!"
                )
            }
        }
        Spacer(modifier = Modifier.height(16.dp))
        Button(
            modifier = Modifier.fillMaxWidth(),
            onClick = onStartClicked
        ) {
            Text("Start")
        }
    }
}

@Composable
fun ClueScreen(
    viewModel: TreasureHuntViewModel,
    currentLat: Double?,
    currentLon: Double?,
    onFoundIt: (Boolean) -> Unit,
    onQuit: () -> Unit
) {
    val currentClue = viewModel.getCurrentClue()
    val elapsedTime by viewModel.elapsedTime.collectAsState()
    val context = LocalContext.current

    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        Text("Clue", style = MaterialTheme.typography.headlineMedium)
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = currentClue?.clueText ?: "No clue available",
            style = MaterialTheme.typography.bodyLarge
        )
        Spacer(modifier = Modifier.height(8.dp))
        Button(onClick = {
            val randomHint = currentClue?.hints?.randomOrNull() ?: "No hints available"
            Toast.makeText(context, randomHint, Toast.LENGTH_SHORT).show()
        }) {
            Text("Hint")
        }
        Spacer(modifier = Modifier.height(16.dp))
        Text("Time elapsed: ${elapsedTime}s")

        Spacer(modifier = Modifier.height(16.dp))
        Button(
            modifier = Modifier.fillMaxWidth(),
            onClick = {
                // no check if no location
                val userLat = currentLat ?: 0.0
                val userLon = currentLon ?: 0.0
                val targetLat = currentClue?.latitude ?: 0.0
                val targetLon = currentClue?.longitude ?: 0.0
                val distance = calculateDistance(userLat, userLon, targetLat, targetLon)
                onFoundIt(distance <= 5.0)
            }
        ) {
            Text("Found It!")
        }
        Spacer(modifier = Modifier.height(8.dp))
        Button(
            modifier = Modifier.fillMaxWidth(),
            onClick = onQuit
        ) {
            Text("Quit")
        }
    }
}

@Composable
fun ClueSolvedScreen(viewModel: TreasureHuntViewModel, onContinue: () -> Unit) {
    LaunchedEffect(Unit) { viewModel.pauseTimer() }
    val elapsedTime by viewModel.elapsedTime.collectAsState()
    val currentClue = viewModel.getCurrentClue()

    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        Text("Clue Solved!", style = MaterialTheme.typography.headlineMedium)
        Spacer(modifier = Modifier.height(16.dp))
        Text("Time elapsed: ${elapsedTime}s")
        Spacer(modifier = Modifier.height(16.dp))
        Text("Location Info: ${currentClue?.locationInfo ?: "N/A"}")
        Spacer(modifier = Modifier.height(16.dp))
        Button(
            modifier = Modifier.fillMaxWidth(),
            onClick = onContinue
        ) {
            Text("Continue")
        }
    }
}

@Composable
fun CompletedScreen(viewModel: TreasureHuntViewModel, onHome: () -> Unit) {
    val elapsedTime by viewModel.elapsedTime.collectAsState()
    val currentClue = viewModel.getCurrentClue()
    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        Text("Treasure Hunt Completed!", style = MaterialTheme.typography.headlineMedium)
        Spacer(modifier = Modifier.height(16.dp))
        Text("Congratulations on finding all clues in the treasure hunt!")
        Spacer(modifier = Modifier.height(16.dp))
        Text("You completed the treasure hunt in: ${elapsedTime}s")
        Spacer(modifier = Modifier.height(16.dp))
        Text("Final Location Info: ${currentClue?.locationInfo ?: "N/A"}")
        Spacer(modifier = Modifier.height(16.dp))
        Button(
            modifier = Modifier.fillMaxWidth(),
            onClick = onHome
        ) {
            Text("Home")
        }
    }
}
