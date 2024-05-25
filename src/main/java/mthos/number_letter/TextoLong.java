package mthos.number_letter;

import java.util.HashMap;
import java.util.Map;

public class TextoLong {

	private int flag;

	public int numero;

	public String importe_parcial;

	public String num;

	public String num_letra;

	public String num_letras;

	public String num_letram;

	public String num_letradm;

	public String num_letracm;

	public String num_letramm;

	public String num_letradmm;

	public static String numeroALetras(int num) {

		int numNegativo = num;

		if (num < 0) {

			num *= -1;

		}

		String resultado = new TextoLong(num).convertirLetras(num);

		if (numNegativo < 0) {

			resultado = "menos " + resultado;

		}

		resultado = resultado.replace("null", "");

		resultado = resultado.replace("  ", " ");

		resultado = resultado.replace("veintiunodos", "veintidos");

		resultado = resultado.replace("veintiuno", "veintiun");

		return resultado;

	}

	public static long textoALong(String numeroTexto) throws NumberFormatException {

		Map<String, Long> numeros = new HashMap<>();

		numeros.put("cero", 0L);
		numeros.put("uno", 1L);
		numeros.put("dos", 2L);
		numeros.put("tres", 3L);
		numeros.put("cuatro", 4L);
		numeros.put("cinco", 5L);
		numeros.put("seis", 6L);
		numeros.put("siete", 7L);
		numeros.put("ocho", 8L);
		numeros.put("nueve", 9L);
		numeros.put("diez", 10L);
		numeros.put("once", 11L);
		numeros.put("doce", 12L);
		numeros.put("trece", 13L);
		numeros.put("catorce", 14L);
		numeros.put("quince", 15L);
		numeros.put("dieciséis", 16L);
		numeros.put("diecisiete", 17L);
		numeros.put("dieciocho", 18L);
		numeros.put("diecinueve", 19L);
		numeros.put("veinte", 20L);
		numeros.put("treinta", 30L);
		numeros.put("cuarenta", 40L);
		numeros.put("cincuenta", 50L);
		numeros.put("sesenta", 60L);
		numeros.put("setenta", 70L);
		numeros.put("ochenta", 80L);
		numeros.put("noventa", 90L);
		numeros.put("cien", 100L);
		numeros.put("doscientos", 200L);
		numeros.put("trescientos", 300L);
		numeros.put("cuatrocientos", 400L);
		numeros.put("quinientos", 500L);
		numeros.put("seiscientos", 600L);
		numeros.put("setecientos", 700L);
		numeros.put("ochocientos", 800L);
		numeros.put("novecientos", 900L);
		numeros.put("mil", 1000L);
		numeros.put("millón", 1000000L);
		numeros.put("millones", 1000000L);
		numeros.put("billón", 1000000000000L);
		numeros.put("trillón", 1000000000000000L);
		numeros.put("cuatrillón", 1000000000000000000L);
		numeros.put("veintiun", 21L);
		numeros.put("treintaun", 31L);
		numeros.put("cuarentaun", 41L);
		numeros.put("cincuentaun", 51L);
		numeros.put("sesentaun", 61L);
		numeros.put("setentaun", 71L);
		numeros.put("ochentaun", 81L);
		numeros.put("noventaun", 91L);

		String[] palabras = numeroTexto.split(" ");

		long resultado = 0;

		long parcial = 0;

		boolean esNegativo = false;

		for (int i = 0; i < palabras.length; i++) {

			String palabra = palabras[i];

			if (palabra.equals("menos")) {

				esNegativo = true;

				continue;

			}

			if (palabra.equals("y")) {

				continue;

			}

			palabra = palabra.replaceAll("[áéíóú]", "aeiou");

			Long valor = numeros.get(palabra);

			if (valor == null) {

				if (palabra.startsWith("veinti")) {

					long valorCompuesto = numeros.get("veinte");

					if (palabra.length() > 6) {

						String unidadStr = palabra.substring(6);

						Long unidadValor = numeros.get(unidadStr);

						if (unidadValor != null) {

							valorCompuesto += unidadValor;

						}

						else {

							throw new NumberFormatException("Palabra no reconocida: " + palabra);

						}

					}

					valor = valorCompuesto;

				}

				else {

					throw new NumberFormatException("Palabra no reconocida: " + palabra);

				}

			}

			if (valor == 100) {

				parcial *= 100;

			}

			else if (valor == 1000) {

				resultado += parcial * 1000;

				parcial = 0;

			}

			else if (valor == 1000000) {

				resultado += parcial * 1000000;

				parcial = 0;

			}

			else {

				parcial += valor;

			}

		}

		resultado += parcial;

		if (esNegativo) {

			resultado *= -1;

		}

		return resultado;

	}

