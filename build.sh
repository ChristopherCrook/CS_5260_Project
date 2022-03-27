#!/bin/bash

echo 'building infrastructure'
javac Hermes/Scheduler.java
javac Hermes/Resources/Resource.java
javac Hermes/Resources/Status.java
javac Hermes/Node.java

javac Hermes/Transforms/HousingTransform.java
javac Hermes/Transforms/AlloyTransform.java
javac Hermes/Transforms/ElectronicsTransform.java

javac -Xlint:deprecation Hermes/Country.java

javac Hermes/Trade/Entry.java
javac Hermes/Trade/Manager.java

echo 'building tests'
javac TestRunner.java
javac Test1.java
javac Test2.java
javac Test3.java
javac Test4.java
javac Test5.java
javac GenerateTest.java

echo 'building complete'
##javac -Xlint:deprecation TestRunner.java
