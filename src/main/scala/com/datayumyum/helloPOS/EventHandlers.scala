package com.datayumyum.helloPOS

import android.view.View
import android.widget.AdapterView.OnItemLongClickListener
import android.widget.{AdapterView, ListView}

object EventHandlers {

  implicit class OnClickHandler(view: View) {
    def onClick(action: => Unit): Unit = {
      view.setOnClickListener(new View.OnClickListener() {
        def onClick(v: View) = {
          action
        }
      })
    }
  }

  implicit class OnItemLongClickHandler(view: ListView) {
    def onItemLongClick(action: Int => Unit): Unit = {
      view.setOnItemLongClickListener(new OnItemLongClickListener() {
        override def onItemLongClick(parent: AdapterView[_], view: View, position: Int, id: Long): Boolean = {
          action(position)
          true
        }
      })
    }
  }

  implicit def toRunnable[F](f: => F): Runnable = new Runnable() {
    def run() = f
  }
}

