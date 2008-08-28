#!/bin/sh

# For this script to work:
# 1/ Get the jars from http://dev.xwiki.org/xwiki/bin/download/Drafts/Documenting+XWiki+Velocity+Macros/velocidoc%2Dbin.zip
# 2/ Copy the jars in ./bin
# 3/ Create a curriki directory
# 4/ Create symbolic links in the curriki directory to the directories you want to parse for velocity macros
# Here is a list of directories:
# currikiskin -> ../../src/main/webapp/skins/curriki8
# currikitemplates -> ../../src/main/webapp/templates
# wiki -> ../../../wiki/src/main/resources
# xwikitemplates -> ../../../../platform/web/standard/src/main/webapp/templates
# albatross -> ../../../../platform/skins/albatross/src/main/resources/albatross/
# You can create the links to the curriki dir with the following commands:
#   ln -s ../src/main/webapp/skins/curriki8 ./curriki/currikiskin
#   ln -s ../src/main/webapp/templates/ ./curriki/currikitemplates
#   ln -s ../../wiki/src/main/resources ./curriki/wiki

./bin/velocidoc.sh -src ./curriki/ -dst currikivelocityapi.zip -z
