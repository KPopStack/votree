package com.toast.votree.service;

import java.sql.Timestamp;
import java.text.ParseException;
import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CopyOnWriteArrayList;

import javax.json.Json;
import javax.json.JsonBuilderFactory;
import javax.json.JsonObject;
import javax.xml.bind.DatatypeConverter;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import com.toast.votree.config.ToastCloud;
import com.toast.votree.exception.UnsupportedCategoryException;
import com.toast.votree.exception.VotreeIsCompleteException;
import com.toast.votree.manager.DbShardingManager;
import com.toast.votree.sharding.UserMapper;
import com.toast.votree.sharding.VoteItemMapper;
import com.toast.votree.sharding.VoteMapper;
import com.toast.votree.sharding.VoteResultMapper;
import com.toast.votree.sharding.VotingMapper;
import com.toast.votree.sharding.VotreeMapper;
import com.toast.votree.util.DbgUtil;
import com.toast.votree.util.JsonUtil;
import com.toast.votree.util.MapUtils;
import com.toast.votree.util.RestResponse;
import com.toast.votree.util.VaildAccessTokenUtil;
import com.toast.votree.vo.User;
import com.toast.votree.vo.Vote;
import com.toast.votree.vo.VoteBox;
import com.toast.votree.vo.VoteItem;
import com.toast.votree.vo.Voting;
import com.toast.votree.vo.Voting.VoteInVoting;
import com.toast.votree.vo.Votree;
import com.toast.votree.vo.VotreeTopResult;
import com.toast.votree.vo.VotreeTotalResult;

@Service("votreeService")
public class VotreeServiceImpl implements VotreeService {
  
  @Autowired
  ToastCloud toastCloud;
  @Autowired
  RestTemplate restTemplate;
  @Autowired
  DbShardingManager dbShardingManager;
  @Autowired
  DatabaseMapperRedisService databaseMapperRedisService;
  @Autowired
  ToastSearchVotree toastSearchVotree;
  @Autowired
  HitCountWriteBackService hitCountWriteBackService;
  @Override
  public List<Votree> searchVotrees(String votreeName, Integer offset) {
    List<String> votreeIds = toastSearchVotree.getIdsByInput(votreeName);
    List<Votree> votrees = new ArrayList<Votree>();
    VotreeMapper votreeMapper;
    for(String votreeId : votreeIds) {
      int dbIndex = (int)databaseMapperRedisService.findDataByKeyInHashKey(votreeId, "dbIndex");
      votreeMapper = dbShardingManager.getMappers(VotreeMapper.class).get(dbIndex);
      Votree votree = votreeMapper.selectVotreeByVotreeId(votreeId);
      if(votree != null) {
        votrees.add(votree);
      }
    }
    UserMapper userMapper = dbShardingManager.getMapperInCommonDb(UserMapper.class);
    votrees.forEach(votree -> {
      if(userMapper.selectUserById(votree.getProposerId()) != null) {
        votree.setProposerName(userMapper.selectUserById(votree.getProposerId()).getName());
      }
    });
    Collections.sort(votrees, new DateTimeComparatorAsc());
    return votrees;
  }

  @Override
  public Map<String, Object> getVotreeWithUserChoice(String votreeId, int userId) throws VotreeIsCompleteException{
    Map<String, Object> map = new HashMap<>();
    map.put("votreeId", votreeId);
    map.put("userId", userId);
    VotreeMapper votreeMapper = dbShardingManager.getMapper(votreeId, VotreeMapper.class);
    Votree selectedVotree = votreeMapper.selectVotreeByVotreeId(votreeId);
    if(isCompletedVotree(selectedVotree)){
      Map<String, Object> errorMap = new HashMap<String, Object>();
      errorMap.put("error", "완료된 Votree의 투표화면에는 접근할 수 없습니다.");
      return errorMap;
    }
    return votreeMapper.selectVotreeWithUserChoiceByVotreeIdAndUserId(map);
  }

