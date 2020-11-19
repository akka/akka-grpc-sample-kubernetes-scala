package com.example.helloworld

import akka.actor.ActorSystem
import akka.event.LoggingAdapter
import akka.grpc.GrpcClientSettings
import akka.http.scaladsl.Http
import akka.http.scaladsl.model._
import akka.http.scaladsl.server.Directives._
import akka.stream.{ActorMaterializer, Materializer}

import scala.concurrent.duration._
import scala.concurrent.{ExecutionContext, Future}
import scala.util.{Failure, Success}


object HttpToGrpc {

  def main(args: Array[String]): Unit = {
    implicit val system: ActorSystem = ActorSystem("HttpToGrpc")
    implicit val mat: Materializer = Materializer(system)
    implicit val ec: ExecutionContext = system.dispatcher
    val log: LoggingAdapter = system.log

    val settings = GrpcClientSettings.fromConfig("helloworld.GreeterService")
    val client = GreeterServiceClient(settings)

    system.scheduler.scheduleAtFixedRate(5.seconds, 5.seconds)(() => {
        log.info("Scheduled say hello to chris")
        val response: Future[HelloReply] = client.sayHello(HelloRequest("Christopher"))
        response.onComplete { r =>
          log.info("Scheduled say hello response {}", r)
        }
      })

    val route =
      path("hello" / Segment) { name =>
        get {
          log.info("hello request")
          onComplete(client.sayHello(HelloRequest(name))) {
            case Success(reply) => complete(reply.message)
            case Failure(t) =>
              log.error(t, "Request failed")
              complete(StatusCodes.InternalServerError, t.getMessage)
          }
        }
      }

    val bindingFuture = Http().newServerAt("0.0.0.0", 8080).bindFlow(route)
    bindingFuture.onComplete {
      case Success(sb) =>
        log.info("Bound: {}", sb)
      case Failure(t) =>
        log.error(t, "Failed to bind. Shutting down")
        system.terminate()
    }

  }
}
