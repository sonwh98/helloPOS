package com.datayumyum.helloPOS

import java.net.URL
import java.util.concurrent.{LinkedBlockingQueue, ThreadPoolExecutor, TimeUnit}

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.{Button, EditText, Toast}
import com.datayumyum.helloPOS.EventHandlers.ViewOnEventHandlers
import com.datayumyum.helloPOS.Util.uiThread

import scala.concurrent._
import scala.io.Source

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
      val doLogin: Future[String] = Future {
        val result: String = Source.fromInputStream(new URL(url).openStream).mkString
        result
      }

      val failedMsg: String = s"cannot login ${url}"
      doLogin.onSuccess {
        case result =>
          if (result == "true") {
            Log.e(TAG, result)
            val intent = new Intent(LoginActivity.this, classOf[PosActivity]);
            startActivity(intent)
          } else {
            Log.w(TAG, failedMsg)
            uiThread {
              Toast.makeText(LoginActivity.this, failedMsg, Toast.LENGTH_LONG).show()
            }
          }
      }

      doLogin.onFailure {
        case result => {
          Log.w(TAG, failedMsg)
          uiThread {
            Toast.makeText(LoginActivity.this, failedMsg, Toast.LENGTH_LONG).show()
          }
        }
      }
    }
  }
}