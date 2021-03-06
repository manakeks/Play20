package test
import org.specs2.mutable._
import play.api.mvc._
import play.api.json._
import play.api.libs.iteratee._
import play.api.libs.concurrent._
import org.openqa.selenium.htmlunit.HtmlUnitDriver
import play.api.test.IntegrationTest._
import com.ning.http.client.providers.netty.NettyResponse
import play.api.WS
import play.api.ws.Response

import models._
import models.Protocol._

object FunctionalSpec extends Specification {

"an Application" should {
  "pass functional test" in {
   withNettyServer{
    val driver = new HtmlUnitDriver()
      driver.get("http://localhost:9001")
      driver.getPageSource must contain ("Hello world")
      val resultGet: String = WS.url("http://localhost:9001").get().value match { case r: Redeemed[Response] => r.a.body }
      resultGet must contain ("Hello world")
      val resultPost: String = WS.url("http://localhost:9001/post").post().value match { case r: Redeemed[Response] => r.a.body }
      resultPost must contain ("POST!")
      val resultJson: User = WS.url("http://localhost:9001/json").get().value match {
          case r: Redeemed[Response] => r.a.json.as[User]
      }

      driver.get("http://localhost:9001/conf")
      driver.getPageSource must contain("This value comes from complex-app's complex1.conf")
      driver.getPageSource must contain("None")

      resultJson must be equalTo(User(1, "Sadek", List("tea")))

      driver.get("http://localhost:9001/json_java")
      driver.getPageSource must contain ("{\"peter\":\"foo\",\"yay\":\"value\"}")
      
      driver.get("http://localhost:9001/headers")
      driver.getPageSource must contain("localhost:9001")

      // --- Cookies
      driver.get("http://localhost:9001/json_java")
      driver.manage.getCookies.size must be_== (0)

      driver.get("http://localhost:9001/cookie")
      val cookieFoo = driver.manage.getCookieNamed("foo")
      cookieFoo.getValue must be_== ("bar")

      driver.get("http://localhost:9001/read/foo")
      driver.getPageSource must contain ("Cookie foo has value: bar")

      driver.get("http://localhost:9001/read/bar")
      driver.getPageSource must be_== ("")

      driver.get("http://localhost:9001/clear/foo")
      driver.manage.getCookies.size must be_== (0)
   }
  }
 }

}
