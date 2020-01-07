import com.wxg.dao.StudentMapper;
import com.wxg.dao.TeacherMapper;
import com.wxg.pojo.Student;
import com.wxg.pojo.Teacher;
import com.wxg.utils.MyBatisUtils;
import org.apache.ibatis.session.SqlSession;
import org.junit.Test;

import java.util.List;

public class UserMapperTest {
    @Test
    public void test(){
        SqlSession sqlSession = MyBatisUtils.getSqlSession();
        TeacherMapper mapper = sqlSession.getMapper(TeacherMapper.class);
        Teacher teacher=mapper.getTeacher2(1);
        System.out.println(teacher);
    }

    @Test
    public void test2(){
        SqlSession sqlSession = MyBatisUtils.getSqlSession();
        StudentMapper mapper = sqlSession.getMapper(StudentMapper.class);
        List<Student> list = mapper.getStudent2();
        for (Student student : list) {
            System.out.println(student);
        }

    }
}
