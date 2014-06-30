package com.datayumyum.helloPOS

case class Receipt(store: Store, lineItems: List[(Int, Product)]) {
  def subTotal: Double = {
    Util.sumLineItems(lineItems)
  }

  def tax: Double = {
    Util.calculateTax(0.08, subTotal)
  }

  def total: Double = {
    subTotal + tax
  }

  def toEdn(): String = {
    store.name
  }
}