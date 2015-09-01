function goToContent(topicId){
	window.location.href = "/topic/queryTopicById.action?topicAbstractInfoVO.topicId="+ topicId+"&r=" + (new Date()).getTime();
}