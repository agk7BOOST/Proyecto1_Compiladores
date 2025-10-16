import com.formdev.flatlaf.FlatDarculaLaf;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.Style;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyledDocument;
import java.awt.*;
import java.io.File;
import java.nio.file.Files;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class InterfazGrafica extends JFrame {

    private JTextPane areaCodigo;
    private JTextArea areaLexico, areaSintactico, areaSemantico, areaOptimizado, areaGenerado, areaConsola;
    private AnalizadorLexico analizadorLexico;

    private Style stylePalabraClave, styleIdentificador, styleNumero, styleString, styleOperador, styleComentario, styleDefault;

    public InterfazGrafica() {
        FlatDarculaLaf.setup();
        analizadorLexico = new AnalizadorLexico();

        setTitle("Compilador Didáctico - Fases del Proceso");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1200, 800);
        setLocationRelativeTo(null);

        add(crearPanelSuperior(), BorderLayout.NORTH);

        areaCodigo = new JTextPane();
        areaCodigo.setFont(new Font("Consolas", Font.PLAIN, 14));
        areaCodigo.setText(
                "// Ejemplo de uso del compilador\n" +
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

        JTextArea textAreaProxy = new JTextArea();
        textAreaProxy.setDocument(areaCodigo.getDocument());
        textAreaProxy.setFont(areaCodigo.getFont());

        JScrollPane scrollCodigo = new JScrollPane(areaCodigo);
        scrollCodigo.setRowHeaderView(new TextLineNumber(textAreaProxy));


        JTabbedPane panelResultados = crearPanelDeResultados();
        JSplitPane splitPanePrincipal = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, scrollCodigo, panelResultados);
        splitPanePrincipal.setResizeWeight(0.5);

        JPanel panelPrincipal = new JPanel(new BorderLayout(10, 10));
        panelPrincipal.setBorder(new EmptyBorder(0, 10, 10, 10));
        panelPrincipal.add(splitPanePrincipal, BorderLayout.CENTER);

        JButton botonCompilar = new JButton("Compilar y Ejecutar");
        botonCompilar.setFont(new Font("Segoe UI", Font.BOLD, 16));
        botonCompilar.addActionListener(e -> compilar());

        JPanel panelBoton = new JPanel();
        panelBoton.add(botonCompilar);

        panelPrincipal.add(panelBoton, BorderLayout.SOUTH);

        add(panelPrincipal, BorderLayout.CENTER);
        setJMenuBar(crearBarraMenu());

        inicializarEstilos();
        areaCodigo.getDocument().addDocumentListener(new DocumentListener() {
            @Override public void insertUpdate(DocumentEvent e) { actualizarResaltado(); }
            @Override public void removeUpdate(DocumentEvent e) { actualizarResaltado(); }
            @Override public void changedUpdate(DocumentEvent e) {}
        });

        actualizarResaltado();
    }

    private void compilar() {
        try {
            limpiarResultados();
            String codigo = areaCodigo.getText();

            String codigoLimpio = analizadorLexico.limpiarCodigo(codigo);
            List<Token> tokens = analizadorLexico.analizar(codigoLimpio);
            mostrarTokens(tokens);

            if (tokens.stream().anyMatch(t -> t.getTipo() == TipoToken.ERROR)) {
                areaSintactico.setText("Se encontraron errores léxicos. Corrija el código y vuelva a intentarlo.");
                return;
            }
            if (tokens.isEmpty()) {
                areaSintactico.setText("No hay tokens para analizar.");
                return;
            }

            AnalizadorSintactico parser = new AnalizadorSintactico(tokens);
            Programa ast = parser.analizar();
            areaSintactico.setText(new AstPrinter().print(ast));

            AnalizadorSemantico semantico = new AnalizadorSemantico(tokens);
            String resultadoSemantico = semantico.analizar();
            areaSemantico.setText(resultadoSemantico);
            if (resultadoSemantico.contains("ERROR SEMÁNTICO")) {
                areaOptimizado.setText("Se encontraron errores semánticos. No se puede continuar.");
                return;
            }

            OptimizadorCodigo optimizador = new OptimizadorCodigo();
            Programa astOptimizado = (Programa) optimizador.optimizar(ast);
            areaOptimizado.setText(new AstPrinter().print(astOptimizado));

            areaGenerado.setText(new GeneradorCodigo().generar(astOptimizado));

            Simulador simulador = new Simulador();
            String salidaConsola = simulador.simular(astOptimizado);
            areaConsola.setText(salidaConsola);

        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error de compilación:\n" + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void limpiarResultados() {
        areaLexico.setText("");
        areaSintactico.setText("");
        areaSemantico.setText("");
        areaOptimizado.setText("");
        areaGenerado.setText("");
        areaConsola.setText("");
    }

    private void inicializarEstilos() {
        StyledDocument doc = areaCodigo.getStyledDocument();
        styleDefault = doc.addStyle("Default", null);
        StyleConstants.setForeground(styleDefault, new Color(200, 200, 200));

        stylePalabraClave = doc.addStyle("PalabraClave", styleDefault);
        StyleConstants.setForeground(stylePalabraClave, new Color(204, 120, 50));
        StyleConstants.setBold(stylePalabraClave, true);

        styleIdentificador = doc.addStyle("Identificador", styleDefault);
        StyleConstants.setForeground(styleIdentificador, Color.WHITE);

        styleNumero = doc.addStyle("Numero", styleDefault);
        StyleConstants.setForeground(styleNumero, new Color(104, 151, 187));

        styleString = doc.addStyle("String", styleDefault);
        StyleConstants.setForeground(styleString, new Color(106, 135, 89));

        styleOperador = doc.addStyle("Operador", styleDefault);
        StyleConstants.setForeground(styleOperador, new Color(255, 204, 0));

        styleComentario = doc.addStyle("Comentario", styleDefault);
        StyleConstants.setForeground(styleComentario, new Color(128, 128, 128));
        StyleConstants.setItalic(styleComentario, true);
    }

    private void actualizarResaltado() {
        SwingUtilities.invokeLater(() -> {
            String texto = areaCodigo.getText();
            StyledDocument doc = areaCodigo.getStyledDocument();

            doc.setCharacterAttributes(0, texto.length(), styleDefault, true);

            String patronTokens =
                    "(//.*)|" +
                            "(\"[^\"]*\")|" +
                            "\\b(if|else|while|return|int|float|void|for|imprimir)\\b|" +
                            "([a-zA-Z_][a-zA-Z0-9_]*)|" +
                            "(\\d+)|" +
                            "(==|!=|<=|>=|&&|\\|\\||[+\\-*/=<>(){};])";

            Pattern pattern = Pattern.compile(patronTokens);
            Matcher matcher = pattern.matcher(texto);

            while (matcher.find()) {
                int inicio = matcher.start();
                int fin = matcher.end();
                Style estiloAplicar = null;

                if (matcher.group(1) != null) estiloAplicar = styleComentario;
                else if (matcher.group(2) != null) estiloAplicar = styleString;
                else if (matcher.group(3) != null) estiloAplicar = stylePalabraClave;
                else if (matcher.group(4) != null) estiloAplicar = styleIdentificador;
                else if (matcher.group(5) != null) estiloAplicar = styleNumero;
                else if (matcher.group(6) != null) estiloAplicar = styleOperador;

                if (estiloAplicar != null) {
                    doc.setCharacterAttributes(inicio, fin - inicio, estiloAplicar, true);
                }
            }
        });
    }

    private JPanel crearPanelSuperior() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(new Color(60, 63, 65));
        panel.setBorder(new EmptyBorder(10, 15, 10, 15));
        JLabel titulo = new JLabel("Compilador Didáctico");
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
        areaSintactico = new JTextArea();
        areaSemantico = new JTextArea();
        areaOptimizado = new JTextArea();
        areaGenerado = new JTextArea();
        areaConsola = new JTextArea();

        tabs.addTab("1. Análisis Léxico (Tokens)", new JScrollPane(areaLexico));
        tabs.addTab("2. Análisis Sintáctico (AST)", new JScrollPane(areaSintactico));
        tabs.addTab("3. Análisis Semántico (Símbolos)", new JScrollPane(areaSemantico));
        tabs.addTab("4. Optimización (AST Optimizado)", new JScrollPane(areaOptimizado));
        tabs.addTab("5. Generación de Código (C++)", new JScrollPane(areaGenerado));
        tabs.addTab("6. Ejecución (Consola)", new JScrollPane(areaConsola));

        for (JTextArea area : Arrays.asList(areaLexico, areaSintactico, areaSemantico, areaOptimizado, areaGenerado, areaConsola)) {
            area.setFont(new Font("Consolas", Font.PLAIN, 14));
            area.setEditable(false);
            area.setMargin(new Insets(5, 5, 5, 5));
        }
        return tabs;
    }

    private void mostrarTokens(List<Token> tokens) {
        StringBuilder sb = new StringBuilder("--- CÓDIGO LIMPIO ---\n");
        sb.append(analizadorLexico.limpiarCodigo(areaCodigo.getText())).append("\n\n");
        sb.append("--- TOKENS ENCONTRADOS ---\n");
        for (Token token : tokens) {
            sb.append(String.format("<%-15s> %s%n", token.getTipo(), token.getValor()));
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