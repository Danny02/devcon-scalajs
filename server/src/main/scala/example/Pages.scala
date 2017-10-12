package example

import java.time.Instant

import shared.component.PageIds._
import shared.component.{Components, Speaker, Talk}

import scalatags.Text.short._
import scalatags.Text._

object Pages {

  object TextComponents extends Components(scalatags.Text)

  import TextComponents._

  def index() = {
    val dummyTalk: Frag = Talk(None, "dummy", Speaker("John Do"), Instant.now, Instant.now)

    val con = *.cls := "container"

    template("Akka HTTP with Scala.js")(
      header(con)(h1("DevCon 2017")),
      tags2.main(*.id := mainId, con)(
        h2("Agenda"),
        tags2.section(*.id := talkListId, con)(Seq.fill(3)(dummyTalk).asUnorderedList)
      )
    )
  }

  def template(titleText: String)(content: Frag*) = {
    "<!DOCTYPE html>" + html(
      head(
        tags2.title(titleText),
        meta(attr("charset") := "utf-8"),
        meta(attr("name") := "viewport",
             attr("content") := "width=device-width, initial-scale=1, shrink-to-fit=no"), {
          link(
            *.rel := "stylesheet",
            *.href := "https://maxcdn.bootstrapcdn.com/bootstrap/4.0.0-beta/css/bootstrap.min.css",
            attr("integrity") := "sha384-/Y6pD6FV/Vv2HJnA6t+vslU6fwYXjCFtcEpHbNJ0lyAFsXTsjBbfaDjzALeQsN6M",
            attr("crossorigin") := "anonymous"
          )
        }
      ),
      body(
        content,
        scalaJsScripts("client")
      )
    )
  }

  def scalaJsScripts(projectName: String): Frag = {
    val lower = projectName.toLowerCase
    val scripts = Seq(
      s"$lower-jsdeps.min.js",
      s"$lower-jsdeps.js",
      s"$lower-opt.js",
      s"$lower-fastopt.js",
      s"$lower-launcher.js",
    )

    scripts
      .filter(name => getClass.getResource(s"/public/$name") != null)
      .map(name => s"/assets/$name")
      .map(s => script(*.`type` := "text/javascript", *.src := s))
  }
}
