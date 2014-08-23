package com.datayumyum.helloPOS

case class LineItem(quantity: Int, product: Product, customIngredients: Option[List[Product]])