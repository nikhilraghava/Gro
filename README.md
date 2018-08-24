# Gro

Gro is an intuitive voice-controlled smart gardening system targeting the Android Operating System. It aims to ease the hassle of daily continued management of urban gardens in cities using an Internet-of-Things (IoT) enabled irrigation system and the Gro Android application. Users can install the Gro Android app on their Android smartphones and remotely manage and monitor their garden using simple voice commands. The application also allows the users to set schedules which will trigger the IoT enabled irrigation system to automatically water the plants on a daily basis at a user-defined time.

Gro’s user interface is minimalistic and easy to use. Using the application is as simple as talking to someone because users don’t have to go through the trouble of learning to navigate the user interface.

## Voice Commands

To water the plants forthwith:

>   “Water my plants now”
    
To water the plants at a scheduled time:

>   “Water my plants at 4:55 p.m.”
    
To cancel the schedule:

>   “Cancel my schedules”
    
To get the ambient temperature reading:

>   “What is the current temperature?”
    
To get the ambient humidity reading: 

>   “What is the humidity now?"
    
Voice commands are processed by a regular expression (regex) engine that searches for keyword patterns and performs actions accordingly. Thus grammar and word ordering are not important. This makes the application very intuitive because users don’t have to remember application specific voice commands.

Gro is smart enough to alert users when rain is forecast and they wish to set a schedule for the day. This feature will check if rain is forecast within the next 24 hours and if users set a schedule within the timeframe, Gro will inform the user that it might be unnecessary to water the plants today because rain is forecast within the next 24 hours.

## Author

Nikhil Raghavendra (@nikhilraghava)
