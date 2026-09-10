package my.edu.aiu.app.tdm_insight_android.ui.screens

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay
import my.edu.aiu.app.tdm_insight_android.ui.theme.DeepTeal
import my.edu.aiu.app.tdm_insight_android.ui.theme.EmeraldGreen
import my.edu.aiu.app.tdm_insight_android.ui.theme.SoftMint

@Composable
fun SplashScreen(onTimeout: () -> Unit) {
    val alpha = remember { Animatable(0f) }
    val scale = remember { Animatable(0.8f) }

    LaunchedEffect(Unit) {
        // High-end animation sequence
        alpha.animateTo(
            targetValue = 1f,
            animationSpec = tween(durationMillis = 1000, easing = FastOutSlowInEasing)
        )
        scale.animateTo(
            targetValue = 1f,
            animationSpec = tween(durationMillis = 1200, easing = FastOutSlowInEasing)
        )
        delay(2300) // Total stay time ~3.5s
        onTimeout()
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(SoftMint),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .alpha(alpha.value)
                .scale(scale.value)
        ) {
            // Spinner dots style loader with a clinical green
            CircularProgressIndicator(
                color = EmeraldGreen,
                strokeWidth = 6.dp,
                modifier = Modifier.size(72.dp)
            )
            
            Spacer(modifier = Modifier.height(48.dp))
            
            Text(
                text = "TDM Insight",
                style = MaterialTheme.typography.headlineLarge.copy(
                    color = DeepTeal,
                    fontWeight = FontWeight.ExtraBold,
                    letterSpacing = 4.sp
                )
            )
            
            Spacer(modifier = Modifier.height(8.dp))
            
            Text(
                text = "Precision Pharmacokinetics",
                style = MaterialTheme.typography.bodyMedium.copy(
                    color = Color.DarkGray.copy(alpha = 0.8f),
                    fontWeight = FontWeight.Light,
                    letterSpacing = 1.sp
                )
            )
        }
    }
}
