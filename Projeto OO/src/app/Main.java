package app;

import java.util.List;
import java.util.Scanner;
import entidades.*;
import enums.CategoriaVeiculo;
import enums.StatusMotorista;
import excecoes.*;
import servicos.GerenciadorDeCorridas;

public class Main {
    private static Scanner scanner = new Scanner(System.in);
    private static GerenciadorDeCorridas sistema = new GerenciadorDeCorridas();

    public static void main(String[] args) {
        popularDadosIniciais();

        int opcao;
        do {
            System.out.println("1. Login");
            System.out.println("2. Cadastrar");
            System.out.println("0. Sair");
            System.out.print("Opção: ");
            opcao = lerInteiro();

            switch (opcao) {
                case 1: fluxoLogin(); break;
                case 2: fluxoCadastro(); break;
                case 0: System.out.println("Encerrando sistema..."); break;
                default: System.out.println("Opção inválida.");
            }
        } while (opcao != 0);
    }

    //FLUXOS DE ACESSO

    private static void fluxoLogin() {
        System.out.println("\n--- Login ---");
        System.out.print("Email: "); String email = scanner.nextLine();
        System.out.print("Senha: "); String senha = scanner.nextLine();

        Passageiro p = sistema.buscarPassageiroPorEmail(email);
        if (p != null && p.validarSenha(senha)) {
            fluxoSessaoPassageiro(p);
            return;
        }

        Motorista m = sistema.buscarMotoristaPorEmail(email);
        if (m != null && m.validarSenha(senha)) {
            fluxoSessaoMotorista(m);
            return;
        }
        System.out.println("Erro: Credenciais inválidas.");
    }

    private static void fluxoCadastro() {
        System.out.println("\n--- Novo Cadastro ---");
        System.out.println("1. Quero ser Passageiro");
        System.out.println("2. Quero ser Motorista");
        System.out.print("Escolha: ");
        int tipo = lerInteiro();
        
        System.out.print("Nome Completo: "); String n = scanner.nextLine();
        System.out.print("CPF: "); String c = scanner.nextLine();
        System.out.print("Email: "); String e = scanner.nextLine();
        System.out.print("Telefone: "); String t = scanner.nextLine();
        System.out.print("Senha: "); String s = scanner.nextLine();
        
        try {
            if (tipo == 1) {
                Passageiro p = new Passageiro(n, c, e, t, s);
                configurarPagamento(p);
                sistema.cadastrarPassageiro(p); // Lança UsuarioJaCadastradoException
                System.out.println(">> Passageiro cadastrado com sucesso!");
            
            } else if (tipo == 2) {
                System.out.print("CNH: "); String cnh = scanner.nextLine();
                System.out.println("-- Dados do Veículo Obrigatório --");
                System.out.print("Placa: "); String placa = scanner.nextLine();
                System.out.print("Modelo: "); String modelo = scanner.nextLine();
                System.out.print("Cor: "); String cor = scanner.nextLine();
                System.out.print("Ano: "); int ano = lerInteiro();
                System.out.print("Categoria (1-Comum / 2-Luxo): ");
                int catOp = lerInteiro();
                CategoriaVeiculo cat = (catOp == 2) ? CategoriaVeiculo.LUXO : CategoriaVeiculo.COMUM;
                
                Veiculo v = new Veiculo(placa, modelo, cor, ano, cat);
                Motorista m = new Motorista(n, c, e, t, s, cnh, v);
                
                try {
                    m.ficarOnline();
                } catch (MotoristaInvalidoException ex) {
                    System.out.println("Aviso: " + ex.getMessage());
                }
                
                sistema.cadastrarMotorista(m);
                System.out.println(">> Motorista cadastrado com sucesso!");
            } else {
                System.out.println("Tipo inválido.");
            }
        } catch (UsuarioJaCadastradoException ex) {
            System.out.println("ERRO DE CADASTRO: " + ex.getMessage());
        } catch (Exception ex) {
            System.out.println("Erro inesperado: " + ex.getMessage());
        }
    }

    //PASSAGEIRO

