package com.datayumyum.helloPOS

import java.text.NumberFormat
import java.util.Locale

import android.animation.ValueAnimator.AnimatorUpdateListener
import android.animation.{ArgbEvaluator, ValueAnimator}
import android.app.{Activity, AlertDialog}
import android.content.{Intent, DialogInterface}
import android.content.DialogInterface.OnMultiChoiceClickListener
import android.os.Bundle
import android.util.Log
import android.view.ViewGroup.LayoutParams
import android.view._
import android.widget._
import com.datayumyum.helloPOS.EventHandlers._
import com.datayumyum.helloPOS.Util.{thread, uiThread}

import scala.collection.mutable
import com.datayumyum.helloPOS.Util.inject

class PosActivity extends Activity {
  val TAG = "com.datayumyum.pos.PosActivity"

  val currencyFormat = NumberFormat.getCurrencyInstance(Locale.US)
  lazy val userEmail = getIntent().getExtras().getString(Intent.EXTRA_EMAIL)
  lazy val store = Store.findById("17592186045418")

  implicit val activity = PosActivity.this
  lazy val gridView: GridView = inject(R.id.gridview)
  lazy val lineItemListView: ListView = inject(R.id.lineItemListView)

  override def onCreate(savedInstanceState: Bundle): Unit = {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.main_activity)

