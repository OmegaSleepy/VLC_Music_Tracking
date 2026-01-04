# Sophisticated VLC tracking app
This repository is deticated for a console java program which's goal is to track, analyze, save to sqlite and export as an html file a report about the listened music (inside of VLC).  

- - -

## Requirements
* VLC ```3.0.21 Vetinari```, any older version is not tested, you can open an issue request in the future when ```3.1``` releases  
* Java JDK version 21+, any older will not work
* (only for building) Maven install listed in %PATH%

## Recomendations and setup

Usage is easy and does not require anything from the user other than clicking the .jar file <br>
If you want to build yourself run ```mvn clean install``` inside of your cmd, otherwise you can download the jar file in the releases tab in github.  <br>

## Use
There are 2 main args that you can use inside of a bash cmd ``track`` and ``export`` (see below for info on those).   
The jar can be executed simply with the following command ``java -jar <JAR_NAME> <track/export>``.  

I also have provided inside of ```main_package/scripts``` quick use scripts for two actions:  
* running the tracker ``tracker.sh`` 
* * runs the jar file with args ``track``, a line will show up: ``Would you like to turn on VLC`` and VLC will hopefully open and the tracking will begin
* running the exporter ``createReport.sh``  
* * runs the jar file with args ``export``, a cmd will  

- - -
## Known issues:
1. When an integer overflows (very rare) there is no way to revalidate SQLite Data
2. Due to poor rebase on my part, I lost some code and the program logging is not centralized

- - -
## Reason for creation
I love music, I love VLC.  
This app is aimed at people that like the social aspect of being tracked (ex. yt music "yearly recap" or Spotify Wrapped), but like the open nature of VLC.  
The app **does not** send any information to any server and works in localhost only. I will not make this project track, collect and sell information in any way. 
The information that it saves is saved only on your machine and it is your responsability to safely distribute the contents of the tracker and the sql table.
- - -
## Contribution
* New to github here! I am almost sure I removed the ability to contrubute? If I didn't and you are deticated to help out, I recommend using IntelliJ IDEA. Post a pull request, will check it out! 
* Run the maven ``pom.xml`` to download the used maven libraries
* Run ```mvn clean install``` in your cmd inside of the project folder 

- - -
   *Sending ☕ love* ~ OmegaSleepy creator of the app
