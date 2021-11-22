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
import com.example.usuario.herramientadelmayordomoii.Fragments.RecordatoriosFragment;
import com.example.usuario.herramientadelmayordomoii.Interfaces.IClienteFragment;

//import android.support.design.widget.FloatingActionButton;
//import android.support.design.widget.Snackbar;
//import android.view.View;


public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, ClientesFragment.Callback, EstanciasFragment.CallBack, ClienteFragment.Callback{

    private DrawerLayout drawer;
    private NavigationView navigationView;

    private String currentStateClienteFragment;


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
            setUpEstanciasFragment();
        }
    }

    @Override
    public void setUpNewClientFragment() {
        currentStateClienteFragment = ClienteFragment.STATE_NEW_CLIENTE_MODE;
        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new ClienteFragment(),ClienteFragment.TAG).addToBackStack(null).commit();
    }

    @Override
    public String getCurrentStateClienteFragment() {
        return currentStateClienteFragment;
    }

    @Override
    public void setNewCurrentStateClienteFragment(String newCurrentStateClienteFragment) {
        currentStateClienteFragment = newCurrentStateClienteFragment;
        udActivity(newCurrentStateClienteFragment);
    }

    @Override
    public void setUpClienteFragment(int id) {
        currentStateClienteFragment = ClienteFragment.STATE_CLIENTE_MODE;
        ClienteFragment fragment = new ClienteFragment();
        Bundle bundle = new Bundle();
        bundle.putInt("id",id);
        fragment.setArguments(bundle);
        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, fragment,ClienteFragment.TAG).addToBackStack(null).commit();
        udActivity(ClienteFragment.STATE_CLIENTE_MODE);
    }

    @Override
    public void setUpNewEstanciaFragment() {
        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new EstanciaFragment()).addToBackStack(EstanciaFragment.TAG).commit();
        udActivity(EstanciaFragment.TAG);
    }

    private void setUpEstanciasFragment() {
        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new EstanciasFragment()).addToBackStack(EstanciasFragment.TAG).commit();
        udActivity(EstanciasFragment.TAG);
    }

    private void setUpClientesFragment() {
        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new ClientesFragment()).addToBackStack(ClientesFragment.TAG).commit();
        udActivity(ClientesFragment.TAG);
    }


    private void udActivity(String fragmentTag){
        String title = "";
        switch(fragmentTag){
            case EstanciasFragment.TAG:
                title = getResources().getString(R.string.en_casa);
                navigationView.setCheckedItem(R.id.nav_item_estancias);
                break;
            case EstanciaFragment.TAG:
                title = getResources().getString(R.string.nueva_estancia);
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
                IClienteFragment fragment = (IClienteFragment)getSupportFragmentManager().findFragmentById(R.id.fragment_container);
                fragment.setUpNewState(ClienteFragment.STATE_CLIENTE_MODE);
                currentStateClienteFragment = ClienteFragment.STATE_CLIENTE_MODE;
                udActivity(ClienteFragment.STATE_CLIENTE_MODE);
            }else{
                super.onBackPressed();
                udActivity(getSupportFragmentManager().getBackStackEntryAt(getSupportFragmentManager().getBackStackEntryCount()-1).getName());
            }
        } else {
            super.onBackPressed();
            udActivity(getSupportFragmentManager().getBackStackEntryAt(getSupportFragmentManager().getBackStackEntryCount()-1).getName());
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


    private void makeToast(String s){
        Toast.makeText(this, s, Toast.LENGTH_SHORT).show();
    }
}
