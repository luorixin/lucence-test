package com.example.lucence.service;

import org.apache.lucene.analysis.Tokenizer;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;
import org.apache.lucene.analysis.tokenattributes.OffsetAttribute;
import org.apache.lucene.analysis.tokenattributes.TypeAttribute;
import org.wltea.analyzer.core.IKSegmenter;
import org.wltea.analyzer.core.Lexeme;

import java.io.IOException;

public class IKTokenizer6x extends Tokenizer {

  // IK分词器实现
  private IKSegmenter ikSegmenter;
  // 词元文本属性
  private final CharTermAttribute termAttr;
  // 词元位移属性
  private final OffsetAttribute offsetAttr;
  // 词元分类属性（该属性分类参考org.wltea.analyzer.core.Lexeme中的分类常量）
  private final TypeAttribute typeAttr;
  // 记录最后一个词元的结束位置
  private int endPosition;
  // Lucene 6.x Tokenizer适配器类构造函数;实现最新的Tokenizer接口
  public IKTokenizer6x(boolean useSmart){
    super();
    offsetAttr = addAttribute(OffsetAttribute.class);
    termAttr = addAttribute(CharTermAttribute.class);
    typeAttr = addAttribute(TypeAttribute.class);
    ikSegmenter = new IKSegmenter(input, useSmart);
  }

  @Override
  public boolean incrementToken() throws IOException {
    // 清除所有的词元属性
    clearAttributes();
    Lexeme nextLexeme = ikSegmenter.next();
    if (nextLexeme != null){
      // 将Lexeme转成Attributes
      // 设置词元文本
      termAttr.append(nextLexeme.getLexemeText());
      // 设置词元长度
      termAttr.setLength(nextLexeme.getLength());
      // 设置词元位移
      offsetAttr.setOffset(nextLexeme.getBeginPosition(), nextLexeme.getEndPosition());
      // 记录分词的最后位置
      endPosition = nextLexeme.getEndPosition();
      // 记录词元分类
      typeAttr.setType(nextLexeme.getLexemeText());
      // 返会true告知还有下个词元
      return true;
    }
    // 返会false告知词元输出完毕
    return false;
  }

  @Override
  public void reset() throws IOException{
    super.reset();
    ikSegmenter.reset(input);
  }

  @Override
  public final void end(){
    int finalOffset = correctOffset(this.endPosition);
    offsetAttr.setOffset(finalOffset, finalOffset);
  }
}
