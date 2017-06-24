package ancienthelmet.client

import akka.actor.ActorSystem

object Main {
	/**
	* Use parameters `0.0.0.0 6001` to start server listening on port 6001.
	*/
	def main(args: Array[String]): Unit = {
		val (address, port) =
			if (args.length == 2) (args(0), args(1).toInt)
			else ("127.0.0.1", 6000)

		val system = ActorSystem()
		val app = new AncientClient(system, address, port)
        //app.setDisplayStatView(true)
        /*
         * Workaround for splash screen bug with LWJGL on Linux.
         */
        app.setShowSettings(false)
        app.start()
	}
}