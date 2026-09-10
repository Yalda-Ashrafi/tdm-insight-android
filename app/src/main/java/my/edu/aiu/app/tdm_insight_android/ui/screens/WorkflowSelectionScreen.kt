package my.edu.aiu.app.tdm_insight_android.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import my.edu.aiu.app.tdm_insight_android.ui.theme.AquaBlue
import my.edu.aiu.app.tdm_insight_android.ui.theme.DeepTeal
import my.edu.aiu.app.tdm_insight_android.ui.theme.EmeraldGreen
import my.edu.aiu.app.tdm_insight_android.ui.theme.SoftPink

/**
 * OWNER: Yalda
 */
@Composable
fun WorkflowSelectionScreen(
    onPreDoseClick: () -> Unit,
    onPostDoseClick: () -> Unit,
    onPrePostClick: () -> Unit,
    onBackClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                brush = Brush.linearGradient(
                    colors = listOf(SoftPink, AquaBlue)
                )
            ),
        contentAlignment = Alignment.Center
    ) {
        Card(
            shape = RoundedCornerShape(28.dp),
            elevation = CardDefaults.cardElevation(defaultElevation = 12.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White.copy(alpha = 0.96f)),
            modifier = Modifier.padding(24.dp)
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.padding(32.dp)
            ) {
                Text(
                    text = "Select TDM Workflow",
                    style = MaterialTheme.typography.titleLarge.copy(
                        color = DeepTeal,
                        fontWeight = FontWeight.Bold
                    )
                )
                Text(
                    text = "Choose a calculation method based on available lab data.",
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.Gray,
                    textAlign = TextAlign.Center
                )
                
                Spacer(modifier = Modifier.height(32.dp))
                
                // PRE
                WorkflowButton(
                    title = "Pre-Dose (Trough Only)",
                    subtitle = "Elimination is ESTIMATED from renal function.",
                    color = EmeraldGreen,
                    onClick = onPreDoseClick
                )
                
                Spacer(modifier = Modifier.height(16.dp))

                // POST
                WorkflowButton(
                    title = "Post-Dose (Peak Only)",
                    subtitle = "Elimination is ESTIMATED from renal function.",
                    color = Color(0xFF4DB6AC),
                    onClick = onPostDoseClick
                )
                
                Spacer(modifier = Modifier.height(16.dp))
                
                // PRE + POST
                WorkflowButton(
                    title = "Pre + Post (Gold Standard)",
                    subtitle = "Elimination is MEASURED from patient samples.",
                    color = DeepTeal,
                    onClick = onPrePostClick
                )

                Spacer(modifier = Modifier.height(32.dp))
                
                Button(
                    onClick = onBackClick,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color.LightGray,
                        contentColor = Color.DarkGray
                    ),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text(text = "Back")
                }
            }
        }
    }
}

@Composable
fun WorkflowButton(title: String, subtitle: String, color: Color, onClick: () -> Unit) {
    Button(
        onClick = onClick,
        colors = ButtonDefaults.buttonColors(containerColor = color),
        shape = RoundedCornerShape(12.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(text = title, fontWeight = FontWeight.Bold, fontSize = 16.sp)
            Text(text = subtitle, fontSize = 10.sp, fontWeight = FontWeight.Normal)
        }
    }
}
