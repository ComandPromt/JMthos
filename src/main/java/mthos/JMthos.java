package mthos;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.GraphicsConfiguration;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.Image;
import java.awt.RenderingHints;
import java.awt.Toolkit;
import java.awt.Transparency;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
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
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import javax.imageio.ImageIO;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;
import javax.swing.Icon;
import javax.swing.ImageIcon;

import org.apache.commons.io.FileUtils;
import org.json.JSONObject;

import io.github.biezhi.webp.WebpIO;

public abstract class JMthos {

	public static boolean convertImg(String rutaAbsoluta, String extension)

			throws IOException {

		extension = extension.toLowerCase();

		FileInputStream inputStream = new FileInputStream(rutaAbsoluta);

		FileOutputStream outputStream = new FileOutputStream(
				rutaAbsoluta.substring(0, rutaAbsoluta.lastIndexOf(".") + 1) + extension);

		BufferedImage inputImage = ImageIO.read(inputStream);

		boolean result = ImageIO.write(inputImage, extension, outputStream);

		outputStream.close();

		inputStream.close();

		return result;

	}

	public static double truncateDouble(double number, int numDigits) {

		double result = number;

		String arg = "" + number;

		int idx = arg.indexOf('.');

		if (idx != -1 && (arg.length() > idx + numDigits)) {

			arg = arg.substring(0, idx + numDigits + 1);

			result = Double.parseDouble(arg);

		}

		return result;

	}

	static final String OS = System.getProperty("os.name");

	private JMthos() {

	}

