package com.example.usuario.herramientadelmayordomo;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.example.usuario.herramientadelmayordomo.Almacenamiento.MySharedPreferences;
import com.example.usuario.herramientadelmayordomo.Entities.Estancia;
import com.example.usuario.herramientadelmayordomo.Entities.MyEmail;
import com.example.usuario.herramientadelmayordomo.Fragments.AjustesFragment;
import com.example.usuario.herramientadelmayordomo.Fragments.ClienteFragment;
import com.example.usuario.herramientadelmayordomo.Fragments.ClientesFragment;
import com.example.usuario.herramientadelmayordomo.Fragments.EstanciaFragment;
import com.example.usuario.herramientadelmayordomo.Fragments.EstanciasFragment;
import com.example.usuario.herramientadelmayordomo.Fragments.FamilyNameFragment;
import com.example.usuario.herramientadelmayordomo.Fragments.FamilyNamesFragment;
import com.example.usuario.herramientadelmayordomo.Fragments.FrontFragment;
import com.example.usuario.herramientadelmayordomo.Fragments.RecordatoriosFragment;
import com.example.usuario.herramientadelmayordomo.Fragments.ReporteFragment;
import com.example.usuario.herramientadelmayordomo.Fragments.ReportesFragment;
import com.example.usuario.herramientadelmayordomo.Interfaces.IMyFragments;
import com.example.usuario.herramientadelmayordomo.Util.MyApp;

import java.util.ArrayList;

//import android.support.design.widget.FloatingActionButton;
//import android.support.design.widget.Snackbar;
//import android.view.View;


