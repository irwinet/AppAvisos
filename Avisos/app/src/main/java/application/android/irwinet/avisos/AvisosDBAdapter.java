package application.android.irwinet.avisos;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabaseLockedException;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/**
 * Created by Irwinet on 15/10/2017.
 */

public class AvisosDBAdapter {
    //nombres de las columnas
    public static final String COL_ID="_id";
    public static final String COL_CONTENT="content";
    public static final String COL_IMPORTANT="important";
    //indices correspondientes
    public static final int INDEX_ID=0;
    public static final int INDEX_CONTENT=INDEX_ID+1;
    public static final int INDEX_IMPORTANT=INDEX_ID+2;
    //usado for logging
    private static final String TAG="AvisosDBAdapter";

    private DatabaseHelper mDbHelper;
    private SQLiteDatabase mBd;

    private static final String DATABASE_NAME="dba_remdrs";
    private static final String TABLE_NAME="tbl_remdrs";
    private static final int DATABASE_VERSION=1;

    private final Context mCtx;

    //declaraciones SQL usada para crear la base de datos
    private static final String DATABASE_CREATE =
            "CREATE TABLE if not exists "+TABLE_NAME+" ( "+
                    COL_ID + " INTEGER PRIMARY KEY autoincrement, "+
                    COL_CONTENT + "TEXT, "+
                    COL_IMPORTANT + "INTEGER );";

    public AvisosDBAdapter(Context ctx)
    {
        this.mCtx=ctx;
    }

    //Abrir
    public void open() throws SQLException
    {
        mDbHelper= new DatabaseHelper(mCtx);
        mBd = mDbHelper.getWritableDatabase();
    }
    //Cerrar
    public void close()
    {
        if(mDbHelper!=null)
        {
            mDbHelper.close();
        }
    }

    //CRUD
    //Create
    public void createReminder(String name, boolean important)
    {
        ContentValues values=new ContentValues();
        values.put(COL_CONTENT,name);
        values.put(COL_IMPORTANT,important?1:0);
        mBd.insert(TABLE_NAME,null,values);
    }
    public long createReminder (Aviso reminder) {
        ContentValues values = new ContentValues();
        values.put(COL_CONTENT, reminder.getVarContent());
        values.put(COL_IMPORTANT, reminder.getIntImportant());

        return mBd.insert(TABLE_NAME, null, values);
    }
    //Read
    public Aviso fetchReminderById(int id)
    {
        Cursor cursor=mBd.query(TABLE_NAME,new String[]{COL_ID,COL_CONTENT,COL_IMPORTANT},COL_ID+"=?",
                new String[]{String.valueOf(id)},null,null,null,null);
        if (cursor!=null)
            cursor.moveToFirst();

        return new Aviso(
                cursor.getInt(INDEX_ID),
                cursor.getString(INDEX_CONTENT),
                cursor.getInt(INDEX_IMPORTANT)
        );
    }

    public Cursor fetchALLReminders()
    {
        Cursor mCursor= mBd.query(TABLE_NAME,new String[]{COL_ID,COL_CONTENT,COL_IMPORTANT},null,null,null,null,null);

        if(mCursor!=null)
            mCursor.moveToFirst();

        return mCursor;
    }

    //Update
    public void updateReminder(Aviso reminder)
    {
        ContentValues values=new ContentValues();
        values.put(COL_CONTENT,reminder.getVarContent());
        values.put(COL_IMPORTANT,reminder.getIntImportant());
        mBd.update(TABLE_NAME,values,COL_ID+"=?",new String[]{String.valueOf(reminder.getIntAvisoId())});
    }
    //Delete
    public void deleteRememberById(int nId)
    {
        mBd.delete(TABLE_NAME,COL_ID+"=?",new String[]{String.valueOf(nId)});
    }

    public  void deleteAllReminders()
    {
        mBd.delete(TABLE_NAME,null,null);
    }
    //END CRUD

    public static class DatabaseHelper extends SQLiteOpenHelper
    {
        DatabaseHelper(Context context)
        {
            super(context,DATABASE_NAME,null,DATABASE_VERSION);
        }

        @Override
        public void onCreate(SQLiteDatabase db){
            Log.w(TAG,DATABASE_CREATE);
            db.execSQL(DATABASE_CREATE);
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion)
        {
            Log.w(TAG,"Upgrading database from version "+ oldVersion+" to "
                    + newVersion+", which will destroy all old data");
            db.execSQL("DROP TABLE IF EXISTS "+TABLE_NAME);
            onCreate(db);
        }
    }
}
