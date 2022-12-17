# Server

## Introduction
This is my first networks related project, and I just wished to get a better understanding of how Java (or any high-level language) would implement a client-server relationship.

## Features
This project maintains the ability to create multiple client-side connections with the use of threads, and just returns the input that the user gives to it.

To close a connection, enter 'quit'

## Technologies
* Java SDK 1.8
* Java 8

## How to use

Clone this repository. 
Navigate to this repository's src/main/java 

To start the server, run:

*javac \*.java*

*java ServerStarter*

To create clients, utilize the Client class. You must provide a port and IP address known to you.
## TODO
I hope to continue exploring how networks are used. Possible additions are:
* more complex behavior between clients and servers
* giving priority to certain clients
* connecting this type of behavior to a more interactive GUI