package com.example.apptiendavirtual;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.ContentValues;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.example.apptiendavirtual.BaseDeDatos.BaseDatos_pmdm;

import java.util.ArrayList;

public class TiendaVirtualPrincipal extends AppCompatActivity {
    private BaseDatos_pmdm baseDatos;
    private SQLiteDatabase operacionesBD;
    private final int CODIGO_IDENTIFICADOR=2;
    public void pedirPermiso(){
        if (Build.VERSION.SDK_INT>=23){
            int permiso = checkSelfPermission(Manifest.permission.CAMERA);
            int permiso2 = checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE);
            if (permiso == PackageManager.PERMISSION_GRANTED
                    && permiso2 == PackageManager.PERMISSION_GRANTED){

            }
            else{
                ActivityCompat.requestPermissions(TiendaVirtualPrincipal.this, new String[]{Manifest.permission.CAMERA,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE},CODIGO_IDENTIFICADOR);
            }
        }

    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        if (baseDatos==null){
            baseDatos = new BaseDatos_pmdm(getApplicationContext());
            operacionesBD = baseDatos.getWritableDatabase();
            baseDatos.asigarSQLiteDatabase(operacionesBD);
        }
        pedirPermiso();

    }
    protected void onDestroy() {
        baseDatos.close();
        super.onDestroy();
    }

    public void registro (View v){
        Intent intent = new Intent(this, ActividadRegistrarse.class);
        startActivity(intent);
    }

    public void comprobarLogin (View v){

        EditText editUsu = findViewById(R.id.editUsuario);
        String usuario = editUsu.getText().toString();
        EditText editPws = findViewById(R.id.editContra);
        String contra = editPws.getText().toString();
        String nombreUsuario;
        ArrayList<String> todosLosNombresU= new ArrayList<String>();
        String contraBd;
        ArrayList<String> todasLasContras = new ArrayList<String>();
        ArrayList<Integer> todosLosId = new ArrayList<Integer>();
        String cliente="Cliente";
        String administrador ="Administrador";
        String tipoClienteBd, nombre, apellidos, rutas;
        int id;
        ArrayList<String> todosLosTipoCliente = new ArrayList<String>();
        ArrayList<String> todoNombres = new ArrayList<String>();
        ArrayList<String> todoApellidos = new ArrayList<String>();
        ArrayList<String> todoRutas = new ArrayList<String>();

        Cursor cursor = operacionesBD.rawQuery("select _id, usuario, contra, tipoCliente, nombre, apellidos, ruta from Usuarios", null);
        if (cursor.moveToFirst()) {
            while (!cursor.isAfterLast()) {
                id =cursor.getInt(0);
                todosLosId.add(id);
                nombreUsuario = cursor.getString(1);
                todosLosNombresU.add(nombreUsuario);
                contraBd = cursor.getString(2);
                todasLasContras.add(contraBd);
                tipoClienteBd = cursor.getString(3);
                todosLosTipoCliente.add(tipoClienteBd);
                nombre = cursor.getString(4);
                todoNombres.add(nombre);
                apellidos = cursor.getString(5);
                todoApellidos.add(apellidos);
                rutas = cursor.getString(6);
                todoRutas.add(rutas);
                cursor.moveToNext();
             }
        }
        //Si el usuario y contraseña son correctos y el tipo de cliente introducido es "Cliente" se ejecuta la ActividadCliente.
        if(todosLosNombresU.contains(usuario) && contra.equals(todasLasContras.get(todosLosNombresU.indexOf(usuario)))
                && cliente.equals(todosLosTipoCliente.get(todosLosNombresU.indexOf(usuario)))){
            int id2=todosLosId.get(todosLosNombresU.indexOf(usuario));
            String nombre2= todoNombres.get(todosLosNombresU.indexOf(usuario));
            String apellidos2= todoApellidos.get(todosLosNombresU.indexOf(usuario));
            String ruta2= todoRutas.get(todosLosNombresU.indexOf(usuario));
            ContentValues nuevoRegistro = new ContentValues();
            nuevoRegistro.put("nombre", nombre2);
            nuevoRegistro.put("apellidos", apellidos2);
            nuevoRegistro.put("ruta", ruta2);
            nuevoRegistro.put("indice", id2);
            String condicionwhere = "_id=?";
            String[] parametros = new String[]{String.valueOf(0)};
            operacionesBD.update("NombreLogin",nuevoRegistro,condicionwhere,parametros);
            Toast.makeText(this, "Login correcto.", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(this, ActividadCliente.class);
            startActivity(intent);
        }
        //Si el usuario y contraseña son correctos y el tipo de cliente introducido es "Administrador" se ejecuta la ActividadAdministrador.
        else if(todosLosNombresU.contains(usuario) && contra.equals(todasLasContras.get(todosLosNombresU.indexOf(usuario)))
                && administrador.equals(todosLosTipoCliente.get(todosLosNombresU.indexOf(usuario)))){
            int id2=todosLosId.get(todosLosNombresU.indexOf(usuario));
            String nombre2= todoNombres.get(todosLosNombresU.indexOf(usuario));
            String apellidos2= todoApellidos.get(todosLosNombresU.indexOf(usuario));
            String ruta2= todoRutas.get(todosLosNombresU.indexOf(usuario));
            ContentValues nuevoRegistro = new ContentValues();
            nuevoRegistro.put("nombre", nombre2);
            nuevoRegistro.put("apellidos", apellidos2);
            nuevoRegistro.put("ruta", ruta2);
            nuevoRegistro.put("indice", id2);
            String condicionwhere = "_id=?";
            String[] parametros = new String[]{String.valueOf(0)};
            operacionesBD.update("NombreLogin",nuevoRegistro,condicionwhere,parametros);
            Toast.makeText(this, "Login correcto.", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(this, ActividadAdministrador.class);
            startActivity(intent);
        }
        else if(usuario.length()==0 || contra.length()==0) {
            Toast.makeText(this, "Introduce el usuario y contraseña para hacer login.",
                    Toast.LENGTH_LONG).show();
        }
        else {
            Toast.makeText(this, "Usuarios y/o contraseña incorrecto/s. Vuelve a intentarlo.",
                    Toast.LENGTH_LONG).show();
            editUsu.setText("");
            editPws.setText("");
        }
    }
}