    private static void fluxoSessaoPassageiro(Passageiro p) {
        int op;
        do {
            String statusBloqueio = p.isBloqueado() ? " [BLOQUEADO - PENDÊNCIA FINANCEIRA]" : "";
            System.out.printf("\n--- Passageiro: %s%s | Nota: %.1f ★ ---\n", p.getNome(), statusBloqueio, p.getNotaMedia());
            System.out.println("1. Solicitar Corrida");
            System.out.println("2. Ver Corrida Atual / Cancelar");
            System.out.println("3. Avaliar Corridas Pendentes");
            System.out.println("4. Configurar Pagamento");
            
            if (p.isBloqueado()) {
                System.out.println("9. REGULARIZAR PENDÊNCIA (Pagar Débitos)");
            }
            System.out.println("0. Deslogar");
            System.out.print("Opção: ");
            op = lerInteiro();

            switch (op) {
                case 1: realizarSolicitacao(p); break;
                case 2: gerenciarCorridaPassageiro(p); break;
                case 3: menuAvaliacao(p); break;
                case 4: configurarPagamento(p); break;
                case 9: 
                    if (p.isBloqueado()) {
                        sistema.regularizarPassageiro(p);
                        System.out.println(">> Débitos regularizados! Você pode solicitar corridas novamente.");
                    }
                    break;
                case 0: break;
                default: System.out.println("Opção inválida.");
            }
        } while (op != 0);
    }

    private static void realizarSolicitacao(Passageiro p) {
        System.out.print("Origem: "); String origem = scanner.nextLine();
        System.out.print("Destino: "); String destino = scanner.nextLine();
        System.out.print("Km estimados: "); double km = scanner.nextDouble(); scanner.nextLine();

        //SIMULACAO DE PRECOS
        double precoComum = sistema.simularPreco(km, CategoriaVeiculo.COMUM);
        double precoLuxo = sistema.simularPreco(km, CategoriaVeiculo.LUXO);

        System.out.println("\n--- Estimativa de Preço ---");
        System.out.printf("1. Categoria COMUM: R$ %.2f\n", precoComum);
        System.out.printf("2. Categoria LUXO:  R$ %.2f\n", precoLuxo);
        System.out.print("Escolha a categoria (1 ou 2): ");
        int escolha = lerInteiro();
        
        CategoriaVeiculo cat = (escolha == 2) ? CategoriaVeiculo.LUXO : CategoriaVeiculo.COMUM;

        try {
            sistema.solicitarCorrida(p, origem, destino, km, cat);
            System.out.println(">> Solicitação enviada para motoristas da categoria " + cat + "!");
            
        } catch (PassageiroPendenteException e) {
            System.out.println("BLOQUEIO: " + e.getMessage());
            System.out.println("Vá na opção '9' para regularizar.");
            
        } catch (DistanciaInvalidaException e) {
            System.out.println("Erro nos dados: " + e.getMessage());
            
        } catch (NenhumMotoristaDisponivelException e) {
            System.out.println("Indisponibilidade: " + e.getMessage());
            
        } catch (Exception e) {
            System.out.println("Erro genérico: " + e.getMessage());
        }
    }

    private static void gerenciarCorridaPassageiro(Passageiro p) {
        Corrida c = sistema.buscarCorridaAtivaPassageiro(p);
        if (c == null) {
            System.out.println("Nenhuma corrida ativa.");
            return;
        }

        System.out.println("--- Status da Corrida ---");
        System.out.println(c);
        
        if (c.getMotorista() != null) {
            System.out.printf("Motorista: %s (Nota: %.1f ★)\n", 
                    c.getMotorista().getNome(), c.getMotorista().getNotaMedia());
            System.out.println("Veículo: " + c.getMotorista().getVeiculo());
        } else {
            System.out.println("Situação: Aguardando motorista aceitar...");
            System.out.println("1. Cancelar Solicitação | 0. Voltar");
            if (lerInteiro() == 1) {
                try {
                    sistema.cancelarCorrida(c);
                    System.out.println("Solicitação cancelada.");
                } catch (EstadoInvalidoDaCorridaException e) {
                    System.out.println("Erro: " + e.getMessage());
                }
            }
        }
    }

    //MOTORISTA

