package com.ktds.dataark

import akka.actor.{Props, ActorSystem, PoisonPill}
import com.ktds.dataark.common.DataArkSettings
import com.ktds.dataark.apps._

object DataArkApp extends App {
    val settings = new DataArkSettings
    import settings._

    val system = ActorSystem(AppName, rootConfig)
    val guardian = system.actorOf(Props(new NodeGuardian(settings)), "node-guardian")

    system.registerOnTermination {
	guardian ! PoisonPill	
    }
}
