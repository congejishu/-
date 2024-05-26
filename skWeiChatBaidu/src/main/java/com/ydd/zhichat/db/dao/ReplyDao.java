package com.ydd.zhichat.db.dao;

import com.j256.ormlite.dao.Dao;
import com.ydd.zhichat.bean.Friend;
import com.ydd.zhichat.db.SQLiteHelper;

public class ReplyDao {
    private static FriendDao instance = null;
    public Dao<Friend, Integer> friendDao;
    private SQLiteHelper mHelper;

}