    private static void fluxoSessaoMotorista(Motorista m) {
        int op;
        do {
            System.out.printf("\n--- Motorista: %s | Veículo: %s | Nota: %.1f ★ ---\n", 
                    m.getNome(), m.getVeiculo().getCategoria(), m.getNotaMedia());
            System.out.println("Status Atual: " + m.getStatus());
            System.out.println("1. Alternar Status (Online/Offline)");
            System.out.println("2. Ver Solicitações (Apenas " + m.getVeiculo().getCategoria() + ")");
            System.out.println("3. Gerenciar Viagem Atual");
            System.out.println("4. Trocar Veículo");
            System.out.println("5. Avaliar Passageiros Pendentes");
            System.out.println("0. Deslogar");
            System.out.print("Opção: ");
            op = lerInteiro();

            switch (op) {
                case 1: 
                    try {
                        if (m.getStatus() == StatusMotorista.OFFLINE) m.ficarOnline();
                        else m.ficarOffline();
                        System.out.println("Status alterado para: " + m.getStatus());
                    } catch (Exception e) {
                        System.out.println("Erro ao alterar status: " + e.getMessage());
                    }
                    break;
                case 2: listarAceitarCorridas(m); break;
                case 3: gerenciarViagemMotorista(m); break;
                case 4: trocarVeiculo(m); break;
                case 5: menuAvaliacao(m); break;
                case 0: break;
                default: System.out.println("Opção inválida.");
            }
        } while (op != 0);
    }

    private static void listarAceitarCorridas(Motorista m) {
        if (m.getStatus() != StatusMotorista.ONLINE) {
            System.out.println("Aviso: Você precisa estar Online para ver chamados.");
            return;
        }

        List<Corrida> lista = sistema.buscarCorridasSolicitadasPorCategoria(m);
        
        if (lista.isEmpty()) {
            System.out.println("Nenhuma solicitação para a categoria " + m.getVeiculo().getCategoria() + " no momento.");
            return;
        }

        System.out.println("--- Solicitações Disponíveis ---");
        for (int i = 0; i < lista.size(); i++) {
            Corrida c = lista.get(i);
            System.out.printf("%d. %s\n   Passageiro: %s (Nota: %.1f ★)\n", 
                    (i+1), c.toString(), c.getPassageiro().getNome(), c.getPassageiro().getNotaMedia());
        }
        
        System.out.print("Digite o número para ACEITAR (0 para voltar): ");
        int esc = lerInteiro();
        if (esc > 0 && esc <= lista.size()) {
            try {
                sistema.aceitarCorrida(lista.get(esc-1), m);
                System.out.println(">> Corrida Aceita! Dirija-se ao local de partida.");
            } catch (Exception e) {
                System.out.println("Erro ao aceitar: " + e.getMessage());
            }
        }
    }

    private static void gerenciarViagemMotorista(Motorista m) {
        Corrida c = sistema.buscarCorridaAtivaMotorista(m);
        if (c == null) {
            System.out.println("Você não está em nenhuma viagem ativa.");
            return;
        }

        System.out.println("--- Viagem em Curso ---");
        System.out.println(c);
        System.out.println("1. Iniciar Viagem (Passageiro embarcou)");
        System.out.println("2. Finalizar Viagem (Chegou ao destino)");
        System.out.println("0. Voltar");
        int op = lerInteiro();

        try {
            if (op == 1) {
                sistema.iniciarViagem(c);
                System.out.println("Viagem INICIADA.");
            } else if (op == 2) {
                sistema.finalizarViagem(c);
                System.out.println("Viagem FINALIZADA.");
            }
        } catch (EstadoInvalidoDaCorridaException e) {
            System.out.println("Erro de fluxo: " + e.getMessage());
        } catch (Exception e) {
            System.out.println("Erro: " + e.getMessage());
        }
    }

    //SISTEMA DE AVALIACAO

