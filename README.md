# cultbeautytest
Command line application for open movie db api

Dedcided to use parts of the play framework for async requests and json parsing and then to run from command line as a jar.

Used assembly to build a jar containing all dependencies thus usage:

`java -jar movies-assembly-1.0.jar movieName `

To build the jar you will need sbt:

`sbt` 
`>assembly`
