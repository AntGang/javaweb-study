---
typora-copy-images-to: ./
---

# Mybatis

## 1、简介

MyBatis 是一款优秀的**持久层框架**，它支持定制化 SQL、存储过程以及高级映射。MyBatis 避免了几乎所有的 JDBC 代码和手动设置参数以及获取结果集。MyBatis 可以使用简单的 XML 或注解来配置和映射原生类型、接口和 Java 的 POJO（Plain Old Java Objects，普通老式 Java 对象）为数据库中的记录。





## 2、第一个Mybatis程序

### 2.1、安装

使用Maven 来构建项目，则需将下面的 dependency 代码置于 pom.xml

```xml
<dependency>
    <groupId>org.mybatis</groupId>
    <artifactId>mybatis</artifactId>
    <version>3.4.6</version>
</dependency>
```

### 2.2、配置文件

XML 配置文件中包含了对 MyBatis 系统的核心设置，包含获取数据库连接实例的数据源（DataSource）和决定事务作用域和控制方式的事务管理器（TransactionManager）。要注意 XML 头部的声明，它用来验证 XML 文档正确性。environment 元素体中包含了事务管理和连接池的配置。mappers 元素则是包含一组映射器（mapper），这些映射器的 XML 映射文件包含了 SQL 代码和映射定义信息。

```xml
<?xml version="1.0" encoding="UTF-8" ?>
<!DOCTYPE configuration
  PUBLIC "-//mybatis.org//DTD Config 3.0//EN"
  "http://mybatis.org/dtd/mybatis-3-config.dtd">
<configuration>
  <environments default="development">
    <environment id="development">
      <transactionManager type="JDBC"/>
      <dataSource type="POOLED">
        <property name="driver" value="${driver}"/>
        <property name="url" value="${url}"/>
        <property name="username" value="${username}"/>
        <property name="password" value="${password}"/>
      </dataSource>
    </environment>
  </environments>
  <mappers>
    <mapper resource="org/mybatis/example/BlogMapper.xml"/>
  </mappers>
</configuration
```

每个基于 MyBatis 的应用都是以一个 SqlSessionFactory 的实例为核心的。SqlSessionFactory 的实例可以通过 SqlSessionFactoryBuilder 获得。而 SqlSessionFactoryBuilder 则可以从 XML 配置文件或一个预先定制的 Configuration 的实例构建出 SqlSessionFactory 的实例。

### 2.3、从 SqlSessionFactory 中获取 SqlSession

既然有了 SqlSessionFactory，顾名思义，我们就可以从中获得 SqlSession 的实例了。SqlSession 完全包含了面向数据库执行 SQL 命令所需的所有方法。你可以通过 SqlSession 实例来直接执行已映射的 SQL 语句。例如：

```java
@Test
public void test(){
        //第一步：获得SqlSession对象
        SqlSession sqlSession = MyBatisUtils.getSqlSession();
        try{
            //方式一：getMapper
            UserMapper mapper = sqlSession.getMapper(UserMapper.class);
            List<User> userList = mapper.getUserList();       
		//方式二：
        //List<User> userList = sqlSession.selectList("com.wxg.dao.UserDao.getUserList");

        for (User user : userList) {
            System.out.println(user);
        }

    }
    finally {
        //关闭SqlSession
        sqlSession.close();
    }
}
```
编写一个工具类MyBatisUtils获取Sqlsession

```java
//sqlSessionFactory--->SessionFactory
public class MyBatisUtils {
    private static SqlSessionFactory sqlSessionFactory;
    static {
        try{
            //使用mybatis第一步、获取sqlSessionFactory对象
            String resource = "mybatis-config.xml";
            InputStream inputStream = Resources.getResourceAsStream(resource);
            sqlSessionFactory = new SqlSessionFactoryBuilder().build(inputStream);
        }catch(IOException e) {
            e.printStackTrace();
        }
    }

    //既然有了 SqlSessionFactory，顾名思义，我们就可以从中获得 SqlSession 的实例了。
    // SqlSession 完全包含了面向数据库执行 SQL 命令所需的所有方法。
    // 你可以通过 SqlSession 实例来直接执行已映射的 SQL 语句。
    public static SqlSession getSqlSession(){
        return sqlSessionFactory.openSession();
    }

}
```

