package Projeto01;

public class Processo {
    String nome;
    int tempoBurst;
    int tempoChegada;
    int tempoEspera;
    int tempoRetorno;

    public Processo(String nome, int tempoBurst, int tempoChegada) {
        this.nome = nome;
        this.tempoBurst = tempoBurst;
        this.tempoChegada = tempoChegada;
    }

    @Override
    public String toString() {
        return String.format("Processo[Nome=%s, Burst=%d, Chegada=%d]", nome, tempoBurst, tempoChegada);
    }
}