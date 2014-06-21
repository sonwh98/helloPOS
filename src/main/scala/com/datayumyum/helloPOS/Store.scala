package com.datayumyum.helloPOS

import scala.collection.mutable
import scala.util.parsing.json.JSON

case class Store(name: String, address: Address, phone: String, url: String, catalog: Map[String, List[Item]]) {
  override def toString(): String = {
    name + "\n" + address.toString() + "\n" + phone + "\n" + url
  }
}

object Store {
  def from(jsonStr: String): Store = {
    val result: Option[Any] = JSON.parseFull(jsonStr)
    val storeMap = result.get.asInstanceOf[Map[String, Any]]
    val name = storeMap("store/name").asInstanceOf[String]
    val addressMap = storeMap("address").asInstanceOf[Map[String, String]]
    val address = new Address(line1 = addressMap("address/line1"),
      city = addressMap("address/city"), state = addressMap("address/state"), zip = addressMap("address/zip"))
    val phone = storeMap("phone").asInstanceOf[String]
    val webSite = storeMap("web-site").asInstanceOf[String]

    val catalogList = storeMap("catalog").asInstanceOf[List[Map[String, Any]]]

    var catalog = new mutable.HashMap[String, List[Item]]
    catalogList.foreach { category: Map[String, Any] =>
      val name = category("category/name").asInstanceOf[String]
      val products: List[Map[String, Any]] = category("category/products").asInstanceOf[List[Map[String, Any]]]
      val itemList = products.map { product: Map[String, Any] =>
        val name: String = product("product/name").asInstanceOf[String]
        val sku: String = product("product/sku").asInstanceOf[String]
        val price: Double = product("product/price").asInstanceOf[Double]
        var imageUrl: String = product.get("url").getOrElse("http://www.flaticon.com/png/256/45787.png").asInstanceOf[String]
        Item(name, sku, imageUrl, price)
      }
      catalog(name) = itemList
    }
    new Store(name = name, address = address, phone = phone, url = webSite, catalog.toMap)
  }
}