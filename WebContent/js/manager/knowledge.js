/**
 * 知识列表的内容展现
 */
 $(function(){
	$("#title-grid").jqGrid({
		url: "/kms/knowledge/title/0",
		datatype: "json",
		jsonReader: {
			root: "value.rows",
			page: "value.page",
			records: "value.records"			
		},
		colNames: ["编号", "内容类型", "更新日期"],
		colModel:[
			{name: "id", index: "id", width: 80, sortable: false},
			{name: "name", index: "name", width: 600, sortable: false },
			{name: "updateDate", index: "updateDate desc", 
					width: 150, align: "right", sortable: false,
					formatter: "date", formatoptions: {
						srcformat: "U",
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
		caption: "知识列表",
		height: window.screen.availHeight - 300
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
		height : 600,
		width : 650,
		modal : true,
		buttons : {
			"新增知识" : function() {
				//检查输入内容的合法性
				
				// 提交内容到服务端
				var putRequest = $.ajax({
					type: "put",
					url: "/kms/knowledge",
					data: $("#create-text-knowledge-form").serialize()
				});
				putRequest.success(function(data){
					switch(data.retcode)
					{
						case RET_OK:
							$("#knowledge-grid").trigger("reloadGrid");
							$("#create-text-knowledge-dialog").dialog("close");
							
							break;
						case RET_NO_RIGHT:
						case RET_PARA_IS_ERROR:
						default:
							jAlert("提交的时候发生错误，请重试", "提醒");
							break;
					}
				});
				
				putRequest.error(function(data){
					jAlert("提交的时候发生网络故障，请稍后重试", "提醒");
				});

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
		height : 600,
		width : 650,
		modal : true,
		buttons : {
			"保存修改" : function() {

			},
			"放弃" : function() {
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
			jAlert("您没有选中任何的知识分类。", "提醒");
			return;
		}
		$("#add-subject-id").val(currentSubjectId);
		
		$("#create-text-knowledge-dialog").dialog("open");
	});
});

// 点击编辑按钮
$(function(){
	$("#editKnowledge").button().click(function(event)
	{
		$("#edit-text-knowledge-dialog").dialog("open");
	});
});

// 点击删除按钮
$(function(){
	$("#deleteKnowledge").button().click(function(event)
	{
		
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
		var tmpTitle = $("#other-knowledge-title").clone();
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
		if($("#other-knowledge-title").next().size() == 0)
		{
			jAlert("最后一个副标题输入框无法删除。", "提醒");
			return false;
		}
		$("#other-knowledge-title").last().remove();
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
		var tmpTitle = $("#edit-other-knowledge-title").clone();
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
		if($("#edit-other-knowledge-title").next().size() == 0)
		{
			jAlert("最后一个副标题输入框无法删除。", "提醒");
			return false;
		}
		$("#edit-other-knowledge-title").last().remove();
		return false;
	});
});
