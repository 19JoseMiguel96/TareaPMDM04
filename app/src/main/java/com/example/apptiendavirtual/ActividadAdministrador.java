package com.example.apptiendavirtual;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.apptiendavirtual.BaseDeDatos.BaseDatos_pmdm;

public class ActividadAdministrador extends AppCompatActivity {
    private BaseDatos_pmdm baseDatos;
    private SQLiteDatabase operacionesBD;
    String nombre, apellidos, ruta;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_actividad_administrador);
        Toolbar toolbarAdministrador = (Toolbar) findViewById(R.id.toolbarAdministrador);
        setSupportActionBar(toolbarAdministrador);

        if (baseDatos==null){
            baseDatos = new BaseDatos_pmdm(getApplicationContext());
            operacionesBD = baseDatos.getWritableDatabase();
            baseDatos.asigarSQLiteDatabase(operacionesBD);
        }
        Cursor cursor = operacionesBD.rawQuery("select nombre, apellidos, ruta from NombreLogin", null);
        if (cursor.moveToFirst()) {
            while (!cursor.isAfterLast()) {
                nombre =cursor.getString(0);
                apellidos =cursor.getString(1);
                ruta =cursor.getString(2);
                cursor.moveToNext();
            }
        }
        TextView textViewUACabecera = findViewById(R.id.textView13);
        textViewUACabecera.setText(nombre+ " " + apellidos );
        ImageView imgUsu = (ImageView) findViewById(R.id.imageView);
        Bitmap bitmap = BitmapFactory.decodeFile(ruta);
        if(bitmap!=null){
            imgUsu.setImageBitmap(bitmap);
        }
    }
    public void verPedidosPendientes(View v){
        Intent intent = new Intent(this, ActividadTodosLosPedidos.class);
        startActivity(intent);
    }
    public void verPedidosConfirmados(View v){
        Intent intent = new Intent(this, ActividadPedidosConfirmados.class);
        startActivity(intent);
    }
    public void verRechazados(View v){
        Intent intent = new Intent(this, ActividadVerPedidosRechazados.class);
        startActivity(intent);
    }
    public void modificarDatosAdmin(View v){
        Intent intent = new Intent(this, ActividadModificarDatosAdmin.class);
        startActivity(intent);
    }
    public void Salir(View v){
        Toast.makeText(this, "Sesión cerrada.", Toast.LENGTH_LONG).show();
        Intent intent = new Intent(this, TiendaVirtualPrincipal.class);
        startActivity(intent);
        finish();
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.items_actividad_administrador, menu);
        return true;
    }
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.accion_Pedidos:
                Intent intent = new Intent(this,  ActividadTodosLosPedidos.class);
                startActivity(intent);
                return true;

            case R.id.accion_PedidosConfirmados:
                Intent intent2 = new Intent(this, ActividadPedidosConfirmados.class);
                startActivity(intent2);
                return true;
            case R.id.accion_PedidosRechazados:
                Intent intent3 = new Intent(this, ActividadVerPedidosRechazados.class);
                startActivity(intent3);
                return true;
            case R.id.accion_CerrarSesion:
                Toast.makeText(this, "Sesión cerrada.", Toast.LENGTH_LONG).show();
                Intent intent4 = new Intent(this, TiendaVirtualPrincipal.class);
                startActivity(intent4);
                finish();
                return true;

            default:
                // If we got here, the user's action was not recognized.
                // Invoke the superclass to handle it.
                return super.onOptionsItemSelected(item);

        }
    }

}
