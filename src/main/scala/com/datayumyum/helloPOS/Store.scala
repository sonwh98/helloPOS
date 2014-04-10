package com.datayumyum.helloPOS

case class Store(name: String, address: Address, phone: String, url: String) {
  override def toString(): String = {
    name + "\n" + address.toString() + "\n" + phone + "\n" + url
  }
}