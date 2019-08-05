package com.example.lucence.service;

import com.example.lucence.model.FileModel;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.FieldType;
import org.apache.lucene.index.IndexOptions;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.tika.exception.TikaException;
import org.apache.tika.metadata.Metadata;
import org.apache.tika.parser.AutoDetectParser;
import org.apache.tika.parser.ParseContext;
import org.apache.tika.parser.Parser;
import org.apache.tika.sax.BodyContentHandler;
import org.springframework.util.ResourceUtils;
import org.xml.sax.SAXException;

import java.io.File;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;

public class CreateIndex {

  public static ArrayList<FileModel> extractFile() throws IOException{
    ArrayList<FileModel> list = new ArrayList<FileModel>();
    File fileDir = new File("src/main/resources/static");
    File[] allFiles = fileDir.listFiles();
    for (File f: allFiles
         ) {
      FileModel sf = new FileModel(f.getName(), ParserExtraction(f));
      list.add(sf);
    }
    return list;
  }

  public static String ParserExtraction(File file){
    String fileContent = "";
    BodyContentHandler handler = new BodyContentHandler(10 * 1024 * 1024);
    Parser parser = new AutoDetectParser();
    Metadata metadata = new Metadata();
    FileInputStream inputStream;
    try{
      inputStream = new FileInputStream(file);
      ParseContext context = new ParseContext();
      parser.parse(inputStream, handler, metadata, context);
      fileContent = handler.toString();
    }catch (FileNotFoundException e){
      e.printStackTrace();
    }catch (IOException e){
      e.printStackTrace();
    }catch (SAXException e){
      e.printStackTrace();
    }catch (TikaException e){
      e.printStackTrace();
    }
    return fileContent;
  }

  public static void main(String[] args) throws IOException{
    Analyzer analyzer = new IKAnalyzer6x();
    IndexWriterConfig icw = new IndexWriterConfig(analyzer);
    icw.setOpenMode(IndexWriterConfig.OpenMode.CREATE);
    Directory dir = null;
    IndexWriter indexWriter = null;
    Path indexPath = Paths.get("src/main/resources/indexdir");
    FieldType fieldType = new FieldType();
    fieldType.setIndexOptions(IndexOptions.DOCS_AND_FREQS_AND_POSITIONS_AND_OFFSETS);
    fieldType.setStored(true);
    fieldType.setTokenized(true);
    fieldType.setStoreTermVectors(true);
    fieldType.setStoreTermVectorPositions(true);
    fieldType.setStoreTermVectorOffsets(true);
    Date start = new Date();
    if (!Files.isReadable(indexPath)){
      System.out.println(indexPath.toAbsolutePath() + "不存在或者不可读，请检查！");
      System.exit(1);
    }
    dir = FSDirectory.open(indexPath);
    indexWriter = new IndexWriter(dir, icw);
    ArrayList<FileModel> fileList = extractFile();
    for (FileModel file: fileList) {
      Document doc = new Document();
      doc.add(new Field("title", file.getTitle(), fieldType));
      doc.add(new Field("content", file.getContent(), fieldType));
      indexWriter.addDocument(doc);
    }
    indexWriter.commit();
    indexWriter.close();
    dir.close();
    Date end = new Date();
    System.out.println("索引文档建立完成，共耗时："+ (end.getTime() - start.getTime()) + "毫秒");
  }
}


