package com.toast.votree.test.service;

import static org.hamcrest.CoreMatchers.everyItem;
import static org.hamcrest.CoreMatchers.instanceOf;
import static org.junit.Assert.assertThat;

import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;

import com.toast.votree.manager.DbShardingManager;
import com.toast.votree.service.VotreeService;
import com.toast.votree.sharding.UserMapper;
import com.toast.votree.sharding.VoteResultMapper;
import com.toast.votree.util.DbgUtil;
import com.toast.votree.vo.User;
import com.toast.votree.vo.Voting;
import com.toast.votree.vo.Voting.VoteInVoting;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {
    "classpath:spring/applicationContext-test.xml",
    "classpath:spring/servlet-context.xml"
})
@Transactional
public class VotreeServiceResultTest {

  @Autowired
  DbShardingManager dbShardingManager;
  
  @Autowired
  VotreeService votreeService;
  
  @Before
  public void setUp() {
  }

  @Test
  public void testResultOfVotreeService() {
    String votreeId = "1457062854241_7f0f6a8ab52644079dc6f1eec71dec1f";
//    String votreeId = "1457536727087_ea246f0ee9804a59b5fc572720ef4c25";
    votreeService.calcVotreeResultForBestChoice(votreeId);
  }
  
  @Test
  public void test_VoteBox데이터가져오기() {
    VoteResultMapper mapper = dbShardingManager.getMapper("1457062854241_7f0f6a8ab52644079dc6f1eec71dec1f", VoteResultMapper.class);
    List<Voting> result = mapper.selectVotingsByVotreeId("1457062854241_7f0f6a8ab52644079dc6f1eec71dec1f");
    
    UserMapper userMapper = dbShardingManager.getMapperInCommonDb(UserMapper.class);
    User user = userMapper.selectUserById(result.get(0).getUserId());
    DbgUtil.logger().debug("user:" +  DbgUtil.dump(user));
    
    DbgUtil.logger().debug("size:" + result.size());
    VoteInVoting vote = result.get(0).getVotes().get(0);
    DbgUtil.logger().info(":" + DbgUtil.dump(vote));
//    UserChoice userChoice = result.get(0).getVotes().get(0).getUserChoices().get(0);
//    DbgUtil.logger().info(":" + DbgUtil.dump(userChoice));
    
    assertThat(result, instanceOf(List.class));
    assertThat(result, everyItem(instanceOf(Voting.class)));
    result.forEach(
        voting -> {
          assertThat(voting.getVotes(), everyItem(instanceOf(Voting.VoteInVoting.class)));
        });
  }
}
