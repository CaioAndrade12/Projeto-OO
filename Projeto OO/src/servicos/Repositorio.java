package servicos;

import java.util.ArrayList;
import java.util.List;

public class Repositorio<T> {
    private List<T> itens;

    public Repositorio() {
        this.itens = new ArrayList<>();
    }

    public void adicionar(T item) {
        if (item != null) {
            this.itens.add(item);
        }
    }

    public List<T> listarTodos() {
        return new ArrayList<>(this.itens); 
    }
}