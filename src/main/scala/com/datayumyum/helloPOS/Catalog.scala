package com.datayumyum.helloPOS

import scala.util.parsing.json.JSON
import scala.collection.mutable

object Catalog {
  def from(jsonStr: String): mutable.HashMap[String, List[Item]] = {
    val result: Option[Any] = JSON.parseFull(jsonStr)
    val parsedMap: Map[String, List[Map[String, Any]]] = result.get.asInstanceOf[Map[String, List[Map[String, Any]]]]
    val menu: mutable.HashMap[String, List[Item]] = mutable.HashMap.empty[String, List[Item]]
    for ((category, mapList) <- parsedMap) {
      val itemList = mapList.map(m => {
        Item(m("name").asInstanceOf[String], m("imageURL").asInstanceOf[String], m("price").asInstanceOf[Double])
      })
      menu(category) = itemList
    }

    return menu
  }
}
