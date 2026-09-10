package my.edu.aiu.app.tdm_insight_android.ui.screens

/**
 * OWNER: Benat
 */

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil3.compose.rememberAsyncImagePainter
import my.edu.aiu.app.tdm_insight_android.ui.components.InputField
import my.edu.aiu.app.tdm_insight_android.ui.components.NextBackButtons
import my.edu.aiu.app.tdm_insight_android.ui.theme.AquaBlue
import my.edu.aiu.app.tdm_insight_android.ui.theme.DeepTeal
import my.edu.aiu.app.tdm_insight_android.ui.theme.EmeraldGreen
import my.edu.aiu.app.tdm_insight_android.ui.theme.SoftPink
import my.edu.aiu.app.tdm_insight_android.viewmodel.PatientViewModel

@Composable
fun PatientCaseScreen(
    viewModel: PatientViewModel,
    onSaveClick: () -> Unit,
    onBackClick: () -> Unit,
    onCameraClick: () -> Unit
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
                    text = "Patient Case Entry",
                    style = MaterialTheme.typography.titleLarge.copy(
                        color = DeepTeal,
                        fontWeight = FontWeight.Bold,
                        fontSize = 22.sp
                    )
                )
                Spacer(modifier = Modifier.height(8.dp))
                
                Text(
                    text = "Enter patient details below to calculate pharmacokinetic parameters.",
                    color = Color.Gray,
                    fontSize = 14.sp,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(bottom = 16.dp)
                )

                // Input fields connected to ViewModel with professional guidance placeholders
                InputField(
                    label = "Full Name / Case ID", 
                    value = viewModel.caseName, 
                    onValueChange = { viewModel.caseName = it },
                    placeholder = "Unique identifier or full name",
                    isRequired = true
                )
                Spacer(modifier = Modifier.height(12.dp))
                
                Text(
                    text = "Gender *",
                    style = MaterialTheme.typography.labelMedium,
                    modifier = Modifier.fillMaxWidth(),
                    textAlign = TextAlign.Start,
                    color = DeepTeal
                )
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .selectable(
                                selected = viewModel.isMale,
                                onClick = { viewModel.isMale = true },
                                role = Role.RadioButton
                            )
                            .padding(8.dp)
                    ) {
                        RadioButton(selected = viewModel.isMale, onClick = null)
                        Text("Male", modifier = Modifier.padding(start = 4.dp))
                    }
                    Spacer(modifier = Modifier.width(24.dp))
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .selectable(
                                selected = !viewModel.isMale,
                                onClick = { viewModel.isMale = false },
                                role = Role.RadioButton
                            )
                            .padding(8.dp)
                    ) {
                        RadioButton(selected = !viewModel.isMale, onClick = null)
                        Text("Female", modifier = Modifier.padding(start = 4.dp))
                    }
                }
                
                Spacer(modifier = Modifier.height(12.dp))
                InputField(
                    label = "Age (years)", 
                    value = viewModel.age, 
                    onValueChange = { viewModel.age = it }, 
                    isNumeric = true,
                    placeholder = "Accepts: 18 - 120 years",
                    isRequired = true
                )
                Spacer(modifier = Modifier.height(12.dp))
                InputField(
                    label = "Weight (kg)", 
                    value = viewModel.weight, 
                    onValueChange = { viewModel.weight = it }, 
                    isNumeric = true, 
                    suffix = "kg",
                    placeholder = "Accepts: 40.0 - 250.0 kg",
                    isRequired = true
                )
                Spacer(modifier = Modifier.height(12.dp))
                InputField(
                    label = "Height (cm)", 
                    value = viewModel.height, 
                    onValueChange = { viewModel.height = it }, 
                    isNumeric = true, 
                    suffix = "cm",
                    placeholder = "Accepts: 50 - 250 cm",
                    isRequired = true
                )
                Spacer(modifier = Modifier.height(12.dp))
                InputField(
                    label = "Serum Creatinine (mg/dL)", 
                    value = viewModel.scr, 
                    onValueChange = { viewModel.scr = it }, 
                    isNumeric = true, 
                    suffix = "mg/dL",
                    placeholder = "Accepts: 0.1 - 20.0 mg/dL",
                    isRequired = true
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Camera Section
                OutlinedButton(
                    onClick = onCameraClick,
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = EmeraldGreen),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Icon(Icons.Default.CameraAlt, contentDescription = null)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Capture Lab Report", fontWeight = FontWeight.SemiBold)
                }

                viewModel.photoUri?.let { uri ->
                    Spacer(modifier = Modifier.height(8.dp))
                    Image(
                        painter = rememberAsyncImagePainter(uri),
                        contentDescription = "Captured Report",
                        modifier = Modifier
                            .size(120.dp)
                            .clip(RoundedCornerShape(12.dp)),
                        contentScale = ContentScale.Crop
                    )
                }

                Spacer(modifier = Modifier.height(24.dp))
                
                NextBackButtons(
                    onBack = onBackClick,
                    onNext = onSaveClick,
                    nextText = "Save Case"
                )
            }
        }
    }
}
