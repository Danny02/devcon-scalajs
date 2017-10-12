package example

import java.time.Instant

import monix.eval.Task
import monix.execution.Scheduler.Implicits.global
import org.scalajs.dom
import shared.component.PageIds._
import shared.component.{Components, Speaker, Talk}

import scalatags.JsDom.all._
import scalatags.JsDom.tags2._

object ScalaJSExample {

  object JsDomComponents extends Components(scalatags.JsDom)
  import JsDomComponents._

  def main(args: Array[String]): Unit = {
    refreshTalks.runAsync

    val newTalkName = input().render
    val speakerName = input().render

    val addTalkButton = button(onclick := { () =>
      val newTalk =
        Talk(None, newTalkName.value, Speaker(speakerName.value), Instant.now, Instant.now)

      for {
        _ <- AgendaApi.createTalkRequest(newTalk)
        _ <- refreshTalks
      } {}
    })("add new Talk")

    val addTalkSection = section(
      label("talk name: "),
      newTalkName,
      label("speaker name: "),
      speakerName,
      addTalkButton
    )

    val entrypoint = dom.document.getElementById(mainId)
    entrypoint.appendChild(addTalkSection.render)
  }

  val refreshTalks: Task[_] = {
    val taskList = dom.document.getElementById(talkListId)

    AgendaApi.talksRequest.map { talks =>
      taskList.innerHTML = ""
      taskList.appendChild(talks.asUnorderedList.render)
    }
  }

}
