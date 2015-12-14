package models
import anorm._
import anorm.SqlParser._
import play.api.db.DB
import play.api.Play.current
import anorm.SQL

case class Hotel(City: String, Id: Long, Room: String, Price: Long)
object HotelMD {

  val parser = str("City") ~ get[Long]("HOTELID") ~ str("Room") ~ get[Long]("Price") map {
    case c ~ i ~ r ~ p => Hotel(c, i, r, p)
  }

  def getAllHotel() = {
    DB.withConnection { implicit c =>
      val result: List[Hotel] = SQL"Select * from Hotels".as(parser.*)
      result
    }
  }

  def getHotelByCity(s: String) = {
    val query = s"Select * from Hotels WHERE  CITY LIKE '%$s%'"
    println(query)
    DB.withConnection { implicit c =>
      val result: List[Hotel] = SQL(query).as(parser.*)
      println(result)
      result
    }
  }

  def getHotelSortedByPrice(sortBy: String, sortDir: String) = {
    val query = s"Select * from Hotels ORDER BY $sortBy $sortDir"
    //println(query)
    DB.withConnection { implicit c =>
      val result: List[Hotel] = SQL(query).as(parser.*)
      println(result)
      result
    }

  }
}