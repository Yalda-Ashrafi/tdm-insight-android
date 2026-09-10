package my.edu.aiu.app.tdm_insight_android.ui.screens

/**
 * OWNER: Elyas
 */

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import my.edu.aiu.app.tdm_insight_android.ui.components.InputField
import my.edu.aiu.app.tdm_insight_android.ui.components.NextBackButtons
import my.edu.aiu.app.tdm_insight_android.ui.theme.AquaBlue
import my.edu.aiu.app.tdm_insight_android.ui.theme.DeepTeal
import my.edu.aiu.app.tdm_insight_android.ui.theme.SoftPink
import my.edu.aiu.app.tdm_insight_android.viewmodel.PatientViewModel

@Composable
fun InputFormScreen(
    viewModel: PatientViewModel,
    onReviewClick: () -> Unit,
    onBackClick: () -> Unit
) {
    val scrollState = rememberScrollState()
    val workflow = viewModel.selectedWorkflow

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
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth()
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .padding(24.dp)
                    .verticalScroll(scrollState)
            ) {
                Text(
                    text = "Therapy Input Form",
                    style = MaterialTheme.typography.titleLarge.copy(
                        color = DeepTeal,
                        fontWeight = FontWeight.Bold,
                        fontSize = 22.sp
                    )
                )
                Text(
                    text = "Selected Workflow: ${workflow.title}",
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.Gray
                )
                
                Spacer(modifier = Modifier.height(16.dp))

                // Section 1: Dosing (Required for all)
                SectionHeader("Dosing Regimen")
                InputField(
                    label = "Dose (mg)", 
                    value = viewModel.dose, 
                    onValueChange = { viewModel.dose = it }, 
                    isNumeric = true, 
                    suffix = "mg",
                    placeholder = "Accepts: 250 - 3000 mg",
                    isRequired = true
                )
                Spacer(modifier = Modifier.height(12.dp))
                InputField(
                    label = "Interval (hours)", 
                    value = viewModel.interval, 
                    onValueChange = { viewModel.interval = it }, 
                    isNumeric = true, 
                    suffix = "h",
                    placeholder = "Commonly 8, 12, or 24h",
                    isRequired = true
                )
                Spacer(modifier = Modifier.height(12.dp))
                InputField(
                    label = "Infusion Duration (hours)", 
                    value = viewModel.infusion, 
                    onValueChange = { viewModel.infusion = it }, 
                    isNumeric = true, 
                    suffix = "h",
                    placeholder = "Usually 1.0 - 2.0h",
                    isRequired = true
                )

                // Section 2: Laboratory Values (DYNAMIC)
                if (workflow.needsPre) {
                    Spacer(modifier = Modifier.height(24.dp))
                    SectionHeader("Pre-Dose (Trough) Level")
                    InputField(
                        label = "Pre-dose Conc (mg/L)", 
                        value = viewModel.preConc, 
                        onValueChange = { viewModel.preConc = it }, 
                        isNumeric = true, 
                        suffix = "mg/L",
                        placeholder = "Typical trough: 10 - 20 mg/L",
                        isRequired = true
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    InputField(
                        label = "Hours before next dose", 
                        value = viewModel.preTime, 
                        onValueChange = { viewModel.preTime = it }, 
                        isNumeric = true, 
                        suffix = "h",
                        placeholder = "Typical: 0.5 - 1.0h",
                        isRequired = true
                    )
                }

                if (workflow.needsPost) {
                    Spacer(modifier = Modifier.height(24.dp))
                    SectionHeader("Post-Dose (Peak) Level")
                    InputField(
                        label = "Post-dose Conc (mg/L)", 
                        value = viewModel.postConc, 
                        onValueChange = { viewModel.postConc = it }, 
                        isNumeric = true, 
                        suffix = "mg/L",
                        placeholder = "Typical peak: 25 - 40 mg/L",
                        isRequired = true
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    InputField(
                        label = "Hours after infusion end", 
                        value = viewModel.postTime, 
                        onValueChange = { viewModel.postTime = it }, 
                        isNumeric = true, 
                        suffix = "h",
                        placeholder = "Typical: 1.0 - 2.0h",
                        isRequired = true
                    )
                }

                Spacer(modifier = Modifier.height(32.dp))
                
                NextBackButtons(
                    onBack = onBackClick,
                    onNext = onReviewClick,
                    nextText = "Review Inputs"
                )
            }
        }
    }
}
