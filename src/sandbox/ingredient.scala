import com.datayumyum.helloPOS._

val nut = new Product("Walnut", "123", "http://www.image.com", 1.00, None)
val icecream = new Product("Ice-cream", "123", "http://www.image.com", 1.00, None)
val banana = new Product("Banana", "123", "http://www.image.com", 1.00, None)
val berry = new Product("Blue Berry", "123", "http://www.image.com", 1.00, None)
val bananaSplit = new Product("Banana Split", "123", "http://www.image.com", 1.00,
  Some(List(nut, icecream, banana)))

val lineItem = new LineItem(quantity = 1, product = bananaSplit, customIngredients = Some(List(nut, icecream, berry)))


lineItem.customIngredients.get.map{_.name}.reduce{ (acc,name)=>acc+"\n+"+name}