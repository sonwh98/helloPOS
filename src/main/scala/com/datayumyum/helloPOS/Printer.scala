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

  //  def print(receipt: Receipt) {
  //    val CUT_PAPER: Array[Byte] = Array(0x1b, 0x64, 0x02)
  //    val ALIGN_CENTER: Array[Byte] = Array(0x1b, 0x1d, 0x61, 0x01)
  //    val ALIGN_LEFT: Array[Byte] = Array(0x1b, 0x1d, 0x61, 0x00)
  //    val ALIGN_RIGHT: Array[Byte] = Array(0x1b, 0x1d, 0x61, 0x02)
  //    val HORIZONTAL_TAB: Array[Byte] = Array(0x1b, 0x44, 0x02, 0x10, 0x22, 0x00)
  //    val MOVE_HORIZONTAL_TAB: Array[Byte] = Array(' ', 0x09, ' ')
  //    val BOLD_ON: Array[Byte] = Array(0x1b, 0x45)
  //    val BOLD_OFF: Array[Byte] = Array(0x1b, 0x46)
  //
  //    def createHeader(): Array[Byte] = {
  //      val store = ALIGN_CENTER ++ (receipt.store.toString() + "\n").getBytes()
  //      val now: Date = new Date()
  //      val date = "Date: " + new SimpleDateFormat("MM-dd-yyy").format(now)
  //      val time = "Time: " + new SimpleDateFormat("hh:mma\n").format(now)
  //      val line = "------------------------------------------------\n"
  //      store ++ ALIGN_LEFT ++ HORIZONTAL_TAB ++ date.getBytes() ++ MOVE_HORIZONTAL_TAB ++ time.getBytes() ++ line.getBytes()
  //    }
  //
  //    val header: Array[Byte] = createHeader()
  //    val body: Array[Byte] = receipt.lineItems.map {
  //      case (quantity, item) => {
  //        f"$quantity%s ${item.name} ${item.price * quantity}"
  //      }
  //    }.mkString("\n").getBytes()
  //
  //    val cmd = header ++ "\n\n".getBytes() ++ ALIGN_LEFT ++ body ++ CUT_PAPER
  //    sendCommand(cmd)
  //  }

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