/*var imgObjId;
var imgObjUrl;

function addImg(itemId){
	var objId= "#"+itemId;
	var sumTd = $("#topicImgTableTr").find("td").length;
	//$("#addImgButton").before("<td id=img"+sumTd+"><input id=upfile"+sumTd+" name=\"imgList\" type=\"file\" style=\"display:none\"></td>");
	var upfileObjId="#upfile1";
	$(upfileObjId).click();
	var uploadImgId = "#upfile"+sumTd;
	var imgName = $(upfileObjId).val();
	var imgurl=getImgUrl(upfileObjId,sumTd);
	alert(imgurl);
	var imgItemStr = "<td class=\"info\" id=\"imgshowTd"+sumTd+"\"><img width=\"100px\" height=\"100px\" id=\"imguploadId"+sumTd+"\" src=\""+imgurl+"\"></td>";
	imgObjId="imguploadId"+sumTd;
	imgObjUrl = imgurl;
	$("#addImgButton").before(imgItemStr);
}*/


function addImg(itemId){
	var sumTd = $("#topicImgFileTr").find("td").length;
	var fileId = sumTd+1;
	var upfileObjId="#upfile"+sumTd;
	$(upfileObjId).click();
	
	$(upfileObjId).change(function (){
		var imgurl=getImgUrl(upfileObjId);
		console.error(imgurl);
		$("#topicImgFiletd").after("<td id=imgtd"+fileId+"><input id=upfile"+fileId+" name=\"img"+fileId+"\" type=\"file\" onChange=\"preivew(this);\"></td>");
		var imgItemStr = "<td class=\"info\" id=\"imgshowTd"+sumTd+"\"><img width=\"100px\" height=\"100px\" id=\"imguploadId"+sumTd+"\" src=\""+imgurl+"\"></td>";
		$("#addImgButton").before(imgItemStr);
	});
}

/*function addImgShow(imgurl,sumTd){
	var imgItemStr = "<td class=\"info\" id=\"imgshowTd"+sumTd+"\"><img width=\"100px\" height=\"100px\" id=\"imguploadId"+sumTd+"\" src=\""+imgurl+"\"></td>";
	$("#addImgButton").before(imgItemStr);
}*/

function getImgUrl(uploadImgId){
	var strSrc = $("#showImg").val(); 
	var f = document.getElementById('upfile1').files[0];
	var imgItem = $(uploadImgId)[0].files[0];
	var imgurl = window.URL.createObjectURL(imgItem);
	return imgurl;
}

function validImgName(){
	var upfileObjId="#upfile1";
	var imgNames = $(upfileObjId).val();
	var imgNameArray = imgNames.split(",");
	for(var i=0;i<imgNameArray.length;i++){
		alert(imgNameArray[i]);
		if(checkImgName(imgNameArray[i])==false){
			return false;
		}
	}
	return true;
}

function checkImgName(imgName){
	//正则表达式，校验图片格式
	var syn=new RegExp("\.(jpg|jpeg|ico|bmp|png)$","i");
	//var syn=/\.(jpg|jpeg|ico|bmp|png)$/i;  注意这里不是字符串
	var r = syn.test(imgName);
	if(imgName == "") return false;
	if (!syn.test(imgName)){
		alert("请上传jpg/png/ico/bmp格式的图片！");
		return false;
	}
	return true;
}


var preivew = function(file){
    try{ 
    	var imgNames = file.value;
    	var uid = "#" + file.id;

    	if(checkImgName(imgNames)==false){
    		$(uid).parent().remove();
    	}
    		return;
    }catch(e){ 
        alert(e); 
    } 
}

/*
var syn="\.(jpg|jpeg|ico|bmp|png)$/i";
var a=syn.test("a.jpg");

var syn=/\.(jpg|jpeg|ico|bmp|png)$/i;
var a=syn.test("u=639632922,3655547558&fm=21&gp=0.jpg");
alert(a);

var syn=/\.(jpg|jpeg|ico|bmp|png)$/i;
var a=syn.test("a.jpg");
alert(a);
var b = "/\.(jpg|jpeg|ico|bmp|png)$/".test("a.jpg");
alert(b);

var a = /\.(gif|jpg|jpeg|png|GIF|JPG|PNG)$/.test("a.jpg");
alert(a);

var syn=new RegExp("\.(gif|jpg|jpeg|png|GIF|JPG|PNG)$");
alert(syn.test("a.jpg"));
var b = syn.test("a.jpg");
alert(b);*/
