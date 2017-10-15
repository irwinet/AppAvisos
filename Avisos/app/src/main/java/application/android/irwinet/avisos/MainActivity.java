package application.android.irwinet.avisos;

import android.database.Cursor;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.ListView;

public class MainActivity extends AppCompatActivity {

    private ListView lvAvisos;
    private AvisosDBAdapter mDbAdapter;
    private AvisosSimpleCursorAdapter mCursorAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        lvAvisos = (ListView) findViewById(R.id.lvAvisos);
        findViewById(R.id.lvAvisos);
        lvAvisos.setDivider(null);
        mDbAdapter=new AvisosDBAdapter(this);
        mDbAdapter.open();

        if(savedInstanceState==null)
        {
            mDbAdapter.deleteAllReminders();
            mDbAdapter.createReminder("Visitar el centro de recogida",true);
            mDbAdapter.createReminder("Enviar los regalos prometidos",false);
            mDbAdapter.createReminder("Hacer la compra semanas",false);
            mDbAdapter.createReminder("Comprobar el correo",false);
        }

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        Cursor cursor=mDbAdapter.fetchALLReminders();

        String[] from = new String[]{
                AvisosDBAdapter.COL_CONTENT
        };

        int[] to =new int[]{
                R.id.tvRow
        };

        mCursorAdapter=new AvisosSimpleCursorAdapter(
                MainActivity.this,R.layout.avisos_row,cursor,from,to,0
        );

        lvAvisos.setAdapter(mCursorAdapter);
        mDbAdapter.close();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        switch (id)
        {
            case R.id.action_new:
                //create ne aviso
                Log.d(getLocalClassName(),"Crear Nuevo Aviso");
                return true;
            case R.id.action_exit:
                finish();
                return true;
            default:
                return false;
        }
    }
}
