import java.io.*;
import java.util.*;
import org.yaml.snakeyaml.Yaml;

public class SimuladorRedeFilas {
    static int aleatoriosUsados = 0;
    static Random rnd = new Random(42);

    public static void incrementarAleatorios() {
        aleatoriosUsados++;
    }

    public static void main(String[] args) throws IOException {
        Yaml yaml = new Yaml();
        Map<String, Object> config;

        try (InputStream input = new FileInputStream(new File("modelo.yml"))) {
            config = yaml.load(input);
        }

        Map<Integer, Fila> filas = new HashMap<>();
        Map<String, Object> filasConfig = (Map<String, Object>) config.get("filas");

        for (Map.Entry<String, Object> entry : filasConfig.entrySet()) {
            String idStr = entry.getKey();
            int id = Integer.parseInt(idStr);
            Map<String, Object> f = (Map<String, Object>) entry.getValue();

            int servidores = ((Number) f.get("servidores")).intValue();
            int capacidade = ((Number) f.get("capacidade")).intValue();
            double atendimentoMin = ((Number) f.get("atendimento_min")).doubleValue();
            double atendimentoMax = ((Number) f.get("atendimento_max")).doubleValue();

            Fila fila = new Fila(idStr, servidores, capacidade, atendimentoMin, atendimentoMax, rnd);

            Map<String, Object> destinos = (Map<String, Object>) f.get("destinos");
            for (Map.Entry<String, Object> destino : destinos.entrySet()) {
                String destinoId = destino.getKey();
                double probabilidade = ((Number) destino.getValue()).doubleValue();
                fila.destinos.put(destinoId, probabilidade);
            }

            filas.put(id, fila);
        }

        Map<String, Object> chegadaConfig = (Map<String, Object>) config.get("chegada");
        double chegadaMin = ((Number) chegadaConfig.get("min")).doubleValue();
        double chegadaMax = ((Number) chegadaConfig.get("max")).doubleValue();
        int filaInicialId = Integer.parseInt(chegadaConfig.get("fila_inicial").toString());

        PriorityQueue<Evento> agenda = new PriorityQueue<>();
        double tempoAtual = 2.0;

        // Primeira chegada
        agenda.add(new Evento(tempoAtual, TipoEvento.CHEGADA, filas.get(filaInicialId), new Cliente(tempoAtual)));
        incrementarAleatorios();

        while (!agenda.isEmpty() && aleatoriosUsados < 100_000) {
            Evento evento = agenda.poll();
            tempoAtual = evento.tempo;

            if (evento.tipo == TipoEvento.CHEGADA) {
                evento.fila.adicionarCliente(evento.cliente, tempoAtual, agenda);

                double interChegada = chegadaMin + (chegadaMax - chegadaMin) * rnd.nextDouble();
                incrementarAleatorios();
                tempoAtual += interChegada;

                agenda.add(new Evento(tempoAtual, TipoEvento.CHEGADA, filas.get(filaInicialId), new Cliente(tempoAtual)));
            } else if (evento.tipo == TipoEvento.SAIDA) {
                evento.fila.finalizarAtendimento(tempoAtual, agenda);

                String proximo = rotear(evento.fila.destinos);
                if (!proximo.equals("saida")) {
                    int idDestino = Integer.parseInt(proximo);
                    Fila filaDestino = filas.get(idDestino);
                    filaDestino.adicionarCliente(new Cliente(tempoAtual), tempoAtual, agenda);
                }
            }
        }

        System.out.printf("\nTempo global da simulação: %.2f\n", tempoAtual);
        for (Fila fila : filas.values()) {
            fila.imprimirEstatisticas();
        }
    }

    static String rotear(Map<String, Double> destinos) {
        double r = rnd.nextDouble();
        incrementarAleatorios();
        double acumulado = 0.0;

        for (Map.Entry<String, Double> entry : destinos.entrySet()) {
            acumulado += entry.getValue();
            if (r <= acumulado) {
                return entry.getKey();
            }
        }
        // Se não caiu em nenhuma probabilidade (erro de arredondamento)
        return destinos.keySet().iterator().next();
    }
}
