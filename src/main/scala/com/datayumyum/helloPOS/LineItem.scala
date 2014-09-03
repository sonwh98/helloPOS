package com.datayumyum.helloPOS

case class LineItem(quantity: Int, product: Product, var customIngredients: Option[List[Product]]) {
  def toEdn(): String = {
    s"""{:quantity ${quantity}
      | :product/uuid ${product.uuid}
      | :line-item/component-ids [${customIngredients.getOrElse(List()).map { product => f"""\"${product.uuid}\"""" }.mkString(" ")}]
      |
    """.stripMargin
  }
}