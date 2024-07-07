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
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
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
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JTextPane;
import javax.swing.SwingUtilities;
import javax.swing.text.DefaultStyledDocument;
import javax.swing.text.Style;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyleContext;
import javax.swing.text.StyledDocument;

import org.apache.commons.io.FileUtils;
import org.json.JSONObject;

import io.github.biezhi.webp.WebpIO;

public class JMthos {

	private static final String URL_REGEX = "^(https?|ftp)://[a-zA-Z0-9\\-\\.]+(:[a-zA-Z0-9]*)?/?([a-zA-Z0-9\\-\\._\\?,'/\\+&%\\$#=~])*$";

	private static final Pattern URL_PATTERN = Pattern.compile(URL_REGEX);

	public static final String OS = System.getProperty("os.name");

	/**
	 * Genera una lista de números con ceros a la izquierda hasta el número
	 * especificado.
	 *
	 * @param number el número hasta el cual se generarán los números con ceros a la
	 *               izquierda
	 * @return una lista de cadenas de números con ceros a la izquierda
	 */

	public static ArrayList<String> pintarCeros(int number) {

		ArrayList<String> lista = new ArrayList<>();

		int zeros = Integer.toString(number).length();

		for (int i = 1; i <= number; i++) {

			lista.add(String.format("%0" + (zeros + 1) + "d", i));

		}

		return lista;

	}

	/**
	 * Ordena una lista de nombres de archivo de forma ascendente basada en el
	 * número en el nombre del archivo. Los nombres con números precedidos por ceros
	 * y con menos ceros a la izquierda aparecen primero, seguidos por aquellos sin
	 * ceros precedentes.
	 *
	 * @param fileNames la lista de nombres de archivo a ordenar
	 */
	public static void sortFileNames(List<String> fileNames) {

		Collections.sort(fileNames, new Comparator<String>() {

			@Override

			public int compare(String o1, String o2) {

				String numStr1 = extractNumber(o1);

				String numStr2 = extractNumber(o2);

				int zeros1 = countLeadingZeros(numStr1);

				int zeros2 = countLeadingZeros(numStr2);

				if (zeros1 == 0 && zeros2 > 0) {

					return 1;

				}

				else if (zeros1 > 0 && zeros2 == 0) {

					return -1;

				}

				if (zeros1 != zeros2) {

					return Integer.compare(zeros1, zeros2);

				}

				return Integer.compare(Integer.parseInt(numStr1), Integer.parseInt(numStr2));

			}

			/**
			 * Extrae la parte numérica del nombre del archivo.
			 *
			 * @param fileName el nombre del archivo del que extraer el número
			 * @return la parte numérica extraída del nombre del archivo
			 */
			private String extractNumber(String fileName) {

				int start = fileName.lastIndexOf('_') + 1;

				int end = fileName.lastIndexOf('.');

				return fileName.substring(start, end);

			}

			/**
			 * Cuenta los ceros a la izquierda en una cadena numérica.
			 *
			 * @param str la cadena de la que se contarán los ceros a la izquierda
			 * @return la cantidad de ceros a la izquierda
			 */
			private int countLeadingZeros(String str) {

				int count = 0;

				for (int i = 0; i < str.length(); i++) {

					if (str.charAt(i) == '0') {

						count++;

					}

					else {

						break;

					}

				}

				return count;

			}

		});

	}

	/**
	 * Genera una tabla HTML a partir de listas de valores de encabezado y de
	 * columna.
	 *
	 * @param valoresEncabezado una lista de cadenas que representan los valores de
	 *                          los encabezados de la tabla
	 * @param valoresColumna    una lista de cadenas que representan los valores de
	 *                          las celdas de la tabla
	 * @param rowspanValue      el valor de rowspan para las celdas de la tabla
	 *                          (debe ser mayor que 1 para aplicarse)
	 * @param colspanValue      el valor de colspan para las celdas de la tabla
	 *                          (debe ser mayor que 1 para aplicarse)
	 * @return una cadena que contiene la representación HTML de la tabla generada
	 */

	public static String generarTablaHtml(List<String> valoresEncabezado, List<String> valoresColumna, int rowspanValue,
			int colspanValue) {

		StringBuilder html = new StringBuilder();

		html.append("<table>\n");

		if (valoresEncabezado != null && !valoresEncabezado.isEmpty()) {

			html.append("<thead>\n");

			html.append("<tr>\n");

			for (String valor : valoresEncabezado) {

				html.append("<th>").append(valor).append("</th>\n");

			}

			html.append("</tr>\n");

			html.append("</thead>\n");

		}

		html.append("<tbody>\n");

		int numFilas = valoresColumna.size() / valoresEncabezado.size();

		for (int i = 0; i < numFilas; i++) {

			html.append("<tr>\n");

			for (int j = 0; j < valoresEncabezado.size(); j++) {

				int index = i * valoresEncabezado.size() + j;

				if (index < valoresColumna.size()) {

					html.append("<td");

					if (rowspanValue > 1) {

						html.append(" rowspan=\"").append(rowspanValue).append("\"");

					}

					if (colspanValue > 1) {

						html.append(" colspan=\"").append(colspanValue).append("\"");

					}

					html.append(">");

					html.append(valoresColumna.get(index));

					html.append("</td>\n");

				}

			}

			html.append("</tr>\n");

		}

		html.append("</tbody>\n");

		html.append("</table>\n");

		return html.toString();

	}

	/**
	 * Calcula el porcentaje de una cantidad dada.
	 *
	 * @param total      la cantidad total
	 * @param percentage el porcentaje a calcular
	 * @return el valor del porcentaje de la cantidad total
	 * @throws IllegalArgumentException si el porcentaje es negativo o la cantidad
	 *                                  total es negativa
	 */

	public static double calculatePercentage(double total, double percentage) {

		if (total < 0 || percentage < 0) {

			throw new IllegalArgumentException("La cantidad total y el porcentaje deben ser valores no negativos.");

		}

		return (total * percentage) / 100;

	}

	/**
	 * Ajusta una cantidad en un porcentaje dado, ya sea aumentando o disminuyendo.
	 *
	 * @param total      la cantidad total
	 * @param percentage el porcentaje para ajustar la cantidad
	 * @param increase   true para aumentar la cantidad, false para disminuirla
	 * @return la cantidad ajustada en el porcentaje dado
	 * @throws IllegalArgumentException si el porcentaje es negativo o la cantidad
	 *                                  total es negativa
	 */

	public static double adjustByPercentage(double total, double percentage, boolean increase) {

		if (total < 0 || percentage < 0) {

			throw new IllegalArgumentException("La cantidad total y el porcentaje deben ser valores no negativos.");

		}

		double adjustment = calculatePercentage(total, percentage);

		return increase ? total + adjustment : total - adjustment;

	}

	/**
	 * Convierte un ArrayList a una List.
	 *
	 * @param <T>       el tipo de elementos en la lista
	 * @param arrayList el ArrayList que se va a convertir
	 * @return una List que contiene los elementos del ArrayList proporcionado
	 */

	public static <T> List<T> convertArrayListToList(ArrayList<T> arrayList) {

		return new ArrayList<>(arrayList);

	}

	/**
	 * Convierte un LinkedList a una List.
	 *
	 * @param <T>        el tipo de elementos en la lista
	 * @param linkedList el LinkedList que se va a convertir
	 * @return una List que contiene los elementos del LinkedList proporcionado
	 */

	public static <T> List<T> convertLinkedListToList(LinkedList<T> linkedList) {

		return new LinkedList<>(linkedList);

	}

	/**
	 * Muestra un diálogo modal con el título y los componentes especificados.
	 *
	 * @param tthis el componente padre del diálogo
	 * @param title el título del diálogo
	 * @param lista la lista de componentes que se añadirán al diálogo
	 */

	public static void showNewDialog(JComponent tthis, String title, List<JComponent> lista) {

		JFrame parentFrame = (JFrame) SwingUtilities.getWindowAncestor(tthis);

		JDialog dialog = new JDialog(parentFrame, title, true);

		dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);

		for (JComponent valor : lista) {

			dialog.getContentPane().add(valor);

		}

		dialog.setSize(300, 200);

		dialog.setLocationRelativeTo(parentFrame);

