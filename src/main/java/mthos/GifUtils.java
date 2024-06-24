package mthos;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import mthos.gif.AnimatedGifEncoder;
import mthos.gif.GifDecoder;

public class GifUtils {

	/**
	 * Crea un GIF a partir de una serie de imágenes en un directorio.
	 *
	 * @param path      La ruta del directorio que contiene las imágenes.
	 * @param output    La ruta del archivo GIF de salida.
	 * @param extension La extensión de los archivos de imagen (por ejemplo, "png").
	 */

	public static void crearGif(String path, String output, String extension) {

		crearGif(path, output, extension, 25f, 1);

	}

	/**
	 * Crea un GIF a partir de una serie de imágenes en un directorio con opciones
	 * adicionales.
	 *
	 * @param path      La ruta del directorio que contiene las imágenes.
	 * @param output    La ruta del archivo GIF de salida.
	 * @param extension La extensión de los archivos de imagen (por ejemplo, "png").
	 * @param frameRate La tasa de fotogramas para el GIF.
	 * @param open      Indica si se debe abrir el archivo después de crearlo.
	 */

	public static void crearGif(String path, String output, String extension, float frameRate, int open) {

		try {

			path = JMthos.pasarDeArchivoACarpeta(path);

			LinkedList<String> lista = (LinkedList<String>) JMthos.listar(path, extension, false, true);

			if (!lista.isEmpty()) {

				AnimatedGifEncoder e = new AnimatedGifEncoder();

				e.start(output);

				for (int i = 0; i < lista.size(); i++) {

					e.addFrame(JMthos.fileToBufferedImage(new File(lista.get(i))));

				}

				e.setFrameRate(frameRate);

				e.finish();

				JMthos.abrir(output, open);

			}

		}

		catch (Exception e) {

			// Manejo de excepciones

		}

	}

	/**
	 * Optimiza un archivo GIF utilizando Gifsicle.
	 *
	 * @param input        La ruta del archivo GIF de entrada.
	 * @param output       La ruta del archivo GIF de salida.
	 * @param gifsiclePath La ruta de la herramienta Gifsicle.
	 * @param level        El nivel de optimización (1-3).
	 * @param open         Indica si se debe abrir el archivo después de
	 *                     optimizarlo.
	 * @throws IOException          Si ocurre un error de E/S.
	 * @throws InterruptedException Si el proceso es interrumpido.
	 */

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

