package jp.leafytree.android.hello

import android.os.Bundle
import android.widget.TextView
import android.support.v4.app.FragmentActivity

class HelloActivity extends FragmentActivity {
  override def onCreate(savedInstanceState: Bundle) {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.activity_hello)
    val scalaTextView = findViewById(R.id.scala_text_view).asInstanceOf[TextView]
    scalaTextView.setText(new HelloJava().say())
  }
}