public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, ClientesFragment.Callback, EstanciasFragment.CallBack, ClienteFragment.Callback,
                    EstanciaFragment.Callback, FamilyNamesFragment.Callback, FamilyNameFragment.CallBack, FrontFragment.Callback, ReporteFragment.CallBack,
        AjustesFragment.MyCallBack, RecordatoriosFragment.CallBack, ReportesFragment.CallBack {

    public static final int REQUEST_CODE_PERMISSION_WRITE_EXTERNAL_EXTORAGE = 0;

    private DrawerLayout drawer;
    private NavigationView navigationView;

    private int currentStateClienteFragment;
    private int currentStateFamilyNameFragment;
    private int currentStateEstanciaFragment;
    //private int currentStateEstanciasFragment;
    private int currentStateReporteFragment;

    private ArrayList<Integer> estanciasFragmentHistorial;

    private boolean seDioMttoBD;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        seDioMttoBD = false;
        estanciasFragmentHistorial = new ArrayList<>();

        if(savedInstanceState != null) {
            for(String key:savedInstanceState.keySet()){
                switch (key){
                    case "currentStateClienteFragment":
                        currentStateClienteFragment = savedInstanceState.getInt("currentStateClienteFragment");
                        break;
                    case "currentStateEstanciaFragment":
                        currentStateEstanciaFragment = savedInstanceState.getInt("currentStateEstanciaFragment");
                        break;
                    case "currentStateReporteFragment":
                        currentStateReporteFragment = savedInstanceState.getInt("currentStateReporteFragment");
                        break;
                    case "currentStateFamilyNameFragment":
                        currentStateFamilyNameFragment = savedInstanceState.getInt("currentStateFamilyNameFragment");
                        break;
                    case "estanciasFragmentHistorial":
                        estanciasFragmentHistorial = savedInstanceState.getIntegerArrayList("estanciasFragmentHistorial");
                        break;
                    case "seDioMttoBD":
                        seDioMttoBD = savedInstanceState.getBoolean("seDioMttoBD");
                        break;
                }
            }
        }

        //getApplicationContext().deleteDatabase(AdminSQLiteOpenHelper.BD_NAME);

        if(!seDioMttoBD) {
            MyApp.mantenimientoBD(getApplicationContext());
            seDioMttoBD = true;
        }



        /*
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });*/

        drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,new FrontFragment(),FrontFragment.TAG).addToBackStack(null).commit();
        }

        if(getIntent().getExtras()!=null){
            long estanciaId = getIntent().getExtras().getLong(Estancia.KEY_ESTANCIA_ID);
            if(estanciaId>0){
                setUpEstanciaFragment(estanciaId);
                if(getIntent().getExtras().getBoolean(MyEmail.KEY_ENVIAR_INFO)){
                    Estancia estancia = Estancia.getEstanciaFromDB(getApplicationContext(),estanciaId);
                    MyEmail.setUpEmail(getApplicationContext(),new MyEmail(
                            new String[]{MySharedPreferences.getDefaultMail(getApplicationContext())},
                            estancia.getEmailAsunto(),
                            estancia.getEmailBody(getApplicationContext())));
                }
            }
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putInt("currentStateClienteFragment",currentStateClienteFragment);
        outState.putInt("currentStateEstanciaFragment",currentStateEstanciaFragment);
        outState.putInt("currentStateFamilyNameFragment",currentStateFamilyNameFragment);
        outState.putInt("currentStateReporteFragment",currentStateReporteFragment);
        outState.putIntegerArrayList("estanciasFragmentHistorial",estanciasFragmentHistorial);
        outState.putBoolean("seDioMttoBD", seDioMttoBD);
        super.onSaveInstanceState(outState);
    }

    @Override
    public int getCurrentStateClienteFragment() {
        return currentStateClienteFragment;
    }

    @Override
    public int getCurrentStateFamilyNameFragment() {
        return currentStateFamilyNameFragment;
    }

    @Override
    public int getCurrentStateEstanciaFragment() {return currentStateEstanciaFragment;}

    @Override
    public void setNewCurrentStateClienteFragment(int newState) {
        currentStateClienteFragment = newState;
        udActivity(ClienteFragment.TAG);
    }

    @Override
    public void setNewCurrentStateFamilyNameFragment(int newState) {
        currentStateFamilyNameFragment = newState;
    }

    @Override
    public void setNewCurrentStateEstanciaFragment(int newState) {
        currentStateEstanciaFragment = newState;
    }

    @Override
    public void setUpNewClientFragment() {
        currentStateClienteFragment = MyApp.STATE_NEW;
        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new ClienteFragment(),ClienteFragment.TAG).addToBackStack(null).commit();
    }

    @Override
    public void setUpClienteFragment(int id) {
        currentStateClienteFragment = MyApp.STATE_REGULAR;
        ClienteFragment fragment = new ClienteFragment();
        Bundle bundle = new Bundle();
        bundle.putInt("id",id);
        fragment.setArguments(bundle);
        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, fragment,ClienteFragment.TAG).addToBackStack(null).commit();
    }

    @Override
    public void setUpNewEstanciaFragment() {
        currentStateEstanciaFragment = MyApp.STATE_NEW;
        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new EstanciaFragment(),EstanciaFragment.TAG).addToBackStack(null).commit();
    }

    @Override
    public void setUpEstanciaFragment(long id) {
        currentStateEstanciaFragment = MyApp.STATE_REGULAR;
        EstanciaFragment f = new EstanciaFragment();
        Bundle info = new Bundle();
        info.putLong("id",id);
        f.setArguments(info);
        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, f,EstanciaFragment.TAG).addToBackStack(null).commit();
    }

    private void setUpEstanciasFragment(int state) {
        estanciasFragmentHistorial.add(state);
        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new EstanciasFragment(),EstanciasFragment.TAG).addToBackStack(null).commit();
    }

    public void setUpClientesFragment() {
        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new ClientesFragment(),ClientesFragment.TAG).addToBackStack(null).commit();
    }

    @Override
    public void setUpFamilyNamesFragment() {
        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,new FamilyNamesFragment(),FamilyNamesFragment.TAG).addToBackStack(null).commit();
    }


    @Override
    public void setUpFamilyNameFragment(int id) {
        Bundle info = new Bundle();
        info.putInt("id",id);
        FamilyNameFragment fragment = new FamilyNameFragment();
        fragment.setArguments(info);
        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,fragment,FamilyNameFragment.TAG).addToBackStack(null).commit();
        currentStateFamilyNameFragment = MyApp.STATE_REGULAR;
    }

    @Override
    public void setUpNewFamilyNameFragment() {
        currentStateFamilyNameFragment = MyApp.STATE_NEW;
        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,new FamilyNameFragment(), FamilyNameFragment.TAG).addToBackStack(null).commit();
    }

    @Override
    public void setUpReportFragment(long estanciaId, long reporteId) {
        currentStateReporteFragment = MyApp.STATE_NEW;
        Bundle info = new Bundle();
        info.putLong("estanciaId",estanciaId);
        info.putLong("reporteId",reporteId);
        ReporteFragment repFragment = new ReporteFragment();
        repFragment.setArguments(info);
        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,repFragment,ReporteFragment.TAG).addToBackStack(null).commit();
        udActivity(ReporteFragment.TAG);
    }

    private void setUpRecordatoriosFragment(){
        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new RecordatoriosFragment()).addToBackStack(null).commit();
    }

    private void setUpAjustesFragment(){
        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new AjustesFragment()).addToBackStack(null).commit();
    }

    private void setUpReportesFragment(){
        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,new ReportesFragment()).addToBackStack(null).commit();
    }

    @Override
    public int getCurrentStateReporteFragment() {
        return currentStateReporteFragment;
    }

    @Override
    public void setCurrentStateReporteFragment(int state) {
        currentStateReporteFragment = state;
        udActivity(ReporteFragment.TAG);
    }

    @Override
    public int getCurrentStateEstanciasFragment() {
        return estanciasFragmentHistorial.get(estanciasFragmentHistorial.size()-1);
    }


    public void udActivity(String fragmentTag){
        String title = "";
        switch(fragmentTag){
            case EstanciasFragment.TAG:
                int currentState = estanciasFragmentHistorial.get(estanciasFragmentHistorial.size()-1);
                if(currentState==MyApp.STATE_EN_CASA){title = getResources().getString(R.string.en_casa);
                    navigationView.setCheckedItem(R.id.nav_item_en_casa);}
                else if(currentState==MyApp.STATE_SEGUN_PERIODO){title = getResources().getString(R.string.segun_periodo);
                    navigationView.setCheckedItem(R.id.nav_item_segun_periodo);}
                else if(currentState==MyApp.STATE_SEGUN_CLIENTE){title = getResources().getString(R.string.segun_cliente);
                    navigationView.setCheckedItem(R.id.nav_item_segun_cliente);}
                else if(currentState==MyApp.STATE_SEGUN_HAB){title = getResources().getString(R.string.segun_hab);
                    navigationView.setCheckedItem(R.id.nav_item_segun_hab);}
                break;
            case EstanciaFragment.TAG:
                if(currentStateEstanciaFragment==MyApp.STATE_REGULAR){title = getResources().getString(R.string.estancia);unCheckedNavigationView();}
                else if(currentStateEstanciaFragment==MyApp.STATE_NEW){title = getResources().getString(R.string.nueva_estancia);navigationView.setCheckedItem(R.id.nav_item_nueva_estancia);}
                else if(currentStateEstanciaFragment==MyApp.STATE_UPDATE){title = getResources().getString(R.string.actualizar_estancia);unCheckedNavigationView();}
                break;
            case ClientesFragment.TAG:
                title = getResources().getString(R.string.clientes);
                navigationView.setCheckedItem(R.id.nav_item_clientes);
                break;
            case ClienteFragment.TAG:
                if(currentStateClienteFragment==MyApp.STATE_NEW){title = getResources().getString(R.string.nuevo_cliente);navigationView.setCheckedItem(R.id.nav_item_nuevo_cliente);}
                else if(currentStateClienteFragment==MyApp.STATE_REGULAR){title = getResources().getString(R.string.info_cliente_title);navigationView.setCheckedItem(R.id.nav_item_clientes);}
                else if(currentStateClienteFragment==MyApp.STATE_UPDATE){title = getResources().getString(R.string.actualizar_cliente_title);navigationView.setCheckedItem(R.id.nav_item_clientes);}
                break;
            case FamilyNamesFragment.TAG:
                title = getResources().getString(R.string.familias);
                navigationView.setCheckedItem(R.id.nav_item_family_names);
                break;
            case FamilyNameFragment.TAG:
                if(currentStateFamilyNameFragment==MyApp.STATE_REGULAR){title = getResources().getString(R.string.familia);navigationView.setCheckedItem(R.id.nav_item_family_names);}
                else if(currentStateFamilyNameFragment==MyApp.STATE_NEW){title = getResources().getString(R.string.nueva_familia);navigationView.setCheckedItem(R.id.nav_item_nuevo_family_name);}
                else if(currentStateFamilyNameFragment==MyApp.STATE_UPDATE){title = getResources().getString(R.string.editar_nombre_de_familia);navigationView.setCheckedItem(R.id.nav_item_family_names);}
                break;
            case ReporteFragment.TAG:
                if(currentStateReporteFragment==MyApp.STATE_REGULAR){title = getResources().getString(R.string.reportes);}
                else if(currentStateReporteFragment==MyApp.STATE_NEW){title = getResources().getString(R.string.new_reporte);}
                //navigationView.setCheckedItem(R.id.nav_item_estancias);
                break;
            case ReportesFragment.TAG:
                title = getString(R.string.reportes);
                navigationView.setCheckedItem(R.id.nav_item_reportes);
                break;
            case RecordatoriosFragment.TAG:
                title = getString(R.string.recordatorios);
                navigationView.setCheckedItem(R.id.nav_item_recordatorios);
                break;
            case AjustesFragment.TAG:
                title = getString(R.string.ajustes);
                navigationView.setCheckedItem(R.id.nav_item_ajustes);
                break;
            case FrontFragment.TAG:
                title = getResources().getString(R.string.app_name);
                unCheckedNavigationView();
                break;
            default:
                title = getResources().getString(R.string.app_name);
                break;
        }
        setTitle(title);
    }

    private void unCheckedNavigationView(){
        for(int i=0; i<navigationView.getMenu().size(); i++){
            if(navigationView.getMenu().getItem(i).hasSubMenu()){
                for (int y=0; y<navigationView.getMenu().getItem(i).getSubMenu().size(); y++){
                    navigationView.getMenu().getItem(i).getSubMenu().getItem(y).setChecked(false);
                }
            } else {
                navigationView.getMenu().getItem(i).setChecked(false);
            }
        }
    }

    @Override
    public void onBackPressed() {
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else if(getSupportFragmentManager().findFragmentById(R.id.fragment_container) instanceof ClienteFragment){
            if(currentStateClienteFragment==MyApp.STATE_UPDATE){
                try{
                    IMyFragments fragment = (IMyFragments)getSupportFragmentManager().findFragmentById(R.id.fragment_container);
                    fragment.setUpNewState(MyApp.STATE_REGULAR);
                    currentStateClienteFragment = MyApp.STATE_REGULAR;
                    udActivity(ClienteFragment.TAG);
                }catch(ClassCastException e){
                    Toast.makeText(this, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }else{
                super.onBackPressed();
            }
        } else if(getSupportFragmentManager().findFragmentById(R.id.fragment_container) instanceof FamilyNameFragment){
            if(currentStateFamilyNameFragment==MyApp.STATE_UPDATE){
                try{
                    IMyFragments fragment = (IMyFragments)getSupportFragmentManager().findFragmentById(R.id.fragment_container);
                    fragment.setUpNewState(MyApp.STATE_REGULAR);
                    currentStateFamilyNameFragment = MyApp.STATE_REGULAR;
                    udActivity(FamilyNameFragment.TAG);
                }catch (ClassCastException e){
                    Toast.makeText(this,e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }else{
                super.onBackPressed();
            }
        } else if(getSupportFragmentManager().findFragmentById(R.id.fragment_container) instanceof EstanciaFragment){
            if(currentStateEstanciaFragment==MyApp.STATE_UPDATE){
                try{
                    IMyFragments fragment = (IMyFragments)getSupportFragmentManager().findFragmentById(R.id.fragment_container);
                    currentStateEstanciaFragment = MyApp.STATE_REGULAR;
                    fragment.setUpNewState(MyApp.STATE_REGULAR);
                    //udActivity(EstanciaFragment.TAG);
                }catch (ClassCastException e){
                    Toast.makeText(this,e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }else{
                super.onBackPressed();
            }
        } else if(getSupportFragmentManager().findFragmentById(R.id.fragment_container) instanceof EstanciasFragment){
            estanciasFragmentHistorial.remove(estanciasFragmentHistorial.size()-1);
            super.onBackPressed();
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_settings) {
            setUpAjustesFragment();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.nav_item_nueva_estancia:
                setUpNewEstanciaFragment();
                break;
            case R.id.nav_item_en_casa:
                setUpEstanciasFragment(MyApp.STATE_EN_CASA);
                break;
            case R.id.nav_item_segun_cliente:
                setUpEstanciasFragment(MyApp.STATE_SEGUN_CLIENTE);
                break;
            case R.id.nav_item_segun_periodo:
                setUpEstanciasFragment(MyApp.STATE_SEGUN_PERIODO);
                break;
            case R.id.nav_item_segun_hab:
                setUpEstanciasFragment(MyApp.STATE_SEGUN_HAB);
                break;
            case R.id.nav_item_nuevo_cliente:
                setUpNewClientFragment();
                break;
            case R.id.nav_item_clientes:
                setUpClientesFragment();
                break;
            case R.id.nav_item_nuevo_family_name:
                setUpNewFamilyNameFragment();
                break;
            case R.id.nav_item_family_names:
                setUpFamilyNamesFragment();
                break;
            case R.id.nav_item_reportes:
                setUpReportesFragment();
                break;
            case R.id.nav_item_recordatorios:
                setUpRecordatoriosFragment();
                break;
            case R.id.nav_item_ajustes:
                setUpAjustesFragment();
                break;
        }
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}
