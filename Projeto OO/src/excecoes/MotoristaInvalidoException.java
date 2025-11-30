package excecoes;

public class MotoristaInvalidoException extends Exception {
    private static final long serialVersionUID = 1L;

    public MotoristaInvalidoException(String message) {
        super(message);
    }
}