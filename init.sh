#!/bin/sh
# 
# Before building the maven project, start with installing the gadget jar locally since there is no
# repository for the version 1.2.0 which is used for this project.
# 
# @author Nabil Benothman

echo "\n[INFO] Installing the GWT gadgets jar file locally\n"


mvn install:install-file -Dfile=gadgets/gwt-gadgets-1.2.0.jar -DgroupId=com.google.gwt.google-apis \
    -DartifactId=gwt-gadgets -Dversion=1.2.0 -Dpackaging=jar


