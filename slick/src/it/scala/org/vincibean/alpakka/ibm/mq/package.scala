package org.vincibean.alpakka.ibm

import akka.stream.alpakka.slick.scaladsl.SlickSession
import slick.lifted.ProvenShape

package object mq {

  implicit val session: SlickSession = SlickSession.forConfig("slick-h2")

  // This import brings everything you need into scope
  import session.profile.api._

  class Users(tag: Tag)
    extends Table[(Int, String)](tag, "ALPAKKA_SLICK_SCALADSL_TEST_USERS") {
    def id: Rep[Int] = column[Int]("ID", O.PrimaryKey)
    def name: Rep[String] = column[String]("NAME")
    def * : ProvenShape[(Int, String)] = (id, name)
  }

}
