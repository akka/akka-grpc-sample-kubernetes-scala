# Akka gRPC Kubernetes

This is an example of an [Akka HTTP](https://doc.akka.io/docs/akka-http/current) application communicating with an [Akka gRPC](https://developer.lightbend.com/docs/akka-grpc/current/) application inside of Kubernetes.

The Akka HTTP application discovers the Akka gRPC application using [Akka Discovery](https://developer.lightbend.com/docs/akka-management/current/discovery.html).
It uses the `akka-dns` mechanism which relies on the `SRV` records created by kubernetes.

The Docker images and Kubernetes resources are generated automatically by the [sbt-reactive-app](https://developer.lightbend.com/docs/lightbend-orchestration/current/) sbt plugin.

All the technologies used in this example are open source.

## Other approaches

This project uses sbt,
the [sbt-reactive-app](https://developer.lightbend.com/docs/lightbend-orchestration/current/)
plugin and the Scala language.

If you are using Java and sbt you can use exactly the same approach.

If you are using another build tool, such as Maven or Gradle, the code would
still be the same, but you would have to build the Docker image and deploy it to Kubernetes yourself.

## Usage

### Prerequisites

Install the following:

* [Docker](https://docs.docker.com/install/)
* [Kubectl](https://kubernetes.io/docs/tasks/tools/install-kubectl/)
* [Minikube](https://github.com/kubernetes/minikube)
* [Sbt](https://www.scala-sbt.org/)
* [reactive-cli](https://developer.lightbend.com/docs/lightbend-orchestration/current/setup/cli-installation.html)

### Running

Once minikube is running the two applications can be deployed using:

`sbt 'deploy minikube'`

Verify the deployments:

```
$ kubectl get deployments
NAME                          DESIRED   CURRENT   UP-TO-DATE   AVAILABLE   AGE
grpcservice-v0-1-0-snapshot   1         1         1            1           40s
httptogrpc-v0-1-0-snapshot    1         1         1            1           40s

```

There are servies for both:
```
$ kubectl get services
NAME          TYPE        CLUSTER-IP       EXTERNAL-IP   PORT(S)    AGE
grpcservice   ClusterIP   10.106.188.203   <none>        8080/TCP   1m
httptogrpc    ClusterIP   10.103.134.197   <none>        8080/TCP   1m
```

Ingress just for the HTTP app:

```
$ kubectl get ingress
NAME         HOSTS              ADDRESS   PORTS     AGE
httptogrpc   superservice.com             80        2m
```

The HTTP application periodically hits the gRPC applicaton which you can see in the logs:

```
[INFO] [08/15/2018 07:02:51.712] [HttpToGrpc-akka.actor.default-dispatcher-4] [akka.actor.ActorSystemImpl(HttpToGrpc)] Scheduled say hello to chris
[INFO] [08/15/2018 07:02:51.730] [HttpToGrpc-akka.actor.default-dispatcher-4] [akka.actor.ActorSystemImpl(HttpToGrpc)] Scheduled say hello response Success(HelloReply(Hello, Christopher))
```

And you can send a HTTP request via `Ingress` to the `httptogrpc` application:

```
$ curl -v --header 'Host: superservice.com' $(minikube ip)/hello/donkey
> GET /hello/donkey HTTP/1.1
> Host: superservice.com
> User-Agent: curl/7.59.0
> Accept: */*
> 
< HTTP/1.1 200 OK
< Server: nginx/1.13.12
< Date: Wed, 15 Aug 2018 07:03:56 GMT
< Content-Type: text/plain; charset=UTF-8
< Content-Length: 13
< Connection: keep-alive
< 
* Connection #0 to host 192.168.99.100 left intact
Hello, donkey%
```

The `Host` header needs to be set as that is how minikube [Ingress](https://github.com/kubernetes/ingress-nginx) routes requests to services.
