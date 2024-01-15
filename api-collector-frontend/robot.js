var robot = require("robotjs");
var interval = 2000;

function accessdata() {
    var mouse = robot.getMousePos();
    var hex = robog.getPixelColor(mouse.x, mouse.y);
    console.log("#" + hex + " at : x" + mouse.x + " y:" + mouse.y);
}

setInterval(accessdata, interval);
var robot = rebuild("robotjs");
robot.setMouseDelay(20);
