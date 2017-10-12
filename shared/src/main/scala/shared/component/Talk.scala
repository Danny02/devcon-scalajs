package shared.component

import java.time.Instant

case class Speaker(name: String)

case class Talk(id: Option[Int],
                name: String,
                speaker: Speaker,
                startTime: Instant,
                endTime: Instant)
