package com.alfaloop.android.alfabridge.db.gen;

import android.database.Cursor;
import android.database.sqlite.SQLiteStatement;

import org.greenrobot.greendao.AbstractDao;
import org.greenrobot.greendao.Property;
import org.greenrobot.greendao.internal.DaoConfig;
import org.greenrobot.greendao.database.Database;
import org.greenrobot.greendao.database.DatabaseStatement;

import com.alfaloop.android.alfabridge.db.util.GreenConverter;
import java.util.List;

import com.alfaloop.android.alfabridge.db.model.DeployRecord;

// THIS CODE IS GENERATED BY greenDAO, DO NOT EDIT.
/** 
 * DAO for table "DEPLOY_RECORD".
*/
public class DeployRecordDao extends AbstractDao<DeployRecord, Long> {

    public static final String TABLENAME = "DEPLOY_RECORD";

    /**
     * Properties of entity DeployRecord.<br/>
     * Can be used for QueryBuilder and for referencing column names.
     */
    public static class Properties {
        public final static Property Id = new Property(0, Long.class, "id", true, "_id");
        public final static Property Uuid = new Property(1, String.class, "uuid", false, "UUID");
        public final static Property Name = new Property(2, String.class, "name", false, "NAME");
        public final static Property Hexprogram = new Property(3, String.class, "hexprogram", false, "HEXPROGRAM");
        public final static Property Type = new Property(4, String.class, "type", false, "TYPE");
        public final static Property UpdateDate = new Property(5, java.util.Date.class, "updateDate", false, "UPDATE_DATE");
        public final static Property SdkVersion = new Property(6, String.class, "sdkVersion", false, "SDK_VERSION");
        public final static Property Version = new Property(7, String.class, "version", false, "VERSION");
        public final static Property Hexicon = new Property(8, String.class, "hexicon", false, "HEXICON");
        public final static Property WebUrl = new Property(9, String.class, "webUrl", false, "WEB_URL");
        public final static Property PlatformType = new Property(10, String.class, "platformType", false, "PLATFORM_TYPE");
        public final static Property InitFiles = new Property(11, String.class, "initFiles", false, "INIT_FILES");
    }

    private final GreenConverter initFilesConverter = new GreenConverter();

    public DeployRecordDao(DaoConfig config) {
        super(config);
    }
    
    public DeployRecordDao(DaoConfig config, DaoSession daoSession) {
        super(config, daoSession);
    }

    /** Creates the underlying database table. */
    public static void createTable(Database db, boolean ifNotExists) {
        String constraint = ifNotExists? "IF NOT EXISTS ": "";
        db.execSQL("CREATE TABLE " + constraint + "\"DEPLOY_RECORD\" (" + //
                "\"_id\" INTEGER PRIMARY KEY AUTOINCREMENT ," + // 0: id
                "\"UUID\" TEXT NOT NULL UNIQUE ," + // 1: uuid
                "\"NAME\" TEXT NOT NULL ," + // 2: name
                "\"HEXPROGRAM\" TEXT NOT NULL ," + // 3: hexprogram
                "\"TYPE\" TEXT NOT NULL ," + // 4: type
                "\"UPDATE_DATE\" INTEGER NOT NULL ," + // 5: updateDate
                "\"SDK_VERSION\" TEXT," + // 6: sdkVersion
                "\"VERSION\" TEXT," + // 7: version
                "\"HEXICON\" TEXT," + // 8: hexicon
                "\"WEB_URL\" TEXT," + // 9: webUrl
                "\"PLATFORM_TYPE\" TEXT," + // 10: platformType
                "\"INIT_FILES\" TEXT);"); // 11: initFiles
    }

    /** Drops the underlying database table. */
    public static void dropTable(Database db, boolean ifExists) {
        String sql = "DROP TABLE " + (ifExists ? "IF EXISTS " : "") + "\"DEPLOY_RECORD\"";
        db.execSQL(sql);
    }

    @Override
    protected final void bindValues(DatabaseStatement stmt, DeployRecord entity) {
        stmt.clearBindings();
 
        Long id = entity.getId();
        if (id != null) {
            stmt.bindLong(1, id);
        }
        stmt.bindString(2, entity.getUuid());
        stmt.bindString(3, entity.getName());
        stmt.bindString(4, entity.getHexprogram());
        stmt.bindString(5, entity.getType());
        stmt.bindLong(6, entity.getUpdateDate().getTime());
 
        String sdkVersion = entity.getSdkVersion();
        if (sdkVersion != null) {
            stmt.bindString(7, sdkVersion);
        }
 
        String version = entity.getVersion();
        if (version != null) {
            stmt.bindString(8, version);
        }
 
        String hexicon = entity.getHexicon();
        if (hexicon != null) {
            stmt.bindString(9, hexicon);
        }
 
        String webUrl = entity.getWebUrl();
        if (webUrl != null) {
            stmt.bindString(10, webUrl);
        }
 
        String platformType = entity.getPlatformType();
        if (platformType != null) {
            stmt.bindString(11, platformType);
        }
 
        List initFiles = entity.getInitFiles();
        if (initFiles != null) {
            stmt.bindString(12, initFilesConverter.convertToDatabaseValue(initFiles));
        }
    }

