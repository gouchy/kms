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
			{name: "id", index: "id", width: 80},
			{name: "name", index: "name", width: 600 },
			{name: "updateDate", index: "updateDate desc", 
					width: 150, align: "right",
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
		caption: "知识列表",
		height: window.screen.availHeight - 300
	}); 
	$("#title-grid").jqGrid("navGrid", "#title-grid-pager", 
					{edit: false, add: false, del: false});
 });
 
/**
 * 创建文本类知识窗口
 */
 $(function(){
		$("#create-text-knowledge-dialog").dialog({
			autoOpen: false,
			height: 300,
			width: 350,
			modal: true,
			buttons: {
				"新增知识": function() 
				{
					
				},
				"放弃": function()
				{
					
				}
			}
		});
	});
 
 /***********************************************************************
  * 知识内容列表的操作
  */
 // 点击增加按钮
$(function(){
	$("#createTextKnowledge").button().click(function(event)
	{
		$("#create-text-knowledge-dialog").dialog("open");
	});
});

// 点击编辑按钮
$(function(){
	$("#editKnowledge").button().click(function(event)
	{
		
	});
});

// 点击删除按钮
$(function(){
	$("#deleteKnowledge").button().click(function(event)
	{
		
	});
});
