package application.android.irwinet.avisos;

import android.annotation.TargetApi;
import android.app.Dialog;
import android.database.Cursor;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.ActionMode;
import android.view.MenuInflater;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.view.Window;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    private ListView lvAvisos;
    private AvisosDBAdapter mDbAdapter;
    private AvisosSimpleCursorAdapter mCursorAdapter;
    @TargetApi(Build.VERSION_CODES.HONEYCOMB)

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setHomeButtonEnabled(true);
        actionBar.setDisplayShowHomeEnabled(true);
        actionBar.setIcon(R.mipmap.ic_launcher);

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
                        mDbAdapter.open();
                        //editar aviso
                        if(position==0)
                        {
                            int nId = getIdFromPosition(masterListPosition);
                            Aviso aviso = mDbAdapter.fetchReminderById(nId);
                            fireCustomDialog(aviso);
                            Toast.makeText(MainActivity.this,"Se edito correctamente", Toast.LENGTH_SHORT).show();
                        }
                        else //borrar aviso
                        {
                            mDbAdapter.deleteRememberById(getIdFromPosition(masterListPosition));
                            mCursorAdapter.changeCursor(mDbAdapter.fetchALLReminders());
                            Toast.makeText(MainActivity.this,"Se elimino correctamente", Toast.LENGTH_SHORT).show();
                        }
                        mDbAdapter.close();
                        dialog.dismiss();
                    }
                });
            }
        });

        if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.HONEYCOMB)
        {
            lvAvisos.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE_MODAL);
            lvAvisos.setMultiChoiceModeListener(new AbsListView.MultiChoiceModeListener() {
                @Override
                public void onItemCheckedStateChanged(ActionMode mode, int position, long id, boolean checked) { }

                @Override
                public boolean onCreateActionMode(ActionMode mode, Menu menu) {
                    MenuInflater inflater=getMenuInflater();
                    inflater.inflate(R.menu.cam_menu,menu);
                    return true;
                }

                @Override
                public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
                    return false;
                }

                @Override
                public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
                    mDbAdapter.open();
                    switch (item.getItemId())
                    {
                        case R.id.menu_item_delete_aviso:
                            for (int nC=mCursorAdapter.getCount()-1; nC>=0; nC--)
                            {
                                if(lvAvisos.isItemChecked(nC))
                                {
                                    mDbAdapter.deleteRememberById(getIdFromPosition(nC));
                                }
                            }

                            mode.finish();
                            mCursorAdapter.changeCursor(mDbAdapter.fetchALLReminders());
                            return true;
                    }
                    mDbAdapter.close();
                    return false;
                }

                @Override
                public void onDestroyActionMode(ActionMode mode) { }
            });
        }

        mDbAdapter.close();
    }

    private int getIdFromPosition(int nC) {
        return (int)mCursorAdapter.getItemId(nC);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    private void fireCustomDialog(final Aviso aviso)
    {
        final Dialog dialog=new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_custom);

        TextView titleView = (TextView) dialog.findViewById(R.id.custom_title);
        final EditText editCustom = (EditText) dialog.findViewById(R.id.custom_edit_reminder);
        Button commitButton = (Button) dialog.findViewById(R.id.custom_button_commit);
        final CheckBox checkBox = (CheckBox) dialog.findViewById(R.id.custom_check_box);
        LinearLayout rootLayout = (LinearLayout) dialog.findViewById(R.id.custom_root_layout);
        final boolean isEditOperation = (aviso != null);

        //esto es para un edit
        if(isEditOperation)
        {
            titleView.setText("Editar Aviso");
            checkBox.setChecked(aviso.getIntImportant()==1);
            editCustom.setText(aviso.getVarContent());
            rootLayout.setBackgroundColor(getResources().getColor(R.color.azul_neutro));
        }

        commitButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v)
            {
                mDbAdapter.open();
                String reminderText = editCustom.getText().toString();
                if(isEditOperation)
                {
                    Aviso reminderEdited = new Aviso(aviso.getIntAvisoId(), reminderText, checkBox.isChecked()?1:0);
                    mDbAdapter.updateReminder(reminderEdited);
                }//Esto es para un nuevo aviso
                else
                {
                    mDbAdapter.createReminder(reminderText,checkBox.isChecked());
                }

                mCursorAdapter.changeCursor(mDbAdapter.fetchALLReminders());
                mDbAdapter.close();
                dialog.dismiss();
            }
        });

        Button buttonCancel=(Button) dialog.findViewById(R.id.custom_button_cancel);
        buttonCancel.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v)
            {
                dialog.dismiss();
            }
        });

        dialog.show();
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
                //Log.d(getLocalClassName(),"Crear Nuevo Aviso");
                fireCustomDialog(null);
                return true;
            case R.id.action_exit:
                finish();
                return true;
            default:
                return false;
        }
    }
}
