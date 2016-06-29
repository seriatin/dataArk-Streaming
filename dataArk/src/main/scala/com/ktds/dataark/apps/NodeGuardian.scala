package com.ktds.dataark.apps

import scala.collection.immutable
import scala.concurrent.duration._
import akka.actor._
import akka.cluster.Cluster
import akka.pattern.gracefulStop

import org.apache.spark.streaming.StreamingContext
import org.apache.spark.{SparkConf, SparkContext}

import com.ktds.dataark.common._

class NodeGuardian(settings: DataArkSettings)
    extends ClusterAware with AggregationActor with ActorLogging {

    import DataArk._
    import DataArkEvent._
    import settings._

    val streaming = context.actorOf(Props(new StreamingActor(settings)), "dataArk-Streaming")

    override def preStart(): Unit = {
	log.info("Starting at {}", cluster.selfAddress)
	cluster.joinSeedNodes(immutable.Seq(self.path.address))
    }

    override def postStop(): Unit = {
	log.info("Node {} shutting down.", cluster.selfAddress)
	cluster.leave(self.path.address)
    }

    override def receive = actions orElse super.receive

    def actions : Actor.Receive = {
	case e: LifeCycleEvent =>
	    log.debug("Send Event to Streaming Actor {} ", e)
	    streaming forward e
	case PoisonPill =>
	    gracefulShutdown()
    }
   
    def gracefulShutdown(): Unit = {
	context.children foreach { c =>
	    context.unwatch(c)
	    context.stop(c)
	}
    }
     
}









class ClusterAware extends Actor with ActorLogging {
    import akka.cluster.ClusterEvent._

    val cluster = Cluster(context.system)

    override def preStart(): Unit = 
	cluster.subscribe(self, classOf[ClusterDomainEvent])

    override def postStop(): Unit = cluster.unsubscribe(self)

    def receive: Actor.Receive = {
	case MemberUp(member) =>
	    log.info("Member is Up: {}", member.address)
	case UnreachableMember(member) =>
	    log.info("Member detected as unreachable: {}", member)
	case MemberRemoved(member, previousStatus) =>
	    log.info("Member is Removed: {} after {}", member.address, previousStatus)
	case _: MemberEvent => // ignore
    }
}
