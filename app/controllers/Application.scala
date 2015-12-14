package controllers

import play.api._
import play.api.mvc._
import models._
import play.api.libs.json._
import auth.AuthorizeAction

//case class Hotel(City: String, Id: Long, Room: String, Price: Long)
class Application extends Controller {

  implicit val hotelWrites = new Writes[Hotel] {
    def writes(hotel: Hotel) = Json.obj(
      "Id" -> hotel.Id,
      "City" -> hotel.City,
      "Room" -> hotel.Room,
      "Price" -> hotel.Price)
  }
  def index = AuthorizeAction {

    val hotelList: List[Hotel] = HotelMD.getAllHotel()
    //println(hotelList)
    val json = Json.toJson(hotelList)
    //Ok(views.html.index("Your new application is ready."))
    Ok(json)
  }

  def hotelList(s: String) = Action {
    //println("BY CITY")
    val hotelList: List[Hotel] = HotelMD.getHotelByCity(s)
    //println(hotelList)
    val json = Json.toJson(hotelList)

    //Ok(views.html.index("Your new application is ready."))
    Ok(json)

  }
  def hotelListSortByPrice(sortBy: String, sortDir: String) = Action {
    //println("BY CITY")
    val hotelList: List[Hotel] = HotelMD.getHotelSortedByPrice(sortBy, sortDir)
    //println(hotelList)
    val json = Json.toJson(hotelList)
    //Ok(views.html.index("Your new application is ready."))
    Ok(json)

  }

}
