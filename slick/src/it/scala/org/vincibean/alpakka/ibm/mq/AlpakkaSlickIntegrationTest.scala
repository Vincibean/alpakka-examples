package org.vincibean.alpakka.ibm.mq

import akka.actor.ActorSystem
import akka.stream.ActorMaterializer
import akka.stream.alpakka.slick.scaladsl.{Slick, SlickSession}
import akka.stream.scaladsl.{Sink, Source}
import org.specs2.Specification
import org.specs2.concurrent.ExecutionEnv
import org.specs2.matcher.MatchResult
import org.specs2.specification.core.SpecStructure

import scala.collection.immutable
import scala.concurrent.{ExecutionContextExecutor, Future}

class AlpakkaSlickIntegrationTest(implicit ee: ExecutionEnv) extends Specification {

  override def is: SpecStructure =
    s2"""
         Using Alpakka with Slick should work $s1
      """

  def s1: MatchResult[Future[immutable.Seq[(Int, String)]]] = {
    implicit val system: ActorSystem = ActorSystem("alpakka-slick")
    implicit val mat: ActorMaterializer = ActorMaterializer()
    implicit val ec: ExecutionContextExecutor = system.dispatcher
    implicit val session: SlickSession = SlickSession.forConfig("slick-h2")

    import session.profile.api._

    val users = TableQuery[Users]

    val us = (1 to 42).map(id => id -> s"User #$id")

    val insert = Source(us).runWith(Slick.sink(users +=))
    val select = Slick.source(users.distinct.result).runWith(Sink.seq)
    val result = for {
      _ <- insert
      s <- select
    } yield s

    result.onComplete { _ =>
      session.close()
      system.terminate()
    }

    result must containTheSameElementsAs(us).await
  }

}