### 2.4、映射 SQL 语句

```xml
<?xml version="1.0" encoding="UTF-8" ?>
<!DOCTYPE mapper
        PUBLIC "-//mybatis.org//DTD Mapper 3.0//EN"
        "http://mybatis.org/dtd/mybatis-3-mapper.dtd">
<!--namespace=绑定一个对应的mapper接口-->
<mapper namespace="com.wxg.dao.UserMapper">
    <!--select查询语句-->
    <select id="getUserList" resultType="com.wxg.pojo.User">
       /*定义sql*/
       select * from mybatis.user
   </select>
</mapper>
   
```



## 3、CRUD

### 3.1、namespace

​		绑定一个对应的mapper/dao接口

​		命名空间的作用有两个，一个是利用更长的完全限定名来将不同的语句隔离开来，同时也实现了你上面见到的接口绑定。就算你觉得暂时用不到接口绑定，你也应该遵循这里的规定，以防哪天你改变了主意。 长远来看，只要将命名空间置于合适的 Java 包命名空间之中，你的代码会变得更加整洁，也有利于你更方便地使用 MyBatis。

### 3.2、select

```xml
 <!--select查询语句-->
 <select id="getUserList" resultType="com.wxg.pojo.User">
    /*定义sql*/
    select * from mybatis.user
</select>

 <select id="getUserById" resultType="com.wxg.pojo.User" parameterType="int">
    /*定义sql*/
    select * from mybatis.user where id = #{id};
</select>
```

### 3.3、insert

```xml
<!--对象中的属性可以直接取出来-->
<insert id="addUser" parameterType="com.wxg.pojo.User">
    insert into mybatis.user (id, name, pwd) values (#{id},#{name},#{pwd});
</insert>
```

### 3.4、update

```xml
<update id="updateUser" parameterType="com.wxg.pojo.User">
    update mybatis.user set name = #{name},pwd=#{pwd} where id=#{id} ;
</update>
```

### 3.5、delete

```xml
<delete id="deleteUser" parameterType="int">
    delete from mybatis.user where id = #{id};
</delete>
```

- 注意点：增删改要提交事务

### 3.6、分析错误

- 映射配置文件标签不要匹配错
- resource绑定mapper，需要使用路径/
- NullPointerException没有注册到资源
- XML配置文件编码问题

```xml
<properties>
    <!-- 设置默认编码 -->
    <project.build.sourceEncoding>UTF-8</project.build.sourceEncoding>
</properties>
```

- Maven配置文件路径问题

```xml
<!--在build中配置resources，来防止我们资源导出失败的问题-->
<build>
    <resources>
        <resource>
            <directory>src/main/resources</directory>
            <includes>
                <include>**/*.properties</include>
                <include>**/*.xml</include>
            </includes>
        </resource>
        <resource>
            <directory>src/main/java</directory>
            <includes>
                <include>**/*.properties</include>
                <include>**/*.xml</include>
            </includes>
            <filtering>true</filtering>
        </resource>
    </resources>
</build>
```



### 3.7、万能map

如果实体类，数据库中的表、字段或者参数过多，应当使用map！

```
int addUser2(Map<String, Object> map);


<!--对象中的属性，可以直接取出来 parameterType=传递map中的key-->
    <insert id="addUser2" parameterType="map">
        insert into mybatis.user (id, name, pwd) values (#{userId},#{userName},#{password});
    </insert>
```

