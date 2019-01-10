function undo() {
    $.ajax(
        {
            type: 'GET',
            url: "undo",

            success: function (result) {
                updateGame()
            }
        }
    )
}

function redo() {
    $.ajax(
        {
            type: 'GET',
            url: "redo",

            success: function (result) {
                updateGame()
            }
        }
    )
}

function nextTurn() {
    $.ajax(
        {
            type: 'GET',
            url: "turn",

            success: function (result) {
                updateGame()
            }
        }
    )
}

function newGame(count) {
    $.ajax(
        {
            type: 'GET',
            url: "new/" + count,

            success: function (result) {
                updateGame()
            }
        }
    )
}

function updateGame() {
    $.ajax(
        {
            type: 'GET',
            url: "gameJson",

            success: function (result) {
                boardVue.rows = result.rows;
                gameStateVue.activePlayer = result.activePlayer;
                gameStateVue.diced = result.diced;
                gameStateVue.message = result.message;
            }
        }
    )
}

$(document).ready(function () {
    updateGame();
});

$(document).keypress(function (event) {
    var keycode = (event.keyCode ? event.keyCode : event.which);
    if (keycode == '110') {
        nextTurn()
    }
    if (keycode == '117') {
        undo()
    }
    if (keycode == '114') {
        redo()
    }
});

function openNewGameDropdown() {
    document.getElementById("myDropdown").classList.toggle("show");
}

$(document).click(function (event) {
    //---close dropdown if opened----------
    if (!event.target.matches('.dropbtn')) {
        var dropdowns = document.getElementsByClassName("dropdown-content");
        var i;
        for (i = 0; i < dropdowns.length; i++) {
            var openDropdown = dropdowns[i];
            if (openDropdown.classList.contains('show')) {
                openDropdown.classList.remove('show');
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
                    url: "touch/" + x + "/" + y,

                    success: function (result) {
                        updateGame()
                    }
                }
            )
        }
    }
});

let boardVue = new Vue({
    el: '#board',
    template: `<div class="board"><div v-for="row in rows" class="gameRow">
        <field v-for="field in row.fields" v-bind:x="field.x" v-bind:y="field.y" v-bind:class="compFieldClasses(field)"></field>
    </div></div>`,
    data: {
        rows: []
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

let gameStateVue = new Vue({
    el: '#gameState',
    template: `<div>
        <p>{{message}}</p>
        <p>
            Player:<img v-bind:src="'/assets/images/player' + activePlayer + '.png'" width="30" height="30" class="d-inline-block align-top">
            Diced:<img v-bind:src="'/assets/images/dice' + diced + '.png'" width="30" height="30" class="d-inline-block align-top">
        </p>
    </div>`,
    data: {
        activePlayer: '1',
        diced: '1',
        message: 'loading'
    }
});