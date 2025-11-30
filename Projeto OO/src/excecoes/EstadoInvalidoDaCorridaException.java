package excecoes;

// RuntimeException pois representa erro grave de lógica de fluxo
public class EstadoInvalidoDaCorridaException extends RuntimeException {
    private static final long serialVersionUID = 1L;

    public EstadoInvalidoDaCorridaException(String message) {
        super(message);
    }
}