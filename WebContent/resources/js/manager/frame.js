 // 页面完成加载初始化
 $(document).ready(function () {
	// 布局设置
	$("body").layout({applyDefaultStyles: true});
	
	// Accordion 设置
	$("#left_panel").accordion({ heightStyle: "fill" });
	
	// 知识分类树 设置
	$("#subject-tree").jstree({
		"json_data": {
			"data": [{
					"attr": {"id": "0"},
					"data": {
						"title": "知识分类列表"
					}
				}
			]
		},
		"plugins": ["themes", "json_data", "ui", "crrm"],
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
		case RET_PARA_IS_ERROR:
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
 
// 初始化创建分类的窗口
 $(function(){
	$("#create-subject-dialog").dialog({
		autoOpen: false,
		height: 300,
		width: 350,
		modal: true,
		buttons: {
			"创建分类": function() 
			{
				// 检查分类的名称是否符合要求
				var tmpSubjectName = $("#create-subject-name").val();
				if(tmpSubjectName == null || tmpSubjectName.length == 0)
				{
					jAlert("请输入新的分类的名称", "提醒");
					return;
				}
				
				// 获取当前所在节点的id
				var selectedNode = $("#subject-tree").jstree("get_selected");
				var parentSubjectId = 0;
				if(selectedNode != null && selectedNode.length > 0)
				{
					parentSubjectId = selectedNode.attr("id");
				}
				else
				{
					// 选中第一个节点
					$("#subject-tree").jstree("select_node",$("li#0"));
				}
				
				
				// 调用后台，保存到数据库
				var subjectAddRequest = $.ajax({
					type: "put",
					url: "/kms/subject",
					contentType: "application/json",
					data: JSON.stringify({
						"name": tmpSubjectName,
						"parentSubject": parentSubjectId
					})
				});
				
				subjectAddRequest.success(function(data){
					switch(data.retcode)
					{
					case RET_OK:
						// 在树中增加节点
						$("#subject-tree").jstree("create", null, "inside", {"data": tmpSubjectName, "attr": {"id": data.value.subjectId}}, null, true);					
						$("#create-subject-dialog").dialog("close");
						break;
					case RET_DB_FAIL:
					case RET_PARA_IS_ERROR:
					default:
						jAlert("创建分类失败了，请重新尝试。", "提醒");
						break;
					}
				});
				subjectAddRequest.error(function(){
					jAlert("网络发生错误，请重新尝试。", "错误");
				});
				
				
			},
			"放弃": function()
			{
				$(this).dialog("close");
			}
		}
	}); 
 });
 
 // 新增知识分类的按钮点击处理事件
 $(function(){
	 $("#createSubject").button().click(function(event)
	 {
		 $("#create-subject-dialog").dialog("open");
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
 
