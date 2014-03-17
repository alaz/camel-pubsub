organization := "com.osinka"

name := "seda-bus-test"

scalaVersion := "2.10.3"

scalacOptions ++= List("-deprecation", "-unchecked", "-feature")

mainClass in Compile := Some("org.apache.camel.spring.Main")

libraryDependencies ++= Seq(
  "org.apache.camel"	%  "camel-core"		% "2.12.3",
  "org.apache.camel"	%  "camel-spring"	% "2.12.3",
  "org.slf4j"		%  "slf4j-simple"	% "1.7.5"
)

