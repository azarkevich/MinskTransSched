# MinskTransSched

Build
---
-  Install JDK (JRE not enough)
-  Install git
-  Chekout project at any location
-  Install WTK (Wireless Toolkit)
-  Install Ant
-  Install Antenna (http://antenna.sourceforge.net/) for build j2me applications with Ant
-  Install Proguard

Develop in Eclipse
---
- Install Eclipse
- Setup using JDK in Eclipse
- Install MTJ plugin (from http://www.eclipse.org/mtj/)
- Setup global options for MTJ: 
	Antenna JAR
	WTK root
	Proguard dir
	Install all available devices in WTK
	Setup preverification (use project device configuration, default preverifier + specify path to preverifier in WTK)
- Run Eclipse, specify chekout folder as workspace. Appeared empty workspace.
- Import TSM project(existing project into workspace). Change settings for imported project:
	Change JavaME settings if them differ from global
	Set for Java Compiler specific settings:
		.class compatibility CLDC 1.1
	Set for encoding for text files UTF-8 (project wide)

Now you can compile and try run midlet in emulator.
	
-  Create .local.properties in root of working directory, if you want make releases and has access to project
	sourceforge.user=<username>,minsktranssched
	sourceforge.password=<user password>
-  TODO: how to build MTJ project? build.xml not generated automatically!

For run from command line
$WTKHOME/bin/emulator.exe -Xdescriptor:XXX.jad 