```java
    //万能map
    @Test
    public void addUser2(){
        SqlSession sqlSession = MyBatisUtils.getSqlSession();
        UserMapper mapper = sqlSession.getMapper(UserMapper.class);
        HashMap<String, Object> map = new HashMap<>();
        map.put("userId",4);
        map.put("userName","王五");
        map.put("password","23333");
        mapper.addUser2(map);
        //提交事务
        sqlSession.commit();
        sqlSession.close();
    }
```



Map传递参数，直接直接在sql取出key即可！【parameterType="map"】

对象传递参数，直接在sql中去对象的属性即可！【parameterType="Object"】

只有一个基本类型参数的情况下，直接在sql中取到！【parameterType=“int”（可省略）】

**多个参数用Map或者注解**

### 3.8、模糊查询

```xml
	List<User> likeByName(String name);  
        
	<select id="likeByName" resultType="com.wxg.pojo.User">
        select * from user where name like #{name}
    </select>
```

```java
    List<User> users = userMapper.likeByName("%李%");    
```

如果在sql拼接中使用通配符则会导致sql注入

​			select * from user where name like “%”#{name}“%”



## 4、配置解析

### 4.1、核心配置文件

- mybatis-config.xml

- Mybatis的配置文件包含了会深深影响Mybatis行为的设置和属性信息

  ```xm
  configuration（配置）
  properties（属性）
  settings（设置）
  typeAliases（类型别名）
  typeHandlers（类型处理器）
  objectFactory（对象工厂）
  plugins（插件）
  environments（环境配置）
  environment（环境变量）
  transactionManager（事务管理器）
  dataSource（数据源）
  databaseIdProvider（数据库厂商标识）
  mappers（映射器）
  ```

注：**各个标签是有顺序的**

### 4.2、环境变量（environments）

MyBatis 可以配置成适应多种环境，这种机制有助于将 SQL 映射应用于多种数据库之中,不过要记住：尽管可以配置多个环境，但**每个 SqlSessionFactory 实例只能选择一种环境。**



```xml
<environment id="development">
    <transactionManager type="JDBC">
      <property name="..." value="..."/>
    </transactionManager>
    <dataSource type="POOLED">
      <property name="driver" value="${driver}"/>
      <property name="url" value="${url}"/>
      <property name="username" value="${username}"/>
      <property name="password" value="${password}"/>
    </dataSource>
  </environment>
```

注意这里的关键点:

- 默认使用的环境 ID（比如：default="development"）。
- 每个 environment 元素定义的环境 ID（比如：id="development"）。
- 事务管理器的配置（比如：type="JDBC"）。
  - JDBC – 这个配置就是直接使用了 JDBC 的提交和回滚设置，它依赖于从数据源得到的连接来管理事务作用域。
  - MANAGED – 这个配置几乎没做什么。它从来不提交或回滚一个连接，而是让容器来管理事务的整个生命周期（比如 JEE 应用服务器的上下文）。 默认情况下它会关闭连接，然而一些容器并不希望这样，因此需要将 closeConnection 属性设置为 false 来阻止它默认的关闭行为。

- 数据源的配置（比如：type="POOLED"）。
  - **POOLED**– 这种数据源的实现利用“池”的概念将 JDBC 连接对象组织起来，避免了创建新的连接实例时所必需的初始化和认证时间。 这是一种使得并发 Web 应用快速响应请求的流行处理方式。



### 4.3、属性（properties）

这些属性都是可外部配置且可动态替换的，既可以在典型的 Java 属性文件中配置，亦可通过 properties 元素的子元素来传递。【db.properties】

```properties
driver=com.mysql.jdbc.Driver
url=jdbc:mysql://localhost:3306/mybatis?useSSL=true&useUnicode=true&character&Encoding=utf8
username=root
password=123456
```

然后其中的属性就可以在整个配置文件中被用来替换需要动态配置的属性值。

```xml
<dataSource type="POOLED">
    <property name="driver" value="${driver}"/>
    <property name="url" value="${url}"/>
    <property name="username" value="${username}"/>
    <property name="password" value="${password}"/>
</dataSource>
```



