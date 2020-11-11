package xin.jiangqiang.lucene.testLucene;

import org.apache.lucene.analysis.cn.smart.SmartChineseAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.StringField;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.*;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TermQuery;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.store.*;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.nio.file.Paths;

/**
 * @author jiangqiang
 * @date 2020/11/10 10:37
 */
public class IndexSearchDemo {
    private Directory directory = null;
    private String[] ids = {"1", "2"};
    private String[] teamname = {"fpx", "ig"};
    private String[] contents = {"涅槃队，凤凰涅槃，勇夺2019LOLS赛冠军!", "翻山队，登峰造极，翻过那座山夺得了2019LOLS赛冠军!"};
    private String[] players = {"doinb,tian,lwx,crisp,gimgoong", "rookie,jacklove,ning,baolan,theshy"};
    private IndexWriterConfig indexWriterConfig = new IndexWriterConfig(new SmartChineseAnalyzer());
    private IndexWriter indexWriter;

    @Before
    public void init() {
        try {
            this.directory = FSDirectory.open(Paths.get("D:\\Documents\\file\\lucene"));
            this.indexWriter = new IndexWriter(directory, indexWriterConfig);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @After
    public void clearResource() {
        try {
            indexWriter.close();
            directory.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 创建索引
     */
    @Test
    public void createIndex() {
        try {
            for (int i = 0; i < 2; i++) {
                Document document = new Document();
                Field idField = new StringField("id", ids[i], Field.Store.YES);
                Field teamnameField = new StringField("teamname", teamname[i], Field.Store.YES);
                Field contentField = new TextField("content", contents[i], Field.Store.YES);
                Field playersField = new StringField("players", players[i], Field.Store.YES);
                document.add(idField);
                document.add(teamnameField);
                document.add(contentField);
                document.add(playersField);
                indexWriter.addDocument(document);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void testIndexWriter() throws Exception {
        System.out.println(indexWriter.getPendingNumDocs());
        System.out.println("写入了" + indexWriter.numRamDocs() + "个文档");
    }

    @Test
    public void testIndexReader() throws Exception {
        IndexReader reader = DirectoryReader.open(directory);
        System.out.println("最大文档数：" + reader.maxDoc());
        System.out.println("实际文档数：" + reader.numDocs());
        reader.close();
    }

    @Test
    public void testDeleteBeforeMerge() throws Exception {

//        System.out.println("删除前：" + indexWriter.numDocs());
        indexWriter.deleteDocuments(new Term("id", "1"));// term：根据id找到为1的
        indexWriter.commit();
//        System.out.println("writer.maxDoc()：" + indexWriter.maxDoc());
//        System.out.println("writer.numDocs()：" + indexWriter.numDocs());
    }

    /**
     * 测试删除 在合并后
     *
     * @throws Exception
     */
    @Test
    public void testDeleteAfterMerge() throws Exception {

//        System.out.println("删除前：" + indexWriter.numDocs());
//        indexWriter.deleteDocuments(new Term("id", "1"));
//        indexWriter.forceMergeDeletes(); // 强制删除
//        indexWriter.commit();
//        System.out.println("writer.maxDoc()：" + indexWriter.maxDoc());
//        System.out.println("writer.numDocs()：" + indexWriter.numDocs());

    }

    /**
     * 测试更新
     *
     * @throws Exception
     */
    @Test
    public void testUpdate() throws Exception {

        Document doc = new Document();
        doc.add(new StringField("id", "1", Field.Store.YES));
        doc.add(new StringField("city", "qingdao", Field.Store.YES));
        doc.add(new TextField("desc", "dsss is a city.", Field.Store.NO));
        indexWriter.updateDocument(new Term("id", "1"), doc);
        indexWriter.close();
    }

    @Test
    public void testTermQuery() throws IOException {
//        createIndex();
//        System.out.println(indexWriter.numRamDocs());

        IndexReader reader = DirectoryReader.open(directory);
        System.out.println("最大文档数：" + reader.maxDoc());
        System.out.println("实际文档数：" + reader.numDocs());
        reader.close();

//        Term term = new Term("id", "1");
////
//        IndexReader reader = DirectoryReader.open(index);
//        IndexSearcher searcher = new IndexSearcher(reader);
////
//        IndexSearcher indexSearcher = getIndexSearcher.;
//
//        TopDocs topDocs = indexSearcher.search(new TermQuery(term), 10);
//
//        // 1)打印总记录数（命中数）：类似于百度为您找到相关结果约100,000,000个
//
//        long totalHits = topDocs.totalHits.value;
//
//        System.out.println("查询（命中）总的文档条数：" + totalHits);
//
//        // LOGGER.info("查询（命中）文档最大分数："+topDocs.getMaxScore());
//
//        // 2)获取指定的最大条数的、命中的查询结果的文档对象集合
//
//        ScoreDoc[] scoreDocs = topDocs.scoreDocs;
//
//        // 打印具体文档
//
//        for (ScoreDoc scoreDoc : scoreDocs) {
//
//            int doc = scoreDoc.doc;
//
//            Document document = indexSearcher.doc(doc);
//
//            // 打印content字段的值
//
//            System.out.println("id: " + document.get("id"));
//
//            System.out.println("teamname: " + document.get("teamname"));
//
//            System.out.println("content: " + document.get("content"));
//
//            System.out.println("players: " + document.get("players"));
//
//        }

    }

}
