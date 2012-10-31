$(function () {
    var headerHeight = $('#header').height(),
        body = $('body');

    $('a[href^="#"]').each(function () {
        var link = $(this);
        var target = $(link.attr('href'));
        var pos = target.offset().top - headerHeight;

        link.on('click', function (e) {
            e.preventDefault();
            $.scrollTo(pos);
        });
    });
});
