function convertTimeStamp(UNIX_timestamp) {
    var a = new Date(UNIX_timestamp * 1000);
    var year = a.getFullYear();
    var month = a.getMonth();
    var date = a.getDate();
    var hour = format2digit(a.getHours());
    var min = format2digit(a.getMinutes());
    var sec = format2digit(a.getSeconds());
    var time = year + '-' + month + '-' + date + ' ' + hour + ':' + min + ':' + sec;
    return time;
}

function  format2digit(digit) {
    digit = ("0" + digit).slice(-2);
    return digit;
}

var transformEnum = function (desc, enumData) {
    for (var x in enumData) {
        if (enumData[x] === desc) {
            return parseInt(x);
        }
    }
    return '';
};