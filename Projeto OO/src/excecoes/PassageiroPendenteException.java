package excecoes;

public class PassageiroPendenteException extends Exception {
    private static final long serialVersionUID = 1L;

    public PassageiroPendenteException(String message) {
        super(message);
    }
}