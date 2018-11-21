$(document).ready(function () {
    console.log("document ready")
});

$(document).click(function(event) {
    var target = $(event.target)

    //quit if something else than stone is clicked
    if (!target.hasClass("stone"))
        return

    target = target.parent()

    var x = target.attr("x")
    var y = target.attr("y")

    console.log(x + "  " + y)

});
