package my.edu.aiu.app.tdm_insight_android.ui.screens

/**
 * OWNER: Elyas
 */

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.scaleIn
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import my.edu.aiu.app.tdm_insight_android.engine.CalculationOutcome
import my.edu.aiu.app.tdm_insight_android.ui.theme.AquaBlue
import my.edu.aiu.app.tdm_insight_android.ui.theme.DeepTeal
import my.edu.aiu.app.tdm_insight_android.ui.theme.SoftPink
import my.edu.aiu.app.tdm_insight_android.viewmodel.PatientViewModel

@Composable
fun ExplanationScreen(
    viewModel: PatientViewModel,
    onBackClick: () -> Unit
) {
    val visible = remember { mutableStateOf(value = true) }
    val scrollState = rememberScrollState()
    val outcome = viewModel.calculationResult

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.linearGradient(
                    colors = listOf(SoftPink, AquaBlue)
                )
            ),
        contentAlignment = Alignment.Center
    ) {
        if (outcome is CalculationOutcome.Success) {
            val result = outcome.result
            
            AnimatedVisibility(
                visible = visible.value,
                enter = fadeIn(animationSpec = tween(800)) + scaleIn(initialScale = 0.9f)
            ) {
                Card(
                    shape = RoundedCornerShape(28.dp),
                    elevation = CardDefaults.cardElevation(defaultElevation = 12.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White.copy(alpha = 0.98f)),
                    modifier = Modifier.padding(16.dp).fillMaxWidth()
                ) {
                    Column(
                        horizontalAlignment = Alignment.Start,
                        modifier = Modifier.padding(24.dp).verticalScroll(scrollState)
                    ) {
                        Text(
                            text = result.workflow.title,
                            style = MaterialTheme.typography.titleLarge.copy(
                                color = DeepTeal,
                                fontWeight = FontWeight.Bold,
                                fontSize = 22.sp
                            )
                        )
                        Text(
                            text = "Calculation Explanation",
                            style = MaterialTheme.typography.bodySmall,
                            color = Color.Gray
                        )
                        Spacer(modifier = Modifier.height(16.dp))

                        // Requirements 10 & 11: Display intermediate results and explanation
                        result.steps.forEach { step ->
                            StepRow(
                                order = step.order,
                                label = step.label,
                                formula = step.formula,
                                substitution = step.substitution,
                                value = step.value
                            )
                            Spacer(modifier = Modifier.height(16.dp))
                        }

                        Spacer(modifier = Modifier.height(24.dp))
                        
                        Button(
                            onClick = onBackClick,
                            colors = ButtonDefaults.buttonColors(containerColor = DeepTeal),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(text = "Back to Results", color = Color.White, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun StepRow(order: Int, label: String, formula: String, substitution: String, value: String) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Text(text = "$order. $label", fontWeight = FontWeight.Bold, color = DeepTeal, fontSize = 15.sp)
        Text(text = formula, style = MaterialTheme.typography.bodySmall, color = Color.Gray)
        Text(text = substitution, style = MaterialTheme.typography.bodySmall, color = Color.Gray)
        Text(text = "= $value", fontWeight = FontWeight.ExtraBold, color = Color.Black, fontSize = 16.sp)
        HorizontalDivider(modifier = Modifier.padding(top = 8.dp), color = Color.LightGray.copy(alpha = 0.3f))
    }
}
