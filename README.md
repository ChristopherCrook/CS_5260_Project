####### README FOR HERMES - PROJECT 1 FOR CS 5260 #######

Prerequisites: Java 8 or higher, an OS that provides the Linux or RHEL
kernel (the developer used Ubuntu), and Mozilla Firefox browser

Building: run the build.sh script
> build.sh

To Run class tests:
> java TestRunner

To Run individial Tests (1 thru 5)
> java Test<number>

To clean the build:
> ./clean_build.sh

To sanitize the directory of all output files:
> ./clean_text_files.sh

####### PROJECT 2 FOR CS 5260 - ALIEN INVASION #######

This section discusses Project 2, which uses Hermes to simulate 
countries for the AlienInvasion app. The AlienInvasion program 
will start a variety of countries and, in-between each schedule, 
will carry out a random attack.

Each attack will roll() a TwentySidedDie class instance to get 
the roll amount. A modifier is also calculated to factor in the 
effectiveness of the attack for a particular roll, given the status
of the country being attacked.

This program also CivetWeb-1.15, which is a copyrighted, free software 
program. Please read the LICENSE.md and README.md in the
civetweb-1.15/ directory.

To Run the program, perform a build using the following command:
> build.sh

Navigate to another shell window and navigate to the CS_5260_Project/
directory. Enter the following command to bring up the CivetWeb app 
in a browser:
> ./civetwebapp

Run the program in the main shell window using the following command:
java AlienInvasion

The program can run up to 3 hours in some instances. 

To clean the build:
> ./clean_build.sh

To sanitize the directory of all output files:
> ./clean_text_files.sh
