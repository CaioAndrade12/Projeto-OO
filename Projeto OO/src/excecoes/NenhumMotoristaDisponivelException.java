package excecoes;

public class NenhumMotoristaDisponivelException extends Exception {
    private static final long serialVersionUID = 1L;

    public NenhumMotoristaDisponivelException(String message) {
        super(message);
    }
}