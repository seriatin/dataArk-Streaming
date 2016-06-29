package com.ktds.dataark

import org.apache.spark._
import org.apache.spark.SparkContext._
import org.apache.spark.streaming._
import org.apache.spark.streaming.StreamingContext._
import org.apache.spark.storage.StorageLevel
import org.apache.spark.rdd._
import org.apache.spark.sql._
import org.apache.spark.sql.functions._
import org.kie.api.runtime.StatelessKieSession

import com.cloudera.sprue.Patient

object SepsisStream {
    def main(args: Array[String]) {
	val batchInterval = 5
	val sparkConf = new SparkConf().setMaster("local[2]").setAppName("DataArk Rules Streaming")
	val sc = new SparkContext(sparkConf)
	val ssc = new StreamingContext(sc, Seconds(batchInterval))
	val sqc = new SQLContext(sc)

	val patientQueueRDD = scala.collection.mutable.Queue[RDD[Patient]]()
	val patientStream = ssc.queueStream(patientQueueRDD)
	val rulesExecutor = new RulesExecutor()

	for (batch <- 1 to 100) {
	    val randomPatients = PatientDataGenerator.getPatientList(batch)
	    val rdd = ssc.sparkContext.parallelize(randomPatients)
	    patientQueueRDD += rdd
	}

	patientStream.foreachRDD(rdd => {
	    val eveluatedPatients = rdd.mapPartitions(incomingEvents => { rulesExecutor.evalRules(incomingEvents) })

	    import sqc.implicits._

	    val patientdf = sqc.createDataFrame(eveluatedPatients, classOf[Patient])
	    //val patientdf = eveluatedPatients.toDF()

	    println("Total Patients in batch: " + patientdf.count())
	    println("Patients with atleast one condition: " + patientdf.filter("sirsFlag > 0").count())

	})
	ssc.start()
	ssc.awaitTermination()
    }
}
