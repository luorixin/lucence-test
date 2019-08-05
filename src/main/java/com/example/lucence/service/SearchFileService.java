package com.example.lucence.service;

import com.example.lucence.model.FileModel;
import com.googlecode.mp4parser.MultiFileDataSourceImpl;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.queryparser.classic.MultiFieldQueryParser;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.search.highlight.*;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;

@Service
public class SearchFileService {
  public ArrayList<FileModel> getTopDoc(String key, String indexpathStr, int N){
    ArrayList<FileModel> hitsList = new ArrayList<FileModel>();
    //检索域
    String[] fields = {"title", "content"};
    Path indexPath = Paths.get(indexpathStr);
    Directory dir = null;
    try {
      dir = FSDirectory.open(indexPath);
      IndexReader indexReader = DirectoryReader.open(dir);
      IndexSearcher searcher = new IndexSearcher(indexReader);
      Analyzer analyzer = new IKAnalyzer6x();
      MultiFieldQueryParser parser = new MultiFieldQueryParser(fields, analyzer);
      Query query = parser.parse(key);
      TopDocs topDocs = searcher.search(query, N);
      SimpleHTMLFormatter fors = new SimpleHTMLFormatter("<span style='color:red;'>","</span>");

      QueryScorer scoreTitle = new QueryScorer(query, fields[0]);
      Highlighter hlgliter = new Highlighter(fors, scoreTitle);

      QueryScorer scoreContent = new QueryScorer(query, fields[1]);
      Highlighter hlgContent = new Highlighter(fors, scoreContent);

      TopDocs hits = searcher.search(query, 100);
      for(ScoreDoc sd: topDocs.scoreDocs){
        Document doc = searcher.doc(sd.doc);
        String title = doc.get("title");
        String content = doc.get("content");
        TokenStream tokenStream = TokenSources.getAnyTokenStream(searcher.getIndexReader(), sd.doc, fields[0], new IKAnalyzer6x());
        Fragmenter fragmenter = new SimpleSpanFragmenter(scoreTitle);
        hlgliter.setTextFragmenter(fragmenter);
        String h1_title = hlgliter.getBestFragment(tokenStream, title);
        //获取高亮的片段，可以对数量进行控制
        tokenStream = TokenSources.getAnyTokenStream(searcher.getIndexReader(), sd.doc, fields[1], new IKAnalyzer6x());
        fragmenter = new SimpleSpanFragmenter(scoreContent);
        hlgContent.setTextFragmenter(fragmenter);
        String h1_content = hlgContent.getBestFragment(tokenStream, content);
        FileModel fm = new FileModel(h1_title!=null ? h1_title : title, h1_content!=null ? h1_content : content);
        hitsList.add(fm);
      }
      dir.close();
      indexReader.close();
    }catch (IOException e){
      e.printStackTrace();
    }catch (ParseException e){
      e.printStackTrace();
    }catch (InvalidTokenOffsetsException e){
      e.printStackTrace();
    }
    return hitsList;
  }
}
