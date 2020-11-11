package xin.jiangqiang.lucene.mypackage;

import java.awt.AWTException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.io.Charsets;
import org.apache.commons.io.IOUtils;

/**
 * @author jiangqiang
 * @date 2020/11/10 17:49
 */
public class ProductUtil {
    public static void main(String[] args) throws IOException, InterruptedException, AWTException {
        String fileName = "140k_products.txt";
        InputStream inputStream = ProductUtil.class.getClassLoader().getResourceAsStream(fileName);
//        InputStream inputStream = ProductUtil.class.getResourceAsStream("/"+fileName);
        List<Product> products = file2list(inputStream);
        System.out.println(products.size());
    }

    public static List<Product> file2list(InputStream inputStream) throws IOException {
        List<String> lines = IOUtils.readLines(inputStream, Charsets.toCharset("UTF-8"));
        List<Product> products = new ArrayList<>();
        for (String line : lines) {
            Product p = line2product(line);
            products.add(p);
        }
        return products;
    }

    private static Product line2product(String line) {
        Product p = new Product();
        String[] fields = line.split(",");
        p.setId(Integer.parseInt(fields[0]));
        p.setName(fields[1]);
        p.setCategory(fields[2]);
        p.setPrice(Float.parseFloat(fields[3]));
        p.setPlace(fields[4]);
        p.setCode(fields[5]);
        return p;
    }

}