    @Override
    protected final void bindValues(SQLiteStatement stmt, DeployRecord entity) {
        stmt.clearBindings();
 
        Long id = entity.getId();
        if (id != null) {
            stmt.bindLong(1, id);
        }
        stmt.bindString(2, entity.getUuid());
        stmt.bindString(3, entity.getName());
        stmt.bindString(4, entity.getHexprogram());
        stmt.bindString(5, entity.getType());
        stmt.bindLong(6, entity.getUpdateDate().getTime());
 
        String sdkVersion = entity.getSdkVersion();
        if (sdkVersion != null) {
            stmt.bindString(7, sdkVersion);
        }
 
        String version = entity.getVersion();
        if (version != null) {
            stmt.bindString(8, version);
        }
 
        String hexicon = entity.getHexicon();
        if (hexicon != null) {
            stmt.bindString(9, hexicon);
        }
 
        String webUrl = entity.getWebUrl();
        if (webUrl != null) {
            stmt.bindString(10, webUrl);
        }
 
        String platformType = entity.getPlatformType();
        if (platformType != null) {
            stmt.bindString(11, platformType);
        }
 
        List initFiles = entity.getInitFiles();
        if (initFiles != null) {
            stmt.bindString(12, initFilesConverter.convertToDatabaseValue(initFiles));
        }
    }

    @Override
    public Long readKey(Cursor cursor, int offset) {
        return cursor.isNull(offset + 0) ? null : cursor.getLong(offset + 0);
    }    

    @Override
    public DeployRecord readEntity(Cursor cursor, int offset) {
        DeployRecord entity = new DeployRecord( //
            cursor.isNull(offset + 0) ? null : cursor.getLong(offset + 0), // id
            cursor.getString(offset + 1), // uuid
            cursor.getString(offset + 2), // name
            cursor.getString(offset + 3), // hexprogram
            cursor.getString(offset + 4), // type
            new java.util.Date(cursor.getLong(offset + 5)), // updateDate
            cursor.isNull(offset + 6) ? null : cursor.getString(offset + 6), // sdkVersion
            cursor.isNull(offset + 7) ? null : cursor.getString(offset + 7), // version
            cursor.isNull(offset + 8) ? null : cursor.getString(offset + 8), // hexicon
            cursor.isNull(offset + 9) ? null : cursor.getString(offset + 9), // webUrl
            cursor.isNull(offset + 10) ? null : cursor.getString(offset + 10), // platformType
            cursor.isNull(offset + 11) ? null : initFilesConverter.convertToEntityProperty(cursor.getString(offset + 11)) // initFiles
        );
        return entity;
    }
     
    @Override
    public void readEntity(Cursor cursor, DeployRecord entity, int offset) {
        entity.setId(cursor.isNull(offset + 0) ? null : cursor.getLong(offset + 0));
        entity.setUuid(cursor.getString(offset + 1));
        entity.setName(cursor.getString(offset + 2));
        entity.setHexprogram(cursor.getString(offset + 3));
        entity.setType(cursor.getString(offset + 4));
        entity.setUpdateDate(new java.util.Date(cursor.getLong(offset + 5)));
        entity.setSdkVersion(cursor.isNull(offset + 6) ? null : cursor.getString(offset + 6));
        entity.setVersion(cursor.isNull(offset + 7) ? null : cursor.getString(offset + 7));
        entity.setHexicon(cursor.isNull(offset + 8) ? null : cursor.getString(offset + 8));
        entity.setWebUrl(cursor.isNull(offset + 9) ? null : cursor.getString(offset + 9));
        entity.setPlatformType(cursor.isNull(offset + 10) ? null : cursor.getString(offset + 10));
        entity.setInitFiles(cursor.isNull(offset + 11) ? null : initFilesConverter.convertToEntityProperty(cursor.getString(offset + 11)));
     }
    
    @Override
    protected final Long updateKeyAfterInsert(DeployRecord entity, long rowId) {
        entity.setId(rowId);
        return rowId;
    }
    
    @Override
    public Long getKey(DeployRecord entity) {
        if(entity != null) {
            return entity.getId();
        } else {
            return null;
        }
    }

    @Override
    public boolean hasKey(DeployRecord entity) {
        return entity.getId() != null;
    }

    @Override
    protected final boolean isEntityUpdateable() {
        return true;
    }
    
}
