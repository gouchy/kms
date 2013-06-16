/**
 * 知识列表的内容展现
 */
 $(function(){
	$("#title-grid").jqGrid({
		url: "/kms/rest/knowledge/subject/0",
		datatype: "json",
		jsonReader: {
			root: "value.rows",
			page: "value.page",
			records: "value.records",
			total: "value.total"
		},
		colNames: ["编号", "内容类型", "更新日期"],
		colModel:[
			{name: "id", index: "id", width: 80, sortable: false, align: "center"},
			{name: "name", index: "name", width: 600, sortable: false },
			{name: "updateDate", index: "updateDate desc", 
					width: 150, align: "right", sortable: false,
					formatter: "date", formatoptions: {
						srcformat: "U",
						newformat: "Y-m-d H:i:s"
					}
			}
		],
		rowNum: 35,
		rowList: [15, 25, 35, 45, 55,100],
		pager: "#title-grid-pager",
		sortname: "updateDate",
		viewrecords: true,
		sortorder: "desc",
		caption: "所有知识",
		height: window.innerHeight - 200,
		/**
		 * 当用户双机了某一行的时候，会出现这一行的详细知识信息内容
		 * @param id
		 */
		ondblClickRow: function(id){
			// 根据id获取这条知识的详细内容
			var result = KnowledgeController.getKnowledgeByTitleId({"id": id});
			switch(result.retcode)
			{
			case RET_OK:
				$("#show-knowledge-title").text(result.value.mainTitle.name);
				$("#show-knowledge-content").text(result.value.knowledge.content);
				$("#show-knowledge-other-title").empty();
				for(var index in result.value.otherTitle)
				{
					var otherTitle_li = $("<li></li>").text(result.value.otherTitle[index].name);
					$("#show-knowledge-other-title").append(otherTitle_li);
				}
				$("#show-knowledge-dialog").dialog("open");
				break;
			case RET_NO_RIGHT:
			case RET_IS_ERROR:
			default:
				jAlert("发生故障，请重新再试试。", "提醒");
				break;
			}
		}
	}); 
	$("#title-grid").jqGrid("navGrid", "#title-grid-pager", 
					{edit: false, add: false, del: false});
 });
 
