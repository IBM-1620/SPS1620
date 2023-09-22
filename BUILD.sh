#!/usr/bin/bash
rm -rf SPS1620.jar *.class SPS1620
javac *.java
mkdir SPS1620
mv *.class SPS1620
jar cfm SPS1620.jar MANIFEST.MF SPS1620
