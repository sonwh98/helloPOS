package com.datayumyum.helloPOS

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
    val url = storeMap("url").asInstanceOf[String]

    val catalogList = storeMap("catalog").asInstanceOf[List[Map[String, Any]]]
    val sandwiches = catalogList(0)
    val categories = catalogList.map { cat: Map[String, Any] =>

    }
    val products = sandwiches("category/products").asInstanceOf[List[Map[String, Any]]]
    new Store(name = name, address = address, phone = phone, url = url, catalog)
  }
}