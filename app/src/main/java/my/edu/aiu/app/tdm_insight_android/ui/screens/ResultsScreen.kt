package my.edu.aiu.app.tdm_insight_android.ui.screens

/**
 * OWNER: Elyas
 */

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.PictureAsPdf
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import my.edu.aiu.app.tdm_insight_android.engine.CalculationOutcome
import my.edu.aiu.app.tdm_insight_android.features.export.PdfExporter
import my.edu.aiu.app.tdm_insight_android.features.graph.ConcentrationGraph
import my.edu.aiu.app.tdm_insight_android.model.TdmResult
import my.edu.aiu.app.tdm_insight_android.model.WorkflowType
import my.edu.aiu.app.tdm_insight_android.ui.components.InputField
import my.edu.aiu.app.tdm_insight_android.ui.components.NextBackButtons
import my.edu.aiu.app.tdm_insight_android.ui.theme.AquaBlue
import my.edu.aiu.app.tdm_insight_android.ui.theme.CharcoalGray
import my.edu.aiu.app.tdm_insight_android.ui.theme.DeepTeal
import my.edu.aiu.app.tdm_insight_android.ui.theme.EmeraldGreen
import my.edu.aiu.app.tdm_insight_android.ui.theme.SoftMint
import my.edu.aiu.app.tdm_insight_android.ui.theme.SoftPink
import my.edu.aiu.app.tdm_insight_android.viewmodel.PatientViewModel
import java.util.Locale
import kotlin.math.exp

