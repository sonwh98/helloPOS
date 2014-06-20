import java.net.URL

import scala.io.Source
import scala.util.parsing.json.JSON

val storeJson = Source.fromInputStream(new URL("http://hive.kaicode.com:3000/pos/store/17592186045418").openStream).mkString
val result: Option[Any] = JSON.parseFull(storeJson)
val store = result.get.asInstanceOf[Map[String, Any]]
val address = store("address").asInstanceOf[Map[String, String]]
val catalog = store("catalog").asInstanceOf[List[Map[String, Any]]]
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

println(stuff)