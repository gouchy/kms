package passion.app.kms.manager.dao;

import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import passion.app.kms.manager.bean.KnowledgeBean;

/**
 * 知识内容操作
 * @author gouchy
 *
 */
public interface KnowledgeMapper
{
	/**
	 * 创建新的知识内容
	 * @param knowledge 知识内容，id属性在插入后获得记录号
	 * @return 受影响的函数
	 */
	@Insert("insert into kms_knowledge (type, content) values (#{type}, #{content}")
	@Options(useGeneratedKeys = true, keyProperty = "id", keyColumn = "id")
	public int createKnowledge(KnowledgeBean knowledge);
	
	/**
	 * 读取一个知识
	 * @param id ID号
	 * @return 知识内容
	 */
	@Select("select * from kms_knowledge where id = #{id}")
	public KnowledgeBean readKnowledgeById(long id);
	
	/**
	 * 更新知识的内容
	 * @param knowledge 知识内容, id:需要修改记录的id，type：需要修改知识的类型，content：知识内容
	 * @return 受影响的行数
	 */
	@Update("update kms_knowledge set type = #{type}, content = #{content} where id = #{id}")
	public int updateKnowledge(KnowledgeBean knowledge);
	
	/**
	 * 设置知识的删除标签
	 * @param id 需要删除的知识Id
	 * @return 受影响的行数
	 */
	@Update("update kms_knowledge set deleteFlag = 1 where id =#{id}")
	public int updateKnowledgeDeleteFlag(long id);

}
