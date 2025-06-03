package Projeto01;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class EscalonadorSJF {

    public void escalonar(List<Processo> processos) {
        processos.sort(Comparator.comparingInt(p -> p.tempoChegada));

        List<Processo> processosFinalizados = new ArrayList<>();
        int tempoAtual = 0;
        int tempoEsperaTotal = 0;
        int tempoRetornoTotal = 0;

        List<Processo> filaProntos = new ArrayList<>(processos);

        while (!filaProntos.isEmpty()) {
            List<Processo> disponiveis = new ArrayList<>();
            for (Processo p : filaProntos) {
                if (p.tempoChegada <= tempoAtual) {
                    disponiveis.add(p);
                }
            }

            if (disponiveis.isEmpty()) {
                tempoAtual = filaProntos.get(0).tempoChegada;
                continue;
            }

            disponiveis.sort(Comparator.comparingInt(p -> p.tempoBurst));
            Processo processoAtual = disponiveis.get(0);
            filaProntos.remove(processoAtual);

            processoAtual.tempoEspera = tempoAtual - processoAtual.tempoChegada;
            tempoEsperaTotal += processoAtual.tempoEspera;

            tempoAtual += processoAtual.tempoBurst;

            processoAtual.tempoRetorno = tempoAtual - processoAtual.tempoChegada;
            tempoRetornoTotal += processoAtual.tempoRetorno;

            processosFinalizados.add(processoAtual);
        }

        imprimirResultados(processosFinalizados, tempoEsperaTotal, tempoRetornoTotal);
    }

    private void imprimirResultados(List<Processo> processos, int tempoEsperaTotal, int tempoRetornoTotal) {
        System.out.println("\n--- Resultados para Shortest Job First (SJF) ---");
        System.out.println("---------------------------------------------------------------------------------");
        System.out.printf("%-10s | %-15s | %-15s | %-15s | %-15s\n", "Processo", "T. Chegada", "T. Burst", "T. Espera", "T. Retorno");
        System.out.println("---------------------------------------------------------------------------------");
        for (Processo p : processos) {
            System.out.printf("%-10s | %-15d | %-15d | %-15d | %-15d\n", p.nome, p.tempoChegada, p.tempoBurst, p.tempoEspera, p.tempoRetorno);
        }
        System.out.println("---------------------------------------------------------------------------------");
        System.out.printf("Tempo Médio de Espera: %.2f\n", (double) tempoEsperaTotal / processos.size());
        System.out.printf("Tempo Médio de Retorno: %.2f\n", (double) tempoRetornoTotal / processos.size());
    }
}