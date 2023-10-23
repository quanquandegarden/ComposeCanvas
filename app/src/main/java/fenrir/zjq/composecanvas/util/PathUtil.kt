package fenrir.zjq.composecanvas.util

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Path


object PathUtil {

    private val SVGD: List<Char> = listOf(
        'M',    // 移动到某点 (x,y)+
        'm',    // 移动到某点（相对坐标）(dx,dy)+
        'L',    // 画线到某点 (x,y)+
        'l',    // 画线到某点（相对坐标）(dx,dy)+
        'H',    // 水平画线到（给定横坐标，原纵坐标）点 x+
        'h',    // 水平画线到（相对横坐标，原纵坐标）点 dx+
        'V',    // 垂直画线到（原横坐标，给定纵坐标）点 y+
        'v',    // 垂直画线到（原横坐标，相对纵坐标）点 dy+
        'A',    // 画椭圆曲线到给定终点。 7n个参数（rx,ry,angle,large-arc-flg,sweep-flg,x,y)+
        'a',    // 画椭圆曲线到相对终点。 7n个参数（rx,ry,angle,large-arc-flg,sweep-flg,dx,dy)+
        'Q',    // 画二次贝塞尔曲线到给定终点(x,y),控制点为(x1,y1)。 4n个参数（x1,y1,x,y)+
        'q',    // 画二次贝塞尔曲线到相对终点(dx,dy),控制点为(dx1,dy1)。 4n个参数(dx1,dy1,dx,dy)+
        'T',    // 画二次贝塞尔曲线到终点(x,y),控制点为上个二次贝塞尔控制点关于当前绘制点的对称点，若上个不是，则为当前绘制点。 2n个参数(x,y)+
        't',    // 画二次贝塞尔曲线到相对终点(dx,dy),控制点为上个二次贝塞尔控制点关于当前绘制点的对称点，若上个不是，则为当前绘制点。 2n个参数(dx,dy)+
        'C',    // 画三次贝塞尔曲线到终点(x,y),(x1,y1)为第一个控制点，(x2,y2)为第二个控制点。 6n个参数(x1,y1,x2,y2,x,y)+
        'c',    // 画三次贝塞尔曲线到相对终点(dx,dy),(dx1,dy1)为第一个相对控制点，(dx2,dy2)为第二个相对控制点。 6n个参数(dx1,dy1,dx2,dy2,dx,dy)+
        'S',    // 画三次贝塞尔曲线到终点(x,y)，第一个控制点为上个三次贝塞尔的第二个控制点关于当前绘制点的对称点，若上个不是，则为当前控制点，(x2,y2)为第二个控制点。 4n个参数(x2,y2,x,y)+
        's',    // 画三次贝塞尔曲线到相对终点(dx,dy)，第一个控制点为上个三次贝塞尔的第二个控制点关于当前绘制点的对称点，若上个不是，则为当前控制点，(dx2,dy2)为第二个相对控制点。 4n个参数(dx2,dy2,dx,dy)+
        'Z',    // 通过连接路径的最后一个点与路径的起始点来闭合当前的子路径。如果这两个点的坐标不同，则在两者之间绘制一条直线。
        'z',    // 通过连接路径的最后一个点与路径的起始点来闭合当前的子路径。如果这两个点的坐标不同，则在两者之间绘制一条直线。
    )

    /**
     * use svg d attribute to split String to List<String>
     *     eg: "M783.7,527.8S1008.8,166.9 876,115C743.1,63.1 610.2,455.4 610.2,455.4l173.5,72.4Z"
     *     => ["M783.7,527.8", "S1008.8,166.9 876,115", "C743.1,63.1 610.2,455.4 610.2,455.4", "l173.5,72.4", "Z"]
     *     ps: Please ensure that the command parameter conform to the format of the eg
     */
    private fun splitSvgCommand(
        command: String,
        d: List<Char>,
        containDelimiter: Boolean = true
    ): List<String> {
        val pathStrings: MutableList<String> = mutableListOf()
        var pathString = ""
        for ((i, c) in command.withIndex()) {
            if (i != 0 && d.contains(c) && pathString.isNotEmpty()) {
                pathStrings.add(pathString)
                pathString = ""
            }
            if (!(d.contains(c) && !containDelimiter)) {
                pathString += c
            }
        }
        pathStrings.add(pathString)
        return pathStrings
    }


