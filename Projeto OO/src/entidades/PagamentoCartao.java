package entidades;

import java.util.Random;
import excecoes.PagamentoRecusadoException;

public class PagamentoCartao implements MetodoPagamento {
    @Override
    public void processarPagamento(double valor) throws PagamentoRecusadoException {
        //20% DE CHANCE DE ERRO
        if (new Random().nextInt(10) < 2) {
            throw new PagamentoRecusadoException("Transação negada pela operadora do cartão.");
        }
        System.out.println("Pagamento de R$ " + String.format("%.2f", valor) + " aprovado no Crédito.");
    }
}