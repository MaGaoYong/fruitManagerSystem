package com.atguigu.fruit.dao;

import com.atguigu.fruit.pojo.Fruit;

import java.util.List;

/**
 * @author adventure
 * @create 2022-05-17 17:20
 */
public interface FruitDAO {

    //获取指定页码上的所有的库存列表信息,每页显示5条
    List<Fruit> getFruitList(String keyword,Integer pageNo);

    //根据fid获取特定的水果库存信息
    Fruit getFruitByFid(Integer fid);

    //修改指定的库存记录
    void updateFruit(Fruit fruit);

    //根据fid删除指定的库存记录
    void delFruit(Integer fid);

    //添加水果
    void addFruit(Fruit furit);

    //查询库存总记录条数
    int getFruitCount(String keyword);
}