  @Override
  public Votree showDetailVotree(String votreeId, int userId, String from) {
    Map<String, Object> map = new HashMap<>();
    map.put("votree_id", votreeId);
    map.put("user_id", userId);
    VotreeMapper votreeMapper = dbShardingManager.getMapper(votreeId, VotreeMapper.class);
//    Votree selectedVotree = votreeMapper.selectVotreeByVotreeIdAndUserId(map);
    if(!from.equals("RESULT")) {
//      DbgUtil.logger().debug("showDetailVotree:" + votreeId + ", " + selectedVotree.getId());
      hitCountWriteBackService.hitCountWriteBack(votreeId);
    }
    return votreeMapper.selectVotreeByVotreeIdAndUserId(map);
  }
  @Override
  public RestResponse modifyVotree(String votreeId, int userId, String body) {
    RestResponse response;
    try{
      JSONObject jsonVotree = new JSONObject(body);
      JSONArray jsonVotes = jsonVotree.getJSONArray("votes");
      Votree addedVotree = extractVotreeByJson(jsonVotree, votreeId); 
      List <Vote> addedVotes = extractVotesByJson(jsonVotes);
      VotreeMapper votreeMapper = dbShardingManager.getMapper(addedVotree.getId(), VotreeMapper.class);
      VoteMapper voteMapper = dbShardingManager.getMapper(addedVotree.getId(), VoteMapper.class);
      UserMapper userMapper = dbShardingManager.getMapperInCommonDb(UserMapper.class);
      addedVotree.setProposerName(userMapper.selectUserById(userId).getName());
      votreeMapper.updateVotree(addedVotree);
      
      for(Vote vote : addedVotes){
        vote.setVotreeId(addedVotree.getId());
        voteMapper.updateVote(vote);
      }
      DbgUtil.logger().debug("ADDED VOTREE : " + addedVotree);
      toastSearchVotree.indexingToToastSearch(addedVotree);//Votree Indexing Update
      response = new RestResponse.Builder("투표 수정이 성공하였습니다.").build();
    } catch (Exception e){
      DbgUtil.logger().error(e.getMessage());
      response = new RestResponse.Builder(false, 400, "투표 수정에 문제가 발생했습니다.").build();
    }
    return response;
  }

