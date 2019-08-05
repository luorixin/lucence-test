package com.example.lucence.service;

import lombok.Getter;
import lombok.Setter;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.Tokenizer;

@Getter
@Setter
public class IKAnalyzer6x extends Analyzer {

  private boolean useSmart;

  // IK分词器Lucene Analyzer接口实现类;默认细粒度切分算法
  public IKAnalyzer6x() {
    this(false);
  }

  // IK分词器Lucene Analyzer接口实现类;当为true时，分词器进行智能切分
  public IKAnalyzer6x(boolean useSmart) {
    super();
    this.useSmart = useSmart;
  }

  // 重写最新版本的createComponents;重载Analyzer接口，构造分词组件
  @Override
  protected TokenStreamComponents createComponents(String s) {
    Tokenizer _IKTokenizer = new IKTokenizer6x(this.isUseSmart());

    return new TokenStreamComponents(_IKTokenizer);
  }
}
