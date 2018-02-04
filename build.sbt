name := "lykke-waves-wallet"

version := "0.1"

libraryDependencies ++= Seq(
  "com.typesafe.akka" %% "akka-http" % "10.0.11",
  "com.wavesplatform" % "wavesj" % "0.3",
  "com.typesafe.akka" %% "akka-http-testkit" % "10.0.11" % Test
)

scalaVersion := "2.12.4"

// package
enablePlugins(JavaServerAppPackaging, DebianPlugin, JDebPackaging, GitVersioning)

javaOptions in Universal ++= Seq(
  // -J prefix is required by the bash script
  "-J-server",
  // JVM memory tuning for 1g ram
  "-J-Xms128m",
  "-J-Xmx1g",

  // from https://groups.google.com/d/msg/akka-user/9s4Yl7aEz3E/zfxmdc0cGQAJ
  "-J-XX:+UseG1GC",
  "-J-XX:+UseNUMA",
  "-J-XX:+AlwaysPreTouch",

  // probably can't use these with jstack and others tools
  "-J-XX:+PerfDisableSharedMem",
  "-J-XX:+ParallelRefProcEnabled",
  "-J-XX:+UseStringDeduplication")