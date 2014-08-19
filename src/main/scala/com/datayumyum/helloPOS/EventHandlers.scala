package com.datayumyum.helloPOS

import android.view.{MotionEvent, View}
import android.widget.AdapterView.OnItemLongClickListener
import android.widget.{AdapterView, ListView}

object EventHandlers {

  implicit class OnClickHandler(view: View) {
    def onClick(action: => Unit): Unit = {
      view.setOnClickListener(new View.OnClickListener() {
        override def onClick(v: View) = {
          action
        }
      })
    }

    def onTouch(action: MotionEvent => Unit): Unit = {
      view.setOnTouchListener(new View.OnTouchListener() {
        override def onTouch(view: View, event: MotionEvent): Boolean = {
          action(event)
          true
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

