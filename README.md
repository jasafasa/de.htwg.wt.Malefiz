The Malefiz project for the lecture web technologies
=====================================

This is the WebView to the [Malefiz](https://github.com/TiHau/de.htwg.se.Malefiz) Game, for the login is used [Silhouette](https://github.com/mohiva/play-silhouette).

## Where can i play it?

You can find a running example of this WebView under the following URL: https://malefiz.herokuapp.com/

## Features

* Sign Up
* Sign In (Credentials)
* Social Auth (Google+)
* Avatar service
* Remember me functionality
* Password reset/change functionality
* Account activation functionality over mock(the logs)
* Polymer(https://github.com/jasafasa/de.htwg.wt.Malefiz/tree/polymer)
* Bootstrap
* WebSockets
* Vue
* AJax & JQuery
* Play Framework


## Game specific Rest-API from the Play routes!
| Method | URI | Description |
| ---- | ----- | ----- |
| GET | /malefiz | The malefiz game page. |
| GET | /about | The about page. |
| GET | /new/\[2-4]| starts a new game with set player count. |
| GET | /touch/x/y | touches the field at x(0-16),y(0-15). |
| GET | /turn | ends your turn. |
| GET | /undo | undo what you done in your turn. |
| GET | /redo | redo what you done in your turn. |
| GET | /gameJson | returns the current game state as json. |
| GET | /websocket | Initialize Websocket |
| GET | /signOut | logs you out |
| GET | /signUp | signUp page |
| POST | /signUp | send added signUp data |
| GET | /signIn | signIn page |
| POST | /signIn | send added signIn data |


# License

The code is licensed under [Apache License v2.0](http://www.apache.org/licenses/LICENSE-2.0).
