package fenrir.zjq.composecanvas.ui.screen

import android.graphics.Paint
import android.graphics.Typeface
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PointMode
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.imageResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import fenrir.zjq.composecanvas.R
import fenrir.zjq.composecanvas.annonation.DarkLightPreviews
import fenrir.zjq.composecanvas.annonation.FontScalePreviews
import fenrir.zjq.composecanvas.ui.theme.ComposeCanvasTheme
import fenrir.zjq.composecanvas.ui.widgets.PieChart
import fenrir.zjq.composecanvas.ui.widgets.Rabbit

object MainScreen {
    object Default {
        @Composable
        fun Content() {
            Greeting()
        }
    }
}

@Composable
fun Greeting(modifier: Modifier = Modifier) {
    val dogImage = ImageBitmap.imageResource(id = R.drawable.image_dog)

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(all = 10.dp)
    ) {
        item {
            val point = listOf(10f, 40f, 20f, 80f, 100f, 60f)
            val labels = listOf("吃饭", "睡觉", "打豆豆", "TIMI时刻", "淘淘时刻", "逛逛时刻")
            val color =
                listOf(Color.Blue, Color.Yellow, Color.Green, Color.Gray, Color.Red, Color.Cyan)

            PieChart("总支出占比", color, point, labels)
        }
        item {
            Spacer(modifier = Modifier.height(10.dp))
            Rabbit()
        }
        item {
            Spacer(modifier = Modifier.height(10.dp))
            Canvas(
                modifier = Modifier
                    .size(100.dp, 100.dp)
                    .border(1.dp, color = Color.Black)
            ) {
                drawLine(
                    start = Offset(0f, 0f),
                    end = Offset(100.dp.toPx(), 100.dp.toPx()),
                    brush = Brush.horizontalGradient(colors = listOf(Color.Yellow, Color.Green)),
                    strokeWidth = 5.dp.toPx(),
                    cap = StrokeCap.Square,
                    alpha = 0.5f,
                )
            }
        }
        item {
            Spacer(modifier = Modifier.height(10.dp))
            Canvas(
                modifier = Modifier
                    .size(100.dp, 100.dp)
                    .border(1.dp, color = Color.Black)
            ) {
                drawRect(
                    color = Color.Green,
                    size = Size(50.dp.toPx(), 50.dp.toPx()),
                    style = Stroke(width = 10f)
                )
            }
        }
        item {
            Spacer(modifier = Modifier.height(10.dp))
            Canvas(
                modifier = Modifier
                    .size(100.dp, 100.dp)
                    .border(1.dp, color = Color.Black)
            ) {
                drawImage(image = dogImage)
            }
        }

        item {
            Spacer(modifier = Modifier.height(10.dp))
            Canvas(
                modifier = Modifier
                    .size(100.dp, 100.dp)
                    .border(1.dp, color = Color.Black)
            ) {
                drawRoundRect(
                    color = Color.Blue,
                    topLeft = Offset(x = 50.dp.toPx(), 50.dp.toPx()),
                    cornerRadius = CornerRadius(x = 20f, y = 20f)
                )
            }
        }
        item {
            Spacer(modifier = Modifier.height(10.dp))
            Canvas(
                modifier = Modifier
                    .size(100.dp, 100.dp)
                    .border(1.dp, color = Color.Black)
            ) {
                drawRoundRect(
                    color = Color.Red,
                    cornerRadius = CornerRadius(40f, 40f),
                    style = Stroke(width = 40f),
                    topLeft = Offset(x = 20f, y = 20f),
                    size = Size(width = size.width - 40f, height = size.height - 40f),
                    alpha = 0.5f
                )
            }
        }
        item {
            Spacer(modifier = Modifier.height(10.dp))
            Row {
                Canvas(
                    modifier = Modifier
                        .size(100.dp, 100.dp)
                        .border(1.dp, color = Color.Black)
                ) {
                    drawCircle(
                        color = Color.Green,
                        radius = 30.dp.toPx()
                    )
                }
                Spacer(modifier = Modifier.width(20.dp))
                Canvas(
                    modifier = Modifier
                        .size(100.dp, 100.dp)
                        .border(1.dp, color = Color.Black)
                ) {
                    drawCircle(
                        color = Color.Green,
                        style = Stroke(width = 10f),
                        center = Offset(x = 60.dp.toPx(), y = 40.dp.toPx()),
                        radius = 40.dp.toPx()
                    )
                }
            }
        }
        item {
            Spacer(modifier = Modifier.height(10.dp))
            Row {
                Canvas(
                    modifier = Modifier
                        .size(100.dp, 100.dp)
                        .border(1.dp, color = Color.Black)
                ) {
                    drawOval(
                        color = Color.LightGray,
                        style = Stroke(width = 10f)
                    )
                }
                Spacer(modifier = Modifier.width(20.dp))
                Canvas(
                    modifier = Modifier
                        .size(100.dp, 100.dp)
                        .border(1.dp, color = Color.Black)
                ) {
                    drawOval(
                        color = Color.LightGray,
                        style = Stroke(width = 10f),
                        size = Size(width = size.width, height = size.height / 2)
                    )
                }
                Spacer(modifier = Modifier.width(20.dp))
                Canvas(
                    modifier = Modifier
                        .size(100.dp, 100.dp)
                        .border(1.dp, color = Color.Black)
                ) {
                    drawOval(
                        color = Color.LightGray,
                        style = Stroke(width = 10f),
                        size = Size(width = size.width / 2, height = size.height / 3),
                        topLeft = Offset(x = 60f, y = 60f)
                    )
                }
            }
        }
        item {
            Spacer(modifier = Modifier.height(10.dp))
            Row {
                Canvas(
                    modifier = Modifier
                        .size(100.dp, 100.dp)
                        .border(1.dp, color = Color.Black)
                ) {
                    drawArc(
                        color = Color.Yellow,
                        startAngle = 0f,
                        sweepAngle = 360f,
                        useCenter = false,
                        size = Size(width = size.width, height = size.height / 2)
                    )
                }
                Spacer(modifier = Modifier.width(20.dp))
                Canvas(
                    modifier = Modifier
                        .size(100.dp, 100.dp)
                        .border(1.dp, color = Color.Black)
                ) {
                    drawArc(
                        color = Color.Yellow,
                        startAngle = -90f,
                        sweepAngle = 270f,
                        useCenter = true
                    )
                }
                Spacer(modifier = Modifier.width(20.dp))
                Canvas(
                    modifier = Modifier
                        .size(100.dp, 100.dp)
                        .border(1.dp, color = Color.Black)
                ) {
                    drawArc(
                        color = Color.Yellow,
                        startAngle = 0f,
                        sweepAngle = 100f,
                        useCenter = false,
                        style = Stroke(width = 8f)
                    )
                }
            }
        }
        item {
            Spacer(modifier = Modifier.height(10.dp))
            Row {
                Canvas(
                    modifier = Modifier
                        .size(100.dp, 100.dp)
                        .border(1.dp, color = Color.Black)
                ) {
                    val myPath = Path()
                    myPath.lineTo(size.width / 2, size.height / 2)
                    myPath.lineTo(0f, size.height)
                    myPath.close()

                    drawPath(
                        path = myPath,
                        color = Color.Red
                    )
                }
                Spacer(modifier = Modifier.width(20.dp))
                Canvas(
                    modifier = Modifier
                        .size(100.dp, 100.dp)
                        .border(1.dp, color = Color.Black)
                ) {
                    val myPath = Path()
                    myPath.lineTo(size.width / 2, size.height / 2)
                    myPath.lineTo(0f, size.height)
                    myPath.close()
                    drawPath(
                        path = myPath,
                        color = Color.Red,
                        style = Stroke(width = 20f)
                    )
                }
            }
        }
        item {
            Spacer(modifier = Modifier.height(10.dp))
            Canvas(
                modifier = Modifier
                    .size(100.dp)
                    .border(1.dp, color = Color.Black),
            ) {
                val eyePoints = arrayListOf(
                    Offset(size.width / 3, size.height / 3),
                    Offset(size.width / 3 * 2, size.height / 3),
                )
                val mousePoints = arrayListOf(
                    Offset(size.width / 3, size.height * 2 / 3),
                    Offset(size.width / 3 * 2, size.height * 2 / 3),
                )


                drawPoints(
                    color = Color.Red,
                    points = eyePoints,
                    pointMode = PointMode.Points,
                    strokeWidth = 40f,
                    cap = StrokeCap.Round
                )

                drawPoints(
                    color = Color.Red,
                    points = mousePoints,
                    pointMode = PointMode.Lines,
                    strokeWidth = 20f
                )
            }
        }
        item {
            val assetManager = LocalContext.current.assets
            val paint = Paint().apply {
                textAlign = Paint.Align.CENTER
                textSize = 200f
                color = Color.White.toArgb()
                typeface = Typeface.createFromAsset(assetManager, "FACEBOLF.OTF")
            }

            Spacer(modifier = Modifier.height(10.dp))
            Canvas(modifier = Modifier.size(100.dp)) {
                drawRoundRect(
                    color = Color(0xFF1776d1),
                    cornerRadius = CornerRadius(20f, 20f)
                )
                drawContext.canvas.nativeCanvas
                    .drawText("f", center.x + 25, center.y + 90, paint)
            }
        }
    }
}

@FontScalePreviews
@DarkLightPreviews
@Preview
@Composable
fun GreetingPreview() {
    ComposeCanvasTheme {
        Greeting()
    }
}

