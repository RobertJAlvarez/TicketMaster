#!/bin/bash

#This line calculate number of lines in all .java files in this directory
ls *.java ticketmaster/*.java ticketmaster/display/*.java | xargs wc -l
