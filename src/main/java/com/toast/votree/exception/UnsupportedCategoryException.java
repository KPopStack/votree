package com.toast.votree.exception;

/*
 * 지원하지 않는 카테고리 타입을 인자로 넘겼을경우 로직 처리시 발생하는 에러
 */
public class UnsupportedCategoryException extends IllegalArgumentException {
	private static final long serialVersionUID = 2714889584216044203L;
  private String categoryName;
  
  public UnsupportedCategoryException(String categoryName) {
    super(String.valueOf(categoryName));
    this.categoryName = categoryName;
  }

  public String getCategoryName() {
    return categoryName;
  }

}
