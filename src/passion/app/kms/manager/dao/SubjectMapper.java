package passion.app.kms.manager.dao;

import java.util.List;

import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Options;
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
	@Insert("insert into kms_subject (name, type, userId, parentSubject) values (#{name}， #{type}, #{userId}, #{parentSubject})")
	@Options(useGeneratedKeys = true, keyProperty = "id", keyColumn = "id")
	public int createSubject(SubjectBean subject);
	
	/**
	 * 获取指定ID的知识分类
	 * @param id 知识分类ID
	 * @return 知识分类信息
	 */
	@Select("select * from kms_subject where id = #{id}")
	public SubjectBean readSubjectById(long id);
	
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
	public int updateSubjectDeleteFlag(long id);
	
	/**
	 * 读取指定ID知识分类的子分类
	 * @param id 指定知识分类Id
	 * @return 子知识分类列表
	 */
	@Select("select * from kms_subject where parentSubject = #{id} and deleteFlag = 0")
	public List<SubjectBean> readChildSubject(long id);
	
	/**
	 * 检查知识分类的拥有者
	 * @param subjectId 知识分类的ID
	 * @param userId 用户的ID
	 */
	@Select("select count(*) from kms_subject where id = #{subjectId} and userId = #{userId}")
	public int checkSubjectOwner(long subjectId, long userId);
}
