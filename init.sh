#!/bin/sh
# 
# Before building the maven project, start with installing the GWT gadgets jar file locally since there is no
# repository for the version 1.2.0 which is used for this project.
# 
# @author Nabil Benothman

echo "\n[INFO] Installing the GWT gadgets jar file locally\n"


mvn install:install-file -Dfile=gadgets/gwt-gadgets-1.2.0.jar -DgroupId=com.google.gwt.google-apis \
    -DartifactId=gwt-gadgets -Dversion=1.2.0 -Dpackaging=jar

#mvn install:install-file -Dfile=smartgwt/smartgwt.jar -DgroupId=com.smartgwt \
#    -DartifactId=smartgwt -Dversion=2.4 -Dpackaging=jar

#mvn install:install-file -Dfile=smartgwt/smartgwt-skins.jar -DgroupId=com.smartgwt \
#    -DartifactId=smartgwt-skins -Dversion=2.4 -Dpackaging=jar


