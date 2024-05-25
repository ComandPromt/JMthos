package mthos;

import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GraphicsConfiguration;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.Image;
import java.awt.Point;
import java.awt.RenderingHints;
import java.awt.Toolkit;
import java.awt.Transparency;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.awt.event.KeyEvent;
import java.awt.event.MouseWheelEvent;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.NumberFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import javax.imageio.ImageIO;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;
import javax.swing.DefaultListCellRenderer;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JTextPane;
import javax.swing.text.DefaultStyledDocument;
import javax.swing.text.Style;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyleContext;
import javax.swing.text.StyledDocument;

import org.apache.commons.io.FileUtils;
import org.json.JSONObject;

import io.github.biezhi.webp.WebpIO;
import mthos.gif.AnimatedGifEncoder;
import mthos.gif.GifDecoder;

public abstract class JMthos {

	public static final String OS = System.getProperty("os.name");

	public static void crearGif(String path, String output, String extension) {

		crearGif(path, output, extension, 25f, 1);

	}

	public static void crearGif(String path, String output, String extension, float frameRate, int open) {

		try {

			path = pasarDeArchivoACarpeta(path);

			LinkedList<String> lista = (LinkedList<String>) listar(path, extension, false, true);

			if (!lista.isEmpty()) {

				AnimatedGifEncoder e = new AnimatedGifEncoder();

				e.start(output);

				for (int i = 0; i < lista.size(); i++) {

					e.addFrame(fileToBufferedImage(new File(lista.get(i))));

				}

				e.setFrameRate(frameRate);

				e.finish();

				metodoAbrir(output, open);

			}

		}

		catch (Exception e) {

		}

	}

	public static String pasarDeArchivoACarpeta(String path) {

		String resultado = path;

		if (terminaEnArchivo(resultado)) {

			resultado = obtenerDirectorio(resultado);

		}

		else {

			resultado = ponerSeparador(resultado);

		}

		return resultado;
	}

	public static boolean terminaEnArchivo(String path) {

		Pattern pattern = Pattern.compile("\\.([a-z]{3,4})$");

		Matcher matcher = pattern.matcher(path);

		return matcher.find();

	}

	public static void optimizeGifWithGifsicle(String input, String output, int level)
			throws IOException, InterruptedException {

		optimizeGifWithGifsicle(input, output, "", level, 0);

	}

	public static void optimizeGifWithGifsicle(String input, String output, String gifsiclePath, int level)
			throws IOException, InterruptedException {

		optimizeGifWithGifsicle(input, output, gifsiclePath, level, 0);

	}

	public static void optimizeGifWithGifsicle(String input, String output, String gifsiclePath)
			throws IOException, InterruptedException {

		optimizeGifWithGifsicle(input, output, gifsiclePath, 3, 0);

	}

	double truncateDouble(double number, int numDigits) {

		double result = number;

		String arg = "" + number;

		int idx = arg.indexOf('.');

		if (idx != -1) {

			if (arg.length() > idx + numDigits) {

				arg = arg.substring(0, idx + numDigits + 1);

				result = Double.parseDouble(arg);

			}
		}

		return result;

	}

	public static void optimizeGifWithGifsicle(String input, String output, String gifsiclePath, int level, int open)
			throws IOException, InterruptedException {

		if (level < 1 || level > 3) {

			level = 3;

		}

		List<String> command = new ArrayList<>();

		if (gifsiclePath == null || gifsiclePath.isEmpty()) {

			command.add("gifsicle");

		}

		else {

			command.add(gifsiclePath);

		}

		command.add("--optimize=" + level);

		command.add("--colors");

		command.add("256");

		command.add("--output");

		command.add(output);

		command.add(input);

		ProcessBuilder processBuilder = new ProcessBuilder(command);

		processBuilder.redirectErrorStream(true);

		try {

			Process process = processBuilder.start();

			if (process.waitFor() != 0) {

				throw new RuntimeException(
						"La optimización de Gifsicle falló con el código de salida: " + process.waitFor());

			}

			else {

				metodoAbrir(output, open);

			}

		}

		catch (IOException | InterruptedException e) {

			throw new RuntimeException("Error al optimizar el GIF", e);

		}

	}

	private static void metodoAbrir(String output, int open) {

		switch (open) {

		case 0:

			break;

		case 1:

			abrir(output);

			break;

		default:

			abrir(obtenerDirectorio(output));

			break;

		}

	}

	public static String obtenerDirectorio(String output) {

		try {

			return output.substring(0, output.lastIndexOf(saberSeparador()) + 1);

		}

		catch (Exception e) {

			return "";

		}

	}

	/**
	 * Exporta un BufferedImage a un archivo.
	 *
	 * @param image    la imagen a exportar.
	 * @param format   el formato de la imagen (por ejemplo, "png", "jpg").
	 * @param filePath la ruta del archivo donde se guardará la imagen.
	 * @throws IOException si ocurre un error al escribir el archivo.
	 */

	public static void exportBufferedImage(BufferedImage image, String format, String filePath) throws IOException {

		boolean formatSupported = false;

		String[] supportedFormats = ImageIO.getWriterFormatNames();

		for (String supportedFormat : supportedFormats) {

			if (supportedFormat.equalsIgnoreCase(format)) {

				formatSupported = true;

				break;

			}

		}

		if (!formatSupported) {

			throw new IOException("Formato no soportado: " + format);

		}

		File outputFile = new File(filePath);

		if (!ImageIO.write(image, format, outputFile)) {

			throw new IOException("Error al escribir la imagen en el archivo. Formato no soportado: " + format);

		}

	}

	public static void exportarFramesGif(String filePath, String outputFolder) {

		exportarFramesGif(filePath, outputFolder, "", "png", true);

	}

	public static void exportarFramesGif(String filePath, String outputFolder, String extension) {

		exportarFramesGif(filePath, outputFolder, "", extension, true);

	}

	public static void exportarFramesGif(String filePath, String outputFolder, String name, String extension) {

		exportarFramesGif(filePath, outputFolder, name, extension, true);

	}

	public static void optimizarGif(String input, String output) {

		AnimatedGifEncoder e = new AnimatedGifEncoder();

		e.start(output);

		e.setFrameRate(25);

		GifDecoder d = new GifDecoder();

		d.read(input);

		for (int i = 0; i < d.getFrameCount(); i++) {

			e.addFrame(d.getFrame(i));

		}

		e.finish();

	}

	public static void exportarFramesGif(String filePath, String outputFolder, String name, String extension,
			boolean openOutput) {

		outputFolder = pasarDeArchivoACarpeta(outputFolder);

		extension = extension.toLowerCase();

		boolean jpg = extension.equals("jpg");

		if (name == null || name.equals("")) {

			name = "_frame_";

		}

		if (jpg || extension == null || extension.equals("")) {

			extension = "png";

		}

		if (outputFolder == null || outputFolder.equals("")) {

			outputFolder = filePath.substring(0, filePath.lastIndexOf(saberSeparador()) + 1);

		}

		else if (!new File(outputFolder).exists()) {

			crearCarpeta(outputFolder);

		}

		if (esGifAnimado(filePath)) {

			GifDecoder d = new GifDecoder();

			d.read(filePath);

			BufferedImage frame;

			int contador = 1;

			for (int i = 0; i < d.getFrameCount(); i++) {

				frame = d.getFrame(i);

				try {

					exportBufferedImage(frame, extension,
							ponerSeparador(outputFolder) + name + contador + "." + extension);

					if (jpg) {

						convertPNGtoJPG(ponerSeparador(outputFolder) + name + contador + "." + extension,
								ponerSeparador(outputFolder) + name + contador + "." + "jpg");

						eliminarArchivo(ponerSeparador(outputFolder) + name + contador + "." + extension);

					}

				}

				catch (Exception e) {

				}

				contador++;

			}

			if (openOutput) {

				abrir(outputFolder);

			}

		}

	}