  @Override
  public Map<String, List<Votree>> showVotrees(int runningPageNum, int expiredPageNum
      , int votesType, int contentsPerPage, final String CALL_CLASSIFICATION, int userId) {
    Map<String, List<Votree>> totalVotree = new HashMap<>();
    
    List<Votree> runningVotrees = new ArrayList<>(); 
    List<Votree> expiredVotrees = new ArrayList<>();
    List<Votree> joinedVotrees = new ArrayList<>();
    
    VotreeMapper votreeMapper1 = dbShardingManager.getMappers(VotreeMapper.class).get(0);
    VotreeMapper votreeMapper2 = dbShardingManager.getMappers(VotreeMapper.class).get(1);
    
    /* 메인화면 콜 */
    if(CALL_CLASSIFICATION.equals("mainCall")) {
      for (VotreeMapper mapper : dbShardingManager.getMappers(VotreeMapper.class)) {
        runningVotrees.addAll( mapper.selectVotreesOnRunningState(0) );
      }
      
      expiredVotrees = votreeMapper1.selectVotreesOnExpiredState(0);
      expiredVotrees.addAll(votreeMapper2.selectVotreesOnExpiredState(0));
    }
    /* 프로필화면 콜 */
    else if(CALL_CLASSIFICATION.equals("profileCall")){
      for (VotreeMapper mapper : dbShardingManager.getMappers(VotreeMapper.class)) {
        runningVotrees.addAll( mapper.selectVotreesOnRunningStateByUserId(userId) );
      }
      
      expiredVotrees = votreeMapper1.selectVotreesOnExpiredStateByUserId(userId); 
      expiredVotrees.addAll(votreeMapper2.selectVotreesOnExpiredStateByUserId(userId));
    
      joinedVotrees = votreeMapper1.selectJoinedVotreesByUserId(userId);
      joinedVotrees.addAll(votreeMapper2.selectJoinedVotreesByUserId(userId));
      
      setVotreeProposer(runningVotrees);
      setVotreeProposer(expiredVotrees);
      setVotreeProposer(joinedVotrees);
      
      totalVotree.put("runningVotrees", runningVotrees);
      totalVotree.put("expiredVotrees", expiredVotrees);
      totalVotree.put("joinedVotrees", joinedVotrees);
      
      return totalVotree;
    } else {
      throw new IllegalArgumentException("wrong CALL_CLASSIFICATION field value");
    }
    Collections.sort(runningVotrees, new DateTimeComparatorAsc());
    if(runningVotrees.size() > runningPageNum*contentsPerPage + 12) {
      runningVotrees = runningVotrees.subList(runningPageNum*contentsPerPage, runningPageNum*contentsPerPage + 12);
    } else {
      runningVotrees = runningVotrees.subList(runningPageNum*contentsPerPage, runningVotrees.size());
    }
    Collections.sort(expiredVotrees, new DateTimeComparatorDesc());
    if(expiredVotrees.size() > expiredPageNum*contentsPerPage + 12) {
      expiredVotrees = expiredVotrees.subList(expiredPageNum*contentsPerPage, expiredPageNum*contentsPerPage + 12);
    } else {
      expiredVotrees = expiredVotrees.subList(expiredPageNum*contentsPerPage, expiredVotrees.size());
    }

    setVotreeProposer(runningVotrees);
    setVotreeProposer(expiredVotrees);
    
    totalVotree.put("runningVotrees", runningVotrees);
    totalVotree.put("expiredVotrees", expiredVotrees);
    
    return totalVotree;
  }

  static class DateTimeComparatorAsc implements Comparator<Votree> {
    @Override
    public int compare(Votree arg0, Votree arg1) {
      return arg0.getDueDatetime().compareTo(arg1.getDueDatetime());
    }
  }
  static class DateTimeComparatorDesc implements Comparator<Votree> {
    @Override
    public int compare(Votree arg0, Votree arg1) {
      return arg1.getDueDatetime().compareTo(arg0.getDueDatetime());
    }
  }
  
