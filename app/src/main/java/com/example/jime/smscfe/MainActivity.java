package com.example.jime.smscfe;

import android.app.Activity;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
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
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        buscar = (Button)findViewById(R.id.btBuscar);
        iniciar = (Button)findViewById(R.id.btIniciar);
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

            }
        });

    }

    public void explorador() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("text/plain");
        startActivityForResult(Intent.createChooser(intent, "Choose File"), VALOR_RETORNO);
    }
    public void leerTxt(String ruta) {
        ArrayList<datosFactura> datos = new ArrayList<>();
        String linea = "";
        String lineas[] = new String[10];
        int conta = 0;
        String re = "";
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

                datosFactura contenido = new datosFactura(rpu,importe,tel,"Pendiente");
                datos.add(contenido);
            }
            FacturasAdapter adapter = new FacturasAdapter(this,datos);
            lista.setAdapter(adapter);
        } catch (Exception e) {
            System.out.println("Excepcion: " + e.toString());
        }
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
