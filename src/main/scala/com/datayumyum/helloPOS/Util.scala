package com.datayumyum.helloPOS

import android.os.{Handler, Looper}

object Util {
  def sumLineItems(lineItems: Seq[LineItem]): Double = {
    lineItems.map {
      case lineItem: LineItem => {
        lineItem.quantity * lineItem.product.price
      }
    }.sum
  }

  def calculateTax(taxRate: Double, price: Double): Double = {
    taxRate * price
  }

  lazy val handler = new Handler(Looper.getMainLooper)
  lazy val mainUiThread = Looper.getMainLooper.getThread

  def uiThread(f: => Unit): Unit = {
    if (mainUiThread == Thread.currentThread) {
      f
    } else {
      handler.post(new Runnable() {
        def run() {
          f
        }
      })

    }
  }

  def thread(f: => Unit) = (new Thread(new Runnable() {
    override def run() {
      f
    }
  })).start


}