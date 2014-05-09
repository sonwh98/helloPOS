package com.datayumyum.helloPOS

import com.starmicronics.stario.{StarIOPortException, StarIOPort, StarPrinterStatus}
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
    val compression = true
    val pageMode = true

    def createHeaderCmd(): Array[Byte] = {
      val header = receipt.store.toString()
      val headerLayout: StaticLayout = new StaticLayout(header, textpaint, paperWidth, Layout.Alignment.ALIGN_CENTER, 1, 0, false)
      val headerBitmap: Bitmap = Bitmap.createBitmap(headerLayout.getWidth, headerLayout.getHeight, Bitmap.Config.RGB_565)
      val c: Canvas = new Canvas(headerBitmap)
      c.drawColor(Color.WHITE)
      c.translate(0, 0)
      c.drawLine(0.0f, headerLayout.getHeight, headerLayout.getWidth, headerLayout.getHeight, textpaint)
      headerLayout.draw(c)
      val starbitmap = new StarBitmap(headerBitmap, false, paperWidth)
      starbitmap.getImageEscPosDataForPrinting(compression, pageMode)
    }

    def createBodyCmd(): Array[Byte] = {
      val body = receipt.lineItems.map {
        case (quantity, item) => {
          f"$quantity%s ${item.name} ${item.price * quantity}"
        }
      }.mkString("\n")

      //      val bodyLayout: StaticLayout = new StaticLayout(body, textpaint, paperWidth, Layout.Alignment.ALIGN_NORMAL, 1, 0, false)
      val paperHeight = receipt.lineItems.size * textSize
      val bodyBitMap = Bitmap.createBitmap(paperWidth, paperHeight, Bitmap.Config.RGB_565)
      val canvas = new Canvas(bodyBitMap)
      canvas.drawColor(Color.WHITE)
      canvas.translate(0, 0)
      var x = 0.0f
      var y = 0.0f
      receipt.lineItems.foreach {
        lineItem =>
          val quantity: Int = lineItem._1
          val item: Item = lineItem._2
          val subTotal = item.price * quantity

          val quantityStr: String = quantity.toString
          canvas.drawText(quantityStr, x, y, textpaint)

          val padding = 2
          x = x + textpaint.measureText(quantityStr) + padding
          canvas.drawText(item.name, x, y, textpaint)

          val subTotalStr: String = "%.2f".format(subTotal)
          x = paperWidth - textpaint.measureText(subTotalStr)
          canvas.drawText(subTotalStr, x, y, textpaint)

          y = y + textSize
          x = 0
      }

      val starBodyBitmap = new StarBitmap(bodyBitMap, false, paperWidth)
      starBodyBitmap.getImageEscPosDataForPrinting(compression, pageMode)
    }

    val headerCmd = createHeaderCmd()
    val bodyCmd = createBodyCmd()
    val NEW_LINES: Array[Byte] = "\n\n\n\n".getBytes()
    sendCommand(headerCmd ++ bodyCmd ++ NEW_LINES)
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