  private void setVotreeProposer(List<Votree> votrees) {
    UserMapper userMapper = dbShardingManager.getMapperInCommonDb(UserMapper.class);
    votrees.forEach(votree -> {
      try {
        String proposerName = userMapper.selectUserById(votree.getProposerId()).getName();
        votree.setProposerName(proposerName);
      } catch (Exception e) {
        DbgUtil.logger().debug(e.getMessage());
      }
    });
  }
  @Override
  @Transactional
  public RestResponse createVotree(Votree votree, int userId) {
    RestResponse response;
    try {
      if (votree == null) {
        throw new NullPointerException("votree is null");
      }
      DbgUtil.logger().debug(JsonUtil.toJsonPretty(votree));
      votree.setId(dbShardingManager.generateShardingKey());
      votree.setTurnout(0);
      votree.setProposerId(userId);
      List<Vote> votes = votree.getVotes();
      List <VoteItem> voteItems = new ArrayList<>(); 

      dbShardingManager.mappingToDbByShardingKey(votree.getId());
      
      VotreeMapper votreeMapper = dbShardingManager.getMapper(votree.getId(), VotreeMapper.class);
      VoteMapper voteMapper = dbShardingManager.getMapper(votree.getId(), VoteMapper.class);
      VoteItemMapper voteItemMapper = dbShardingManager.getMapper(votree.getId(), VoteItemMapper.class);
      UserMapper userMapper = dbShardingManager.getMapperInCommonDb(UserMapper.class);
      votree.setProposerName(userMapper.selectUserById(votree.getProposerId()).getName());
      
      votreeMapper.insertVotree(votree);
//      int currentVoteItemIndex = 0;
      
      for(Vote vote : votes){
        vote.setVotreeId(votree.getId());
        vote.setVoteItemNum(vote.getVoteItems().size());
        voteMapper.insertVote(vote);
        for(VoteItem voteItem : vote.getVoteItems()){
          voteItem.setVoteId(vote.getId());
        }
        voteItems.addAll(vote.getVoteItems());
        encodeBase64AndUploadImage(vote.getVoteItems());
//        for( int i = currentVoteItemIndex ; i < (currentVoteItemIndex  + vote.getVoteItemNum()) ; i++ ){
//          voteItems.get(i).setVoteId(vote.getId());
//        }
//        currentVoteItemIndex += vote.getVoteItemNum();
      }
      voteItemMapper.insertVoteItem(voteItems);
      response = new RestResponse.Builder("투표 생성이 성공하였습니다.").body(votree.getId()).build();
    } catch (JSONException e) {
      DbgUtil.logger().error(e.getMessage());
      response = new RestResponse.Builder(false, 400, "투표 생성이 문제가 발생했습니다. - JSon 에러").build();
    } catch (NullPointerException e) {
      DbgUtil.logger().error(e.getMessage());
      response = new RestResponse.Builder(false, 400, "투표 생성이 문제가 발생했습니다. - "+e.getMessage()).build();
    } catch (UnsupportedCategoryException e) {
      DbgUtil.logger().error(e.getMessage());
      response = new RestResponse.Builder(false, 400, "투표 생성이 문제가 발생했습니다. - 지원하지않는 카테고리:"+e.getMessage()).build();
    } catch (IllegalArgumentException e) {
      DbgUtil.logger().error(e.getMessage());
      response = new RestResponse.Builder(false, 400, "투표 생성이 문제가 발생했습니다. - 잘못된 base64값").build();
    } catch (RuntimeException e) {
      DbgUtil.logger().error(e.getMessage(), e);
      response = new RestResponse.Builder(false, 400, "투표 생성이 문제가 발생했습니다. - "+e.getMessage()).build();
    }
    
    toastSearchVotree.indexingToToastSearch(votree);
    
    return response;
  }

  private List<Vote> extractVotesByJson(JSONArray jsonArray) {
    List<Vote> addedVotes = new ArrayList<>();
    for (int i = 0; i < jsonArray.length(); i++){
      Vote vote = new Vote();
      vote.setTopic(jsonArray.getJSONObject(i).getString("voteName"));
      vote.setId((jsonArray.getJSONObject(i).getInt("id")));
      vote.setPreviewYN("N");
      vote.setVoteItemNum(jsonArray.getJSONObject(i).getJSONArray("itemList").length());
      vote.setDuplicateYN(jsonArray.getJSONObject(i).getString("isDuplicate"));
      addedVotes.add(vote);
    }
    return addedVotes;
  }

  private void encodeBase64AndUploadImage(List <VoteItem> voteItems){
    for(int j = 0; j <voteItems.size(); j++){
      if(voteItems.get(j).getCategoryId() == 2){
        String randString = makeRandomString();
        String base64String = voteItems.get(j).getValue().split(",")[1];
        byte[] imageBytes = DatatypeConverter.parseBase64Binary(base64String);
        upload(imageBytes, randString);
        voteItems.get(j).setValue(randString+".png");
      }
    }
  }

  private String makeRandomString() {
    return UUID.randomUUID().toString().replaceAll("-", "");
  }
  
