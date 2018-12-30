package com.example.jime.smscfe;

import android.Manifest;
import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.InputStreamReader;
import java.net.URISyntaxException;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    private int VALOR_RETORNO = 1;
    Button buscar,iniciar;
    ListView lista;
    public static Activity activity;
    String InfoFacturas [][] = new String[1000][4];
    private final int MY_PERMISSIONS_REQUEST_CAMERA = 1;
    private final int MY_PERMISSIONS_REQUEST_MENSAJE = 2;
    ProgressDialog enviando;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        buscar = (Button)findViewById(R.id.btBuscar);
        iniciar = (Button)findViewById(R.id.btIniciar);
        iniciar.setVisibility(View.INVISIBLE);
        lista = (ListView)findViewById(R.id.ListaFacturas);
        activity = this;

        buscar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                explorador();

            }
        });

        iniciar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                enviando = new ProgressDialog(MainActivity.this);
                enviando.setMessage("Enviando mensajes. Por favor espere...");
                enviando.setIndeterminate(false);
                enviando.setCancelable(false);
                enviando.show();
                IniciaProceso();
            }
        });

        Inicializa();
        permisosAlmacenamiento();
        permisoMensajes();

    }
    public void IniciaProceso(){
        for(int x = 0;x<1000;x++){
            try{
                if(InfoFacturas[x][0].length()>0) {
                    String mensaje = "Su servicio "+InfoFacturas[x][0]+" A sido facturado con un importe " +
                            "de: $"+InfoFacturas[x][1]+" pesos, con vencimiento maximo 3 dias posteriores a este mensaje. Visite www.cfe.com";
                    if(EnviarMensaje(InfoFacturas[x][2],mensaje)) {
                        InfoFacturas[x][3] = "Enviado";
                    }
                }else{
                    break;
                }
            }catch (Exception e){

            }
        }
        rellenaLista();
        enviando.dismiss();
        enviando.cancel();
    }
    private boolean EnviarMensaje (String Numero, String Mensaje){
        try {
            if(Mensaje.length()<=155) {
                SmsManager sms = SmsManager.getDefault();
                sms.sendTextMessage(Numero, null, Mensaje, null, null);
                return true;
            }else{
                Toast.makeText(getApplicationContext(), "MENSAJE NO ENVIADO, EL MENSAJE ES MUY LARGO", Toast.LENGTH_LONG).show();
            }

        }catch (Exception e) {
            Toast.makeText(getApplicationContext(), "ERROR ENVIANDO MENSAJE", Toast.LENGTH_LONG).show();
            e.printStackTrace();
        }
        return false;
    }

    public void Inicializa(){
        for(int x = 0;x<1000;x++){
            InfoFacturas[x][0] = "";
            InfoFacturas[x][1] = "";
            InfoFacturas[x][2] = "";
            InfoFacturas[x][3] = "";
        }
    }

    public void explorador() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("text/plain");
        startActivityForResult(Intent.createChooser(intent, "Choose File"), VALOR_RETORNO);
    }
    public void leerTxt(String ruta) {

        String linea = "";
        int contador = 0;
        try {
            File archivo = new File(ruta);
            BufferedReader txt = new BufferedReader(new InputStreamReader(new FileInputStream(archivo.getAbsolutePath())));
            while ((linea = txt.readLine()) != null) {
                System.out.println(linea);
                String line = linea;
                String rpu = line.substring(0,line.indexOf("-")).trim();
                line = linea.substring(line.indexOf("-")+1, line.length());
                String importe = line.substring(0,line.indexOf("-")).trim();
                String tel = line.substring(line.indexOf("-")+1, line.length()).trim();
                InfoFacturas[contador][0] = rpu;
                InfoFacturas[contador][1] = importe;
                InfoFacturas[contador][2] = tel;
                InfoFacturas[contador][3] = "Pendiente";
                contador++;
            }
            rellenaLista();
            if(contador>0){
                iniciar.setVisibility(View.VISIBLE);
            }
        } catch (Exception e) {
            System.out.println("Excepcion: " + e.toString());
        }
    }
    public void permisosAlmacenamiento(){
        if (ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {

            if (ActivityCompat.shouldShowRequestPermissionRationale(this,Manifest.permission.WRITE_EXTERNAL_STORAGE)) {

            } else {

                ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, MY_PERMISSIONS_REQUEST_CAMERA);

            }
        }
        if (ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {

            if (ActivityCompat.shouldShowRequestPermissionRationale(this,Manifest.permission.READ_EXTERNAL_STORAGE)) {

            } else {

                ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, MY_PERMISSIONS_REQUEST_CAMERA);

            }
        }
    }

    public void permisoMensajes(){
        if (ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.SEND_SMS) != PackageManager.PERMISSION_GRANTED) {

            if (ActivityCompat.shouldShowRequestPermissionRationale(this,Manifest.permission.SEND_SMS)) {

            } else {

                ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.SEND_SMS}, MY_PERMISSIONS_REQUEST_MENSAJE);

            }
        }
        if (ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.SEND_SMS) != PackageManager.PERMISSION_GRANTED) {

            if (ActivityCompat.shouldShowRequestPermissionRationale(this,Manifest.permission.SEND_SMS)) {

            } else {

                ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.SEND_SMS}, MY_PERMISSIONS_REQUEST_MENSAJE);

            }
        }
    }

    public void rellenaLista(){
        ArrayList<datosFactura> datos = new ArrayList<>();
        for(int x = 0;x<1000;x++){
            if(InfoFacturas[x][0].length()>0) {
                datosFactura contenido = new datosFactura(InfoFacturas[x][0], InfoFacturas[x][1], InfoFacturas[x][2], InfoFacturas[x][3]);
                datos.add(contenido);
            }else{
                break;
            }
        }
        FacturasAdapter adapter = new FacturasAdapter(this,datos);
        lista.setAdapter(adapter);
    }

    public static String getFilePath(Context context, Uri uri) throws URISyntaxException {
        String selection = null;
        String[] selectionArgs = null;
        // Uri is different in versions after KITKAT (Android 4.4), we need to
        if (Build.VERSION.SDK_INT >= 19 && DocumentsContract.isDocumentUri(context.getApplicationContext(), uri)) {
            if (isExternalStorageDocument(uri)) {
                final String docId = DocumentsContract.getDocumentId(uri);
                final String[] split = docId.split(":");
                return Environment.getExternalStorageDirectory() + "/" + split[1];
            } else if (isDownloadsDocument(uri)) {
                final String id = DocumentsContract.getDocumentId(uri);
                uri = ContentUris.withAppendedId(
                        Uri.parse("content://downloads/public_downloads"), Long.valueOf(id));
            } else if (isMediaDocument(uri)) {
                final String docId = DocumentsContract.getDocumentId(uri);
                final String[] split = docId.split(":");
                final String type = split[0];
                if ("image".equals(type)) {
                    uri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
                } else if ("video".equals(type)) {
                    uri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
                } else if ("audio".equals(type)) {
                    uri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
                }
                selection = "_id=?";
                selectionArgs = new String[]{
                        split[1]
                };
            }
        }
        if ("content".equalsIgnoreCase(uri.getScheme())) {
            String[] projection = {
                    MediaStore.Images.Media.DATA
            };
            Cursor cursor = null;
            try {
                cursor = context.getContentResolver()
                        .query(uri, projection, selection, selectionArgs, null);
                int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
                if (cursor.moveToFirst()) {
                    return cursor.getString(column_index);
                }
            } catch (Exception e) {
            }
        } else if ("file".equalsIgnoreCase(uri.getScheme())) {
            return uri.getPath();
        }
        return null;
    }

    public static boolean isExternalStorageDocument(Uri uri) {
        return "com.android.externalstorage.documents".equals(uri.getAuthority());
    }

    public static boolean isDownloadsDocument(Uri uri) {
        return "com.android.providers.downloads.documents".equals(uri.getAuthority());
    }

    public static boolean isMediaDocument(Uri uri) {
        return "com.android.providers.media.documents".equals(uri.getAuthority());
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_CANCELED) {
            //Cancelado por el usuario
        }
        if ((resultCode == RESULT_OK) && (requestCode == VALOR_RETORNO)) {
            //Procesar el resultado
            Uri uri = data.getData();
            try {
                String ruta = getFilePath(getApplicationContext(),uri);
                System.out.println("Ruta: "+ruta);
                leerTxt(ruta);

            } catch (URISyntaxException e) {
                e.printStackTrace();
            }
        }
    }


}
