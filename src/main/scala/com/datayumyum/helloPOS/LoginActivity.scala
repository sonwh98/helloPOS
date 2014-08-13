package com.datayumyum.helloPOS

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import EventHandlers._

class LoginActivity extends Activity {
  val TAG = "com.datayumyum.helloPOS.LoginActivity"

  lazy val loginButton: Button = findViewById(R.id.loginSubmitButton).asInstanceOf[Button]

  override def onCreate(savedInstanceState: Bundle): Unit = {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.login_activity)
    loginButton.onClick { () =>
      Log.i(TAG, s"${loginButton} pressed")
      val intent = new Intent(this, classOf[PosActivity]);
      startActivity(intent)
    }

  }
}