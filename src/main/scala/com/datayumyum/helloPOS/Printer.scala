package com.datayumyum.helloPOS

import com.starmicronics.stario.{StarIOPortException, StarIOPort, StarPrinterStatus}
import java.text.SimpleDateFormat
import java.util.Date
import android.util.Log

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
    val SET_WIDTH: Array[Byte] = Array(0x1d, 0x57, 0x40, 0x32)
    val ALIGN_CENTER: Array[Byte] = Array(0x1b, 0x61, 0x01)
    val ALIGN_LEFT: Array[Byte] = Array(0x1b, 0x61, 0x00)
    val HORIZONTAL_TAB: Array[Byte] = Array(0x1b, 0x44, 0x02, 0x10, 0x22, 0x00)
    val NEW_LINE: Array[Byte] = "\n\n\n\n\n".getBytes()

    val header: Array[Byte] = (receipt.store.toString() + "\n").getBytes()
    val body: Array[Byte] = receipt.lineItems.map {
      case (quantity, item) => {
        f"$quantity%s ${item.name} ${item.price * quantity}"
      }
    }.mkString("\n").getBytes()
    val chinese = "小马词典".getBytes("UTF-8")
    val cmd: Array[Byte] = SET_WIDTH ++ ALIGN_CENTER ++ header ++ ALIGN_LEFT ++ HORIZONTAL_TAB ++ body ++ chinese ++ NEW_LINE
    sendCommand(cmd)
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