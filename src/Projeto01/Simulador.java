package Projeto01;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class Simulador {

    public static void main(String[] args) {
        List<Processo> processos = gerarProcessosAleatorios(8);
        System.out.println("Conjunto de processos gerados para a simulação:");
        processos.forEach(System.out::println);

        simularComThreads(2, processos);
        simularComThreads(4, processos);
        simularComThreads(6, processos);
    }

    public static List<Processo> gerarProcessosAleatorios(int quantidade) {
        List<Processo> lista = new ArrayList<>();
        Random random = new Random();
        for (int i = 1; i <= quantidade; i++) {
            String nome = "P" + i;
            int tempoBurst = random.nextInt(15) + 1;
            int tempoChegada = random.nextInt(10);
            lista.add(new Processo(nome, tempoBurst, tempoChegada));
        }
        return lista;
    }

    public static void simularComThreads(int numThreads, List<Processo> processos) {
        System.out.printf("\n\n=============== INICIANDO SIMULAÇÃO COM %d THREADS ===============\n", numThreads);
        ExecutorService executor = Executors.newFixedThreadPool(numThreads);

        executor.submit(() -> {
            List<Processo> copiaSJF = new ArrayList<>();
            processos.forEach(p -> copiaSJF.add(new Processo(p.nome, p.tempoBurst, p.tempoChegada)));
            new EscalonadorSJF().escalonar(copiaSJF);
        });

        executor.submit(() -> {
            List<Processo> copiaRR = new ArrayList<>();
            processos.forEach(p -> copiaRR.add(new Processo(p.nome, p.tempoBurst, p.tempoChegada)));
            new EscalonadorRoundRobin().escalonar(copiaRR, 4);
        });

        executor.shutdown();
        try {
            executor.awaitTermination(1, TimeUnit.MINUTES);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}