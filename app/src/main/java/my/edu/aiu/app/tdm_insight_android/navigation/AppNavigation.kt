package my.edu.aiu.app.tdm_insight_android.navigation

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import kotlinx.coroutines.launch
import my.edu.aiu.app.tdm_insight_android.ui.screens.*
import my.edu.aiu.app.tdm_insight_android.ui.theme.DeepTeal
import my.edu.aiu.app.tdm_insight_android.viewmodel.PatientViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppNavigation() {
    val navController = rememberNavController()
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()
    val navBackStackEntry = navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry.value?.destination?.route
    
    var showSplash by remember { mutableStateOf(value = true) }
    
    // Shared ViewModel for clinical data persistence
    val patientViewModel: PatientViewModel = viewModel()

    if (showSplash) {
        SplashScreen(onTimeout = { showSplash = false })
    } else {
        ModalNavigationDrawer(
            drawerState = drawerState,
            drawerContent = {
                ModalDrawerSheet {
                    Text(
                        "TDM Insight", 
                        modifier = Modifier.padding(16.dp),
                        color = DeepTeal
                    )
                    HorizontalDivider()
                    
                    NavigationDrawerItem(
                        label = { Text("Home") },
                        selected = currentRoute == "welcome",
                        onClick = {
                            scope.launch { drawerState.close() }
                            navController.navigate("welcome") {
                                popUpTo("welcome") { inclusive = true }
                            }
                        }
                    )
                    NavigationDrawerItem(
                        label = { Text("Patient Case") },
                        selected = currentRoute == "patientCase",
                        onClick = {
                            scope.launch { drawerState.close() }
                            navController.navigate("patientCase")
                        }
                    )
                    NavigationDrawerItem(
                        label = { Text("Workflow") },
                        selected = currentRoute == "workflow",
                        onClick = {
                            scope.launch { drawerState.close() }
                            navController.navigate("workflow")
                        }
                    )
                    NavigationDrawerItem(
                        label = { Text("About") },
                        selected = currentRoute == "about",
                        onClick = {
                            scope.launch { drawerState.close() }
                            navController.navigate("about")
                        }
                    )
                }
            }
        ) {
            Scaffold(
                topBar = {
                    TopAppBar(
                        title = { Text("TDM Insight") },
                        navigationIcon = {
                            IconButton(onClick = { scope.launch { drawerState.open() } }) {
                                Icon(imageVector = Icons.Default.Menu, contentDescription = "Menu")
                            }
                        },
                        colors = TopAppBarDefaults.topAppBarColors(
                            containerColor = DeepTeal,
                            titleContentColor = Color.White
                        )
                    )
                }
            ) { innerPadding ->
                Box(modifier = Modifier.padding(innerPadding)) {
                    NavHost(navController, startDestination = "welcome") {
                        composable("welcome") { 
                            WelcomeScreen(onStartClick = { navController.navigate("workflow") }) 
                        }
                        composable("workflow") { 
                            WorkflowSelectionScreen(
                                onPreDoseClick = { 
                                    patientViewModel.selectedWorkflow = my.edu.aiu.app.tdm_insight_android.model.WorkflowType.PRE
                                    navController.navigate("patientCase") 
                                },
                                onPostDoseClick = {
                                    patientViewModel.selectedWorkflow = my.edu.aiu.app.tdm_insight_android.model.WorkflowType.POST
                                    navController.navigate("patientCase")
                                },
                                onPrePostClick = { 
                                    patientViewModel.selectedWorkflow = my.edu.aiu.app.tdm_insight_android.model.WorkflowType.PRE_POST
                                    navController.navigate("patientCase") 
                                },
                                onBackClick = { navController.popBackStack() }
                            ) 
                        }
                        composable("patientCase") { 
                            PatientCaseScreen(
                                viewModel = patientViewModel,
                                onSaveClick = { navController.navigate("input") },
                                onBackClick = { navController.popBackStack() },
                                onCameraClick = { navController.navigate("camera") }
                            ) 
                        }
                        composable("camera") {
                            CameraPreviewScreen(
                                onImageCaptured = { uri ->
                                    patientViewModel.photoUri = uri
                                    navController.popBackStack()
                                },
                                onBack = { navController.popBackStack() }
                            )
                        }
                        composable("input") { 
                            InputFormScreen(
                                viewModel = patientViewModel,
                                onReviewClick = { navController.navigate("review") },
                                onBackClick = { navController.popBackStack() }
                            ) 
                        }
                        composable("review") { 
                            ReviewInputsScreen(
                                viewModel = patientViewModel,
                                onConfirmClick = { navController.navigate("results") },
                                onBackClick = { navController.popBackStack() }
                            ) 
                        }
                        composable("results") { 
                            ResultsScreen(
                                viewModel = patientViewModel,
                                onViewExplanationClick = { navController.navigate("explanation") },
                                onBackClick = { navController.popBackStack() }
                            ) 
                        }
                        composable("explanation") { 
                            ExplanationScreen(
                                viewModel = patientViewModel,
                                onBackClick = { navController.popBackStack() }
                            ) 
                        }
                        composable("about") { 
                            AboutScreen(onBackClick = { navController.popBackStack() }) 
                        }
                    }
                }
            }
        }
    }
}
