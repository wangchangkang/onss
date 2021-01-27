const domain = 'http://127.0.0.1'
let h = document.documentElement.clientHeight;
$("body").css("height", h);

function getData({type = 'GET', url, data = {}, dataType = 'json'}) {
    let $loadingToast = $('#loadingToast');
    $loadingToast.fadeIn(1000);
    return new Promise(function (resolve, reject) {
        $.ajax({
            url:`${domain}/${url}`,
            data,
            type,
            dataType,
            success: function (res) {
                resolve(res);
            },
            error: function (err) {
                reject(err);
            },
            complete: function (res) {
                $loadingToast.fadeOut(1000);
            }
        });
    });
}
export { getData,domain }