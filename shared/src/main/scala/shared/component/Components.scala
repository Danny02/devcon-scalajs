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
    override def render(t: Talk) = s"${t.speaker.name}: ${t.name} (${t.startTime})"
  }

  implicit def toFrag[A: Renderer](a: A): Frag = implicitly[Renderer[A]].render(a)

  implicit def seqFragable[A: Renderer](s: Seq[A]): Seq[Frag] = s.map(a => a: Frag)

  implicit class SeqOps(s: Seq[Frag]) {
    def asUnorderedList: Frag = ul(s.map(li(_)))
  }
  implicit class SeqOps2[A: Renderer](s: Seq[A]) {
    def asUnorderedList: Frag = seqFragable(s).asUnorderedList
  }
}