	public TextoLong(int n) {

		numero = n;

		flag = 0;

		if (numero < 0) {

			numero *= -1;

			if (numero < 10) {

				num = "menos ";

			}

			if (numero >= 10 && numero < 100) {

				num_letra = "menos ";

			}

		}

	}

	private String unidad(int numero) {

		if (this.numero == 0) {

			num = "cero";

		}

		switch (numero) {

		case 9:

			num += "nueve";

			break;

		case 8:

			num += "ocho";

			break;

		case 7:

			num += "siete";

			break;

		case 6:

			num += "seis";

			break;

		case 5:

			num += "cinco";

			break;

		case 4:

			num += "cuatro";

			break;

		case 3:

			num += "tres";

			break;

		case 2:

			num += "dos";

			break;

		case 1:

			if (flag == 0)
				num += "uno";

			else
				num = "uno";

			break;

		case 0:

			if (num.isEmpty()) {

				num = "";

			}

			break;

		}

		return num;

	}

	private String decena(int numero) {

		if (numero >= 90 && numero <= 99) {

			num_letra = "noventa ";

			if (numero > 90)
				num_letra = num_letra.concat("y ").concat(unidad(numero - 90));

		}

		else if (numero >= 80 && numero <= 89) {

			num_letra = "ochenta ";

			if (numero > 80)

				num_letra = num_letra.concat("y ").concat(unidad(numero - 80));

		}

		else if (numero >= 70 && numero <= 79) {

			num_letra = "setenta ";

			if (numero > 70)

				num_letra = num_letra.concat("y ").concat(unidad(numero - 70));

		}

		else if (numero >= 60 && numero <= 69) {

			num_letra = "sesenta ";

			if (numero > 60)

				num_letra = num_letra.concat("y ").concat(unidad(numero - 60));

		}

		else if (numero >= 50 && numero <= 59) {

			num_letra = "cincuenta ";

			if (numero > 50)

				num_letra = num_letra.concat("y ").concat(unidad(numero - 50));

		} else if (numero >= 40 && numero <= 49) {

			num_letra = "cuarenta ";

			if (numero > 40)
				num_letra = num_letra.concat("y ").concat(unidad(numero - 40));

		}

		else if (numero >= 30 && numero <= 39) {

			num_letra = "treinta ";

			if (numero > 30)

				num_letra = num_letra.concat("y ").concat(unidad(numero - 30));

		}

		else if (numero >= 20 && numero <= 29) {

			if (numero == 20)
				num_letra = "veinte ";
			else
				num_letra = "veinti".concat(unidad(numero - 20));

		}

		else if (numero >= 10 && numero <= 19) {

			switch (numero) {

			case 10:

				num_letra = "diez ";

				break;

			case 11:

				num_letra = "once ";

				break;

			case 12:

				num_letra = "doce ";

				break;

			case 13:

				num_letra = "trece ";

				break;

			case 14:

				num_letra = "catorce ";

				break;

			case 15:

				num_letra = "quince ";

				break;

			case 16:

				num_letra = "dieciseis ";

				break;

			case 17:

				num_letra = "diecisiete ";

				break;

			case 18:

				num_letra = "dieciocho ";

				break;

			case 19:

				num_letra = "diecinueve ";

				break;

			}

		}

		else

			num_letra = unidad(numero);

		return num_letra;

	}

