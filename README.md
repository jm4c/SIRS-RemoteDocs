RemoteDocs
==========

Platform
--------

The project is cross-platform. We tested it on Windows 10 64-bit and Linux 64-bit, but it should also run on OS X.

The only required dependency is the Java Development Kit 8.


Setup instructions
------------------

To build the project, run
```sh
./gradlew build
```

Then, start the server with
```sh
./gradlew :rdocs-server:run
```
and the client(s) with
```sh
./gradlew :rdocs-client:run
```


Client usage
------------

The server address may be configures in `rdocs-client/src/main/resources/client.properties` (the default is localhost).

First, register a new user by clicking register and filling in the form.

Then, login with the user you just created and create a new document by clicking "New".

Give a title to the document, type in some text and click "Save", then "Close". You can reopen this document by selecting it and clicking "Open".

Go back to the login window by clicking "Logout" and register a new user. Login with this new user, create a new document and share it with the first user by clicking "Share" and then dragging the name of the user you want to share it with to the right pane and clicking "Save".

Login again with the first user, and notice the the shared document is now accessible to you.

When you open a shared document, you can see who is the owner (creator) of the document, and also see who made the last edit and when.