### 4.4、类型别名（typeAliases）

类型别名是为 Java 类型设置一个短的名字。

```xml
	<!--可以给实体类起别名-->
    <typeAliases>
        <typeAlias type="com.wxg.pojo.User" alias="User"/>
    </typeAliases>
```

也可以指定一个包名，MyBatis 会在包名下面搜索需要的 Java Bean，比如：

```xml
<typeAliases>
  <package name="domain.blog"/>
</typeAliases>
```

每一个在包 `domain.blog` 中的 Java Bean，在没有注解的情况下，会使用 Bean 的首字母小写的非限定类名来作为它的别名。 比如 `domain.blog.Author` 的别名为 `author`；若有注解，则别名为其注解值。见下面的例子：

```xml
@Alias("author")
public class Author {
    ...
}
```

| 别名 | 映射的类型 |
| :--- | :--------- |
| _int | int        |
| int  | Integer    |



### 4.5、设置（settings）

这是 MyBatis 中极为重要的调整设置，它们会改变 MyBatis 的运行时行为。

| 设置名             | 描述                                                         | 有效值                                                       | 默认值 |
| :----------------- | :----------------------------------------------------------- | :----------------------------------------------------------- | :----- |
| cacheEnabled       | 全局地开启或关闭配置文件中的所有映射器已经配置的任何缓存。   | true \| false                                                | true   |
| lazyLoadingEnabled | 延迟加载的全局开关。当开启时，所有关联对象都会延迟加载。 特定关联关系中可通过设置 `fetchType` 属性来覆盖该项的开关状态。 | true \| false                                                | false  |
| logImpl            | 指定 MyBatis 所用日志的具体实现，未指定时将自动查找。        | SLF4J \| LOG4J \| LOG4J2 \| JDK_LOGGING \| COMMONS_LOGGING \| STDOUT_LOGGING \| NO_LOGGING | 未设置 |



### 4.6、其他配置

- typeHandlers（类型处理器）

- objectFactory（对象工厂）

- 插件（plugins）

  ###### MyBatis-Plus





### 4.7、映射器（mappers）

既然 MyBatis 的行为已经由上述元素配置完了，我们现在就要定义 SQL 映射语句了。 但是首先我们需要告诉 MyBatis 到哪里去找到这些语句。 Java 在自动查找这方面没有提供一个很好的方法，所以最佳的方式是告诉 MyBatis 到哪里去找映射文件。 你可以使用相对于类路径的资源引用， 或完全限定资源定位符（包括 `file:///` 的 URL），或类名和包名等。例如：

```xml
方式一
<!-- 使用相对于类路径的资源引用 -->
<mappers>
  <mapper resource="org/mybatis/builder/AuthorMapper.xml"/>
  <mapper resource="org/mybatis/builder/BlogMapper.xml"/>
  <mapper resource="org/mybatis/builder/PostMapper.xml"/>
</mappers>
方式二
<!-- 使用完全限定资源定位符（URL） -->
<mappers>
  <mapper url="file:///var/mappers/AuthorMapper.xml"/>
  <mapper url="file:///var/mappers/BlogMapper.xml"/>
  <mapper url="file:///var/mappers/PostMapper.xml"/>
</mappers>
方式三
<!-- 使用映射器接口实现类的完全限定类名 -->
<mappers>
  <mapper class="org.mybatis.builder.AuthorMapper"/>
  <mapper class="org.mybatis.builder.BlogMapper"/>
  <mapper class="org.mybatis.builder.PostMapper"/>
</mappers>
方式四
<!-- 将包内的映射器接口实现全部注册为映射器 -->
<mappers>
  <package name="org.mybatis.builder"/>
</mappers>
```

这些配置会告诉了 MyBatis 去哪里找映射文件，剩下的细节就应该是每个 SQL 映射文件。