		dialog.setVisible(true);

	}

	/**
	 * Muestra un diálogo modal con el título, tamaño y componentes especificados.
	 *
	 * @param tthis  el componente padre del diálogo
	 * @param width  el ancho del diálogo
	 * @param height la altura del diálogo
	 * @param title  el título del diálogo
	 * @param lista  la lista de componentes que se añadirán al diálogo
	 */

	public static void showNewDialog(JComponent tthis, int width, int height, String title, List<JComponent> lista) {

		JFrame parentFrame = (JFrame) SwingUtilities.getWindowAncestor(tthis);

		JDialog dialog = new JDialog(parentFrame, title, true);

		dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);

		for (JComponent valor : lista) {

			dialog.getContentPane().add(valor);

		}

		dialog.setSize(width, height);

		dialog.setLocationRelativeTo(parentFrame);

		dialog.setVisible(true);

	}

	/**
	 * Convierte un mapa ordenado en una lista de cadenas.
	 *
	 * @param map Mapa ordenado a convertir en una lista.
	 * @return Lista de cadenas obtenidas del mapa ordenado.
	 */

	public static List<String> sortedMapToList(Map<Integer, String> map) {

		List<String> list = new ArrayList<>();

		for (Map.Entry<Integer, String> entry : map.entrySet()) {

			list.add(entry.getValue());

		}

		return list;

	}

	/**
	 * Convierte una lista de cadenas en la forma "a b" a una lista de números
	 * individuales como cadenas.
	 *
	 * @param lista     Lista de cadenas en la forma "a b".
	 * @param separador Separador utilizado para dividir las cadenas en números
	 *                  individuales.
	 * @return Lista de números individuales como cadenas.
	 */

	public static List<String> limpiarLista(List<String> lista, String separador) {

		if (separador == null) {

			separador = " ";

		}

		List<String> result = new ArrayList<>();

		for (String str : lista) {

			String[] numbers = str.split(separador);

			for (String num : numbers) {

				result.add(num);

			}

		}

		return result;

	}

	/**
	 * Convierte una lista de cadenas en un mapa donde las claves son índices
	 * incrementales y los valores son cadenas agrupadas según el tamaño de división
	 * especificado.
	 *
	 * @param lista Lista de cadenas para convertir en mapa.
	 * @param split Tamaño de división para agrupar cadenas en cada valor del mapa.
	 * @return Mapa donde las claves son índices y los valores son cadenas
	 *         agrupadas.
	 */

	public Map<Integer, String> convertirAMapYOrdenar(List<String> lista, int split, int sortByIndex,
			boolean ascendingSorted) {

		return ordenarMap(convertListToMap(lista, split), sortByIndex, ascendingSorted);

	}

	/**
	 * Método que realiza una regla de tres compuesta para resolver una proporción
	 * entre tres términos.
	 *
	 * @param valor1           Primer valor conocido.
	 * @param valor2           Segundo valor conocido.
	 * @param valor3           Tercer valor conocido.
	 * @param valorDesconocido Valor desconocido que se desea encontrar.
	 * @return El valor desconocido calculado mediante la regla de tres compuesta.
	 * @throws IllegalArgumentException Si alguno de los valores conocidos es cero.
	 */

	public static double reglaDeTresCompuesta(double valor1, double valor2, double valor3, double valorDesconocido) {

		if (valor1 == 0 || valor2 == 0 || valor3 == 0) {

			throw new IllegalArgumentException("Los valores conocidos no pueden ser cero.");

		}

		return (valor3 * valor2 * valorDesconocido) / (valor1 * valor2);

	}

	/**
	 * Método que realiza una regla de tres compuesta para resolver una proporción
	 * entre tres términos.
	 *
	 * @param valor1           Primer valor conocido.
	 * @param valor2           Segundo valor conocido.
	 * @param valor3           Tercer valor conocido.
	 * @param valorDesconocido Valor desconocido que se desea encontrar.
	 * @return El valor desconocido calculado mediante la regla de tres compuesta.
	 * @throws IllegalArgumentException Si alguno de los valores conocidos es cero.
	 */

	public static double reglaDeTresCompuesta(int valor1, int valor2, int valor3, int valorDesconocido) {

		if (valor1 == 0 || valor2 == 0 || valor3 == 0) {

			throw new IllegalArgumentException("Los valores conocidos no pueden ser cero.");

		}

		return (valor3 * valor2 * valorDesconocido) / (valor1 * valor2);

	}

	/**
	 * Método que realiza una regla de tres simple para resolver una proporción.
	 *
	 * @param valor1           Primer valor conocido.
	 * @param valor2           Segundo valor conocido.
	 * @param valorDesconocido Valor desconocido que se desea encontrar.
	 * @return El valor desconocido calculado mediante la regla de tres.
	 */

	public static double reglaDeTres(double valor1, double valor2, double valorDesconocido) {

		return (valor2 * valorDesconocido) / valor1;

	}

	/**
	 * Método que realiza una regla de tres simple para resolver una proporción.
	 *
	 * @param valor1           Primer valor conocido.
	 * @param valor2           Segundo valor conocido.
	 * @param valorDesconocido Valor desconocido que se desea encontrar.
	 * @return El valor desconocido calculado mediante la regla de tres.
	 */

	public static double reglaDeTres(int valor1, int valor2, int valorDesconocido) {

		return (valor2 * valorDesconocido) / valor1;

	}

	/**
	 * Convierte una lista de cadenas en un mapa donde las claves son índices
	 * incrementales y los valores son cadenas agrupadas según el tamaño de división
	 * especificado.
	 *
	 * @param lista Lista de cadenas para convertir en mapa.
	 * @param split Tamaño de división para agrupar cadenas en cada valor del mapa.
	 * @return Mapa donde las claves son índices y los valores son cadenas
	 *         agrupadas.
	 */

	public static Map<Integer, String> convertListToMap(List<String> lista, int split) {

		HashMap<Integer, String> hashMap = new HashMap<>();

		StringBuilder dato = new StringBuilder();

		int index = 0;

		for (int i = 0; i < lista.size(); i += split) {

			dato.setLength(0);

			for (int j = 0; j < split && (i + j) < lista.size(); j++) {

				if (j > 0) {

					dato.append(" ");

				}

				dato.append(lista.get(i + j));

			}

			hashMap.put(index++, dato.toString());

		}

		return hashMap;

	}

	/**
	 * Ordena un mapa por los valores obtenidos al dividir las cadenas en cada
	 * entrada del mapa. El orden se determina según el índice especificado.
	 *
	 * @param map         Mapa a ordenar.
	 * @param sortByIndex Índice (0 o 1) para determinar por cuál valor ordenar cada
	 *                    entrada del mapa. Si es 0, ordena por el primer valor de
	 *                    cada entrada. Si es 1, ordena por el segundo valor de cada
	 *                    entrada.
	 * @return Mapa ordenado por los valores según el índice especificado.
	 */

	public static Map<Integer, String> ordenarMap(Map<Integer, String> map, int sortByIndex, boolean ascendingSorted) {

		List<Map.Entry<Integer, String>> entryList = new ArrayList<>(map.entrySet());

		Collections.sort(entryList, (e1, e2) -> {

			String[] values1 = e1.getValue().split(" ");

			String[] values2 = e2.getValue().split(" ");

			int indexToSortBy = (sortByIndex == 2) ? 1 : 0;

			int comparisonResult = values1[indexToSortBy].compareTo(values2[indexToSortBy]);

			return ascendingSorted ? comparisonResult : -comparisonResult;

		});

		Map<Integer, String> sortedMap = new LinkedHashMap<>();

		entryList.forEach(entry -> sortedMap.put(entry.getKey(), entry.getValue()));

		Map<Integer, String> finalSortedMap = new LinkedHashMap<>();

		int newIndex = 0;

		for (Map.Entry<Integer, String> entry : sortedMap.entrySet()) {

			finalSortedMap.put(newIndex++, entry.getValue());

		}

		return finalSortedMap;

	}

	/**
	 * Recarga el panel especificado invalidando su caché y pidiendo una nueva
	 * renderización.
	 *
	 * @param panel El panel que se desea recargar.
	 */

	public static void recargarPanel(JPanel panel) {

		try {

			panel.revalidate();

			panel.repaint();

		} catch (Exception e) {

		}

	}

	/**
	 * Verifica si una cadena tiene el formato de una URL utilizando los protocolos
	 * HTTP, HTTPS o FTP.
	 *
	 * @param url La cadena a validar.
	 * @return true si la cadena es una URL válida con los protocolos HTTP, HTTPS o
	 *         FTP; false en caso contrario.
	 */

	public static boolean isValidURL(String url) {

		if (url == null) {

			return false;

		}

		Matcher matcher = URL_PATTERN.matcher(url);

		return matcher.matches();

	}

	/**
	 * Elimina las etiquetas HTML de un texto según el nombre de etiqueta, clases
	 * y/o id especificados. Si tagName es nulo, retorna una cadena vacía. Si
	 * classNamesList es null o vacío, elimina todas las etiquetas que coincidan con
	 * tagName y id. Si exactMatch es true, elimina las etiquetas que contienen
	 * todas las clases de classNamesList. Si exactMatch es false, elimina las
	 * etiquetas que contienen al menos una clase de classNamesList.
	 *
	 * @param html           Texto HTML del cual se eliminarán las etiquetas.
	 * @param tagName        Nombre de la etiqueta HTML (no puede ser nulo).
	 * @param classNamesList Lista de nombres de clases CSS (puede ser null o
	 *                       vacío).
	 * @param id             Id del elemento (puede ser null o vacío).
	 * @param exactMatch     Indica si se debe hacer coincidencia exacta de clases
	 *                       (true) o no (false).
	 * @return Texto sin las etiquetas especificadas.
	 */

	public static String removeHtmlTags(String html, String tagName, List<String> classNamesList, String id,

			boolean exactMatch) {

		if (tagName == null) {

			return "";

		}

		String result = "";

		String regex;

		if ((classNamesList == null || classNamesList.isEmpty()) && (id == null || id.isEmpty())) {

			regex = "<" + tagName + "\\b[^>]*>";

		}

		else if (classNamesList != null && !classNamesList.isEmpty()) {

			StringBuilder classesRegex = new StringBuilder();

			if (exactMatch) {

				for (String className : classNamesList) {

					classesRegex.append("(?=.*\\bclass\\s*=\\s*\"").append(className).append("\"\\b)");

				}

			}

			else {

				for (String className : classNamesList) {

					classesRegex.append("\\bclass\\s*=\\s*\"").append(className).append("\"\\b|");

				}

				classesRegex.deleteCharAt(classesRegex.length() - 1); // Remove last '|'

			}

			regex = "<" + tagName + "\\b[^>]*" + classesRegex.toString() + "[^>]*>";

		}

		else {

			regex = "<" + tagName + "\\b[^>]*\\bid\\s*=\\s*\"" + id + "\"[^>]*>";

		}

		Pattern pattern = Pattern.compile(regex, Pattern.DOTALL);

		Matcher matcher = pattern.matcher(html);

		result = matcher.replaceAll("");

		return result;

	}

	/**
	 * Elimina una etiqueta HTML y su contenido, excepto la etiqueta especificada
	 * con su clase o id.
	 *
	 * @param html            El código HTML original.
	 * @param tagToRemove     La etiqueta que se debe eliminar junto con su
	 *                        contenido.
	 * @param tagToKeep       La etiqueta que no se debe eliminar.
	 * @param classNameToKeep La clase de la etiqueta que no se debe eliminar (puede
	 *                        ser null o vacía).
	 * @param idToKeep        El id de la etiqueta que no se debe eliminar (puede
	 *                        ser null o vacío).
	 * @return El código HTML con la etiqueta especificada eliminada excepto la
	 *         etiqueta a conservar.
	 */

	public static String removeHtmlTagsExcept(String html, String tagToRemove, String tagToKeep, String classNameToKeep,
			String idToKeep) {

		if (tagToRemove == null || tagToRemove.isEmpty() || tagToKeep == null || tagToKeep.isEmpty()) {

			return html;

		}

		String keepTagPattern = "<" + tagToKeep + "\\b[^>]*";

		if (classNameToKeep != null && !classNameToKeep.isEmpty()) {

			keepTagPattern += "\\bclass\\s*=\\s*\"[^\"]*\\b" + Pattern.quote(classNameToKeep) + "\\b[^\"]*\"";

		}

		if (idToKeep != null && !idToKeep.isEmpty()) {

			keepTagPattern += "\\bid\\s*=\\s*\"" + Pattern.quote(idToKeep) + "\"";

		}

		keepTagPattern += "[^>]*>.*?<\\/" + tagToKeep + ">";

		Pattern keepPattern = Pattern.compile(keepTagPattern, Pattern.DOTALL);

		Matcher keepMatcher = keepPattern.matcher(html);

		StringBuilder preservedTags = new StringBuilder();

		while (keepMatcher.find()) {

			preservedTags.append(keepMatcher.group());

		}

		return preservedTags.toString();

	}

	/**
	 * Lee el contenido de un archivo HTML y lo retorna como una cadena.
	 *
	 * @param htmlFilePath Ruta del archivo HTML.
	 * @return Contenido del archivo HTML como cadena.
	 * @throws IOException Si ocurre un error al leer el archivo.
	 */

	public static String readHtmlFile(String htmlFilePath) throws IOException {

		StringBuilder contentBuilder = new StringBuilder();

		try (BufferedReader reader = new BufferedReader(new FileReader(htmlFilePath, StandardCharsets.UTF_8))) {

			String line;

			while ((line = reader.readLine()) != null) {

				contentBuilder.append(line).append("\n");

			}

		}

		return contentBuilder.toString();

	}

	/**
	 * Limpia las etiquetas HTML de una cadena de texto.
	 *
	 * @param htmlText Texto que puede contener etiquetas HTML.
	 * @return Texto limpio sin etiquetas HTML.
	 */

	public static String cleanHtmlTags(String htmlText) {

		return htmlText.replaceAll("<.*?>", "").trim();

	}

	/**
	 * Genera una estructura de tabla HTML a partir de celdas HTML concatenadas.
	 * Cada celda se representa como una fila en la tabla.
	 *
	 * @param html El código HTML que contiene celdas consecutivas sin estructura de
	 *             tabla.
	 * @return Una cadena que representa la tabla HTML con las celdas organizadas en
	 *         filas y columnas.
	 */

	public static String generateHtmlTable(String html) {

		String[] cells = html.split("</td><td");

		StringBuilder tableBuilder = new StringBuilder("<table>\n");

		for (String cell : cells) {

			String formattedCell = "<td" + cell + "</td>";

			tableBuilder.append("    <tr>").append(formattedCell).append("</tr>\n");

		}

		tableBuilder.append("</table>");

		return tableBuilder.toString().replace("<td<td", "<td");

	}

	/**
	 * Elimina las etiquetas HTML de un texto según el nombre de etiqueta, clase y/o
	 * id especificados. Retorna una cadena vacía si tagName es nulo o si ambos
	 * className e id son nulos. Si className es nulo, elimina las etiquetas con el
	 * id especificado. Si id es nulo, elimina las etiquetas con la clase
	 * especificada.
	 *
	 * @param html      Texto HTML del cual se eliminarán las etiquetas.
	 * @param tagName   Nombre de la etiqueta HTML.
	 * @param className Nombre de la clase CSS (puede ser null).
	 * @param id        Id del elemento (puede ser null).
	 * @return Texto sin las etiquetas especificadas.
	 */

	public static String removeHtmlTags(String html, String tagName, String className, String id) {

		if (tagName == null) {

			return "";

		}

		String result = "";

		String regex;

		if ((className == null || className.isEmpty()) && (id == null || id.isEmpty())) {

			regex = "<" + tagName + "\\b[^>]*>.*?</" + tagName + ">";

		}

		else if (className != null && !className.isEmpty()) {

			regex = "<" + tagName + "\\b[^>]*\\bclass\\s*=\\s*\"[^\"]*\\b" + className + "\\b[^\"]*\"[^>]*>.*?</"
					+ tagName + ">";

		}

		else {

			regex = "<" + tagName + "\\b[^>]*\\bid\\s*=\\s*\"" + id + "\"[^>]*>.*?</" + tagName + ">";

		}

		Pattern pattern = Pattern.compile(regex, Pattern.DOTALL);

		Matcher matcher = pattern.matcher(html);

		result = matcher.replaceAll("");

		return result;

	}

	/**
	 * Calcula la potencia de un número base elevado a un exponente dado.
	 *
	 * @param base      el número base.
	 * @param exponente el exponente al cual se eleva la base.
	 * @return el resultado de base elevado a exponente.
	 */

	public static double potencia(double base, double exponente) {

		return Math.pow(base, exponente);

	}

	/**
	 * Redondea un número hacia arriba al entero más cercano.
	 *
	 * @param numero El número a redondear.
	 * @return El entero más cercano redondeado hacia arriba.
	 */

	public static int redondearHaciaArriba(float numero) {

		return (int) Math.ceil(numero);

	}

	/**
	 * Redondea un número hacia abajo al entero más cercano.
	 *
	 * @param numero El número a redondear.
	 * @return El entero más cercano redondeado hacia abajo.
	 */

	public static int redondearHaciaAbajo(float numero) {

		return (int) Math.floor(numero);

	}

	/**
	 * Convierte una ruta de archivo en una ruta de directorio si termina en un
	 * archivo.
	 *
	 * @param path La ruta a convertir.
	 * @return La ruta del directorio.
	 */

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

	/**
	 * Verifica si una ruta termina en un archivo basándose en la extensión.
	 *
	 * @param path La ruta a verificar.
	 * @return {@code true} si la ruta termina en un archivo, {@code false} en caso
	 *         contrario.
	 */

	public static boolean terminaEnArchivo(String path) {

		Pattern pattern = Pattern.compile("\\.([a-z]{3,4})$");

		Matcher matcher = pattern.matcher(path);

		return matcher.find();

	}

	/**
	 * Trunca un número de punto flotante a un número específico de dígitos
	 * decimales.
	 *
	 * @param number    El número a truncar.
	 * @param numDigits El número de dígitos decimales a mantener.
	 * @return El número truncado.
	 */

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

	/**
	 * Abre un archivo o directorio según el valor del parámetro open.
	 *
	 * @param output La ruta del archivo o directorio a abrir.
	 * @param open   El indicador de apertura (0: no abrir, 1: abrir archivo, otro:
	 *               abrir directorio).
	 */

	public static void abrir(String output, int open) {

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

	/**
	 * Obtiene el directorio de una ruta de archivo.
	 *
	 * @param output La ruta del archivo.
	 * @return La ruta del directorio.
	 */

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

	/**
	 * Convierte un archivo de imagen en un objeto BufferedImage.
	 *
	 * @param file El archivo de imagen.
	 * @return El objeto BufferedImage.
	 * @throws IOException Si ocurre un error durante la lectura del archivo.
	 */

	public static BufferedImage fileToBufferedImage(File file) throws IOException {

		return ImageIO.read(file);

	}

	/**
	 * Limpia los espacios en una cadena. Puede eliminar todos los espacios o solo
	 * los espacios duplicados.
	 *
	 * @param cadena           La cadena a limpiar.
	 * @param eliminarEspacios Si es {@code true}, elimina todos los espacios. Si es
	 *                         {@code false}, elimina solo los espacios duplicados.
	 * @return La cadena limpiada.
	 */

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

	/**
	 * Lee el contenido de un archivo y lo devuelve como una cadena.
	 *
	 * @param filePath La ruta del archivo.
	 * @return El contenido del archivo.
	 * @throws IOException Si ocurre un error durante la lectura del archivo.
	 */

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

	/**
	 * Escribe contenido en un archivo.
	 *
	 * @param filePath La ruta del archivo.
	 * @param content  El contenido a escribir.
	 * @throws IOException Si ocurre un error durante la escritura en el archivo.
	 */

	public static void writeFile(String filePath, String content) throws IOException {

		try (BufferedWriter bw = new BufferedWriter(new FileWriter(filePath))) {

			bw.write(content);

		}

	}

	/**
	 * Divide dos números enteros.
	 *
	 * @param a El dividendo.
	 * @param b El divisor.
	 * @return El resultado de la división.
	 * @throws ArithmeticException Si el divisor es cero.
	 */

	public static double dividir(int a, int b) {

		if (b == 0) {

			throw new ArithmeticException("Division by zero is not allowed");

		}

		return (double) a / b;

	}

	/**
	 * Devuelve el valor máximo entre dos enteros.
	 *
	 * @param a El primer entero.
	 * @param b El segundo entero.
	 * @return El valor máximo.
	 */

	public static int valorMaximo(int a, int b) {

		return Math.max(a, b);

	}

	/**
	 * Devuelve el valor mínimo entre dos enteros.
	 *
	 * @param a El primer entero.
	 * @param b El segundo entero.
	 * @return El valor mínimo.
	 */

	public static int valorMinimo(int a, int b) {

		return Math.min(a, b);

	}

	/**
	 * Calcula el valor de la sucesión geométrica para un índice dado.
	 *
	 * @param parametro una cadena que contiene el primer valor seguido de '#' y el
	 *                  segundo valor de la sucesión geométrica.
	 * @param n         el índice del término que se desea calcular en la sucesión
	 *                  geométrica.
	 * @return el valor correspondiente al índice n en la sucesión geométrica.
	 */

	public static double calcularSucesionGeometrica(String parametro, int n) {

		double primerValor = Double.parseDouble(parametro.substring(0, parametro.indexOf("#")));

		double razon =

				Double.parseDouble(parametro.substring(parametro.indexOf("#") + 1, parametro.length())) / primerValor;

		return primerValor * Math.pow(razon, n - 1);

	}

	/**
	 * Calcula el valor específico en una sucesión geométrica para un índice dado.
	 *
	 * @param parametro una cadena que contiene el primer valor seguido de '#' y el
	 *                  segundo valor de la sucesión geométrica.
	 * @param n         el índice del término que se desea calcular en la sucesión
	 *                  geométrica.
	 * @return el valor correspondiente al índice n en la sucesión geométrica.
	 */

	public static double calcularValorEnSucesionGeometrica(String parametro, int n) {

		return n * (Double.parseDouble(parametro.substring(parametro.indexOf("#") + 1, parametro.length()))
				/ (Double.parseDouble(parametro.substring(0, parametro.indexOf("#")))));

	}

	public static int calcularSucesionAritmeticaAInt(String parametro, int n, boolean round) {

		return convertirDoubleAInt(calcularSucesionAritmeticaADouble(parametro, n));

	}

	/**
	 * Calcula la sucesión aritmética y devuelve el resultado como un entero
	 * redondeado.
	 *
	 * @param parametro Una cadena que contiene pares de índices y valores separados
	 *                  por comas, donde cada par está en el formato "indice#valor".
	 * @param n         El índice para el cual se desea calcular el valor de la
	 *                  sucesión aritmética.
	 * @return El valor calculado de la sucesión aritmética en el índice n,
	 *         redondeado al entero más cercano.
	 * @throws IllegalArgumentException Si el parámetro es nulo o vacío, o si el
	 *                                  índice n no es un entero positivo.
	 */

	public static int calcularSucesionAritmeticaAInt(String parametro, int n) {

		return (int) Math.round(calcularSucesionAritmeticaADouble(parametro, n));

	}

	/**
	 * Calcula la sucesión aritmética y devuelve el resultado como un número de tipo
	 * Double.
	 *
	 * @param parametro Una cadena que contiene pares de índices y valores separados
	 *                  por comas, donde cada par está en el formato "indice#valor".
	 * @param n         El índice para el cual se desea calcular el valor de la
	 *                  sucesión aritmética.
	 * @return El valor calculado de la sucesión aritmética en el índice n.
	 * @throws IllegalArgumentException Si el parámetro es nulo o vacío, o si el
	 *                                  índice n no es un entero positivo.
	 */

	public static Double calcularSucesionAritmeticaADouble(String parametro, int n) {

		Double resultado = null;

		if (parametro == null || parametro.isEmpty()) {

			throw new IllegalArgumentException("El parámetro no puede ser nulo o vacío.");

		}

		else if (n <= 0) {

			throw new IllegalArgumentException("El índice n debe ser un entero positivo.");

		}

		else {

			String[] pares = parametro.split(",");

			int[] indices = new int[pares.length];

			int[] valores = new int[pares.length];

			boolean formatoIncorrecto = false;

			String[] partes;

			for (int i = 0; i < pares.length && !formatoIncorrecto; i++) {

				partes = pares[i].split("#");

				if (partes.length != 2) {

					formatoIncorrecto = true;

				}

				else {

					try {

						indices[i] = Integer.parseInt(partes[0]);

						valores[i] = Integer.parseInt(partes[1]);

					}

					catch (NumberFormatException e) {

						formatoIncorrecto = true;

					}

				}

			}

			if (!formatoIncorrecto) {

				for (int i = 0; i < indices.length; i++) {

					if (indices[i] == n) {

						resultado = (double) valores[i];

						return resultado;

					}

				}

				if (indices.length >= 2) {

					int indiceBase = indices[0];

					int valorBase = valores[0];

					double sumaDiferencias = 0;

					for (int i = 1; i < indices.length; i++) {

						sumaDiferencias += (double) (valores[i] - valores[i - 1]) / (indices[i] - indices[i - 1]);

					}

					double razon = sumaDiferencias / (indices.length - 1);

					resultado = valorBase + (n - indiceBase) * razon;

				}

				else {

					System.err.println("Se requieren al menos dos pares de valores para calcular la fórmula.");

				}

			}

		}

		return resultado;

	}

	/**
	 * Convierte un entero a una cadena de caracteres.
	 *
	 * @param numero El entero a convertir.
	 * @return La representación en cadena del entero.
	 */

	public String enteroACadena(int numero) {

		return String.valueOf(numero);

	}

	/**
	 * Convierte un número de punto flotante a una cadena de caracteres.
	 *
	 * @param numero El número de punto flotante a convertir.
	 * @return La representación en cadena del número de punto flotante.
	 */

	public String floatACadena(float numero) {

		return String.valueOf(numero);

	}

	/**
	 * Convierte un número de doble precisión a una cadena de caracteres.
	 *
	 * @param numero El número de doble precisión a convertir.
	 * @return La representación en cadena del número de doble precisión.
	 */

	public String doubleACadena(double numero) {

		return String.valueOf(numero);

	}

	/**
	 * Convierte un código ASCII a su caracter correspondiente.
	 *
	 * @param codigoAscii El código ASCII a convertir.
	 * @return El caracter correspondiente al código ASCII.
	 */

	public static char asciiToChar(int codigoAscii) {

		return (char) codigoAscii;

	}

	/**
	 * Convierte un caracter a su código ASCII correspondiente.
	 *
	 * @param caracter El caracter a convertir.
	 * @return El código ASCII del caracter.
	 */

	public static int charToAscii(char caracter) {

		return (int) caracter;

	}

	/**
	 * Convierte una cadena de caracteres a un entero.
	 *
	 * @param cadena La cadena a convertir.
	 * @return El entero resultante.
	 * @throws NumberFormatException Si la cadena no representa un entero válido.
	 */

	public int cadenaAEntero(String cadena) {

		return Integer.parseInt(cadena);

	}

	/**
	 * Verifica si un número entero es par.
	 *
	 * @param numero El número a verificar.
	 * @return {@code true} si el número es par, {@code false} si es impar.
	 */

	public static boolean esPar(int numero) {

		return numero % 2 == 0;

	}

	/**
	 * Redondea un número de doble precisión a una cantidad específica de decimales.
	 *
	 * @param numero    El número a redondear.
	 * @param decimales La cantidad de decimales a mantener.
	 * @return El número redondeado.
	 * @throws IllegalArgumentException Si el número de decimales es negativo.
	 */

	public static double redondearDouble(double numero, int decimales) {

		if (decimales < 0) {

			throw new IllegalArgumentException("El número de decimales no puede ser negativo.");

		}

		BigDecimal bigDecimal = new BigDecimal(Double.toString(numero));

		bigDecimal = bigDecimal.setScale(decimales, RoundingMode.HALF_UP);

		return bigDecimal.doubleValue();

	}

	/**
	 * Convierte una imagen JPG a PNG.
	 *
	 * @param jpgFilePath La ruta del archivo JPG.
	 * @param pngFilePath La ruta del archivo PNG de salida.
	 * @throws IOException Si ocurre un error durante la conversión.
	 */

	public static void convertJPGtoPNG(String jpgFilePath, String pngFilePath) throws IOException {

		BufferedImage bufferedImage = ImageIO.read(new File(jpgFilePath));

		BufferedImage newBufferedImage = new BufferedImage(bufferedImage.getWidth(), bufferedImage.getHeight(),
				BufferedImage.TYPE_INT_ARGB);

		newBufferedImage.createGraphics().drawImage(bufferedImage, 0, 0, java.awt.Color.WHITE, null);

		ImageIO.write(newBufferedImage, "png", new File(pngFilePath));
	}

	/**
	 * Convierte una imagen PNG a JPG.
	 *
	 * @param jpgFilePath La ruta del archivo PNG.
	 * @param pngFilePath La ruta del archivo JPG de salida.
	 * @throws IOException Si ocurre un error durante la conversión.
	 */

	public static void convertPNGtoJPG(String pngFilePath, String jpgFilePath) throws IOException {

		BufferedImage bufferedImage = ImageIO.read(new File(pngFilePath));

		BufferedImage newBufferedImage = new BufferedImage(bufferedImage.getWidth(), bufferedImage.getHeight(),
				BufferedImage.TYPE_INT_RGB);

		newBufferedImage.createGraphics().drawImage(bufferedImage, 0, 0, java.awt.Color.WHITE, null);

		ImageIO.write(newBufferedImage, "jpg", new File(jpgFilePath));

	}

	/**
	 * Centra un JTextPane en su contenedor.
	 *
	 * @param textPane El JTextPane a centrar.
	 * @return El JTextPane centrado.
	 */

	public static JTextPane centrarJTextPane(JTextPane textPane) {

		try {

			textPane.setDocument(centrarJTextPane());

		} catch (Exception e) {

		}

		return textPane;

	}

	/**
	 * Crea un StyledDocument con alineación centrada.
	 *
	 * @return El StyledDocument con alineación centrada.
	 */

	public static StyledDocument centrarJTextPane() {

		StyleContext context = new StyleContext();

		Style style = context.getStyle(StyleContext.DEFAULT_STYLE);

		StyleConstants.setAlignment(style, StyleConstants.ALIGN_CENTER);

		return new DefaultStyledDocument(context);

	}

	/**
	 * Centra un JFrame en la pantalla.
	 *
	 * @param frame El JFrame a centrar.
	 */

	public static void centrarJFrame(JFrame frame) {

		try {

			frame.setLocationRelativeTo(null);

		}

		catch (Exception e) {

		}

	}

	/**
	 * Configura un JComboBox para que los elementos estén centrados.
	 *
	 * @param comboBox El JComboBox a configurar.
	 */

	public static void redondearComboBx(JComboBox<?> comboBox) {

		try {

			DefaultListCellRenderer listRenderer = new DefaultListCellRenderer();

			listRenderer.setHorizontalAlignment(DefaultListCellRenderer.CENTER);

			comboBox.setRenderer(listRenderer);

		}

		catch (Exception e) {

		}

	}

	/**
	 * Redondea un número de punto flotante a una cantidad específica de decimales.
	 *
	 * @param numero    El número a redondear.
	 * @param decimales La cantidad de decimales a mantener.
	 * @return El número redondeado.
	 * @throws IllegalArgumentException Si el número de decimales es negativo.
	 */

	public static float redondearFloat(float numero, int decimales) {

		if (decimales < 0) {

			throw new IllegalArgumentException("El número de decimales no puede ser negativo.");

		}

		BigDecimal bigDecimal = new BigDecimal(Float.toString(numero));

		bigDecimal = bigDecimal.setScale(decimales, RoundingMode.HALF_UP);

		return bigDecimal.floatValue();

	}

	/**
	 * Divide dos números enteros y redondea el resultado al entero más cercano.
	 *
	 * @param numerador   El numerador.
	 * @param denominador El denominador.
	 * @return El resultado de la división redondeado al entero más cercano.
	 * @throws ArithmeticException Si el denominador es cero.
	 */

	public static double dividirYRedondearADouble(int numerador, int denominador) {

		if (denominador == 0) {

			throw new ArithmeticException("El denominador no puede ser cero.");

		}

		double resultado = (double) numerador / denominador;

		if (numerador % denominador != 0) {

			resultado = Math.ceil(resultado);

		}

		return resultado;

	}

	/**
	 * Divide dos números enteros y redondea el resultado al entero más cercano.
	 *
	 * @param numerador   El numerador.
	 * @param denominador El denominador.
	 * @return El resultado de la división redondeado al entero más cercano.
	 * @throws ArithmeticException Si el denominador es cero.
	 */

	public static float dividirYRedondearAFloat(int numerador, int denominador) {

		if (denominador == 0) {

			throw new ArithmeticException("El denominador no puede ser cero.");

		}

		float resultado = (float) numerador / denominador;

		if (numerador % denominador > 0) {

			resultado = (float) Math.ceil(resultado);

		}

		return resultado;

	}

	/**
	 * Divide dos números enteros y redondea el resultado al entero más cercano.
	 *
	 * @param numerador   El numerador.
	 * @param denominador El denominador.
	 * @return El resultado de la división redondeado al entero más cercano.
	 * @throws ArithmeticException Si el denominador es cero.
	 */

	public static int dividirYRedondearAEntero(int numerador, int denominador) {

		if (denominador == 0) {

			throw new ArithmeticException("El denominador no puede ser cero.");

		}

		int resultado = numerador / denominador;

		if (numerador % denominador > 0) {

			resultado += 1;

		}

		return resultado;

	}

	/**
	 * Verifica si la tecla presionada es Enter.
	 *
	 * @param e El evento de teclado.
	 * @return {@code true} si la tecla presionada es Enter, {@code false} en caso
	 *         contrario.
	 */

	public static boolean isEnter(KeyEvent e) {

		return (e.getKeyCode() == KeyEvent.VK_ENTER) ? true : false;

	}

	/**
	 * Verifica si el evento de rueda de desplazamiento está moviendo hacia abajo.
	 *
	 * @param e El evento de rueda de desplazamiento.
	 * @return {@code true} si el evento indica un movimiento hacia abajo,
	 *         {@code false} en caso contrario.
	 */

	public static boolean scrollHaciaAbajo(MouseWheelEvent e) {

		return (e.getWheelRotation() == 1) ? true : false;

	}

	/**
	 * Ejecuta una consulta SQLite y devuelve los resultados.
	 *
	 * @param dbName  El nombre de la base de datos SQLite.
	 * @param query   La consulta SQL.
	 * @param columns Las columnas de las cuales se desea obtener los resultados.
	 * @return Una lista de resultados.
	 */

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

	/**
	 * Inserta datos en una tabla SQLite.
	 * 
	 * @param db      Nombre de la base de datos SQLite.
	 * @param table   Nombre de la tabla en la que se insertarán los datos.
	 * @param columns Lista de nombres de columnas en las que se insertarán los
	 *                datos.
	 * @param values  Lista de valores que se insertarán en las columnas.
	 */

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

	/**
	 * Encuentra la posición de una subcadena específica en una cadena, dado un
	 * número de ocurrencia.
	 * 
	 * @param cadena    La cadena en la que se buscará.
	 * @param subcadena La subcadena que se buscará.
	 * @param indice    El número de ocurrencia que se quiere encontrar.
	 * @return La posición de la ocurrencia especificada de la subcadena en la
	 *         cadena, o -1 si no se encuentra esa ocurrencia.
	 */

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

	/**
	 * Encuentra todas las posiciones de una cadena dentro de un texto.
	 * 
	 * @param texto  El texto en el que se buscarán las posiciones.
	 * @param cadena La cadena cuyas posiciones se buscarán.
	 * @return Una lista de todas las posiciones donde se encuentra la cadena dentro
	 *         del texto.
	 */

	public static List<Integer> encontrarPosiciones(String texto, String cadena) {

		List<Integer> posiciones = new ArrayList<>();

		int indice = texto.indexOf(cadena);

		while (indice >= 0) {

			posiciones.add(indice);

			indice = texto.indexOf(cadena, indice + 1);

		}

		return posiciones;

	}

	/**
	 * Reemplaza todas las ocurrencias de una subcadena en un texto por una lista de
	 * reemplazos en orden.
	 * 
	 * @param texto            El texto original.
	 * @param textoAReemplazar La subcadena que se reemplazará.
	 * @param reemplazos       La lista de cadenas de reemplazo.
	 * @return El texto con las reemplazos aplicados.
	 */

	public static String reemplazarPosiciones(String texto, String textoAReemplazar, List<String> reemplazos) {

		List<Integer> posiciones = encontrarPosiciones(texto, textoAReemplazar);

		StringBuilder mensaje = new StringBuilder(texto);

		int offset = 0;

		String reemplazo = "";

		int posicionActual;

		for (int i = 0; i < posiciones.size(); i++) {

			reemplazo = reemplazos.get(i % reemplazos.size());

			posicionActual = posiciones.get(i) + offset;

			mensaje.replace(posicionActual, posicionActual + textoAReemplazar.length(), reemplazo);

			offset += reemplazo.length() - textoAReemplazar.length();

		}

		return mensaje.toString();

	}

	/**
	 * Convierte una lista de rutas de archivos a una sola cadena separada por un
	 * delimitador específico.
	 * 
	 * @param lista     La lista de rutas de archivos.
	 * @param separador El delimitador que se usará para separar las rutas en la
	 *                  cadena resultante.
	 * @return Una cadena con las rutas de archivos separadas por el delimitador.
	 */

	public static String listaAString(List<String> lista, String separador) {

		LinkedList<String> archivos = lista.stream().filter(path -> !Files.isDirectory(Paths.get(path)))
				.collect(Collectors.toCollection(LinkedList::new));

		return String.join(separador, archivos);

	}

	/**
	 * Carga una imagen desde el archivo especificado.
	 *
	 * @param imagePath Ruta del archivo de imagen a cargar.
	 * @return La imagen cargada como un objeto BufferedImage, o null si ocurre un
	 *         error al cargarla.
	 */

	public static BufferedImage loadImage(String imagePath) {

		try {

			return ImageIO.read(new File(imagePath));

		}

		catch (IOException e) {

			return null;

		}

	}

	/**
	 * Renombra archivos dentro de una carpeta según la extensión de entrada y
	 * salida especificadas.
	 *
	 * @param carpeta          Ruta de la carpeta que contiene los archivos a
	 *                         renombrar.
	 * @param extensionEntrada Extensión de los archivos antes del renombrado.
	 * @param extensionSalida  Extensión de los archivos después del renombrado.
	 * @param borrar           Indica si se deben eliminar los archivos originales
	 *                         después de convertirlos (solo aplica a imágenes).
	 */

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

	/**
	 * Muestra archivos en una ruta de Linux, eliminando el punto del nombre de
	 * archivo si es oculto.
	 *
	 * @param path Ruta de la carpeta que contiene los archivos a mostrar.
	 */

	public static void mostrarArchivosDeLinux(String path) {

		LinkedList<String> lista = (LinkedList<String>) listar(path, "all", false, true);

		for (String texto : lista) {

			try {

				if ((texto.substring(texto.lastIndexOf("/") + 1, texto.lastIndexOf("/") + 2).equals("."))) {

					String salida = texto.substring(0, texto.lastIndexOf("/") + 1);

					String salida2 = texto.substring(texto.lastIndexOf("/") + 2, texto.length());

					new File(texto).renameTo(new File(salida + salida2));

				}

			}

			catch (Exception e) {

			}

		}

	}

	/**
	 * Muestra archivos ocultos en una ruta de Linux, eliminando el punto del nombre
	 * de archivo si es oculto.
	 *
	 * @param path      Ruta de la carpeta que contiene los archivos a mostrar.
	 * @param extension Extensión de los archivos a buscar.
	 */

	public static void mostrarArchivosOcultosDeLinux(String path, String extension) {

		LinkedList<String> lista = (LinkedList<String>) listar(path, extension, false, true);

		for (String texto : lista) {

			try {

				if ((texto.substring(texto.lastIndexOf("/") + 1, texto.lastIndexOf("/") + 2).equals("."))) {

					String salida = texto.substring(0, texto.lastIndexOf("/") + 1);

					String salida2 = texto.substring(texto.lastIndexOf("/") + 2, texto.length());

					new File(texto).renameTo(new File(salida + salida2));

				}

			}

			catch (Exception e) {

			}

		}

	}

	/**
	 * Convierte un valor de tipo Double a int.
	 *
	 * @param valorDouble el valor Double que se desea convertir a int.
	 * @return el valor convertido a int.
	 * @throws IllegalArgumentException si el valorDouble es null.
	 */
	public static int convertirDoubleAInt(Double valorDouble) {

		if (valorDouble == null) {

			throw new IllegalArgumentException("El valor Double no puede ser nulo.");

		}

		return valorDouble.intValue();

	}

	/**
	 * Agrega un separador al final de la cadena si no existe ya.
	 *
	 * @param texto Cadena a la que se agregará el separador.
	 * @return Cadena con el separador agregado al final.
	 */

	public static String ponerSeparador(String texto) {

		if (!texto.endsWith(saberSeparador())) {

			texto += saberSeparador();

		}

		return texto;

	}

	/**
	 * Encuentra la cadena más larga en una lista de cadenas.
	 *
	 * @param list Lista de cadenas en la que se buscará la más larga.
	 * @return La cadena más larga encontrada en la lista, o null si la lista está
	 *         vacía.
	 */

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

	/**
	 * Calcula el porcentaje de un valor respecto al total.
	 *
	 * @param valor Valor del cual se desea calcular el porcentaje.
	 * @param total Valor total del cual se calculará el porcentaje.
	 * @return Porcentaje calculado como un entero.
	 */

	public static int calcularPorcentaje(int valor, int total) {

		float resultado = (valor * 100) / total;

		int salida;

		NumberFormat numberFormat = NumberFormat.getInstance();

		numberFormat.setMaximumFractionDigits(0);

		numberFormat.setRoundingMode(RoundingMode.DOWN);

		salida = Integer.parseInt(numberFormat.format(resultado));

		return salida;

	}

	/**
	 * Aumenta el día actual por un valor dado y devuelve la fecha resultante.
	 *
	 * @param valor Número de días que se desea aumentar.
	 * @return Fecha resultante como un String en formato de fecha.
	 */

	public static String aumentarDia(int valor) {

		Calendar calendar = Calendar.getInstance();

		calendar.setTime(new Date());

		int calendarTime = Calendar.DAY_OF_MONTH;

		int temp = calendar.get(calendarTime);

		calendar.set(calendarTime, temp + valor);

		Date newDate = calendar.getTime();

		return newDate.toString();

	}

	/**
	 * Obtiene el nombre del archivo con su extensión desde una ruta de archivo
	 * completa.
	 *
	 * @param archivo Ruta del archivo del cual se desea obtener el nombre con
	 *                extensión.
	 * @return Nombre del archivo con su extensión.
	 */

	public static String saberNombreArchivoConExtension(String archivo) {

		String resultado = "";

		try {

			resultado = archivo.substring(archivo.lastIndexOf(saberSeparador()) + 1, archivo.length());

		}

		catch (Exception e) {

		}

		return resultado;

	}

	/**
	 * Convierte un Icono en un objeto BufferedImage.
	 *
	 * @param icon Icono que se desea convertir.
	 * @return Objeto BufferedImage generado desde el Icono.
	 */

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

	/**
	 * Obtiene las dimensiones de una imagen después de redimensionarla, si se
	 * requiere.
	 *
	 * @param originalImage Imagen original de la cual se desean obtener las
	 *                      dimensiones.
	 * @param newWidth      Ancho deseado para la imagen redimensionada.
	 * @param newHeight     Alto deseado para la imagen redimensionada.
	 * @param resize        Indica si se debe redimensionar la imagen.
	 * @return Punto con las dimensiones resultantes de la imagen.
	 */

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

	/**
	 * Carga una imagen desde un archivo.
	 *
	 * @param image Ruta del archivo de imagen que se desea cargar.
	 * @return La imagen cargada como un objeto BufferedImage, o null si ocurre
	 *         algún error.
	 */

	public static BufferedImage loadFileImage(String image) {

		try {

			return javax.imageio.ImageIO.read(new File(image));

		}

		catch (Exception e) {

			return null;

		}

	}

	/**
	 * Copia el texto especificado en el portapapeles del sistema.
	 *
	 * @param text Texto que se desea copiar en el portapapeles.
	 */

	public static void copy(String text) {

		try {

			Clipboard clipboard = getSystemClipboard();

			clipboard.setContents(new StringSelection(text), null);

		}

		catch (Exception e) {

		}

	}

	/**
	 * Verifica si el código de tecla proporcionado corresponde a un caracter
	 * imprimible.
	 *
	 * @param keyCode Código de tecla que se desea verificar.
	 * @return true si el código de tecla no corresponde a un caracter imprimible,
	 *         false en caso contrario.
	 */

	public static boolean tieneCaracterNoImprimible(int keyCode) {

		return !(keyCode != 112 && keyCode != 113 && keyCode != 114 && keyCode != 115 && keyCode != 116
				&& keyCode != 117 && keyCode != 19 && keyCode != 118 && keyCode != 119 && keyCode != 120
				&& keyCode != 121 && keyCode != 122 && keyCode != 123 && keyCode != 27 && keyCode != 16 && keyCode != 17
				&& keyCode != 18 && keyCode != 65406 && keyCode != 155 && keyCode != 127 && keyCode != 33
				&& keyCode != 34 && keyCode != 20 && keyCode != 35 && keyCode != 36 && keyCode != 144 && keyCode != 37
				&& keyCode != 38 && keyCode != 39 && keyCode != 40);

	}

	/**
	 * Calcula una cadena de espacios del tamaño especificado.
	 *
	 * @param numeroEspacios Número de espacios que se desea generar.
	 * @return Cadena de espacios generada.
	 */

	public static String calcularNumeroEspacios(int numeroEspacios) {

		String resultado = "";

		for (int i = 0; i < numeroEspacios; i++)

			resultado = String.valueOf(resultado) + " ";

		return resultado;

	}

	/**
	 * Convierte un ArrayList en una LinkedList.
	 *
	 * @param arrayList ArrayList que se desea convertir.
	 * @param <T>       Tipo de los elementos contenidos en la lista.
	 * @return LinkedList generada desde el ArrayList proporcionado.
	 */

	public static <T> LinkedList<T> convertirArrayListALinkedList(ArrayList<T> arrayList) {

		return new LinkedList<>(arrayList);

	}

	/**
	 * Convierte una LinkedList en un ArrayList.
	 *
	 * @param linkedList LinkedList que se desea convertir.
	 * @param <T>        Tipo de los elementos contenidos en la lista.
	 * @return ArrayList generado desde la LinkedList proporcionada.
	 */

	public static <T> ArrayList<T> convertirLinkedListAArrayList(LinkedList<T> linkedList) {

		return new ArrayList<>(linkedList);

	}

	/**
	 * Calcula el seno de un ángulo dado en grados.
	 *
	 * @param angulo Ángulo en grados del cual se desea calcular el seno.
	 * @return Seno del ángulo dado.
	 */

	public static double calcularSeno(double angulo) {

		return Math.sin(Math.toRadians(angulo));

	}

	/**
	 * Calcula el coseno de un ángulo dado en grados.
	 *
	 * @param angulo Ángulo en grados del cual se desea calcular el coseno.
	 * @return Coseno del ángulo dado.
	 */

	public static double calcularCoseno(double angulo) {

		return Math.cos(Math.toRadians(angulo));

	}

	/**
	 * Calcula la tangente de un ángulo dado en grados.
	 *
	 * @param angulo Ángulo en grados del cual se desea calcular la tangente.
	 * @return Tangente del ángulo dado.
	 */

	public static double calcularTangente(double angulo) {

		return Math.tan(Math.toRadians(angulo));

	}

	/**
	 * Redimensiona una imagen representada como un objeto Image.
	 *
	 * @param originalImage Imagen original que se desea redimensionar.
	 * @param newWidth      Nuevo ancho de la imagen redimensionada.
	 * @param newHeight     Nuevo alto de la imagen redimensionada.
	 * @return Imagen redimensionada como un objeto Image.
	 */

	public static Image resizeImage(Image originalImage, int newWidth, int newHeight) {

		BufferedImage resizedImage = new BufferedImage(newWidth, newHeight, BufferedImage.TYPE_INT_ARGB);

		Graphics2D graphics2D = resizedImage.createGraphics();

		graphics2D.drawImage(originalImage, 0, 0, newWidth, newHeight, null);

		graphics2D.dispose();

		return resizedImage;

	}

	/**
	 * Redimensiona una imagen representada como un objeto BufferedImage.
	 *
	 * @param originalImage Imagen original que se desea redimensionar.
	 * @param newWidth      Nuevo ancho de la imagen redimensionada.
	 * @param newHeight     Nuevo alto de la imagen redimensionada.
	 * @return Imagen redimensionada como un objeto BufferedImage.
	 */

	public static BufferedImage resizeImage(BufferedImage originalImage, int newWidth, int newHeight) {

		BufferedImage resizedImage = new BufferedImage(newWidth, newHeight, BufferedImage.TYPE_INT_ARGB);

		Graphics2D graphics2D = resizedImage.createGraphics();

		graphics2D.drawImage(originalImage, 0, 0, newWidth, newHeight, null);

		graphics2D.dispose();

		return resizedImage;

	}

	/**
	 * Redimensiona una imagen cargada desde un archivo en el disco.
	 *
	 * @param path      Ruta del archivo de imagen que se desea redimensionar.
	 * @param newWidth  Nuevo ancho de la imagen redimensionada.
	 * @param newHeight Nuevo alto de la imagen redimensionada.
	 * @return Imagen redimensionada como un objeto BufferedImage, o null si ocurre
	 *         algún error.
	 */

	public static BufferedImage resizeImage(String path, int newWidth, int newHeight) {

		try {

			BufferedImage originalImage = ImageIO.read(new File(path));

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

	/**
	 * Verifica si un texto cumple con una expresión regular dada.
	 *
	 * @param texto  Texto que se desea verificar.
	 * @param patron Expresión regular que se utilizará para la verificación.
	 * @return true si el texto cumple con la expresión regular, false en caso
	 *         contrario.
	 */

	public static boolean cumpleLaExpresionRegular(String texto, String patron) {

		return Pattern.compile(patron, Pattern.CASE_INSENSITIVE).matcher(texto).find();

	}

	/**
	 * Obtiene la fecha de hoy en formato de LocalDate.
	 *
	 * @param separador Separador a utilizar en la fecha.
	 * @param english   true para formato "año-mes-día", false para formato
	 *                  "día-mes-año".
	 * @param zero      true para agregar cero en meses y días menores a 10.
	 * @return Fecha de hoy como objeto LocalDate.
	 */

	public static LocalDate hoy(String separador, boolean english, boolean zero) {

		return LocalDate.parse(saberFechaActual(separador, english, zero), DateTimeFormatter.ISO_LOCAL_DATE);

	}

	/**
	 * Obtiene la fecha de hoy en formato de String.
	 *
	 * @param separador Separador a utilizar en la fecha.
	 * @param english   true para formato "año-mes-día", false para formato
	 *                  "día-mes-año".
	 * @param zero      true para agregar cero en meses y días menores a 10.
	 * @return Fecha de hoy en formato de String según los parámetros especificados.
	 */

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

	/**
	 * Convierte una imagen a otro formato especificado.
	 *
	 * @param rutaAbsoluta Ruta absoluta del archivo de imagen que se desea
	 *                     convertir.
	 * @param extension    Extensión del formato al cual se desea convertir la
	 *                     imagen.
	 * @return true si la conversión fue exitosa, false si ocurrió algún error.
	 */

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

	/**
	 * Redimensiona una imagen desde la ruta de entrada especificada hacia la ruta
	 * de salida con las dimensiones proporcionadas.
	 * 
	 * @param inputImagePath  Ruta de la imagen de entrada.
	 * @param outputImagePath Ruta donde se guardará la imagen redimensionada.
	 * @param scaledWidth     Ancho al cual redimensionar la imagen, en píxeles.
	 * @param scaledHeight    Alto al cual redimensionar la imagen, en píxeles.
	 * @throws IOException Si ocurre un error durante la lectura o escritura del
	 *                     archivo de imagen.
	 */

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

	/**
	 * Elimina un archivo o directorio especificado por la ruta proporcionada.
	 * 
	 * @param archivo Ruta al archivo o directorio a eliminar.
	 * @throws IOException Si ocurre un error durante la eliminación.
	 */

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

	/**
	 * Convierte una imagen WebP a formato PNG o WebP según la bandera 'png' y
	 * opcionalmente elimina el archivo fuente.
	 * 
	 * @param png             True para convertir a PNG, false para convertir a
	 *                        WebP.
	 * @param src             Ruta al archivo WebP de origen.
	 * @param dest            Ruta de destino para la imagen convertida.
	 * @param eliminarArchivo True para eliminar el archivo fuente después de la
	 *                        conversión.
	 * @throws IOException Si ocurre un error durante la conversión o eliminación.
	 */

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

	/**
	 * Lista archivos o carpetas en un directorio basado en criterios especificados
	 * y los agrega a una lista.
	 * 
	 * @param ruta    Ruta al directorio a listar.
	 * @param lista   Lista a la cual se agregarán nombres de archivos o carpetas.
	 * @param carpeta True para incluir carpetas en la lista, false de lo contrario.
	 * @return Una lista ordenada de rutas de archivos o carpetas.
	 */

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

	/**
	 * Convierte imágenes de un formato de entrada a un formato de salida en una
	 * carpeta específica.
	 *
	 * @param extensionEntrada Extensión del formato de entrada de las imágenes.
	 * @param extensionSalida  Extensión del formato al cual se desea convertir las
	 *                         imágenes.
	 * @param folder           Carpeta que contiene las imágenes que se desean
	 *                         convertir.
	 * @throws IOException Si ocurre un error de lectura o escritura de archivos.
	 */

	public static void convertirImagen(String extensionEntrada, String extensionSalida, String folder)
			throws IOException {

		LinkedList<String> imagenes = (LinkedList<String>) listar(folder, extensionEntrada, false, true);

		File beforeFile;

		File afterFile;

		BufferedImage beforeImg;

		BufferedImage afterImg;

		for (String imagen : imagenes) {

			beforeFile = new File(imagen);

			afterFile = new File(imagen.substring(0, imagen.lastIndexOf(".") + 1) + extensionSalida);

			beforeImg = ImageIO.read(beforeFile);

			afterImg = new BufferedImage(beforeImg.getWidth(), beforeImg.getHeight(), BufferedImage.TYPE_INT_RGB);

			afterImg.createGraphics().drawImage(beforeImg, 0, 0, Color.white, null);

			ImageIO.write(afterImg, extensionSalida, afterFile);

			eliminarFichero(imagen);

		}

	}

	/**
	 * Calcula el checksum SHA-256 de un archivo.
	 *
	 * @param filename Nombre del archivo del cual se desea calcular el checksum.
	 * @return Checksum SHA-256 como una cadena de caracteres.
	 */

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

		}

		finally {

			if (fis != null) {

				fis.close();

			}

		}

		return complete.digest();

	}

	/**
	 * Obtiene el checksum SHA-256 de un archivo como una cadena hexadecimal.
	 *
	 * @param filename Nombre del archivo del cual se desea obtener el checksum.
	 * @return Checksum SHA-256 como una cadena hexadecimal.
	 */

	public static String getSHA256Checksum(String filename) {

		String result = "";

		try {

			byte[] b = createChecksum(filename);

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

	/**
	 * Converts a PNG image to JPG format.
	 * 
	 * @param file Path to the PNG file to convert.
	 * @throws IOException If there is an error reading or writing the image file.
	 */

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

	/**
	 * Checks if a file is an image file (supports multiple formats).
	 * 
	 * @param file Path to the file to check.
	 * @return True if the file is a recognized image format, false otherwise.
	 */

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

	/**
	 * Converts all PNG images in a specified folder to JPG format.
	 * 
	 * @param folder Path to the folder containing PNG images to convert.
	 * @throws IOException If there is an error reading or writing any image file.
	 */

	public static void png2JpgInFolder(String folder) throws IOException {

		LinkedList<String> imagenesPng = (LinkedList<String>) listar(folder, "png", false, true);

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

	/**
	 * Lists files and folders in a directory based on given criteria.
	 * 
	 * @param ruta         Path to the directory to list.
	 * @param extension    File extension filter ("all" for all files, "videos",
	 *                     "images", or specific extension).
	 * @param carpeta      Whether to include folders in the listing.
	 * @param absolutePath Whether to return absolute paths.
	 * @return A sorted list of file or folder paths.
	 */

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

						if (tieneExtensionDeVideo(ruta + fichero)) {

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

	/**
	 * Lista archivos o carpetas en un directorio basado en una lista de extensiones
	 * especificada y opcionalmente agrega rutas absolutas.
	 * 
	 * @param ruta         Ruta al directorio a listar.
	 * @param lista        Array de extensiones de archivos a incluir.
	 * @param carpeta      True para incluir carpetas en la lista, false de lo
	 *                     contrario.
	 * @param absolutePath True para retornar rutas absolutas, false para rutas
	 *                     relativas.
	 * @return Una lista ordenada de rutas de archivos o carpetas que coinciden con
	 *         los criterios.
	 */

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

				if ((!carpeta && folder.isFile() && Arrays.asList(lista).contains(extensionArchivo))
						|| (carpeta && folder.isDirectory())) {

					saberSiEsRutaAbsoluta(ruta, absolutePath, list, fichero);

				}

			}

		}

		Collections.sort(list);

		return list;

	}

	/**
	 * Agrega la ruta absoluta o relativa de un archivo o carpeta a una lista,
	 * dependiendo del parámetro absolutePath.
	 * 
	 * @param ruta         Ruta base de los archivos o carpetas.
	 * @param absolutePath True para agregar rutas absolutas, false para agregar
	 *                     rutas relativas.
	 * @param list         Lista donde se agregará la ruta del archivo o carpeta.
	 * @param fichero      Nombre del archivo o carpeta.
	 */

	static void saberSiEsRutaAbsoluta(String ruta, boolean absolutePath, LinkedList<String> list, String fichero) {

		if (absolutePath) {

			list.add(ruta + fichero);

		}

		else {

			list.add(fichero);

		}

	}

	/**
	 * Elimina los espacios adicionales de una cadena y opcionalmente elimina todos
	 * los espacios.
	 * 
	 * @param cadena Cadena de entrada para eliminar espacios.
	 * @param filtro True para eliminar todos los espacios, false para conservar un
	 *               solo espacio entre palabras.
	 * @return Cadena modificada sin espacios adicionales.
	 */

	public static String eliminarEspacios(String cadena, boolean filtro) {

		cadena = cadena.trim();

		cadena = cadena.replace("   ", "  ");

		cadena = cadena.replace("  ", " ");

		if (filtro) {

			cadena = cadena.replace(" ", "");

		}

		return cadena;

	}

	/**
	 * Obtiene la fecha y hora actual formateada según el patrón especificado.
	 * 
	 * @param pattern Patrón de formato de fecha y hora.
	 * @return Cadena formateada que representa la fecha y hora actuales.
	 */

	public static String saberFechaYHoraActual(String pattern) {

		DateTimeFormatter dtf = DateTimeFormatter.ofPattern(pattern);

		return dtf.format(LocalDateTime.now());

	}

	/**
	 * Obtiene la fecha actual en formato "yyyy-MM-dd".
	 * 
	 * @return Cadena que representa la fecha actual en formato "yyyy-MM-dd".
	 */

	public static String saberFechaActual() {

		Calendar c = Calendar.getInstance();

		return c.get(Calendar.YEAR) + "-" + (c.get(Calendar.MONTH) + 1) + "-" + c.get(Calendar.DATE);

	}

	/**
	 * Obtiene la fecha y hora actual formateada en el formato deseado, inglés o
	 * estándar.
	 * 
	 * @param englishFormat True para formato inglés "yyyy/MM/dd HH:mm:ss", false
	 *                      para formato estándar "dd/MM/yyyy HH:mm:ss".
	 * @return Cadena que representa la fecha y hora actual formateada.
	 */

	public static String saberFechaYHoraActual(boolean englishFormat) {

		String patron = "yyyy/MM/dd HH:mm:ss";

		if (!englishFormat) {

			patron = "dd/MM/yyyy HH:mm:ss";

		}

		DateTimeFormatter dtf = DateTimeFormatter.ofPattern(patron);

		return dtf.format(LocalDateTime.now());

	}

	/**
	 * Verifica si la ruta de archivo especificada tiene una extensión de imagen
	 * reconocida.
	 * 
	 * @param absolutePath Ruta absoluta del archivo para verificar la extensión.
	 * @return True si la extensión del archivo es una imagen reconocida, false de
	 *         lo contrario.
	 */

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

	/**
	 * Verifica si el archivo especificado por su ruta absoluta es un archivo de
	 * video basado en su extensión.
	 * 
	 * @param absolutePath Ruta absoluta del archivo para verificar la extensión.
	 * @return True si la extensión del archivo corresponde a un formato de video
	 *         reconocido, false de lo contrario.
	 */

	public static boolean tieneExtensionDeVideo(String absolutePath) {

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

	/**
	 * Convierte un icono en una imagen.
	 * 
	 * @param icon Icono a convertir en imagen.
	 * @return Imagen resultante del icono.
	 */

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

	/**
	 * Escala una imagen a las dimensiones especificadas.
	 * 
	 * @param srcImg Imagen de entrada a escalar.
	 * @param w      Ancho deseado para la imagen escalada.
	 * @param h      Alto deseado para la imagen escalada.
	 * @return Imagen escalada al tamaño especificado.
	 */

	public static Image getScaledImage(Image srcImg, int w, int h) {

		BufferedImage resizedImg = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);

		Graphics2D g2 = resizedImg.createGraphics();

		g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);

		g2.drawImage(srcImg, 0, 0, w, h, null);

		g2.dispose();

		return resizedImg;

	}

	/**
	 * Redimensiona un ImageIcon a las dimensiones especificadas.
	 * 
	 * @param image  ImageIcon a redimensionar.
	 * @param width  Ancho deseado para el ImageIcon redimensionado.
	 * @param height Alto deseado para el ImageIcon redimensionado.
	 * @return ImageIcon redimensionado al tamaño especificado.
	 */

	public static ImageIcon resize(ImageIcon image, int width, int height) {

		BufferedImage bi = new BufferedImage(width, height, Transparency.TRANSLUCENT);

		Graphics2D g2d = bi.createGraphics();

		g2d.addRenderingHints(new RenderingHints(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY));

		g2d.drawImage(image.getImage(), 0, 0, width, height, null);

		g2d.dispose();

		return new ImageIcon(bi);

	}

	/**
	 * Cuenta la cantidad de archivos en una carpeta que coinciden con una extensión
	 * específica o todos los archivos si se especifica el filtro ".".
	 * 
	 * @param carpeta Carpeta donde se contarán los archivos.
	 * @param filtro  Extensión de archivo a contar o "." para contar todos los
	 *                archivos.
	 * @return Cantidad de archivos que coinciden con el filtro en la carpeta
	 *         especificada.
	 */

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

	/**
	 * Cuenta la cantidad de archivos en una carpeta que coinciden con una extensión
	 * específica o todos los archivos si se especifica el filtro ".".
	 * 
	 * @param carpeta Carpeta donde se contarán los archivos.
	 * @param filtro  Extensión de archivo a contar o "." para contar todos los
	 *                archivos.
	 * @return Cantidad de archivos que coinciden con el filtro en la carpeta
	 *         especificada.
	 */

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

	/**
	 * Ejecuta un programa de Windows Subsystem for Linux (WSL).
	 * 
	 * @param programa Nombre del programa a ejecutar.
	 * @param cmd      Indica si se debe utilizar el comando cmd.
	 */

	public static void ejecutarProgramaDeWsl(String programa, boolean cmd) {

		ejecutarComando("wsl -e " + programa, cmd);

	}

	/**
	 * Ejecuta un comando.
	 * 
	 * @param string Comando a ejecutar.
	 * @param cmd    Indica si se debe utilizar el comando cmd.
	 */

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

	/**
	 * Ejecuta un programa Java.
	 * 
	 * @param string Ruta del programa Java a ejecutar.
	 * @param cmd    Indica si se debe utilizar el comando cmd.
	 */

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

	/**
	 * Obtiene el portapapeles del sistema.
	 * 
	 * @return Objeto Clipboard que representa el portapapeles del sistema.
	 */

	private static Clipboard getSystemClipboard() {

		Toolkit defaultToolkit = Toolkit.getDefaultToolkit();

		return defaultToolkit.getSystemClipboard();

	}

	/**
	 * Obtiene los nombres de las fuentes disponibles en el entorno gráfico local.
	 * 
	 * @return Arreglo de cadenas que representan los nombres de las fuentes
	 *         disponibles.
	 */

	public static String[] getFonts() {

		GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();

		return ge.getAvailableFontFamilyNames();

	}

	/**
	 * Obtiene una lista de los nombres de las fuentes disponibles en el entorno
	 * gráfico local.
	 * 
	 * @return Lista de cadenas que representan los nombres de las fuentes
	 *         disponibles.
	 */

	public static List<String> obtenerFuentes() {

		return Arrays.asList(getFonts());

	}

	/**
	 * Obtiene la ruta del directorio actual.
	 * 
	 * @return Ruta canónica del directorio actual.
	 * @throws IOException Si ocurre un error al obtener la ruta.
	 */

	public static String rutaActual() throws IOException {

		return new File(".").getCanonicalPath() + saberSeparador();

	}

	/**
	 * Abre el explorador de archivos en la ruta especificada.
	 * 
	 * @param ruta Ruta del directorio o archivo a abrir.
	 */

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

	/**
	 * Crea una carpeta en la ruta especificada.
	 *
	 * @param path La ruta de la carpeta a crear.
	 * @return 200 si la carpeta se crea con éxito, 300 si ocurre una excepción.
	 */

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

	/**
	 * Elimina "file:/" de la URL dada.
	 *
	 * @param url La URL a limpiar.
	 * @return La URL limpia.
	 */

	public static String cleanURL(String url) {

		return url.replace("file:/", "");

	}

	/**
	 * Obtiene la extensión del nombre de archivo dado.
	 *
	 * @param nombreArchivo El nombre del archivo.
	 * @return La extensión en minúsculas del archivo.
	 */

	public static String saberExtension(String nombreArchivo) {

		String extension = "";

		if (nombreArchivo.length() >= 3) {

			extension = nombreArchivo.substring(
					nombreArchivo.length() - nombreArchivo.substring(nombreArchivo.lastIndexOf(".") + 1).length());

		}

		return extension.toLowerCase();

	}

	/**
	 * Convierte la duración del video dada a segundos.
	 *
	 * @param duracionVideo La duración del video en formato HH:MM:SS.
	 * @return La duración del video en segundos.
	 */

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

	/**
	 * Cuenta las ocurrencias de una cadena específica en el texto dado.
	 *
	 * @param text   El texto en el que se contarán las ocurrencias.
	 * @param search La cadena a buscar.
	 * @return El número de ocurrencias de la cadena de búsqueda en el texto.
	 */

	public static int contarOcurrencias(String text, String search) {

		int contador = 0;

		while (text.indexOf(search) > -1) {

			text = text.substring(text.indexOf(search) + search.length(), text.length());

			contador++;

		}

		return contador;

	}

	/**
	 * Convierte el número dado de segundos al formato HH:MM:SS.
	 *
	 * @param segundos El número de segundos a convertir.
	 * @return El tiempo en formato HH:MM:SS.
	 */

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

	/**
	 * Obtiene el nombre del archivo de la ruta dada.
	 *
	 * @param ruta La ruta del archivo.
	 * @return El nombre del archivo.
	 */

	public static String saberNombreArchivo(String ruta) {

		String separador = saberSeparador();

		String resultado = "";

		if (ruta.contains(separador)) {

			resultado = ruta.substring(ruta.lastIndexOf(separador), ruta.lastIndexOf("."));

		}

		return resultado;

	}

	/**
	 * Mueve un archivo desde la ruta de origen a la ruta de destino.
	 *
	 * @param origen  La ruta de origen del archivo.
	 * @param destino La ruta de destino del archivo.
	 */

	public static void moverArchivo(String origen, String destino) {

		try {

			Files.move(FileSystems.getDefault().getPath(origen), FileSystems.getDefault().getPath(destino),
					StandardCopyOption.REPLACE_EXISTING);

		}

		catch (Exception e) {

		}

	}

	/**
	 * Escribe un archivo en la ruta especificada con las líneas proporcionadas.
	 *
	 * @param ruta  Ruta del archivo donde se escribirá.
	 * @param lines Líneas que se escribirán en el archivo.
	 */

	public static void escribirFichero(String ruta, List<String> lines) {

		try {

			Path file = Paths.get(ruta);

			Files.write(file, lines, StandardCharsets.UTF_8);

		}

		catch (Exception ex) {

		}

	}

	/**
	 * Lee un archivo de texto desde la ruta especificada y retorna las líneas
	 * leídas.
	 *
	 * @param ruta Ruta del archivo a leer.
	 * @return Lista de cadenas que representan las líneas del archivo.
	 */

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

	/**
	 * Elimina el punto final de la cadena y reemplaza los puntos internos por
	 * guiones bajos.
	 *
	 * @param cadena Cadena en la que se realizarán las modificaciones.
	 * @return Cadena modificada según las reglas establecidas.
	 */

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

	/**
	 * Determina el separador de directorios adecuado según el sistema operativo
	 * actual.
	 *
	 * @return Separador de directorios (por ejemplo, "\\" para Windows, "/" para
	 *         otros sistemas).
	 */

	public static String saberSeparador() {

		if (System.getProperty("os.name").contains("indows")) {

			return "\\";

		}

		else {

			return "/";

		}

	}

	/**
	 * Convierte una cadena en formato UTF-8 a ISO-8859-1.
	 *
	 * @param s Cadena en formato UTF-8.
	 * @return Cadena convertida a ISO-8859-1.
	 */

	public static String convertToUTF8(String s) {

		String out = "";

		out = new String(s.getBytes(StandardCharsets.UTF_8), StandardCharsets.ISO_8859_1);

		return out;

	}

	/**
	 * Obtiene el directorio actual del sistema.
	 *
	 * @return Ruta del directorio actual del sistema, terminada con el separador de
	 *         directorios adecuado.
	 */

	public static String directorioActual() {

		String resultado = "";

		try {

			resultado = new File(".").getCanonicalPath() + saberSeparador();

		}

		catch (IOException e) {

		}

		return resultado;

	}

	/**
	 * Lee un número específico de líneas desde un archivo en la ruta relativa
	 * especificada.
	 *
	 * @param rutaRelativa Ruta relativa del archivo a leer.
	 * @param longitud     Número de líneas que se van a leer desde el archivo.
	 * @return Arreglo de cadenas con las líneas leídas desde el archivo.
	 * @throws IOException Si ocurre un error al leer el archivo.
	 */

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

			}

			catch (Exception e) {

			}

			finally {

				if (fE != null) {

					try {

						fE.close();

					}

					catch (IOException e) {

					}

				}

				if (flE != null) {

					try {

						flE.close();

					}

					catch (IOException e) {

					}

				}

			}

		}

		else {

			throw new IOException();

		}

		return salida;

	}

	/**
	 * Reproduce un archivo de sonido especificado por su nombre.
	 *
	 * @param nombreSonido Nombre del archivo de sonido a reproducir.
	 */

	public static void reproducirSonido(String nombreSonido) {

		try {

			AudioInputStream audioInputStream = AudioSystem
					.getAudioInputStream(new File(nombreSonido).getAbsoluteFile());

			Clip clip = AudioSystem.getClip();

			clip.open(audioInputStream);

			clip.start();

		}

		catch (UnsupportedAudioFileException | IOException | LineUnavailableException ex) {

		}

	}

	/**
	 * Elimina todos los archivos con una extensión específica en la ruta
	 * especificada.
	 *
	 * @param ruta      Ruta donde se encuentran los archivos a eliminar.
	 * @param extension Extensión de los archivos a eliminar (por ejemplo, "txt",
	 *                  "jpg", etc.).
	 * @throws IOException Si ocurre un error al eliminar los archivos.
	 */

	public static void eliminarArchivos(String ruta, String extension) throws IOException {

		LinkedList<String> frames = (LinkedList<String>) listar(ruta, extension, false, true);

		for (int i = 0; i < frames.size(); i++) {

			if (!frames.get(i).isEmpty()) {

				eliminarFichero(ruta + saberSeparador() + frames.get(i));

			}

		}

	}

	/**
	 * Elimina todos los archivos especificados en la lista proporcionada.
	 *
	 * @param listaFicheros Lista de rutas de archivos a eliminar.
	 * @throws IOException Si ocurre un error al eliminar los archivos.
	 */

	public static void eliminarArchivos(LinkedList<String> listaFicheros) throws IOException {

		for (int i = 0; i < listaFicheros.size(); i++) {

			if (!listaFicheros.get(i).isEmpty()) {

				eliminarFichero(listaFicheros.get(i));

			}

		}

	}

	/**
	 * Lee todo el contenido de un objeto Reader y lo devuelve como una cadena.
	 *
	 * @param rd Reader del cual se leerá el contenido.
	 * @return Cadena que representa todo el contenido leído del Reader.
	 * @throws IOException Si ocurre un error de lectura.
	 */

	public static String readAll(Reader rd) throws IOException {

		StringBuilder sb = new StringBuilder();

		int cp;

		while ((cp = rd.read()) != -1) {

			sb.append((char) cp);

		}

		return sb.toString();

	}

	/**
	 * Lee un objeto JSON desde una URL y devuelve un objeto JSONObject.
	 *
	 * @param url URL desde donde se leerá el JSON.
	 * @return Objeto JSONObject que representa el JSON leído desde la URL.
	 * @throws IOException Si ocurre un error al leer desde la URL.
	 */

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

	/**
	 * Extrae la carpeta de una ruta de archivo dada.
	 *
	 * @param ruta Ruta de archivo de la cual se extraerá la carpeta.
	 * @return Carpeta extraída de la ruta de archivo.
	 */

	public static String extraerCarpeta(String ruta) {

		return ruta.substring(0, ruta.lastIndexOf(saberSeparador()) + 1);

	}

	/**
	 * Elimina un archivo especificado por su ruta.
	 *
	 * @param file Ruta del archivo que se eliminará.
	 * @return Código de respuesta indicando el resultado de la operación (200, 400,
	 *         404, 501, 502).
	 * @throws IOException Si ocurre un error al intentar eliminar el archivo.
	 */

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

	/**
	 * Crea un nuevo archivo con el contenido especificado.
	 *
	 * @param filePath Ruta del archivo que se creará.
	 * @param texto    Contenido que se escribirá en el archivo.
	 * @return Código de respuesta indicando el resultado de la operación (200, 400,
	 *         500).
	 */

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

	/**
	 * Elimina todos los archivos en una carpeta especificada.
	 *
	 * @param ruta Ruta de la carpeta que se desea vaciar.
	 * @throws IOException Si ocurre un error al intentar eliminar los archivos.
	 */

	public static void vaciarCarpeta(String ruta) throws IOException {

		LinkedList<String> frames = (LinkedList<String>) listar(ruta, ".", false, true);

		for (int i = 0; i < frames.size(); i++) {

			if (!frames.get(i).isEmpty()) {

				eliminarFichero(frames.get(i));

			}

		}

	}

	/**
	 * Realiza un ping a una URL especificada para verificar su disponibilidad.
	 *
	 * @param url URL a la cual se realizará el ping.
	 * @return true si la URL está disponible, false si no lo está.
	 */

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

	/**
	 * Renombra un archivo especificado añadiéndole una nueva extensión.
	 *
	 * @param archivo   Ruta del archivo que se desea renombrar.
	 * @param extension Nueva extensión que se añadirá al archivo.
	 */

	public static void renombrarArchivo(String archivo, String extension) {

		try {

			new File(archivo).renameTo(new File(archivo.substring(0, archivo.lastIndexOf(".") + 1) + extension));

		}

		catch (Exception e) {

		}

	}

}
