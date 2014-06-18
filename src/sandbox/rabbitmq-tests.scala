import com.rabbitmq.client.ConnectionFactory
import com.rabbitmq.client.Connection
import com.rabbitmq.client.Channel
import com.rabbitmq.client.MessageProperties

val factory = new ConnectionFactory()
factory.setHost("hive.kaicode.com")
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
val message = "一儿三四五六七八九十啊吧色:::0:::20:::365:::40"

channel.basicPublish("", queueName, MessageProperties.PERSISTENT_TEXT_PLAIN, message.getBytes())
channel.close()
connection.close()
println("foo")
