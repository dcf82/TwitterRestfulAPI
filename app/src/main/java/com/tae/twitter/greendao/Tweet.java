package com.tae.twitter.greendao;

import com.tae.twitter.greendao.DaoSession;
import de.greenrobot.dao.DaoException;

// THIS CODE IS GENERATED BY greenDAO, EDIT ONLY INSIDE THE "KEEP"-SECTIONS

// KEEP INCLUDES - put your custom includes here
// KEEP INCLUDES END
/**
 * Entity mapped to table "TWEET".
 */
public class Tweet {

    private Long id;
    private Integer mTweetId;
    private Long mTweetTimeStamp;

    /** Used to resolve relations */
    private transient DaoSession daoSession;

    /** Used for active entity operations. */
    private transient TweetDao myDao;


    // KEEP FIELDS - put your custom fields here
    // KEEP FIELDS END

    public Tweet() {
    }

    public Tweet(Long id) {
        this.id = id;
    }

    public Tweet(Long id, Integer mTweetId, Long mTweetTimeStamp) {
        this.id = id;
        this.mTweetId = mTweetId;
        this.mTweetTimeStamp = mTweetTimeStamp;
    }

    /** called by internal mechanisms, do not call yourself. */
    public void __setDaoSession(DaoSession daoSession) {
        this.daoSession = daoSession;
        myDao = daoSession != null ? daoSession.getTweetDao() : null;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Integer getMTweetId() {
        return mTweetId;
    }

    public void setMTweetId(Integer mTweetId) {
        this.mTweetId = mTweetId;
    }

    public Long getMTweetTimeStamp() {
        return mTweetTimeStamp;
    }

    public void setMTweetTimeStamp(Long mTweetTimeStamp) {
        this.mTweetTimeStamp = mTweetTimeStamp;
    }

    /** Convenient call for {@link AbstractDao#delete(Object)}. Entity must attached to an entity context. */
    public void delete() {
        if (myDao == null) {
            throw new DaoException("Entity is detached from DAO context");
        }    
        myDao.delete(this);
    }

    /** Convenient call for {@link AbstractDao#update(Object)}. Entity must attached to an entity context. */
    public void update() {
        if (myDao == null) {
            throw new DaoException("Entity is detached from DAO context");
        }    
        myDao.update(this);
    }

    /** Convenient call for {@link AbstractDao#refresh(Object)}. Entity must attached to an entity context. */
    public void refresh() {
        if (myDao == null) {
            throw new DaoException("Entity is detached from DAO context");
        }    
        myDao.refresh(this);
    }

    // KEEP METHODS - put your custom methods here
    // KEEP METHODS END

}