    /**
     * translate vector path to Path
     *
     * @param input vector path
     *    ps: The parameters are separated by ‘,’  and there is no separation between d attributes.
     *    eg: ["M783.7,527.8","S1008.8,166.9 876,115", "C743.1,63.1 610.2,455.4 610.2,455.4", "l173.5,72.4", "Z"]
     *    eg: "M783.7,527.8S1008.8,166.9 876,115C743.1,63.1 610.2,455.4 610.2,455.4l173.5,72.4Z"
     */
    fun svgToPath(input: String): Path {
        val path = Path()  // 绘制路径需要的path
        val pathStrings = splitSvgCommand(command = input, d = SVGD)  // 根据d属性切分后的path命令字符串列表

        var lastQControlP = Offset.Zero // 最新绘制过的二次贝塞尔的控制点坐标
        var lastCSecControlP = Offset.Zero // 最新绘制过的三次贝塞尔曲线的第二个控制点坐标
        var lastEndP = Offset.Zero // 最新绘制过的path的终点

        for ((i, d) in pathStrings.withIndex()) {
            // in svg d attribute, 'Z' and 'z' commands have no parameters
            val params: List<Float> = if ((d.first() == 'Z' || d.first() == 'z')) {
                emptyList()
            } else {
                splitSvgCommand(
                    command = d.substring(1, d.length),
                    d = listOf(',', ' '),
                    containDelimiter = false
                ).map { it.toFloat() }
            }
            when (d.first()) {
                'M' -> {
                    val count = params.size / 2
                    for (j in 0 until count) {
                        path.moveTo(params[j * 2], params[j * 2 + 1])
                        lastEndP = Offset(params[j * 2], params[j * 2 + 1])
                    }
                }

                'm' -> {
                    val count = params.size / 2
                    for (j in 0 until count) {
                        path.relativeMoveTo(params[j * 2], params[j * 2 + 1])
                        lastEndP =
                            Offset(lastEndP.x + params[j * 2], lastEndP.y + params[j * 2 + 1])
                    }
                }

                'L' -> {
                    val count = params.size / 2
                    for (j in 0 until count) {
                        path.lineTo(params[j * 2], params[j * 2 + 1])
                        lastEndP = Offset(params[j * 2], params[j * 2 + 1])
                    }
                }

                'l' -> {
                    val count = params.size / 2
                    for (j in 0 until count) {
                        path.relativeLineTo(params[j * 2], params[j * 2 + 1])
                        lastEndP =
                            Offset(lastEndP.x + params[j * 2], lastEndP.y + params[j * 2 + 1])
                    }
                }

                'H' -> {
                    val count = params.size
                    for (j in 0 until count) {
                        path.lineTo(params[j], lastEndP.y)  // 保持当前绘制点的纵坐标不变，横坐标为给定参数
                        lastEndP = Offset(params[j], lastEndP.y)
                    }
                }

                'h' -> {
                    val count = params.size
                    for (j in 0 until count) {
                        path.relativeLineTo(params[j], 0f)
                        lastEndP = Offset(lastEndP.x + params[j], lastEndP.y)
                    }
                }

                'V' -> {
                    val count = params.size
                    for (j in 0 until count) {
                        path.lineTo(lastEndP.x, params[j])  // 保持当前绘制点的横坐标不变，纵坐标为给定参数
                        lastEndP = Offset(lastCSecControlP.x, params[j])
                    }
                }

                'v' -> {
                    val count = params.size
                    for (j in 0 until count) {
                        path.relativeLineTo(0f, params[j])
                        lastEndP = Offset(lastEndP.x, lastEndP.y + params[j])
                    }
                }

                'A' -> {
                    // TODO 椭圆曲线
                    val count = params.size / 7
                    for (j in 0 until count) {
                        path.moveTo(params[j * 7 + 5], params[j * 7 + 6])
                        lastEndP = Offset(params[j * 7 + 5], params[j * 7 + 6])
                    }
                }

                'a' -> {
                    // TODO 椭圆曲线
                    val count = params.size / 7
                    for (j in 0 until count) {
                        path.relativeMoveTo(params[j * 7 + 5], params[j * 7 + 6])
                        lastEndP =
                            Offset(lastEndP.x + params[j * 7 + 5], lastEndP.y + params[j * 7 + 6])
                    }
                }

                'Q' -> {
                    // Q指令接受4n个参数，每四个参数中前两个接收控制点横纵坐标，后两个接受终点横纵坐标
                    val count = params.size / 4
                    for (j in 0 until count) {
                        path.quadraticBezierTo(
                            params[j * 4],
                            params[j * 4 + 1],
                            params[j * 4 + 2],
                            params[j * 4 + 3]
                        )
                        lastQControlP = Offset(params[j * 4], params[j * 4 + 1])
                        lastEndP = Offset(params[j * 4 + 2], params[j * 4 + 3])
                    }
                }

                'q' -> {

                    var startP = lastEndP // 贝塞尔的起点

                    // q指令接受4n个参数，每四个参数中前两个接收控制点相对横纵坐标，后两个接受终点相对横纵坐标
                    val count = params.size / 4
                    for (j in 0 until count) {
                        path.relativeQuadraticBezierTo(
                            params[j * 4],
                            params[j * 4 + 1],
                            params[j * 4 + 2],
                            params[j * 4 + 3]
                        )
                        // 更新为当前绘制的贝塞尔的控制点坐标
                        lastQControlP =
                            Offset(startP.x + params[j * 4], startP.y + params[j * 4 + 1])
                        // 更新为下个贝塞尔的起点坐标
                        startP = Offset(params[j * 4 + 2], params[j * 4 + 3])
                        lastEndP = startP // 本次贝塞尔终点也为下次的起点
                    }
                }

                'T' -> {
                    val preCommand = pathStrings[i - 1].first()  // 上个命令是啥
                    var startP = lastEndP  // 贝塞尔曲线的起点坐标

                    // T指令接收2n个参数。每2个参数表示终点横纵坐标
                    val count = params.size / 2
                    for (j in 0 until count) {
                        // 计算本次控制点
                        // 若上次是二次贝塞尔曲线，控制点为上次曲线的控制点关于当前绘制点的对称点
                        // 若上次不是二次贝塞尔曲线，控制点为当前绘制点
                        val controlP: Offset =
                            if (j == 0 && !(preCommand == 'Q' || preCommand == 'q' || preCommand == 'T' || preCommand == 't')) {
                                startP
                            } else {
                                Offset(
                                    2 * startP.x - lastQControlP.x,
                                    2 * startP.y - lastQControlP.y
                                )
                            }
                        path.quadraticBezierTo(
                            controlP.x,
                            controlP.y,
                            params[0],
                            params[1]
                        ) // 绘制当前贝塞尔
                        lastQControlP = controlP // 更新为当前绘制的贝塞尔的控制点坐标
                        startP = Offset(params[j * 2], params[j * 2 + 1])  // 更新为下个贝塞尔的起点坐标
                        lastEndP = startP
                    }
                }

                't' -> {

                    val preCommand = pathStrings[i - 1].first()  // 上个命令是啥
                    var startP = lastEndP  // 贝塞尔曲线的起点坐标

                    // T指令接收2n个参数。每2个参数表示终点横纵坐标
                    val count = params.size / 2
                    for (j in 0 until count) {
                        // 计算本次控制点
                        // 若上次是二次贝塞尔曲线，控制点为上次曲线的控制点关于当前绘制点的对称点
                        // 若上次不是二次贝塞尔曲线，控制点为当前绘制点
                        val controlP: Offset =
                            if (j == 0 && !(preCommand == 'Q' || preCommand == 'q' || preCommand == 'T' || preCommand == 't')) {
                                startP
                            } else {
                                Offset(
                                    2 * startP.x - lastQControlP.x,
                                    2 * startP.y - lastQControlP.y
                                )
                            }
                        // 绘制当前贝塞尔
                        path.relativeQuadraticBezierTo(
                            controlP.x - startP.x,
                            controlP.y - startP.y,
                            params[0],
                            params[1]
                        )
                        // 更新为当前绘制的贝塞尔的控制点坐标
                        lastQControlP = controlP
                        // 更新为下个贝塞尔的起点坐标
                        startP = Offset(
                            startP.x + params[j * 2],
                            startP.y + params[j * 2 + 1]
                        )
                        lastEndP = startP
                    }
                }

                'C' -> {
                    // C指令接受6n个参数，每6个参数中，前四个参数接收两个控制点的横纵坐标，后两个参数接收终点横纵坐标
                    val count = params.size / 6
                    for (j in 0 until count) {
                        path.cubicTo(
                            params[j * 6],
                            params[j * 6 + 1],
                            params[j * 6 + 2],
                            params[j * 6 + 3],
                            params[j * 6 + 4],
                            params[j * 6 + 5],
                        )
                        lastCSecControlP = Offset(params[j * 6 + 2], params[j * 6 + 3])
                        lastEndP = Offset(params[j * 6 + 4], params[j * 6 + 5])
                    }
                }

                'c' -> {
                    var startP = lastEndP  // 贝塞尔的起点

                    // c指令接受6n个参数，每6个参数中，前四个参数接收两个控制点的相对横纵坐标，后两个参数接收终点相对横纵坐标
                    val count = params.size / 6
                    for (j in 0 until count) {
                        path.relativeCubicTo(
                            params[j * 6],
                            params[j * 6 + 1],
                            params[j * 6 + 2],
                            params[j * 6 + 3],
                            params[j * 6 + 4],
                            params[j * 6 + 5],
                        )
                        // 更新为当前绘制的贝塞尔的第二个控制点
                        lastCSecControlP =
                            Offset(startP.x + params[j * 6 + 2], startP.y + params[j * 6 + 3])
                        // 更新为下个贝塞尔的起点
                        startP = Offset(startP.x + params[j * 6 + 4], startP.y + params[j * 6 + 5])
                        lastEndP = startP
                    }
                }

                'S' -> {
                    val preCommand = pathStrings[i - 1].first() // 上个指令是啥
                    var startP = lastEndP // 贝塞尔的起点坐标

                    // S指令接受4n个参数，每4个参数中，前2个参数接收第二个控制点的横纵坐标，后两个参数接收终点横纵坐标
                    val count = params.size / 4
                    for (j in 0 until count) {
                        // 第一个控制点的坐标为前一次三次贝塞尔曲线绘制命令的第二个控制点关于当前绘制点的对称点
                        // （若前一次不是三次贝塞尔曲线绘制命令，则第一个控制点与当前绘制点相同）
                        val firstControlP =
                            if (j == 0 && !(preCommand == 'C' || preCommand == 'c' || preCommand == 'S' || preCommand == 's')) {
                                startP
                            } else {
                                Offset(
                                    2 * startP.x - lastCSecControlP.x,
                                    2 * startP.y - lastCSecControlP.y
                                )
                            }
                        // 绘制当前贝塞尔
                        path.cubicTo(
                            firstControlP.x,
                            firstControlP.y,
                            params[j * 4],
                            params[j * 4 + 1],
                            params[j * 4 + 2],
                            params[j * 4 + 3]
                        )
                        // 更新为当前贝塞尔的第二个控制点坐标
                        lastCSecControlP = Offset(params[j * 4], params[j * 4 + 1])
                        // 更新为下个贝塞尔的起点坐标
                        startP = Offset(params[j * 4 + 2], params[j * 4 + 3])
                        lastEndP = startP
                    }
                }

                's' -> {
                    val preCommand = pathStrings[i - 1].first() // 上个指令是啥
                    var startP = lastEndP // 贝塞尔的起点坐标

                    // s指令接受4n个参数，每4个参数中，前2个参数接收第二个控制点的相对横纵坐标，后两个参数接收终点相对横纵坐标
                    val count = params.size / 4
                    for (j in 0 until count) {
                        // 计算第一个控制点坐标
                        // 第一个控制点的坐标为前一次三次贝塞尔曲线绘制命令的第二个控制点关于当前绘制点的对称点
                        // （若前一次不是三次贝塞尔曲线绘制命令，则第一个控制点与当前绘制点相同）
                        val firstControlP =
                            if (j == 0 && !(preCommand == 'C' || preCommand == 'c' || preCommand == 'S' || preCommand == 's')) {
                                startP
                            } else {
                                Offset(
                                    2 * startP.x - lastCSecControlP.x,
                                    2 * startP.y - lastCSecControlP.y
                                )
                            }
                        // 绘制当前贝塞尔
                        path.relativeCubicTo(
                            firstControlP.x - startP.x,
                            firstControlP.y - startP.y,
                            params[j * 4],
                            params[j * 4 + 1],
                            params[j * 4 + 2],
                            params[j * 4 + 3]
                        )
                        // 更新为当前贝塞尔的第二个控制点
                        lastCSecControlP =
                            Offset(startP.x + params[j * 4], startP.y + params[j * 4 + 1])
                        // 更新为下一个贝塞尔的起点
                        startP = Offset(startP.x + params[j * 4 + 2], startP.y + params[j * 4 + 3])
                        lastEndP = startP
                    }
                }

                'Z', 'z' -> {
                    path.close()

                    lastQControlP = Offset.Zero
                    lastCSecControlP = Offset.Zero
                    lastEndP = Offset.Zero
                }
            }
        }
        return path
    }
}