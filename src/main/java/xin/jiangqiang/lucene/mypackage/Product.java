package xin.jiangqiang.lucene.mypackage;

import lombok.Data;

/**
 * @author jiangqiang
 * @date 2020/11/10 17:47
 */
@Data
public class Product {
    int id;
    String name;
    String category;
    float price;
    String place;
    String code;
}