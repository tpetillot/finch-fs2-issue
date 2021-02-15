name := "finch-fs2"
version := "0.1"
scalaVersion := "2.13.4"

libraryDependencies += "org.typelevel"      %% "cats-core"      % "2.3.1"
libraryDependencies += "org.typelevel"      %% "cats-effect"    % "2.3.1"

libraryDependencies += "io.chrisdavenport"  %% "log4cats-slf4j"  % "1.1.1"
libraryDependencies += "org.slf4j" % "slf4j-simple" % "1.7.30"

libraryDependencies += "co.fs2" %% "fs2-core" % "2.4.4"

libraryDependencies += "com.github.finagle"   %% "finchx-core"    % "0.32.1"
libraryDependencies +=  "com.github.finagle"  %% "finchx-fs2"     % "0.32.1"
libraryDependencies +=  "com.github.finagle"  %% "finchx-circe"   % "0.32.1"

libraryDependencies +=  "io.circe"            %% "circe-core"     % "0.13.0"
libraryDependencies +=  "io.circe"            %% "circe-generic"  % "0.13.0"