				JMthos.abrir(output, open);

			}

		}

		catch (IOException | InterruptedException e) {

			throw new RuntimeException("Error al optimizar el GIF", e);

		}

	}

	/**
	 * Optimiza un archivo GIF utilizando Gifsicle con un nivel de optimización
	 * especificado.
	 *
	 * @param input  La ruta del archivo GIF de entrada.
	 * @param output La ruta del archivo GIF de salida.
	 * @param level  El nivel de optimización (1-3).
	 * @throws IOException          Si ocurre un error de E/S.
	 * @throws InterruptedException Si el proceso es interrumpido.
	 */

	public static void optimizeGifWithGifsicle(String input, String output, int level)
			throws IOException, InterruptedException {

		optimizeGifWithGifsicle(input, output, "", level, 0);

	}

	/**
	 * Optimiza un archivo GIF utilizando Gifsicle con una ruta de herramienta
	 * especificada y un nivel de optimización.
	 *
	 * @param input        La ruta del archivo GIF de entrada.
	 * @param output       La ruta del archivo GIF de salida.
	 * @param gifsiclePath La ruta de la herramienta Gifsicle.
	 * @param level        El nivel de optimización (1-3).
	 * @throws IOException          Si ocurre un error de E/S.
	 * @throws InterruptedException Si el proceso es interrumpido.
	 */

	public static void optimizeGifWithGifsicle(String input, String output, String gifsiclePath, int level)
			throws IOException, InterruptedException {

		optimizeGifWithGifsicle(input, output, gifsiclePath, level, 0);

	}

	/**
	 * Optimiza un archivo GIF utilizando Gifsicle con una ruta de herramienta
	 * especificada.
	 *
	 * @param input        La ruta del archivo GIF de entrada.
	 * @param output       La ruta del archivo GIF de salida.
	 * @param gifsiclePath La ruta de la herramienta Gifsicle.
	 * @throws IOException          Si ocurre un error de E/S.
	 * @throws InterruptedException Si el proceso es interrumpido.
	 */

	public static void optimizeGifWithGifsicle(String input, String output, String gifsiclePath)
			throws IOException, InterruptedException {

		optimizeGifWithGifsicle(input, output, gifsiclePath, 3, 0);

	}

	/**
	 * Exporta los fotogramas de un archivo GIF a una carpeta de salida.
	 *
	 * @param filePath     La ruta del archivo GIF de entrada.
	 * @param outputFolder La carpeta de salida para los fotogramas.
	 */

	public static void exportarFramesGif(String filePath, String outputFolder) {

		exportarFramesGif(filePath, outputFolder, "", "png", true);

	}

	/**
	 * Exporta los fotogramas de un archivo GIF a una carpeta de salida con una
	 * extensión especificada.
	 *
	 * @param filePath     La ruta del archivo GIF de entrada.
	 * @param outputFolder La carpeta de salida para los fotogramas.
	 * @param extension    La extensión de los archivos de imagen de salida.
	 */

	public static void exportarFramesGif(String filePath, String outputFolder, String extension) {

		exportarFramesGif(filePath, outputFolder, "", extension, true);

	}

	/**
	 * Exporta los fotogramas de un archivo GIF a una carpeta de salida con un
	 * nombre y una extensión especificados.
	 *
	 * @param filePath     La ruta del archivo GIF de entrada.
	 * @param outputFolder La carpeta de salida para los fotogramas.
	 * @param name         El nombre base para los archivos de imagen de salida.
	 * @param extension    La extensión de los archivos de imagen de salida.
	 */

	public static void exportarFramesGif(String filePath, String outputFolder, String name, String extension) {

		exportarFramesGif(filePath, outputFolder, name, extension, true);

	}

	/**
	 * Optimiza un archivo GIF utilizando un codificador de GIF animado.
	 *
	 * @param input  La ruta del archivo GIF de entrada.
	 * @param output La ruta del archivo GIF de salida.
	 */

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

	/**
	 * Exporta los fotogramas de un archivo GIF a una carpeta de salida con opciones
	 * avanzadas.
	 *
	 * @param filePath     La ruta del archivo GIF de entrada.
	 * @param outputFolder La carpeta de salida para los fotogramas.
	 * @param name         El nombre base para los archivos de imagen de salida.
	 * @param extension    La extensión de los archivos de imagen de salida.
	 * @param openOutput   Indica si se debe abrir la carpeta de salida después de
	 *                     exportar.
	 */

	public static void exportarFramesGif(String filePath, String outputFolder, String name, String extension,
			boolean openOutput) {

		outputFolder = JMthos.pasarDeArchivoACarpeta(outputFolder);

		extension = extension.toLowerCase();

		boolean jpg = extension.equals("jpg");

		if (name == null || name.equals("")) {

			name = "_frame_";

		}

		if (jpg || extension == null || extension.equals("")) {

			extension = "png";

		}

		if (outputFolder == null || outputFolder.equals("")) {

			outputFolder = filePath.substring(0, filePath.lastIndexOf(JMthos.saberSeparador()) + 1);

		}

		else if (!new File(outputFolder).exists()) {

			JMthos.crearCarpeta(outputFolder);

		}

		if (esGifAnimado(filePath)) {

			GifDecoder d = new GifDecoder();

			d.read(filePath);

			BufferedImage frame;

			int contador = 1;

			for (int i = 0; i < d.getFrameCount(); i++) {

				frame = d.getFrame(i);

				try {

					JMthos.exportBufferedImage(frame, extension,
							JMthos.ponerSeparador(outputFolder) + name + contador + "." + extension);

					if (jpg) {

						JMthos.convertPNGtoJPG(JMthos.ponerSeparador(outputFolder) + name + contador + "." + extension,
								JMthos.ponerSeparador(outputFolder) + name + contador + "." + "jpg");

						JMthos.eliminarArchivo(JMthos.ponerSeparador(outputFolder) + name + contador + "." + extension);

					}

				}

				catch (Exception e) {

				}

				contador++;

			}

			if (openOutput) {

				JMthos.abrir(outputFolder);

			}

		}

	}

	/**
	 * Verifica si un archivo es un GIF animado.
	 *
	 * @param filePath La ruta del archivo GIF.
	 * @return {@code true} si el archivo es un GIF animado, {@code false} en caso
	 *         contrario.
	 */

	public static boolean esGifAnimado(String filePath) {

		GifDecoder d = new GifDecoder();

		d.read(filePath);

		return d.getLoopCount() > -1;

	}

}
