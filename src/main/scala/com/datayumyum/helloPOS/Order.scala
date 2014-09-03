package com.datayumyum.helloPOS

case class Order(store: Store, lineItems: List[LineItem]) {
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
    s"""{:store/uuid \"${store.uuid}\"
         :order/line-items [${lineItems.map{lineItem=>lineItem.toEdn()}.mkString(" ")}] }
    """
  }
}