const domain = 'http://127.0.0.1';
const types = [
    {id: 1, title: '服装', icon: "/images/clothing.png"},
    {id: 2, title: '美食', icon: "/images/delicious.png"},
    {id: 3, title: '果蔬', icon: "/images/fruits.png"},
    {id: 4, title: '饮品', icon: "/images/drinks.png"},
    {id: 5, title: '超市', icon: "/images/supermarkets.png"},
    {id: 6, title: '书店', icon: "/images/bookstores.png"},
];
let h = document.documentElement.clientHeight;
$("body").css("height", h);

function getType(id) {
    return types.find(type => type.id === id)
}

function getData({type = 'GET', url, data = {}, dataType = 'json'}) {
    let $loadingToast = $('#loadingToast');
    $loadingToast.fadeIn(1000);
    return new Promise(function (resolve, reject) {
        $.ajax({
            url: `${domain}/${url}`,
            data,
            type,
            dataType,
            success: function (res) {
                console.log(res.content)
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

export {getData, getType, domain}