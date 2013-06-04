package passion.app.kms.manager.dao;

import java.util.List;

import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import passion.app.kms.manager.bean.TitleBean;

/**
 * 标题操作类
 * @author gouchy
 *
 */
public interface TitleMapper
{
	/**
	 * 根据ID获取标题内容
	 * @param id Id号
	 * @return 标题内容
	 */
	@Select("select * from kms_title where id = #{id}")
	public TitleBean readTitleById(long id);
	
	/**
	 * 创建一条标题行
	 * @param title 标题
	 * @return 影响行数
	 */
	@Insert("insert into kms_title(type, name, knowledgeId, subjectId, updateDate) values(#{type}, #{name}, #{knowledgeId}, #{subjectId}, #{updateDate})")
	@Options(useGeneratedKeys = true, keyProperty = "id", keyColumn = "id")
	public int createTitile(TitleBean title);
	
	/**
	 * 设置标题的删除标识
	 * @param id 知识标题Id
	 * @return 影响行数
	 */
	@Update("update kms_title set deleteFlag = 1 where id = #{id}")
	public int updateTitleDeleteFlag(long id);
	
	/**
	 * 设置重新索引标识
	 * @param id 知识标题
	 * @return 影响行数
	 */
	@Update("update kms_title set indexSatus = #{indexStatus} where id = #{id}")
	public int updateTitleIndexStatus(long id, int indexStatus);
	
	/**
	 * 更新标题内容
	 * @param title 标题内容
	 * @return 影响函数
	 */
	@Update("update kms_title set type = #{type}, name = #{name}, updateDate = #{updateDate} where id = #{id}")
	public int updateTitle(TitleBean title);
	
	/**
	 * 检查知识标题的拥有者是否是指定用户
	 * @param titleId 标题拥有者的ID
	 * @param userId 指定用户的ID
	 * @return
	 */
	@Select("select count(*) from kms_subject s, kms_title t where t.id = #{titleId} and t.subjectId = s.id and s.userId = #{userId}")
	public int checkTitleOwner(long titleId, long userId);
	
	@Select("select * from kms_title ")
	public List<TitleBean> getTitleList(long page, long rows, long subjectId, long userId);
	
	@Select("select * from kms_title")
	public List<TitleBean> getAllTitleList(long page, long rows, long userId);
}