  @SuppressWarnings("unchecked")
  private void upload(byte[] img, String filename){
    boolean isUseAccessToken = false;
    UriComponentsBuilder uriBuilder =
        UriComponentsBuilder.fromHttpUrl(toastCloud.getRequestUrlForToken());
    
    Timestamp currentTimeStamp = VaildAccessTokenUtil.getCurrentTimeStamp();
    
    if(toastCloud.getExpireTime() != null) {
      Timestamp expireTimeStamp = Timestamp.from(toastCloud.getExpireTime().toInstant());
      isUseAccessToken = (expireTimeStamp.getTime() < currentTimeStamp.getTime()) ? false : true;
    }

    
    if(toastCloud.getExpireTime() == null || !isUseAccessToken) {
      /* accessToken 받아오기 */
      JsonBuilderFactory factory = Json.createBuilderFactory(null);
      JsonObject value = factory.createObjectBuilder()
          .add("auth", Json.createObjectBuilder()
              .add("tenantName", toastCloud.getTenantName())
              .add("passwordCredentials", Json.createObjectBuilder()
                  .add("username", toastCloud.getUserName())
                  .add("password", toastCloud.getPlainPassword())))
          .build();
      HttpHeaders headers = new HttpHeaders();
      headers.setContentType(MediaType.APPLICATION_JSON);
      HttpEntity<String> request= new HttpEntity<>(value.toString(), headers);
      Map<String,Object> response = restTemplate.postForObject(
          uriBuilder.build().toUri()
          , request
          , Map.class);
      Object objAccessParamValue = response.get("access");
      if (objAccessParamValue == null) {
        throw new NullPointerException("AccessToken Param value is null!");
      }
      String accessToken = String.valueOf(objAccessParamValue);
      String expireTime = accessToken.split("=")[2].split(",")[0];
      accessToken = accessToken.split("=")[3].split(",")[0];
      
      ZonedDateTime zoneDateTime = ZonedDateTime.parse(expireTime);
      
      toastCloud.setAccessToken(accessToken);
      toastCloud.setExpireTime(zoneDateTime);
    }
    //TODO REVIEW 공통부분 재활용
    /* 이미지파일 저장하기 */
    final String REQUEST_URL_TO_UPLOAD_OBJECT = 
        toastCloud.getRequestUrlToUploadObject()
        + toastCloud.getAccount()
        + toastCloud.getPathForImages() 
        + filename +".png";
    uriBuilder = UriComponentsBuilder.fromHttpUrl(REQUEST_URL_TO_UPLOAD_OBJECT);
    HttpHeaders headersForObject = new HttpHeaders();
    headersForObject.setContentType(MediaType.IMAGE_PNG);
    headersForObject.add("X-AUTH-TOKEN", toastCloud.getAccessToken());
    HttpEntity<byte[]> requestForObject= new HttpEntity<>(img, headersForObject);
    restTemplate.put(uriBuilder.build().toUri(), requestForObject);
  }

