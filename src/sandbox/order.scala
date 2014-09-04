import com.datayumyum.helloPOS._

val store = Store.findById("17592186045418")


val sandwiches = store.catalog("Sandwiches")
val s1 = sandwiches(0)
val i0 = s1.ingredients(0)
val i1 = s1.ingredients(1)
val i2 = s1.ingredients(2)
val lineItem1 = LineItem(1, s1, List(i0, i1, i2))
val order = Order(store, List(lineItem1))
OrderMessenger.sendOrder(order.toEdn())