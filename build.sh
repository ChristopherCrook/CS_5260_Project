#!/bin/bash

javac Hermes/Scheduler.java
javac Hermes/Resources/Resource.java
javac Hermes/Resources/Status.java

javac Hermes/Transforms/HousingTransform.java
javac Hermes/Transforms/AlloyTransform.java
javac Hermes/Transforms/ElectronicsTransform.java

javac Hermes/Country.java

javac TestRunner.java
##javac -Xlint:deprecation TestRunner.java
