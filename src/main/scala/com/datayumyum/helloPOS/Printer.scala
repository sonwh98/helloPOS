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
    paint.setStyle(Paint.Style.STROKE)
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
      headerLayout.draw(c)
      c.drawRect(0f, 0f, paperWidth, headerLayout.getHeight, textpaint)
      val starbitmap = new StarBitmap(headerBitmap, false, paperWidth)
      starbitmap.getImageEscPosDataForPrinting(compression, pageMode)
    }

    def createBodyCmd(): Array[Byte] = {
      val paperHeight = receipt.lineItems.size * textSize
      val bodyBitMap = Bitmap.createBitmap(paperWidth, paperHeight, Bitmap.Config.RGB_565)
      val canvas = new Canvas(bodyBitMap)
      canvas.drawColor(Color.WHITE)
      canvas.drawRect(0.0f, 0.0f, paperWidth, paperHeight, textpaint)
      var x = 0.0f
      var y = textSize
      receipt.lineItems.foreach {
        lineItem =>
          val quantity: Int = lineItem.quantity
          val product: Product = lineItem.product
          val subTotal = product.price * quantity

          val quantityStr: String = quantity.toString
          canvas.drawText(quantityStr, x, y, textpaint)

          val padding = 2
          x = x + textpaint.measureText(quantityStr) + padding
          canvas.drawText(product.name, x, y, textpaint)

          val subTotalStr: String = "%.2f".format(subTotal)
          x = paperWidth - textpaint.measureText(subTotalStr)
          canvas.drawText(subTotalStr, x, y, textpaint)

          y = y + textSize
          x = 0
      }

      val starBodyBitmap = new StarBitmap(bodyBitMap, false, paperWidth)
      starBodyBitmap.getImageEscPosDataForPrinting(compression, pageMode)
    }

    def createFooterCmd(): Array[Byte] = {
      val lines = List("Sub Total", "Tax 8%", "Total", "Tender", "Change")
      val bitmapHeight = lines.size * textSize
      val footerBitMap = Bitmap.createBitmap(paperWidth, bitmapHeight, Bitmap.Config.RGB_565)
      val canvas = new Canvas(footerBitMap)
      canvas.drawColor(Color.WHITE)
      canvas.translate(0, 0)

      val subTotalStr: String = "Sub Total %.2f".format(receipt.subTotal)
      val taxAmountStr: String = "%.2f".format(receipt.tax)
      val totalStr: String = "Total %.2f".format(receipt.total)
      val subTotalStrSize: Float = textpaint.measureText(subTotalStr)
      val subTotalTextSize: Float = textpaint.measureText("Sub Total")
      var x = paperWidth - subTotalStrSize
      var y = textSize
      canvas.drawText(subTotalStr, x, y, textpaint)

      y = y + textSize
      x = (paperWidth - subTotalStrSize) + subTotalTextSize - textpaint.measureText("Tax %8")
      canvas.drawText("Tax %8", x, y, textpaint)

      x = paperWidth - textpaint.measureText(taxAmountStr)
      canvas.drawText(taxAmountStr, x, y, textpaint)

      x = paperWidth - textpaint.measureText(totalStr)
      y = y + textSize
      canvas.drawText(totalStr, x, y, textpaint)
      val starBitmap = new StarBitmap(footerBitMap, false, paperWidth)
      starBitmap.getImageEscPosDataForPrinting(compression, pageMode)
    }
    val headerCmd = createHeaderCmd()
    val bodyCmd = createBodyCmd()
    val footerCmd = createFooterCmd()
    val NEW_LINES: Array[Byte] = "\n\n".getBytes()
    sendCommand(headerCmd ++ bodyCmd ++ footerCmd ++ NEW_LINES)
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