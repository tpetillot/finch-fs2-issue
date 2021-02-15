import cats.effect.Effect
import cats.implicits._
import fs2._
import io.chrisdavenport.log4cats.slf4j.Slf4jLogger
import io.circe.generic.auto._
import io.finch.Endpoint.Compiled
import io.finch._
import io.finch.circe._

object Streaming {
  final case class Message(content: String)
}

class Streaming[F[_]](implicit F: Effect[F], liftReader: LiftReader[Stream, F]) extends Endpoint.Module[F] {

  import Streaming._

  private val logger = Slf4jLogger.getLogger[F]

  private val error: Pipe[F, Array[Byte], Array[Byte]] = {
    def go(s: Stream[F, Array[Byte]]): Pull[F, Array[Byte], Unit] =
      s.pull.uncons.flatMap(_ => Pull.raiseError[F](new IllegalStateException("ERROR")))
    in => go(in).stream
  }

  private val streaming = post("stream" :: binaryBodyStream[Stream]) { (stream: Stream[F, Array[Byte]]) =>
    stream
      .through(error)
      .compile
      .lastOrError
      .as(Message("Ok!"))
      .handleErrorWith { throwable: Throwable =>
        for {
          _ <- logger.error(throwable)("Error occurred! Message content will be error message.")
        } yield Message(throwable.getMessage)
      }
      .map(Ok)
  }

  val compiled: Compiled[F] = Bootstrap.serve[Application.Json](streaming).compile
}
