/***********************************************************************
 * 页面空间初始化区域
 */ 

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
	
	// 加载知识分类列表
	load_tree(0);
	
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
				if(tmpSubjectName != null)
				{
					tmpSubjectName = tmpSubjectName.trim();
				}
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
 
 // 重命名知识分类的窗口
 $(function(){
	$("#rename-subject-dialog").dialog({
		autoOpen: false,
		height: 300,
		width: 350,
		modal: true,
		buttons: {
			"重命名": function() 
			{
				// 检查分类的名称是否符合要求
				var tmpSubjectName = $("#rename-subject-name").val();
				if(tmpSubjectName != null)
				{
					tmpSubjectName = tmpSubjectName.trim();
				}
				if(tmpSubjectName == null || tmpSubjectName.length == 0)
				{
					jAlert("请输入分类的名称", "提醒");
					return;
				}
				
				// 获取当前所在节点的id
				var selectedNode = $("#subject-tree").jstree("get_selected");
				var currentSubjectId = 0;
				if(selectedNode != null && selectedNode.length > 0)
				{
					currentSubjectId = selectedNode.attr("id");
				}
				else
				{
					// 选中第一个节点
					$("#subject-tree").jstree("select_node",$("li#0"));
				}
				
				
				// 调用后台，保存到数据库
				var subjectAddRequest = $.ajax({
					type: "post",
					url: "/kms/subject/" + currentSubjectId,
					contentType: "application/json",
					data: tmpSubjectName
				});
				
				subjectAddRequest.success(function(data){
					switch(data.retcode)
					{
					case RET_OK:
						// 在树中修改节点
						$("#subject-tree").jstree("set_text", $("#subject-tree").jstree("get_selected"), tmpSubjectName);
						$("#rename-subject-dialog").dialog("close");
						break;
					case RET_DB_FAIL:
					case RET_PARA_IS_ERROR:
					default:
						jAlert("重命名分类失败了，请重新尝试。", "提醒");
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

 // 知识列表的内容展现
 $(function(){
	$("#title-grid").jqGrid({
		url: "/kms/knowledge/title/0",
		datatype: "json",
		jsonReader: {
			root: "value.rows",
			page: "value.page",
			records: "value.records"			
		},
		colNames: ["编号", "主标题", "更新日期"],
		colModel:[
			{name: "id", index: "id", width: 20},
			{name: "name", index: "name", },
			{name: "updateDate", index: "updateDate desc", 
					width: 60, align: "right",
					formatter: "date", formatoptions: {
						srcformat: "ISO8601Long",
						newformat: "Y-m-d H:i:s"
					}
			}
		],
		rowNum: 15,
		rowList: [10, 20, 30],
		pager: "#title-grid-pager",
		sortname: "updateDate",
		viewrecords: true,
		sortorder: "desc",
		caption: "知识列表"
	}); 
	$("#title-grid").jqGrid("navGrid", "#title-grid-pager", {edit: false, add: false, del: false});
 });
 
 
 /**************************************************************************
  * 公共函数定义
  */
 function load_tree(parentSubjectId) {
	// 添加知识分类树的第一层节点
	var subjectRequest = $.ajax({
		type : "get",
		url : "/kms/subject/" + parentSubjectId,
	});
	subjectRequest.success(function(data) {
		switch (data.retcode) {
		case RET_OK:
			var tmpSubjectList = data.value.subjectList;
			if (tmpSubjectList != null && tmpSubjectList.length > 0) {
				for ( var i = 0; i < tmpSubjectList.length; i++) {
					$("#subject-tree").jstree("create", "li#" + parentSubjectId, "inside", { // 根节点的最上面那个地方添加
						"data" : {
							"title" : tmpSubjectList[i].name
						}, // 此处必须定义为一个对象
						"attr" : {
							"id" : tmpSubjectList[i].id
						},
					// 设置图片显示，文件夹形式和文件形式
					// "icon": tmpSubjectList[i].leaf == 1 ?
					// "../images/close-forder.png": "../images/file.png",
					},
					load_tree(tmpSubjectList[i].id),
					true);
				}
			}
			break;
		case RET_PARA_IS_ERROR:
		case RET_DB_FAIL:
		default:
			jAlert("获取知识分类数据失败，请刷新后重新尝试。", "提醒");
			break;
		}
	});
	subjectRequest.error(function() {
		jAlert("网络访问失败，请稍后重试。", "提醒");
	});

}
 
 
 /***************************************************************************
	 * 知识分类按钮操作按钮处理事件
	 */
 
 // 新增知识分类的按钮点击处理事件
 $(function(){
	 $("#createSubject").button().click(function(event)
	 {
		 $("#create-subject-dialog").dialog("open");
	 });
 });
 
 // 删除知识分类
 $(function() {
	$("#removeSubject").button().click(function(event) {
		// 获取当前所在节点的id
		var selectedNode = $("#subject-tree").jstree("get_selected");
		var currentSubjectId = 0;
		if(selectedNode != null && selectedNode.length > 0)
		{
			currentSubjectId = selectedNode.attr("id");
		}
		else
		{
			jAlert("您没有选中任何的知识分类。", "提醒");
			return;
		}
		
		// 如果当前选中的是root-subject，则不允许删除
		if(currentSubjectId == 0)
		{
			jAlert("根节点无法删除。", "提醒");
			return;
		}
		
		// 如果当前不是叶子节点，不允许删除
		var tmpChildren = $("#subject-tree").jstree("is_leaf", $("#subject-tree").jstree("get_selected"));
		if (tmpChildren == false) 
		{
			jAlert("这个分类下面还有子分类，无法删除。", "提醒");
			return;
		}

		jConfirm("您需要删除这个节点吗？", "请确认", function(r) {
			if (r == false) {
				return;
			}
			// 向服务器发起删除请求
			var deleteRequest = $.ajax({
				type: "delete",
				url: "/kms/subject/" + currentSubjectId
			});
			deleteRequest.success(function(data)
			{
				switch(data.retcode)
				{
				case RET_OK:
					// 界面上面删除
					$("#subject-tree").jstree("remove");
					break;
				case RET_PARA_IS_ERROR:
				case RET_DB_FAIL:
				case RET_NO_RIGHT:
				default:
					jAlert("删除分类失败，请重试。", "提醒");
					break;
				}
			});
			
			deleteRequest.error(function(data)
			{
				jAlert("网络发生错误，请重新尝试", "错误");
			});

			
		});

	});
});
 
 // 重命名知识分类
 $(function(){
	 $("#renameSubject").button().click(function(event)
	 {
		 // 如果当前选中的是root-subject，则不允许修改
		 var subjectName = $("#subject-tree").jstree("get_text", $("#subject-tree").jstree("get_selected"));
		 $("#rename-subject-name").val(subjectName);
		 $("#rename-subject-dialog").dialog("open");
	 });
 });
 
 
 /**********************************************************************
  * 知识树事件处理
  */
 $(function(){
	 $("#subject-tree").bind("select_node.jstree", function(e, data) {
		 // 单击事件处理
		 
		 // 获得到点击的节点的ID编号
		 // var currentSubjectId = data.rslt.obj.attr("id");
		 //alert(currentSubjectId);
		 
		 
	 });
 });
