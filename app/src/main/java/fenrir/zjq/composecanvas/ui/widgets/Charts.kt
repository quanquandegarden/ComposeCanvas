package fenrir.zjq.composecanvas.ui.widgets

import android.content.Context
import android.graphics.Paint
import android.widget.Toast
import androidx.compose.animation.core.FloatTweenSpec
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.translate
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.input.pointer.PointerInputChange
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.sin

// 根据用户所点击的touchAngles和startAngles得出当前点击是第几项
private fun getPositionFromAngle(angles: List<Float>, touchAngle: Double, startAngle: Float): Int {
    var totalanagle = 0f
    for ((i, angle) in angles.withIndex()) {
        totalanagle += angle
        if (touchAngle >= startAngle && touchAngle <= (startAngle + totalanagle) % 360) {
            return i
        } else if (startAngle + totalanagle > 360) {
            if (touchAngle >= startAngle || touchAngle < (startAngle + totalanagle) % 360) {
                return i
            }
        }//角度计算
    }
    return -1
}

@Composable
fun PieChart(title: String, color: List<Color>, point: List<Float>, labels: List<String>) {
    Column(
        modifier = Modifier.border(color = Color.Black, width = 1.dp),
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(title)
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            CenterPieChart(context = LocalContext.current, color, point, labels)
            Column(
                modifier = Modifier
                    .padding(start = 30.dp, end = 10.dp), horizontalAlignment = Alignment.Start
            ) {
                for ((i, p) in point.withIndex()) {
                    Row(
                        horizontalArrangement = Arrangement.Start,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(10.dp, 10.dp)
                                .background(color = color[i])
                        )
                        Text(
                            text = "${labels[i]} ($p)",
                            fontSize = 12.sp,
                            modifier = Modifier.padding(start = 5.dp, end = 5.dp)
                        )
                    }
                }//循环添加右边排列元素
            }
        }//右边排列

    }
}

