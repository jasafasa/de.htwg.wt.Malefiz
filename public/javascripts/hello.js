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

                Object.entries(result.fields).forEach(
                    ([key, value]) => {
                        console.log(value)
                        if(!value.isFreeSpace) {
                            $('.game-field').addClass('marked-field')
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

$(document).ready(function () {
    addButtons()
    console.log("document ready")
});