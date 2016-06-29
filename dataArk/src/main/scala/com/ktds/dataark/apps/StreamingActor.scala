package com.ktds.dataark.apps

import akka.pattern.pipe
import akka.actor.{ActorLogging, Actor, ActorRef}
import org.apache.spark.SparkContext
import org.apache.spark.SparkContext._
import org.joda.time.DateTime

import com.ktds.dataark.common._

class StreamingActor(settings: DataArkSettings)
    extends AggregationActor with ActorLogging {

    import settings._
    import DataArkEvent._
    import DataArk._

    import scala.collection.mutable
    val jobList = mutable.Map.empty[String, StreamConfig]

    def receive : Actor.Receive = {
	case Configuration(config) => jobConfiguration(config)
	case Startup(name) => startup(name)
	case Shutdown(name) => shutdown(name)
    }

    def jobConfiguration(config: StreamConfig): Unit = {
	log.info("Add Job ")
	jobList += ( config.name -> config )	
    }

    def startup(name: String): Unit = {
	log.info("Start Streaming Job")
    }

    def shutdown(name: String): Unit = {
	log.info("Shutdown Streaming Job")
    }
}

