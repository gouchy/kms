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
			case RET_PARA_IS_ERROR:
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
  * 新增图文知识的窗口初始化
  */
$(function() {
	$("#create-mul-knowledge-dialog").dialog({
		autoOpen : false,
		height : 700,
		width : 1050,
		modal : true,
		buttons: {
			"新增知识": function(){
				
			},
			"放弃": function(){
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

// 新增图文知识按钮
$(function(){
	$("#createMulKnowledge").button().click(function(event){
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
		
//		$("#add-subject-id").val(currentSubjectId);
		
		// 清理界面内容为空
		$(".cover h4 span").text("标题");
		$(".cover img").attr("src", "");
		
		var item = $(".sub-msg-item:last").clone(true);
		$(".sub-msg-item").remove();
		item.children("span").children("img").attr("src", "");
		item.children("h4").children("span").text("标题");
		$(".sub-add").before(item);
		
		$("#mul-current-item").val(0);
		$("#mul-title").val();
		editor.setContent("");
		// TODO：图片清除
		// TODO：原文链接清除
		
		
//		$("#knowledge-title").val("");
//		$("#knowledge-content").val("");
//		var cloneTitle = $("input[name=other-knowledge-title]:last").clone();
//		cloneTitle.val("");
//		$("#other-knowledge-title-panel").empty();
//		$("#other-knowledge-title-panel").append(cloneTitle);
		
		$("#create-mul-knowledge-dialog").dialog("open");
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
		case RET_PARA_IS_ERROR:
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











/***************************************************************************************
 * 图文知识窗口的相关操作
 */
/**
 * 鼠标悬停的效果
 */
$(function(){
	$(".knowledge-item").hover(function(){
		$(this).children("ul").addClass("sub-msg-opr-show");
	}, function(){
		$(this).children("ul").removeClass("sub-msg-opr-show");
	});
});

/**
 * 新增知识Item
 */
$(function(){
	$(".sub-add-btn").click(function(){
		if($(".sub-msg-item").size() > 7)
		{
			jAlert("不能再增加了，只能这么多条。", "提醒");
			reutrn;
		}
		
		var cloneItem = $(".sub-msg-item:last").clone(true);
		cloneItem.children("span").children("img").attr("src", "");
		cloneItem.children("h4").children("span").text("标题");
		cloneItem.children("input").val("");
		var id = parseInt(cloneItem.attr("id").substr(10)) + 1;
		
		// 设置复制的节点的各个属性
		cloneItem.attr("id", "appmsgItem" + id);
		cloneItem.children("ul").children("li").children("a").attr("data-rid", id);
		cloneItem.children("ul").children("li").children("a").attr("data-rid", id);
		
		$(".sub-add").before(cloneItem);
	});
});

/**
 * 删除某一个Item
 */
$(function(){
	$(".iconDel").click(function(){
		if($(".sub-msg-item").size() == 1)
		{
			jAlert("最后一个不能删除。", "提醒");
			return;
		}
		
		// 按钮的上是整个item元素
		var item = $(this).parent().parent().parent();
		jConfirm("真的要删除此项目吗？", "请确认", function(r){
			if(r == false)
				return;
			var deleteItemTop = item.position().top;
			var deleteItemHeight = item.height();
			item.remove();
			
			// 删除时，如果发现编辑框已经在这个下面了，需要上移到第一个节点
			if(parseInt($("#msgEditArea").css("margin-top")) == deleteItemTop)
			{
				$("#msgEditArea").css("margin-top", $(".cover").position().top);
			}
			// 删除的节点是当前编辑节点的上面某一个节点的时候，编辑框需要上移一格
			else if(deleteItemTop < parseInt($("#msgEditArea").css("margin-top")))
			{
				$("#msgEditArea").css("margin-top", parseInt($("#msgEditArea").css("margin-top")) - deleteItemHeight);
			}
		});
	});
});

/**
 * 编辑某一个Item
 */
$(function(){
	$(".iconEdit").click(function(){
		// 按钮的上是整个item元素
		var item = $(this).parent().parent().parent();
		$("#msgEditArea").css("margin-top", item.position().top);
		id = $(this).attr("data-rid");
		
		// 清除已有的值
		$("#mul-title").val("");
		
		// 获取当前选中的值
		
		$("#mul-current-item").val(id); // 设置当前编辑的区域的id，注意顺序，editor.setContent会触发事件
		var title = item.children("h4").children("span").text();
		if(title != "标题")
		{
			$("#mul-title").val(title);
		}
		var content = item.children("input").val();
		editor.setContent(content); 
		

	});
});

$(function(){
	$("#mul-edit-img-upload").fileupload({
		dataType: "json",
		acceptFileTypes:  /(\.|\/)(gif|jpe?g|png)$/i,
		maxFileSize: 5000000,
		disableImageResize: /Android(?!.*Chrome)|Opera/
            .test(window.navigator && navigator.userAgent),
        previewMaxWidth: 100,
        previewMaxHeight: 100,
        previewCrop: true
	});
	
	$("#mul-edit-img-upload").bind("fileuploaddone", function(e, data){
		if(data.result.state == "SUCCESS")
		{
			$("#mul-edit-img").attr("src", data.result.url);
		}
		else
		{
			jAlert(data.result.state, "提醒");
		}
	});
	
	$("#mul-edit-img-upload").bind("fileuploadfail", function(e, data){
		jAlert("上传失败，请重试。", "提醒");
	});
});

/**
 * UEditor编辑器初始化设置
 */
var editor = null;
$(function(){
	editor = new baidu.editor.ui.Editor();
	editor.render("myEditor");
	editor.ready(function(){
		editor.setHeight(150);
	});
});

/**
 * 编辑区域发生变化时，修改相应的值
 */
$(function(){
	$("#mul-title").keyup(function(){
		var content = $(this).val();
		var id = "#appmsgItem" + $("#mul-current-item").val();
		$(id).children("h4").children("span").text(content);
	});
	
	editor.addListener("contentChange", function(){
		var id = "#appmsgItem" + $("#mul-current-item").val();
		$(id).children("input").val(editor.getContent());
	});
	
});





