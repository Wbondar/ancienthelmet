package ancienthelmet.server

import akka.actor.ActorSystem
import akka.stream.ActorMaterializer
import akka.stream.scaladsl.Flow
import akka.stream.scaladsl.Sink
import akka.stream.scaladsl.Source
import akka.stream.scaladsl.Tcp
import akka.util.ByteString

import scala.util.Failure
import scala.util.Success

class AncientServer(val actorSystem: ActorSystem, val address: String, val port: Int) {
	implicit val sys = actorSystem
	implicit val actorMaterializer = ActorMaterializer()    
	import actorSystem.dispatcher

	val gameStateUpdateFlow = Sink.foreach[Tcp.IncomingConnection] { conn =>
		println("Client connected from: " + conn.remoteAddress)
		conn handleWith Flow[ByteString]
	}

	val connections = Tcp().bind(address, port)
	val binding = connections.to(gameStateUpdateFlow).run()

	binding.onComplete {
		case Success(b) =>
			println("Server started, listening on: " + b.localAddress)
		case Failure(e) =>
			println(s"Server could not bind to $address:$port: ${e.getMessage}")
			actorSystem.terminate()
	}

}