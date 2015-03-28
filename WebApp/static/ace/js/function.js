//检查输入文本格式，其中obj为传回来的控件对象
function CheckFormat(obj){
	var _type = $(obj).attr('id'); //获取传回控件对象的ID值
	var error_tip="";
	var format="";
	switch(_type){                 //规范格式的正则表达式
	case "email":
			 format=/^([\w\.-]+)@([a-zA-Z0-9-]+)(\.[a-zA-Z\.]+)$/;
			 error_tip = "Email格式错误，请输入正确格式";
			break;
	case "_num":
			 format="^[0-9]*$";
			 error_tip = "格式错误，只能输入数字";
			break;
	case "link":
			 format="^http://([\w-]+\.)+[\w-]+(/[\w-./?%&=]*)?$";
			 error_tip = "输入网址格式错误，请输入正确格式";
			break;
	case "url":
			 format="^http://([\w-]+\.)+[\w-]+(/[\w-./?%&=]*)?$";
			 error_tip = "输入网址格式错误，请输入正确格式";
			break;
	defalut:;
			break;
	}
	
	if ($("#"+_type).val() == "") { 
		$("#"+_type+"confirmMsg").html("<font color='red'>输入不能为空</font>"); 
		$("#"+_type).focus(); 
		return false; 
	} 
	else if(format != "" && !$("#"+_type).val().match(format)){
		$("#"+_type+"confirmMsg").html("<font color='red'>"+error_tip+"</font>"); 
		$("#"+_type).focus(); 
		return false; 
		}
	else{
		$("#"+_type+"confirmMsg").html(""); 
		return true; 
		}
}
