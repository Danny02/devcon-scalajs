package example

import java.time.Instant

import cats.data.EitherT
import com.sun.org.apache.xml.internal.security.utils.IdResolver
import example.ScalaJSExample.json
import io.circe._
import io.circe.generic.auto._
import io.circe.syntax._
import io.circe.parser._
import monix.eval.Task
import monix.execution.Scheduler.Implicits.global
import org.scalajs.dom.ext.Ajax

import scala.concurrent.duration.DurationDouble
import scala.scalajs.js
import org.scalajs.dom

import scala.util.{Failure, Success, Try}

import scalatags.Text.all._


case class Speaker(name: String)

case class Talk(id: Int, name: String, speaker: Speaker, startTime: Instant, endTime: Instant)

object ScalaJSExample extends js.JSApp {

  val vortragApi = "http://devconagenda-dev.us-east-2.elasticbeanstalk.com/agenda"

  implicit val encodeInstant = Encoder.encodeString.contramap[Instant](_.toString)

  implicit val decodeInstant = Decoder.decodeString.emapTry(r => Try(Instant.ofEpochMilli(new js.Date(r).getTime().toLong)))

  def main(): Unit = {
    val entrypoint = dom.document.getElementById("scalaJsMain")

    request(vortragApi).timeout(5 seconds).runOnComplete { t =>
      t.flatMap(s => parseTalks(s).toTry) match {
        case Success(talks) => entrypoint.innerHTML = render(talks).toString
        case Failure(t) => entrypoint.textContent = t.getMessage
      }
    }
  }

  def render(talks: List[Talk]) = {
    ul(
      for (talk <- talks)
      yield li(
        s"${talk.speaker.name}: ${talk.name} (${talk.startTime})"
      )
    )
  }

  def parseTalks(json: String) = {
    val doc = parse(json).getOrElse(Json.Null)
    doc.hcursor.get[List[Talk]]("talks")
  }

  def request(url: String): Task[String] = Task.fromFuture {
    Ajax.get(url).map(_.responseText)
  }

  val json =
    """{
                 "talks": [
                   {
                     "id": 1,
                     "name": "Test 1",
                     "speaker": {
                       "name": "Hans"
                     },
                     "startTime": "2017-10-11T15:00:30+02:00",
                     "endTime": "2017-10-11T16:00:00+02:00"
                   },
                   {
                     "id": 2,
                     "name": "AWS",
                     "speaker": {
                       "name": "Herbert"
                     },
                     "startTime": "2017-10-12T15:05:30+02:00",
                     "endTime": "2017-10-12T16:00:00+02:00"
                   }
                 ]
               }"""
}
