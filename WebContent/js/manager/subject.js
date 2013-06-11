/**
  * 初始化创建分类的窗口
  */ 
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
				var result = SubjectController.createSubject({
					$entity: {
						name: tmpSubjectName,
						parentSubject: parentSubjectId
					}
				});
				switch(result.retcode)
				{
				case RET_OK:
					// 在树中增加节点
					$("#subject-tree").jstree("create", null, "inside", {"data": tmpSubjectName, "attr": {"id": result.value.subjectId}}, null, true);					
					$("#create-subject-dialog").dialog("close");
					break;
				case RET_DB_FAIL:
				case RET_PARA_IS_ERROR:
				default:
					jAlert("创建分类失败了，请重新尝试。", "提醒");
					break;
				}		
			},
			"放弃": function()
			{
				$(this).dialog("close");
			}
		}
	}); 
 });
 
 /**
  * 重命名知识分类的窗口
  */
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
				var result = SubjectController.updateSubjectName({
					id: currentSubjectId,
					$entity: tmpSubjectName
				});

				switch(result.retcode)
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
				
			},
			"放弃": function()
			{
				$(this).dialog("close");
			}
		}
	}); 
 });
 
 
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
			
			var result = SubjectController.deleteSubjectById({id: currentSubjectId});
			switch(result.retcode)
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
		 var currentSubjectId = data.rslt.obj.attr("id");
		 
		 var currentSubjectName = "所有知识";
		 if(currentSubjectId != "0")
		 { 
			 currentSubjectName = "知识分类: "+ $("#subject-tree").jstree("get_text", $("#subject-tree").jstree("get_selected"));
		 }
		 
		 // 刷新右边的知识列表
		 $("#title-grid").jqGrid('setGridParam', {url:"/kms/rest/knowledge/subject/"+currentSubjectId, page:1});
		 $("#title-grid").jqGrid('setCaption', currentSubjectName)
		 .trigger('reloadGrid');
	 
	 });
});

