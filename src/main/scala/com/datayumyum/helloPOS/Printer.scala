package com.datayumyum.helloPOS

import com.starmicronics.stario.{StarIOPortException, StarIOPort, StarPrinterStatus}
import java.text.SimpleDateFormat
import java.util.Date
import android.util.Log
import android.text.{TextPaint, Layout, StaticLayout}
import android.graphics._
import com.StarMicronics.StarIOSDK.StarBitmap

object Printer {
  val TAG = "com.datayumyum.pos.Printer"

  var port: StarIOPort = null

  def print(receipt: Receipt) {
    val paint: Paint = new Paint
    paint.setStyle(Paint.Style.FILL)
    paint.setColor(Color.BLACK)
    paint.setAntiAlias(true)
    var style: Int = 0
    var font: Typeface = Typeface.DEFAULT
    val typeface: Typeface = Typeface.create(font, style)
    paint.setTypeface(typeface)
    val textSize = 30
    val paperWidth = 576
    paint.setTextSize(textSize)
    val textpaint: TextPaint = new TextPaint(paint)

    val header = receipt.store.toString()
    val body = receipt.lineItems.map {
      case (quantity, item) => {
        f"$quantity%s ${item.name} ${item.price * quantity}"
      }
    }.mkString("\n")
    val chinese = "小马词典"
    val headerLayout: StaticLayout = new StaticLayout(header, textpaint, paperWidth, Layout.Alignment.ALIGN_CENTER, 1, 0, false)
    val headerBitmap: Bitmap = Bitmap.createBitmap(headerLayout.getWidth, headerLayout.getHeight, Bitmap.Config.RGB_565)
    val c: Canvas = new Canvas(headerBitmap)
    c.drawColor(Color.WHITE)
    c.translate(0, 0)
    c.drawLine(0.0f, headerLayout.getHeight, headerLayout.getWidth, headerLayout.getHeight, textpaint)
    headerLayout.draw(c)

    val starbitmap = new StarBitmap(headerBitmap, false, paperWidth)
    val compression = true
    val pageMode = true
    val hearderCmd = starbitmap.getImageEscPosDataForPrinting(compression, pageMode)


    val bodyLayout: StaticLayout = new StaticLayout(body, textpaint, paperWidth, Layout.Alignment.ALIGN_NORMAL, 1, 0, false)
    val bodyBitMap = Bitmap.createBitmap(bodyLayout.getWidth, bodyLayout.getHeight, Bitmap.Config.RGB_565)
    val c1 = new Canvas(bodyBitMap)
    c1.drawColor(Color.WHITE)
    c1.translate(0, 0)
    bodyLayout.draw(c1)
    val starBodyBitmap = new StarBitmap(bodyBitMap, false, paperWidth)
    val bodyCmd = starBodyBitmap.getImageEscPosDataForPrinting(compression, pageMode)
    val NEW_LINES: Array[Byte] = "\n\n\n\n".getBytes()

    sendCommand(hearderCmd ++ bodyCmd ++ NEW_LINES)
  }

  def sendCommand(cmd: Array[Byte]) {
    val status: StarPrinterStatus = getPort.beginCheckedBlock()
    port.writePort(cmd, 0, cmd.length)
    val status2 = getPort.endCheckedBlock()
  }


  def getPort: StarIOPort = {
    if (port == null) {
      try {
        val settings = "mini"
        port = StarIOPort.getPort("BT:Star Micronics", settings, 1000)
      } catch {
        case ex: StarIOPortException => {
          val settings = ""
          port = StarIOPort.getPort("BT:Star Micronics", settings, 1000)
        }
      }
    }
    port
  }
}