$(document).ready(function () {
    console.log("document ready")
    $('#newButton').click(function () {
        console.log("newgame 2")
        $.ajax(
            {
                type: 'GET',
                url: "new/2",
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

    if (target.hasClass("marked-field")) {

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