	public static boolean esGifAnimado(String filePath) {

		GifDecoder d = new GifDecoder();

		d.read(filePath);

		return d.getLoopCount() > -1;

	}

	/**
	 * Trunca un número decimal al número especificado de dígitos decimales.
	 * 
	 * @param number  el número decimal a truncar.
	 * @param digitos el número de dígitos decimales a mantener.
	 * @return el número truncado con el número especificado de dígitos decimales.
	 */

	public static double truncateDecimal(double number, int digitos) {

		double raiz = 10;

		double multiplicador = Math.pow(raiz, digitos);

		double resultado = ((int) (number * multiplicador)) / multiplicador;

		return Double.parseDouble(String.format("%." + digitos + "f", resultado).replace(",", "."));

	}

	/**
	 * Trunca un número decimal al número especificado de dígitos decimales.
	 * 
	 * @param number  el número decimal a truncar.
	 * @param digitos el número de dígitos decimales a mantener.
	 * @return el número truncado con el número especificado de dígitos decimales.
	 */

	public static float truncateFloat(float number, int digitos) {

		double raiz = 10;

		double multiplicador = Math.pow(raiz, digitos);

		double resultado = ((int) (number * multiplicador)) / multiplicador;

		return Float.parseFloat(String.format("%." + digitos + "f", resultado).replace(",", "."));

	}

	public static BufferedImage fileToBufferedImage(File file) throws IOException {

		return ImageIO.read(file);

	}

	public static String limpiarEspacios(String cadena, boolean eliminarEspacios) {

		if (cadena == null || cadena.isEmpty()) {

			return cadena;

		}

		cadena = cadena.trim();

		StringBuilder resultado = new StringBuilder();

		boolean espacioPrevio = false;

		for (char c : cadena.toCharArray()) {

			if (Character.isWhitespace(c)) {

				if (!eliminarEspacios && !espacioPrevio) {

					resultado.append(c);

					espacioPrevio = true;

				}

			}

			else {

				resultado.append(c);

				espacioPrevio = false;

			}

		}

		return resultado.toString();

	}

	/**
	 * Convierte la primera letra de una cadena a mayúscula.
	 * 
	 * @param cadena la cadena de entrada.
	 * @return la cadena con la primera letra en mayúscula.
	 */
	public static String primeraLetraMayuscula(String cadena) {

		if (cadena == null || cadena.isEmpty()) {

			return cadena;

		}

		return Character.toUpperCase(cadena.charAt(0)) + cadena.substring(1);

	}

	public static String readFile(String filePath) throws IOException {

		StringBuilder content = new StringBuilder();

		try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {

			String line;

			while ((line = br.readLine()) != null) {

				content.append(line).append("\n");

			}

		}

		return content.toString();

	}

	public static void writeFile(String filePath, String content) throws IOException {

		try (BufferedWriter bw = new BufferedWriter(new FileWriter(filePath))) {

			bw.write(content);

		}

	}

	public static double dividir(int a, int b) {

		if (b == 0) {

			throw new ArithmeticException("Division by zero is not allowed");

		}

		return (double) a / b;

	}

	public static int valorMaximo(int a, int b) {

		return (a > b) ? a : b;

	}

	public static int valorMinimo(int a, int b) {

		return (a < b) ? a : b;

	}

	/**
	 * Calcula el valor de la sucesión geométrica para un índice dado a partir de un
	 * parámetro de texto.
	 * 
	 * @param parametro Una cadena de texto que contiene pares de índice y valor en
	 *                  el formato "1#2,2#4,3#8,4#16". El formato debe ser
	 *                  exactamente como se describe, con pares separados por comas
	 *                  y los índices y valores separados por el símbolo '#'.
	 * @param n         El índice para el cual se desea calcular el valor de la
	 *                  sucesión geométrica. Debe ser un entero positivo.
	 * @return El valor de la sucesión geométrica en el índice dado. Si hay un error
	 *         en el formato del parámetro o en el valor de n, se retorna null.
	 */

	public static Integer crearSucesionGeometrica(String parametro, int n) {

		Integer resultado = null;

		if (parametro == null || parametro.isEmpty()) {

			System.err.println("El parámetro no puede ser nulo o vacío.");

		}

		else if (n <= 0) {

			System.err.println("El índice n debe ser un entero positivo.");

		}

		else {

			String[] pares = parametro.split(",");

			int[] indices = new int[pares.length];

			int[] valores = new int[pares.length];

			boolean formatoIncorrecto = false;

			for (int i = 0; i < pares.length && !formatoIncorrecto; i++) {

				String[] partes = pares[i].split("#");

				if (partes.length != 2) {

					System.err.println("El formato del parámetro es incorrecto en: " + pares[i]);

					formatoIncorrecto = true;

				}

				else {

					try {

						indices[i] = Integer.parseInt(partes[0]);

						valores[i] = Integer.parseInt(partes[1]);

					}

					catch (NumberFormatException e) {

						System.err.println("El formato de número es incorrecto en: " + pares[i]);

						formatoIncorrecto = true;

					}

				}

			}

			if (!formatoIncorrecto && indices.length >= 2) {

				double razon = (double) valores[1] / valores[0];

				resultado = (int) (valores[0] * Math.pow(razon, n - 1));

			}

			else if (indices.length < 2) {

				System.err.println("Se requieren al menos dos pares de valores para calcular la fórmula.");

			}

		}

		return resultado;

	}

	/**
	 * Calcula el valor de la sucesión aritmética para un índice dado a partir de un
	 * parámetro de texto.
	 * 
	 * @param parametro Una cadena de texto que contiene pares de índice y valor en
	 *                  el formato "1#2,2#4,3#8,4#16". El formato debe ser
	 *                  exactamente como se describe, con pares separados por comas
	 *                  y los índices y valores separados por el símbolo '#'.
	 * @param n         El índice para el cual se desea calcular el valor de la
	 *                  sucesión geométrica. Debe ser un entero positivo.
	 * @return El valor de la sucesión geométrica en el índice dado. Si hay un error
	 *         en el formato del parámetro o en el valor de n, se retorna null.
	 */

