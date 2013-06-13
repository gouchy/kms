/***********************************************************************
 * 页面空间初始化区域
 */ 

/**
 * 页面完成加载初始化
 */
 $(document).ready(function () {
	// 布局设置
	var layout = $("body").layout({applyDefaultStyles: true,
		west__onresize: $.layout.callbacks.resizePaneAccordions});
	layout.sizePane("west", 380);
	
	// Accordion 设置
	$("#left_panel").accordion({ heightStyle: "fill", fillSpace: true });
	
	// 知识分类树 设置
	$("#subject-tree")
	.bind("loaded.jstree", function(){
		// 加载知识分类列表
		load_tree(0);
	})
	.jstree({
		"json_data": {
			"data": [{
					"attr": {"id": "0"},
					"data": {
						"title": "知识分类列表"
					}
				}
			]
		},
		"themes": {
			"theme": "classic"
		},
		"plugins": ["themes", "json_data", "ui", "crrm"],
	});

 });
 

 /**************************************************************************
  * 公共函数定义
  */
 function load_tree(parentSubjectId) {
	// 添加知识分类树的第一层节点
	var result = SubjectController.getChildSubjectList({"id": parentSubjectId});

	switch (result.retcode) {
	case RET_OK:
		var tmpSubjectList = result.value.subjectList;
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
				}, null, true);
				load_tree(tmpSubjectList[i].id);
			}
		}
		break;
	case RET_PARA_IS_ERROR:
	case RET_DB_FAIL:
	default:
		jAlert("获取知识分类数据失败，请刷新后重新尝试。", "提醒");
		break;
	}


}
 
