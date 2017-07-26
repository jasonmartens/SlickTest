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

  class CarsTable(tag: Tag) extends Table[Car](tag, "cars") {

    def name = column[String]("name")

    def make = column[String]("make")

    def model = column[String]("model")

    def cars_pk = primaryKey("cars_pk", name)

    def * = (name, make, model) <> (Car.tupled, Car.unapply)
  }

  val cars: TableQuery[CarsTable] = TableQuery[CarsTable]



  class DriversTable(tag: Tag) extends Table[Driver](tag, "drivers") {

    def name = column[String]("name")

    def car = column[String]("car")

    def drivers_pk = primaryKey("drivers_pk", name)

    def car_fk = foreignKey("car_fk", car, cars)(_.name)

    def * = (name, car) <> (Driver.tupled, Driver.unapply)
  }

  val drivers: TableQuery[DriversTable] = TableQuery[DriversTable]



  class TripsTable(tag: Tag) extends Table[Trip](tag, "trips") {

    def name = column[String]("name")

    def driver = column[String]("driver")

    def car = column[String]("car")

    def trips_pk = primaryKey("trips_pk", name)

    def trip_driver_fk = foreignKey("trip_driver_fk", driver, drivers)(_.name)

    def trip_car_fk = foreignKey("trip_car_fk", car, cars)(_.name)

    def * = (name, driver, car) <> (Trip.tupled, Trip.unapply)
  }

  val trips: TableQuery[TripsTable] = TableQuery[TripsTable]
}