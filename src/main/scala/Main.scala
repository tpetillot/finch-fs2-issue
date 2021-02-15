import java.util.concurrent.{ExecutorService, Executors}

import cats.effect.{ExitCode, IO, IOApp, Resource}
import com.twitter.conversions.DurationOps._
import com.twitter.conversions.StorageUnitOps._
import com.twitter.finagle.Http
import com.twitter.util.Future
import io.chrisdavenport.log4cats.slf4j.Slf4jLogger
import io.finch._
import io.finch.fs2._

object Main extends IOApp {

  private val logger = Slf4jLogger.getLogger[IO]

  private val executorService: Resource[IO, ExecutorService] = {
    val size = 2 * Runtime.getRuntime.availableProcessors
    Resource.make(IO.delay(Executors.newFixedThreadPool(size)))(es => IO.delay(es.shutdown()))
  }

  private val streaming = new Streaming[IO]()

  private def server(executorService: ExecutorService) = Resource.make {
    for {
      listeningServer <- IO.delay {
        Http.server
          .withExecutionOffloaded(executorService)
          .withRequestTimeout(30.seconds)
          .withMaxRequestSize(20.kilobytes)
          .withStreaming(20.kilobytes)
          .serve(s":8080", Endpoint.toService(streaming.compiled))
      }
      _ <- logger.info(s"API server started")
    } yield listeningServer
  } { listeningServer =>
    for {
      _ <- logger.info(s"Shutting down API server")
      _ <- IO.suspend(implicitly[ToAsync[Future, IO]].apply(listeningServer.close()))
      _ <- IO.delay(executorService.shutdown())
    } yield ()
  }

  override def run(args: List[String]): IO[ExitCode] =
    (for {
      executorService <- executorService
      server <- server(executorService)
    } yield server).use(_ => IO.never).as(ExitCode.Success)
}
