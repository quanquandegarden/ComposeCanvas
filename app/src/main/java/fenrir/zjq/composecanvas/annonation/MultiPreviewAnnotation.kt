package fenrir.zjq.composecanvas.annonation

import android.content.res.Configuration
import android.graphics.Bitmap.Config
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.ui.tooling.preview.Preview

@Preview(
    name = "Small font",
    group = "Font scales",
    fontScale = 0.5f
)

@Preview(
    name = "Large font",
    group = "Font scales",
    fontScale = 1.5f
)

annotation class FontScalePreviews

@Preview(
    name = "Dark mode",
    group = "Dark light theme",
    uiMode = Configuration.UI_MODE_NIGHT_YES or Configuration.UI_MODE_TYPE_NORMAL,
    showBackground = true
)

@Preview(
    name = "Light mode",
    group = "Dark light theme",
    uiMode = Configuration.UI_MODE_NIGHT_YES or Configuration.UI_MODE_TYPE_NORMAL,
    showBackground = true
)

annotation class DarkLightPreviews
