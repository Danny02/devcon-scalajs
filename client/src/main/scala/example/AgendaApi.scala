package example

import java.time.Instant

import io.circe._
import io.circe.generic.auto._
import io.circe.parser._
import io.circe.syntax._
import monix.eval.Task
import org.scalajs.dom.ext.Ajax
import shared.component.{Speaker, Talk}

import scala.scalajs.js
import scala.util.Try

object AgendaApi {

  private implicit val encodeInstant = Encoder.encodeString.contramap[Instant](_.toString)

  private implicit val decodeInstant =
    Decoder.decodeString.emapTry(r => Try(Instant.ofEpochMilli(new js.Date(r).getTime().toLong)))

  private val agendaApiUrl = "http://devconagenda-dev.us-east-2.elasticbeanstalk.com/talks"

  val talksRequest: Task[Seq[Talk]] = {
    def parseResponse(response: String): Decoder.Result[Seq[Talk]] = {
      val json   = parse(response).getOrElse(Json.Null)
      val cursor = json.hcursor
      cursor.downField("_embedded").get[Seq[Talk]]("talks")
    }

    for {
      f     <- Task.deferFuture(Ajax.get(agendaApiUrl))
      talks <- Task.fromTry(parseResponse(f.responseText).toTry)
    } yield talks
  }

  val createTalkRequest: Talk => Task[_] = { talk =>
    require(talk.id.isEmpty)
    val printer = Printer.noSpaces.copy(dropNullKeys = true)
    Task.deferFuture(
      Ajax.post(agendaApiUrl,
                printer.pretty(talk.asJson),
                headers = Map("Content-Type" -> "application/json")))
  }

  val deleteTalkRequest: Talk => Task[_] = { talk =>
    require(talk.id.isDefined)
    Task.deferFuture(Ajax.delete(s"$agendaApiUrl/${talk.id.get}"))
  }

  //  def main(args: Array[String]) = {
  //    val printer = Printer.noSpaces.copy(dropNullKeys = true)
  //    println(printer.pretty(Talk(None, "test", Speaker("asd"), Instant.now, Instant.now).asJson))
  //  }
}
