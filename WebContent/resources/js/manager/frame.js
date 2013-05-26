 // 页面完成加载初始化
 $(document).ready(function () {
	// 布局设置
	$("body").layout({applyDefaultStyles: true});
	
	// Accordion 设置
	$("#left_panel").accordion({ heightStyle: "fill" });
	
	// 知识分类树 设置
	$("#subject-tree").jstree({
		"plugins": ["themes", "html_data", "ui", "crrm"],
		"core": {}
	});
	
	// 添加知识分类树的第一层节点
	var subjectRequest = $.ajax({
		type: "get",
		url: "/kms/subject/0",
	});
	subjectRequest.success(function(data){
		switch(data.retcode)
		{
		case RET_OK:
			
			break;
		case RET_PARAM_IS_NULL:
		case RET_DB_FAIL:
		default:
			jAlert("获取知识分类数据失败，请刷新后重新尝试。", "提醒");
			break;
		}
	});
	subjectRequest.error(function()
	{
		jAlert("网络访问失败，请稍后重试。", "提醒");
	});
	
	
 });
 

 // 新增知识分类
 $(function(){
	 $("#createSubject").button().click(function(event)
	 {
		 $("#subject-tree").jstree("create");
	 });
 });
 
 // 删除知识分类
 $(function(){
	 $("#deleteSubject").button().click(function(event)
	 {
		 // 如果当前选中的是root-subject，则不允许删除
		 
		 // 如果当前不是叶子节点，不允许删除
		 
		 
		 $("#subject-tree").jstree("remove");
	 });
 });
 
 // 重命名知识分类
 $(function(){
	 $("#renameSubject").button().click(function(event)
	 {
		 // 如果当前选中的是root-subject，则不允许修改
		 
		 
		 $("#subject-tree").jstree("rename");
	 });
 });
