import java.util.*;

public class Fila {
    String id;
    int servidores;
    int capacidade;
    double atendimentoMin, atendimentoMax;
    Map<String, Double> destinos = new LinkedHashMap<>(); // ID da próxima fila ou "saida"

    Queue<Cliente> fila = new LinkedList<>();
    PriorityQueue<Double> servidoresOcupados = new PriorityQueue<>();
    double[] tempoPorEstado;
    int clientesPerdidos = 0;
    double ultimoTempo = 0;
    double tempoAtual = 0;
    Random rnd;

    public Fila(String id, int servidores, int capacidade, double atendimentoMin, double atendimentoMax, Random rnd) {
        this.id = id;
        this.servidores = servidores;
        this.capacidade = capacidade;
        this.tempoPorEstado = new double[capacidade + 1];
        this.atendimentoMin = atendimentoMin;
        this.atendimentoMax = atendimentoMax;
        this.rnd = rnd;
    }

    public void atualizarEstado(double tempoAtual) {
        int estado = fila.size();
        tempoPorEstado[estado] += tempoAtual - ultimoTempo;
        ultimoTempo = tempoAtual;
        this.tempoAtual = tempoAtual;
    }

    public boolean podeAdicionar() {
        return fila.size() + servidoresOcupados.size() < capacidade;
    }

    public void adicionarCliente(Cliente cliente, double tempoAtual, PriorityQueue<Evento> agenda) {
        atualizarEstado(tempoAtual);

        if (!podeAdicionar()) {
            clientesPerdidos++;
            return;
        }

        if (servidoresOcupados.size() < servidores) {
            double duracao = atendimentoMin + (atendimentoMax - atendimentoMin) * rnd.nextDouble();
            SimuladorRedeFilas.incrementarAleatorios();
            double fimAtendimento = tempoAtual + duracao;
            servidoresOcupados.add(fimAtendimento);
            agenda.add(new Evento(fimAtendimento, TipoEvento.SAIDA, this, cliente));
        } else {
            fila.add(cliente);
        }
    }

    public void finalizarAtendimento(double tempoAtual, PriorityQueue<Evento> agenda) {
        atualizarEstado(tempoAtual);
        servidoresOcupados.poll();

        if (!fila.isEmpty()) {
            Cliente proximo = fila.poll();
            double duracao = atendimentoMin + (atendimentoMax - atendimentoMin) * rnd.nextDouble();
            SimuladorRedeFilas.incrementarAleatorios();
            double fimAtendimento = tempoAtual + duracao;
            servidoresOcupados.add(fimAtendimento);
            agenda.add(new Evento(fimAtendimento, TipoEvento.SAIDA, this, proximo));
        }
    }

    public void imprimirEstatisticas() {
        System.out.println("\nFila " + id);
        System.out.println("Clientes perdidos: " + clientesPerdidos);
        System.out.println("Distribuição de estados:");
        for (int i = 0; i < tempoPorEstado.length; i++) {
            System.out.printf("Estado %d: %.4f\n", i, tempoPorEstado[i] / tempoAtual);
        }
    }
}
