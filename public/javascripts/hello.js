function undo() {
    console.log("undo")
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
    console.log("redo")
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
    console.log("next")
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

function addButtons() {
    $('#newButton').click(function () {
        console.log("newgame 2")
        $.ajax(
            {
                type: 'GET',
                url: "new/" + $('#newGamePlayerCount').val(),
            }
        )
    });

    $('#undoButton').click(function () {
        undo()
    });

    $('#redoButton').click(function () {
        redo()
    });

    $('#nextButton').click(function () {
        nextTurn()
    });
}

function updateGame() {
    $.ajax(
        {
            type: 'GET',
            url: "gameJson",

            success: function (result) {

                $('#state-message').html(result.message)
                $('#player-image').attr('src', '/assets/images/player'+result.activePlayer+'.png')
                $('#dice-image').attr('src', '/assets/images/dice'+result.diced+'.png')

                Object.entries(result.fields).forEach(
                    ([key, value]) => {
                        console.log(value)
                        if(!value.isFreeSpace) {
                            var field = $('.game-field').filter('[x=' + value.x + ']').filter('[y=' + value.y + ']')
                            if (value.avariable) {
                                field.addClass('marked-field')
                            } else {
                                field.removeClass('marked-field')
                            }

                            if (value.sort == 'b')  {
                               field.html('<div class="stone block-stone"></div>')
                            } else if(value.sort == '1') {
                                field.html('<div class="stone p1-stone"></div>')
                            } else if(value.sort == '2') {
                                field.html('<div class="stone p2-stone"></div>')
                            } else if(value.sort == '3') {
                                field.html('<div class="stone p3-stone"></div>')
                            } else if(value.sort == '4') {
                                field.html('<div class="stone p4-stone"></div>')
                            }else {
                                field.html('')
                            }
                        }
                    }
            );
            }
        }
    )
}

$(document).keypress(function (event) {
    var keycode = (event.keyCode ? event.keyCode : event.which);
    console.log(keycode)
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

$(document).click(function (event) {
    var target = $(event.target)

    if (target.hasClass("stone")) {

        target = target.parent()

        var x = target.attr("x")
        var y = target.attr("y")

        console.log(x + "  " + y)

        $.ajax(
            {
                type: 'GET',
                url: "touch/" + x + "/" + y,

                success: function () {
                    updateGame()
                }
            }
        )
    }

    if (target.hasClass("game-field")) {

        var x = target.attr("x")
        var y = target.attr("y")

        console.log(x + "  " + y)

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
});

function connectWebSocket() {
    var websocket = new WebSocket("ws://localhost:9000/websocket");
    websocket.setTimeout

    websocket.onopen = function(event) {
        console.log("Connected to Websocket");
    }

    websocket.onclose = function () {
        console.log('Connection with Websocket Closed!');
    };

    websocket.onerror = function (error) {
        console.log('Error in Websocket Occured: ' + error);
    };

    websocket.onmessage = function (e) {
        if (typeof e.data === "string") {
            var json = JSON.parse(e.data);

        }

    };
}

$(document).ready(function () {
    addButtons()
    connectWebSocket()
    console.log("document ready")
});