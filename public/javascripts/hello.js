//let wsUri = "ws://localhost:9000/websocket";
let wsUri = "wss://malefiz.herokuapp.com/websocket";

function undo() {
    $.ajax(
        {
            type: 'GET',
            url: "undo"
        }
    )
}

function redo() {
    $.ajax(
        {
            type: 'GET',
            url: "redo"
        }
    )
}

function nextTurn() {
    $.ajax(
        {
            type: 'GET',
            url: "turn",
        }
    )
}

function newGame(count) {
    $.ajax(
        {
            type: 'GET',
            url: "new/" + count
        }
    )
}

function updateGame(data) {
    boardVue.game = data;
}

function resizeBoard() {
    let newVal = $('.field').width();

    $('.gameRow').each(function(){
        $(this).height(newVal);
    });
}

$(document).ready(function () {
    resizeBoard();
    connectWebSocket();
});

$(window).resize(function () {
    resizeBoard();
});

$('.field').each(function(){
    console.log('resize');
    $(this).height($(this).width());
});

$(document).keypress(function (event) {
    let keyCode = (event.keyCode ? event.keyCode : event.which);
    if (keyCode === 110) {
        nextTurn()
    }
    if (keyCode === 117) {
        undo()
    }
    if (keyCode === 114) {
        redo()
    }
});

function openNewGameDropdown() {
    document.getElementById("myDropdown").classList.toggle("show");
}

$(document).click(function (event) {
    //---close dropdown if opened----------
    if (!event.target.matches('.dropbtn')) {
        let dropDowns = document.getElementsByClassName("dropdown-content");
        for (let i = 0; i < dropDowns.length; i++) {
            let openDropDown = dropDowns[i];
            if (openDropDown.classList.contains('show')) {
                openDropDown.classList.remove('show');
            }
        }
    }
});

let fieldVueComponent = Vue.component('field', {
    template: '<div v-on:click="clicked" class="field"></div>',
    methods: {
        clicked: function () {
            let x = this.$attrs['x'];
            let y = this.$attrs['y'];

            $.ajax(
                {
                    type: 'GET',
                    url: "touch/" + x + "/" + y
                }
            )
        }
    }
});

let boardVue = new Vue({
    el: '#game',
    template: `<div class="game">
        <div class="gameState">
            <p class="alignleft">{{game.message}}</p>
            <p class="alignright">
                Player: <img v-bind:src="'/assets/images/player' + game.activePlayer + '.png'" class="stateImg">
                <img v-bind:src="'/assets/images/dice' + game.diced + '.png'" class="stateImg">
            </p>
        </div>
        <div class="board">
            <div v-for="row in game.rows" class="gameRow">
                <field v-for="field in row.fields" v-bind:x="field.x" v-bind:y="field.y" v-bind:class="compFieldClasses(field)"></field>
            </div>
        </div>
    </div>`,
    data: {
        game: []
    },
    methods: {
        compFieldClasses: function (field) {
            return {
                gameField: !field.isFreeSpace,
                markedField: field.avariable,
                p1Stone: field.sort === '1',
                p2Stone: field.sort === '2',
                p3Stone: field.sort === '3',
                p4Stone: field.sort === '4',
                blockStone: field.sort === 'b'
            }
        }
    },
    components: {
        'field': fieldVueComponent
    }
});

function connectWebSocket() {
    websocket = new WebSocket(wsUri);
    websocket.setTimeout
    websocket.onopen = function(event) {
        console.log("Connected to Websocket");
        websocket.send('message'); //update game
    }

    websocket.onclose = function () {
        console.log('Connection with Websocket Closed!');
        websocket = new WebSocket(wsUri);
        connectWebSocket();
    };

    websocket.onerror = function (error) {
        console.log('Error in Websocket Occured: ' + error);
        websocket = new WebSocket(wsUri);
        connectWebSocket();
    };

    websocket.onmessage = function (e) {
        if (typeof e.data === "string") {
            let data = JSON.parse(e.data);
            updateGame(data);
        }
    };

}