package xin.jiangqiang.lucene.mypackage;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.CharArraySet;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.WordlistLoader;
import org.apache.lucene.analysis.cn.smart.SmartChineseAnalyzer;
import org.apache.lucene.analysis.tokenattributes.OffsetAttribute;
import org.apache.lucene.util.IOUtils;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

/**
 * @author jiangqiang
 * @date 2020/11/9 16:24
 */
public class TestAnalyzer2 {
    public static void main(String[] args) {
        try {
            String text = "通过docker容器部署halo博客系统，方便快捷，全程只要1分钟";
            CharArraySet stopWords = CharArraySet.unmodifiableSet(WordlistLoader.getWordSet(IOUtils.getDecodingReader(
                    TestAnalyzer2.class, "stopwords.txt", StandardCharsets.UTF_8), "//"));
            Analyzer analyzer = new SmartChineseAnalyzer(stopWords);
            TokenStream tokenStream = analyzer.tokenStream("testField", text);
            OffsetAttribute offsetAttribute = tokenStream.addAttribute(OffsetAttribute.class);
            tokenStream.reset();
            List<String> tokens = new ArrayList<>();
            while (tokenStream.incrementToken()) {
                tokens.add(offsetAttribute.toString());
            }
            tokenStream.end();
            System.out.println(tokens);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
