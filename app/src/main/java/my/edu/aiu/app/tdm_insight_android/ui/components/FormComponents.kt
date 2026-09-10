package my.edu.aiu.app.tdm_insight_android.ui.components

/**
 * OWNER: Elyas
 */

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import my.edu.aiu.app.tdm_insight_android.ui.theme.DeepTeal
import my.edu.aiu.app.tdm_insight_android.ui.theme.EmeraldGreen

@Composable
fun InputField(
    label: String,
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    error: String? = null,
    isNumeric: Boolean = false,
    suffix: String? = null,
    placeholder: String? = null,
    isRequired: Boolean = false
) {
    val labelText = buildAnnotatedString {
        if (isRequired) {
            withStyle(style = SpanStyle(color = Color.Red)) {
                append("* ")
            }
        }
        append(label)
    }

    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text(text = labelText) },
        placeholder = placeholder?.let { { Text(text = it, color = Color.Gray.copy(alpha = 0.5f)) } },
        modifier = modifier.fillMaxWidth(),
        isError = error != null,
        supportingText = error?.let { { Text(text = it) } },
        suffix = suffix?.let { { Text(text = it) } },
        keyboardOptions = KeyboardOptions(
            keyboardType = if (isNumeric) KeyboardType.Decimal else KeyboardType.Text
        ),
        singleLine = true
    )
}

@Composable
fun TdmButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    containerColor: Color = EmeraldGreen
) {
    Button(
        onClick = onClick,
        modifier = modifier.fillMaxWidth(),
        enabled = enabled,
        colors = ButtonDefaults.buttonColors(
            containerColor = containerColor,
            contentColor = Color.White
        ),
        elevation = ButtonDefaults.buttonElevation(defaultElevation = 4.dp)
    ) {
        Text(text = text)
    }
}

@Composable
fun NextBackButtons(
    onBack: () -> Unit,
    onNext: () -> Unit,
    nextText: String = "Next",
    backText: String = "Back",
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 16.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Button(
            onClick = onBack,
            colors = ButtonDefaults.buttonColors(
                containerColor = Color.LightGray,
                contentColor = Color.DarkGray
            ),
            modifier = Modifier.weight(1f)
        ) {
            Text(text = backText)
        }
        
        Spacer(modifier = Modifier.width(16.dp))
        
        Button(
            onClick = onNext,
            colors = ButtonDefaults.buttonColors(
                containerColor = EmeraldGreen, // Consistent medical green
                contentColor = Color.White
            ),
            modifier = Modifier.weight(1f)
        ) {
            Text(text = nextText)
        }
    }
}
