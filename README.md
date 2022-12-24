# Server

## Introduction
This is my first networks related project, and I just wished to get a better 
understanding of how Java (or any high-level language) would implement a client-server relationship.
Moreover, I wanted to see how security would play into managing user information and how to protect
certain pieces of information.

## Features
This project maintains the ability to create multiple client-side connections with the use of threads, 
and returns the input that the user gives to it. A database is simulated for the user data, and it is
loaded in when the server is created and written-through when an account is created. This utilizes the 
Linux filesystem to offer data persistency.

A very basic level of protection is offered with the Java Security library. Passwords are given a 8-byte salt.
They are then put through a PBKDF2 and SHA1 hash 20,000 times and saved to the datastore. 

When establishing a connection, a user needs to first 'create' an account by creating a Client and
calling authenticate() with the Client object with the 'create' parameter as true. They can later connect to the Server
with the correct credentials with the 'create' parameter as false when calling authenticate().
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
I hope to continue exploring how networks are used. Possible network additions are:
* more complex behavior between clients and servers
* giving priority to certain clients
* connecting this type of behavior to a more interactive GUI

Regarding Security:
I understand that SHA256 is more secure, and it is not the best practice to store the passwords
in an easily accessible location.
* create a database to hold the passwords
* add a middle layer of authentication before reaching the servers
* use a different hashing algorithm