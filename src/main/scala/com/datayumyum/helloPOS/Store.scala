package com.datayumyum.helloPOS

import org.json._

case class Store(name: String, address: Address, phone: String, url: String, catalog: Map[String, List[Product]]) {
  override def toString(): String = {
    name + "\n" + address.toString() + "\n" + phone + "\n" + url
  }
}

object Store {
  def from(jsonStr: String): Store = {
    val jsonObject = new JSONObject(jsonStr)

    val products = jsonObject.getJSONArray("products")
    val catalog = jsonObject.getJSONArray("catalog")

    def getUrl(p: JSONObject): String = {
      var url: String = "http://www.com"
      try {
        url = p.getString("url")
      } catch {
        case e: JSONException => url = "http://www.flaticon.com/png/256/45787.png"
      }
      url
    }

    val pp = (0 until products.length()).map { index =>
      val p = products.getJSONObject(index)
      Product(name = p.getString("product/name"), sku = p.getString("product/sku"), imageURL = getUrl(p), price = p.getDouble("product/price"))
    }

    var myCatalog = collection.mutable.Map[String, List[Product]]()
    (0 until catalog.length()).foreach { index =>
      val category = catalog.getJSONObject(index)
      val catName = category.getString("category/name")
      val products = category.getJSONArray("products")
      val productList = (0 until products.length()).map { index =>
        val p = products.getJSONObject(index)

        Product(name = p.getString("product/name"),
          sku = p.getString("product/sku"),
          imageURL = getUrl(p),
          price = p.getDouble("product/price"))
      }.toList

      myCatalog(catName) = productList.sortWith{ (item1, item2)=> item1.sku<item2.sku }
    }

    val addr = jsonObject.getJSONObject("address")
    val address = new Address(line1 = addr.getString("address/line1"), city = addr.getString("address/city"), state = addr.getString("address/state"), zip = addr.getString("address/zip"))
    val store = Store(name = jsonObject.getString("store/name"),
      address = address,
      phone = jsonObject.getString("phone"),
      url = jsonObject.getString("url"),
      catalog = myCatalog.toMap)
    store
  }
}
