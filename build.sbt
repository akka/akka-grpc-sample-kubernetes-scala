name := "akka-grpc-kubernetes"
scalaVersion := "2.12.6"

lazy val akkaVersion = "2.5.21"
lazy val discoveryVersion = "1.0.0"
lazy val akkaHttpVersion = "10.1.8"
lazy val alpnVersion = "2.0.9"

lazy val root = (project in file("."))
  .aggregate(httpToGrpc, grpcService)

// Http front end that calls out to a gRPC back end
lazy val httpToGrpc = (project in file("http-to-grpc"))
  .enablePlugins(AkkaGrpcPlugin, SbtReactiveAppPlugin, JavaAgent)
  .settings(
    libraryDependencies ++= Seq(
      "com.typesafe.akka" %% "akka-parsing" % akkaHttpVersion,
      "com.typesafe.akka" %% "akka-http-core" % akkaHttpVersion,
      "com.typesafe.akka" %% "akka-http" % akkaHttpVersion,
      "com.typesafe.akka" %% "akka-http-spray-json" % akkaHttpVersion,
      "com.typesafe.akka" %% "akka-http2-support" % akkaHttpVersion,
      "com.lightbend.akka.discovery" %% "akka-discovery-kubernetes-api" % discoveryVersion,
    ),
    javaAgents += "org.mortbay.jetty.alpn" % "jetty-alpn-agent" % alpnVersion % "runtime",
    // regular HttpEndpoint that forwards to the gRPC service
    endpoints += HttpEndpoint(
      name = "http",
      8080,
      ingress = HttpIngress(
        ingressPorts = Vector(8080),
        hosts = Vector("superservice.com"),
        paths = Vector.empty)
    )

  )

lazy val grpcService = (project in file("grpc-service"))
  .enablePlugins(AkkaGrpcPlugin, SbtReactiveAppPlugin, JavaAgent)
  .settings(
    javaAgents += "org.mortbay.jetty.alpn" % "jetty-alpn-agent" % alpnVersion % "runtime",
    // gRPC endpoint
    endpoints += HttpEndpoint(
      name = "http",
      port = 8080
    )
  )