@Composable
fun ResultsScreen(
    viewModel: PatientViewModel,
    onViewExplanationClick: () -> Unit,
    onBackClick: () -> Unit
) {
    val context = LocalContext.current
    val visible = remember { mutableStateOf(value = true) }
    val scrollState = rememberScrollState()
    
    val outcome = viewModel.calculationResult

    // Requirement 10: Accurate PK simulation for infusions
    fun updateSimulation() {
        val doseVal = viewModel.plannedDose.toDoubleOrNull() ?: 0.0
        val tauVal = viewModel.plannedInterval.toDoubleOrNull() ?: 12.0
        val tInfVal = viewModel.plannedInfusion.toDoubleOrNull() ?: 1.0
        
        val ke = if (outcome is CalculationOutcome.Success) outcome.result.kePerHour else 0.1
        val vd = if (outcome is CalculationOutcome.Success) outcome.result.volumeOfDistributionL else 50.0

        if (tauVal > tInfVal && ke > 0 && vd > 0) {
            val r = doseVal / tInfVal // Infusion rate mg/h
            viewModel.expectedCmax = (r / (ke * vd)) * (1 - exp(-ke * tInfVal)) / (1 - exp(-ke * tauVal))
            viewModel.expectedCmin = viewModel.expectedCmax * exp(-ke * (tauVal - tInfVal))
        }
    }
    
    LaunchedEffect(outcome) {
        if (outcome is CalculationOutcome.Success) {
            viewModel.plannedDose = viewModel.dose
            viewModel.plannedInterval = viewModel.interval
            viewModel.plannedInfusion = viewModel.infusion
            updateSimulation()
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Brush.linearGradient(colors = listOf(SoftPink, AquaBlue))),
        contentAlignment = Alignment.TopCenter
    ) {
        if (outcome is CalculationOutcome.Success) {
            val result = outcome.result
            
            AnimatedVisibility(
                visible = visible.value,
                enter = fadeIn(animationSpec = tween(800)) + slideInVertically(initialOffsetY = { 40 })
            ) {
                Card(
                    shape = RoundedCornerShape(28.dp),
                    elevation = CardDefaults.cardElevation(defaultElevation = 14.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White.copy(alpha = 0.98f)),
                    modifier = Modifier.padding(16.dp).fillMaxWidth()
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.padding(24.dp).verticalScroll(scrollState)
                    ) {
                        Text(
                            text = result.workflow.title,
                            style = MaterialTheme.typography.titleLarge.copy(color = DeepTeal, fontWeight = FontWeight.Bold, fontSize = 24.sp)
                        )
                        Text(text = "Calculation Results", style = MaterialTheme.typography.bodySmall, color = Color.Gray)
                        
                        // SOURCE BADGE
                        Spacer(modifier = Modifier.height(8.dp))
                        val isEstimated = result.workflow != WorkflowType.PRE_POST
                        SuggestionChip(
                            onClick = { },
                            label = { Text(text = if (isEstimated) "Ke: ESTIMATED" else "Ke: MEASURED", fontSize = 10.sp, fontWeight = FontWeight.Bold) },
                            colors = SuggestionChipDefaults.suggestionChipColors(
                                containerColor = if (isEstimated) Color(0xFFFFF3E0) else Color(0xFFE8F5E9),
                                labelColor = if (isEstimated) Color(0xFFE65100) else Color(0xFF2E7D32)
                            )
                        )

                        Spacer(modifier = Modifier.height(20.dp))
                        
                        Card(colors = CardDefaults.cardColors(containerColor = DeepTeal.copy(alpha = 0.05f)), shape = RoundedCornerShape(16.dp), modifier = Modifier.fillMaxWidth()) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                ResultRow("Elimination Rate (Ke)", String.format(Locale.US, "%.4f h⁻¹", result.kePerHour))
                                ResultRow("Half-life", String.format(Locale.US, "%.1f hours", result.halfLifeHours))
                                ResultRow("Vd", String.format(Locale.US, "%.1f L", result.volumeOfDistributionL))
                                ResultRow("Clearance", String.format(Locale.US, "%.2f L/hr", result.clearanceLPerHour))
                                ResultRow("AUC24", String.format(Locale.US, "%.0f mg·hr/L", result.auc24))
                            }
                        }

                        if (result.notices.isNotEmpty()) {
                            Spacer(modifier = Modifier.height(16.dp))
                            Card(colors = CardDefaults.cardColors(containerColor = Color(0xFFE3F2FD)), modifier = Modifier.fillMaxWidth()) {
                                Column(modifier = Modifier.padding(12.dp)) {
                                    result.notices.forEach { notice ->
                                        Row(verticalAlignment = Alignment.CenterVertically) {
                                            Icon(Icons.Default.Info, null, tint = Color(0xFF1976D2), modifier = Modifier.size(18.dp))
                                            Spacer(modifier = Modifier.width(8.dp))
                                            Text(text = notice.message, color = Color(0xFF1565C0), fontSize = 12.sp)
                                        }
                                    }
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(24.dp))
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                            Button(onClick = { PdfExporter.downloadPdf(context, viewModel) }, colors = ButtonDefaults.buttonColors(containerColor = DeepTeal), modifier = Modifier.weight(1f), shape = RoundedCornerShape(12.dp)) {
                                Icon(Icons.Default.PictureAsPdf, null, modifier = Modifier.size(20.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("Download", fontSize = 14.sp)
                            }
                            Button(onClick = { PdfExporter.sharePdf(context, viewModel) }, colors = ButtonDefaults.buttonColors(containerColor = EmeraldGreen), modifier = Modifier.weight(1f), shape = RoundedCornerShape(12.dp)) {
                                Icon(Icons.Default.Share, null, modifier = Modifier.size(20.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("Share", fontSize = 14.sp)
                            }
                        }

                        Spacer(modifier = Modifier.height(32.dp))
                        Text(text = "Dynamic Simulation Curve", style = MaterialTheme.typography.titleSmall, color = CharcoalGray.copy(alpha = 0.6f), fontWeight = FontWeight.Bold)
                        Spacer(modifier = Modifier.height(12.dp))
                        
                        ConcentrationGraph(
                            result = TdmResult(
                                workflow = result.workflow,
                                kePerHour = result.kePerHour,
                                halfLifeHours = result.halfLifeHours,
                                volumeOfDistributionL = result.volumeOfDistributionL,
                                clearanceLPerHour = result.clearanceLPerHour,
                                peakAtEndOfInfusion = viewModel.expectedCmax,
                                troughBeforeNextDose = viewModel.expectedCmin,
                                auc24 = result.auc24,
                                dailyDoseMg = result.dailyDoseMg,
                                creatinineClearanceMlMin = result.creatinineClearanceMlMin,
                                keSource = result.keSource,
                                steps = emptyList()
                            ),
                            intervalHours = viewModel.plannedInterval.toDoubleOrNull() ?: 12.0,
                            infusionHours = viewModel.plannedInfusion.toDoubleOrNull() ?: 1.0,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(180.dp)
                                .background(Color(0xFFFBFBFB), RoundedCornerShape(16.dp))
                                .padding(12.dp)
                        )
                        
                        HorizontalDivider(modifier = Modifier.padding(vertical = 32.dp), color = Color.LightGray.copy(alpha = 0.5f))
                        Text(text = "Dose & Shape Simulator", style = MaterialTheme.typography.titleMedium, color = EmeraldGreen, fontWeight = FontWeight.Bold)
                        Spacer(modifier = Modifier.height(16.dp))
                        
                        InputField(label = "New Dose (mg)", value = viewModel.plannedDose, onValueChange = { viewModel.plannedDose = it; updateSimulation() }, isNumeric = true, placeholder = "e.g. 1250", isRequired = true)
                        Spacer(modifier = Modifier.height(12.dp))
                        InputField(label = "New Interval (h)", value = viewModel.plannedInterval, onValueChange = { viewModel.plannedInterval = it; updateSimulation() }, isNumeric = true, placeholder = "e.g. 12, 24", isRequired = true)
                        Spacer(modifier = Modifier.height(12.dp))
                        InputField(label = "New Infusion Time (h)", value = viewModel.plannedInfusion, onValueChange = { viewModel.plannedInfusion = it; updateSimulation() }, isNumeric = true, placeholder = "e.g. 1.0 - 5.0", isRequired = true)
                        
                        Spacer(modifier = Modifier.height(32.dp))
                        Card(colors = CardDefaults.cardColors(containerColor = Color(0xFFF9F9F9)), shape = RoundedCornerShape(12.dp), modifier = Modifier.fillMaxWidth()) {
                            Row(modifier = Modifier.fillMaxWidth().padding(16.dp), horizontalArrangement = Arrangement.SpaceAround, verticalAlignment = Alignment.CenterVertically) {
                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    Text("Simulated Peak", fontSize = 11.sp, color = Color.Gray)
                                    Text(text = String.format(Locale.US, "%.1f mg/L", viewModel.expectedCmax), fontWeight = FontWeight.ExtraBold, color = DeepTeal, fontSize = 18.sp)
                                }
                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    Text("Simulated Trough", fontSize = 11.sp, color = Color.Gray)
                                    Text(text = String.format(Locale.US, "%.1f mg/L", viewModel.expectedCmin), fontWeight = FontWeight.ExtraBold, color = EmeraldGreen, fontSize = 18.sp)
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(48.dp))
                        NextBackButtons(onBack = onBackClick, onNext = onViewExplanationClick, nextText = "View Explanation")
                    }
                }
            }
        } else if (outcome is CalculationOutcome.Failure) {
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text(text = "Calculation Error: ${outcome.issues.firstOrNull()?.message}", color = Color.Red)
            }
        }
    }
}

@Composable
fun ResultRow(label: String, value: String) {
    Row(modifier = Modifier.fillMaxWidth().padding(vertical = 10.dp), horizontalArrangement = Arrangement.SpaceBetween) {
        Text(text = label, color = CharcoalGray.copy(alpha = 0.7f), fontSize = 15.sp, fontWeight = FontWeight.Medium)
        Text(text = value, fontWeight = FontWeight.Bold, color = CharcoalGray, fontSize = 17.sp)
    }
}
