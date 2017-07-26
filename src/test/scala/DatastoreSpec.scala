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
           |      url = "jdbc:h2:mem:$dbName;MODE=MYSQL;DB_CLOSE_DELAY=-1"
           |    }
           |  }
    """.stripMargin
      val dbConfig: DatabaseConfig[JdbcProfile] = DatabaseConfig.forConfig(configName, config = ConfigFactory.parseString(dbConfigString))
      val datastore = new Datastore(dbConfig)
      import datastore.dbConfig.profile.api._

      val schema = datastore.cars.schema ++ datastore.drivers.schema ++ datastore.trips.schema
      Await.ready(datastore.dbConfig.db.run(schema.create), 10 seconds)
      Await.ready(datastore.dbConfig.db.run(datastore.cars += Car("KITT", "Pontiac", "Trans-Am")), 10 seconds)
      Await.ready(datastore.dbConfig.db.run(datastore.drivers += Driver("Michael Knight", "KITT")), 10 seconds)
      Await.ready(datastore.dbConfig.db.run(datastore.trips += Trip("Finale", "Michael Knight", "KITT")), 10 seconds)

      val car = Await.result(datastore.dbConfig.db.run(datastore.cars.result), 10 seconds)
      car.head shouldBe Car("KITT", "Pontiac", "Trans-Am")
      val driver = Await.result(datastore.dbConfig.db.run(datastore.drivers.result), 10 seconds)
      driver.head shouldBe Driver("Michael Knight", "KITT")
      val trip = Await.result(datastore.dbConfig.db.run(datastore.trips.result), 10 seconds)
      trip.head shouldBe Trip("Finale", "Michael Knight", "KITT")
    }
  }
}
