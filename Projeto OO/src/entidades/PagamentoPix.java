package entidades;

public class PagamentoPix implements MetodoPagamento {
    @Override
    public void processarPagamento(double valor) {
        System.out.println("Pagamento de R$ " + String.format("%.2f", valor) + " processado via PIX.");
    }
}