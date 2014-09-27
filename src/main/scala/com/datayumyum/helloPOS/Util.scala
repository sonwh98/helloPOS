package com.datayumyum.helloPOS

import java.util.concurrent.{LinkedBlockingQueue, ThreadPoolExecutor, TimeUnit}

import android.app.Activity
import android.os.{Handler, Looper}

import scala.concurrent.{ExecutionContext, ExecutionContextExecutor}

object Util {
  implicit lazy val executor: ExecutionContextExecutor = ExecutionContext.fromExecutor(new ThreadPoolExecutor(100, 100, 1000, TimeUnit.SECONDS, new LinkedBlockingQueue[Runnable]))

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

  //view injection like butterknife and dagger without the bloat
  def inject[V](id: Int)(implicit activity: Activity): V = {
    activity.findViewById(id).asInstanceOf[V]
  }

}