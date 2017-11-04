# Distributed Systems - 2016/2017
#### University of Coimbra - Department of Informatics Engineering
###### Bachelor Degree in Informatics Engineering
<br>
João Feliciano - jamaia@student.dei.uc.pt
<br>
Luís Coimbra - lcoimbra@student.dei.uc.pt
<br>
Luís Silva - lfgsilva@student.dei.uc.pt

A project about inverted auctions, where the lowest bid wins.<br>
First deadline following a Client-Server architecture and the second deadline adding a web interface following a MVC architecture.<br>
Technologies and a more detailed description of the project below.

## Deadline 1
Clients communicate through command line (i.e. telnet) with protocol visible in sd_projecto_meta1_v4.pdf.

* SQL Database - SQL script inside folder (db_script.sql).
* RMI Servers with fail over, first online is the main one, the rest is backup - Servers responsible for communicating with the database.
* TCP Servers, communicating between them using multicast - Receive client commands, execute methods on RMI, has interface to enable RMI callbacks.

## Deadline 2
Clients can use both the command line and a web interface where they can now use a Facebook account to register and login.

* Web Server with MVC architecture:
 * Java Servlets;
 * Struts 2;
 * JSP;
 * JavaBeans;
 * Websockets.
* REST
 * Facebook - register/log in and post on profile.
 * eBay - search items and show lowest price.

## To run
* Get database;
* Fill iBei.properties info;
* DetailAuctionAction.java - EBAY_APP_ID variable;
* fbAssociateAction.java - apiKey & apiSecret variables.
