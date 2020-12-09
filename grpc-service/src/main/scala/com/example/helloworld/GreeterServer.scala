package com.example.helloworld

import akka.actor.ActorSystem
import akka.http.scaladsl.model.{HttpRequest, HttpResponse}
import akka.http.scaladsl.{Http, HttpConnectionContext}
import akka.stream.{ActorMaterializer, Materializer}

import scala.concurrent.{ExecutionContext, Future}


object GreeterServer {

  def main(args: Array[String]): Unit = {
    val system: ActorSystem = ActorSystem("GreeterServer")
    new GreeterServer(system).run()
  }
}

class GreeterServer(system: ActorSystem) {

  def run(): Future[Http.ServerBinding] = {
    implicit val sys: ActorSystem = system
    implicit val mat: Materializer = Materializer(sys)
    implicit val ec: ExecutionContext = sys.dispatcher

    val service: HttpRequest => Future[HttpResponse] =
      GreeterServiceHandler(new GreeterServiceImpl(mat, system.log))

    val bound = Http().newServerAt("0.0.0.0", 8080).bind(service)

    bound.foreach { binding =>
      sys.log.info("gRPC server bound to: {}", binding.localAddress)
    }

    bound
  }
}