	public static void convertirImagen(String extensionEntrada, String extensionSalida, String folder)
			throws IOException {

		LinkedList<String> imagenesPng = new LinkedList<String>();

		imagenesPng = (LinkedList<String>) listar(folder, extensionEntrada, false);

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

	private static byte[] createChecksum(String filename) throws NoSuchAlgorithmException, IOException {

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

			if (!fichero.isDirectory()) {

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

				extensionArchivo = extraerExtension(fichero);

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

		java.awt.Color color = null;

		afterImg.createGraphics().drawImage(beforeImg, 0, 0, color.white, null);

		ImageIO.write(afterImg, "jpg", afterFile);

		eliminarFichero(file);

	}

	public static void png2JpgInFolder(String folder) throws IOException {

		LinkedList<String> imagenesPng = new LinkedList<String>();

		imagenesPng = (LinkedList<String>) listar(folder, "png", false);

		File beforeFile;

		File afterFile;

		for (int i = 0; i < imagenesPng.size(); i++) {

			beforeFile = new File(imagenesPng.get(i));

			afterFile = new File(imagenesPng.get(i).substring(0, imagenesPng.get(i).lastIndexOf(".")) + ".jpg");

			BufferedImage beforeImg = ImageIO.read(beforeFile);

			BufferedImage afterImg = new BufferedImage(beforeImg.getWidth(), beforeImg.getHeight(),
					BufferedImage.TYPE_INT_RGB);

			java.awt.Color color = null;

			afterImg.createGraphics().drawImage(beforeImg, 0, 0, color.white, null);

			ImageIO.write(afterImg, "jpg", afterFile);

			eliminarFichero(imagenesPng.get(i));

		}

	}

	public static List<String> listar(String ruta, String extension, boolean carpeta) {

		LinkedList<String> lista = new LinkedList<>();

		File f = new File(ruta);

		ArrayList<String> videosPermitidos = new ArrayList<>();

		videosPermitidos.add("mp4");

		videosPermitidos.add("mpg");

		videosPermitidos.add("avi");

		videosPermitidos.add("mkv");

		ArrayList<String> imagenesPermitidas = new ArrayList<>();

		imagenesPermitidas.add("jpg");

		imagenesPermitidas.add("png");

		imagenesPermitidas.add("jpeg");

		imagenesPermitidas.add("gif");

		if (f.exists()) {

			File[] ficheros = f.listFiles();

			String fichero = "";

			String extensionArchivo;

			File folder;

			for (int x = 0; x < ficheros.length; x++) {

				fichero = ficheros[x].getName();

				folder = new File(fichero);

				extensionArchivo = extraerExtension(fichero);

				if (carpeta && folder.isDirectory()) {

					lista.add(fichero);

				}

				else if (!carpeta && folder.isFile()) {

					switch (extension) {

					case "all":

						lista.add(ruta + fichero);

						break;

					case "videos":

						if (videosPermitidos.contains(extensionArchivo)) {

							lista.add(ruta + fichero);

						}

						break;

					case "images":

						if (imagenesPermitidas.contains(extensionArchivo)) {

							lista.add(ruta + fichero);

						}

						break;

					default:

						if (extension.equals(extensionArchivo)) {

							lista.add(ruta + fichero);

						}

						break;

					}

				}

			}

		}

		Collections.sort(lista);

		return lista;

	}

	public static List<String> listarConArray(String ruta, String[] lista, boolean carpeta) {

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

				extensionArchivo = extraerExtension(fichero);

				if (carpeta && folder.isDirectory()) {

					list.add(fichero);

				}

				else if (!carpeta && folder.isFile() &&

						Arrays.asList(lista).contains(extensionArchivo)) {

					list.add(ruta + fichero);

				}

			}

		}

		Collections.sort(list);

		return list;

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

	public static boolean esImagen(String absolutePath) {

		boolean resultado = false;

		switch (extraerExtension(absolutePath)) {

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

		switch (extraerExtension(absolutePath)) {

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

	public static int listarFicherosPorCarpeta(String carpeta, String filtro) {

		File exCarpeta = new File(carpeta);

		int ocurrencias = 0;

		if (exCarpeta.isDirectory()) {

			String extension;

			String nombreArchivo;

			File folder;

			for (final File ficheroEntrada : exCarpeta.listFiles()) {

				nombreArchivo = ficheroEntrada.getName();

				extension = extraerExtension(nombreArchivo);

				folder = new File(exCarpeta + saberSeparador() + nombreArchivo);

				if (!folder.isDirectory() && (extension.equals(filtro) || filtro.equals("."))) {

					ocurrencias++;

				}

			}

		}

		return ocurrencias;

	}

	public static int listarFicherosPorCarpeta(final File carpeta, String filtro) {

		int ocurrencias = 0;

		if (carpeta.isDirectory()) {

			String extension;

			String nombreArchivo;

			File folder;

			for (final File ficheroEntrada : carpeta.listFiles()) {

				nombreArchivo = ficheroEntrada.getName();

				extension = extraerExtension(nombreArchivo);

				folder = new File(carpeta + saberSeparador() + nombreArchivo);

				if (!folder.isDirectory() && (extension.equals(filtro) || filtro.equals("."))) {

					ocurrencias++;

				}

			}

		}

		return ocurrencias;

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
					+ " && java -jar \"" + string + "\"");

		}

		catch (Exception e) {

			e.printStackTrace();

		}

	}

	private static Clipboard getSystemClipboard() {

		Toolkit defaultToolkit = Toolkit.getDefaultToolkit();

		return defaultToolkit.getSystemClipboard();

	}

	public static void copiarAlPortapapeles(String text) {

		try {

			Clipboard clipboard = getSystemClipboard();

			clipboard.setContents(new StringSelection(text), null);

		}

		catch (Exception e) {

		}

	}

	public static String[] getFonts() {

		GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();

		return ge.getAvailableFontFamilyNames();

	}

	public static String rutaActual() throws IOException {

		return new File(".").getCanonicalPath() + saberSeparador();

	}

	public static void abrirCarpeta(String ruta) throws IOException {

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

	public static void crearCarpeta(String path) {

		new File(path).mkdir();

	}

	public static String cleanURL(String url) {

		return url.replace("file:/", "");

	}

	public static String extraerExtension(String nombreArchivo) {

		String extension = "";

		if (nombreArchivo.length() >= 3) {

			extension = nombreArchivo.substring(
					nombreArchivo.length() - nombreArchivo.substring(nombreArchivo.lastIndexOf(".") + 1).length());

		}

		return extension.toLowerCase();

	}

	public static double convertirASegundos(String duracionVideo) {

		double horas, minutos, segundos;

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

	public static String calcularTiempo(long segundos) {

		int minutos = 0;

		int horas = 0;

		if (segundos == 60) {

			minutos = 1;

			segundos = 0;

		}

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

		String ceroHoras = "";

		String ceroMinutos = "";

		String ceroSegundos = "";

		if (horas <= 9) {

			ceroHoras = "0";

		}

		if (minutos <= 9) {

			ceroMinutos = "0";

		}

		if (segundos <= 9) {

			ceroSegundos = "0";

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

			cadena = cadena2.replace(".", "_") + "." + extraerExtension(cadena);

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

		LinkedList<String> frames = (LinkedList<String>) listar(ruta, extension, false);

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

	private static String readAll(Reader rd) throws IOException {

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

	public static int crearFichero(String file) {

		short respuesta = 400;

		try {

			File archivo = new File(file);

			if (archivo.exists() && archivo.createNewFile()) {

				respuesta = 200;

			}

		}

		catch (IOException e) {

			respuesta = 500;

		}

		return respuesta;

	}

	public static void crearFichero(String ruta, String texto) throws IOException {

		File archivo = new File(ruta);

		try {

			if (!archivo.exists()) {

				archivo.mkdir();

			}

			BufferedWriter bw = new BufferedWriter(new FileWriter(archivo));

			try {

				bw.write(texto);

			}

			finally {

				bw.close();

			}

		}

		catch (Exception e) {

		}

	}

	public static void vaciarCarpeta(String ruta) throws IOException {

		LinkedList<String> frames = (LinkedList<String>) listar(ruta, ".", false);

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

			File f1 = new File(archivo);

			File f2 = new File(archivo.substring(0, archivo.lastIndexOf(".") + 1) + extension);

			f1.renameTo(f2);

		}

		catch (Exception e) {

		}

	}

	public static int renombrar(String ruta1, String ruta2) {

		File f1 = new File(ruta1);

		File f2 = new File(ruta2);

		short respuesta = 400;

		if (f1.exists() && f2.exists() && f1.renameTo(f2)) {

			respuesta = 200;

		}

		return respuesta;

	}

}
