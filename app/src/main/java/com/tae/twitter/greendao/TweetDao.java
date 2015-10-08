package com.tae.twitter.greendao;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;

import de.greenrobot.dao.AbstractDao;
import de.greenrobot.dao.Property;
import de.greenrobot.dao.internal.DaoConfig;

import com.tae.twitter.greendao.Tweet;

// THIS CODE IS GENERATED BY greenDAO, DO NOT EDIT.
/** 
 * DAO for table "TWEET".
*/
public class TweetDao extends AbstractDao<Tweet, Long> {

    public static final String TABLENAME = "TWEET";

    /**
     * Properties of entity Tweet.<br/>
     * Can be used for QueryBuilder and for referencing column names.
    */
    public static class Properties {
        public final static Property Id = new Property(0, Long.class, "id", true, "_id");
        public final static Property MTweetId = new Property(1, Integer.class, "mTweetId", false, "M_TWEET_ID");
        public final static Property MTweetTimeStamp = new Property(2, Long.class, "mTweetTimeStamp", false, "M_TWEET_TIME_STAMP");
    };

    private DaoSession daoSession;


    public TweetDao(DaoConfig config) {
        super(config);
    }
    
    public TweetDao(DaoConfig config, DaoSession daoSession) {
        super(config, daoSession);
        this.daoSession = daoSession;
    }

    /** Creates the underlying database table. */
    public static void createTable(SQLiteDatabase db, boolean ifNotExists) {
        String constraint = ifNotExists? "IF NOT EXISTS ": "";
        db.execSQL("CREATE TABLE " + constraint + "\"TWEET\" (" + //
                "\"_id\" INTEGER PRIMARY KEY AUTOINCREMENT ," + // 0: id
                "\"M_TWEET_ID\" INTEGER," + // 1: mTweetId
                "\"M_TWEET_TIME_STAMP\" INTEGER);"); // 2: mTweetTimeStamp
    }

    /** Drops the underlying database table. */
    public static void dropTable(SQLiteDatabase db, boolean ifExists) {
        String sql = "DROP TABLE " + (ifExists ? "IF EXISTS " : "") + "\"TWEET\"";
        db.execSQL(sql);
    }

    /** @inheritdoc */
    @Override
    protected void bindValues(SQLiteStatement stmt, Tweet entity) {
        stmt.clearBindings();
 
        Long id = entity.getId();
        if (id != null) {
            stmt.bindLong(1, id);
        }
 
        Integer mTweetId = entity.getMTweetId();
        if (mTweetId != null) {
            stmt.bindLong(2, mTweetId);
        }
 
        Long mTweetTimeStamp = entity.getMTweetTimeStamp();
        if (mTweetTimeStamp != null) {
            stmt.bindLong(3, mTweetTimeStamp);
        }
    }

    @Override
    protected void attachEntity(Tweet entity) {
        super.attachEntity(entity);
        entity.__setDaoSession(daoSession);
    }

    /** @inheritdoc */
    @Override
    public Long readKey(Cursor cursor, int offset) {
        return cursor.isNull(offset + 0) ? null : cursor.getLong(offset + 0);
    }    

    /** @inheritdoc */
    @Override
    public Tweet readEntity(Cursor cursor, int offset) {
        Tweet entity = new Tweet( //
            cursor.isNull(offset + 0) ? null : cursor.getLong(offset + 0), // id
            cursor.isNull(offset + 1) ? null : cursor.getInt(offset + 1), // mTweetId
            cursor.isNull(offset + 2) ? null : cursor.getLong(offset + 2) // mTweetTimeStamp
        );
        return entity;
    }
     
    /** @inheritdoc */
    @Override
    public void readEntity(Cursor cursor, Tweet entity, int offset) {
        entity.setId(cursor.isNull(offset + 0) ? null : cursor.getLong(offset + 0));
        entity.setMTweetId(cursor.isNull(offset + 1) ? null : cursor.getInt(offset + 1));
        entity.setMTweetTimeStamp(cursor.isNull(offset + 2) ? null : cursor.getLong(offset + 2));
     }
    
    /** @inheritdoc */
    @Override
    protected Long updateKeyAfterInsert(Tweet entity, long rowId) {
        entity.setId(rowId);
        return rowId;
    }
    
    /** @inheritdoc */
    @Override
    public Long getKey(Tweet entity) {
        if(entity != null) {
            return entity.getId();
        } else {
            return null;
        }
    }

    /** @inheritdoc */
    @Override    
    protected boolean isEntityUpdateable() {
        return true;
    }
    
}