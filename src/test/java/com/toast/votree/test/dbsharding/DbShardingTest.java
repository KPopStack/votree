package com.toast.votree.test.dbsharding;

import java.sql.SQLException;
import java.util.List;

import org.apache.commons.dbcp2.BasicDataSource;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mybatis.spring.MyBatisSystemException;
import org.mybatis.spring.SqlSessionTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.toast.votree.manager.DbShardingManager;
import com.toast.votree.sharding.ProxyMapperFactory;
import com.toast.votree.sharding.UserMapper;
import com.toast.votree.sharding.VoteMapper;
import com.toast.votree.sharding.VotreeMapper;
import com.toast.votree.util.DbgUtil;
import com.toast.votree.vo.User;
import com.toast.votree.vo.Vote;
import com.toast.votree.vo.Votree;

import junit.framework.Assert;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {
    "classpath:spring/applicationContext-test.xml",
    "classpath:spring/servlet-context.xml"
})
//@Transactional
public class DbShardingTest {

  @Autowired
  DbShardingManager dbShardingManager;
  
  @Autowired
  BasicDataSource dataSource1;
  
  @Autowired
  BasicDataSource dataSource2;
  
  @Autowired
  SqlSessionTemplate sqlSessionTemplate1;
  
  @Autowired
  SqlSessionTemplate sqlSessionTemplate2;
  
  @Test()
  @Ignore
  public void db_isValidTest() throws SQLException {
    
    VotreeMapper mapper = sqlSessionTemplate2.getMapper(VotreeMapper.class);
    DbgUtil.logger().debug("db[1]:" + mapper.selectVotreesOnRunningState(0));
    
    DbgUtil.logger().debug("true:"+dbShardingManager.isAvailableDbLocation(0));
  }
  
  @Test()
  @Ignore
  public void mapper_return_resultSet_zero() {
    //given: dbIndex-0 Db is not valid!! 
    VotreeMapper mapper = dbShardingManager.getMappers(VotreeMapper.class).get(0);
    VotreeMapper proxyMapper = (VotreeMapper) ProxyMapperFactory.newInstance(mapper);
    
    List<Votree> runningVotreesByProxy = proxyMapper.selectVotreesOnRunningState(0);
    
    Assert.assertNotNull(runningVotreesByProxy);
    Assert.assertEquals(0, runningVotreesByProxy.size());
  }
  
  @Test(expected=MyBatisSystemException.class)
  @Ignore
  public void mapper_Throw_Exception() {
    VotreeMapper mapper = dbShardingManager.getMappers(VotreeMapper.class).get(0);
    List<Votree> runningVotrees = mapper.selectVotreesOnRunningState(0);
  }
  
  @Test
  @Ignore
  public void userMapper_Test() {
    UserMapper userMapper = dbShardingManager.getMapperInCommonDb(UserMapper.class);
    User newUser = new User().setEmail("test1@test.com").setName("haha").setOauthProvider("testProvider").setProviderKey("testproviderKey");

    userMapper.insertUser(newUser);

    User selectedUser = userMapper.selectUserById(newUser.getId());

  }
  
  @Test
  @Ignore
  public void dbShardingIndex_Test() {
    for (int i = 0; i < 10; i++) {
      String shardingKey = dbShardingManager.generateShardingKey();
      int dbIndex1 = dbShardingManager.mappingToDbByShardingKey(shardingKey);
      int dbIndex2 = dbShardingManager.findDbIndexByShardingKey(shardingKey);
    }
  }
  
  @Test
  @Ignore
  public void voteMapper_Test() {
    String shardingKey = dbShardingManager.generateShardingKey();
    VoteMapper voteMapper = dbShardingManager.getMapper(shardingKey, VoteMapper.class);

    Vote newVote = new Vote().setDuplicateYN("N").setPreviewYN("N").setTopic("TopicTest").setVotreeId("asdfasdfasdf").setWeight(1);


    voteMapper.insertVote(newVote);

    Vote selectedVote = voteMapper.selectVoteByVoteId(newVote.getId());


  }

//  @Test
//  public void DbSharding_Test3(){
//    List<Votree> votreesDb1 = new ArrayList<Votree>();
//    List<Votree> votreesDb2 = new ArrayList<Votree>();
//    Map<String, Object> map = new HashMap<String, Object>();
//    map.put("keyword", "ASDF");
//    map.put("offset", 0);
//    VotreeMapper votreeMapper1 = dbShardingManager.getMappers(VotreeMapper.class).get(0);
//    VotreeMapper votreeMapper2 = dbShardingManager.getMappers(VotreeMapper.class).get(1);
//    votreesDb1 = votreeMapper1.selectVotreesByKeyword(map);
//    votreesDb2 = votreeMapper2.selectVotreesByKeyword(map);
//    votreesDb1.addAll(votreesDb2); //TODO 검색 결과도 정렬이 필요
//  }
  
}
