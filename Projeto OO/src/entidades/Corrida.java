package entidades;

import enums.CategoriaVeiculo;
import enums.StatusCorrida;

public class Corrida {
    private static int contadorIds = 1;
    private int id;
    private Passageiro passageiro;
    private Motorista motorista;
    private String origem;
    private String destino;
    private double distanciaKm;
    private double valorTotal;
    private StatusCorrida status;
    private CategoriaVeiculo categoriaSolicitada; // NOVO ATRIBUTO
    
    private boolean avaliacaoDoPassageiroFeita;
    private boolean avaliacaoDoMotoristaFeita;

    public Corrida(Passageiro passageiro, String origem, String destino, double distanciaKm, CategoriaVeiculo categoriaSolicitada) {
        this.id = contadorIds++;
        this.passageiro = passageiro;
        this.origem = origem;
        this.destino = destino;
        this.distanciaKm = distanciaKm;
        this.categoriaSolicitada = categoriaSolicitada;
        this.status = StatusCorrida.SOLICITADA;
        this.avaliacaoDoPassageiroFeita = false;
        this.avaliacaoDoMotoristaFeita = false;
        
        calcularValorFinal();
    }

    private void calcularValorFinal() {
        double base = this.categoriaSolicitada.getTarifaBase();
        double mult = this.categoriaSolicitada.getMultiplicadorKm();
        this.valorTotal = base + (this.distanciaKm * mult);
    }

    public CategoriaVeiculo getCategoriaSolicitada() { return categoriaSolicitada; }
    
    public boolean isAvaliacaoDoPassageiroFeita() { return avaliacaoDoPassageiroFeita; }
    public void setAvaliacaoDoPassageiroFeita(boolean feita) { this.avaliacaoDoPassageiroFeita = feita; }

    public boolean isAvaliacaoDoMotoristaFeita() { return avaliacaoDoMotoristaFeita; }
    public void setAvaliacaoDoMotoristaFeita(boolean feita) { this.avaliacaoDoMotoristaFeita = feita; }

    public int getId() { return id; }
    public void setMotorista(Motorista motorista) { this.motorista = motorista; }
    public void setStatus(StatusCorrida status) { this.status = status; }
    public StatusCorrida getStatus() { return status; }
    public Motorista getMotorista() { return motorista; }
    public Passageiro getPassageiro() { return passageiro; }
    public double getValorTotal() { return valorTotal; }

    @Override
    public String toString() {
        return String.format("[ID: %d | %s] %s -> %s (R$ %.2f)", 
                id, categoriaSolicitada, origem, destino, valorTotal);
    }
}