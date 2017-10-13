package shared.component

import scalatags.generic
import scalatags.generic.Bundle

class Components[Builder, Output <: FragT, FragT](bundle: Bundle[Builder, Output, FragT]) {
  import bundle.all._

  private type Frag = generic.Frag[Builder, FragT]

  trait Renderer[A] {
    def render(a: A): Frag
  }

  implicit val talkRenderer = new Renderer[Talk] {
    override def render(t: Talk): Frag = {
      div(cls := "card", style := "width: 20rem; margin: 1rem")(
        img(cls := "card-img-top", src := "http://via.placeholder.com/300x200"),
        div(cls := "card-body")(
          h4(cls := "card-title")(t.name),
          p(cls := "card-text")(s"by ${t.speaker.name} on ${t.startTime}")
        )
      )
    }
  }

  implicit val talksRenderer = new Renderer[Seq[Talk]] {
    override def render(t: Seq[Talk]): Frag = seqFragable(t)
  }

  implicit def toFrag[A: Renderer](a: A): Frag = implicitly[Renderer[A]].render(a)

  implicit def seqFragable[A: Renderer](s: Seq[A]): Seq[Frag] = s.map(a => a: Frag)

}
