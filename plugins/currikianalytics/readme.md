#Curriki Analytics Plugin
First release: 01.10.2013

Author: Felix Tscheulin

##What is it?
This is the developer readme for the CurrikiAnalyticsPlugin. A XWiki plugin used at Curriki to solve fundamental
needs to track user interactions with Currikis systems.

##How does it work?
This plugin provides a basic framework of code pieces making it easy to build further functionality based
on the user interactions with the system. For that it places a central data object in the users session which keeps
track of the history of visited urls, the CurrikiAnalyticsSession. Fur future needs this object may be mapped to a
database table intead of keeping data in the memory.

![Sequence-Diagram](http://www.websequencediagrams.com/cgi-bin/cdraw?lz=dGl0bGUgQ3VycmlraUFuYWx0aWNzUGx1Z2luCgpWaXNpdG9yLT4AGQc6AAwFIHBhZ2UKACwHABMJQW5hbHkAMwo6bG9nUGFnZVZpZXcoKQAmCAAWDwArElNlc3Npb246YWRkQ3VycmVudFVybFRvVXJsU3RvcmUALxsAdwlNb2R1bGVzOmNyYXcALQsAEBAAYhpnZXQAgQAHKCksZ2V0Q29va2llcygpLi4uADQTVHJpZ2dlcjp0AAIGKCkKAAsHABEKbWF0Y2gADQxOb3RpZmllcjphZGQABgZjYXRpb25zKCkscmVtb3ZlAAcPCgAqCC0-AIMOBzphZGQgcwCCKQYgZmxhZ3Mgb3IgYwCBJgYK&s=default "Sequence-Diagram")

<!--
title CurrikiAnalticsPlugin

Visitor->Curriki:Visit page
Curriki->CurrikiAnalyticsPlugin:logPageView()
CurrikiAnalyticsPlugin->CurrikiAnalyticsSession:addCurrentUrlToUrlStore()
CurrikiAnalyticsPlugin->AnalyticsModules:crawUrlStore()
AnalyticsModules->CurrikiAnalyticsSession:getSession(),getCookies()...
AnalyticsModules->Trigger:trigger()
Trigger->Trigger:match()
Trigger->Notifier:addNotifications(),removeNotifications()
Notifier->Visitor:add session flags or cookies
-->

##What is it able to do?
With the parts shown above it is possible to write specific analytics modules with a set of triggers which then can
have a set of notifiers.This three parts can be used to build more complex architectures. All analytics modules are
currently called on every requests and all triggers are run then.Triggers can decide on wether or not they see the need
to use the notifiers attached to them.

#Modules

##LoginToView Module
The LoginToView Analytics module is the first concrete implementation which uses the more general definitions of the
AnalyitcsModule, Triggers and Notifiers. It consists of the LoginToViewAnalyitcsModule itself, a Trigger to match url
patterns of visited resources and a Notifier which has access to the users session and another Trigger to remove all
session entries when the module is turned off.

###Configuration
The LoginToView Analytics Module has two main pages for configuration
####CurrikiCode/LoginToViewConfig
This page is the main configuration file for this module. You can turn the whole LoginToView functionality on and off
and set the number of resources users are allowed to view before they get the login dialog. If this config gets changed
the AnalyticsModule gets notified of the change and reloads the whole configuration.

    ## This is the config for the LoginToViewAnalytics Module
    ## it holds all switches and values that are needed to control the
    ## log int to view functionality. Lines beginning with "##" are comments
    ## and are ignored by the system.

    ##Main switch, to turn the log in to view functionality on and off
    ##login_to_view=off
    login_to_view=on

    ##The threshold for the number of resources users are allowed to view
    ##E.g. 3 means that on the third view on a resource the user is locked out
    number_of_resources_to_view=3

    ##The number of dialogs to show before the last dialog appears and the
    ##user is locked out of the system, this number must be smaller than the threshold
    number_of_warnings=1


####CurrikiCode/LoginToViewExceptions
This pages lists the exeptions for the LoginToViewTrigger if a resource is on this list, it does never
trigger a login dialog even though the LoginToView is turned on. This can be used to make several
resources or collections accessible while demand users on others to login to view.

    /Coll_Admin/ggg
    /Coll_Group_Blubber/sfsdfsdf
    /Coll_Admin/test-collection


