$(document).ready(function () {
    console.log("document ready")
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
        console.log("undo")
        $.ajax(
            {
                type: 'GET',
                url: "undo",
            }
        )
    });

    $('#redoButton').click(function () {
        console.log("redo")
        $.ajax(
            {
                type: 'GET',
                url: "redo",
            }
        )
    });

    $('#nextButton').click(function () {
        console.log("next")
        $.ajax(
            {
                type: 'GET',
                url: "turn",
            }
        )
    });
});

$(document).keypress(function(event){
    var keycode = (event.keyCode ? event.keyCode : event.which);
    console.log(keycode)
    if(keycode == '110'){
        console.log("next")
        $.ajax(
            {
                type: 'GET',
                url: "turn",
            }
        )
    }
    if(keycode == '117'){
        console.log("undo")
        $.ajax(
            {
                type: 'GET',
                url: "undo",
            }
        )
    }
    if(keycode == '114'){
        console.log("redo")
        $.ajax(
            {
                type: 'GET',
                url: "redo",
            }
        )
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
            }
        )
    }
});
