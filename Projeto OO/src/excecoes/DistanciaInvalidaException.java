package excecoes;

public class DistanciaInvalidaException extends Exception {
    private static final long serialVersionUID = 1L;

    public DistanciaInvalidaException(String message) {
        super(message);
    }
}