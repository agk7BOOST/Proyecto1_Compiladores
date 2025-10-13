// Archivo: src/InterfazGrafica.java
import com.formdev.flatlaf.FlatDarculaLaf;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.io.File;
import java.nio.file.Files;
import java.util.Arrays;
import java.util.List;

public class InterfazGrafica extends JFrame {

    private JTextArea areaCodigo;
    private JTextArea areaLexico, areaAst, areaAstOptimizado, areaConsola, areaGenerado;
    private AnalizadorLexico analizadorLexico;

    public InterfazGrafica() {
        FlatDarculaLaf.setup();
        analizadorLexico = new AnalizadorLexico();

        // --- CONFIGURACIÓN DE LA VENTANA PRINCIPAL ---
        setTitle("Compilador Didáctico - Grupo 2");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1200, 800);
        setLocationRelativeTo(null);

        // --- PANEL SUPERIOR CON TÍTULO Y CRÉDITOS ---
        add(crearPanelSuperior(), BorderLayout.NORTH);

        // --- ÁREA DE CÓDIGO CON NÚMEROS DE LÍNEA ---
        areaCodigo = new JTextArea(
                "imprimir(\"--- Bucle For ---\");\n" +
                        "for (int i = 0; i < 5; i = i + 1) {\n" +
                        "    if (i == 3) {\n" +
                        "        imprimir(\"i es tres!\");\n" +
                        "    } else {\n" +
                        "        imprimir(i);\n" +
                        "    }\n" +
                        "}\n\n" +
                        "imprimir(\"--- Bucle While ---\");\n" +
                        "int j = 3;\n" +
                        "while (j > 0) {\n" +
                        "    imprimir(j);\n" +
                        "    j = j - 1;\n" +
                        "}\n"
        );
        areaCodigo.setFont(new Font("Consolas", Font.PLAIN, 14));
        JScrollPane scrollCodigo = new JScrollPane(areaCodigo);
        scrollCodigo.setRowHeaderView(new TextLineNumber(areaCodigo));

        // --- PANEL DE RESULTADOS CON PESTAÑAS ---
        JTabbedPane panelResultados = crearPanelDeResultados();

