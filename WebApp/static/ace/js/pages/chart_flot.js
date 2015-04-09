$(function() {
    var data1 = [];
    var totalPoints = 300;
    function GetData() {
        data1.shift();
        while (data1.length < totalPoints) {
            var prev = data1.length > 0 ? data1[data1.length - 1] : 50;
            var y = prev + Math.random() * 10 - 5;
            y = y < 0 ? 0 : (y > 100 ? 100 : y);
            data1.push(y);
        }
        var result = [];
        for (var i = 0; i < data1.length; ++i) {
            result.push([i, data1[i]])
        }
        return result;
    }
    var updateInterval = 100;
});

	require.config({
        paths:{ 
            echarts:'/static/js/echarts/echarts-map',
            'echarts/chart/bar' : '/static/js/echarts/echarts-map',
            'echarts/chart/line' : '/static/js/echarts/echarts-map',  
            'echarts/chart/pie' : '/static/js/echarts/echarts-map',  
            'echarts/chart/map' : '/static/js/echarts/echarts-map',
            'echarts/chart/scatter' : '/static/js/echarts/echarts-map',
            'echarts/chart/funnel' : '/static/js/echarts/echarts-map',
        }
    });
	

 /* require.config({
        paths:{ 
            echarts:'./js/echarts/echarts-map',

        }
    });*/
    // Step:4 require echarts and use it in the callback.
    // Step:4 动态加载echarts然后在回调函数中开始使用，注意保持按需加载结构定义图表路径
   