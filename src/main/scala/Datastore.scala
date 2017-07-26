import Datastore.{Car, Driver, Trip}
import slick.basic.DatabaseConfig
import slick.jdbc.JdbcProfile

object Datastore {
  case class Car(name: String, make: String, model: String)
  case class Driver(name: String, car: String)
  case class Trip(name: String, driver: String, car: String)
}


class Datastore(dbc: DatabaseConfig[JdbcProfile]) extends DatastoreImpl {

  override val dbConfig: DatabaseConfig[JdbcProfile] = dbc
}
trait DatastoreImpl {

  val dbConfig: DatabaseConfig[JdbcProfile]

  import dbConfig.profile.api._

  class CarsTable(tag: Tag) extends Table[Car](tag, "CARS") {

    def name = column[String]("car")

    def make = column[String]("make")

    def model = column[String]("model")

    def pk = primaryKey("pk", name)

    def * = (name, make, model) <> (Car.tupled, Car.unapply)
  }

  val cars: TableQuery[CarsTable] = TableQuery[CarsTable]

  class DriversTable(tag: Tag) extends Table[Driver](tag, "DRIVERS") {

    def name = column[String]("name")

    def car = column[String]("car")

    def pk = primaryKey("pk", name)

    def car_fk = foreignKey("car", car, cars)(_.name)

    def * = (name, car) <> (Driver.tupled, Driver.unapply)
  }

  val drivers: TableQuery[DriversTable] = TableQuery[DriversTable]

  class TripsTable(tag: Tag) extends Table[Trip](tag, "TRIPS") {

    def name = column[String]("name")

    def driver = column[String]("driver")

    def car = column[String]("car")

    def pk = primaryKey("pk", name)

    def driver_fk = foreignKey("driver", driver, drivers)(_.name)

    def * = (name, driver, car) <> (Trip.tupled, Trip.unapply)
  }

  val trips: TableQuery[TripsTable] = TableQuery[TripsTable]
}