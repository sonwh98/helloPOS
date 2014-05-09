package com.datayumyum.helloPOS

object Util {
  def sumLineItems(lineItems: Seq[(Int, Item)]): Double = {
    lineItems.map {
      case (quantity, item) => {
        quantity * item.price
      }
    }.sum
  }

  def calculateTax(taxRate: Double, price: Double): Double = {
    taxRate * price
  }
}