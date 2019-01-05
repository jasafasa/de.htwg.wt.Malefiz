
import {
    LitElement,
    html
} from 'https://unpkg.com/@polymer/lit-element/lit-element.js?module';

class MenuBar extends LitElement {

    static get properties() {
        return {
            player: {
                type: Number
            },
            message: {
                type: String
            },
            diced: {
                type: Number
            }
        }
    }

    render() {
        return html `
           <style>
          .navbar {
  overflow: hidden;
  background-color: #333;
  font-family: Arial;
}

/* Links inside the navbar */
.navbar a {
  float: left;
  font-size: 16px;
  color: white;
  text-align: center;
  padding: 14px 16px;
  text-decoration: none;
}

/* The dropdown container */
.dropdown {
  float: left;
  overflow: hidden;
}

/* Dropdown button */
.dropdown .dropbtn {
  font-size: 16px;
  border: none;
  outline: none;
  color: white;
  padding: 14px 16px;
  background-color: inherit;
  font-family: inherit; /* Important for vertical align on mobile phones */
  margin: 0; /* Important for vertical align on mobile phones */
}

/* Add a red background color to navbar links on hover */
.navbar a:hover, .dropdown:hover .dropbtn {
  background-color: red;
}

/* Dropdown content (hidden by default) */
.dropdown-content {
  display: none;
  position: absolute;
  background-color: #f9f9f9;
  min-width: 160px;
  box-shadow: 0px 8px 16px 0px rgba(0,0,0,0.2);
  z-index: 1;
}

/* Links inside the dropdown */
.dropdown-content a {
  float: none;
  color: black;
  padding: 12px 16px;
  text-decoration: none;
  display: block;
  text-align: left;
}

/* Add a grey background color to dropdown links on hover */
.dropdown-content a:hover {
  background-color: #ddd;
}

/* Show the dropdown menu on hover */
.dropdown:hover .dropdown-content {
  display: block;
}
</style>
 <div class="navbar">
  <a href="/">Malefiz</a>
  <a href="/info">Infopage</a>
  <div class="dropdown">
    <button class="dropbtn">Action
      <i class="fa fa-caret-down"></i>
    </button>
    <div class="dropdown-content">
      <a href="/undo">undo</a>
      <a href="/redo">redo</a>
      <a href="/turn">next</a>
    </div>
  </div>
    <div class="dropdown">
    <button class="dropbtn">New Game
      <i class="fa fa-caret-down"></i>
    </button>
    <div class="dropdown-content">
      <a href="/new/2">2 player</a>
      <a href="/new/3">3 player</a>
      <a href="/new/4">4 player</a>
    </div>
  </div>
   <a id="state-message" class="navbar-brand" href="#">${this.message}   </a>
        <a class="navbar-brand" href="#">
            Player: ${this.player}
        </a>
        <a class="navbar-brand" href="#">
            Diced: ${this.diced}
</a>
</div> 

        `;
    }
}
customElements.define('menu-bar', MenuBar);

