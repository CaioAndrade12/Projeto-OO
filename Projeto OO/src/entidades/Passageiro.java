package entidades;

public class Passageiro extends Usuario {
    private MetodoPagamento metodoPagamento;
    private boolean estaBloqueado;

    public Passageiro(String nome, String cpf, String email, String telefone, String senha) {
        super(nome, cpf, email, telefone, senha);
        this.estaBloqueado = false;
    }

    public void setMetodoPagamento(MetodoPagamento metodoPagamento) {
        this.metodoPagamento = metodoPagamento;
    }
    
    public void setBloqueado(boolean bloqueado) {
        this.estaBloqueado = bloqueado;
    }
    
    public boolean isBloqueado() {
        return estaBloqueado;
    }

    public MetodoPagamento getMetodoPagamento() {
        return metodoPagamento;
    }
    
    @Override
    public String toString() {
    	return "Passageiro: " + getNome() + (estaBloqueado ? " [BLOQUEADO]" : "");
    }
}