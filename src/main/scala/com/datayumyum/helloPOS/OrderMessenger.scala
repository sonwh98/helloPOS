package com.datayumyum.helloPOS

import com.rabbitmq.client.{MessageProperties, Channel, Connection, ConnectionFactory}

/**
 * Created by sto on 6/18/14.
 */
object OrderMessenger {
  val factory = new ConnectionFactory()
  factory.setHost("hive.kaicode.com")
//  factory.setHost("localhost")
  factory.setPort(5672)
  factory.setUsername("order")
  factory.setPassword("EisKisP1")
  val connection: Connection = factory.newConnection()
  val channel: Channel = connection.createChannel()

  val queueName = "order-queue"
  val durable = true
  val exclusive = false
  val autoDelete = true
  channel.queueDeclare(queueName, durable, exclusive, autoDelete, null)

  def sendOrder(endOrderStr: String) {
    channel.basicPublish("", queueName, MessageProperties.PERSISTENT_TEXT_PLAIN, endOrderStr.getBytes)
  }
}
