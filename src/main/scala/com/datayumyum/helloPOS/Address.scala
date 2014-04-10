package com.datayumyum.helloPOS

case class Address(line1: String, city: String, state: String, zip: String) {
  override def toString: String = {
    f"${line1}\r\n${city}, ${state} ${zip}"
  }
}