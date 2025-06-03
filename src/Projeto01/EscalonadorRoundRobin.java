package Projeto01;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.Comparator;

public class EscalonadorRoundRobin {

    public void escalonar(List<Processo> processos, int quantum) {
        Queue<Processo> fila = new LinkedList<>();
        List<Processo> processosFinalizados = new ArrayList<>();
        int tempoAtual = 0;
        int n = processos.size();
        int[] burstRestante = new int[n + 1];

        for (int i = 0; i < n; i++) {
            Processo p = processos.get(i);
            int pid = Integer.parseInt(p.nome.substring(1));
            burstRestante[pid] = p.tempoBurst;
        }

        List<Processo> processosCopia = new ArrayList<>(processos);
        processosCopia.sort(Comparator.comparingInt(p -> p.tempoChegada));
        int proximoProcessoIndex = 0;

        while(true) {
            boolean concluido = true;
            
            while(proximoProcessoIndex < n && processosCopia.get(proximoProcessoIndex).tempoChegada <= tempoAtual){
                fila.add(processosCopia.get(proximoProcessoIndex));
                proximoProcessoIndex++;
            }

            if(fila.isEmpty()){
                if(proximoProcessoIndex < n){
                    tempoAtual = processosCopia.get(proximoProcessoIndex).tempoChegada;
                } else {
                    break;
                }
                continue;
            }

            Processo processoAtual = fila.poll();
            int pid = Integer.parseInt(processoAtual.nome.substring(1));
            
            concluido = false;

            if (burstRestante[pid] > quantum) {
                tempoAtual += quantum;
                burstRestante[pid] -= quantum;
                while(proximoProcessoIndex < n && processosCopia.get(proximoProcessoIndex).tempoChegada <= tempoAtual){
                    fila.add(processosCopia.get(proximoProcessoIndex));
                    proximoProcessoIndex++;
                }
                fila.add(processoAtual);
            } else {
                tempoAtual += burstRestante[pid];
                processoAtual.tempoRetorno = tempoAtual - processoAtual.tempoChegada;
                processoAtual.tempoEspera = processoAtual.tempoRetorno - processoAtual.tempoBurst;
                burstRestante[pid] = 0;
                processosFinalizados.add(processoAtual);
            }
        }
        
        int tempoEsperaTotal = 0;
        int tempoRetornoTotal = 0;
        for(Processo p : processosFinalizados){
            tempoEsperaTotal += p.tempoEspera;
            tempoRetornoTotal += p.tempoRetorno;
        }

        imprimirResultados(processosFinalizados, tempoEsperaTotal, tempoRetornoTotal, quantum);
    }
    
    private void imprimirResultados(List<Processo> processos, int tempoEsperaTotal, int tempoRetornoTotal, int quantum) {
        processos.sort(Comparator.comparing(p -> p.nome));
        System.out.println("\n=== Resultados para Round Robin (Quantum=" + quantum + ") ===");
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