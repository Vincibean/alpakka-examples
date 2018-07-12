package org.vincibean.alpakka.ibm.mq

import akka.Done
import akka.actor.ActorSystem
import akka.stream.alpakka.jms.scaladsl.{JmsConsumer, JmsProducer}
import akka.stream.alpakka.jms.{AcknowledgeMode, Credentials, JmsConsumerSettings, JmsProducerSettings}
import akka.stream.scaladsl.{Sink, Source}
import akka.stream.{ActorMaterializer, KillSwitch}
import com.ibm.mq.jms.{MQQueueConnectionFactory, MQTopicConnectionFactory}
import com.ibm.msg.client.wmq.common.CommonConstants
import org.specs2.Specification
import org.specs2.concurrent.ExecutionEnv
import org.specs2.matcher.MatchResult
import org.specs2.specification.core.SpecStructure

import scala.collection.immutable
import scala.concurrent.Future

class AlpakkaIbmMqIntegrationTest(implicit ee: ExecutionEnv) extends Specification {

  override def is: SpecStructure =
    s2"""
         Using Alpakka with IBM MQ should work $s1
      """

  val textMessage = "Some Text Message"

  def s1: MatchResult[Future[Option[String]]] = {
    implicit val system: ActorSystem = ActorSystem("alpakka-ibm-mq")
    implicit val materializer: ActorMaterializer = ActorMaterializer()

    val QueueManagerName = "QM1"
    val TestChannelName = "DEV.APP.SVRCONN"

    // Create the IBM MQ QueueConnectionFactory
    val queueConnectionFactory: MQQueueConnectionFactory = new MQQueueConnectionFactory()
    queueConnectionFactory.setQueueManager(QueueManagerName)
    queueConnectionFactory.setChannel(TestChannelName)
    queueConnectionFactory.setTransportType(CommonConstants.WMQ_CM_CLIENT)


    val credentials = Credentials("app", "")
    val queue = "DEV.QUEUE.1"

    /**
      * All JMS sources materialize to a KillSwitch to allow safely stopping consumption without message loss for transactional and acknowledged messages, and with minimal message loss for the simple JMS source.
      *
      * To stop consumption safely, call shutdown() on the KillSwitch that is the materialized value of the source. To abruptly abort consumption (without concerns for message loss), call abort(Throwable) on the KillSwitch.
      */
    val jmsSource: Source[String, KillSwitch] = JmsConsumer.textSource(
      JmsConsumerSettings(queueConnectionFactory)
        .withBufferSize(10) // The bufferSize parameter controls the maximum number of messages to prefetch before applying backpressure.
        .withAcknowledgeMode(AcknowledgeMode.AutoAcknowledge) // The default AcknowledgeMode is AutoAcknowledge but can be overridden to custom AcknowledgeModes, even implementation-specific ones by setting the AcknowledgeMode in the JmsConsumerSettings when creating the stream.
        .withSessionCount(5) // The sessionCount parameter controls the number of JMS sessions to run in parallel. DO NOT set the sessionCount greater than 1 for topics. Doing so will result in duplicate messages being delivered. Each topic message is delivered to each JMS session and all the messages feed to the same Source. JMS 2.0 created shared consumers to solve this problem and multiple sessions without duplication may be supported in the future.
        .withCredential(credentials)
        .withQueue(queue)
    )

    val jmsTopicSink: Sink[String, Future[Done]] = JmsProducer.textSink(
      JmsProducerSettings(queueConnectionFactory)
        .withCredential(credentials)
        .withQueue(queue)
    )

    Source.single(textMessage).runWith(jmsTopicSink)
    val result = jmsSource.take(1L).runWith(Sink.seq)
    result.map(_.headOption) must beSome(textMessage).await
  }

}
