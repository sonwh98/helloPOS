package com.datayumyum.helloPOS

case class LineItem(quantity: Int, product: Product, var customIngredients: Option[List[Product]]) {
  def toEdn(): String = {
    s"""{:quantity ${quantity}
      | :product/uuid #uuid \"${product.uuid}\"
      | :line-item/customizations [${customIngredients.getOrElse(List()).map { product => f"""#uuid \"${product.uuid}\"""" }.mkString(" ")}]}
      |
    """.stripMargin
  }
}