package excecoes;

public class UsuarioJaCadastradoException extends Exception {
    private static final long serialVersionUID = 1L;

    public UsuarioJaCadastradoException(String message) {
        super(message);
    }
}