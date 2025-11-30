package entidades;

import enums.StatusMotorista;
import excecoes.MotoristaInvalidoException;

public class Motorista extends Usuario {
    private String cnh;
    private Veiculo veiculo;
    private StatusMotorista status;

    public Motorista(String nome, String cpf, String email, String telefone, String senha, String cnh, Veiculo veiculo) {
        super(nome, cpf, email, telefone, senha);
        this.cnh = cnh;
        this.veiculo = veiculo;
        this.status = StatusMotorista.OFFLINE;
    }

    public void ficarOnline() throws MotoristaInvalidoException {
        if (cnh == null || cnh.isEmpty()) throw new MotoristaInvalidoException("CNH Inválida.");
        if (veiculo == null) throw new MotoristaInvalidoException("Sem veículo.");
        this.status = StatusMotorista.ONLINE;
    }

    public void ficarOffline() { this.status = StatusMotorista.OFFLINE; }
    public void entrarEmCorrida() { this.status = StatusMotorista.EM_CORRIDA; }
    
    public void setVeiculo(Veiculo novoVeiculo) { this.veiculo = novoVeiculo; }

    public StatusMotorista getStatus() { return status; }
    public Veiculo getVeiculo() { return veiculo; }
    public String getCnh() { return cnh; }

    @Override
    public String toString() {
        return "Motorista: " + getNome() + " | " + status;
    }
}