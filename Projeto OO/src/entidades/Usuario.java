package entidades;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import excecoes.NotaInvalidaException;

public abstract class Usuario {
    private String nome;
    private String cpf;
    private String email;
    private String telefone;
    private String senha; 
    private List<Integer> avaliacoes;

    public Usuario(String nome, String cpf, String email, String telefone, String senha) {
        this.nome = nome;
        this.cpf = cpf;
        this.email = email;
        this.telefone = telefone;
        this.senha = senha;
        this.avaliacoes = new ArrayList<>();
    }

    public boolean validarSenha(String senhaEntrada) {
        return this.senha != null && this.senha.equals(senhaEntrada);
    }

    public void adicionarAvaliacao(int nota) throws NotaInvalidaException {
        if (nota < 1 || nota > 5) {
            throw new NotaInvalidaException("A nota deve ser entre 1 e 5. Valor recebido: " + nota);
        }
        this.avaliacoes.add(nota);
    }

    public double getNotaMedia() {
        if (avaliacoes.isEmpty()) return 5.0;
        return avaliacoes.stream().mapToInt(Integer::intValue).average().orElse(0.0);
    }

    public List<Integer> getAvaliacoes() { return Collections.unmodifiableList(avaliacoes); }
    public String getNome() { return nome; }
    public String getEmail() { return email; }
    public String getCpf() { return cpf; }
    public String getTelefone() { return telefone; }
}