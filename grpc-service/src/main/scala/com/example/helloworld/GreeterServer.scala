package com.example.helloworld

import akka.actor.ActorSystem
import akka.http.scaladsl.UseHttp2.Always
import akka.http.scaladsl.model.{HttpRequest, HttpResponse}
import akka.http.scaladsl.{Http, Http2, HttpConnectionContext}
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

    val bound = Http().bindAndHandleAsync(
      service,
      interface = "0.0.0.0",
      port = 8080,
      connectionContext = HttpConnectionContext())

    bound.foreach { binding =>
      sys.log.info("gRPC server bound to: {}", binding.localAddress)
    }

    bound
  }
}
