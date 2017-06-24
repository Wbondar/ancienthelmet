package ancienthelmet.client

import akka.actor.ActorSystem
import akka.stream.ActorMaterializer
import akka.stream.scaladsl.Flow
import akka.stream.scaladsl.Sink
import akka.stream.scaladsl.Source
import akka.stream.scaladsl.Tcp
import akka.util.ByteString

import com.jme3.app.SimpleApplication;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.renderer.RenderManager;
import com.jme3.scene.Geometry;
import com.jme3.scene.shape.Box;

import scala.util.Failure
import scala.util.Success

class AncientClient(val actorSystem: ActorSystem, val address: String, val port: Int) extends SimpleApplication {
	implicit val sys = actorSystem
	implicit val actorMaterializer = ActorMaterializer()
	import actorSystem.dispatcher
	val connection = Tcp().outgoingConnection(address, port)

	override def simpleInitApp(): Unit = {
        val b = new Box(1, 1, 1)
        val geom = new Geometry("Box", b)

        val mat = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md")
        mat.setColor("Color", ColorRGBA.Blue)
        geom.setMaterial(mat)

        rootNode.attachChild(geom)
	}

	override def simpleUpdate(tpf: Float): Unit = {		
		val testInput = ('a' to 'z').map(ByteString(_))

		val result = Source(testInput).via(connection)
			.runFold(ByteString.empty) { (acc, in) ⇒ acc ++ in }

		result.onComplete {
			case Success(successResult) =>
				println(s"Result: " + successResult.utf8String)
				//println("Shutting down client")
				//actorSystem.terminate()
			case Failure(e) =>
				println("Failure: " + e.getMessage)
				actorSystem.terminate()
		}
	}

	override def stop(): Unit = {
		super.stop()
		actorSystem.terminate()
	}
}