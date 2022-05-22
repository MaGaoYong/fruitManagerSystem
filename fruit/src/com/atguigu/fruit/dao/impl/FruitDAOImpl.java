package com.atguigu.fruit.dao.impl;

import com.atguigu.fruit.dao.FruitDAO;
import com.atguigu.fruit.pojo.Fruit;
import com.atguigu.myssm.basedao.BaseDAO;

import java.util.List;

/**
 * @author adventure
 * @create 2022-05-17 17:21
 */
public class FruitDAOImpl extends BaseDAO<Fruit> implements FruitDAO {

    @Override
    public List<Fruit> getFruitList(String keyword,Integer pageNo) {

        //使得数据库中显示某一页5条数据
        //SELECT * FROM t_fruit LIMIT 5*(pageNo-1),5;

            return super.executeQuery("SELECT * FROM t_fruit WHERE fname LIKE ? OR remark LIKE ? LIMIT ? ,5","%"+keyword+"%","%"+keyword+"%",(pageNo-1) * 5);

    }

    @Override
    public Fruit getFruitByFid(Integer fid) {

            return super.load("SELECT * FROM t_fruit WHERE fid = ?",fid);

    }

    @Override
    public void updateFruit(Fruit fruit) {
        String sql = "UPDATE t_fruit SET fname = ?,price=?,fcount=?,remark=? WHERE fid = ?";
            super.executeUpdate(sql,fruit.getFname(),fruit.getPrice(),fruit.getFcount(),fruit.getRemark(),fruit.getFid());

    }

    @Override
    public void delFruit(Integer fid) {

            super.executeUpdate("DELETE FROM t_fruit WHERE fid = ?",fid);

    }

    @Override
    public void addFruit(Fruit furit) {
        String sql = "INSERT INTO t_fruit VALUES(0,?,?,?,?)";

            super.executeUpdate(sql,furit.getFname(),furit.getPrice(),furit.getFcount(),furit.getRemark());

    }

    @Override
    public int getFruitCount(String keyword) {

            return ((Long)super.executeComplexQuery("SELECT COUNT(*) FROM t_fruit WHERE fname LIKE ? OR remark LIKE ?","%"+keyword+"%","%"+keyword+"%")[0]).intValue();

    }
}