	public static Integer calcularSucesionAritmetica(String parametro, int n) {

		Integer resultado = null;

		if (parametro == null || parametro.isEmpty()) {

			System.err.println("El parámetro no puede ser nulo o vacío.");

		}

		else if (n <= 0) {

			System.err.println("El índice n debe ser un entero positivo.");

		}

		else {

			String[] pares = parametro.split(",");

			int[] indices = new int[pares.length];

			int[] valores = new int[pares.length];

			boolean formatoIncorrecto = false;

			for (int i = 0; i < pares.length && !formatoIncorrecto; i++) {

				String[] partes = pares[i].split("#");

				if (partes.length != 2) {

					System.err.println("El formato del parámetro es incorrecto en: " + pares[i]);

					formatoIncorrecto = true;

				}

				else {

					try {

						indices[i] = Integer.parseInt(partes[0]);

						valores[i] = Integer.parseInt(partes[1]);

					}

					catch (NumberFormatException e) {

						System.err.println("El formato de número es incorrecto en: " + pares[i]);

						formatoIncorrecto = true;

					}

				}

			}

			if (!formatoIncorrecto && indices.length >= 2) {

				int constante = (valores[1] - valores[0]) / (indices[1] - indices[0]);

				resultado = constante * (n - 1);

			}

			else if (indices.length < 2) {

				System.err.println("Se requieren al menos dos pares de valores para calcular la fórmula.");

			}

		}

		return resultado;

	}

	public String enteroACadena(int numero) {

		return String.valueOf(numero);

	}

	public String floatACadena(float numero) {

		return String.valueOf(numero);

	}

	public String doubleACadena(double numero) {

		return String.valueOf(numero);

	}

	public static char asciiToChar(int codigoAscii) {

		return (char) codigoAscii;

	}

	public static int charToAscii(char caracter) {

		return (int) caracter;

	}

	public int cadenaAEntero(String cadena) {

		return Integer.parseInt(cadena);

	}

	public static boolean esPar(int numero) {

		return numero % 2 == 0;

	}

	public static double redondearDouble(double numero, int decimales) {

		if (decimales < 0) {

			throw new IllegalArgumentException("El número de decimales no puede ser negativo.");

		}

		BigDecimal bigDecimal = new BigDecimal(Double.toString(numero));

		bigDecimal = bigDecimal.setScale(decimales, RoundingMode.HALF_UP);

		return bigDecimal.doubleValue();

	}

	public static void convertJPGtoPNG(String jpgFilePath, String pngFilePath) throws IOException {

		BufferedImage bufferedImage = ImageIO.read(new File(jpgFilePath));

		BufferedImage newBufferedImage = new BufferedImage(bufferedImage.getWidth(), bufferedImage.getHeight(),
				BufferedImage.TYPE_INT_ARGB);

		newBufferedImage.createGraphics().drawImage(bufferedImage, 0, 0, java.awt.Color.WHITE, null);

		ImageIO.write(newBufferedImage, "png", new File(pngFilePath));
	}

	public static void convertPNGtoJPG(String pngFilePath, String jpgFilePath) throws IOException {

		BufferedImage bufferedImage = ImageIO.read(new File(pngFilePath));

		BufferedImage newBufferedImage = new BufferedImage(bufferedImage.getWidth(), bufferedImage.getHeight(),
				BufferedImage.TYPE_INT_RGB);

		newBufferedImage.createGraphics().drawImage(bufferedImage, 0, 0, java.awt.Color.WHITE, null);

		ImageIO.write(newBufferedImage, "jpg", new File(jpgFilePath));

	}

	public static JTextPane centrarJTextPane(JTextPane textPane) {

		try {

			textPane.setDocument(centrarJTextPane());

		} catch (Exception e) {

		}

		return textPane;

	}

	public static StyledDocument centrarJTextPane() {

		StyleContext context = new StyleContext();

		Style style = context.getStyle(StyleContext.DEFAULT_STYLE);

		StyleConstants.setAlignment(style, StyleConstants.ALIGN_CENTER);

		return new DefaultStyledDocument(context);

	}

	public static void centrarJFrame(JFrame frame) {

		try {

			frame.setLocationRelativeTo(null);

		}

		catch (Exception e) {

		}

	}

	public static void redondearComboBx(JComboBox<?> comboBox) {

		try {

			DefaultListCellRenderer listRenderer = new DefaultListCellRenderer();

			listRenderer.setHorizontalAlignment(DefaultListCellRenderer.CENTER);

			comboBox.setRenderer(listRenderer);

		}

		catch (Exception e) {

		}

	}

	public static float redondearFloat(float numero, int decimales) {

		if (decimales < 0) {

			throw new IllegalArgumentException("El número de decimales no puede ser negativo.");

		}

		BigDecimal bigDecimal = new BigDecimal(Float.toString(numero));

		bigDecimal = bigDecimal.setScale(decimales, RoundingMode.HALF_UP);

		return bigDecimal.floatValue();

	}

	public static int dividirYRedondear(int numerador, int denominador) {

		if (denominador == 0) {

			throw new ArithmeticException("El denominador no puede ser cero.");

		}

		int resultado = numerador / denominador;

		if (numerador % denominador > 0) {

			resultado += 1;

		}

		return resultado;

	}

	public static boolean isEnter(KeyEvent e) {

		return (e.getKeyCode() == KeyEvent.VK_ENTER) ? true : false;

	}

	public static boolean scrollHaciaAbajo(MouseWheelEvent e) {

		return (e.getWheelRotation() == 1) ? true : false;

	}

	public static ArrayList<String> selectSQlite(String dbName, String query, List<String> columns) {

		String url = "jdbc:sqlite:" + dbName;

		ArrayList<String> resultados = new ArrayList<>();

		try {

			Class.forName("org.sqlite.JDBC");

			Connection connection = DriverManager.getConnection(url);

			Statement statement = connection.createStatement();

			ResultSet resultSet = statement.executeQuery(query);

			while (resultSet.next()) {

				for (int i = 0; i < columns.size(); i++) {

					resultados.add(resultSet.getString(columns.get(i)));

				}

			}

			resultSet.close();

			statement.close();

			connection.close();

		}

		catch (Exception e) {

		}

		return resultados;

	}

	public static void insertSQLite(String db, String table, List<String> columns, List<String> values) {

		try (Connection conn = DriverManager.getConnection("jdbc:sqlite:" + db);

				PreparedStatement pstmt = conn
						.prepareStatement("INSERT INTO " + table + " (" + String.join(",", columns) + ") VALUES ("
								+ values.stream().map(v -> "?").collect(Collectors.joining(",")) + ")")) {

			for (int i = 0; i < values.size(); i++) {

				pstmt.setString(i + 1, values.get(i));

			}

			pstmt.executeUpdate();

		}

		catch (SQLException e) {

		}

	}

	public static int encontrarAparicion(String cadena, String subcadena, int indice) {

		int aparicionesEncontradas = 0;

		int posicion = -1;

		while (aparicionesEncontradas < indice) {

			posicion = cadena.indexOf(subcadena, posicion + 1);

			if (posicion == -1) {

				return -1;
			}

			aparicionesEncontradas++;

		}

		return posicion;

	}

	public static List<Integer> encontrarPosiciones(String texto, String cadena) {

		List<Integer> posiciones = new ArrayList<>();

		int indice = texto.indexOf(cadena);

		while (indice >= 0) {

			posiciones.add(indice);

			indice = texto.indexOf(cadena, indice + 1);

		}

		return posiciones;

	}

	public static String reemplazarPosiciones(String texto, String textoAReemplazar, List<String> reemplazos) {

		List<Integer> posiciones = encontrarPosiciones(texto, textoAReemplazar);

		StringBuilder mensaje = new StringBuilder(texto);

		int offset = 0;

		for (int i = 0; i < posiciones.size(); i++) {

			String reemplazo = reemplazos.get(i % reemplazos.size());

			int posicionActual = posiciones.get(i) + offset;

			mensaje.replace(posicionActual, posicionActual + textoAReemplazar.length(), reemplazo);

			offset += reemplazo.length() - textoAReemplazar.length();

		}

		return mensaje.toString();

	}

