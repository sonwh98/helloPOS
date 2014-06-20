import EDN._

val lineItems = List("chicken", "fried rice")
val r = Writer.writeAll(lineItems)
println(r)