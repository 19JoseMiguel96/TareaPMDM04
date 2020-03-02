package com.example.apptiendavirtual;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.loader.content.CursorLoader;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.Toast;

import com.example.apptiendavirtual.BaseDeDatos.BaseDatos_pmdm;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import static androidx.core.content.FileProvider.getUriForFile;

public class ActividadRegistrarse extends AppCompatActivity {
    private BaseDatos_pmdm baseDatos;
    private SQLiteDatabase operacionesBD;
    Date fecha = new Date();
    DateFormat formatoFecha = new SimpleDateFormat("HH-mm-ss-dd-MM-yyyy");
    private final int REQUEST_CODE_GRAVACION_IMAXE = 1;
    private final int CODIGO_IDENTIFICADOR=2;
    private static final int SELECT_FILE = 3;
    private String nombreFoto="foto"+formatoFecha.format(fecha)+".jpg";
    File ruta,archivo;
    /**
     * Obtenemos la imágen de la aplicación de Android.
     */
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (resultCode == RESULT_OK) {
            Uri selectedImage;
            switch(requestCode) {
                case REQUEST_CODE_GRAVACION_IMAXE:
                    ruta = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
                    archivo = new File(ruta, nombreFoto);
                    if (!archivo.exists()) return;        // Si no hay foto
                    ImageView imgview = (ImageView) findViewById(R.id.imageView2);
                    Bitmap bitmap = BitmapFactory.decodeFile(archivo.getAbsolutePath());
                    imgview.setImageBitmap(bitmap);
                    break;
                case SELECT_FILE:
                        selectedImage = data.getData();
                        String selectedPath=selectedImage.getPath();

                            if (selectedPath != null) {
                                InputStream imageStream = null;

                                try {
                                    imageStream = getContentResolver().openInputStream(
                                            selectedImage);
                                } catch (FileNotFoundException e) {
                                    e.printStackTrace();
                                }
                                archivo = new File(getPathFromURI(selectedImage));
                                // Transformamos la URI de la imagen a inputStream y este a un Bitmap
                                Bitmap bmp = BitmapFactory.decodeStream(imageStream);

                                // Ponemos nuestro bitmap en un ImageView que tengamos en la vista
                                ImageView mImg = (ImageView) findViewById(R.id.imageView2);
                                mImg.setImageBitmap(bmp);

                            }

                    break;
            }
        }
        else if (resultCode == RESULT_CANCELED) {
            // Foto cancelada
        }
        else {

            // Fallo en la captura de la foto.
        }
    }
    public String getPathFromURI(Uri uri){
        String realPath="";
// SDK < API11
        if (Build.VERSION.SDK_INT < 11) {
            String[] proj = { MediaStore.Images.Media.DATA };
            @SuppressLint("Recycle") Cursor cursor = getContentResolver().query(uri, proj, null, null, null);
            int column_index = 0;
            String result="";
            if (cursor != null) {
                column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
                realPath=cursor.getString(column_index);
            }
        }
        // SDK >= 11 && SDK < 19
        else if (Build.VERSION.SDK_INT < 19){
            String[] proj = { MediaStore.Images.Media.DATA };
            CursorLoader cursorLoader = new CursorLoader(this, uri, proj, null, null, null);
            Cursor cursor = cursorLoader.loadInBackground();
            if(cursor != null){
                int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
                cursor.moveToFirst();
                realPath = cursor.getString(column_index);
            }
        }
        // SDK > 19 (Android 4.4)
        else{
            String wholeID = DocumentsContract.getDocumentId(uri);
            // Split at colon, use second item in the array
            String id = wholeID.split(":")[1];
            String[] column = { MediaStore.Images.Media.DATA };
            // where id is equal to
            String sel = MediaStore.Images.Media._ID + "=?";
            Cursor cursor = getContentResolver().query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, column, sel, new String[]{ id }, null);
            int columnIndex = 0;
            if (cursor != null) {
                columnIndex = cursor.getColumnIndex(column[0]);
                if (cursor.moveToFirst()) {
                    realPath = cursor.getString(columnIndex);
                }
                cursor.close();
            }
        }
        return realPath;
    }
    public void sacarFoto (View v) {
        File ruta = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
        File archivo = new File(ruta,nombreFoto);
        Uri contentUri=null;

        if (Build.VERSION.SDK_INT >= 24) {
            contentUri = getUriForFile(getApplicationContext(), getApplicationContext()
                    .getPackageName() + ".provider", archivo);
        }
        else {
            contentUri = Uri.fromFile(archivo);
        }

        Intent intento = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        intento.putExtra(MediaStore.EXTRA_OUTPUT, contentUri);

        startActivityForResult(intento, REQUEST_CODE_GRAVACION_IMAXE);
    }
    public void abrirGaleria(View v){
        Intent galeriaIntent = new Intent(Intent.ACTION_GET_CONTENT);
        galeriaIntent.setType("image/*");
        startActivityForResult(galeriaIntent, SELECT_FILE);
    }
    public void pedirPermiso(){
        if (Build.VERSION.SDK_INT>=23){
            int permiso = checkSelfPermission(Manifest.permission.CAMERA);
            int permiso2 = checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE);
            if (permiso == PackageManager.PERMISSION_GRANTED
                    && permiso2 == PackageManager.PERMISSION_GRANTED){

            }
            else{
                ActivityCompat.requestPermissions(ActividadRegistrarse.this, new String[]{Manifest.permission.CAMERA,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE},CODIGO_IDENTIFICADOR);
            }
        }

    }
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {

        switch (requestCode) {
            case CODIGO_IDENTIFICADOR: {
                Button btn = findViewById(R.id.button16);
                Button btn2 = findViewById(R.id.button17);
                // Permisos concedidos
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED
                        && grantResults[1] == PackageManager.PERMISSION_GRANTED) {
                    btn.setEnabled(true);
                    btn2.setEnabled(true);

                }
                else {
                    //Desabilitamos los botones de la imágen si falta algún permiso.
                    btn.setEnabled(false);
                    btn2.setEnabled(false);
                    Toast.makeText(this,"Los permisos de cámara y escritura son necesarios añadir una foto de perfil.",Toast.LENGTH_LONG).show();
                }
                return;
            }

        }
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.actividad_registrarse);

        if (baseDatos==null){
            baseDatos = new BaseDatos_pmdm(getApplicationContext());
            operacionesBD = baseDatos.getWritableDatabase();
            baseDatos.asigarSQLiteDatabase(operacionesBD);

        }
        pedirPermiso();

    }
    public void comprobarUsuariosExistentes (View v) {
        EditText editNomR = findViewById(R.id.editText4);
        String nombreUs = editNomR.getText().toString();
        EditText editApeR = findViewById(R.id.editText5);
        String apellUs = editApeR.getText().toString();
        EditText editEmailR = findViewById(R.id.editText7);
        String emailUs = editEmailR.getText().toString();
        EditText editUsu2 = findViewById(R.id.editUsuR);
        String usuarioR = editUsu2.getText().toString();
        EditText editPwsR = findViewById(R.id.editContraR);
        String contraR = editPwsR.getText().toString();
        RadioButton radioCliente = findViewById(R.id.radioButton);
        RadioButton radioAdministrador = findViewById(R.id.radioButton2);
        String tipoCliente;
        String nombre;
        ArrayList<String> todosLosNombresU= new ArrayList<String>();
        String contraBd;
        ArrayList<String> todasLasContras = new ArrayList<String>();
        int id;
        ArrayList<Integer> todosLosId = new ArrayList<Integer>();
        int id1=1;
        int idPost;
        Cursor cursor = operacionesBD.rawQuery("select _id, nombre, contra from Usuarios", null);
        if (cursor.moveToFirst()) {
            while (!cursor.isAfterLast()) {
                id =cursor.getInt(0);
                todosLosId.add(id);
                nombre = cursor.getString(1);
                todosLosNombresU.add(nombre);
                contraBd = cursor.getString(2);
                todasLasContras.add(contraBd);
                cursor.moveToNext();
            }
        }
        if(archivo==null){
            archivo = new File(String.valueOf(ruta));
        }
        if(radioCliente.isChecked() && usuarioR.length()!=0 &&
                contraR.length()!=0 && nombreUs.length()!=0 && apellUs.length()!=0 && emailUs.length()!=0 ||
                radioAdministrador.isChecked() && usuarioR.length()!=0 && contraR.length()!=0
                        && nombreUs.length()!=0 && apellUs.length()!=0 && emailUs.length()!=0 ) {
            if(todosLosNombresU.contains(usuarioR)/*|| usuariosRA.contains(usuarioR)*/) {
                Toast.makeText(this, "El usuario introducido ya existe." +
                        " Inténtelo de nuevo con otro nombre", Toast.LENGTH_LONG).show();
                editUsu2.setText("");
                editPwsR.setText("");
            }
            else{
                if (radioCliente.isChecked()) {
                    ImageView imageview = findViewById(R.id.imageView2);
                    tipoCliente= "Cliente";
                    idPost= todosLosId.size()+ id1;
                    ContentValues nuevoRegistro = new ContentValues();
                    nuevoRegistro.put("_id", idPost);
                    nuevoRegistro.put("usuario",usuarioR);
                    nuevoRegistro.put("contra",contraR);
                    nuevoRegistro.put("nombre",nombreUs);
                    nuevoRegistro.put("apellidos",apellUs);
                    nuevoRegistro.put("email",emailUs);
                    nuevoRegistro.put("tipoCliente",tipoCliente);
                    nuevoRegistro.put("ruta",archivo.toString());
                    operacionesBD.insert("Usuarios", null, nuevoRegistro);
                    //usuariosRC.add(usuarioR);
                    //contrasRC.add(contraR);
                }
                else if(radioAdministrador.isChecked()){
                    tipoCliente= "Administrador";
                    idPost= todosLosId.size()+ id1;
                    ContentValues nuevoRegistro = new ContentValues();
                    nuevoRegistro.put("_id", idPost);
                    nuevoRegistro.put("usuario",usuarioR);
                    nuevoRegistro.put("contra",contraR);
                    nuevoRegistro.put("nombre",nombreUs);
                    nuevoRegistro.put("apellidos",apellUs);
                    nuevoRegistro.put("email",emailUs);
                    nuevoRegistro.put("tipoCliente",tipoCliente);
                    nuevoRegistro.put("ruta",archivo.toString());
                    operacionesBD.insert("Usuarios", null, nuevoRegistro);
                    //usuariosRA.add(usuarioR);
                    //contrasRA.add(contraR);
                }
                Intent intent = new Intent(this, TiendaVirtualPrincipal.class);
                Toast.makeText(this, "¡El usuario -"+ usuarioR+"- ha sido creado con éxito!"
                        ,Toast.LENGTH_LONG).show();
                startActivity(intent);
            }
        }
        else if(usuarioR.length()!=0 &&
                contraR.length()!=0 && nombreUs.length()!=0 && apellUs.length()!=0 && emailUs.length()!=0){
            Toast.makeText(this, "¡Selecciona también el tipo de usuario!",
                    Toast.LENGTH_LONG).show();
        }
        else{
            Toast.makeText(this, "¡Introduce todos los datos!  ",Toast.LENGTH_LONG).show();
        }
    }
}
