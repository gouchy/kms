package passion.app.kms.manager.controller;

import java.util.List;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import passion.app.kms.manager.bean.ResultBean;
import passion.app.kms.manager.bean.SubjectBean;
import passion.app.kms.manager.constant.ErrorCode;
import passion.app.kms.manager.dao.SubjectMapper;

public class SubjectController
{
	@Autowired
	private SubjectMapper subjectMapper;
	
	/**
	 * 创建一个新的subject节点
	 * @param subject 分类
	 * @return 返回值
	 */
	@RequestMapping(value = "/subject", method = RequestMethod.PUT, consumes = {"application/json"}, produces = {"application/json"})
	public @ResponseBody ResultBean createSubject(HttpSession session, @RequestBody SubjectBean subject)
	{
		ResultBean result = new ResultBean(ErrorCode.OK);
		
		// 检查参数完整性
		if(subject.getName() == null || subject.getName().equals(""))
		{
			result.setRetcode(ErrorCode.PARA_IS_ERROR);
			return result;
		}
		
		// 取出用户ID
		Integer userId = null;
		try
		{
			userId = Integer.parseInt((String)session.getAttribute("userId"));
		}
		catch(Exception e)
		{
			result.setRetcode(ErrorCode.PARA_IS_ERROR);
			return result;
		}		
		subject.setUserId(userId);
		
		// 推荐分类
		subject.setType(1);
		
		// 新增节点都为叶子节点
		subject.setType(0);
		
		// 存入数据库
		if(subjectMapper.createSubject(subject) == 0)
		{
			result.setRetcode(ErrorCode.DB_FAIL);
			return result;
		}
		
		// 修改上级节点为非叶子节点
		if(subject.getParentSubject() != 0)
		{
			subjectMapper.updateSubjectNotLeaf(subject.getParentSubject());
		}
		
		return result;
	}
	
	/**
	 * 更新知识分类标题
	 * @param session 会话
	 * @param id 知识分类id
	 * @param subjectName 知识分类内容
	 * @return 返回值
	 */
	@RequestMapping(value = "/subject/{id}", method = RequestMethod.POST, produces = {"application/json"})
	public @ResponseBody ResultBean udpateSubjectName(HttpSession session, @PathVariable("id") long id,
													   @RequestParam("subjectName") String subjectName)
	{
		ResultBean result = new ResultBean(ErrorCode.OK);
		
		// 检查参数
		if(subjectName == null || subjectName.equals(""))
		{
			result.setRetcode(ErrorCode.PARA_IS_ERROR);
			return result;
		}
		
		
		// 检查此条记录是否是当前用户的
		// 取出用户ID
		Integer userId = null;
		try
		{
			userId = Integer.parseInt((String) session.getAttribute("userId"));
		} catch (Exception e)
		{
			result.setRetcode(ErrorCode.PARA_IS_ERROR);
			return result;
		}
		if(subjectMapper.checkSubjectOwner(id, userId) == 0)
		{
			result.setRetcode(ErrorCode.NO_RIGHT);
			return result;
		}
		
		// 更新分类信息
		SubjectBean updateSubject = subjectMapper.readSubjectById(id);
		if(updateSubject == null)
		{
			result.setRetcode(ErrorCode.PARA_IS_ERROR);
			return result;
		}
		updateSubject.setName(subjectName);
		if(subjectMapper.updateSubject(updateSubject) == 0)
		{
			result.setRetcode(ErrorCode.DB_FAIL);
			return result;
		}
		
		return result;
	}
	
	/**
	 * 置某条知识分类为删除
	 * @param session 会话
 	 * @param id 删除的知识分类id
	 * @return 返回值 PARA_IS_ERROR, NO_RIGHT, DB_FAIL, OK
	 */
	@RequestMapping(value = "/subject", method = RequestMethod.DELETE, produces = {"application/json"})
	public @ResponseBody ResultBean deleteSubjectById(HttpSession session, @PathVariable("id") long id)
	{
		ResultBean result = new ResultBean(ErrorCode.OK);

		// 检查记录是否属于当前用户
		// 取出用户ID
		Integer userId = null;
		try
		{
			userId = Integer.parseInt((String) session.getAttribute("userId"));
		} catch (Exception e)
		{
			result.setRetcode(ErrorCode.PARA_IS_ERROR);
			return result;
		}
		
		SubjectBean checkSubject = subjectMapper.readSubjectById(id);
		if (checkSubject == null)
		{
			result.setRetcode(ErrorCode.PARA_IS_ERROR);
			return result;
		}
		if (checkSubject.getUserId() == userId)
		{
			result.setRetcode(ErrorCode.NO_RIGHT);
			return result;
		}
		
		// 置删除标记
		if(subjectMapper.updateSubjectDeleteFlag(id) == 0)
		{
			result.setRetcode(ErrorCode.DB_FAIL);
			return result;
		}
		
		// 查询上级节点是否还有叶子节点
		if(checkSubject.getParentSubject() != 0)
		{
			List<SubjectBean> checkChildSubjectList = subjectMapper.readChildSubject(checkSubject.getParentSubject(), userId);
			if(checkChildSubjectList == null || checkChildSubjectList.size() == 0)
			{
				// 已没有叶子节点，直接置为叶子节点
				subjectMapper.updateSubjectLeaf(checkSubject.getParentSubject());
			}
		}
		
		return result;
	}
	
	/**
	 * 获得子分类列表
	 * @param session 会话
	 * @param parentSubjectId 父分类ID， -1为根ID
	 * @return 返回值 PARA_IS_ERROR, DB_FAIL, OK
	 */
	@RequestMapping(value = "/subject/{id}", method = RequestMethod.GET, produces = {"application/json"})
	public @ResponseBody ResultBean getChildSubjectList(HttpSession session, @PathVariable("id") long parentSubjectId)
	{
		ResultBean result = new ResultBean(ErrorCode.OK);
		
		// 检查权限
		// 取出用户ID
		Integer userId = null;
		try
		{
			userId = Integer.parseInt((String) session.getAttribute("userId"));
		} catch (Exception e)
		{
			result.setRetcode(ErrorCode.PARA_IS_ERROR);
			return result;
		}
		if (subjectMapper.checkSubjectOwner(parentSubjectId, userId) == 0)
		{
			result.setRetcode(ErrorCode.DB_FAIL);
			return result;
		}
		
		// 取出子记录
		List<SubjectBean> childSubjectList = subjectMapper.readChildSubject(parentSubjectId, userId);
		
		result.addEntry("subjectList", childSubjectList);
		
		return result;
	}
}
