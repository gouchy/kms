package passion.app.kms.manager.controller;

import java.util.List;

import javax.servlet.http.HttpSession;
import javax.ws.rs.Consumes;
import javax.ws.rs.CookieParam;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.jboss.resteasy.annotations.Form;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import passion.app.kms.manager.bean.ResultBean;
import passion.app.kms.manager.bean.SubjectBean;
import passion.app.kms.manager.constant.ErrorCode;
import passion.app.kms.manager.dao.SubjectMapper;
import passion.app.kms.manager.data.UserData;

@Controller
@Path("/subject")
public class SubjectController
{
	@Autowired
	private SubjectMapper subjectMapper;
	
	/**
	 * 创建一个新的subject节点
	 * @param subject 分类
	 * @return 返回值 PARA_IS_ERROR, DB_FAIL, OK
	 */
	@PUT
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public  ResultBean createSubject(SubjectBean subject, @CookieParam("username") String username)
	{
		ResultBean result = new ResultBean(ErrorCode.OK);

		// 检查参数完整性
		if(subject.getName() == null || subject.getName().equals(""))
		{
			result.setRetcode(ErrorCode.PARA_IS_ERROR);
			return result;
		}
		
		// 取出用户ID
		long userId = UserData.getBindId(username);
		
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
		
		// 返回的结果中增加新创建的subjectId编号
		result.addEntry("subjectId", subject.getId());
		
		return result;
	}
	
	/**
	 * 更新知识分类标题
	 * @param session 会话
	 * @param id 知识分类id
	 * @param subjectName 知识分类内容
	 * @return 返回值
	 */
	@POST
	@Path("/{id}")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public ResultBean udpateSubjectName(HttpSession session, @PathParam("id") long id,
													  @Form String subjectName)
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
		long userId = 0;
		try
		{
			userId =  (long) session.getAttribute("userId");
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
	@DELETE
	@Path("/{id}")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public ResultBean deleteSubjectById(HttpSession session, @PathParam("id") long id)
	{
		ResultBean result = new ResultBean(ErrorCode.OK);

		// 检查记录是否属于当前用户
		// 取出用户ID
		long userId = 0;
		try
		{
			userId =  (long) session.getAttribute("userId");
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
		if (checkSubject.getUserId() != userId)
		{
			result.setRetcode(ErrorCode.NO_RIGHT);
			return result;
		}
		if (checkSubject.getLeaf() == 1)
		{
			result.setRetcode(ErrorCode.PARA_IS_ERROR);
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
	@GET
	@Path("/{id}")
	@Produces(MediaType.APPLICATION_JSON)
	public ResultBean getChildSubjectList( @PathParam("id") long parentSubjectId, 
											@CookieParam("username") String username)
	{
		ResultBean result = new ResultBean(ErrorCode.OK);
		
		// 检查权限
		// 取出用户ID
		long userId = UserData.getBindId(username);

		if (parentSubjectId != 0 && subjectMapper.checkSubjectOwner(parentSubjectId, userId) == 0)
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
