#/bin/bash

VER=$(sed -r -n -e '/MIDlet-Version:/p' MinskTransSched/BusSchedule.jad | sed -r -e 's/MIDlet-Version: //')
rm -fR build
svn export . build
cd build
7z a ../MinskTransSched_v$VER.src.7z -r *
7z a -tzip ../MinskTransSched_v$VER.src.zip -r *
cd ..
rm -fR build/*

mv MinskTransSched_v$VER.src.* build/
cp MinskTransSched/deployed/MinskTransSched.jad build/BusSchedule_v$VER.jad
cp MinskTransSched/deployed/MinskTransSched.jar build/BusSchedule_v$VER.jar