注：**方法一可以写成*Mapper.xml来匹配，方法三和方法四的接口和配置文件必须同名并且在同包下**

### 4.8、作用域（Scope）和生命周期

作用域和生命周期类是至关重要的，因为错误的使用会导致非常严重的并发问题。





## 5、解决属性名和字段名不一致的问题

### 5.1、问题

```java
public class User {
    private int id;
    private String name;
    private String pwd;
 }
```

```
select * from mybatis.user
//类处理器
select id,name,pwd from mybatis.user where id=#{id}
```

![image-20200103221157196](F:\学习笔记\image-20200103221157196.png)



解决方法：

- 起别名：

  ```
  select id,name,pwd as password from mybatis.user where id=#{id}
  ```



### 5.2、resultMap

结果集映射

| id   | name | pwd      |
| ---- | ---- | -------- |
| id   | name | password |

column数据库字段，property实体类中属性

```xml
 <!--select查询语句-->
 <select id="getUserList" resultMap="UserMap">
    select * from mybatis.user
</select>

 <resultMap id="UserMap" type="User">
     <result column="id" property="id"></result>
     <result column="name" property="name"></result>
     <result column="pwd" property="password"></result>
 </resultMap>
```

- `resultMap` 元素是 MyBatis 中最重要最强大的元素。

- ResultMap 的设计思想是，对于简单的语句根本不需要配置显式的结果映射，而对于复杂一点的语句只需要描述它们的关系就行了。
- `ResultMap` 最优秀的地方在于，虽然你已经对它相当了解了，但是根本就不需要显式地用到他们。
- 如果世界总是这么简单就好了。





## 6、日志

### 6.1、日志工厂

| logImpl | 指定 MyBatis 所用日志的具体实现，未指定时将自动查找。 | SLF4J \| LOG4J \| LOG4J2 \| JDK_LOGGING \| COMMONS_LOGGING \| STDOUT_LOGGING \| NO_LOGGING | 未设置 |
| ------- | ----------------------------------------------------- | ------------------------------------------------------------ | ------ |
|         |                                                       |                                                              |        |

- SLF4J
-  **LOG4J**
-  LOG4J2
-  JDK_LOGGING
-  COMMONS_LOGGING
-  **STDOUT_LOGGING**
-  NO_LOGGING

在Mybatis中具体使用哪个日志实现，**STDOUT_LOGGING**标准日志输出

```xml
<settings>
    <setting name="logImpl" value="STDOUT_LOGGING"/>
</settings>
```



![image-20200104123225591](F:\学习笔记\image-20200104123225591.png)



### 6.2、log4j

