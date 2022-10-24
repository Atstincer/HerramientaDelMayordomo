package com.example.usuario.herramientadelmayordomoii;

import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.example.usuario.herramientadelmayordomoii.Fragments.ClienteFragment;
import com.example.usuario.herramientadelmayordomoii.Fragments.ClientesFragment;
import com.example.usuario.herramientadelmayordomoii.Fragments.EstanciaFragment;
import com.example.usuario.herramientadelmayordomoii.Fragments.EstanciasFragment;
import com.example.usuario.herramientadelmayordomoii.Fragments.FamilyNameFragment;
import com.example.usuario.herramientadelmayordomoii.Fragments.FamilyNamesFragment;
import com.example.usuario.herramientadelmayordomoii.Fragments.FrontFragment;
import com.example.usuario.herramientadelmayordomoii.Fragments.RecordatoriosFragment;
import com.example.usuario.herramientadelmayordomoii.Fragments.ReporteFragment;
import com.example.usuario.herramientadelmayordomoii.Interfaces.IMyFragments;

//import android.support.design.widget.FloatingActionButton;
//import android.support.design.widget.Snackbar;
//import android.view.View;


public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, ClientesFragment.Callback, EstanciasFragment.CallBack, ClienteFragment.Callback,
                    EstanciaFragment.Callback, FamilyNamesFragment.Callback, FamilyNameFragment.CallBack, FrontFragment.Callback, ReporteFragment.CallBack{

    private DrawerLayout drawer;
    private NavigationView navigationView;

    private String currentStateClienteFragment;
    private String currentStateFamilyNameFragment;
    private String currentStateEstanciaFragment;
    private String currentStateReporteFragment;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

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
    public String getCurrentStateClienteFragment() {
        return currentStateClienteFragment;
    }

    @Override
    public String getCurrentStateFamilyNameFragment() {
        return currentStateFamilyNameFragment;
    }

    @Override
    public String getCurrentStateEstanciaFragment() {return currentStateEstanciaFragment;}

    @Override
    public void setNewCurrentStateClienteFragment(String newCurrentStateClienteFragment) {
        currentStateClienteFragment = newCurrentStateClienteFragment;
        udActivity(newCurrentStateClienteFragment);
    }

    @Override
    public void setNewCurrentStateFamilyNameFragment(String newState) {
        currentStateFamilyNameFragment = newState;
    }

    @Override
    public void setNewCurrentStateEstanciaFragment(String newState) {
        currentStateEstanciaFragment = newState;
    }

    @Override
    public void setUpNewClientFragment() {
        currentStateClienteFragment = ClienteFragment.STATE_NEW_CLIENTE_MODE;
        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new ClienteFragment(),ClienteFragment.TAG).addToBackStack(null).commit();
    }

    @Override
    public void setUpClienteFragment(int id) {
        currentStateClienteFragment = ClienteFragment.STATE_CLIENTE_MODE;
        ClienteFragment fragment = new ClienteFragment();
        Bundle bundle = new Bundle();
        bundle.putInt("id",id);
        fragment.setArguments(bundle);
        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, fragment,ClienteFragment.TAG).addToBackStack(null).commit();
    }

    @Override
    public void setUpNewEstanciaFragment() {
        currentStateEstanciaFragment = EstanciaFragment.STATE_NEW_ESTANCIA_MODE;
        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new EstanciaFragment(),EstanciaFragment.TAG).addToBackStack(null).commit();
    }

    @Override
    public void setUpEstanciaFragment(int id) {
        currentStateEstanciaFragment = EstanciaFragment.STATE_REGULAR_MODE;
        EstanciaFragment f = new EstanciaFragment();
        Bundle info = new Bundle();
        info.putInt("id",id);
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
        currentStateFamilyNameFragment = FamilyNameFragment.STATE_REGULAR_MODE;
    }

    @Override
    public void setUpNewFamilyNameFragment() {
        currentStateFamilyNameFragment = FamilyNameFragment.STATE_NEW_FAMILY_NAME_MODE;
        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,new FamilyNameFragment(), FamilyNameFragment.TAG).addToBackStack(null).commit();
    }

    @Override
    public void setUpNewReporteFragment(int estanciaId) {
        Bundle info = new Bundle();
        info.putInt("estanciaId",estanciaId);
        ReporteFragment repFragment = new ReporteFragment();
        repFragment.setArguments(info);
        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,repFragment,ReporteFragment.TAG).addToBackStack(null).commit();
        currentStateReporteFragment = ReporteFragment.STATE_NEW_REPORTE_MODE;
        udActivity(ReporteFragment.TAG);
    }

    @Override
    public String getCurrentStateReporteFragment() {
        return currentStateReporteFragment;
    }

    @Override
    public void setCurrentStateReporteFragment(String state) {
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
            case EstanciaFragment.STATE_REGULAR_MODE:
                title = getResources().getString(R.string.estancia);
                navigationView.setCheckedItem(R.id.nav_item_estancias);
                break;
            case EstanciaFragment.STATE_NEW_ESTANCIA_MODE:
                title = getResources().getString(R.string.nueva_estancia);
                navigationView.setCheckedItem(R.id.nav_item_estancias);
                break;
            case EstanciaFragment.STATE_ESTANCIA_UD_MODE:
                title = getResources().getString(R.string.actualizar_estancia);
                navigationView.setCheckedItem(R.id.nav_item_estancias);
                break;
            case ClientesFragment.TAG:
                title = getResources().getString(R.string.clientes);
                navigationView.setCheckedItem(R.id.nav_item_clientes);
                break;
            case ClienteFragment.STATE_NEW_CLIENTE_MODE:
                title = getResources().getString(R.string.nuevo_cliente);
                navigationView.setCheckedItem(R.id.nav_item_clientes);
                break;
            case ClienteFragment.STATE_CLIENTE_MODE:
                title = getResources().getString(R.string.info_cliente_title);
                navigationView.setCheckedItem(R.id.nav_item_clientes);
                break;
            case ClienteFragment.STATE_CLIENTE_UD_MODE:
                title = getResources().getString(R.string.actualizar_cliente_title);
                navigationView.setCheckedItem(R.id.nav_item_clientes);
                break;
            case FamilyNamesFragment.TAG:
                title = getResources().getString(R.string.familias);
                navigationView.setCheckedItem(R.id.nav_item_clientes);
                break;
            case FamilyNameFragment.STATE_REGULAR_MODE:
                title = getResources().getString(R.string.familia);
                navigationView.setCheckedItem(R.id.nav_item_clientes);
                break;
            case FamilyNameFragment.STATE_NEW_FAMILY_NAME_MODE:
                title = getResources().getString(R.string.nuevo_nombre_de_familia);
                navigationView.setCheckedItem(R.id.nav_item_clientes);
                break;
            case FamilyNameFragment.STATE_FAMILY_NAME_UD_MODE:
                title = getResources().getString(R.string.editar_nombre_de_familia);
                navigationView.setCheckedItem(R.id.nav_item_clientes);
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
            case ReporteFragment.TAG:
                if(currentStateReporteFragment.equals(ReporteFragment.STATE_REGULAR_MODE)){
                    title = getResources().getString(R.string.reportes);
                }else if(currentStateReporteFragment.equals(ReporteFragment.STATE_NEW_REPORTE_MODE)){
                    title = getResources().getString(R.string.new_reporte);
                }
                navigationView.setCheckedItem(R.id.nav_item_estancias);
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
            if(currentStateClienteFragment.equals(ClienteFragment.STATE_CLIENTE_UD_MODE)){
                try{
                    IMyFragments fragment = (IMyFragments)getSupportFragmentManager().findFragmentById(R.id.fragment_container);
                    fragment.setUpNewState(ClienteFragment.STATE_CLIENTE_MODE);
                    currentStateClienteFragment = ClienteFragment.STATE_CLIENTE_MODE;
                    udActivity(ClienteFragment.STATE_CLIENTE_MODE);
                }catch(ClassCastException e){
                    Toast.makeText(this, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }else{
                super.onBackPressed();
            }
        } else if(getSupportFragmentManager().findFragmentById(R.id.fragment_container) instanceof FamilyNameFragment){
            if(currentStateFamilyNameFragment.equals(FamilyNameFragment.STATE_FAMILY_NAME_UD_MODE)){
                try{
                    IMyFragments fragment = (IMyFragments)getSupportFragmentManager().findFragmentById(R.id.fragment_container);
                    fragment.setUpNewState(FamilyNameFragment.STATE_REGULAR_MODE);
                    currentStateFamilyNameFragment = FamilyNameFragment.STATE_REGULAR_MODE;
                    udActivity(FamilyNameFragment.STATE_REGULAR_MODE);
                }catch (ClassCastException e){
                    Toast.makeText(this, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }else{
                super.onBackPressed();
            }
        } else if(getSupportFragmentManager().findFragmentById(R.id.fragment_container) instanceof EstanciaFragment){
            if(currentStateEstanciaFragment.equals(EstanciaFragment.STATE_ESTANCIA_UD_MODE)){
                try{
                    IMyFragments fragment = (IMyFragments)getSupportFragmentManager().findFragmentById(R.id.fragment_container);
                    fragment.setUpNewState(EstanciaFragment.STATE_REGULAR_MODE);
                    currentStateEstanciaFragment = EstanciaFragment.STATE_REGULAR_MODE;
                    udActivity(EstanciaFragment.STATE_REGULAR_MODE);
                }catch (ClassCastException e){
                    Toast.makeText(this, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }else{
                super.onBackPressed();
            }
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
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            Toast.makeText(this, "Settings clicked...", Toast.LENGTH_SHORT).show();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.

        switch (item.getItemId()) {
            case R.id.nav_item_estancias:
                setUpEstanciasFragment();
                break;
            case R.id.nav_item_clientes:
                setUpClientesFragment();
                break;
            case R.id.nav_item_recordatorios:
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new RecordatoriosFragment()).addToBackStack(null).commit();
                navigationView.setCheckedItem(R.id.nav_item_recordatorios);
                break;
            case R.id.nav_share:
                Toast.makeText(this, "Share clicked..", Toast.LENGTH_SHORT).show();
                break;
            case R.id.nav_send:
                Toast.makeText(this, "Send clicked...", Toast.LENGTH_LONG).show();
                break;
        }

        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}
