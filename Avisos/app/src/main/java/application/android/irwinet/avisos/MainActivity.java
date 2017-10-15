package application.android.irwinet.avisos;

import android.app.Dialog;
import android.database.Cursor;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

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

        mDbAdapter.close();
        lvAvisos.setAdapter(mCursorAdapter);

        lvAvisos.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, final int masterListPosition, long id) {
                //Toast.makeText(MainActivity.this,"pulsado "+ position, Toast.LENGTH_SHORT).show();
                AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                ListView modelListView= new ListView(MainActivity.this);
                String[] modes=new String[]{"Editar Aviso","Borrar Aviso"};
                ArrayAdapter<String> modeAdapter=new ArrayAdapter<String>(MainActivity.this,android.R.layout.simple_list_item_1, android.R.id.text1,modes);
                modelListView.setAdapter(modeAdapter);
                builder.setView(modelListView);
                final Dialog dialog=builder.create();
                dialog.show();

                modelListView.setOnItemClickListener(new AdapterView.OnItemClickListener(){
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id)
                    {
                        //editar aviso
                        if(position==0)
                        {
                            Toast.makeText(MainActivity.this,"editar "+masterListPosition, Toast.LENGTH_SHORT).show();
                        }
                        else //borrar aviso
                        {
                            Toast.makeText(MainActivity.this,"borrar "+masterListPosition, Toast.LENGTH_SHORT).show();
                        }
                        dialog.dismiss();
                    }
                });
            }
        });
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
