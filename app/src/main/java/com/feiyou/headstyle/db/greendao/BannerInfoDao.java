package com.feiyou.headstyle.db.greendao;

import android.database.Cursor;
import android.database.sqlite.SQLiteStatement;

import org.greenrobot.greendao.AbstractDao;
import org.greenrobot.greendao.Property;
import org.greenrobot.greendao.internal.DaoConfig;
import org.greenrobot.greendao.database.Database;
import org.greenrobot.greendao.database.DatabaseStatement;

import com.feiyou.headstyle.bean.BannerInfo;

// THIS CODE IS GENERATED BY greenDAO, DO NOT EDIT.
/** 
 * DAO for table "BANNER_INFO".
*/
public class BannerInfoDao extends AbstractDao<BannerInfo, Long> {

    public static final String TABLENAME = "BANNER_INFO";

    /**
     * Properties of entity BannerInfo.<br/>
     * Can be used for QueryBuilder and for referencing column names.
     */
    public static class Properties {
        public final static Property Id = new Property(0, Long.class, "id", true, "_id");
        public final static Property Sid = new Property(1, String.class, "sid", false, "SID");
        public final static Property Sname = new Property(2, String.class, "sname", false, "SNAME");
        public final static Property Simg = new Property(3, String.class, "simg", false, "SIMG");
    }


    public BannerInfoDao(DaoConfig config) {
        super(config);
    }
    
    public BannerInfoDao(DaoConfig config, DaoSession daoSession) {
        super(config, daoSession);
    }

    /** Creates the underlying database table. */
    public static void createTable(Database db, boolean ifNotExists) {
        String constraint = ifNotExists? "IF NOT EXISTS ": "";
        db.execSQL("CREATE TABLE " + constraint + "\"BANNER_INFO\" (" + //
                "\"_id\" INTEGER PRIMARY KEY ," + // 0: id
                "\"SID\" TEXT," + // 1: sid
                "\"SNAME\" TEXT," + // 2: sname
                "\"SIMG\" TEXT);"); // 3: simg
    }

    /** Drops the underlying database table. */
    public static void dropTable(Database db, boolean ifExists) {
        String sql = "DROP TABLE " + (ifExists ? "IF EXISTS " : "") + "\"BANNER_INFO\"";
        db.execSQL(sql);
    }

    @Override
    protected final void bindValues(DatabaseStatement stmt, BannerInfo entity) {
        stmt.clearBindings();
 
        Long id = entity.getId();
        if (id != null) {
            stmt.bindLong(1, id);
        }
 
        String sid = entity.getSid();
        if (sid != null) {
            stmt.bindString(2, sid);
        }
 
        String sname = entity.getSname();
        if (sname != null) {
            stmt.bindString(3, sname);
        }
 
        String simg = entity.getSimg();
        if (simg != null) {
            stmt.bindString(4, simg);
        }
    }

    @Override
    protected final void bindValues(SQLiteStatement stmt, BannerInfo entity) {
        stmt.clearBindings();
 
        Long id = entity.getId();
        if (id != null) {
            stmt.bindLong(1, id);
        }
 
        String sid = entity.getSid();
        if (sid != null) {
            stmt.bindString(2, sid);
        }
 
        String sname = entity.getSname();
        if (sname != null) {
            stmt.bindString(3, sname);
        }
 
        String simg = entity.getSimg();
        if (simg != null) {
            stmt.bindString(4, simg);
        }
    }

    @Override
    public Long readKey(Cursor cursor, int offset) {
        return cursor.isNull(offset + 0) ? null : cursor.getLong(offset + 0);
    }    

    @Override
    public BannerInfo readEntity(Cursor cursor, int offset) {
        BannerInfo entity = new BannerInfo( //
            cursor.isNull(offset + 0) ? null : cursor.getLong(offset + 0), // id
            cursor.isNull(offset + 1) ? null : cursor.getString(offset + 1), // sid
            cursor.isNull(offset + 2) ? null : cursor.getString(offset + 2), // sname
            cursor.isNull(offset + 3) ? null : cursor.getString(offset + 3) // simg
        );
        return entity;
    }
     
    @Override
    public void readEntity(Cursor cursor, BannerInfo entity, int offset) {
        entity.setId(cursor.isNull(offset + 0) ? null : cursor.getLong(offset + 0));
        entity.setSid(cursor.isNull(offset + 1) ? null : cursor.getString(offset + 1));
        entity.setSname(cursor.isNull(offset + 2) ? null : cursor.getString(offset + 2));
        entity.setSimg(cursor.isNull(offset + 3) ? null : cursor.getString(offset + 3));
     }
    
    @Override
    protected final Long updateKeyAfterInsert(BannerInfo entity, long rowId) {
        entity.setId(rowId);
        return rowId;
    }
    
    @Override
    public Long getKey(BannerInfo entity) {
        if(entity != null) {
            return entity.getId();
        } else {
            return null;
        }
    }

    @Override
    public boolean hasKey(BannerInfo entity) {
        return entity.getId() != null;
    }

    @Override
    protected final boolean isEntityUpdateable() {
        return true;
    }
    
}
