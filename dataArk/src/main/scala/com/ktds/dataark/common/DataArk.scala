package com.ktds.dataark.common

import akka.actor.ActorRef
import org.apache.spark.util.StatCounter
import org.joda.time.DateTime


object DataArk {

    sealed trait DataArkModel extends Serializable
    case class StreamConfig(
	name: String,
	windowLength: Int,
	slidingInterval: Int
    ) extends DataArkModel
    
}

object DataArkEvent {
    import DataArk._

    sealed trait AppEvent extends Serializable

    sealed trait LifeCycleEvent extends AppEvent

    case class Configuration(config: StreamConfig) extends LifeCycleEvent
    case class Startup(name: String) extends LifeCycleEvent
    case class Shutdown(name: String) extends LifeCycleEvent

}