- Log4j是[Apache](https://baike.baidu.com/item/Apache/8512995)的一个开源项目，通过使用Log4j，我们可以控制日志信息输送的目的地是[控制台](https://baike.baidu.com/item/控制台/2438626)、文件、[GUI](https://baike.baidu.com/item/GUI)组件
- 我们也可以控制每一条日志的输出格式
- 通过定义每一条日志信息的级别
- 这些可以通过一个[配置文件](https://baike.baidu.com/item/配置文件/286550)来灵活地进行配置，而不需要修改应用的代码



配置：

一：导入依赖

```xml
<dependency>
    <groupId>log4j</groupId>
    <artifactId>log4j</artifactId>
    <version>1.2.17</version>
</dependency>
```

二：建立log4j.properties

```properties
#将等级为DEBUG的日志信息输出到console和file这两个目的地，console和file的定义在下面的代码
log4j.rootLogger=DEBUG,console,file

#控制台输出的相关设置
log4j.appender.console = org.apache.log4j.ConsoleAppender
log4j.appender.console.Target = System.out
log4j.appender.console.Threshold=DEBUG
log4j.appender.console.layout = org.apache.log4j.PatternLayout
log4j.appender.console.layout.ConversionPattern=[%c]-%m%n

#文件输出的相关设置
log4j.appender.file = org.apache.log4j.RollingFileAppender
log4j.appender.file.File=./log/kuang.log
log4j.appender.file.MaxFileSize=10mb
log4j.appender.file.Threshold=DEBUG
log4j.appender.file.layout=org.apache.log4j.PatternLayout
log4j.appender.file.layout.ConversionPattern=[%p][%d{yy-MM-dd}][%c]%m%n

#日志输出级别
log4j.logger.org.mybatis=DEBUG
log4j.logger.java.sql=DEBUG
log4j.logger.java.sql.Statement=DEBUG
log4j.logger.java.sql.ResultSet=DEBUG
log4j.logger.java.sql.PreparedStatement=DEBUG
```

三：配置log4j日志实现

```xml
<settings>
    <setting name="logImpl" value="LOG4J "/>
</settings>
```



四、log4j的使用

1、导包：

2、日志对象，参数为当前类的class

```java
  static Logger logger = Logger.getLogger(LogDemo.class); //LogDemo为相关的类
```

- 在相应的方法中：

```java
if(logger.isDebugEnabled()){
    logger.debug("*");
}
```





## 7、分页

减少数据的处理量



### 7.1使用Limit分页

```sql
select * from user limit startIndex,pageSize
select * from user limit 3; [0,n-1]
```



使用Mybatis实现分页，核心SQL

1. 接口

   ```java
   //分页
   List<User> getUserByLimit(Map<String,Integer> map);
   ```

2. Mapper.xml

   ```xml
   <select id="getUserByLimit" parameterType="map" resultMap="UserMap">
       select * from user limit #{startIndex},#{pageSize}
   </select>
   ```

3. 测试

   ```java
   @Test
   public void getUserByLimit(){
       SqlSession sqlSession = MyBatisUtils.getSqlSession();
       UserMapper userMapper = sqlSession.getMapper(UserMapper.class);
       HashMap<String, Integer> hashMap = new HashMap<String, Integer>();
       hashMap.put("startIndex",1);
       hashMap.put("pageSize",2);
       List<User> userList = userMapper.getUserByLimit(hashMap);
       for (User user : userList) {
           System.out.println(user);
       }
       sqlSession.close();
   }
   ```





### 7.2、RowBounds分页

不在使用sql实现分页

1. 接口

   ```java
   //分页2
   List<User> getUserByRowBounds();
   ```

2. Mapper.xml

   ```xml
   <select id="getUserByRowBounds" resultMap="UserMap">
       select * from user 
   </select>
   ```

3. 测试

   ```java
   @Test
   public void getUserByRowBounds(){
       SqlSession sqlSession = MyBatisUtils.getSqlSession();
       RowBounds rowBounds = new RowBounds(1,2);
       List<User> userList=sqlSession.selectList("getUserByRowBounds",null,rowBounds);
   
       for (User user : userList) {
           System.out.println(user);
       }
       sqlSession.close();
   }
   ```



### 7.3分页插件

https://pagehelper.github.io/docs/howtouse/



## 8、使用注解开发

### 8.1、面向接口编程

原因：**解耦**

### 8.2、使用注解

1、在接口中实现

```java
public interface UserMapper {
    //查询全部用户
    @Select("select * from user")
    List<User> getUserList();
}
//方法存在多个参数时，所有参数前面必须加上@Param（"id")
@Select("select * from user where id=#{id}")
User getUserById(@Param("id") int id);

@Insert("insert into user(id,name,pwd) values (#{id},#{name},#{password})")
int addUuser(User user);
```

使用注解来映射简单语句会使代码显得更加简洁，然而对于稍微复杂一点的语句，Java 注解就力不从心了，并且会显得更加混乱。 因此，如果你需要完成很复杂的事情，那么最好使用 XML 来映射语句。

2、在核心配置文件中绑定接口

```xml
<mappers>
    <mapper class="com.wxg.dao.UserMapper"/>
</mappers>
```

3、测试



注： 本质是反射机制，底层是动态代理！

**Mybatis执行流程**

<img src="F:\学习笔记\mybatis流程.png" alt="mybatis流程" style="zoom: 80%;" />



**关于@Param（）注解**

- 基本类型的参数或者String类型，需要加上
- 引用类型不需要加
- 若只有一个基本类型则可以忽略
- 在SQL中引用的就是@Param()中设定的属性名



**#{}与${}的区别**

1. #将传入的数据都当成一个字符串，会对自动传入的数据加一个双引号。如：order by #user_id#，如果传入的值是111,那么解析成sql时的值为order by "111", 如果传入的值是id，则解析成的sql为order by "id".
2. $将传入的数据直接显示生成在sql中。如：order by $user_id$，如果传入的值是111,那么解析成sql时的值为order by user_id,  如果传入的值是id，则解析成的sql为order by id.
3. #方式能够很大程度防止sql注入。
4. $方式无法防止Sql注入。
5. $方式一般用于传入数据库对象，例如传入表名.
6. **一般能用#的就别用$.**



**MyBatis排序时使用order by 动态参数时需要注意，用$而不是#**





## 9、lombok

Lombok是一个Java库，能自动插入编辑器并构建工具，简化Java开发。通过添加注解的方式，不需要为类编写getter或eques方法，同时可以自动化日志变量。

**常用注解**

- @Data 注解在类上；提供类所有属性的 getting 和 setting 方法，此外还提供了equals、canEqual、hashCode、toString 方法 
- @Setter ：注解在属性上；为属性提供 setting 方法 
- @Setter ：注解在属性上；为属性提供 getting 方法
-  @Log4j ：注解在类上；为类提供一个 属性名为log 的 log4j 日志对象 
- @NoArgsConstructor ：注解在类上；为类提供一个无参的构造方法 
- @AllArgsConstructor ：注解在类上；为类提供一个全参的构造方法 
- @Cleanup : 可以关闭流 
- @Builder ： 被注解的类加个构造者模式 
- @Synchronized ： 加个同步锁 
- @SneakyThrows : 等同于try/catch 捕获异常 
- @NonNull : 如果给参数加个这个注解 参数为null会抛出空指针异常 
- @Value : 注解和@Data类似，区别在于它会把所有成员变量默认定义为private final修饰，并且不会生成set方法。



## 



## 10、多对一

Student

```java
@Data
public class Student {
    private int id;
    private String name;
    //多个学生对应同一老师
    private Teacher teacher;
}
```

Teacher

```java
@Data
public class Teacher {

    private int id;
    private String name;
}
```

Mapper.xml

```xml
<!--
思路一：
    1.查询所有学生的信息
    2.根据tid查询对应老师的信息 子查询
 -->
<select id="getStudent" resultMap="StudentTeacher">
    select * from student
</select>
<resultMap id="StudentTeacher" type="Student">
    <result column="id" property="id"/>
    <result column="name" property="name"/>
    <!--复杂的属性 需要单独处理    对象：association  集合：collection-->
    <association property="teacher" column="tid" javaType="Teacher" select="getTeacher"/>
</resultMap>
<select id="getTeacher" resultType="Teacher">
    select * from teacher where id=#{id}
</select>


<!--
 思路二：联表查询
-->
<select id="getStudent2" resultMap="StudentTeacher2">
    select s.id sid,s.name sname,t.id tid,t.name tname
    from student s,teacher t
    where s.tid=t.id
</select>
<resultMap id="StudentTeacher2" type="Student">
    <result column="sid" property="id"/>
    <result column="sname" property="name"/>
    <!--复杂的属性 需要单独处理    对象：association  集合：collection-->
    <association property="teacher" javaType="Teacher">
        <id column="tid" property="id"/>
        <result column="tname" property="name"/>
    </association>
</resultMap>
```





## 11、一对多

Student

```java
@Data
public class Student {
    private int id;
    private String name;
    //一个老师对应多个学生
    private int tid;
}
```

Teacher

```java
@Data
public class Teacher {

    private int id;
    private String name;
    private List<Student> students;
}
```

Mapper.xml

```xml
<!--思路一-->
<select id="getTeacher" resultMap="TeacherStudent">
    select s.id sid,s.name sname,t.id tid,t.name tname
    from student s,teacher t
    where t.id=s.tid and t.id=#{tid}
</select>
<resultMap id="TeacherStudent" type="Teacher">
    <id column="tid" property="id"/>
    <result column="tname" property="name"/>
    <!--property对应Teacher类下List集合名称，ofType对应List集合的泛型信息-->
    <collection property="students" ofType="Student">
        <result column="sid" property="id"/>
        <result column="sname" property="name"/>
        <result column="tid" property="tid"/>
    </collection>
</resultMap>

<!--思路二-->
<select id="getTeacher2" resultMap="TeacherStudent2">
    select * from teacher where id=#{tid}
</select>
<resultMap id="TeacherStudent2" type="Teacher">
    <collection property="students" javaType="ArrayList" ofType="Student" column="id" select="getStudentByTid"/>
</resultMap>
<select id="getStudentByTid" resultType="Student">
    select * from student where tid=#{id}
</select>
```



**总结：**

1. 关联 association 多对一

2. 集合collection一对多

3. javaType&ofType

   javaType指定实体类中属性的类型

   ofType指定映射到List集合类型中的pojo类型，泛型中的约束类型





## 12、动态SQL

### **IF**

```xml
<select id="findActiveBlogLike" resultType="Blog">
  SELECT * FROM BLOG WHERE state = ‘ACTIVE’
  <if test="title != null">
    AND title like #{title}
  </if>
  <if test="author != null and author.name != null">
    AND author_name like #{author.name}
  </if>
</select>
```



### choose, when, otherwise

有时我们不想应用到所有的条件语句，而只想从中择其一项。针对这种情况，MyBatis 提供了 choose 元素，它有点像 Java 中的 switch 语句。

```XML
<select id="findActiveBlogLike" resultType="Blog">
  SELECT * FROM BLOG WHERE state = ‘ACTIVE’
  <choose>
    <when test="title != null">
      AND title like #{title}
    </when>
    <when test="author != null and author.name != null">
      AND author_name like #{author.name}
    </when>
    <otherwise>
      AND featured = 1
    </otherwise>
  </choose>
</select>
```



### trim, where, set

where 元素只会在至少有一个子元素的条件返回 SQL 子句的情况下才去插入“WHERE”子句。而且，若语句的开头为“AND”或“OR”，where 元素也会将它们去除。

如果 where 元素没有按正常套路出牌，我们可以通过自定义 trim 元素来定制 where 元素的功能。

```xml
<select id="findActiveBlogLike" resultType="Blog">
  SELECT * FROM BLOG
  <where>
    <if test="state != null">
         state = #{state}
    </if>
    <if test="title != null">
        AND title like #{title}
    </if>
    <if test="author != null and author.name != null">
        AND author_name like #{author.name}
    </if>
  </where>
</select>
```



### sql片段

```xml
 <sql id="sta">
	<if test="state != null">
         state = #{state}
    </if>
    <if test="title != null">
        AND title like #{title}
    </if>
    <if test="author != null and author.name != null">
        AND author_name like #{author.name}
    </if>
  </sql>


  <!--引入sql片段-->
  <where>
    <include refid="sta"></include>
  </where>
```





### foreach

```xml
<select id="listProduct" resultType="Product">
     SELECT * FROM product_ WHERE ID in
  <foreach item="item" index="index" collection="list" open="(" separator="," close=")">
         #{item}
  </foreach>
</select>
```



![image-20200105172116997](F:\学习笔记\image-20200105172116997.png)



