/**
 * Created by cf on 2016/9/29.
 */
window.ParsleyConfig = {
    validators: {
        //验证中英文字节长度
        byterangelength: {
            fn: function (value, length) {
                var len = 0;
                for(var i=0;i<value.length;i++)
                    if(/[^\u0000-\u00FF]/i.test(value[i]))
                        len+=2;
                    else
                        len+=1;
                return len <= length;
            },
            priority: 30
        }
    }
};