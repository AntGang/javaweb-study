import com.wxg.dao.UserMapper;
import com.wxg.pojo.User;
import com.wxg.utils.MyBatisUtils;
import org.apache.ibatis.session.SqlSession;
import org.junit.Test;

import java.util.List;

public class UserMapperTest {
    @Test
    public void test(){
        SqlSession sqlSession = MyBatisUtils.getSqlSession();
        UserMapper mapper = sqlSession.getMapper(UserMapper.class);
/*
        List<User> userList = mapper.getUserList();
        for (User user : userList) {
            System.out.println(user);
        }
*/

        //User user = mapper.getUserById(1);
        User user = new User();
        user.setId(8);
        user.setName("啦啦啦");
        user.setPassword("123");
        int count=mapper.addUuser(user);
        System.out.println(count);

        sqlSession.close();

    }
}
