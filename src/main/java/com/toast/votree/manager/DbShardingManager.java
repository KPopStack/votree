package com.toast.votree.manager;

import java.util.ArrayList;
import java.util.List;

import org.mybatis.spring.MyBatisSystemException;
import org.mybatis.spring.SqlSessionTemplate;
import org.springframework.jdbc.CannotGetJdbcConnectionException;

import com.toast.votree.exception.DbIndexOutOfBoundsException;
import com.toast.votree.sharding.DbShardingMapperAccessor;
import com.toast.votree.sharding.DbShardingStrategy;
import com.toast.votree.sharding.ProxyMapperFactory;
import com.toast.votree.util.DbgUtil;

public class DbShardingManager implements DbShardingMapperAccessor {

  private List<SqlSessionTemplate> sqlSessionTemplates;
  private SqlSessionTemplate commonSqlSessionTemplate;
  private DbShardingStrategy dbShardingStrategy;

  private static final int DB_INDEX_NOT_EXIST = -1;
  
  @SuppressWarnings("unchecked")
  @Override
  public <T> T getMapper(String shardingKey, Class<T> mapperType) {
    int dbIndex = dbShardingStrategy.findDbIndexByShardingKey(shardingKey);
    try{
      SqlSessionTemplate sqlSessionTemplate = sqlSessionTemplates.get(dbIndex);
      T mapper = sqlSessionTemplate.getMapper(mapperType);
      return (T) ProxyMapperFactory.newInstance(mapper);
    }catch(ArrayIndexOutOfBoundsException e){
      throw new DbIndexOutOfBoundsException(e);
    }
  }

  @SuppressWarnings("unchecked")
  @Override
  public <T> List<T> getMappers(Class<T> mapperType) {
    List<T> mappers = new ArrayList<>();
    for (SqlSessionTemplate sqlSessionTemplate : sqlSessionTemplates) {
      T mapper = sqlSessionTemplate.getMapper(mapperType);
      mappers.add((T) ProxyMapperFactory.newInstance(mapper));
    }
    return mappers;
  }

  @Override
  public <T> T getMapperInCommonDb(Class<T> mapperType) {
    return commonSqlSessionTemplate.getMapper(mapperType);
  }

  public String generateShardingKey() {
    return dbShardingStrategy.generateShardingKey();
  }
  
  public int findDbIndexByShardingKey(String shardingKey) {
    return dbShardingStrategy.findDbIndexByShardingKey(shardingKey);
  }
  
  /*
   * TODO: 나중에 주기적인 connection validation check를 통해 validConnection list를 전달하는 방법으로 전환 고려중
   * 매번 ShardDB 선택시 isAvailableDbLocation을 통해 커넥션을 체크하기때문에 느림.. 따라서, 커넥션 상태를 저장하고
   * 주기적으로 정보를 업데이트하는 방식을 고려중...
   */
  public int mappingToDbByShardingKey(String shardingKey) {
    int dbIndex = dbShardingStrategy.mappingDbShardingIndex(shardingKey, this);
    if (dbIndex != DB_INDEX_NOT_EXIST) {
      DbgUtil.logger().debug("it's AvailableDbLocation : " + dbIndex);
      return dbIndex;
    }
    return DB_INDEX_NOT_EXIST;
  }
  
  @Override
  public boolean isAvailableDbLocation(int dbIndex) {
    SqlSessionTemplate sqlSessionTemplate = sqlSessionTemplates.get(dbIndex);
    try {
      return sqlSessionTemplate.selectOne("com.toast.votree.sharding.commonMapper.validationCheckQuery");
    } catch (MyBatisSystemException | CannotGetJdbcConnectionException e) {
      DbgUtil.logger().error(e.getLocalizedMessage());
    } /*catch (SQLException e) {
      DbgUtil.logger().error(e.getLocalizedMessage());
//      throw new RuntimeException(e);
    }*/
    return false;
  }
  
  public DbShardingStrategy getDbShardingStrategy() {
    return dbShardingStrategy;
  }
  
  public void setDbShardingStrategy(DbShardingStrategy dbShardingStrategy) {
    this.dbShardingStrategy = dbShardingStrategy;
  }
  
  public void setSqlSessionTemplates(List<SqlSessionTemplate> sqlSessionTemplates) {
    this.sqlSessionTemplates = sqlSessionTemplates;
  }
  
  public void setCommonSqlSessionTemplate(SqlSessionTemplate commonSqlSessionTemplate) {
    this.commonSqlSessionTemplate = commonSqlSessionTemplate;
  }
  
}
