package com.ktds.dataark.common

import scala.util.Try
import com.typesafe.config.{Config, ConfigFactory}
import org.apache.spark.Logging

final class DataArkSettings(conf: Option[Config] = None) extends Serializable with Logging {

    val rootConfig = conf match {
	case Some(c) => c.withFallback(ConfigFactory.load)
	case _ => ConfigFactory.load
    }

    protected val spark = rootConfig.getConfig("spark")

    val SparkMaster = withFallback[String](Try(spark.getString("master")),
	"spark.master") getOrElse "local[*]"

    protected val dataArk = rootConfig.getConfig("dataArk")

    val AppName = dataArk.getString("app-name")


    private def withFallback[T](env: Try[T], key: String): Option[T] = env match {
	case null => None
	case value => value.toOption
    }

}
