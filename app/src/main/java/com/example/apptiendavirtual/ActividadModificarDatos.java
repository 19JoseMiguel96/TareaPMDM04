package com.example.apptiendavirtual;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
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

import java.io.File;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import static androidx.core.content.FileProvider.getUriForFile;

public class ActividadModificarDatos extends AppCompatActivity {
    private BaseDatos_pmdm baseDatos;
    private SQLiteDatabase operacionesBD;
    Date fecha = new Date();
    DateFormat formatoFecha = new SimpleDateFormat("HH-mm-ss-dd-MM-yyyy");
    private final int REQUEST_CODE_GRAVACION_IMAXE = 1;
    private final int CODIGO_IDENTIFICADOR=2;
    private static final int SELECT_FILE = 2;
    private String nombreFoto="foto"+formatoFecha.format(fecha)+".jpg";
    File ruta,archivo;
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (resultCode == RESULT_OK) {
            Uri selectedImage;
            switch(requestCode) {
                case REQUEST_CODE_GRAVACION_IMAXE:
                    ruta = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
                    archivo = new File(ruta, nombreFoto);
                    if (!archivo.exists()) return;        // Si no hay foto
                    ImageView imgview = (ImageView) findViewById(R.id.imageView10);
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
                        ImageView mImg = (ImageView) findViewById(R.id.imageView10);
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
                ActivityCompat.requestPermissions(ActividadModificarDatos.this, new String[]{Manifest.permission.CAMERA,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE},CODIGO_IDENTIFICADOR);
            }
        }

    }
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {

        switch (requestCode) {
            case CODIGO_IDENTIFICADOR: {
                Button btn = findViewById(R.id.button31);
                Button btn2 = findViewById(R.id.button32);
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
        setContentView(R.layout.actividad_modificar_datos);
        if (baseDatos==null){
            baseDatos = new BaseDatos_pmdm(getApplicationContext());
            operacionesBD = baseDatos.getWritableDatabase();
            baseDatos.asigarSQLiteDatabase(operacionesBD);
        }
        pedirPermiso();
    }
    public void actualizarDatosCliente (View v) {
        EditText editNom = findViewById(R.id.editText31);
        String nombreUs = editNom.getText().toString();
        EditText editApe = findViewById(R.id.editText32);
        String apellUs = editApe.getText().toString();
        EditText editEmail = findViewById(R.id.editText30);
        String emailUs = editEmail.getText().toString();
        EditText editPws3 = findViewById(R.id.editContra3);
        String nuevaContra = editPws3.getText().toString();
        EditText editPws2 = findViewById(R.id.editContra2);
        String repetirContra = editPws2.getText().toString();
        RadioButton radioCliente = findViewById(R.id.radioButton3);
        RadioButton radioAdministrador = findViewById(R.id.radioButton4);
        String nombre = null, apellidos = null, contraBd = null, email = null, tipoUsuario = "Administrador";
        int indice = 0;
        boolean controlCambios2 = false, controlCambios3 = false;
        Cursor cursor = operacionesBD.rawQuery("select u.nombre, u.apellidos, u.contra, u.email, u._id from Usuarios u join NombreLogin nl on nl.indice=u._id;", null);
        if (cursor.moveToFirst()) {
            while (!cursor.isAfterLast()) {
                nombre = cursor.getString(0);
                apellidos = cursor.getString(1);
                contraBd = cursor.getString(2);
                email = cursor.getString(3);
                indice = cursor.getInt(4);
                cursor.moveToNext();
            }
        }
        Log.i("ARCHIVO", nombre);
        //Objetos para actualizar los datos.
        ContentValues nuevoRegistro = new ContentValues();
        ContentValues nuevoRegistro2 = new ContentValues();
        String condicionwhere = "_id=?";
        String[] parametros = new String[]{String.valueOf(0)};
        String condicionwhere2 = "_id=?";
        String[] parametros2 = new String[]{String.valueOf(indice)};
        if(radioCliente.isChecked() || radioAdministrador.isChecked() || nuevaContra.length()!=0 ||
                repetirContra.length()!=0 || nombreUs.length()!=0 || apellUs.length()!=0 || emailUs.length()!=0 || archivo!=null){
            //Cambiar nombre.
            if(nombreUs.length()!=0 && !nombreUs.equals(nombre)){
                nuevoRegistro.put("nombre", nombreUs);
                nuevoRegistro2.put("nombre", nombreUs);
                controlCambios3 = true;
            }
            else if(nombreUs.equals(nombre)){
                Toast.makeText(this, "Cambio de nombre no guardado. El nombre introducido es igual al actual.",Toast.LENGTH_LONG).show();
            }
            //Cambiar apellidos.
            if(apellUs.length()!=0 && !apellUs.equals(apellidos)){
                nuevoRegistro.put("apellidos", apellUs);
                nuevoRegistro2.put("apellidos", apellUs);
                controlCambios3 = true;
            }
            else if(apellUs.equals(apellidos)){
                Toast.makeText(this, "Cambio de apellidos no guardado. Los apellidos introducidos son iguales a los actuales.",Toast.LENGTH_LONG).show();
            }
            //Cambiar email.
            if(emailUs.length()!=0 && !emailUs.equals(email)){
                nuevoRegistro2.put("email", emailUs);
                controlCambios3 = true;
            }
            else if(emailUs.equals(email)){
                Toast.makeText(this, "Cambio de email no guardado. El email introducido es igual al actual.",Toast.LENGTH_LONG).show();
            }
            //Cambiar contraseña.
            if(nuevaContra.length()!=0 && repetirContra.length()!=0
                    && nuevaContra.equals(repetirContra) && !nuevaContra.equals(contraBd)){
                nuevoRegistro2.put("contra", nuevaContra);
                controlCambios3 = true;

            }
            else if(nuevaContra.length()!=0 && repetirContra.length()==0
                    || nuevaContra.length()==0 && repetirContra.length()!=0){
                Toast.makeText(this, "Cambio de contraseña no guardado. Para cambiarla es necesario repetirla.",Toast.LENGTH_LONG).show();
            }
            else if(!nuevaContra.equals(repetirContra)){
                Toast.makeText(this, "Cambio de contraseña no guardado. Las contraseñas no coinciden.",Toast.LENGTH_LONG).show();
            }
            else if (nuevaContra.equals(contraBd)){
                Toast.makeText(this, "Cambio de contraseña no guardado. La nueva debe ser distinta a la actual.",Toast.LENGTH_LONG).show();
            }
            //Cambiar imágen.
            if(archivo!=null) {
                nuevoRegistro.put("ruta",archivo.toString());
                nuevoRegistro2.put("ruta",archivo.toString());
                controlCambios3 = true;
            }
            //Cambiar tipo de cliente.
            if(radioCliente.isChecked()){
                Toast.makeText(this, "Cambio de tipo usuario no guardado. Ya es un -Cliente-.",Toast.LENGTH_LONG).show();
            }
            else if(radioAdministrador.isChecked()){
                Toast.makeText(this, "El usuario ahora es  -Administrador-.",Toast.LENGTH_SHORT).show();
                nuevoRegistro2.put("tipoCliente", tipoUsuario);
                controlCambios2 = true;
                controlCambios3 = false;
            }
            //Si se cambia el tipo de usuario será necesario volver a logear.
            if(controlCambios2==true){
                operacionesBD.update("Usuarios",nuevoRegistro2,condicionwhere2,parametros2);
                Toast.makeText(this, "¡Modificaciones correctas guardadas! Redirigiendo al menú principal.",Toast.LENGTH_LONG).show();
                Intent intent = new Intent(this, TiendaVirtualPrincipal.class);
                startActivity(intent);
                finish();
            }
            //Si no se cambio el tipo de usuario, se vuelve a la ActividadCliente
            if(controlCambios3==true){
                operacionesBD.update("NombreLogin",nuevoRegistro,condicionwhere,parametros);
                operacionesBD.update("Usuarios",nuevoRegistro2,condicionwhere2,parametros2);
                Toast.makeText(this, "¡Modificaciones correctas guardadas!",Toast.LENGTH_LONG).show();
                Intent intent = new Intent(this, ActividadCliente.class);
                startActivity(intent);
                finish();
            }
        }
        else{
            Toast.makeText(this, "Introduce algún dato nuevo para guardar la modificación.",Toast.LENGTH_LONG).show();
        }
    }
}
