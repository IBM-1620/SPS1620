#!/usr/bin/bash
rm -rf SPS1620.jar *.class SPS1620
/cygdrive/c/Program\ Files/Java/jdk1.8.0_333/bin/javac *.java
mkdir SPS1620
mv *.class SPS1620
/cygdrive/c/Program\ Files/Java/jdk1.8.0_333/bin/jar cfm SPS1620.jar MANIFEST.MF SPS1620
