package igorluciano.com.br.combustivelflex;

import android.os.Parcel;
import android.os.Parcelable;

public class Posto implements Parcelable {
    private String id;
    private String nome;
    private String bandeira;
    private double latitude;
    private double longitude;
    private double precoGasolinaComum;
    private double precoGasolinaAditivada;
    private double precoEtanol;
    private String atualizadoEm;
    private float distanciaMetros;
    private String rua;
    private String numero;
    private String bairro;
    private String cidade;
    private String estado;

    public Posto() {}

    protected Posto(Parcel in) {
        id = in.readString();
        nome = in.readString();
        bandeira = in.readString();
        latitude = in.readDouble();
        longitude = in.readDouble();
        precoGasolinaComum = in.readDouble();
        precoGasolinaAditivada = in.readDouble();
        precoEtanol = in.readDouble();
        atualizadoEm = in.readString();
        distanciaMetros = in.readFloat();
        rua = in.readString();
        numero = in.readString();
        bairro = in.readString();
        cidade = in.readString();
        estado = in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(id);
        dest.writeString(nome);
        dest.writeString(bandeira);
        dest.writeDouble(latitude);
        dest.writeDouble(longitude);
        dest.writeDouble(precoGasolinaComum);
        dest.writeDouble(precoGasolinaAditivada);
        dest.writeDouble(precoEtanol);
        dest.writeString(atualizadoEm);
        dest.writeFloat(distanciaMetros);
        dest.writeString(rua);
        dest.writeString(numero);
        dest.writeString(bairro);
        dest.writeString(cidade);
        dest.writeString(estado);
    }

    @Override
    public int describeContents() { return 0; }

    public static final Creator<Posto> CREATOR = new Creator<Posto>() {
        @Override public Posto createFromParcel(Parcel in) { return new Posto(in); }
        @Override public Posto[] newArray(int size) { return new Posto[size]; }
    };

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }

    public String getBandeira() { return bandeira; }
    public void setBandeira(String bandeira) { this.bandeira = bandeira; }

    public double getLatitude() { return latitude; }
    public void setLatitude(double latitude) { this.latitude = latitude; }

    public double getLongitude() { return longitude; }
    public void setLongitude(double longitude) { this.longitude = longitude; }

    public double getPrecoGasolinaComum() { return precoGasolinaComum; }
    public void setPrecoGasolinaComum(double preco) { this.precoGasolinaComum = preco; }

    public double getPrecoGasolinaAditivada() { return precoGasolinaAditivada; }
    public void setPrecoGasolinaAditivada(double preco) { this.precoGasolinaAditivada = preco; }

    public double getPrecoEtanol() { return precoEtanol; }
    public void setPrecoEtanol(double preco) { this.precoEtanol = preco; }

    public String getAtualizadoEm() { return atualizadoEm; }
    public void setAtualizadoEm(String atualizadoEm) { this.atualizadoEm = atualizadoEm; }

    public float getDistanciaMetros() { return distanciaMetros; }
    public void setDistanciaMetros(float distanciaMetros) { this.distanciaMetros = distanciaMetros; }

    public String getRua() { return rua; }
    public void setRua(String rua) { this.rua = rua; }

    public String getNumero() { return numero; }
    public void setNumero(String numero) { this.numero = numero; }

    public String getBairro() { return bairro; }
    public void setBairro(String bairro) { this.bairro = bairro; }

    public String getCidade() { return cidade; }
    public void setCidade(String cidade) { this.cidade = cidade; }

    public String getEstado() { return estado; }
    public void setEstado(String estado) { this.estado = estado; }
}
