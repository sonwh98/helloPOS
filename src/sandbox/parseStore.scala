import java.net.URL
import java.util

import com.datayumyum.helloPOS.Item

import scala.collection.mutable
import scala.io.Source
import scala.util.parsing.json.JSON

val storeJson = Source.fromInputStream(new URL("http://hive.kaicode.com:3000/pos/store/17592186045418").openStream).mkString
val result: Option[Any] = JSON.parseFull(storeJson)
val store = result.get.asInstanceOf[Map[String, Any]]
val address = store("address").asInstanceOf[Map[String, String]]
val catalog = store("catalog").asInstanceOf[List[Map[String, Any]]]

var categories = new mutable.HashMap[String, List[Item]]
catalog.foreach { category: Map[String, Any] =>
  val name = category("category/name").asInstanceOf[String]
  val products: List[Map[String, Any]] = category("category/products").asInstanceOf[List[Map[String, Any]]]
  val itemList = products.map { product: Map[String, Any] =>
    val name: String = product("product/name").asInstanceOf[String]
    val sku: String = product("product/sku").asInstanceOf[String]
    val url: String = product.get("url").getOrElse("http://www.flaticon.com/png/256/45787.png").asInstanceOf[String]
    val price: Double = product("product/price").asInstanceOf[Double]
    Item(name, sku, url, price)
  }
  categories(name) = itemList
}

println(categories)
val sandwiches = catalog(0)
val products = sandwiches("category/products").asInstanceOf[List[Map[String, Any]]]
//println(sandwiches("category/name"))
//println(products)
val stuff = products.map { product: Map[String, Any] =>
  val name = product("product/name").asInstanceOf[String]
  val price = product("product/price").asInstanceOf[Double]
  val sku = product("product/sku").asInstanceOf[String]
  (name, sku, price)
}

println(address)