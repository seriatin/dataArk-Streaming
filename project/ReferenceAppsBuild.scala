import sbt._
import sbt.Keys._

object ReferenceAppsBuild extends Build {

    import Settings._

    lazy val root = Project(
        id = "root",
        base = file("."),
        settings = parentSettings,
        aggregate = Seq(dataArk)
    )

    lazy val dataArk = Project(
        id = "dataArk",
        base = file("./dataArk"),
        settings = defaultSettings ++ Seq(libraryDependencies ++= Dependencies.app)
    )

}

object Versions {
    val Akka	= "2.3.15"
    val JDK     = "1.7"
    val Scala   = "2.10.5"
    val Spark   = "1.6.1"
    val Kafka   = "0.8.2"
    val Drools	= "6.4.0.Final"
    val JodaConvert = "1.7"
    val JodaTime    = "2.4"
}

object Dependencies {

    import Versions._

    object Compile {
	val akkaActor = "com.typesafe.akka" %% "akka-actor" % Akka
	val akkaCluster = "com.typesafe.akka" %% "akka-cluster" % Akka
	val akkaRemote = "com.typesafe.akka" %% "akka-remote" % Akka
	val akkaSlf4j = "com.typesafe.akka" %% "akka-slf4j" % Akka
	val jodaTime = "joda-time" % "joda-time" % JodaTime
	val jodaConvert = "org.joda" % "joda-convert" % JodaConvert
        val sparkStreaming = "org.apache.spark" %% "spark-streaming" % Spark 
        val kafkaStreaming = "org.apache.spark" %% "spark-streaming-kafka" % Spark exclude("com.google.guava", "guava") exclude("org.apache.spark", "spark-core")
        val sparkSQL = "org.apache.spark" %% "spark-sql" % Spark exclude("com.google.guava", "guava") exclude("org.apache.spark", "spark-core")
	//val ruleLibrary = "com.cloudera" % "sprue" % "1.0" 
	val ruleLibrary = "com.cloudera" % "sprue" % "1.0" from "http://test06.bigfence.com:8580/kie-wb/maven2/com/cloudera/sprue/1.0/sprue-1.0.jar"
	val droolsKieApi = "org.kie" % "kie-api" % Drools
	val droolsKieCi = "org.kie" % "kie-ci" % Drools
	val droolsCore = "org.drools" % "drools-core" % Drools
	val droolsCompiler = "org.drools" % "drools-compiler" % Drools
	val droolsDecisiontables = "org.drools" % "drools-decisiontables" % Drools
    }

    import Compile._

    val akka = Seq(akkaActor, akkaCluster, akkaRemote, akkaSlf4j)

    val drools = Seq(droolsKieApi, droolsKieCi, droolsCore, droolsCompiler, droolsDecisiontables)

    val core = akka ++ drools ++ Seq(jodaConvert, jodaTime)

    val app = core ++ Seq(ruleLibrary, sparkStreaming, sparkSQL, kafkaStreaming)

}

object Settings extends Build {
    import net.virtualvoid.sbt.graph.Plugin.graphSettings
    import com.scalapenos.sbt.prompt.SbtPrompt.autoImport._
    import com.scalapenos.sbt.prompt.PromptTheme

    lazy val buildSettings = Seq(
        name := "DataArk Streaming App",
        normalizedName := "DataArk",
        scalaVersion := Versions.Scala,
        promptTheme := theme,
	resolvers := Seq(
	    "Droole Repository" at "http://repository.jboss.org/nexus/content/groups/public/",
	    "guvnor-m2-repo" at "http://test06.bigfence.com:8580/kie-wb/maven2/"
	),
	credentials += Credentials("KIE Workbench Realm", "test06.bigfence.com", "workbench", "workbench1!")
    )

    val parentSettings = buildSettings ++ Seq(
        publishArtifact := false,
        publish := {}
    )

    override lazy val settings = super.settings ++ buildSettings

    lazy val defaultSettings = graphSettings ++ Seq(
	autoCompilerPlugins := true,
	libraryDependencies <+= scalaVersion { v => compilerPlugin("org.scala-lang.plugins" % "continuations" % v) },
	scalacOptions ++= Seq("-encoding", "UTF-8", s"-target:jvm-${Versions.JDK}", "-feature", "-language:_", "-deprecation", "-unchecked", "-Xfatal-warnings", "-Xlint"),
	javacOptions in Compile ++= Seq("-encoding", "UTF-8", "-source", Versions.JDK, "-target", Versions.JDK, "-Xlint:deprecation", "-Xlint:unchecked" ),
	javaOptions ++= Seq("-Dkie.maven.settings.custom=classpath://settings.xml"),
	ivyLoggingLevel in ThisBuild := UpdateLogging.Quiet,
	parallelExecution in ThisBuild := false,
	parallelExecution in Global := false	
    )

    lazy val theme = PromptTheme(List(
	text("[SBT] ", fg(green)),
	userName(fg(000)),
	text("@", fg(000)),
	hostName(fg(000)),
	text(":", fg(000)),
	gitBranch(clean = fg(green), dirty = fg(20)),
	text(":", fg(000)),
	currentProject(fg(magenta)),
	text("> ", fg(000))
    ))
}
