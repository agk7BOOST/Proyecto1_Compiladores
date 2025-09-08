// InterfazGrafica.java
import com.formdev.flatlaf.FlatIntelliJLaf; // Importamos la nueva librería
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
    private AnalizadorLexico analizador;

    public InterfazGrafica() {
        FlatIntelliJLaf.setup();
        setTitle("Analizador Léxico - Grupo 2");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(900, 700);
        setLocationRelativeTo(null);
        analizador = new AnalizadorLexico();

        JPanel panelSuperior = new JPanel();
        panelSuperior.setLayout(new BoxLayout(panelSuperior, BoxLayout.Y_AXIS));
        panelSuperior.setBorder(new EmptyBorder(15, 15, 15, 15));
        panelSuperior.setBackground(new Color(60, 63, 65)); // Un fondo oscuro

        JLabel lblTitulo = new JLabel("Analizador Léxico de Código Fuente");
        lblTitulo.setFont(new Font("Segoe UI", Font.BOLD, 24));
        lblTitulo.setForeground(Color.WHITE);
        lblTitulo.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel lblIntegrantes = new JLabel("<html><b>Integrantes:</b> Ivan Alvarenga, Juan Isidro Garcia, Maria Jazmin Caceres, Ciro Sosa, Victor Duarte</html>");
        lblIntegrantes.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        lblIntegrantes.setForeground(new Color(200, 200, 200));
        lblIntegrantes.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel lblDescripcion = new JLabel("<html><p style='width: 500px; text-align: center; margin-top: 10px;'>Pegue su código en el panel izquierdo o ábralo desde el menú 'Archivo'. Luego, presione 'Analizar' para ver el código limpio y la lista de tokens resultantes en el panel derecho.</p></html>");
        lblDescripcion.setFont(new Font("Segoe UI", Font.ITALIC, 12));
        lblDescripcion.setForeground(new Color(180, 180, 180));
        lblDescripcion.setAlignmentX(Component.CENTER_ALIGNMENT);

        panelSuperior.add(lblTitulo);
        panelSuperior.add(Box.createRigidArea(new Dimension(0, 5))); // Espacio vertical
        panelSuperior.add(lblIntegrantes);
        panelSuperior.add(lblDescripcion);

        JMenuBar menuBar = new JMenuBar();
        JMenu menuArchivo = new JMenu("Archivo");
        JMenuItem itemAbrir = new JMenuItem("Abrir archivo...");
        menuArchivo.add(itemAbrir);
        menuBar.add(menuArchivo);
        setJMenuBar(menuBar);

        JPanel panelPrincipal = new JPanel(new BorderLayout(10, 10));
        panelPrincipal.setBorder(new EmptyBorder(0, 10, 10, 10));

        areaCodigo = new JTextArea("Escribe o pega tu código aquí...");
        areaCodigo.setFont(new Font("Consolas", Font.PLAIN, 14));

        areaResultados = new JTextArea("Los resultados aparecerán aquí...");
        areaResultados.setFont(new Font("Consolas", Font.PLAIN, 14));
        areaResultados.setEditable(false);
        areaResultados.setLineWrap(true); // Para que el texto no se salga

        JScrollPane scrollCodigo = new JScrollPane(areaCodigo);
        JScrollPane scrollResultados = new JScrollPane(areaResultados);

        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, scrollCodigo, scrollResultados);
        splitPane.setResizeWeight(0.5);

        JButton botonAnalizar = new JButton("Analizar Código");
        botonAnalizar.setFont(new Font("Segoe UI", Font.BOLD, 16));
        botonAnalizar.setCursor(new Cursor(Cursor.HAND_CURSOR)); // Cambia el cursor al pasar por encima

        panelPrincipal.add(splitPane, BorderLayout.CENTER);
        panelPrincipal.add(botonAnalizar, BorderLayout.SOUTH);

        add(panelSuperior, BorderLayout.NORTH);
        add(panelPrincipal, BorderLayout.CENTER);

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
        if (codigoFuente.trim().isEmpty() || codigoFuente.equals("Escribe o pega tu código aquí...")) {
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