package com.example.jime.smscfe;

public class datosFactura {
    String rpu,importe,telefono,status;

    public datosFactura(){
        super();
    }

    public datosFactura(String RPU,String IMPORTE, String TELEFONO, String STATUS){
        this.rpu = RPU;
        this.importe = IMPORTE;
        this.telefono = TELEFONO;
        this.status = STATUS;
    }

    public String getRpu() {
        return rpu;
    }

    public void setRpu(String rpu) {
        this.rpu = rpu;
    }

    public String getImporte() {
        return importe;
    }

    public void setImporte(String importe) {
        this.importe = importe;
    }

    public String getTelefono() {
        return telefono;
    }

    public void setTelefono(String telefono) {
        this.telefono = telefono;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
