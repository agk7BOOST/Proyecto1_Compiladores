// Archivo: src/InterfazGrafica.java
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

    private JTextPane areaCodigo; // CAMBIADO a JTextPane
    private JTextArea areaLexico, areaAst, areaAstOptimizado, areaConsola, areaGenerado;
    private AnalizadorLexico analizadorLexico;

    // --- NUEVOS ATRIBUTOS PARA ESTILOS ---
    private Style stylePalabraClave, styleIdentificador, styleNumero, styleString, styleOperador, styleComentario, styleDefault;

    public InterfazGrafica() {
        FlatDarculaLaf.setup();
        analizadorLexico = new AnalizadorLexico();

        setTitle("Compilador Didáctico - Grupo 2");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1200, 800);
        setLocationRelativeTo(null);

        add(crearPanelSuperior(), BorderLayout.NORTH);

        // --- ÁREA DE CÓDIGO CON JTextPane ---
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

        // El TextLineNumber necesita un JTextArea, así que creamos un proxy invisible
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
        panelPrincipal.add(botonCompilar, BorderLayout.SOUTH);

        add(panelPrincipal, BorderLayout.CENTER);
        setJMenuBar(crearBarraMenu());

        // --- CONFIGURACIÓN DEL RESALTADO DE SINTAXIS ---
        inicializarEstilos();
        areaCodigo.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                actualizarResaltado();
            }
            @Override
            public void removeUpdate(DocumentEvent e) {
                actualizarResaltado();
            }
            @Override
            public void changedUpdate(DocumentEvent e) {}
        });

        // Resaltado inicial
        actualizarResaltado();
    }

    private void compilar() {
        try {
            limpiarResultados();
            String codigo = areaCodigo.getText();

            List<Token> tokens = analizadorLexico.analizar(analizadorLexico.limpiarCodigo(codigo));
            mostrarTokens(tokens);

            if (tokens.stream().anyMatch(t -> t.getTipo() == TipoToken.ERROR)) {
                areaAst.setText("Se encontraron errores léxicos. Corrija el código y vuelva a intentarlo.");
                return;
            }

            if (tokens.isEmpty()) {
                areaAst.setText("No hay tokens para analizar.");
                return;
            }

            AnalizadorSintactico parser = new AnalizadorSintactico(tokens);
            Programa ast = parser.analizar();
            areaAst.setText(new AstPrinter().print(ast));

            OptimizadorCodigo optimizador = new OptimizadorCodigo();
            Programa astOptimizado = (Programa) optimizador.optimizar(ast);
            areaAstOptimizado.setText(new AstPrinter().print(astOptimizado));

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
        areaAst.setText("");
        areaAstOptimizado.setText("");
        areaGenerado.setText("");
        areaConsola.setText("");
    }

    // --- MÉTODOS PARA RESALTADO DE SINTAXIS ---

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
                    "(//.*)|" + // 1: Comentarios
                            "(\"[^\"]*\")|" + // 2: Strings
                            "\\b(if|else|while|return|int|float|void|for|imprimir)\\b|" + // 3: Palabras clave
                            "([a-zA-Z_][a-zA-Z0-9_]*)|" + // 4: Identificadores
                            "(\\d+)|" + // 5: Números
                            "(==|!=|<=|>=|&&|\\|\\||[+\\-*/=<>(){};])"; // 6: Operadores

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

    // --- MÉTODOS PARA CREAR LA UI ---

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