# VLC is a great platform
This repository is deticated for a console java program which's goal is to track, analyze, save to sql and export as a html file a report about the listened music.  

#### PROJECT is in a CLEANUP state (delete when cleanup is done)
- - -
## Recomendations and setup

Usage is easy, if you want to build yourself run WIP ```build/main```, otherwise you can download the jar file in the releases tab in github. 
It is recommeneded to have git bash (windows) or be on a UNIX system on your system for easier module usuage.
Have a MySQL server started <----- THIS WILL BE CHANGED (SQLite rewrite 

## Use
There are 2 (3) main args that you can use inside of a bash cmd ``track`` and ``export`` (see below for info on those). 
The jar can be executed simply with the following command ``java -jar <JAR_NAME> <track/export>``.

I also have provided inside of ```main_package/scripts``` quick use scripts for two actions:
* running the tracker ``tracker.sh`` 
* * runs the jar file with args ``track``, a line will show up: ``Would you like to turn on VLC`` and VLC will hopefully open and the tracking will begin
* running the exporter ``createReport.sh``
* * runs the jar file with args ``export``, a cmd will

## Requirements
* VLC ```3.0.21 Vetinari```, any older version is not tested, you can open an issue request in the future when ```3.1``` releases
* Java JDK version 21+, any older will not work
* DEPRICATED -> Python 3.11

- - -
## Known issues
1. The program uses a lib I wrote ``SQLnow``, but the implementation is buggy, currently a goal is to rewrite the few lines of lib usage, grab the logger from there and stop using the lib entirely
2. When VLC is not open and the program asks the user if they want to open it, the program can open VLC, but not start tracking and ask the user if they want to open VLC again
3. The VLC path is hardcoded, that can be fixed by creating a config file
4. MySQL support will be dropping and I will be focusing on rebasing into SQLite for way easier startup

- - -
## Contribution
* New to github here! I am almost sure I removed the ability to contrubute? If I didn't and you are deticated to help out, I recomend using IntelliJ IDEA
* Run the maven ``pom.xml`` to download the used maven libraries
