package passion.app.kms.manager.dao;

import java.util.List;

import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import passion.app.kms.manager.bean.SubjectBean;

/**
 * 知识分类操作
 * @author gouchy
 *
 */
public interface SubjectMapper
{
	/**
	 * 创建知识分类
	 * @param subject 知识分类，id在执行完成后获得插入数据的id
	 * @return  受影响的行数
	 */
	@Insert("insert into kms_subject (name, type, userId, parentSubject, leaf) values (#{name}， #{type}, #{userId}, #{parentSubject}, #{leaf})")
	@Options(useGeneratedKeys = true, keyProperty = "id", keyColumn = "id")
	public int createSubject(SubjectBean subject);
	
	/**
	 * 获取指定ID的知识分类
	 * @param id 知识分类ID
	 * @return 知识分类信息
	 */
	@Select("select * from kms_subject where id = #{id}")
	public SubjectBean readSubjectById(@Param("id") long id);
	
	/**
	 * 更新知识分类信息
	 * @param subject 知识分类信息，id：需要更新的数据id号，userId：原始创建者的Id号，name：需要更新的内容
	 * @return 受影响的行数
	 */
	@Update("update kms_subject set name = #{name} where id = #{id} and userId = #{userId}")
	public int updateSubject(SubjectBean subject);
	
	/**
	 * 设置知识分类删除标识
	 * @param id 知识分类Id
	 * @return 受影响的行数
	 */
	@Update("update kms_subject set deleteFlag =1 where id = #{id}")
	public int updateSubjectDeleteFlag(@Param("id") long id);
	
	/**
	 * 读取指定ID知识分类的子分类
	 * @param id 指定知识分类Id
	 * @return 子知识分类列表
	 */
	@Select("select * from kms_subject where parentSubject = #{id} and deleteFlag = 0 and userId = #{userId}")
	public List<SubjectBean> readChildSubject(@Param("id") long id, @Param("userId") long userId);
	
	/**
	 * 检查知识分类的拥有者
	 * @param subjectId 知识分类的ID
	 * @param userId 用户的ID
	 */
	@Select("select count(*) from kms_subject where id = #{subjectId} and userId = #{userId}")
	public int checkSubjectOwner(@Param("subjectId") long subjectId, @Param("userId") long userId);
	
	/**
	 * 更新节点为非叶子节点
	 * @param subjectId
	 * @return
	 */
	@Update("update kms_subject set leaf = 1 where id = #{subjectId}")
	public int updateSubjectNotLeaf(@Param("subjectId") long subjectId);
	
	/**
	 * 更新节点为叶子节点
	 * @param subjectId
	 * @return
	 */
	@Update("update kms_subject set leaf = 0 where id = #{subjectId}")
	public int updateSubjectLeaf(@Param("subjectId") long subjectId);
}