    private static void menuAvaliacao(Usuario u) {
        List<Corrida> pendentes = sistema.buscarCorridasPendentesDeAvaliacao(u);
        
        if (pendentes.isEmpty()) {
            System.out.println("Nenhuma avaliação pendente.");
            return;
        }

        System.out.println("--- Histórico Pendente ---");
        for (Corrida c : pendentes) {
            String alvo = (u instanceof Passageiro) ? c.getMotorista().getNome() : c.getPassageiro().getNome();
            System.out.printf("ID %d: %s -> Avaliar %s\n", c.getId(), c.toString(), alvo);
        }
        
        System.out.print("Digite o ID da corrida para avaliar (0 sair): ");
        int id = lerInteiro();
        if (id == 0) return;
        
        Corrida alvo = pendentes.stream().filter(x -> x.getId() == id).findFirst().orElse(null);
        if (alvo != null) {
            System.out.print("Nota (1 a 5): ");
            int nota = lerInteiro();
            try {
                sistema.processarAvaliacao(alvo, u, nota);
                System.out.println("Avaliação registrada!");
            } catch (NotaInvalidaException e) {
                System.out.println("Erro: Nota deve ser entre 1 e 5.");
            } catch (Exception e) {
                System.out.println("Erro: " + e.getMessage());
            }
        } else {
            System.out.println("ID inválido.");
        }
    }

    private static void trocarVeiculo(Motorista m) {
        System.out.println("--- Segurança: Troca de Veículo ---");
        System.out.print("Confirme sua senha: ");
        String s = scanner.nextLine();
        
        if (!m.validarSenha(s)) {
            System.out.println("Senha incorreta.");
            return;
        }
        
        System.out.print("Nova Placa: "); String p = scanner.nextLine();
        System.out.print("Novo Modelo: "); String mod = scanner.nextLine();
        System.out.print("Nova Cor: "); String c = scanner.nextLine();
        System.out.print("Novo Ano: "); int ano = lerInteiro();
        System.out.print("Categoria (1-Comum / 2-Luxo): ");
        int catOp = lerInteiro();
        CategoriaVeiculo cat = (catOp == 2) ? CategoriaVeiculo.LUXO : CategoriaVeiculo.COMUM;
        
        m.setVeiculo(new Veiculo(p, mod, c, ano, cat));
        System.out.println("Veículo atualizado com sucesso.");
    }

    private static void configurarPagamento(Passageiro p) {
        System.out.println("--- Meios de Pagamento ---");
        System.out.println("1. Pix");
        System.out.println("2. Cartão de Crédito (Pode falhar)");
        System.out.println("3. Dinheiro / Saldo App");
        System.out.print("Opção: ");
        int op = lerInteiro();

        if (op == 1) p.setMetodoPagamento(new PagamentoPix());
        else if (op == 2) p.setMetodoPagamento(new PagamentoCartao());
        else if (op == 3) {
            System.out.print("Informe o valor do saldo: ");
            double val = scanner.nextDouble(); scanner.nextLine();
            p.setMetodoPagamento(new PagamentoDinheiro(val));
        } else {
            System.out.println("Opção inválida. Mantendo anterior.");
        }
        System.out.println("Pagamento configurado.");
    }

    private static void popularDadosIniciais() {
        Veiculo v1 = new Veiculo("TEST-001", "Fiat Uno", "Branco", 2015, CategoriaVeiculo.COMUM);
        Motorista m1 = new Motorista("Carlos Comum", "111", "carlos@app.com", "9999-0000", "123", "CNH-A", v1);
        
        Veiculo v2 = new Veiculo("LUX-999", "BMW X1", "Preto", 2022, CategoriaVeiculo.LUXO);
        Motorista m2 = new Motorista("Ana Luxo", "222", "ana@app.com", "9999-1111", "123", "CNH-B", v2);

        try {
            m1.ficarOnline();
            sistema.cadastrarMotorista(m1);
            m2.ficarOnline();
            sistema.cadastrarMotorista(m2);
        } catch (Exception e) {}

        Passageiro p1 = new Passageiro("Paulo Passageiro", "333", "paulo@app.com", "8888-0000", "123");
        p1.setMetodoPagamento(new PagamentoPix());
        try { sistema.cadastrarPassageiro(p1); } catch (Exception e) {}
    }
    
    //SIMPLIFICADOR
    private static int lerInteiro() {
        try {
            int i = scanner.nextInt();
            scanner.nextLine();
            return i;
        } catch (Exception e) {
            scanner.nextLine();
            return -1;
        }
    }
}