	private String centena(int numero) {

		if (numero >= 100) {

			if (numero >= 900 && numero <= 999) {

				num_letra = "novecientos ";

				if (numero > 900)

					num_letra = num_letra.concat(decena(numero - 900));

			}

			else if (numero >= 800 && numero <= 899) {

				num_letra = "ochocientos ";

				if (numero > 800)
					num_letra = num_letra.concat(decena(numero - 800));

			}

			else if (numero >= 700 && numero <= 799) {

				num_letra = "setecientos ";

				if (numero > 700)

					num_letra = num_letra.concat(decena(numero - 700));

			}

			else if (numero >= 600 && numero <= 699) {

				num_letra = "seiscientos ";

				if (numero > 600)

					num_letra = num_letra.concat(decena(numero - 600));

			}

			else if (numero >= 500 && numero <= 599) {

				num_letra = "quinientos ";

				if (numero > 500)

					num_letra = num_letra.concat(decena(numero - 500));

			}

			else if (numero >= 400 && numero <= 499) {

				num_letra = "cuatrocientos ";

				if (numero > 400)

					num_letra = num_letra.concat(decena(numero - 400));

			}

			else if (numero >= 300 && numero <= 399) {

				num_letra = "trescientos ";

				if (numero > 300)

					num_letra = num_letra.concat(decena(numero - 300));

			}

			else if (numero >= 200 && numero <= 299) {

				num_letra = "doscientos ";

				if (numero > 200)

					num_letra = num_letra.concat(decena(numero - 200));

			}

			else if (numero >= 100 && numero <= 199) {

				if (numero == 100)

					num_letra = "cien ";

				else

					num_letra = "ciento ".concat(decena(numero - 100));

			}

		}

		else

			num_letra = decena(numero);

		return num_letra;

	}

	private String miles(int numero) {

		if (numero >= 1000 && numero < 2000) {

			num_letram = ("mil ").concat(centena(numero % 1000));

		}

		if (numero >= 2000 && numero < 10000) {

			flag = 1;

			num_letram = unidad(numero / 1000).concat(" mil ").concat(centena(numero % 1000));

		}

		if (numero < 1000)

			num_letram = centena(numero);

		return num_letram;

	}

	private String decmiles(int numero) {

		if (numero == 10000)

			num_letradm = "diez mil";

		if (numero > 10000 && numero < 20000) {

			flag = 1;

			num_letradm = decena(numero / 1000).concat("mil ").concat(centena(numero % 1000));

		}

		if (numero >= 20000 && numero < 100000) {

			flag = 1;

			num_letradm = decena(numero / 1000).concat(" mil ").concat(miles(numero % 1000));

		}

		if (numero < 10000)

			num_letradm = miles(numero);

		return num_letradm;

	}

	private String cienmiles(int numero) {

		if (numero == 100000)

			num_letracm = "cien mil";

		if (numero >= 100000 && numero < 1000000) {

			flag = 1;

			num_letracm = centena(numero / 1000).concat(" mil ").concat(centena(numero % 1000));

		}

		if (numero < 100000)

			num_letracm = decmiles(numero);

		return num_letracm;

	}

	private String millon(int numero) {

		if (numero >= 1000000 && numero < 2000000) {

			flag = 1;

			num_letramm = ("Un millon ").concat(cienmiles(numero % 1000000));

		}

		if (numero >= 2000000 && numero < 10000000) {

			flag = 1;

			num_letramm = unidad(numero / 1000000).concat(" millones ").concat(cienmiles(numero % 1000000));

		}

		if (numero < 1000000)

			num_letramm = cienmiles(numero);

		return num_letramm;

	}

	private String decmillon(int numero) {

		if (numero == 10000000)

			num_letradmm = "diez millones";

		if (numero > 10000000 && numero < 20000000) {

			flag = 1;

			num_letradmm = decena(numero / 1000000).concat("millones ").concat(cienmiles(numero % 1000000));

		}

		if (numero >= 20000000 && numero < 100000000) {

			flag = 1;

			num_letradmm = decena(numero / 1000000).concat(" millones ").concat(millon(numero % 1000000));

		}

		if (numero < 10000000)

			num_letradmm = millon(numero);

		return num_letradmm;

	}

	public String convertirLetras(int numero) {

		num_letras = decmillon(numero);

		return num_letras;

	}

}
