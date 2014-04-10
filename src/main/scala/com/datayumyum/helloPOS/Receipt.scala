package com.datayumyum.helloPOS

case class Receipt(store: Store, lineItems: List[(Int, Item)])