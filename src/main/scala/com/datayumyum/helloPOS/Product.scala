package com.datayumyum.helloPOS

case class Product(uuid:String, name: String, sku: String, imageURL: String, price: Double, ingredients: Option[List[Product]])