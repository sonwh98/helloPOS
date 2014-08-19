package com.datayumyum.helloPOS

import android.os.{Handler, Looper}

object Util {
  def sumLineItems(lineItems: Seq[(Int, Product)]): Double = {
    lineItems.map {
      case (quantity, item) => {
        quantity * item.price
      }
    }.sum
  }

  def calculateTax(taxRate: Double, price: Double): Double = {
    taxRate * price
  }

  lazy val handler = new Handler(Looper.getMainLooper)
  lazy val mainUiThread = Looper.getMainLooper.getThread

  def uiThread[T >: Null](f: => T): T = {
    if (mainUiThread == Thread.currentThread) {
      return f
    } else {
      handler.post(new Runnable() {
        def run() {
          f
        }
      })
      return null
    }
  }

  def thread(f: => Unit) = (new Thread(new Runnable() {
    override def run() {
      f
    }
  })).start


}