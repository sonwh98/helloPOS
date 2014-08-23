package com.datayumyum.helloPOS

case class LineItem(quantity: Int, product: Product, var customIngredients: Option[List[Product]])