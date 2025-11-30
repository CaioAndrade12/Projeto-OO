package entidades;

import enums.CategoriaVeiculo;

public class Veiculo {
    private String placa;
    private String modelo;
    private String cor;
    private int ano;
    private CategoriaVeiculo categoria;

    public Veiculo(String placa, String modelo, String cor, int ano, CategoriaVeiculo categoria) {
        this.placa = placa;
        this.modelo = modelo;
        this.cor = cor;
        this.ano = ano;
        this.categoria = categoria;
    }

    public CategoriaVeiculo getCategoria() { return categoria; }
    public String getModelo() { return modelo; }
    public String getPlaca() { return placa; }
    public String getCor() { return cor; }
    public int getAno() { return ano; }

    @Override
    public String toString() {
        return modelo + " - " + placa + " (" + categoria + ")";
    }
}