package servicos;

import java.util.List;
import java.util.stream.Collectors;

import entidades.*;
import enums.CategoriaVeiculo;
import enums.StatusCorrida;
import enums.StatusMotorista;
import excecoes.*;

public class GerenciadorDeCorridas {
    private Repositorio<Motorista> repoMotoristas;
    private Repositorio<Passageiro> repoPassageiros;
    private Repositorio<Corrida> repoCorridas;

    public GerenciadorDeCorridas() {
        this.repoMotoristas = new Repositorio<>();
        this.repoPassageiros = new Repositorio<>();
        this.repoCorridas = new Repositorio<>();
    }


    public double simularPreco(double km, CategoriaVeiculo cat) {
        return cat.getTarifaBase() + (km * cat.getMultiplicadorKm());
    }

    public List<Corrida> buscarCorridasSolicitadasPorCategoria(Motorista motorista) {
        CategoriaVeiculo catMotorista = motorista.getVeiculo().getCategoria();
        
        return repoCorridas.listarTodos().stream()
                .filter(c -> c.getStatus() == StatusCorrida.SOLICITADA)
                .filter(c -> c.getCategoriaSolicitada() == catMotorista)
                .collect(Collectors.toList());
    }

    public Corrida buscarCorridaAtivaPassageiro(Passageiro p) {
        return repoCorridas.listarTodos().stream().filter(c -> c.getPassageiro() == p && 
               c.getStatus() != StatusCorrida.CONCLUIDA && c.getStatus() != StatusCorrida.PAGA && c.getStatus() != StatusCorrida.CANCELADA).findFirst().orElse(null);
    }
    
    public Corrida buscarCorridaAtivaMotorista(Motorista m) {
        return repoCorridas.listarTodos().stream().filter(c -> c.getMotorista() == m && 
               (c.getStatus() == StatusCorrida.ACEITA || c.getStatus() == StatusCorrida.EM_ANDAMENTO)).findFirst().orElse(null);
    }
    
    public Corrida solicitarCorrida(Passageiro p, String o, String d, double km, CategoriaVeiculo cat) throws Exception {
        if (km <= 0) throw new DistanciaInvalidaException("Km inválido.");
        if (p.isBloqueado()) throw new PassageiroPendenteException("Usuário bloqueado. Pague seus débitos.");
        if (p.getMetodoPagamento() == null) throw new Exception("Sem pagamento configurado.");
        if (buscarCorridaAtivaPassageiro(p) != null) throw new Exception("Já existe corrida ativa.");
        
        boolean haMotoristaCompativel = repoMotoristas.listarTodos().stream()
                .filter(m -> m.getStatus() == StatusMotorista.ONLINE)
                .anyMatch(m -> m.getVeiculo().getCategoria() == cat);
        
        if (!haMotoristaCompativel) throw new NenhumMotoristaDisponivelException("Nenhum motorista " + cat + " online.");
        
        Corrida c = new Corrida(p, o, d, km, cat);
        repoCorridas.adicionar(c);
        return c;
    }

    public void aceitarCorrida(Corrida c, Motorista m) {
        if (c.getStatus() != StatusCorrida.SOLICITADA) throw new EstadoInvalidoDaCorridaException("Indisponível.");
        
        if (c.getCategoriaSolicitada() != m.getVeiculo().getCategoria()) {
            throw new RuntimeException("Você não pode aceitar corridas de outra categoria!");
        }

        c.setMotorista(m);
        c.setStatus(StatusCorrida.ACEITA);
        m.entrarEmCorrida();
    }
    
    
    public void cadastrarMotorista(Motorista m) throws UsuarioJaCadastradoException {
        if(buscarMotoristaPorEmail(m.getEmail())!=null) throw new UsuarioJaCadastradoException("Email existe."); repoMotoristas.adicionar(m);
    }
    public void cadastrarPassageiro(Passageiro p) throws UsuarioJaCadastradoException {
        if(buscarPassageiroPorEmail(p.getEmail())!=null) throw new UsuarioJaCadastradoException("Email existe."); repoPassageiros.adicionar(p);
    }
    public Motorista buscarMotoristaPorEmail(String e) { return repoMotoristas.listarTodos().stream().filter(m->m.getEmail().equals(e)).findFirst().orElse(null); }
    public Passageiro buscarPassageiroPorEmail(String e) { return repoPassageiros.listarTodos().stream().filter(p->p.getEmail().equals(e)).findFirst().orElse(null); }
    
    public void iniciarViagem(Corrida c) { c.setStatus(StatusCorrida.EM_ANDAMENTO); }
    
    public void finalizarViagem(Corrida c) {
        c.setStatus(StatusCorrida.CONCLUIDA);
        try{c.getMotorista().ficarOnline();}catch(Exception e){}
        try {
            c.getPassageiro().getMetodoPagamento().processarPagamento(c.getValorTotal());
            c.setStatus(StatusCorrida.PAGA);
        } catch (Exception e) {
            c.getPassageiro().setBloqueado(true);
        }
    }
    public void cancelarCorrida(Corrida c) { c.setStatus(StatusCorrida.CANCELADA); }
    
    public List<Corrida> buscarCorridasPendentesDeAvaliacao(Usuario u) {
        return repoCorridas.listarTodos().stream().filter(c -> c.getStatus() == StatusCorrida.PAGA).filter(c -> {
            if (u instanceof Passageiro) return c.getPassageiro().equals(u) && !c.isAvaliacaoDoPassageiroFeita();
            else if (u instanceof Motorista) return c.getMotorista().equals(u) && !c.isAvaliacaoDoMotoristaFeita();
            return false;
        }).collect(Collectors.toList());
    }
    
    public void processarAvaliacao(Corrida c, Usuario u, int nota) throws Exception {
        if (u instanceof Passageiro) { c.getMotorista().adicionarAvaliacao(nota); c.setAvaliacaoDoPassageiroFeita(true); }
        else { c.getPassageiro().adicionarAvaliacao(nota); c.setAvaliacaoDoMotoristaFeita(true); }
    }
    
    public void regularizarPassageiro(Passageiro p) { p.setBloqueado(false); }
}