@Composable
fun CenterPieChart(
    context: Context,
    color: List<Color>,
    point: List<Float>,
    labels: List<String>,
    modifier: Modifier = Modifier
) {
    val sum = point.sum()
    val viewWeight = 200.dp //自定义View宽度 此处为正方形 所以宽高一致
    val drawHeight = 60f //内部线高 即有颜色部分的宽度
    val selecyAddHeight = 10f //点击后增加的线高
    val angles = mutableListOf<Float>()

    var start by remember { mutableStateOf(false) }
    var position by remember { mutableStateOf(0) }
    var dragOffest by remember { mutableStateOf(0f) }
    val sweepPre by animateFloatAsState(
        targetValue = if (start) 1f else 0f,
        animationSpec = FloatTweenSpec(duration = 1000),
        label = ""
    )
    val paint = Paint()
    paint.color = Color.Black.toArgb()
    paint.textSize = 28f //中间文本字体大小
    paint.style = Paint.Style.STROKE
    Canvas(
        modifier = modifier
            .width(viewWeight)
            .height(viewWeight)
            .background(Color.White)
            .pointerInput(Unit) {
                detectTapGestures(
                    onTap = {
                        val x = it.x - viewWeight.toPx() / 2
                        val y = it.y - viewWeight.toPx() / 2
                        var touchAngle = Math.toDegrees(atan2(y.toDouble(), x.toDouble()))
                        //坐标1,2象限返回-180~0  3,4象限返回0~180
                        if (x < 0 && y < 0 || x > 0 && y < 0) {//1,2象限
                            touchAngle += 360
                        }

                        val nowpostion =
                            getPositionFromAngle(
                                touchAngle = touchAngle,
                                angles = angles,
                                startAngle = dragOffest % 360
                            )
                        position = if (nowpostion == position) {
                            -1
                        } else {
                            nowpostion
                        }
                        Toast
                            .makeText(
                                context,
                                "onTap: $position",
                                Toast.LENGTH_SHORT
                            )
                            .show()
                    }
                )
            }
            .pointerInput(Unit) {

                var dragstartx = 0f
                var dragstarty = 0f
                detectDragGestures(
                    onDragStart = { offset ->
                        // 拖动开始
                        dragstartx = offset.x
                        dragstarty = offset.y
                    },
                    onDragEnd = {
                        // 拖动结束
                    },
                    onDragCancel = {
                        // 拖动取消
                    },
                    onDrag = { _: PointerInputChange, dragAmount: Offset ->
                        // 拖动中
                        dragstartx += dragAmount.x
                        dragstarty += dragAmount.y
                        val x: Float = if (dragstarty < viewWeight.toPx() / 2) {
                            dragAmount.x
                        } else {
                            dragAmount.x
                        }
                        val y: Float = if (dragstartx < viewWeight.toPx() / 2) {
                            dragAmount.y
                        } else {
                            dragAmount.y
                        }//拆分坐标分量来进行顺逆的判断
                        dragOffest += x + y

                    }
                )
            }
    ) {
        translate(0f, 0f) {
            start = true//开始绘制动画
            var startAngle = dragOffest % 360//初始角度
            var selectAngle = 0f//记录被点击项的初始角度

            for ((i, p) in point.withIndex()) {
                val sweepAngle = p / sum * 360f//偏向的角度
                if (angles.size < point.size) {
                    angles.add(sweepAngle)
                }

                if (position != i) {

                    var angle = startAngle % 360 + sweepAngle * sweepPre * 0.5
                    angle = angle * Math.PI / 180// 要转弧度
                    val y = sin(angle) * 10
                    val x = cos(angle) * 10

                    drawArc(
                        color = color[i],
                        startAngle = startAngle,
                        sweepAngle = sweepAngle * sweepPre,//1f 弥补部分精度不足问题
                        useCenter = false, // 指示圆弧是否闭合边界中心的标志
                        // 样式
                        style = Stroke(width = drawHeight, miter = 0f, cap = StrokeCap.Butt),
                        size = Size(
                            (viewWeight.toPx() - drawHeight * 2),
                            (viewWeight.toPx() - drawHeight * 2)
                        ),
                        topLeft = Offset((drawHeight + x).toFloat(), (drawHeight + y).toFloat())
                    )

                    drawArc(
                        color = color[i],
                        alpha = 0.5f,
                        startAngle = startAngle,
                        sweepAngle = sweepAngle * sweepPre,
                        useCenter = false, // 指示圆弧是否闭合边界中心的标志
                        // 样式
                        style = Stroke(width = drawHeight / 5, miter = 10f, cap = StrokeCap.Butt),
                        size = Size(
                            viewWeight.toPx() - drawHeight * 3,
                            viewWeight.toPx() - drawHeight * 3
                        ),
                        topLeft = Offset(
                            (drawHeight * 1.5f + x).toFloat(),
                            (drawHeight * 1.5f + y).toFloat()
                        )
                    )


                } else {
                    selectAngle = startAngle
                }
                startAngle += sweepAngle
            }

            //中间文本绘制
            var textweight = paint.measureText("全部")
            var text = "$sum"
            var pointweight = paint.measureText(text)
            if (position != -1) {

                //选择的内容
                val sweepAngle = point[position] / sum * 360f

                var angle = selectAngle + (sweepAngle * sweepPre) * 0.5
                angle = angle * Math.PI / 180 // 要转弧度
                val y = sin(angle) * 10
                val x = cos(angle) * 10

                drawArc(
                    color = color[position],
                    startAngle = selectAngle,
                    sweepAngle = sweepAngle * sweepPre,
                    useCenter = false, // 指示圆弧是否闭合边界中心的标志
                    // 样式
                    style = Stroke(
                        width = drawHeight + selecyAddHeight,
                        miter = 10f,
                        cap = StrokeCap.Butt
                    ),
                    size = Size(
                        viewWeight.toPx() - drawHeight * 2 + selecyAddHeight,
                        viewWeight.toPx() - drawHeight * 2 + selecyAddHeight
                    ),
                    topLeft = Offset(
                        (drawHeight - selecyAddHeight / 2 + x).toFloat(),
                        (drawHeight - selecyAddHeight / 2 + y).toFloat()
                    )
                )//选择后宽度

                drawArc(
                    color = color[position],
                    alpha = 0.5f,
                    startAngle = selectAngle,
                    sweepAngle = sweepAngle * sweepPre,
                    useCenter = false, // 指示圆弧是否闭合边界中心的标志
                    style = Stroke(
                        width = drawHeight / 5,
                        miter = 10f,
                        cap = StrokeCap.Butt
                    ),                          // 样式
                    size = Size(
                        viewWeight.toPx() - drawHeight * 3,
                        viewWeight.toPx() - drawHeight * 3
                    ),
                    topLeft = Offset(
                        (drawHeight * 1.5f + x).toFloat(),
                        (drawHeight * 1.5f + y).toFloat()
                    )
                )

                //中间文本绘制
                textweight = paint.measureText(labels[position])
                val pointF = "%.1f".format(sweepAngle * 100 / 360)
                val positionF = point[position]
                text = "$positionF($pointF%)"
                pointweight = paint.measureText(text)
                //用原生Canvas来绘制
                drawContext.canvas.nativeCanvas.drawText(
                    labels[position],
                    viewWeight.toPx() / 2 - textweight / 2,
                    viewWeight.toPx() / 2,
                    paint
                )
                drawContext.canvas.nativeCanvas.drawText(
                    text,
                    viewWeight.toPx() / 2 - pointweight / 2,
                    viewWeight.toPx() / 2 + paint.textSize,
                    paint
                )
            }
            //用原生Canvas来绘制
            drawContext.canvas.nativeCanvas.drawText(
                if (position == -1) "全部" else labels[position],
                viewWeight.toPx() / 2 - textweight / 2,
                viewWeight.toPx() / 2,
                paint
            )

            drawContext.canvas.nativeCanvas.drawText(
                text,
                viewWeight.toPx() / 2 - pointweight / 2,
                viewWeight.toPx() / 2 + paint.textSize,
                paint
            )

        }
    }
}