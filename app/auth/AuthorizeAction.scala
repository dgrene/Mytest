package auth
import play.api.mvc._
import scala.concurrent.Future
import play.api.libs.json._
import scala.collection.mutable.HashMap
import java.util.concurrent.TimeUnit

case class User(userKey: String, userRate: Int, var lastRequestTime: String, var timeRemaining: String, var currentStatus: Boolean)

object AuthorizeAction extends ActionBuilder[Request] {

  val userMap = HashMap[String, (User, Bucket)]()

  val u1 = User("user1", 4, "", "", true)
  val u2 = User("user2", 4, "", "", true)
  val u3 = User("user3", 4, "", "", true)
  val u4 = User("user4", 4, "", "", true)

  val uc1 = (u1, new Bucket(8, TimeUnit.MINUTES, u1))
  //val uc2 = (u1, new Bucket(8, TimeUnit.MINUTES, u1))
  ///val uc3 = (u1, new Bucket(8, TimeUnit.MINUTES, u1))
  //val uc4 = (u1, new Bucket(8, TimeUnit.MINUTES, u1))

  userMap += ("user1" -> uc1) //+= ("user2" -> uc2) += ("user3" -> uc3) += ("user4" -> uc4)

  //  def verifyToken(credent: UserCredential): Boolean = {
  //val cache = new RedisClient("localhost", 6379)
  //cache.exists(credent.encToken + ":" + credent.assetId)
  // }

  def invokeBlock[A](request: Request[A], block: (Request[A]) => Future[Result]) = {

    val apiKey = request.headers.get("userKey")
    apiKey match {
      case x: Some[String] => {
        val keyVal = userMap.get(x.get)
        //println("APIKEY ::")
        //println(apiKey.toString())

        //println(keyVal)
        keyVal match {
          case x: Some[(User, Bucket)] => {
            x.get._2.take() match {
              case false => {
                Future.successful(Results.Unauthorized("Hold for 5 minutes"))
              }
              case true => block(request)
            }
          }
          case _ => {
            Future.successful(Results.Unauthorized("Unauthorized request"))
          }
        }
        //println(keyVal)

      }
      case None => Future.successful(Results.BadRequest("Please pass userKey"))
    }

  }

}
