[<img src="https://img.shields.io/travis/playframework/play-scala-starter-example.svg"/>](https://travis-ci.org/playframework/play-scala-starter-example)

# HTWG play-scala-seed

This is a starter application using Play for the Lecture Web Technologies at HTWG.

## Setup

To set up your project follow these steps:
* Fork this project on github
* Rename the project on your github account (Settings > Repository name)
* Load your forked project in IDEA (File > New > Project from  Version Controll > Github)
* Go to build.sbt file, IDEA should offer to import the project structure from sbt. This will take several minutes. 
* Continue only after import finished. That is the case when red fonds disappear. 
* Go to a Scala file. IDEA should offer to connect to a Java VM and Scala SDK. 
* Select Java 1.8
* Select the same Scala SDK as your project from Software Engineering is running on, Scala 2.12.4 recommended.


* Open the Project Structure (File > Project Structure)
* Make sure the Project Structure > Libraries contains the Scala-sdk libraries in the correct version (2.12.4)
* Make sure your module is not imported twice. If there are two (root, <your project>), delete <your project> and keep the root. 
* Make sure in app > controllers you can create new Scala classes from the context menu.

## Running

* In the IDEA Terminal type in sbt run. This will download play, the first time this also takes some time. 

```
sbt run
```

And then in your browser go to http://localhost:9000. 
Now your project files are compiled, so this again takes some time the first time arround. 
The default page is actually a very good introduction to play. Read it!

## First changes

Go to the view index.scala.html. Change it, so that the home page just shows "Hello World"

## Integrate your Game

To access your existing game from the Software Engineering class do the following

* In the play project, add a lib folder at the top level.
* Load your SE game project as a module (File > New > Module from existing Sources)
* In the project structure, add an Artefact (File > Project Structure > Artefact), select JAR from Module, select your game.
* Set the output folder for the artefact to the lib folder you just created
* Generate the jar by (Build > Build Artefacts)
* Import the jar into IDEA build path (File > Project Structure > Libraries > +)
* Play automatically uses jars in the lib, but you need to restart the play server

Now you should be able to access the classes of your game from your play Controller classes. 