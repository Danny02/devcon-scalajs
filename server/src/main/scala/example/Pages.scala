package example

import scalatags.Text.all._

object Pages {

  def index(message: String) = {
    main("Akka HTTP with Scala.js")(
      h2("Akka HTTP and Scala.js share a same message"),
      ul(
        li("Akka HTTP shouts out: ", em(message))
      ),
      tag("main")(id := "scalaJsMain")
    )
  }

  def main(titleText: String)(content: Frag*) = {
    "<!DOCTYPE html>" + html(
      head(
        tag("title")(titleText)
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
      .map(s => script(`type` := "text/javascript", src := s))
  }
}
