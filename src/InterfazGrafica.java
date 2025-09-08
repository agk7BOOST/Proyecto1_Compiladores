// InterfazGrafica.java
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.List;

public class InterfazGrafica extends JFrame {
    private JTextArea areaCodigo;
    private JTextArea areaResultados;
    private JButton botonAnalizar;
    private AnalizadorLexico analizador;

    public InterfazGrafica() {
        setTitle("Analizador Léxico");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(800, 600);
        setLocationRelativeTo(null);

        analizador = new AnalizadorLexico();

        JMenuBar menuBar = new JMenuBar();
        JMenu menuArchivo = new JMenu("Archivo");
        JMenuItem itemAbrir = new JMenuItem("Abrir archivo...");
        menuArchivo.add(itemAbrir);
        menuBar.add(menuArchivo);
        setJMenuBar(menuBar);

        JPanel panelPrincipal = new JPanel(new BorderLayout(10, 10));
        panelPrincipal.setBorder(new EmptyBorder(10, 10, 10, 10));

        areaCodigo = new JTextArea();
        areaCodigo.setFont(new Font("Consolas", Font.PLAIN, 14));

        areaResultados = new JTextArea();
        areaResultados.setFont(new Font("Consolas", Font.PLAIN, 14));
        areaResultados.setEditable(false);

        JScrollPane scrollCodigo = new JScrollPane(areaCodigo);
        JScrollPane scrollResultados = new JScrollPane(areaResultados);

        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, scrollCodigo, scrollResultados);
        splitPane.setResizeWeight(0.5); // Divide el espacio 50/50

        botonAnalizar = new JButton("Analizar Código");

        panelPrincipal.add(splitPane, BorderLayout.CENTER);
        panelPrincipal.add(botonAnalizar, BorderLayout.SOUTH);

        add(panelPrincipal);


        botonAnalizar.addActionListener(e -> analizarTexto());

        itemAbrir.addActionListener(e -> abrirArchivo());
    }

    private void abrirArchivo() {
        JFileChooser fileChooser = new JFileChooser();
        int seleccion = fileChooser.showOpenDialog(this);

        if (seleccion == JFileChooser.APPROVE_OPTION) {
            File archivoSeleccionado = fileChooser.getSelectedFile();
            try {
                String contenido = new String(Files.readAllBytes(archivoSeleccionado.toPath()));
                areaCodigo.setText(contenido);
                areaResultados.setText("");
            } catch (IOException ex) {
                JOptionPane.showMessageDialog(this, "Error al leer el archivo: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void analizarTexto(){
        String codigoFuente = areaCodigo.getText();
        if (codigoFuente.trim().isEmpty()) {
            areaResultados.setText("No hay código para analizar.");
            return;
        }

        String codigoLimpio = analizador.limpiarCodigo(codigoFuente);
        List<Token> tokens = analizador.analizar(codigoLimpio);

        StringBuilder resultados = new StringBuilder();
        resultados.append("--- CÓDIGO LIMPIO ---\n");
        resultados.append(codigoLimpio).append("\n\n");
        resultados.append("--- TOKENS ENCONTRADOS ---\n");

        if (tokens.isEmpty()){
            resultados.append("No se encontraron tokens.");
        } else {
            for (Token token : tokens) {
                resultados.append(token.toString()).append("\n");
            }
        }
        areaResultados.setText(resultados.toString());
    }
}