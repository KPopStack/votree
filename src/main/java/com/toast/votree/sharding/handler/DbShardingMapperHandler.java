package com.toast.votree.sharding.handler;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import org.mybatis.spring.MyBatisSystemException;
import org.springframework.cglib.proxy.InvocationHandler;

import com.toast.votree.util.DbgUtil;

public class DbShardingMapperHandler implements InvocationHandler {
  protected Object target;

  public DbShardingMapperHandler(Object target) {
    this.target = target;
  }

  @Override
  public Object invoke(Object proxy, Method m, Object[] args) throws Throwable {
    Object result = null;
    try{
      result = m.invoke(target, args);
    } catch (InvocationTargetException e) {
      if (List.class.isAssignableFrom(m.getReturnType())) {
//        Type elementType = ReflectionUtil.getGenericTypeOfMethod(m);
//        DbgUtil.logger().debug("DbShardingMapperHandler - isAssignableFrom!!:" + elementType.getTypeName());
        result = new ArrayList<>();
      }
      if ( !(e.getCause() instanceof MyBatisSystemException) ) {
        throw e;
      }
    } catch (Exception e) {
      DbgUtil.logger().error(e.getLocalizedMessage());
      throw e;
    }
    return result;
  }

}
