package xin.jiangqiang.lucene.mypackage;

import java.io.IOException;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.cn.smart.SmartChineseAnalyzer;

/**
 * @author jiangqiang
 * @date 2020/11/10 17:02
 */
public class TestAnalyzer {

    public static void main(String[] args) throws IOException {
        Analyzer analyzer = new SmartChineseAnalyzer();
        TokenStream ts = analyzer.tokenStream("name", "护眼带光源");
        ts.reset();
        while (ts.incrementToken()) {
            System.out.println(ts.reflectAsString(false));
        }
    }
}