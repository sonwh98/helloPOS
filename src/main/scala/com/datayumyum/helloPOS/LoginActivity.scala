package com.datayumyum.helloPOS

import java.net.URL
import java.util.concurrent.{ThreadPoolExecutor, TimeUnit, LinkedBlockingQueue}

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.{EditText, Toast, Button}
import com.datayumyum.helloPOS.EventHandlers._

import scala.io.Source
import scala.concurrent._

class LoginActivity extends Activity {
  val TAG = "com.datayumyum.helloPOS.LoginActivity"

  lazy val loginButton: Button = findViewById(R.id.loginSubmitButton).asInstanceOf[Button]
  lazy val emailEditText: EditText = findViewById(R.id.email).asInstanceOf[EditText]
  lazy val passwordEditText: EditText = findViewById(R.id.password).asInstanceOf[EditText]

  implicit val exec = ExecutionContext.fromExecutor(
    new ThreadPoolExecutor(100, 100, 1000, TimeUnit.SECONDS,
      new LinkedBlockingQueue[Runnable]))

  override def onCreate(savedInstanceState: Bundle): Unit = {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.login_activity)
    loginButton.onClick {
      val email = emailEditText.getText()
      val password = passwordEditText.getText()
      val url: String = s"http://hive.kaicode.com:3000/pos/authenticate?email=$email&password=$password"
      val f: Future[String] = Future {
        val result: String = Source.fromInputStream(new URL(url).openStream).mkString
        result
      }

      f.onSuccess {
        case result =>
          if (result == "true") {
            Log.e(TAG, result.toString())
            val intent = new Intent(this, classOf[PosActivity]);
            startActivity(intent)
          } else {
            Log.w(TAG, s"cannot login ${url}")
          }
      }

      f.onFailure {
        case result => Log.w(TAG, s"cannot login ${url}")
      }
    }
  }
}