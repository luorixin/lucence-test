package com.example.lucence.model;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@Getter
@Setter
@JsonSerialize(include= JsonSerialize.Inclusion.NON_NULL)
public class Response<T>  implements Serializable {

  private T data;
  private String code;
  private String msg;
  public Response(){
    this.code = "200";
    this.msg = "请求成功";
  }

  public Response(String code,String msg){
    this();
    this.code = code;
    this.msg = msg;
  }
  public Response(String code,String msg,T data){
    this();
    this.code = code;
    this.msg = msg;
    this.data = data;
  }
  public Response(T data){
    this();
    this.data = data;
  }

  @Override
  public String toString() {
    return "Response{" +
      "data=" + data +
      ", code=" + code +
      ", msg='" + msg + '\'' +
      '}';
  }
}

