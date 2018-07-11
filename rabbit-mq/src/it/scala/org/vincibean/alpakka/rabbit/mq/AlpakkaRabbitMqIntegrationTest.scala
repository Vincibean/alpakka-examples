package org.vincibean.alpakka.rabbit.mq

import akka.actor.ActorSystem
import akka.stream.ActorMaterializer
import akka.stream.alpakka.amqp.scaladsl.{AmqpSink, AmqpSource}
import akka.stream.alpakka.amqp.{AmqpLocalConnectionProvider, AmqpSinkSettings, NamedQueueSourceSettings, QueueDeclaration}
import akka.stream.scaladsl.{Sink, Source}
import akka.util.ByteString
import org.specs2.Specification
import org.specs2.concurrent.ExecutionEnv
import org.specs2.matcher.MatchResult
import org.specs2.specification.core.SpecStructure

import scala.collection.immutable
import scala.concurrent.Future

class AlpakkaRabbitMqIntegrationTest(implicit ee: ExecutionEnv) extends Specification {

  override def is: SpecStructure =
    s2"""
         Using Alpakka with Rabbit MQ should work $s1
      """

  implicit val system: ActorSystem = ActorSystem("alpakka-rabbit-mq")
  implicit val materializer: ActorMaterializer = ActorMaterializer()

  def s1: MatchResult[Future[immutable.Seq[ByteString]]] = {
    val queueName = "amqp-conn-it-spec-simple-queue-" + System.currentTimeMillis()
    val queueDeclaration = QueueDeclaration(queueName)

    val connectionProvider = AmqpLocalConnectionProvider
    val amqpSink = AmqpSink.simple(
      AmqpSinkSettings(connectionProvider)
        .withRoutingKey(queueName)
        .withDeclarations(queueDeclaration)
    )

    val amqpSource = AmqpSource.atMostOnceSource(
      NamedQueueSourceSettings(connectionProvider, queueName)
        .withDeclarations(queueDeclaration),
      bufferSize = 10
    )

    val input = Vector("one", "two", "three", "four", "five")
    Source(input)
      .map(s => ByteString(s))
      .runWith(amqpSink)

    val result = amqpSource.take(input.size).runWith(Sink.seq)
    result.map(_.map(_.bytes)) must containTheSameElementsAs(input.map(s => ByteString(s))).await
  }

}
