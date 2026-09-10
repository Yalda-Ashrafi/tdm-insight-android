package my.edu.aiu.app.tdm_insight_android.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import my.edu.aiu.app.tdm_insight_android.ui.components.NextBackButtons
import my.edu.aiu.app.tdm_insight_android.ui.theme.AquaBlue
import my.edu.aiu.app.tdm_insight_android.ui.theme.DeepTeal
import my.edu.aiu.app.tdm_insight_android.ui.theme.SoftPink
import my.edu.aiu.app.tdm_insight_android.viewmodel.PatientViewModel

@Composable
fun ReviewInputsScreen(
    viewModel: PatientViewModel,
    onConfirmClick: () -> Unit,
    onBackClick: () -> Unit
) {
    val scrollState = rememberScrollState()

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.linearGradient(
                    colors = listOf(SoftPink, AquaBlue)
                )
            ),
        contentAlignment = Alignment.TopCenter
    ) {
        Card(
            shape = RoundedCornerShape(28.dp),
            elevation = CardDefaults.cardElevation(defaultElevation = 12.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White.copy(alpha = 0.98f)),
            modifier = Modifier.padding(16.dp).fillMaxWidth()
        ) {
            Column(
                modifier = Modifier
                    .padding(24.dp)
                    .verticalScroll(scrollState)
            ) {
                Text(
                    text = "Review Patient Inputs",
                    style = MaterialTheme.typography.titleLarge.copy(
                        color = DeepTeal,
                        fontWeight = FontWeight.Bold
                    )
                )
                
                Spacer(modifier = Modifier.height(16.dp))
                
                SectionHeader("Patient Details")
                ReviewRow("Case Name", viewModel.caseName)
                ReviewRow("Age", "${viewModel.age} years")
                ReviewRow("Weight", "${viewModel.weight} kg")
                ReviewRow("Height", "${viewModel.height} cm")
                ReviewRow("Gender", if (viewModel.isMale) "Male" else "Female")
                ReviewRow("SCr", "${viewModel.scr} mg/dL")
                
                Spacer(modifier = Modifier.height(16.dp))
                
                SectionHeader("Therapy Plan")
                ReviewRow("Workflow", viewModel.selectedWorkflow.title)
                ReviewRow("Dose", "${viewModel.dose} mg")
                ReviewRow("Interval", "${viewModel.interval} h")
                ReviewRow("Infusion", "${viewModel.infusion} h")

                // Review items in clinical order: Pre then Post
                if (viewModel.selectedWorkflow.needsPre) {
                    ReviewRow("Pre-dose Level", "${viewModel.preConc} mg/L")
                    ReviewRow("Pre-dose Time", "${viewModel.preTime} h")
                }
                if (viewModel.selectedWorkflow.needsPost) {
                    ReviewRow("Post-dose Level", "${viewModel.postConc} mg/L")
                    ReviewRow("Post-dose Time", "${viewModel.postTime} h")
                }
                
                // Validation Errors Display
                val validation = viewModel.validationOutcome
                if (validation != null && !validation.isValid) {
                    Spacer(modifier = Modifier.height(24.dp))
                    Card(
                        colors = CardDefaults.cardColors(containerColor = Color(0xFFFFEBEE)),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Text("Please correct the following errors:", color = Color.Red, fontWeight = FontWeight.Bold)
                            validation.errors.forEach { error ->
                                Text("- ${error.message}", color = Color.Red, fontSize = 13.sp)
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(32.dp))
                
                NextBackButtons(
                    onBack = onBackClick,
                    onNext = {
                        if (viewModel.performValidation()) {
                            viewModel.performCalculation()
                            onConfirmClick()
                        }
                    },
                    nextText = "Confirm and Calculate"
                )
            }
        }
    }
}

@Composable
fun SectionHeader(title: String) {
    Text(
        text = title,
        style = MaterialTheme.typography.titleSmall,
        color = DeepTeal,
        modifier = Modifier.padding(vertical = 8.dp)
    )
    HorizontalDivider(modifier = Modifier.padding(bottom = 8.dp))
}

@Composable
fun ReviewRow(label: String, value: String) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(text = label, color = Color.Gray)
        Text(text = value.ifBlank { "-" }, fontWeight = FontWeight.SemiBold)
    }
}
