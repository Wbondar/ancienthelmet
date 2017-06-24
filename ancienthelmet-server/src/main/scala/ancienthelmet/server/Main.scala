package ancienthelmet.server

import akka.actor.ActorSystem

object Main {
	/**
	* Use parameters `127.0.0.1 6001` to start client connecting to
	* server on 127.0.0.1:6001.
	*
	*/
	def main(args: Array[String]): Unit = {
		val (address, port) =
			if (args.length == 2) (args(0), args(1).toInt)
			else ("127.0.0.1", 6000)

		val system = ActorSystem()
		new AncientServer(system, address, port)
	}
}