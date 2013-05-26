 // 页面完成加载初始化
 $(document).ready(function () {
	// 布局设置
	$("body").layout({applyDefaultStyles: true});
	$("subject-tree").jstree({
		"plugins": ["themes", "html_data", "ui", "crrm"],
		"core": {"initally_open": ["phtml_1"]}
	});
 });
 
