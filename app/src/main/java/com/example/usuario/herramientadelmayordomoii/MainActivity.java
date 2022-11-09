package com.example.usuario.herramientadelmayordomoii;

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

import com.example.usuario.herramientadelmayordomoii.Fragments.AjustesFragment;
import com.example.usuario.herramientadelmayordomoii.Fragments.ClienteFragment;
import com.example.usuario.herramientadelmayordomoii.Fragments.ClientesFragment;
import com.example.usuario.herramientadelmayordomoii.Fragments.EstanciaFragment;
import com.example.usuario.herramientadelmayordomoii.Fragments.EstanciasFragment;
import com.example.usuario.herramientadelmayordomoii.Fragments.FamilyNameFragment;
import com.example.usuario.herramientadelmayordomoii.Fragments.FamilyNamesFragment;
import com.example.usuario.herramientadelmayordomoii.Fragments.FrontFragment;
import com.example.usuario.herramientadelmayordomoii.Fragments.ReporteFragment;
import com.example.usuario.herramientadelmayordomoii.Interfaces.IMyFragments;
import com.example.usuario.herramientadelmayordomoii.Util.MyApp;

//import android.support.design.widget.FloatingActionButton;
//import android.support.design.widget.Snackbar;
//import android.view.View;


public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, ClientesFragment.Callback, EstanciasFragment.CallBack, ClienteFragment.Callback,
                    EstanciaFragment.Callback, FamilyNamesFragment.Callback, FamilyNameFragment.CallBack, FrontFragment.Callback, ReporteFragment.CallBack{

    public static final int REQUEST_CODE_PERMISSION_WRITE_EXTERNAL_EXTORAGE = 0;

    private DrawerLayout drawer;
    private NavigationView navigationView;

    private int currentStateClienteFragment;
    private int currentStateFamilyNameFragment;
    private int currentStateEstanciaFragment;
    private int currentStateReporteFragment;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        //getApplicationContext().deleteDatabase(AdminSQLiteOpenHelper.BD_NAME);

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
            //setUpEstanciasFragment();
            //RelativeLayout rl = (RelativeLayout)findViewById(R.id.content_main);
            //rl.setBackgroundResource(R.drawable.royalton_bg);
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,new FrontFragment(),FrontFragment.TAG).addToBackStack(null).commit();
        }
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

    private void setUpEstanciasFragment() {
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
        Bundle info = new Bundle();
        info.putLong("estanciaId",estanciaId);
        info.putLong("reporteId",reporteId);
        ReporteFragment repFragment = new ReporteFragment();
        repFragment.setArguments(info);
        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,repFragment,ReporteFragment.TAG).addToBackStack(null).commit();
        currentStateReporteFragment = MyApp.STATE_NEW;
        udActivity(ReporteFragment.TAG);
    }

    private void setUpAjustesFragment(){
        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new AjustesFragment()).addToBackStack(null).commit();
        udActivity(AjustesFragment.TAG);
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

    public void udActivity(String fragmentTag){
        String title = "";
        switch(fragmentTag){
            case EstanciasFragment.TAG:
                title = getResources().getString(R.string.en_casa);
                navigationView.setCheckedItem(R.id.nav_item_estancias);
                break;
            case EstanciasFragment.STATE_EN_CASA:
                title = getResources().getString(R.string.en_casa);
                navigationView.setCheckedItem(R.id.nav_item_estancias);
                break;
            case EstanciasFragment.STATE_SEGUN_PERIODO:
                title = getResources().getString(R.string.segun_periodo);
                navigationView.setCheckedItem(R.id.nav_item_estancias);
                break;
            case EstanciasFragment.STATE_SEGUN_CLIENTE:
                title = getResources().getString(R.string.segun_cliente);
                navigationView.setCheckedItem(R.id.nav_item_estancias);
                break;
            case EstanciasFragment.STATE_SEGUN_HAB:
                title = getResources().getString(R.string.segun_hab);
                navigationView.setCheckedItem(R.id.nav_item_estancias);
                break;
            case EstanciaFragment.TAG:
                if(currentStateEstanciaFragment==MyApp.STATE_REGULAR){title = getResources().getString(R.string.estancia);}
                else if(currentStateEstanciaFragment==MyApp.STATE_NEW){title = getResources().getString(R.string.nueva_estancia);}
                else if(currentStateEstanciaFragment==MyApp.STATE_UPDATE){title = getResources().getString(R.string.actualizar_estancia);}
                navigationView.setCheckedItem(R.id.nav_item_estancias);
                break;
            case ClientesFragment.TAG:
                title = getResources().getString(R.string.clientes);
                navigationView.setCheckedItem(R.id.nav_item_clientes);
                break;
            case ClienteFragment.TAG:
                if(currentStateClienteFragment==MyApp.STATE_NEW){title = getResources().getString(R.string.nuevo_cliente);}
                else if(currentStateClienteFragment==MyApp.STATE_REGULAR){title = getResources().getString(R.string.info_cliente_title);}
                else if(currentStateClienteFragment==MyApp.STATE_UPDATE){title = getResources().getString(R.string.actualizar_cliente_title);}
                navigationView.setCheckedItem(R.id.nav_item_clientes);
                break;
            case FamilyNamesFragment.TAG:
                title = getResources().getString(R.string.familias);
                navigationView.setCheckedItem(R.id.nav_item_clientes);
                break;
            case FamilyNameFragment.TAG:
                if(currentStateFamilyNameFragment==MyApp.STATE_REGULAR){title = getResources().getString(R.string.familia);}
                else if(currentStateFamilyNameFragment==MyApp.STATE_NEW){title = getResources().getString(R.string.nuevo_nombre_de_familia);}
                else if(currentStateFamilyNameFragment==MyApp.STATE_UPDATE){title = getResources().getString(R.string.editar_nombre_de_familia);}
                navigationView.setCheckedItem(R.id.nav_item_clientes);
                break;
            case ReporteFragment.TAG:
                if(currentStateReporteFragment==MyApp.STATE_REGULAR){title = getResources().getString(R.string.reportes);}
                else if(currentStateReporteFragment==MyApp.STATE_NEW){title = getResources().getString(R.string.new_reporte);}
                navigationView.setCheckedItem(R.id.nav_item_estancias);
                break;
            case AjustesFragment.TAG:
                title = getString(R.string.ajustes);
                navigationView.setCheckedItem(R.id.nav_item_ajustes);
                break;
            case FrontFragment.TAG:
                title = getResources().getString(R.string.app_name);
                for(int i=0; i<navigationView.getMenu().size(); i++){navigationView.getMenu().getItem(i).setChecked(false);}
                break;
            default:
                title = getResources().getString(R.string.app_name);
                break;
        }
        setTitle(title);
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
                    Toast.makeText(this, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }else{
                super.onBackPressed();
            }
        } else if(getSupportFragmentManager().findFragmentById(R.id.fragment_container) instanceof EstanciaFragment){
            if(currentStateEstanciaFragment==MyApp.STATE_UPDATE){
                try{
                    IMyFragments fragment = (IMyFragments)getSupportFragmentManager().findFragmentById(R.id.fragment_container);
                    fragment.setUpNewState(MyApp.STATE_REGULAR);
                    currentStateEstanciaFragment = MyApp.STATE_REGULAR;
                    udActivity(EstanciaFragment.TAG);
                }catch (ClassCastException e){
                    Toast.makeText(this,e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }else{
                super.onBackPressed();
            }
        }/*else if(getSupportFragmentManager().findFragmentById(R.id.fragment_container) instanceof AjustesFragment){
            System.out.println("En onBackPressed");
            try{
                IRecordatorio fragment = (IRecordatorio)getSupportFragmentManager().findFragmentById(R.id.fragment_container);
                fragment.storePreferencesRecordatorio();
            }catch (ClassCastException e){
                Toast.makeText(this,e.getMessage(),Toast.LENGTH_SHORT).show();
            }
            super.onBackPressed();
        }*/else {
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
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            //Toast.makeText(this, "Settings clicked...", Toast.LENGTH_SHORT).show();
            setUpAjustesFragment();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        // Handle navigation view item clicks here.

        switch (item.getItemId()) {
            case R.id.nav_item_estancias:
                setUpEstanciasFragment();
                break;
            case R.id.nav_item_clientes:
                setUpClientesFragment();
                break;
            case R.id.nav_item_ajustes:
                setUpAjustesFragment();
                break;
            /*
            case R.id.nav_share:
                Toast.makeText(this, "Share clicked..", Toast.LENGTH_SHORT).show();
                break;
            case R.id.nav_send:
                Toast.makeText(this, "Send clicked...", Toast.LENGTH_LONG).show();
                break;*/
        }

        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}
