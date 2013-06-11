package passion.app.kms.manager.dao;

import java.util.List;

import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Param;
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
	public TitleBean readTitleById(@Param("id") long id);
	
	/**
	 * 创建一条标题行
	 * @param title 标题
	 * @return 影响行数
	 */
	@Insert("insert into kms_title(type, name, knowledgeId, subjectId, updateDate, userId) values(#{type}, #{name}, #{knowledgeId}, #{subjectId}, #{updateDate}, #{userId})")
	@Options(useGeneratedKeys = true, keyProperty = "id", keyColumn = "id")
	public int createTitile(TitleBean title);
	
	/**
	 * 设置标题的删除标识
	 * @param id 知识标题Id
	 * @return 影响行数
	 */
	@Update("update kms_title set deleteFlag = 1, indexStatus = 3 where id = #{id}")
	public int updateTitleDeleteFlag(@Param("id") long id);
	
	/**
	 * 设置重新索引标识
	 * @param id 知识标题
	 * @return 影响行数
	 */
	@Update("update kms_title set indexStatus = #{indexStatus} where id = #{id}")
	public int updateTitleIndexStatus(@Param("id") long id, @Param("indexStatus") int indexStatus);
	
	/**
	 * 更新标题内容
	 * @param title 标题内容
	 * @return 影响函数
	 */
	@Update("update kms_title set type = #{type}, name = #{name}, updateDate = #{updateDate}, indexStatus = 2 where id = #{id}")
	public int updateTitle(TitleBean title);
	
	/**
	 * 检查知识标题的拥有者是否是指定用户
	 * @param titleId 标题拥有者的ID
	 * @param userId 指定用户的ID
	 * @return
	 */
	@Select("select count(*) from kms_subject s, kms_title t where t.id = #{titleId} and t.subjectId = s.id and s.userId = #{userId}")
	public int checkTitleOwner(@Param("titleId") long titleId, @Param("userId") long userId);
	
	/**
	 * 获取某一个知识分类下面的知识内容
	 * @param startPosition 起始位置，以0为起点
	 * @param rows 需要返回的数据的最大行数
	 * @param subjectId 知识分类的ID
	 * @param userId 所属用户的ID
	 * @return 数据集
	 */
	@Select("select * from kms_title where userId = #{userId} and deleteFlag = 0 and type = 1 and subjectId = #{subjectId} order by updateDate desc limit #{startPosition}, #{rows}")
	public List<TitleBean> getTitleList(@Param("startPosition") long startPosition, @Param("rows") long rows, 
										 @Param("subjectId") long subjectId, @Param("userId") long userId);
	
	/**
	 * 获取某一个知识分类下面的知识数量
	 * @param subjectId
	 * @param userId
	 * @return 知识数量
	 */
	@Select("select count(*) from kms_title where userId = #{userId} and deleteFlag = 0 and type = 1 and subjectId = #{subjectId}")
	public long getTitleListCount(@Param("subjectId") long subjectId, @Param("userId") long userId);
	
	/**
	 * 获得用户下面所有数据列表
	 * @param startPosition 起始位置，以0为起点
	 * @param rows 需要返回的数据的最大行数
	 * @param userId 所属用户的ID
	 * @return 数据集
	 */
	@Select("select * from kms_title where userId = #{userId} and deleteFlag = 0 and type = 1 order by updateDate desc limit #{startPosition}, #{rows}")
	public List<TitleBean> getAllTitleList(@Param("startPosition") long startPosition, @Param("rows") long rows,
											@Param("userId") long userId);
	
	/**
	 * 获得某用户下面知识的数量
	 * @param userId 用户ID
	 * @return 知识数量
	 */
	@Select("select count(*) from kms_title where userId = #{userId} and deleteFlag = 0 and type = 1")
	public long getAllTitleListCount(@Param("userId") long userId);
	
	/**
	 * 获得某个知识的副标题
	 * @param knowledgeId 知识ID
	 * @return 副标题列表
	 */
	@Select("select * from kms_title where knowledgeId = #{knowledgeId} and type = 2 and deleteFlag = 0")
	public List<TitleBean> getOtherTitleByKnowledgeId(@Param("knowledgeId") long knowledgeId);
	
	/**
	 * 从数据库中获取知识主标题
	 * @param knowledgeId 知识ID
	 * @return 返回知识主标题
	 */
	@Select("select * from kms_title where knowledgeId = #{knowledgeId} and type = 1 and deleteFlag = 0 limit 0,1")
	public TitleBean getMainTitleByKnowledgeId(@Param("knowledgeId") long knowledgeId);
	
	/**
	 * 置某一个知识的副标题为删除标记
	 * 此处索引标记也需要设置为3，代表需要从索引中删除
	 * @param knowledgeId 知识ID
	 * @return 受影响的条数
	 */
	@Update("update kms_title set deleteFlag = 1, indexStatus = 3 where knowledgeId = #{knowledgeId} and type = 2 and deleteFlag = 0")
	public int updateOtherTitleDeleteFlagByKnowledgeId(@Param("knowledgeId") long knowledgeId);
	
	/**
	 * 
	 * @return
	 */
	@Select("select * from kms_title where indexStatus <> 0")
	public List<TitleBean> getNeedIndexTitle();
}
