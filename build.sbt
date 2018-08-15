name := "akka-grpc-kubernetes"
scalaVersion := "2.12.6"

lazy val akkaVersion = "2.5.14"
lazy val akkaGrpcVersion = "0.2-RC1+6-dd0f5837+20180814-1154"
lazy val discoveryVersion = "0.17.0"
lazy val akkaHttpVersion = "10.1.3"
lazy val alpnVersion = "2.0.7"

lazy val root = (project in file("."))
  .aggregate(httpToGrpc, grpcService)

// Http front end that calls out to a gRPC back end
lazy val httpToGrpc = (project in file("http-to-grpc"))
  .enablePlugins(AkkaGrpcPlugin, SbtReactiveAppPlugin, JavaAgent)
  .settings(
    libraryDependencies ++= Seq(
      "com.typesafe.akka" %% "akka-http" % akkaHttpVersion,
      "com.lightbend.akka.discovery" %% "akka-discovery-kubernetes-api" % discoveryVersion,
      "com.lightbend.akka.discovery" %% "akka-discovery-dns" % discoveryVersion,
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


