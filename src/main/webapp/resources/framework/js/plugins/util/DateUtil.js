
/**
 * 格式化【日期】
 * @param date
 * @returns {string}
 */
function formatDate(date){
    var year = date.getFullYear();
    var month = date.getMonth()+1;
    var day = date.getDate();
    return year+"-"+(month<10?"0"+month:month)+"-"+(day<10?"0"+day:day);
}

var DateUtil = {
    format:function(o){
        var s = "";
        if(typeof(o) == "string"){
            s = o;
        }else{
            s = o.value;
        }
        if(s.length == 4){
            o.value = s+"-";
        }
        if(s.length == 7){
            o.value = s+"-";
        }
        if(s.length > 10){
            o.value = s.substring(0,10);
        }
    },
    lastweek : function(){
        var date = new Date(new Date().getTime() - 1000 * 60 * 60 * 24 * 6);
        return formatDate(date);
    },
    daysInMonth : [0,31,28,31,30,31,30,31,31,30,31,30,31],
    lastmonth : function(){
        var now = new Date();
        var daysInMonth = this.daysInMonth;
        var strYear = now.getFullYear();
        var strDay = now.getDate();
        var strMonth = now.getMonth() + 1;
        if (strYear % 4 == 0 && strYear % 100 != 0) {
            daysInMonth[2] = 29;
        }
        if (strMonth - 1 == 0) {
            strYear -= 1;
            strMonth = 12;
        }
        else {
            strMonth -= 1;
        }
        strDay = daysInMonth[strMonth] >= strDay ? strDay : daysInMonth[strMonth];
        if (strMonth < 10) {
            strMonth = "0" + strMonth;
        }
        if (strDay < 10) {
            strDay = "0" + strDay;
        }
        return strYear + "-" + strMonth + "-" + strDay;
    },
    last3month : function(){
        var now = new Date();
        var daysInMonth = this.daysInMonth;
        var strYear = now.getFullYear();
        var strDay = now.getDate();
        var strMonth = now.getMonth() + 1;
        if (strYear % 4 == 0 && strYear % 100 != 0) {
            daysInMonth[2] = 29;
        }
        if (strMonth - 3 > 0) {
            strMonth -= 3;
        }else if(strMonth -1 == 0){
            strYear -= 1;
            strMonth = 10;
        }else if(strMonth -2 == 0){
            strYear -= 1;
            strMonth = 11;
        }else{
            strYear -= 1;
            strMonth = 12;
        }
        strDay = daysInMonth[strMonth] >= strDay ? strDay : daysInMonth[strMonth];
        if (strMonth < 10) {
            strMonth = "0" + strMonth;
        }
        if (strDay < 10) {
            strDay = "0" + strDay;
        }
        return strYear + "-" + strMonth + "-" + strDay;
    },
    lastyear : function(){
        var now = new Date();
        var strYear = now.getFullYear() - 1;
        var strDay = now.getDate();
        var strMonth = now.getMonth()+1;
        if(strMonth<10){
            strMonth="0"+strMonth;
        }
        if(strDay<10){
            strDay="0"+strDay;
        }
        return strYear+"-"+strMonth+"-"+strDay;
    }
};

