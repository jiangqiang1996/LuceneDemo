package xin.jiangqiang.lucene.mypackage;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.cn.smart.SmartChineseAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.*;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.search.highlight.Highlighter;
import org.apache.lucene.search.highlight.QueryScorer;
import org.apache.lucene.search.highlight.SimpleHTMLFormatter;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;

import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

import static xin.jiangqiang.lucene.mypackage.ProductUtil.file2list;

/**
 * @author jiangqiang
 * @date 2020/11/12 9:20
 */
public class T1 {


    public static void main(String[] args) throws Exception {
        String fileName = "140k_products.txt";
        InputStream inputStream = ProductUtil.class.getClassLoader().getResourceAsStream(fileName);
//        InputStream inputStream = ProductUtil.class.getResourceAsStream("/"+fileName);
        List<Product> products = file2list(inputStream);

        Directory directory = FSDirectory.open(Path.of("D:\\Documents\\file\\lucene"));

        // 1. 准备中文分词器
        Analyzer analyzer = new SmartChineseAnalyzer();

        //2.创建索引
        IndexWriter indexWriter = new IndexWriter(directory, new IndexWriterConfig(analyzer));

        for (Product product : products) {
            Document document = new Document();
            document.add(new TextField("name", product.getName(), Field.Store.YES));
            indexWriter.addDocument(document);
        }
        /**
         * term是搜索的最小单元，一个field最后对应多个term
         */
        indexWriter.deleteDocuments(new Term("id", "51173"));

        /**
         * 模拟数据
         */
        Document doc = new Document();
        doc.add(new TextField("id", "51173", Field.Store.YES));
        doc.add(new TextField("name", "神鞭，鞭没了，神还在", Field.Store.YES));
        doc.add(new TextField("category", "道具", Field.Store.YES));
        doc.add(new TextField("price", "998", Field.Store.YES));
        doc.add(new TextField("place", "南海群岛", Field.Store.YES));
        doc.add(new TextField("code", "888888", Field.Store.YES));
        /**
         * 修改索引
         */
        indexWriter.updateDocument(new Term("id", "51173"), doc);

        indexWriter.commit();
        indexWriter.close();


        // 3. 查询器
        String keyword = "鞭";
        Query query = new QueryParser("name", analyzer).parse(keyword);

        // 4. 搜索
        /**
         * 分页原理：
         * 1. 先查询出包含当前页记录数的所有结果，然后取出最后一页。
         * 2. 先查询出当前页之前（不包含）的所有结果，然后接着查一页的数据。
         */
        IndexReader reader = DirectoryReader.open(directory);
        IndexSearcher searcher = new IndexSearcher(reader);
        int numberPerPage = 100;//总共查询一百条
        System.out.printf("查询关键字是：\"%s\"%n", keyword);
        ScoreDoc[] hits = searcher.search(query, numberPerPage).scoreDocs;

        // 5. 显示查询结果
        //语法高亮
        SimpleHTMLFormatter simpleHTMLFormatter = new SimpleHTMLFormatter("<span style='color:red'>", "</span>");
        Highlighter highlighter = new Highlighter(simpleHTMLFormatter, new QueryScorer(query));

        for (int i = 0; i < hits.length; ++i) {
            ScoreDoc scoreDoc = hits[i];
            int docId = scoreDoc.doc;
            Document d = searcher.doc(docId);
            List<IndexableField> fields = d.getFields();
            System.out.print((i + 1));
            System.out.print("\t" + scoreDoc.score);
            for (IndexableField f : fields) {
//                System.out.print("\t" + d.get(f.name()));
                //语法高亮
                TokenStream tokenStream = analyzer.tokenStream(f.name(), new StringReader(d.get(f.name())));
                String fieldContent = highlighter.getBestFragment(tokenStream, d.get(f.name()));
                System.out.print("\t" + fieldContent);
            }
            System.out.println();
        }
        // 6. 关闭查询
        reader.close();
        /**
         * 语法高亮，分页，索引删除 修改
         */

    }

    /**
     * 分页方式一
     *
     * @param query
     * @param searcher
     * @param pageNow  当前页数
     * @param pageSize 每一页的数量
     * @return
     * @throws IOException
     */
    private static ScoreDoc[] pageSearch1(Query query, IndexSearcher searcher, int pageNow, int pageSize)
            throws IOException {
        TopDocs topDocs = searcher.search(query, pageNow * pageSize);
        System.out.println("查询到的总条数\t" + topDocs.totalHits);
        ScoreDoc[] alllScores = topDocs.scoreDocs;

        List<ScoreDoc> hitScores = new ArrayList<>();

        int start = (pageNow - 1) * pageSize;
        int end = pageSize * pageNow;
        for (int i = start; i < end; i++)
            hitScores.add(alllScores[i]);

        ScoreDoc[] hits = hitScores.toArray(new ScoreDoc[]{});
        return hits;
    }

    /**
     * 分页方式二
     *
     * @param query
     * @param searcher
     * @param pageNow
     * @param pageSize
     * @return
     * @throws IOException
     */
    private static ScoreDoc[] pageSearch2(Query query, IndexSearcher searcher, int pageNow, int pageSize)
            throws IOException {

        int start = (pageNow - 1) * pageSize;
        if (0 == start) {
            TopDocs topDocs = searcher.search(query, pageNow * pageSize);
            return topDocs.scoreDocs;
        }
        // 查询数据， 结束页面之前的数据都会查询到，但是只取本页的数据
        TopDocs topDocs = searcher.search(query, start);
        //获取到上一页最后一条

        ScoreDoc preScore = topDocs.scoreDocs[start - 1];

        //查询最后一条数据的后一页数据
        topDocs = searcher.searchAfter(preScore, query, pageSize);
        return topDocs.scoreDocs;

    }
}
