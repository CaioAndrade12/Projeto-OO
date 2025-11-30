package entidades;

import excecoes.SaldoInsuficienteException;

public class PagamentoDinheiro implements MetodoPagamento {
    private double dinheiroAtual;

    public PagamentoDinheiro(double dinheiroInicial) {
        this.dinheiroAtual = dinheiroInicial;
    }

    @Override
    public void processarPagamento(double valor) throws SaldoInsuficienteException {
        if (this.dinheiroAtual < valor) {
            throw new SaldoInsuficienteException("Dinheiro insuficiente.");
        }
        this.dinheiroAtual -= valor;
        System.out.println("Pagamento pagamento em dinheiro realizado.");
    }
}