package com.datayumyum.helloPOS

import android.view.View

object EventHandlers {

  implicit class OnClickHandler(view: View) {
    def onClick(action:  => Unit) = {
      view.setOnClickListener(new View.OnClickListener() {
        def onClick(v: View) = {
          action
        }
      })
    }
  }

  implicit def toRunnable[F](f: => F): Runnable = new Runnable() {
    def run() = f
  }
}

