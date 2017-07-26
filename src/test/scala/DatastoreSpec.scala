import Datastore.{Car, Driver, Trip}
import com.typesafe.config.ConfigFactory
import org.scalatest.{FunSpec, Matchers}
import slick.basic.DatabaseConfig
import slick.jdbc.JdbcProfile

import scala.concurrent.Await
import scala.concurrent.duration._
import scala.language.postfixOps
import scala.util.Random

class DatastoreSpec extends FunSpec with Matchers {

  describe("the datatastore") {
    it("should create all tables") {
      val configName: String = Random.alphanumeric.take(5).mkString("")
      val dbName: String = Random.alphanumeric.take(5).mkString("")
      val dbConfigString =
        s"""
           |  $configName {
           |    profile = "slick.jdbc.H2Profile$$"
           |    db {
           |      connectionPool = disabled
           |      driver = "org.h2.Driver"
           |      url = "jdbc:h2:mem:$dbName"
           |    }
           |  }
    """.stripMargin
      val dbConfig: DatabaseConfig[JdbcProfile] = DatabaseConfig.forConfig(configName, config = ConfigFactory.parseString(dbConfigString))
      val datastore = new Datastore(dbConfig)
      import datastore.dbConfig.profile.api._

      val schema = datastore.cars.schema ++ datastore.drivers.schema ++ datastore.trips.schema
      Await.result(datastore.dbConfig.db.run(DBIO.sequence(Vector(
        schema.create,
        datastore.cars += Car("KITT", "Pontiac", "Trans-Am"),
        datastore.drivers += Driver("Michael Knight", "KITT"),
        datastore.trips += Trip("Finale", "Michael Knight", "KITT"),
        datastore.cars.take(1).result,
        datastore.drivers.take(1).result,
        datastore.trips.take(1).result)
      )), 10 seconds).tail shouldBe Vector(
        1, 1, 1,
        Vector(Car("KITT", "Pontiac", "Trans-Am")),
        Vector(Driver("Michael Knight", "KITT")),
        Vector(Trip("Finale", "Michael Knight", "KITT")))
    }
  }
}
