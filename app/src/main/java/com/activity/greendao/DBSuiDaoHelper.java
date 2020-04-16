package com.activity.greendao;


import com.activity.MyApplication;

import java.util.List;

/**
 * Created by Administrator on 2018/7/18.
 */

public class DBSuiDaoHelper {
    public static void insertsuidao(SuiDao suidao) {
        MyApplication.getDaoInstant().getSuiDaoDao().insertOrReplace(suidao);
    }

    /**
     * 删除数据
     *
     * @param id
     */
    public static void deletesuidao(long id) {
        MyApplication.getDaoInstant().getSuiDaoDao().deleteByKey(id);
    }
    public static void deleteALL() {
        MyApplication.getDaoInstant().getSuiDaoDao().deleteAll();
    }
    /**
     * 更新数据
     */
    public static void updatesuidao(SuiDao shop) {
        MyApplication.getDaoInstant().getSuiDaoDao().update(shop);
    }

    /**
     * 查询所有数据
     *
     * @return
     */
    public static List<SuiDao> queryAll() {
        return MyApplication.getDaoInstant().getSuiDaoDao().loadAll();
    }

    public static String quarySetionName(long id) {
        return MyApplication.getDaoInstant().getSuiDaoDao().load(id).getName();
    }

}