    def configureCategories() {
      val catalog: Map[String, List[Product]] = store.catalog
      val gridAdapters: Map[String, GridAdapter] = catalog.map { entry =>
        val categoryName: String = entry._1
        val itemInCategory: List[Product] = entry._2
        (categoryName, new GridAdapter(itemInCategory))
      }

      uiThread {
        gridView.setAdapter(gridAdapters("Sandwiches"))

        val categoryContainer: LinearLayout = inject(R.id.categoryContainer)
        catalog.keySet.toList.sortWith { (a: String, b: String) => a.compareTo(b) < 0}.foreach((category: String) => {
          val categoryButton: Button = new Button(getApplicationContext())
          categoryButton.setText(category)
          categoryButton.setLayoutParams(new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT, 100))
          categoryButton.onClick {
            gridView.setAdapter(gridAdapters(category))
          }
          categoryContainer.addView(categoryButton)
        })
      }
    }
    def configureLineItemView() {
      lineItemListView.setAdapter(ShoppingCart)

      lineItemListView.onDismiss { reverseSortedPositions: Array[Int] =>
        for (position <- reverseSortedPositions) {
          ShoppingCart.remove(position)
        }
      }
      lineItemListView.onItemLongClick { (position: Int) => {
        val lineItem: LineItem = ShoppingCart.lineItems(position)
        val quantity: Int = lineItem.quantity
        val product: Product = lineItem.product

        val builder: AlertDialog.Builder = new AlertDialog.Builder(PosActivity.this)
        product.ingredients match {
          case Some(ingredientList: List[Product]) => {
            val ingredientListAsString: Array[CharSequence] = ingredientList.map { p: Product => p.name}.toArray[CharSequence]

            val checkedItems = ingredientList.map { product =>
              lineItem.customIngredients match {
                case Some(customIngredients: List[Product]) => customIngredients.contains(product)
                case None => false
              }
            }.toArray[Boolean]

            val ingredientSelectionListener = new OnMultiChoiceClickListener {
              var ingredientSelected: List[Product] = lineItem.customIngredients.getOrElse(List())

              override def onClick(dialogInterface: DialogInterface, indexSelected: Int, isChecked: Boolean) {
                val ingredient = ingredientList(indexSelected)
                if (isChecked) {
                  ingredientSelected = ingredientSelected ++ List(ingredient)
                } else {
                  ingredientSelected = ingredientSelected.filter { product => product != ingredient}
                }
              }
            }
            builder.setMultiChoiceItems(ingredientListAsString, checkedItems, ingredientSelectionListener)
            builder.setTitle(f"Ingredient Customization for ${product.name}").setPositiveButton("OK", new DialogInterface.OnClickListener() {
              override def onClick(dialog: DialogInterface, which: Int): Unit = {
                val selected: List[Product] = ingredientSelectionListener.ingredientSelected
                if (!selected.isEmpty) {
                  lineItem.customIngredients = Some(selected.toList)
                } else {
                  lineItem.customIngredients = None
                }
                ShoppingCart.notifyDataSetChanged()
              }
            })


            builder.create().show()
          }
          case None => Log.d(TAG, s"{$product.name} has no ingredients")
        }
      }
      }
    }

    def configureNumberPad() {
      val buttonIdList = List(R.id.button0, R.id.button1, R.id.button2, R.id.button3, R.id.button4, R.id.button5, R.id.button6, R.id.button7,
        R.id.button8, R.id.button9, R.id.decimalButton)
      buttonIdList.foreach { id =>
        val button: Button = inject(id)
        button.onClick {
          Accumulator.push(button.getText().toString())
        }
      }
      val allButtons = buttonIdList ++ List(R.id.clearButton, R.id.cashButton, R.id.creditButton)
      allButtons.foreach { id =>
        val button: Button = inject(id)
        button.setPadding(50, 50, 50, 50)
      }

      findViewById(R.id.clearButton).onClick {
        Accumulator.reset()
      }

      val submitOrder = () => {
        val tender: Double = Accumulator.pop()
        Log.i(TAG, "submitOrder cashTender: " + tender.toString)
        Accumulator.reset()
        findViewById(R.id.tender).asInstanceOf[TextView].setText(currencyFormat.format(tender))

        val change: Double = tender - ShoppingCart.calculateTotal()
        findViewById(R.id.change).asInstanceOf[TextView].setText(currencyFormat.format(change))
        ShoppingCart.checkout()
      }

      val cashButton: Button = inject(R.id.cashButton)
      val creditButton: Button = inject(R.id.creditButton)

      cashButton.onClick {
        submitOrder()
      }

      creditButton.onClick {
        submitOrder()
      }
    }
    thread {
      configureCategories()
    }
    configureLineItemView()
    configureNumberPad()
  }

  class GridAdapter(products: List[Product]) extends BaseAdapter {
    val itemButtonList: List[View] = products.map((item: Product) => {
      val inflater: LayoutInflater = getLayoutInflater()
      val itemButton: View = inflater.inflate(R.layout.item_button, null)
      val imageButton: ImageButton = itemButton.findViewById(R.id.item_image_button).asInstanceOf[ImageButton]
      val itemLabel: TextView = itemButton.findViewById(R.id.item_label).asInstanceOf[TextView]

      new DownloadImageTask(imageButton).execute(item.imageURL)
      val itemClickHandler = (event: MotionEvent) => {
        val actionType = event.getAction
        actionType match {
          case MotionEvent.ACTION_DOWN => {
            itemButton.setAlpha(0.5f)
            val quantity = Accumulator.pop().asInstanceOf[Int]
            if (quantity == 0) {
              ShoppingCart.add(item)
            } else {
              ShoppingCart.add(item, quantity)
            }
          }
          case MotionEvent.ACTION_MOVE => {
            def isInside(): Boolean = {
              event.getX() > 0 && event.getX() < itemButton.getWidth() && event.getY() > 0 && event.getY() < itemButton.getHeight()
            }
            if (isInside()) {
              itemButton.setAlpha(0.5f)
            } else {
              itemButton.setAlpha(1.0f)
            }
          }
          case _ => itemButton.setAlpha(1.0f)
        }
      }
      imageButton.onTouch(itemClickHandler)
      itemLabel.onTouch(itemClickHandler)

      itemLabel.setText(item.name)
      itemButton
    })

    override def getCount: Int = {
      return itemButtonList.size
    }

    override def getItem(position: Int): Object = {
      return null
    }

    override def getItemId(position: Int): Long = {
      return 0
    }

    override def getView(position: Int, convertView: View, parent: ViewGroup): View = {
      val imageView: View = itemButtonList(position)
      return imageView
    }
  }

  object ShoppingCart extends BaseAdapter {
    val lineItems = new mutable.ArrayBuffer[LineItem]()
    val lineItemViews = mutable.MutableList.empty[View]
    val lineItemListView = findViewById(R.id.lineItemListView).asInstanceOf[ListView]
    val TAG = "com.datayumyum.pos.ShoppingCart"
    val inflater: LayoutInflater = getLayoutInflater()
    var reset: Boolean = true

    override def getCount: Int = {
      return lineItems.size
    }

    override def getItem(position: Int): Object = {
      return lineItems(position)
    }

    override def getItemId(position: Int): Long = {
      return 0
    }

    override def getView(position: Int, convertView: View, parent: ViewGroup): View = {
      var view = convertView
      if (view == null) {
        view = inflater.inflate(R.layout.line_item, null);
        val quantityTextView = view.findViewById(R.id.QUANTITY_CELL)
        val descriptionTextView = view.findViewById(R.id.DESCRIPTION_CELL)
        val priceTextView = view.findViewById(R.id.PRICE_CELL)
        val subTotalTextView = view.findViewById(R.id.SUB_TOTAL_CELL)
        view.setTag((quantityTextView, descriptionTextView, priceTextView, subTotalTextView))
      }

      if (position > lineItemViews.size - 1) {
        lineItemViews += view
      } else {
        lineItemViews(position) = view
      }


      val (quantityTextView: TextView, descriptionTextView: TextView, priceTextView: TextView, subTotalTextView: TextView) = view.getTag()
      val lineItem: LineItem = lineItems(position)
      val quantity = lineItem.quantity
      val product = lineItem.product
      quantityTextView.setText(quantity.toString.trim())

      val descriptionStr = lineItem.customIngredients match {
        case Some(customIngredients: List[Product]) => product.name + "\n+" + customIngredients.map {
          _.name
        }.reduce { (acc: String, name: String) => acc + "\n+" + name}
        case None => product.name
      }

      descriptionTextView.setText(descriptionStr.trim())
      priceTextView.setText(currencyFormat.format(product.price).trim())
      val subTotal = quantity * product.price
      subTotalTextView.setText(currencyFormat.format(subTotal).trim())

      return view
    }

    def add(product: Product, quantity: Int = 1) {
      lineItems.append(new LineItem(quantity, product, None))
      displayTotals()
      notifyDataSetChanged()
    }

    def calculateSubTotal(): Double = {
      Util.sumLineItems(lineItems)
    }

    def calculateTax(): Double = {
      val taxRate = 0.08
      Util.calculateTax(taxRate, calculateSubTotal())
    }

    def calculateTotal(): Double = {
      calculateSubTotal() + calculateTax()
    }

    def displayTotals() {
      findViewById(R.id.subTotal).asInstanceOf[TextView].setText(currencyFormat.format(calculateSubTotal()))
      findViewById(R.id.tax).asInstanceOf[TextView].setText(currencyFormat.format(calculateTax()))
      findViewById(R.id.total).asInstanceOf[TextView].setText(currencyFormat.format(calculateTotal()))
    }

    def animateView(view: View) {
      if (view != null) {
        val colorFrom: java.lang.Integer = getResources().getColor(R.color.wild_blue)
        val colorTo: java.lang.Integer = getResources().getColor(R.color.secondPanel)
        val colorAnimation: ValueAnimator = ValueAnimator.ofObject(new ArgbEvaluator(), colorFrom, colorTo).asInstanceOf[ValueAnimator]
        colorAnimation.addUpdateListener(new AnimatorUpdateListener() {
          override def onAnimationUpdate(animator: ValueAnimator) {
            view.setBackgroundColor(animator.getAnimatedValue().asInstanceOf[Int])
          }

        })
        colorAnimation.start()
      }
    }

    def remove(position: Int) {
      lineItems.remove(position)
      displayTotals()

      //TODO investigate why notifyDataSetChanged() does not redraw the lineItems properly
      //      notifyDataSetChanged()
      lineItemListView.setAdapter(ShoppingCart) //This draws the lineItem properly after its been removed
    }

    def checkout() {
      thread {
        try {
          val order: Order = Order(store, lineItems.toList)
          OrderMessenger.sendOrder(order.toEdn())
          Printer.print(order)
          uiThread {
            clear()
          }
        } catch {
          case ex: Exception => uiThread {
            Toast.makeText(PosActivity.this, f"printer not available ${ex.getMessage}", Toast.LENGTH_LONG).show()
          }
        }
      }
    }

    def clear() {
      lineItems.clear()
      lineItemViews.clear()
      findViewById(R.id.tender).asInstanceOf[TextView].setText("")
      findViewById(R.id.change).asInstanceOf[TextView].setText("")
      displayTotals()
      notifyDataSetChanged()
    }
  }

  object Accumulator {
    var value: String = ""
    val display: TextView = findViewById(R.id.accumulatorDisplay).asInstanceOf[TextView]

    def push(data: String) {
      value = value + data
      display.setText(value)
    }

    def pop(): Double = {
      try {
        if (value.length > 0) {
          return value.toDouble
        } else {
          return 0
        }
      } finally {
        reset
      }
    }

    def reset() {
      value = ""
      display.setText(value)
    }
  }

}

