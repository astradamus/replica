organization := "com.alexanderstrada"
name := "replica"
version := "0.0.1"
scalaVersion := "3.6.3"
val scalaTestVersion = "3.2.19"

Compile / scalaSource := baseDirectory.value / "src" / "main"
Test / scalaSource := baseDirectory.value / "src" / "test"

libraryDependencies += "org.scalatest" %% "scalatest" % scalaTestVersion % Test
