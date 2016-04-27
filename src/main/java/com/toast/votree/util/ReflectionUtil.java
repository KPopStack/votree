package com.toast.votree.util;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class ReflectionUtil {
  
  public static Type getGenericTypeOfMethodReturn(Method m) throws ClassNotFoundException {
    Type returnType = m.getGenericReturnType();

    if(returnType instanceof ParameterizedType) {
        ParameterizedType type = (ParameterizedType) returnType;
        Type[] typeArguments = type.getActualTypeArguments();
        if (typeArguments.length == 0) {
          throw new ClassNotFoundException();
        } else if (typeArguments.length == 1) {
          return typeArguments[0];
        }
    }
    
    return null;
  }
  
  public static List<Type> getGenericTypeOfMethodParameter(Method method) throws ClassNotFoundException {
    List<Type> types = new ArrayList<>(); 
    Type[] genericParameterTypes = method.getGenericParameterTypes();

    for(Type genericParameterType : genericParameterTypes) {
        if(genericParameterType instanceof ParameterizedType) {
            ParameterizedType aType = (ParameterizedType) genericParameterType;
            Type[] parameterArgTypes = aType.getActualTypeArguments();
            for(Type parameterArgType : parameterArgTypes) {
                types.add(parameterArgType);
            }
        }
    }

    return types;
  }
  
  public static Type getGenericTypeOfField(Field field) throws ClassNotFoundException {
    Type genericFieldType = field.getGenericType();

    if(genericFieldType instanceof ParameterizedType) {
        ParameterizedType aType = (ParameterizedType) genericFieldType;
        Type[] fieldArgTypes = aType.getActualTypeArguments();
        if (fieldArgTypes.length == 0) {
          throw new ClassNotFoundException();
        } else if (fieldArgTypes.length == 1) {
          return fieldArgTypes[0];
        }
    }
    
    return null;
  }
}