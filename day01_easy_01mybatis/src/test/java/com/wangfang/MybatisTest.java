package com.wangfang;


import com.wangfang.dao.IUserDao;
import com.wangfang.dao.StudentMapper;
import com.wangfang.domain.Student;
import com.wangfang.domain.StudentLecture;
import com.wangfang.domain.User;
import com.wangfang.domain.UserParam;
import org.apache.ibatis.io.Resources;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;
import org.apache.log4j.Logger;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

public class MybatisTest {
    public static void main(String[] args) throws IOException {
        //��ȡ�����ļ�
        InputStream is = Resources.getResourceAsStream("mybatisConfig.xml");
        //��ȡ����Դ�����ļ�
        Properties props = new Properties();
        //����sqlsessionfactory
        SqlSessionFactoryBuilder builder = new SqlSessionFactoryBuilder();
        SqlSessionFactory sqlSessionFactory =  builder.build(is,"default",props);
        //ʹ�ù�������sqlsession
        SqlSession sqlSession = sqlSessionFactory.openSession();

        //System.out.println(sqlSession);
        //ʹ��sqlsession������dao�ӿڵĴ������
        IUserDao userDao = sqlSession.getMapper(IUserDao.class);
        //ͨ���������ִ�з���
        List<User> list = userDao.findAll();
        for(User user : list)
            System.out.println(user.toString());
        //�ͷ���Դ
        sqlSession.close();
        is.close();
    }
    private  IUserDao userDao;
    private InputStream is;
    private SqlSession sqlSession;
    private StudentMapper studentMapper;
    private static final Logger log = Logger.getLogger(MybatisTest.class);

    @Before
    public void loadProperties() throws IOException {
        is = Resources.getResourceAsStream("mybatisConfig.xml");
        //��ȡ����Դ�����ļ�
        Properties props = new Properties();
        //����sqlsessionfactory
        SqlSessionFactoryBuilder builder = new SqlSessionFactoryBuilder();
        SqlSessionFactory sqlSessionFactory =  builder.build(is,"default",props);
        //ʹ�ù�������sqlsession
        sqlSession = sqlSessionFactory.openSession();
        //System.out.println(sqlSession);
        //ʹ��sqlsession������dao�ӿڵĴ������
        userDao = sqlSession.getMapper(IUserDao.class);
        studentMapper = sqlSession.getMapper(StudentMapper.class);
    }

    @After
    public void close() throws IOException {
        sqlSession.close();
        is.close();
    }

    public void print(Object object){
        System.out.println(object);
    }
    @Test
    public void findAll() throws IOException {
        List<User> list = userDao.findAll();
        print(list.toArray());
    }

    @Test
    public void findUser()throws IOException{
        User user = userDao.findUser(1,null);
        print(user.toString());
    }

    @Test
    public void saveUser(){
        List<User> users = new ArrayList<User>();
        User item = new User("�⹫","��","home","2019-07-02 23:23:23");
        User user = new User("����","Ů","home","2019-07-04 23:23:23");
        users.add(item);
        users.add(user);
        //User user = new User("����","Ů","home","2019-07-01 23:23:23");
        boolean result = userDao.saveUsers(users);
        sqlSession.commit();
        print(result);
    }

    @Test
    public void saveUserForPrimaryKey(){
        UserParam param = new UserParam(14,"����","Ů","����","2019-11-02 15:35:35");
        userDao.saveUser(param);
        sqlSession.commit();
    }

    @Test
    public void setUser(){
        User user = new User("����","Ů","home","2019-07-01 23:23:23");
        user.setAddress("changshang");
        boolean result = userDao.setUser(user.getAddress(),4);
        sqlSession.commit();
        print(result);
    }

    @Test
    public void testOneByOne(){
        studentMapper.getStudent(1);
    }

    @Test
    public void testOneByMany(){
        Student student = studentMapper.getStudent(1);
        log.info("----ִ�в�ѯstudent-----");
    }

    @Test
    public void testGetAllStudent(){
        studentMapper.getAllStudent(1);
        log.info("----ִ�в�ѯtestGetAllStudent-----");
    }

    @Test
    public void testIfTag(){
        UserParam userParam = new UserParam(1,"wangfang",null,null,null);
        userDao.findUserByParamBean(userParam);
    }

    @Test
    public void testGetByParam(){
        userDao.getUserByParam(1,"wangfang");
    }

    @Test
    public void testGetByTag2(){
        UserParam param1 = new UserParam();
        UserParam param = new UserParam(1,null,null,null,null);
        userDao.getUserByTag2(param);
    }

    @Test
    public void testGetByTag3(){
        UserParam param1 = new UserParam();
        UserParam param = new UserParam(1,null,null,null,null);
        userDao.getUserByTag3(param);
    }

    @Test
    public void testSetByTag(){
        UserParam param = new UserParam();
        UserParam param1 = new UserParam(1,"wangfang",null,"�潭��԰",null);
        userDao.setByTag(param1);
    }

    @Test
    public void testSetByTagPassTrim(){
        UserParam param = new UserParam();
        UserParam param1 = new UserParam(1,"wangfang",null,"�潭��԰",null);
        userDao.setByTagPassByTrim(param1);
    }

    @Test
    public void testTrimTag(){
        UserParam param1 = new UserParam();
        UserParam param = new UserParam(1,null,null,null,null);
        userDao.getUserByTag5(param);
    }

    @Test
    public void testTagForeach(){
        List list = new ArrayList();
        list.add("��");
        list.add("Ů");
        userDao.getByForeach(list);
    }


    @Test
    public void getUserByBind(){
        userDao.getUserByBind("wangfang");
    }
}
