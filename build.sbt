name := "cultbeautytest"

version := "1.0"

scalaVersion := "2.11.7"

name := "movies"

version := "1.0"

scalaVersion := "2.11.7"

libraryDependencies +=
  "com.typesafe.play" %% "play-ws" % "2.4.3" excludeAll
    ExclusionRule(organization = "commons-logging")

mainClass in assembly := Some("GetAndSortMovies")