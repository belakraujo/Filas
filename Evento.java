public class Evento implements Comparable<Evento> {
    double tempo;
    TipoEvento tipo;
    Fila fila;
    Cliente cliente;

    public Evento(double tempo, TipoEvento tipo, Fila fila, Cliente cliente) {
        this.tempo = tempo;
        this.tipo = tipo;
        this.fila = fila;
        this.cliente = cliente;
    }

    @Override
    public int compareTo(Evento outro) {
        return Double.compare(this.tempo, outro.tempo);
    }
}