	public static String ListaAString(List<String> lista, String separador) {

		LinkedList<String> archivos = lista.stream().filter(path -> !Files.isDirectory(Paths.get(path)))
				.collect(Collectors.toCollection(LinkedList::new));

		return String.join(separador, archivos);

	}

	public static BufferedImage loadImage(String imagePath) {

		try {

			return ImageIO.read(new File(imagePath));

		}

		catch (IOException e) {

			return null;

		}

	}

	public static void renombrarPorExtension(String carpeta, String extensionEntrada, String extensionSalida,
			boolean borrar) {

		extensionEntrada = extensionEntrada.replace(".", "");

		extensionSalida = extensionSalida.replace(".", "");

		LinkedList<String> lista = (LinkedList<String>) listar(carpeta, extensionEntrada, false, true);

		boolean imagen = false;

		switch (extensionEntrada) {

		case "jpg":

		case "png":

		case "bmp":

		case "gif":

		case "jpeg":

		case "apng":

		case "webp":

		case "jfif":

		case "avif":

			imagen = true;

			break;

		default:
			break;

		}

		File archivo;

		for (String texto : lista) {

			archivo = new File(texto);

			try {

				if (imagen) {

					convertImg(texto, extensionSalida);

					if (borrar) {

						archivo.delete();

					}

				}

				else {

					archivo.renameTo(new File(texto.substring(0, texto.lastIndexOf(".") + 1) + extensionSalida));

				}

			}

			catch (Exception e) {

			}

		}

	}

	public static void mostrarArchivosDeLinux(String path) {

		LinkedList<String> lista = (LinkedList<String>) listar(path, "all", false, true);

		String salida = "";

		String salida2 = "";

		for (String texto : lista) {

			try {

				if ((texto.substring(texto.lastIndexOf("/") + 1, texto.lastIndexOf("/") + 2).equals("."))) {

					salida = texto.substring(0, texto.lastIndexOf("/") + 1);

					salida2 = texto.substring(texto.lastIndexOf("/") + 2, texto.length());

					new File(texto).renameTo(new File(salida + salida2));

				}

			}

			catch (Exception e) {

			}

		}

	}

	public static void mostrarArchivosOcultosDeLinux(String path, String extension) {

		LinkedList<String> lista = (LinkedList<String>) listar(path, extension, false, true);

		String salida = "";

		String salida2 = "";

		for (String texto : lista) {

			try {

				if ((texto.substring(texto.lastIndexOf("/") + 1, texto.lastIndexOf("/") + 2).equals("."))) {

					salida = texto.substring(0, texto.lastIndexOf("/") + 1);

					salida2 = texto.substring(texto.lastIndexOf("/") + 2, texto.length());

					new File(texto).renameTo(new File(salida + salida2));

				}

			}

			catch (Exception e) {

			}

		}

	}

	public static String ponerSeparador(String texto) {

		if (!texto.endsWith(saberSeparador())) {

			texto += saberSeparador();

		}

		return texto;

	}

	public static String findLongestString(List<String> list) {

		String longestString = null;

		int maxLength = -1;

		for (String str : list) {

			if (str.length() > maxLength) {

				maxLength = str.length();

				longestString = str;

			}

		}

		return longestString;

	}

	private JMthos() {

	}

	public static int calcularPorcentaje(int valor, int total) {

		float resultado = (valor * 100) / total;

		int salida;

		NumberFormat numberFormat = NumberFormat.getInstance();

		numberFormat.setMaximumFractionDigits(0);

		numberFormat.setRoundingMode(RoundingMode.DOWN);

		salida = Integer.parseInt(numberFormat.format(resultado));

		return salida;

	}

	public static String aumentarDia(int valor) {

		Calendar calendar = Calendar.getInstance();

		calendar.setTime(new Date());

		int calendarTime = Calendar.DAY_OF_MONTH;

		int temp = calendar.get(calendarTime);

		calendar.set(calendarTime, temp + valor);

		Date newDate = calendar.getTime();

		return newDate.toString();

	}

	public static String saberNombreArchivoConExtension(String archivo) {

		String resultado = "";

		try {

			resultado = archivo.substring(archivo.lastIndexOf(saberSeparador()) + 1, archivo.length());

		}

		catch (Exception e) {

		}

		return resultado;

	}

	public static BufferedImage iconToBufferedImage(Icon icon) {

		int width = icon.getIconWidth();

		int height = icon.getIconHeight();

		BufferedImage bufferedImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);

		Graphics graphics = bufferedImage.getGraphics();

		Component c = new Component() {

			private static final long serialVersionUID = 1L;

		};

		icon.paintIcon(c, graphics, 0, 0);

		graphics.dispose();

