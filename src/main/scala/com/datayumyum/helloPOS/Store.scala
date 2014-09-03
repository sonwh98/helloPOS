package com.datayumyum.helloPOS

import java.net.URL

import org.json._

import scala.io.Source

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

    var myCatalog = collection.mutable.Map[String, List[Product]]()
    (0 until catalog.length()).foreach { index =>
      val category = catalog.getJSONObject(index)
      val catName = category.getString("category/name")
      val products = category.getJSONArray("products")
      val productList = (0 until products.length()).map { index =>
        val p = products.getJSONObject(index)
        if (p.has("product/components")) {
          val components = p.getJSONArray("product/components")
          val ingredients = (0 until components.length()).map { index =>
            val c = components.getJSONObject(index)
            Product(uuid = c.getString("product/uuid"), name = c.getString("product/name"),
              sku = c.getString("product/sku"),
              imageURL = getUrl(c),
              price = c.getDouble("product/price"), ingredients = None)
          }.toList

          Product(uuid = p.getString("product/uuid"), name = p.getString("product/name"),
            sku = p.getString("product/sku"),
            imageURL = getUrl(p),
            price = p.getDouble("product/price"), ingredients = Some(ingredients))
        } else {
          Product(uuid = p.getString("product/uuid"), name = p.getString("product/name"),
            sku = p.getString("product/sku"),
            imageURL = getUrl(p),
            price = p.getDouble("product/price"), ingredients = None)
        }
      }.toList

      myCatalog(catName) = productList.sortWith { (item1, item2) => item1.sku < item2.sku}
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

  def findById(id: String): Store = {
    val storeJsonStr: String = Source.fromInputStream(new URL(s"http://localhost:3000/pos/store/${id}").openStream).mkString
    Store.from(storeJsonStr)
  }
}
