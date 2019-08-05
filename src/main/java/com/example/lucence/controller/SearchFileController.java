package com.example.lucence.controller;

import com.example.lucence.model.FileModel;
import com.example.lucence.model.Response;
import com.example.lucence.service.CreateIndex;
import com.example.lucence.service.SearchFileService;
import org.apache.lucene.search.highlight.InvalidTokenOffsetsException;
import org.apache.lucene.store.Directory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.ParseException;
import java.util.ArrayList;

@RestController
public class SearchFileController {

  @Autowired
  private SearchFileService searchFileService;

  @GetMapping("getDoc")
  public ResponseEntity<Response> getDoc(@RequestParam String key, @RequestParam int N){
    String indexpathStr = "src/main/resources/indexdir";
    ArrayList<FileModel> list = searchFileService.getTopDoc(key, indexpathStr, N);
    return ResponseEntity.ok().body(new Response(list));
  }
}