		return bufferedImage;

	}

	public static Point getSizeOfImage(BufferedImage originalImage, int newWidth, int newHeight, boolean resize) {

		Point punto;

		int originalWidth = originalImage.getWidth();

		int originalHeight = originalImage.getHeight();

		if (resize) {

			double widthRatio = (double) newWidth / originalWidth;

			double heightRatio = (double) newHeight / originalHeight;

			double scaleFactor = Math.min(widthRatio, heightRatio);

			punto = new Point((int) (originalWidth * scaleFactor), (int) (originalHeight * scaleFactor));

		}

		else {

			punto = new Point(originalWidth, originalHeight);

		}

		return punto;

	}

	public static BufferedImage loadFileImage(String image) {

		try {

			return javax.imageio.ImageIO.read(new File(image));

		}

		catch (Exception e) {

			return null;

		}

	}

	public static void copy(String text) {

		try {

			Clipboard clipboard = getSystemClipboard();

			clipboard.setContents(new StringSelection(text), null);

		}

		catch (Exception e) {

		}

	}

	public static boolean tieneCaracterNoImprimible(int keyCode) {

		return !(keyCode != 112 && keyCode != 113 && keyCode != 114 && keyCode != 115 && keyCode != 116

				&& keyCode != 117 && keyCode != 19 && keyCode != 118 && keyCode != 119 && keyCode != 120

				&& keyCode != 121 && keyCode != 122 && keyCode != 123 && keyCode != 27 && keyCode != 16 && keyCode != 17

				&& keyCode != 18 && keyCode != 65406 && keyCode != 155 && keyCode != 127 && keyCode != 33

				&& keyCode != 34 && keyCode != 20 && keyCode != 35 && keyCode != 36 && keyCode != 144 && keyCode != 37

				&& keyCode != 38 && keyCode != 39 && keyCode != 40);

	}

	public static String calcularNumeroEspacios(int numeroEspacios) {

		String resultado = "";

		for (int i = 0; i < numeroEspacios; i++)

			resultado = String.valueOf(resultado) + " ";

		return resultado;

	}

	public static <T> LinkedList<T> convertirArrayListALinkedList(ArrayList<T> arrayList) {

		LinkedList<T> linkedList = new LinkedList<>(arrayList);

		return linkedList;

	}

	public static <T> ArrayList<T> convertirLinkedListAArrayList(LinkedList<T> linkedList) {

		ArrayList<T> arrayList = new ArrayList<>(linkedList);

		return arrayList;

	}

	public static double calcularSeno(double angulo) {

		return Math.sin(Math.toRadians(angulo));

	}

	public static double calcularCoseno(double angulo) {

		return Math.cos(Math.toRadians(angulo));

	}

	public static double calcularTangente(double angulo) {

		return Math.tan(Math.toRadians(angulo));

	}

	public static Image resizeImage(Image originalImage, int newWidth, int newHeight) {

		BufferedImage resizedImage = new BufferedImage(newWidth, newHeight, BufferedImage.TYPE_INT_ARGB);

		Graphics2D graphics2D = resizedImage.createGraphics();

		graphics2D.drawImage(originalImage, 0, 0, newWidth, newHeight, null);

		graphics2D.dispose();

		return resizedImage;

	}

	public static BufferedImage resizeImage(BufferedImage originalImage, int newWidth, int newHeight) {

		BufferedImage resizedImage = new BufferedImage(newWidth, newHeight, BufferedImage.TYPE_INT_ARGB);

		Graphics2D graphics2D = resizedImage.createGraphics();

		graphics2D.drawImage(originalImage, 0, 0, newWidth, newHeight, null);

		graphics2D.dispose();

		return resizedImage;

	}

	public static BufferedImage resizeImage(String path, int newWidth, int newHeight) {

		BufferedImage originalImage;

		try {

			originalImage = ImageIO.read(new File(path));

			int originalWidth = originalImage.getWidth();

			int originalHeight = originalImage.getHeight();

			double widthRatio = (double) newWidth / originalWidth;

			double heightRatio = (double) newHeight / originalHeight;

			double scaleFactor = Math.min(widthRatio, heightRatio);

			return new BufferedImage((int) (originalWidth * scaleFactor), (int) (originalHeight * scaleFactor),
					originalImage.getType());

		}

		catch (Exception e) {

			return null;

		}

	}

	public static boolean cumpleLaExpresionRegular(String texto, String patron) {

		return Pattern.compile(patron, Pattern.CASE_INSENSITIVE).matcher(texto).find();

	}

	public static LocalDate hoy(String separador, boolean english, boolean zero) {

		return LocalDate.parse(saberFechaActual(separador, english, zero), DateTimeFormatter.ISO_LOCAL_DATE);

	}

	public static String saberFechaActual(String separador, boolean english, boolean zero) {

		Calendar c = Calendar.getInstance();

		String mes = Integer.toString(c.get(Calendar.MONTH) + 1);

		if (Integer.parseInt(mes) < 10 && zero) {

			mes = "0" + mes;

		}

		String dia = Integer.toString(c.get(Calendar.DATE) + 1);

		if (Integer.parseInt(dia) < 10 && zero) {

			dia = "0" + dia;

		}

		if (english) {

			return c.get(Calendar.YEAR) + separador + mes + separador + dia;

		}

		else {

			return dia + separador + mes + separador + c.get(Calendar.YEAR);

		}

	}

	public static boolean convertImg(String rutaAbsoluta, String extension) {

		boolean result = false;

		try {

			extension = extension.toLowerCase();

			FileInputStream inputStream = new FileInputStream(rutaAbsoluta);

			FileOutputStream outputStream = new FileOutputStream(
					rutaAbsoluta.substring(0, rutaAbsoluta.lastIndexOf(".") + 1) + extension);

			BufferedImage inputImage = ImageIO.read(inputStream);

			result = ImageIO.write(inputImage, extension, outputStream);

			outputStream.close();

			inputStream.close();

		}

		catch (Exception e) {

		}

		return result;

	}

	public static void convertirImagen(String extensionEntrada, String extensionSalida, String folder)
			throws IOException {

		LinkedList<String> imagenesPng = (LinkedList<String>) listar(folder, extensionEntrada, false, true);

		File beforeFile;

		File afterFile;

		for (int i = 0; i < imagenesPng.size(); i++) {

			beforeFile = new File(imagenesPng.get(i));

			afterFile = new File(
					imagenesPng.get(i).substring(0, imagenesPng.get(i).lastIndexOf(".") + 1) + extensionSalida);

			BufferedImage beforeImg = ImageIO.read(beforeFile);

			BufferedImage afterImg = new BufferedImage(beforeImg.getWidth(), beforeImg.getHeight(),
					BufferedImage.TYPE_INT_RGB);

			afterImg.createGraphics().drawImage(beforeImg, 0, 0, Color.white, null);

			ImageIO.write(afterImg, extensionSalida, afterFile);

			eliminarFichero(imagenesPng.get(i));

		}

	}

	static byte[] createChecksum(String filename) throws NoSuchAlgorithmException, IOException {

		InputStream fis = null;

		MessageDigest complete = MessageDigest.getInstance("SHA-256");

		try {

			fis = new FileInputStream(filename);

			byte[] buffer = new byte[1024];

			int numRead;

			do {

				numRead = fis.read(buffer);

				if (numRead > 0) {

					complete.update(buffer, 0, numRead);

				}

			}

			while (numRead != -1);

			fis.close();

		}

		catch (IOException e) {

			if (fis != null) {

				fis.close();

			}

		}

		return complete.digest();

	}

	public static String getSHA256Checksum(String filename) {

		String result = "";

		try {

			byte[] b;

			b = createChecksum(filename);

			StringBuilder bld = new StringBuilder();

			for (int i = 0; i < b.length; i++) {

				bld.append(Integer.toString((b[i] & 0xff) + 0x100, 16).substring(1));

			}

			result = bld.toString();

		}

		catch (Exception e) {

		}

		return result;

	}

	public static void resizeImage(String inputImagePath, String outputImagePath, int scaledWidth, int scaledHeight)
			throws IOException {

		File inputFile = new File(inputImagePath);

		BufferedImage inputImage = ImageIO.read(inputFile);

		BufferedImage outputImage = new BufferedImage(scaledWidth, scaledHeight, inputImage.getType());

		Graphics2D g2d = outputImage.createGraphics();

		g2d.drawImage(inputImage, 0, 0, scaledWidth, scaledHeight, null);

		g2d.dispose();

		String formatName = outputImagePath.substring(outputImagePath.lastIndexOf(".") + 1);

		ImageIO.write(outputImage, formatName, new File(outputImagePath));

	}

	public static void eliminarFichero(String archivo) throws IOException {

		File fichero = new File(archivo);

		if (fichero.exists()) {

			if (fichero.isFile()) {

				fichero.delete();

			}

			else {

				FileUtils.deleteDirectory(new File(archivo));

			}

		}

	}

	public static void webp2Png(boolean png, String src, String dest, boolean eliminarArchivo) throws IOException {

		try {

			if (png) {

				WebpIO.create().toNormalImage(src, dest);

			}

			else {

				WebpIO.create().toWEBP(src, dest);

			}

			if (eliminarArchivo) {

				eliminarFichero(src);

			}

		}

		catch (Exception e) {

		}

	}

	public static List<String> listarConLista(String ruta, List<String> lista, boolean carpeta) {

		LinkedList<String> list = new LinkedList<>();

		File f = new File(ruta);

		if (f.exists()) {

			File[] ficheros = f.listFiles();

			String fichero = "";

			String extensionArchivo;

			File folder;

			for (int x = 0; x < ficheros.length; x++) {

				fichero = ficheros[x].getName();

				folder = new File(fichero);

				extensionArchivo = saberExtension(fichero);

				if (carpeta && folder.isDirectory()) {

					list.add(fichero);

				}

				else if (!carpeta && folder.isFile() && lista.contains(extensionArchivo)) {

					list.add(ruta + fichero);

				}

			}

		}

		Collections.sort(list);

		return list;

	}

	public static void png2Jpg(String file) throws IOException {

		File beforeFile = new File(file);

		File afterFile = new File(file.substring(0, file.lastIndexOf(".")) + ".jpg");

		BufferedImage beforeImg = ImageIO.read(beforeFile);

		BufferedImage afterImg = new BufferedImage(beforeImg.getWidth(), beforeImg.getHeight(),
				BufferedImage.TYPE_INT_RGB);

		afterImg.createGraphics().drawImage(beforeImg, 0, 0, Color.WHITE, null);

		ImageIO.write(afterImg, "jpg", afterFile);

		eliminarFichero(file);

	}

	public static boolean esImagen(String file) {

		boolean resultado = false;

		try {

			if (ImageIO.read(new File(file)) != null) {

				resultado = true;

			}

		}

		catch (Exception e) {

		}

		return resultado;

	}

	public static void png2JpgInFolder(String folder) throws IOException {

		LinkedList<String> imagenesPng = new LinkedList<String>();

		imagenesPng = (LinkedList<String>) listar(folder, "png", false, true);

		File beforeFile;

		File afterFile;

		for (int i = 0; i < imagenesPng.size(); i++) {

			beforeFile = new File(imagenesPng.get(i));

			afterFile = new File(imagenesPng.get(i).substring(0, imagenesPng.get(i).lastIndexOf(".")) + ".jpg");

			BufferedImage beforeImg = ImageIO.read(beforeFile);

			BufferedImage afterImg = new BufferedImage(beforeImg.getWidth(), beforeImg.getHeight(),
					BufferedImage.TYPE_INT_RGB);

			afterImg.createGraphics().drawImage(beforeImg, 0, 0, Color.WHITE, null);

			ImageIO.write(afterImg, "jpg", afterFile);

			eliminarFichero(imagenesPng.get(i));

		}

	}

	public static List<String> listar(String ruta, String extension, boolean carpeta, boolean absolutePath) {

		if (!ruta.endsWith(saberSeparador())) {

			ruta += saberSeparador();

		}

		if (extension == null) {

			extension = "all";

		}

		LinkedList<String> lista = new LinkedList<>();

		File f = new File(ruta);

		if (f.exists()) {

			File[] ficheros = f.listFiles();

			String fichero = "";

			String extensionArchivo;

			File folder;

			for (int x = 0; x < ficheros.length; x++) {

				fichero = ficheros[x].getName();

				folder = new File(ruta + fichero);

				extensionArchivo = saberExtension(fichero);

				if (carpeta && folder.isDirectory()) {

					saberSiEsRutaAbsoluta(ruta, absolutePath, lista, fichero);

				}

				else if (!carpeta && folder.isFile()) {

					switch (extension) {

					case "all":

						saberSiEsRutaAbsoluta(ruta, absolutePath, lista, fichero);

						break;

					case "videos":

						if (esVideo(ruta + fichero)) {

							saberSiEsRutaAbsoluta(ruta, absolutePath, lista, fichero);

						}

						break;

					case "images":

						if (esImagen(ruta + fichero)) {

							saberSiEsRutaAbsoluta(ruta, absolutePath, lista, fichero);

						}

						break;

					default:

						if (extension.equals(extensionArchivo)) {

							saberSiEsRutaAbsoluta(ruta, absolutePath, lista, fichero);

						}

						break;

					}

				}

			}

		}

		Collections.sort(lista);

		return lista;

	}

	public static List<String> listarConArray(String ruta, String[] lista, boolean carpeta, boolean absolutePath) {

		if (!ruta.endsWith(saberSeparador())) {

			ruta += saberSeparador();

		}

		LinkedList<String> list = new LinkedList<>();

		File f = new File(ruta);

		if (f.exists()) {

			File[] ficheros = f.listFiles();

			String fichero = "";

			String extensionArchivo;

			File folder;

			for (int x = 0; x < ficheros.length; x++) {

				fichero = ficheros[x].getName();

				if (carpeta) {

					folder = new File(fichero);

				}

				else {

					folder = new File(ruta + fichero);

				}

				extensionArchivo = saberExtension(fichero);

				if ((!carpeta && folder.isFile() &&

						Arrays.asList(lista).contains(extensionArchivo)) || (carpeta && folder.isDirectory())) {

					saberSiEsRutaAbsoluta(ruta, absolutePath, list, fichero);

				}

			}

		}

		Collections.sort(list);

		return list;

	}

	static void saberSiEsRutaAbsoluta(String ruta, boolean absolutePath, LinkedList<String> list, String fichero) {

		if (absolutePath) {

			list.add(ruta + fichero);

		}

		else {

			list.add(fichero);

		}

	}

	public static String eliminarEspacios(String cadena, boolean filtro) {

		cadena = cadena.trim();

		cadena = cadena.replace("   ", "  ");

		cadena = cadena.replace("  ", " ");

		if (filtro) {

			cadena = cadena.replace(" ", "");

		}

		return cadena;

	}

	public static String saberFechaYHoraActual(String pattern) {

		DateTimeFormatter dtf = DateTimeFormatter.ofPattern(pattern);

		return dtf.format(LocalDateTime.now());

	}

	public static String saberFechaActual() {

		Calendar c = Calendar.getInstance();

		return c.get(Calendar.YEAR) + "-" + c.get(Calendar.MONTH) + 1 + "-" + c.get(Calendar.DATE);

	}

	public static String saberFechaYHoraActual(boolean englishFormat) {

		String patron = "yyyy/MM/dd HH:mm:ss";

		if (!englishFormat) {

			patron = "dd/MM/yyyy HH:mm:ss";

		}

		DateTimeFormatter dtf = DateTimeFormatter.ofPattern(patron);

		return dtf.format(LocalDateTime.now());

	}

	public static boolean tieneExtensionDeImagen(String absolutePath) {

		boolean resultado = false;

		switch (saberExtension(absolutePath)) {

		case "jpg":

		case "png":

		case "bmp":

		case "gif":

		case "jpeg":

		case "apng":

		case "webp":

		case "jfif":

		case "avif":

			resultado = true;

			break;

		default:

			break;

		}

		return resultado;

	}

	public static boolean esVideo(String absolutePath) {

		boolean resultado = false;

		switch (saberExtension(absolutePath)) {

		case "mp4":

		case "avi":

		case "mpg":

		case "mkv":

		case "mov":

		case "webm":

			resultado = true;

			break;

		default:

			break;

		}

		return resultado;

	}

	public static Image iconToImage(Icon icon) {

		if (icon instanceof ImageIcon) {

			return ((ImageIcon) icon).getImage();

		}

		else {

			int w = icon.getIconWidth();

			int h = icon.getIconHeight();

			GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();

			GraphicsDevice gd = ge.getDefaultScreenDevice();

			GraphicsConfiguration gc = gd.getDefaultConfiguration();

			BufferedImage image = gc.createCompatibleImage(w, h);

			Graphics2D g = image.createGraphics();

			icon.paintIcon(null, g, 0, 0);

			g.dispose();

			return image;

		}

	}

	public static Image getScaledImage(Image srcImg, int w, int h) {

		BufferedImage resizedImg = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);

		Graphics2D g2 = resizedImg.createGraphics();

		g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);

		g2.drawImage(srcImg, 0, 0, w, h, null);

		g2.dispose();

		return resizedImg;

	}

	public static ImageIcon resize(ImageIcon image, int width, int height) {

		BufferedImage bi = new BufferedImage(width, height, Transparency.TRANSLUCENT);

		Graphics2D g2d = bi.createGraphics();

		g2d.addRenderingHints(new RenderingHints(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY));

		g2d.drawImage(image.getImage(), 0, 0, width, height, null);

		g2d.dispose();

		return new ImageIcon(bi);

	}

	public static int contarFicherosPorCarpeta(String carpeta, String filtro) {

		File exCarpeta = new File(carpeta);

		int ocurrencias = 0;

		if (exCarpeta.isDirectory()) {

			String extension;

			String nombreArchivo;

			File folder;

			for (final File ficheroEntrada : exCarpeta.listFiles()) {

				nombreArchivo = ficheroEntrada.getName();

				extension = saberExtension(nombreArchivo);

				folder = new File(exCarpeta + saberSeparador() + nombreArchivo);

				if (!folder.isDirectory() && (extension.equals(filtro) || filtro.equals("."))) {

					ocurrencias++;

				}

			}

		}

		return ocurrencias;

	}

	public static int contarFicherosPorCarpeta(final File carpeta, String filtro) {

		int ocurrencias = 0;

		if (carpeta.isDirectory()) {

			String extension;

			String nombreArchivo;

			File folder;

			for (final File ficheroEntrada : carpeta.listFiles()) {

				nombreArchivo = ficheroEntrada.getName();

				extension = saberExtension(nombreArchivo);

				folder = new File(carpeta + saberSeparador() + nombreArchivo);

				if (!folder.isDirectory() && (extension.equals(filtro) || filtro.equals("."))) {

					ocurrencias++;

				}

			}

		}

		return ocurrencias;

	}

	public static void ejecutarProgramaDeWsl(String programa, boolean cmd) {

		ejecutarComando("wsl -e " + programa, cmd);

	}

	public static void ejecutarComando(String string, boolean cmd) {

		try {

			String cabecera = "";

			if (OS.contains("indow")) {

				cabecera = "cmd /c ";

				if (cmd) {

					cabecera = "cmd /c start cmd.exe /K \"";

				}

			}

			Runtime.getRuntime().exec(cabecera + string);

		}

		catch (Exception e) {

		}

	}

	public static void ejecutarJava(String string, boolean cmd) {

		try {

			String cabecera = "";

			if (OS.contains("indow")) {

				cabecera = "cmd /c ";

				if (cmd) {

					cabecera = "cmd /c start cmd.exe /K \"";

				}

			}

			Runtime.getRuntime().exec(cabecera + "cd " + string.substring(0, string.lastIndexOf(saberSeparador()))
					+ " && java -jar \"" + string.substring(string.lastIndexOf(saberSeparador()) + 1) + "\"");

		}

		catch (Exception e) {

		}

	}

	private static Clipboard getSystemClipboard() {

		Toolkit defaultToolkit = Toolkit.getDefaultToolkit();

		return defaultToolkit.getSystemClipboard();

	}

	public static String[] getFonts() {

		GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();

		return ge.getAvailableFontFamilyNames();

	}

	public static List<String> obtenerFuentes() {

		return Arrays.asList(getFonts());

	}

	public static String rutaActual() throws IOException {

		return new File(".").getCanonicalPath() + saberSeparador();

	}

	public static void abrir(String ruta) {

		if (ruta != null && !ruta.equals("") && !ruta.isEmpty()) {

			try {

				if (OS.contains("indows")) {

					Runtime.getRuntime().exec("cmd /c C:\\Windows\\explorer.exe " + "\"" + ruta + "\"");

				}

				else if (OS.contains("inux")) {

					Runtime.getRuntime().exec("xdg-open " + ruta);

				}

				else {

					Runtime.getRuntime().exec("open " + ruta);

				}

			}

			catch (IOException e) {

			}

		}

	}

	public static int crearCarpeta(String path) {

		int respuesta;

		try {

			respuesta = 200;

			new File(path).mkdir();

		}

		catch (Exception e) {

			respuesta = 300;

		}

		return respuesta;

	}

	public static String cleanURL(String url) {

		return url.replace("file:/", "");

	}

	public static String saberExtension(String nombreArchivo) {

		String extension = "";

		if (nombreArchivo.length() >= 3) {

			extension = nombreArchivo.substring(
					nombreArchivo.length() - nombreArchivo.substring(nombreArchivo.lastIndexOf(".") + 1).length());

		}

		return extension.toLowerCase();

	}

	public static double convertirASegundos(String duracionVideo) {

		double horas;

		double minutos;

		double segundos;

		try {

			horas = Double.parseDouble(duracionVideo.substring(0, duracionVideo.indexOf(":")));

			if (horas > 0) {

				horas *= 3600f;

			}

			minutos = Double.parseDouble(
					duracionVideo.substring(duracionVideo.indexOf(":") + 1, duracionVideo.lastIndexOf(":")));

			if (minutos > 0) {

				minutos *= 60f;

			}

			segundos = Double
					.parseDouble(duracionVideo.substring(duracionVideo.lastIndexOf(":") + 1, duracionVideo.length()));

		}

		catch (Exception e) {

			horas = 0;

			minutos = 0;

			segundos = 0;

		}

		return horas + minutos + segundos;

	}

	public static int contarOcurrencias(String text, String search) {

		int contador = 0;

		while (text.indexOf(search) > -1) {

			text = text.substring(text.indexOf(search) + search.length(), text.length());

			contador++;

		}

		return contador;

	}

	public static String calcularTiempo(long segundos) {

		String ceroHoras = "";

		String ceroMinutos = "";

		String ceroSegundos = "";

		int minutos = 0;

		int horas = 0;

		if (segundos == 60) {

			minutos = 1;

			segundos = 0;

		}

		else {

			minutos = (int) (segundos / 60);

			int calculoSegundos = 0;

			calculoSegundos = 60 * minutos;

			segundos -= calculoSegundos;

			if (minutos == 60) {

				horas = 1;

				minutos = 0;

				segundos = 0;

			}

			if (minutos > 60) {

				if (minutos % 60 == 0) {

					horas = minutos / 60;

					minutos = 0;

					segundos = 0;

				}

				else {

					int contador = 0;

					int horaProxima = 120;

					int siguienteHora = 0;

					while (contador == 0) {

						if (minutos < horaProxima) {

							contador = horaProxima;

						}

						else {

							siguienteHora = horaProxima + 60;

							if (minutos > horaProxima && minutos < siguienteHora) {

								contador = siguienteHora;

							}

							horaProxima = siguienteHora;

						}
					}

					horas = minutos / 60;

					minutos = 60 - (horaProxima - minutos);

				}

			}

			if (horas <= 9) {

				ceroHoras = "0";

			}

			if (minutos <= 9) {

				ceroMinutos = "0";

			}

			if (segundos <= 9) {

				ceroSegundos = "0";

			}

		}

		return ceroHoras + horas + " : " + ceroMinutos + minutos + " : " + ceroSegundos + segundos;

	}

	public static String saberNombreArchivo(String ruta) {

		String separador = saberSeparador();

		String resultado = "";

		if (ruta.contains(separador)) {

			resultado = ruta.substring(ruta.lastIndexOf(separador), ruta.lastIndexOf("."));

		}

		return resultado;

	}

	public static void moverArchivo(String origen, String destino) {

		try {

			Files.move(FileSystems.getDefault().getPath(origen), FileSystems.getDefault().getPath(destino),
					StandardCopyOption.REPLACE_EXISTING);

		}

		catch (Exception e) {

		}

	}

	public static void escribirFichero(String ruta, List<String> lines) {

		try {

			Path file = Paths.get(ruta);

			Files.write(file, lines, StandardCharsets.UTF_8);

		}

		catch (Exception ex) {

		}

	}

	public static List<String> leerArchivo(String ruta) {

		ArrayList<String> resultado = new ArrayList<>();

		try {

			FileInputStream fstream = new FileInputStream(ruta);

			BufferedReader br = new BufferedReader(new InputStreamReader(fstream));

			String strLine;

			while ((strLine = br.readLine()) != null) {

				resultado.add(strLine);

			}

			fstream.close();

		}

		catch (Exception e) {

		}

		return resultado;

	}

	public static String eliminarPuntos(String cadena) {

		String cadena2 = cadena;

		cadena2 = eliminarEspacios(cadena2, false);

		try {

			cadena2 = cadena.substring(0, cadena.lastIndexOf("."));

			cadena = cadena2.replace(".", "_") + "." + saberExtension(cadena);

		}

		catch (Exception e) {

		}

		return cadena;

	}

	public static String saberSeparador() {

		if (System.getProperty("os.name").contains("indows")) {

			return "\\";

		}

		else {

			return "/";

		}

	}

	public static String convertToUTF8(String s) {

		String out = "";

		out = new String(s.getBytes(StandardCharsets.UTF_8), StandardCharsets.ISO_8859_1);

		return out;

	}

	public static String directorioActual() {

		String resultado = "";

		try {

			resultado = new File(".").getCanonicalPath() + saberSeparador();

		}

		catch (IOException e) {

		}

		return resultado;

	}

	public static String[] leerFicheroArray(String rutaRelativa, int longitud) throws IOException {

		String[] salida = new String[longitud];

		String fichero = directorioActual() + rutaRelativa;

		File archivo = new File(fichero);

		if (archivo.exists()) {

			String texto = "";

			int i = 0;

			FileReader flE = null;

			BufferedReader fE = null;

			try {

				flE = new FileReader(fichero);

				fE = new BufferedReader(flE);

				texto = fE.readLine();

				while (texto != null && i < longitud) {

					salida[i] = texto;

					i++;

					texto = fE.readLine();

				}

				fE.close();

				flE.close();

			}

			catch (Exception e) {
				//
			}

			finally {

				if (fE != null) {

					try {
						fE.close();
					}

					catch (IOException e) {
						//
					}

				}

				if (flE != null) {

					try {

						flE.close();

					}

					catch (IOException e) {
						//
					}

				}
			}
		}

		else {

			throw new IOException();

		}

		return salida;

	}

	public static void reproducirSonido(String nombreSonido) {

		try {

			AudioInputStream audioInputStream = AudioSystem
					.getAudioInputStream(new File(nombreSonido).getAbsoluteFile());

			Clip clip = AudioSystem.getClip();

			clip.open(audioInputStream);

			clip.start();

		}

		catch (UnsupportedAudioFileException | IOException | LineUnavailableException ex) {
			//
		}

	}

	public static void eliminarArchivos(String ruta, String extension) throws IOException {

		LinkedList<String> frames = (LinkedList<String>) listar(ruta, extension, false, true);

		for (int i = 0; i < frames.size(); i++) {

			if (!frames.get(i).isEmpty()) {

				eliminarFichero(ruta + saberSeparador() + frames.get(i));
			}

		}

	}

	public static void eliminarArchivos(LinkedList<String> listaFicheros) throws IOException {

		for (int i = 0; i < listaFicheros.size(); i++) {

			if (!listaFicheros.get(i).isEmpty()) {

				eliminarFichero(listaFicheros.get(i));

			}

		}

	}

	static String readAll(Reader rd) throws IOException {

		StringBuilder sb = new StringBuilder();

		int cp;

		while ((cp = rd.read()) != -1) {

			sb.append((char) cp);

		}

		return sb.toString();

	}

	public static JSONObject readJsonFromUrl(String url) throws IOException {

		JSONObject resultado = null;

		try {

			InputStream is = new URL(url).openStream();

			BufferedReader rd = new BufferedReader(new InputStreamReader(is, StandardCharsets.UTF_8));

			String jsonText = readAll(rd);

			is.close();

			resultado = new JSONObject(jsonText);

		}

		catch (Exception e) {

		}

		return resultado;

	}

	public static String extraerCarpeta(String ruta) {

		return ruta.substring(0, ruta.lastIndexOf(saberSeparador()) + 1);

	}

	public static int eliminarArchivo(String file) throws IOException {

		Path archivo = Paths.get(file);

		short respuesta = 400;

		if (new File(file).exists()) {

			try {

				if (Files.deleteIfExists(archivo)) {

					respuesta = 200;

				}

			}

			catch (Exception e) {

				if (new File(file).isDirectory()) {

					respuesta = 501;

				}

				else {

					respuesta = 502;

				}

			}

		}

		else {

			respuesta = 404;

		}

		return respuesta;

	}

	public static int crearFichero(String filePath, String texto) {

		int respuesta = 400;

		try {

			File archivo = new File(filePath);

			if (!archivo.exists() && archivo.createNewFile()) {

				try (BufferedWriter bw = new BufferedWriter(new FileWriter(archivo))) {

					bw.write(texto);

				}

				respuesta = 200;

			}

		}

		catch (IOException e) {

			respuesta = 500;

		}

		return respuesta;

	}

	public static void vaciarCarpeta(String ruta) throws IOException {

		LinkedList<String> frames = (LinkedList<String>) listar(ruta, ".", false, true);

		for (int i = 0; i < frames.size(); i++) {

			if (!frames.get(i).isEmpty()) {

				eliminarFichero(frames.get(i));

			}

		}

	}

	public static boolean pingURL(String url) {

		int timeout = 400000;

		url = url.replaceFirst("^https", "http");

		try {

			HttpURLConnection connection = (HttpURLConnection) new URL(url).openConnection();

			connection.setConnectTimeout(timeout);

			connection.setReadTimeout(timeout);

			connection.setRequestMethod("HEAD");

			int responseCode = connection.getResponseCode();

			if (responseCode == 404) {

				return false;

			}

			else {

				return (200 <= responseCode && responseCode <= 399);

			}

		}

		catch (IOException exception) {

			return false;

		}

	}

	public static void renombrarArchivo(String archivo, String extension) {

		try {

			new File(archivo).renameTo(new File(archivo.substring(0, archivo.lastIndexOf(".") + 1) + extension));

		}

		catch (Exception e) {

		}

	}

}
