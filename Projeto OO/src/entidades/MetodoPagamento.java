package entidades;

import excecoes.PagamentoRecusadoException;
import excecoes.SaldoInsuficienteException;

public interface MetodoPagamento {
    void processarPagamento(double valor) throws SaldoInsuficienteException, PagamentoRecusadoException;
}