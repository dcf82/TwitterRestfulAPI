package com.dao.generator;

import de.greenrobot.daogenerator.DaoGenerator;
import de.greenrobot.daogenerator.Entity;
import de.greenrobot.daogenerator.Schema;

public class GreenDaoGenerator {

    public static void main(String args[]) throws Exception {
        Schema schema = new Schema(1, "com.tae.twitter.greendao");

        schema.enableKeepSectionsByDefault();
        schema.enableActiveEntitiesByDefault();

        addChat(schema);

        new DaoGenerator().generateAll(schema, "app/src/main/java");
    }

    public static void addChat(Schema schema) {
        Entity chatRoom = schema.addEntity("Tweet");
        chatRoom.addIdProperty().autoincrement();
        chatRoom.addIntProperty("mTweetId");
        chatRoom.addLongProperty("mTweetTimeStamp");
    }
}
