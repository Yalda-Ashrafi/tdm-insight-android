package my.edu.aiu.app.tdm_insight_android

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import my.edu.aiu.app.tdm_insight_android.ui.theme.TdminsightandroidTheme
import my.edu.aiu.app.tdm_insight_android.navigation.AppNavigation

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            TdminsightandroidTheme {
                AppNavigation()
            }
        }
    }
}
