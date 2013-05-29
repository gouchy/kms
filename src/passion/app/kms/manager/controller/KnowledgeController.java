package passion.app.kms.manager.controller;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import passion.app.kms.manager.bean.ResultBean;
import passion.app.kms.manager.bean.TitleBean;
import passion.app.kms.manager.constant.ErrorCode;
import passion.app.kms.manager.dao.KnowledgeMapper;
import passion.app.kms.manager.dao.TitleMapper;

@Controller
public class KnowledgeController
{
	@Autowired
	private KnowledgeMapper knowledgeMapper;
	
	@Autowired
	private TitleMapper titleMapper;
	
	@RequestMapping(value = "/knowledge/title/{id}", method = RequestMethod.GET, produces = "application/json")
	public @ResponseBody ResultBean getTitleList(HttpSession session, @PathVariable("id") long id)
	{
		ResultBean result = new ResultBean(ErrorCode.OK);
		
		List<TitleBean> titleList = new ArrayList<TitleBean>();
		TitleBean t1 = new TitleBean();
		t1.setId(1);
		t1.setName("测试1");
		t1.setUpdateDate(new Date());
		titleList.add(t1);
		
		result.addEntry("rows", titleList);
		result.addEntry("total", 1);
		result.addEntry("page", 1);
		result.addEntry("records", 1);

		
		return result;
	}
}
