package com.datayumyum.helloPOS

import android.test.ActivityInstrumentationTestCase2
import junit.framework.Assert
import com.robotium.solo.Solo

class HelloActivityTest extends ActivityInstrumentationTestCase2[HelloActivity](classOf[HelloActivity]) {
  var solo: Solo = _

  override def setUp() {
    solo = new Solo(getInstrumentation(), getActivity())
  }

  def test1() {
    Assert.assertTrue(true)
  }

  def test2() {
    solo.waitForText("Hello. I'm Java !")
  }
}
