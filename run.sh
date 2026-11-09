#!/bin/sh
# Mac / Linux version of run.bat:  ./run.sh   |  ./run.sh check   |  ./run.sh setup
cd "$(dirname "$0")" || exit 1
TARGET=Main
[ "$1" = "check" ] && TARGET=Checkpoint
[ "$1" = "setup" ] && TARGET=CheckSetup
rm -rf out
javac -encoding UTF-8 -d out src/*.java || { echo; echo " Compile failed. Read the FIRST error above."; exit 1; }
java -Djava.net.useSystemProxies=true -cp out "$TARGET"
