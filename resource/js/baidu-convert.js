
const script = document.createElement("script")
script.src = "http://api.map.baidu.com/api?v=3.0&ak=zPgkyADDtGL8fI20nddQQzonEEFKfKvG"
document.body.appendChild(script)

console.log(script)

function getAdRegion(adRegion) {
    var map = new BMap.Map("allmap");
    map.centerAndZoom(new BMap.Point(116.403765, 39.914850), 5);
    map.enableScrollWheelZoom();
}

getAdRegion("成都市")