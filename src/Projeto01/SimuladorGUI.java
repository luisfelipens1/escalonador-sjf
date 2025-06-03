package Projeto01;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import javax.swing.SwingUtilities;

public class SimuladorGUI extends JFrame {

    private final JTextArea logArea;
    private final JLabel processoExecutandoLabel;
    private final DefaultListModel<String> filaProntosModel;

    public SimuladorGUI() {
        setTitle("Visualizador de Escalonamento");
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout(10, 10));

        JPanel controlPanel = new JPanel();
        JButton startSJF = new JButton("Iniciar Simulação SJF");
        controlPanel.add(startSJF);
        add(controlPanel, BorderLayout.SOUTH);

        logArea = new JTextArea();
        logArea.setEditable(false);
        logArea.setFont(new Font("Monospaced", Font.PLAIN, 12));
        add(new JScrollPane(logArea), BorderLayout.CENTER);

        JPanel visualizationPanel = new JPanel(new GridLayout(2, 1, 10, 10));
        
        JPanel execPanel = new JPanel(new BorderLayout());
        execPanel.setBorder(BorderFactory.createTitledBorder("Processo em Execução"));
        processoExecutandoLabel = new JLabel("Nenhum", SwingConstants.CENTER);
        processoExecutandoLabel.setFont(new Font("Arial", Font.BOLD, 20));
        execPanel.add(processoExecutandoLabel);
        visualizationPanel.add(execPanel);

        JPanel readyQueuePanel = new JPanel(new BorderLayout());
        readyQueuePanel.setBorder(BorderFactory.createTitledBorder("Fila de Prontos"));
        filaProntosModel = new DefaultListModel<>();
        JList<String> filaProntosList = new JList<>(filaProntosModel);
        readyQueuePanel.add(new JScrollPane(filaProntosList));
        visualizationPanel.add(readyQueuePanel);

        add(visualizationPanel, BorderLayout.EAST);

        startSJF.addActionListener(e -> {
            List<Processo> processos = Simulador.gerarProcessosAleatorios(5);
            logar("--- Nova Simulação SJF Iniciada ---");
            logar("Processos: " + processos.toString());
            simularVisualmente(processos);
        });
    }

    private void logar(String msg) {
        SwingUtilities.invokeLater(() -> logArea.append(msg + "\n"));
    }

    private void atualizarGUI(Processo emExecucao, List<Processo> fila) {
        SwingUtilities.invokeLater(() -> {
            processoExecutandoLabel.setText(emExecucao == null ? "Ocioso" : emExecucao.toString());
            filaProntosModel.clear();
            fila.forEach(p -> filaProntosModel.addElement(p.toString()));
        });
    }

    private void simularVisualmente(List<Processo> processos) {
        new Thread(() -> {
            List<Processo> filaProntos = new ArrayList<>(processos);
            Collections.sort(filaProntos, Comparator.comparingInt(p -> p.tempoChegada));
            int tempoAtual = 0;

            while (!filaProntos.isEmpty()) {
                List<Processo> disponiveis = new ArrayList<>();
                for (Processo p : filaProntos) {
                    if (p.tempoChegada <= tempoAtual) disponiveis.add(p);
                }

                if (disponiveis.isEmpty()) {
                    logar("Tempo " + tempoAtual + ": CPU Ociosa.");
                    atualizarGUI(null, filaProntos);
                    tempoAtual++;
                    try { Thread.sleep(500); } catch (InterruptedException ex) {}
                    continue;
                }

                disponiveis.sort(Comparator.comparingInt(p -> p.tempoBurst));
                Processo atual = disponiveis.get(0);
                filaProntos.remove(atual);

                logar("Tempo " + tempoAtual + ": Processo " + atual.nome + " selecionado para execução.");
                atualizarGUI(atual, filaProntos);
                
                try {
                    Thread.sleep(atual.tempoBurst * 500);
                } catch (InterruptedException ex) { Thread.currentThread().interrupt(); }

                tempoAtual += atual.tempoBurst;
                logar("Tempo " + tempoAtual + ": Processo " + atual.nome + " finalizado.");
            }
            logar("--- Simulação Finalizada ---");
            atualizarGUI(null, new ArrayList<>());
        }).start();
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new SimuladorGUI().setVisible(true);
        });
    }
}