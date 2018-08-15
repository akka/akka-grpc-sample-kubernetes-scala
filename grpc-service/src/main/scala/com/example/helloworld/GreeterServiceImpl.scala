package com.example.helloworld

import scala.concurrent.Future
import akka.NotUsed
import akka.event.LoggingAdapter
import akka.stream.Materializer
import akka.stream.scaladsl.BroadcastHub
import akka.stream.scaladsl.Keep
import akka.stream.scaladsl.MergeHub
import akka.stream.scaladsl.Sink
import akka.stream.scaladsl.Source


class GreeterServiceImpl(materializer: Materializer, log: LoggingAdapter) extends GreeterService {


  private implicit val mat: Materializer = materializer

  val (inboundHub: Sink[HelloRequest, NotUsed], outboundHub: Source[HelloReply, NotUsed]) =
    MergeHub.source[HelloRequest]
      .map(request => HelloReply(s"Hello, ${request.name}"))
      .toMat(BroadcastHub.sink[HelloReply])(Keep.both)
      .run()

  override def sayHello(request: HelloRequest): Future[HelloReply] = {
    log.info("sayHello {}", request)
    Future.successful(HelloReply(s"Hello, ${request.name}"))
  }

  override def sayHelloToAll(in: Source[HelloRequest, NotUsed]): Source[HelloReply, NotUsed] = {
    log.info("sayHelloToAll")
    in.runWith(inboundHub)
    outboundHub
  }
}