/**
 * 创建文本类知识窗口
 */
 $(function() {
	$("#create-text-knowledge-dialog").dialog({
		autoOpen : false,
		height : 450,
		width : 650,
		modal : true,
		buttons : {
			"新增知识" : function() {
				//检查输入内容的合法性
				var title = $("#knowledge-title").val();
				if(title == null)
				{
					jAlert("标题不能为空。", "提醒");
					return;
				}
				title = title.trim();
				if(title.length == 0)
				{
					jAlert("标题不能为空。", "提醒");
					return;
				}
				
				var content = $("#knowledge-content").val();
				if(content == null)
				{
					jAlert("内容不能为空。", "提醒");
					return;
				}
				//content = content.trim();
				if(content.length == 0)
				{
					jAlert("内容不能为空。", "提醒");
					return;
				}
				
				var otherTitleList = new Array();
				var i = 0;
				$("input[name=other-knowledge-title]").each(function(){
					var tmp = $(this).val();
					if(tmp != null && tmp.trim().length > 0)
					{
						otherTitleList[i] = tmp;
						i ++;
					}
				});
				
				// 获取当前所在节点的id
				var selectedNode = $("#subject-tree").jstree("get_selected");
				var subjectId = 0;
				if(selectedNode != null && selectedNode.length > 0)
				{
					subjectId = selectedNode.attr("id");
				}
				else
				{
					// 选中第一个节点
					$("#subject-tree").jstree("select_node",$("li#0"));
				}
				
				// 提交内容到服务端
				var result = KnowledgeController.createKnowledge({
					$entity: {
						knowledgeTitle: title,
						knowledgeContent: content,
						addSubjectId: subjectId,
						otherKnowledgeTitleList: otherTitleList
					}
				});
				

				switch(result.retcode)
				{
					case RET_OK:
						// 添加成功，重新刷新表格数据
						
						$("#title-grid").trigger("reloadGrid");
						$("#create-text-knowledge-dialog").dialog("close");
						
						break;
					case RET_NO_RIGHT:
					case RET_PARA_IS_ERROR:
					default:
						jAlert("提交的时候发生错误，请重试", "提醒");
						break;
				}
			},
			"放弃" : function() {
				$(this).dialog("close");
			}
		}
	});
});

 /**
  * 编辑知识窗口初始化
  */
 $(function() {
	$("#edit-text-knowledge-dialog").dialog({
		autoOpen : false,
		height : 450,
		width : 650,
		modal : true,
		buttons : {
			"保存修改" : function() {
				//检查输入内容的合法性
				var title = $("#edit-knowledge-title").val();
				if(title == null)
				{
					jAlert("标题不能为空。", "提醒");
					return;
				}
				title = title.trim();
				if(title.length == 0)
				{
					jAlert("标题不能为空。", "提醒");
					return;
				}
				
				var content = $("#edit-knowledge-content").val();
				if(content == null)
				{
					jAlert("内容不能为空。", "提醒");
					return;
				}
				//content = content.trim();
				if(content.length == 0)
				{
					jAlert("内容不能为空。", "提醒");
					return;
				}
				
				var otherTitleList = new Array();
				var i = 0;
				$("input[name=edit-other-knowledge-title]").each(function(){
					var tmp = $(this).val();
					if(tmp != null && tmp.trim().length > 0)
					{
						otherTitleList[i] = tmp;
						i ++;
					}
				});
				
				// 获取当前所在节点的id
				var id = $("#edit-knowledge-id").val();
				
				// 提交内容到服务端
				var result = KnowledgeController.updateKnowledge({
					"id": id,
					$entity: {
						knowledgeTitle: title,
						knowledgeContent: content,
						otherKnowledgeTitleList: otherTitleList
					}
				});
				

				switch(result.retcode)
				{
					case RET_OK:
						// 修改成功，重新刷新表格数据
						
						$("#title-grid").trigger("reloadGrid");
						$("#edit-text-knowledge-dialog").dialog("close");
						
						break;
					case RET_NO_RIGHT:
					case RET_PARA_IS_ERROR:
					default:
						jAlert("提交的时候发生错误，请重试", "提醒");
						break;
				}
			},
			"放弃" : function() {
				$(this).dialog("close");
			}
		}
	});
});
 
/**
 * 查看知识详情窗口
 */
$(function(){
	$("#show-knowledge-dialog").dialog({
		autoOpen: false,
		height: 500,
		width: 650,
		modal: true,
		buttons: {
			"关闭": function(){
				$(this).dialog("close");
			}
		}
	});
});
 
 /***************************************************************************
	 * 知识内容列表的操作
	 */
 // 点击增加按钮
$(function(){
	$("#createTextKnowledge").button().click(function(event)
	{
		// 获取当前所在节点的id
		var selectedNode = $("#subject-tree").jstree("get_selected");
		var currentSubjectId = 0;
		if(selectedNode != null && selectedNode.length > 0)
		{
			currentSubjectId = selectedNode.attr("id");
		}
		else
		{
			// jAlert("您没有选中任何的知识分类。", "提醒");
			// return;
			// 默认选中跟类别
			
		}
		
		if(currentSubjectId == 0 || currentSubjectId == "0")
		{
			jAlert("不能在根分类下面增加知识。", "提醒");
			return;
		}
		
		$("#add-subject-id").val(currentSubjectId);
		
		// 清理界面内容为空
		$("#knowledge-title").val("");
		$("#knowledge-content").val("");
		var cloneTitle = $("input[name=other-knowledge-title]:last").clone();
		cloneTitle.val("");
		$("#other-knowledge-title-panel").empty();
		$("#other-knowledge-title-panel").append(cloneTitle);
		
		$("#create-text-knowledge-dialog").dialog("open");
	});
});

