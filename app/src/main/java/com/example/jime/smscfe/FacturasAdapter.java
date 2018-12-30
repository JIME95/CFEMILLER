package com.example.jime.smscfe;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;

public class FacturasAdapter extends BaseAdapter {

    ArrayList<datosFactura> datos;
    Activity activity;
    View v;
    public FacturasAdapter(Activity activity, ArrayList<datosFactura> Datos){
        this.activity = activity;
        this.datos = Datos;
    }

    @Override
    public int getCount() {
        return datos.size();
    }

    @Override
    public Object getItem(int position) {
        return datos.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        v = convertView;
        if (convertView == null) {
            LayoutInflater inf = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            v = inf.inflate(R.layout.vistafacturas, null);

        }
        TextView rpu = (TextView)v.findViewById(R.id.txRpu);
        TextView importe = (TextView)v.findViewById(R.id.txImporte);
        TextView tel = (TextView)v.findViewById(R.id.txTel);
        TextView status = (TextView)v.findViewById(R.id.txEstado);

        datosFactura content = datos.get(position);

        rpu.setText(content.getRpu());
        importe.setText("$"+content.getImporte());
        tel.setText(content.getTelefono());
        status.setText(content.getStatus());



        return v;
    }
}
