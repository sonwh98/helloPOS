package com.datayumyum.helloPOS

import android.view.{MotionEvent, View}
import android.widget.AdapterView.OnItemLongClickListener
import android.widget.{AdapterView, ListView}

object EventHandlers {

  implicit class ViewOnEventHandlers(view: View) {
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

  implicit class OnItemLongClickHandler(listView: ListView) {
    def onItemLongClick(action: Int => Unit): Unit = {
      listView.setOnItemLongClickListener(new OnItemLongClickListener() {
        override def onItemLongClick(parent: AdapterView[_], view: View, position: Int, id: Long): Boolean = {
          action(position)
          true
        }
      })
    }

    def onDismiss(action: Array[Int] => Unit): Unit = {
      val touchListener = new SwipeDismissListViewTouchListener(listView, new SwipeDismissListViewTouchListener.DismissCallbacks() {
        override def canDismiss(position: Int): Boolean = {
          return true
        }

        override def onDismiss(listView: ListView, reverseSortedPositions: Array[Int]) {
          action(reverseSortedPositions)
        }
      })

      listView.setOnTouchListener(touchListener)
      listView.setOnScrollListener(touchListener.makeScrollListener())

    }
  }

}

