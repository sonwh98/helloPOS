package com.datayumyum.helloPOS

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

  def thread(f: => Unit) = (new Thread(new Runnable() {
    override def run() {
      f
    }
  })).start


}