// 点击编辑按钮
$(function(){
	$("#editKnowledge").button().click(function(event)
	{
		// 获取当前选中的knowledge的id,RowID等于编号字段内容
		var selRow = $("#title-grid").jqGrid("getGridParam", "selrow");
		if(selRow == null)
		{
			jAlert("当前您未选中任何的记录。", "提醒");
			return;
		}		
		
		// 查询此知识的内容，并且设置控件值
		$("#edit-knowledge-id").val(selRow);
		
		var result = KnowledgeController.getKnowledgeByTitleId({"id": selRow});
		switch(result.retcode)
		{
		case RET_OK:
			$("#edit-knowledge-title").val(result.value.mainTitle.name);
			$("#edit-knowledge-content").val(result.value.knowledge.content);
			// 删除多余的副标题输入框
			var cloneOtherTitle = $("input[name=edit-other-knowledge-title]:first").clone();
			cloneOtherTitle.val("");
			$("#edit-other-knowledge-title-panel").empty();
			$("#edit-other-knowledge-title-panel").append(cloneOtherTitle);
			for(var index in result.value.otherTitle)
			{
				var tmpTitle = $("input[name=edit-other-knowledge-title]:last").clone();
				tmpTitle.val("");
				$("input[name=edit-other-knowledge-title]:last").val(result.value.otherTitle[index].name);
				$("#edit-other-knowledge-title-panel").append(tmpTitle);
			}
			break;
		case RET_NO_RIGHT:
		case RET_IS_ERROR:
		default:
			jAlert("发生故障，请重新再试试。", "提醒");
			break;
		}
		
		
		$("#edit-text-knowledge-dialog").dialog("open");
	});
});

// 点击删除按钮
$(function(){
	$("#deleteKnowledge").button().click(function(event)
	{
		var selRow = $("#title-grid").jqGrid("getGridParam", "selrow");
		if(selRow == null)
		{
			jAlert("当前您未选中任何的记录。", "提醒");
			return;
		}	
		
		jConfirm("您真的需要删除这条记录吗？", "确认", function(r){
			if(r == false)
				return;
			
			// 删除这条知识
			var result = KnowledgeController.deleteKnowledgeByTitleId({id: selRow,});
			switch(result.retcode)
			{
			case RET_OK:
				$("#title-grid").trigger("reloadGrid");
				break;
			case RET_NO_RIGHT:
			default:
				jAlert("对不起，删除失败，请重试。", "提醒");
				break;
			}
		});
	});
});

/**
 * 其它知识标题的新增副标题按钮
 */
$(function(){
	$("#addMoreTitle").button(
	{
		text:false,
		icons: {
			primary: "ui-icon-plus"
		}
	})
	.click(function(event)
	{
		var tmpTitle = $("input[name=other-knowledge-title]:last").clone();
		tmpTitle.val("");
		tmpTitle.appendTo("#other-knowledge-title-panel");
		return false;
	});
});

/**
 * 删除最后一条副标题按钮
 */
$(function(){
	$("#deleteMoreTitle").button(
	{
		text:false,
		icons: {
			primary: "ui-icon-minus"
		}
	})
	.click(function(event)
	{
		if($("input[name=other-knowledge-title]").size() == 1 )
		{
			jAlert("最后一个副标题输入框无法删除。", "提醒");
			return false;
		}
		$("input[name=other-knowledge-title]:last").remove();
		return false;
	});
});


/**
 * 编辑知识的时候，其它知识标题的新增副标题按钮
 */
$(function(){
	$("#edit-addMoreTitle").button(
	{
		text:false,
		icons: {
			primary: "ui-icon-plus"
		}
	})
	.click(function(event)
	{
		var tmpTitle = $("input[name=edit-other-knowledge-title]:last").clone();
		tmpTitle.val("");
		tmpTitle.appendTo("#edit-other-knowledge-title-panel");
		return false;
	});
});

/**
 * 编辑知识的时候，删除最后一条副标题按钮
 */
$(function(){
	$("#edit-deleteMoreTitle").button(
	{
		text:false,
		icons: {
			primary: "ui-icon-minus"
		}
	})
	.click(function(event)
	{
		if($("input[name=edit-other-knowledge-title]").size() == 0)
		{
			jAlert("最后一个副标题输入框无法删除。", "提醒");
			return false;
		}
		$("input[name=edit-other-knowledge-title]:last").remove();
		return false;
	});
});