  /* 투표 추가,삭제할때 사용 */
  private Votree extractVotreeByJson(JSONObject jsonVotree, Object id) throws JSONException, ParseException {
    Votree addedVotree = new Votree();
    LocalDateTime startDateTime = LocalDateTime.parse(jsonVotree.getString("startDatetime"), DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"));
    LocalDateTime dueDateTime = LocalDateTime.parse(jsonVotree.getString("dueDatetime"), DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"));
    addedVotree.setId((String)id);
    addedVotree.setStartDatetime(Timestamp.valueOf(startDateTime));
    addedVotree.setDueDatetime(Timestamp.valueOf(dueDateTime));
    addedVotree.setTitle(jsonVotree.getString("votreeTitle"));
    if(jsonVotree.getBoolean("isPrivate")){
      addedVotree.setType(2); 
      addedVotree.setPlainPassword(jsonVotree.getString("plainPassword"));
    }
    else{
      addedVotree.setType(1); 
    }
    return addedVotree;
  }
  
  /*
   * 투표하기
   */
  @Override
  public RestResponse addUserChoice(Voting voting) {
    RestResponse response = null;
    try {
      VotingMapper votingMapper = dbShardingManager.getMapper(voting.getVotreeId(), VotingMapper.class);
      int insertedRowCount = votingMapper.insertToVoteBox(voting);
      votingMapper.incrementTurnOut(voting.getVotreeId());
      if (insertedRowCount == 0) {
        throw new RuntimeException("DB반영된 Row count is zero");
      }
      response = new RestResponse.Builder("투표 생성 " + insertedRowCount + "개 성공").build();
    } catch (NullPointerException e) {
      DbgUtil.logger().debug(e.getMessage());
      response = new RestResponse.Builder(false, 400, "NullPointerException").build();
    } catch (RuntimeException e) {
      if(isRevoteByBackBtn(e.getMessage())){
        response = new RestResponse.Builder(false, 400, "이미 투표하였습니다.").build();
      }
      else{
        DbgUtil.logger().debug(e.getMessage());
        response = new RestResponse.Builder(false, 400, e.getLocalizedMessage()).build();
      }
    } 

    return response;
  }

  @Override
  public RestResponse updateUserChoice(Voting voting) { 
    RestResponse response = null;
    try {
      VotingMapper votingMapper = dbShardingManager.getMapper(voting.getVotreeId(), VotingMapper.class);
      votingMapper.deleteByVoteId(voting);
      int insertedRowCount = votingMapper.insertToVoteBox(voting);
      
      if (insertedRowCount == 0) {
        throw new RuntimeException("DB반영된 Row count is zero");
      }
      response = new RestResponse.Builder("재투표 생성 " + insertedRowCount + "개 성공").build();
    } catch (NullPointerException e) {
      response = new RestResponse.Builder(false, 400, "NullPointerException").build();
    } catch (RuntimeException e) {
      response = new RestResponse.Builder(false, 400, e.getLocalizedMessage()).build();
    }
    
    return response;
  }
  
  
  private boolean isCompletedVotree(Votree votree) {
    return votree.getDueDatetime().before(new Date());
  }
  
  private boolean isRevoteByBackBtn(String message) {
    return message.equals("java.lang.reflect.InvocationTargetException-->null");
  }
  /*
   * 단일투표
   */
//  @Override
//  public int addVote(Vote vote) {
//    return voteDao.insertVote(vote);
//  }
  @Override
  public List<Vote> findVotesByVotreeId(String votreeId) {
//    int dbIndex = (int) databaseMapperRedisService.findDataByKeyInHashKey(votreeId, "dbIndex");
    VoteMapper voteMapper = dbShardingManager.getMapper(votreeId, VoteMapper.class);
    return voteMapper.selectVotesByVotreeID(votreeId);
  }

//  @Override
//  public Vote findVoteByVoteId(int voteId) {
//    return voteDao.selectVoteByVoteId(voteId);
//  }

  @Override
  public List<VotreeTopResult> findTopResultsByVotreeId(String votreeId) {
//    int dbIndex = (int) databaseMapperRedisService.findDataByKeyInHashKey(votreeId, "dbIndex");
    VoteMapper voteMapper = dbShardingManager.getMapper(votreeId, VoteMapper.class);
    return voteMapper.selectTopResultsByVotreeId(votreeId);
  }
  @Override
  public List<VotreeTotalResult> findTotalResultsByVotreeId(String votreeId) {
//    int dbIndex = (int) databaseMapperRedisService.findDataByKeyInHashKey(votreeId, "dbIndex");
    VoteMapper voteMapper = dbShardingManager.getMapper(votreeId, VoteMapper.class);
    return voteMapper.selectTotalResultsByVotreeId(votreeId);
  }
  
  /*
   * 투표항목
   */
//  @Override
//  public int addVoteItem(String value, int voteId, int category) {
//    return voteItemDao.insertVoteItem(value, voteId, category);
//  }

//  @Override
//  public List<VoteItem> findVoteItemsByVoteId(int voteId) {
//    return voteItemDao.selectVoteItemsByVoteId(voteId);
//  }
  
  /*
   * 투표 결과
   */
  @Override
  public Map<String, Object> findVoteResultForChartByVoteId(String votreeId, int voteId) {
//    int dbIndex = (int) databaseMapperRedisService.findDataByKeyInHashKey(votreeId, "dbIndex");
    VoteResultMapper voteResultMapper = dbShardingManager.getMapper(votreeId, VoteResultMapper.class);
    return voteResultMapper.selectVoteResultByVoteId(voteId);
  }

  @Override
  public List<Map<String, Object>> calcVotreeResultForBestChoice(String votreeId) {
    VoteResultMapper resultMapper = dbShardingManager.getMapper(votreeId, VoteResultMapper.class);
    VoteItemMapper voteItemMapper = dbShardingManager.getMapper(votreeId, VoteItemMapper.class);
    UserMapper userMapper = dbShardingManager.getMapperInCommonDb(UserMapper.class);
    List<Voting> voteBox = resultMapper.selectVotingsByVotreeId(votreeId);
    
    
    Map<ResultBranchKey, ResultBranchValue> map = new HashMap<>();
    
    voteBox.forEach(
        voting -> {
          try {
            addUserChoiceBranch(map, voting);
          } catch (Exception e) {
            DbgUtil.logger().error(e.getMessage(), e);
          }
        });
    
    Map<ResultBranchKey, ResultBranchValue> sortedMap = MapUtils.sortByValue(map);
    
    return makeUpResultMap(sortedMap, voteItemMapper, userMapper);
  }
  
  private List<Map<String, Object>> makeUpResultMap(Map<ResultBranchKey, ResultBranchValue> sortedMap, VoteItemMapper itemMapper, UserMapper userMapper) {
    List<Map<String, Object>> resultSet = new ArrayList<>();
    
    sortedMap.entrySet().forEach(entry -> {
//      DbgUtil.logger().debug("key:" + entry.getKey() + ", val:" + entry.getValue());
      Map<String, Object> wrap = new HashMap<>();
      Map<String, Object> branch = new HashMap<>();
      List<Map<String, Object>> items = new ArrayList<>();
      for (Integer voteItemId : entry.getKey().getBranch()) {
        items.add(itemMapper.selectUserChoiceVoteByVoteItemId(voteItemId));        
      }
      branch.put("vote", items);
      
      List<Map<String, Object>> users = new ArrayList<>();
      for (Integer userId : entry.getValue().getUsers()) {
//        DbgUtil.logger().debug("userId:" +userId + ", val:" + DbgUtil.dump(entry.getValue().getUsers()));
        User user = userMapper.selectUserById(userId);
        Map<String, Object> userMap = new HashMap<>();
        userMap.put("name", user.getName());
        userMap.put("email", user.getEmail());
        userMap.put("profileUrl", user.getProfileUrl());
        users.add(userMap);
      }
      branch.put("user", users);
      
      wrap.put("branch", branch);
      resultSet.add(wrap);
    });
    
    
    return resultSet;
  }
  
  private void addUserChoiceBranch(Map<ResultBranchKey, ResultBranchValue> map, Voting voting) throws CloneNotSupportedException {
//    ResultBranchKey key = new ResultBranchKey();
//    ResultBranchValue Value = new ResultBranchValue();
    List<VoteInVoting> votes = voting.getVotes();
//    DbgUtil.logger().info(DbgUtil.dumpShortStyle(votes));
//    int numOfCases = 1;
//    for (VoteInVoting vote : votes) {
//      numOfCases *= vote.getUserChoices().size();
//    }
    
    List<ResultBranchKey> tree = new CopyOnWriteArrayList<>();
    
    List<Integer> userChoices = votes.get(0).getUserChoices();
    userChoices.forEach(userChoice -> {
      tree.add(new ResultBranchKey().addToBranch(userChoice));
    });
    
    for (int i = 1; i < votes.size(); i++) {
      userChoices = votes.get(i).getUserChoices();
//      DbgUtil.logger().info(DbgUtil.dumpShortStyle(userChoices));
      expandBranch(tree, userChoices);
    }
    
    putAndCountBranch(map, tree, voting.getUserId());
    
//    DbgUtil.logger().info(tree.toString());
//    DbgUtil.logger().info("----------------------------");
  }
  
  private void putAndCountBranch(Map<ResultBranchKey, ResultBranchValue> map, List<ResultBranchKey> tree, int userId) {
    tree.forEach(key -> {
      ResultBranchValue value = map.get(key);
      if (value == null) {
        map.put(key, new ResultBranchValue().addUserId(userId));
      } else {
        value.addUserId(userId);
      }
    });
    
  }
  
  private void expandBranch(List<ResultBranchKey> tree, List<Integer> userChoices) throws CloneNotSupportedException {
    for(ResultBranchKey branch : tree) {
      // 유저선택이 2개이상일경우는 2번째 가지수부터는 복제를 통해 만들어서 넣어야함
      for(int i = 1; i < userChoices.size(); i++) {
        addBranchByCloning(tree, branch, userChoices.get(i));
      }
      
      //첫번째 유저 선택은 기존 브랜치를 그대로 확장하면 됨.(유저 선택이 하나라면 위의 For loop는 수행되지 않고 복제 없이 기존 브랜치만 확장)
      branch.addToBranch(userChoices.get(0));
    }
  }
  
  private void addBranchByCloning(List<ResultBranchKey> tree, ResultBranchKey origin, int voteItemId) throws CloneNotSupportedException {
    ResultBranchKey originKey = (ResultBranchKey) origin.clone();
    originKey.addToBranch(voteItemId);
    tree.add(originKey);
  }

  public List<Integer> findVoteBoxesByVotreeId(String votreeId, int loginUserId) {
    VoteResultMapper voteResultMapper = dbShardingManager.getMapper(votreeId, VoteResultMapper.class);
    List<VoteBox> voteBoxes = voteResultMapper.selectVoteBoxesByVotreeId(votreeId);
    
    List<VoteBox> myVoteBoxes = new ArrayList<>();
    List<VoteBox> othersVoteBoxes = new ArrayList<>();
    List<Integer> userIds = new ArrayList<>();
    Map<Integer, Integer> matchingMap = new HashMap<>();
    /*
     * 본인의 vote box와 타인의 vote box를 나눕니다.
     */
    for(VoteBox voteBox : voteBoxes) {
      if(voteBox.getUserId() == loginUserId) {
        myVoteBoxes.add(voteBox);
      }
      else {
        othersVoteBoxes.add(voteBox);
        if(matchingMap.get(voteBox.getUserId()) == null) {
          matchingMap.put(voteBox.getUserId(), 0);//map에 타인의 user id를 키로 저장합니다.(중복 제거)
        }
      }
    }
    
    int myVoteBoxesSize = myVoteBoxes.size();
    
    /*
     * 본인과 일치하는 타인의 vote box를 발견하면 해당 타인의 user id키에 매칭된 수를 저장합니다.
     * 매칭된 수가 본인의 vote box 수와 일치하면 동일한 투표를 한 사람입니다. 
     */
    for(VoteBox otherVoteBox : othersVoteBoxes) {
      for(VoteBox myVoteBox : myVoteBoxes) {
        if(hasSameVoteBoxContents(otherVoteBox, myVoteBox)) {
          int matchingCount = matchingMap.get(otherVoteBox.getUserId()) + 1;
          matchingMap.replace(otherVoteBox.getUserId(), matchingCount);
          if(matchingCount == myVoteBoxesSize) {
            userIds.add(otherVoteBox.getUserId());
          }
        }
      }  
    }
    return userIds;
  }

  private boolean hasSameVoteBoxContents(VoteBox subject, VoteBox comparator) {
    if(subject.getVoteId() == comparator.getVoteId()
        && subject.getVoteItemId() == comparator.getVoteItemId()) {
      return true;
    }
    else {
      return false; 
    }
  }
//  @Override
//  public Map<String, Object> findVoteResultAllInVotree(String votreeId) {
//    return resultDao.selectVoteResultsInVotree(votreeId);
//  }
  
  
//  private synchronized int getShardIndex(){
//    shardIndex += 1;
//    if(shardIndex>2){
//      shardIndex = 1;
//    }
//    return shardIndex%2;
//  }
}
