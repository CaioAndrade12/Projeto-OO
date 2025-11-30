package excecoes;

public class PagamentoRecusadoException extends Exception {
    private static final long serialVersionUID = 1L;

    public PagamentoRecusadoException(String message) {
        super(message);
    }
}