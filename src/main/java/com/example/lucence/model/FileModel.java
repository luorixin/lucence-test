package com.example.lucence.model;


import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@ToString
@Setter
@Getter
@JsonSerialize
public class FileModel {
  private String title;
  private String content;

  public FileModel(){

  }

  public FileModel(String title, String content){
    this.title = title;
    this.content = content;
  }

}
