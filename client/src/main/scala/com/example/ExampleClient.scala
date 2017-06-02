package com.example

import com.example.ping.thriftscala.PingService
import com.twitter.finagle.{ServiceFactory, Thrift}
import com.twitter.util.{Await, Future}

object ExampleClient extends App {

  val host = "localhost:9911"

  val clientServiceIface: PingService.ServiceIface =
    Thrift.client.newServiceIface[PingService.ServiceIface](host, "thrift_client")

  val client: PingService.FutureIface =
    Thrift.client.newIface[PingService.FutureIface](host)

  client.echo("hoge") onSuccess { s => println(s"Reponse: " + s )}
  client.ping() onSuccess { s => println(s"Response: " + s)}

  Await.result(
    for {
      _ <- client.ping()
      _ <- client.echo("hgoe")
    } yield ()
  )

}
