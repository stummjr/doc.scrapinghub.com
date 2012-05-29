$(function () {
    var headerHeight = $('#header').height(),
        body = $('body');

    $('a[href^="#"]').click(function (e) {
        e.preventDefault();
        body.scrollTop($($(this).attr('href')).offset().top - headerHeight);
    });
});
