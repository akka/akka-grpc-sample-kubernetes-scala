name := "akka-grpc-kubernetes"
scalaVersion := "2.13.3"

lazy val akkaVersion = "2.6.11"
lazy val discoveryVersion = "1.0.10"
lazy val akkaHttpVersion = "10.2.3"

lazy val root = (project in file("."))
  .aggregate(httpToGrpc, grpcService)

// Http front end that calls out to a gRPC back end
lazy val httpToGrpc = (project in file("http-to-grpc"))
  .enablePlugins(AkkaGrpcPlugin, DockerPlugin, JavaAppPackaging)
  .settings(
    libraryDependencies ++= Seq(
      "com.typesafe.akka" %% "akka-actor-typed" % akkaVersion,
      "com.typesafe.akka" %% "akka-slf4j" % akkaVersion,
      "com.typesafe.akka" %% "akka-discovery" % akkaVersion,
      "com.typesafe.akka" %% "akka-stream" % akkaVersion,

      "com.typesafe.akka" %% "akka-parsing" % akkaHttpVersion,
      "com.typesafe.akka" %% "akka-http-core" % akkaHttpVersion,
      "com.typesafe.akka" %% "akka-http" % akkaHttpVersion,
      "com.typesafe.akka" %% "akka-http-spray-json" % akkaHttpVersion,
      "com.typesafe.akka" %% "akka-http2-support" % akkaHttpVersion,

      "com.lightbend.akka.discovery" %% "akka-discovery-kubernetes-api" % discoveryVersion,

      "ch.qos.logback" % "logback-classic" % "1.2.3"
    ),
    dockerExposedPorts := Seq(8080),
  )

lazy val grpcService = (project in file("grpc-service"))
  .enablePlugins(AkkaGrpcPlugin, DockerPlugin, JavaAppPackaging)
  .settings(
    dockerExposedPorts := Seq(8080),
    libraryDependencies ++= Seq(
      "com.typesafe.akka" %% "akka-actor" % akkaVersion,
      "com.typesafe.akka" %% "akka-actor-typed" % akkaVersion,
      "com.typesafe.akka" %% "akka-slf4j" % akkaVersion,
      "com.typesafe.akka" %% "akka-stream" % akkaVersion,
      "com.typesafe.akka" %% "akka-discovery" % akkaVersion,

      "com.typesafe.akka" %% "akka-http" % akkaHttpVersion,
      "com.typesafe.akka" %% "akka-http2-support" % akkaHttpVersion,

      "ch.qos.logback" % "logback-classic" % "1.2.3"
    )
  )