        // --- PANEL PRINCIPAL (SPLITPANE) ---
        // ESTA ES LA LÍNEA CORREGIDA: JSplit-Pane -> JSplitPane
        JSplitPane splitPanePrincipal = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, scrollCodigo, panelResultados);
        splitPanePrincipal.setResizeWeight(0.5);

        JPanel panelPrincipal = new JPanel(new BorderLayout(10, 10));
        panelPrincipal.setBorder(new EmptyBorder(0, 10, 10, 10));
        panelPrincipal.add(splitPanePrincipal, BorderLayout.CENTER);

        // --- BOTÓN PARA COMPILAR ---
        JButton botonCompilar = new JButton("Compilar y Ejecutar");
        botonCompilar.setFont(new Font("Segoe UI", Font.BOLD, 16));
        botonCompilar.addActionListener(e -> compilar());
        panelPrincipal.add(botonCompilar, BorderLayout.SOUTH);

        add(panelPrincipal, BorderLayout.CENTER);
        setJMenuBar(crearBarraMenu());
    }

    private void compilar() {
        try {
            // Limpia las áreas de texto de resultados anteriores
            limpiarResultados();

            // FASE 1: LÉXICO
            List<Token> tokens = analizadorLexico.analizar(analizadorLexico.limpiarCodigo(areaCodigo.getText()));
            mostrarTokens(tokens);

            if (tokens.isEmpty()) {
                areaAst.setText("No hay tokens para analizar.");
                return;
            }

            // FASE 2: SINTÁCTICO (Genera AST)
            AnalizadorSintactico parser = new AnalizadorSintactico(tokens);
            Programa ast = parser.analizar();
            areaAst.setText(new AstPrinter().print(ast));

            // FASE 3: OPTIMIZACIÓN
            OptimizadorCodigo optimizador = new OptimizadorCodigo();
            Programa astOptimizado = (Programa) optimizador.optimizar(ast);
            areaAstOptimizado.setText(new AstPrinter().print(astOptimizado));

            // FASE 4: GENERACIÓN DE CÓDIGO C++
            areaGenerado.setText(new GeneradorCodigo().generar(astOptimizado));

            // FASE 5: SIMULACIÓN / EJECUCIÓN
            Simulador simulador = new Simulador();
            String salidaConsola = simulador.simular(astOptimizado);
            areaConsola.setText(salidaConsola);

        } catch (Exception e) {
            // Muestra cualquier error de compilación o ejecución en un pop-up
            JOptionPane.showMessageDialog(this, "Error:\n" + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    // --- MÉTODOS AUXILIARES PARA CREAR LA UI ---
    private void limpiarResultados() {
        areaLexico.setText("");
        areaAst.setText("");
        areaAstOptimizado.setText("");
        areaGenerado.setText("");
        areaConsola.setText("");
    }

    private JPanel crearPanelSuperior() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(new Color(60, 63, 65));
        panel.setBorder(new EmptyBorder(10, 15, 10, 15));
        JLabel titulo = new JLabel("Compilador Completo");
        titulo.setFont(new Font("Segoe UI", Font.BOLD, 24));
        titulo.setForeground(Color.WHITE);
        panel.add(titulo, BorderLayout.CENTER);

        JLabel creditos = new JLabel("<html><b>Integrantes:</b> Ivan Alvarenga, Juan Isidro Garcia, Maria Jazmin Caceres, Ciro Sosa, Victor Duarte</html>");
        creditos.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        creditos.setForeground(new Color(200, 200, 200));
        panel.add(creditos, BorderLayout.SOUTH);
        return panel;
    }

    private JTabbedPane crearPanelDeResultados() {
        JTabbedPane tabs = new JTabbedPane();
        areaLexico = new JTextArea();
        areaAst = new JTextArea();
        areaAstOptimizado = new JTextArea();
        areaGenerado = new JTextArea();
        areaConsola = new JTextArea();

        tabs.addTab("Tokens", new JScrollPane(areaLexico));
        tabs.addTab("AST", new JScrollPane(areaAst));
        tabs.addTab("AST Optimizado", new JScrollPane(areaAstOptimizado));
        tabs.addTab("Código Generado (C++)", new JScrollPane(areaGenerado));
        tabs.addTab("Consola", new JScrollPane(areaConsola));

        for (JTextArea area : Arrays.asList(areaLexico, areaAst, areaAstOptimizado, areaGenerado, areaConsola)) {
            area.setFont(new Font("Consolas", Font.PLAIN, 14));
            area.setEditable(false);
            area.setMargin(new Insets(5, 5, 5, 5));
        }
        return tabs;
    }

    private void mostrarTokens(List<Token> tokens) {
        StringBuilder sb = new StringBuilder();
        for (Token token : tokens) {
            sb.append(token.toString()).append("\n");
        }
        areaLexico.setText(sb.toString());
    }

    private JMenuBar crearBarraMenu() {
        JMenuBar menuBar = new JMenuBar();
        JMenu menuArchivo = new JMenu("Archivo");
        JMenuItem itemAbrir = new JMenuItem("Abrir archivo...");
        itemAbrir.addActionListener(e -> abrirArchivo());
        menuArchivo.add(itemAbrir);
        menuBar.add(menuArchivo);
        return menuBar;
    }

    private void abrirArchivo() {
        JFileChooser fileChooser = new JFileChooser();
        if (fileChooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
            try {
                File archivoSeleccionado = fileChooser.getSelectedFile();
                String contenido = new String(Files.readAllBytes(archivoSeleccionado.toPath()));
                areaCodigo.setText(contenido);
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Error al leer el archivo: " + ex.getMessage(), "Error de Archivo", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
}