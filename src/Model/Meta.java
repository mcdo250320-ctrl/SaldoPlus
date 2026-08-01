package Model;

public class Meta {
    private int id;
    private float monto;
    private String fecha;
    private String descrip;
    private int id_usuario;
    
    public Meta(){}
    
    public Meta(float monto, String fecha, String descrip, int id_usuario){
        this.monto = monto;
        this.fecha = fecha;
        this.descrip = descrip;
        this.id_usuario = id_usuario;
    }
    
    public Meta(int id, float monto, String fecha, String descrip, int id_usuario){
        this.id = id;
        this.monto = monto;
        this.fecha = fecha;
        this.descrip = descrip;
        this.id_usuario = id_usuario;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public float getMonto() {
        return monto;
    }

    public void setMonto(float monto) {
        this.monto = monto;
    }

    public String getFecha() {
        return fecha;
    }

    public void setFecha(String fecha) {
        this.fecha = fecha;
    }

    public String getDescrip() {
        return descrip;
    }

    public void setDescrip(String descrip) {
        this.descrip = descrip;
    }

    public int getId_usuario() {
        return id_usuario;
    }

    public void setId_usuario(int id_usuario) {
        this.id_usuario = id_usuario;
    }
    
    @Override
    public String toString() {
        return descrip;
    }
}