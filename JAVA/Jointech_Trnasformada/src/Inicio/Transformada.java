package Inicio;

import java.util.LinkedHashMap;
import java.util.Map;

public class Transformada  extends Main {
	
	public void setDatos(String trama) {
		
		String Estado = "0204000A"; 		
		int numHex = Integer.parseInt(Estado.substring(1,2), 16);
		
		String binary = Integer.toBinaryString(numHex);
		
		System.out.println(binary);

		
		
		String s = trama; 
		this.parseInsertRecord_JOINTECH(s);
		
	}
		
		private LinkedHashMap<String, String> generateHashMapValues(String caracteres, String valor) {
			LinkedHashMap<String, String> valores = new LinkedHashMap<String, String>();
			valores.put("caracteres", caracteres);
			valores.put("valor", valor);
			return valores;
		}

		private byte[] parseInsertRecord_JOINTECH(String s){	
			
			LinkedHashMap<String, LinkedHashMap<String, String>> indicador = new LinkedHashMap<String, LinkedHashMap<String, String>>();
			indicador.put("Encabezado", generateHashMapValues("2", ""));
			indicador.put("Idmensaje",  generateHashMapValues("4", ""));
			indicador.put("Atributos",  generateHashMapValues("4", ""));
			indicador.put("NumeroIdentificacion",  generateHashMapValues("12", ""));
			indicador.put("MensajeSerie",  generateHashMapValues("4", ""));
			indicador.put("Alarma",  generateHashMapValues("8", ""));
			indicador.put("Estado",  generateHashMapValues("8", ""));
			indicador.put("Latitud",  generateHashMapValues("8", ""));
			indicador.put("Longitud",  generateHashMapValues("8", ""));
			indicador.put("Altitud",  generateHashMapValues("4", ""));
			indicador.put("Velocidad",  generateHashMapValues("4", ""));
			indicador.put("Direccion",  generateHashMapValues("4", ""));
			indicador.put("Fecha",  generateHashMapValues("6", ""));
			indicador.put("Tiempo",  generateHashMapValues("6", ""));
			indicador.put("Bateria",  generateHashMapValues("6", ""));
			indicador.put("BateriaVoltage",  generateHashMapValues("8", ""));
			indicador.put("intensidad",  generateHashMapValues("6", ""));
			indicador.put("NumeroSatelites",  generateHashMapValues("6", ""));
			indicador.put("VersionProtocolo",  generateHashMapValues("8", ""));
			indicador.put("Odometro",  generateHashMapValues("12", ""));
			indicador.put("InformacionCelda",  generateHashMapValues("22", ""));

			indicador.put("empty",  generateHashMapValues("2", ""));
			indicador.put("FinTrama",  generateHashMapValues("2", ""));

			int acumulador = 0;
			int acumuladorAnterior = 0;
			String tramaPorcionada = "";
			int auxiliarLlenadoEnetros = 0 ;
			float auxiliarLlenadoFlotantes = 0 ;

			for(Map.Entry<String, LinkedHashMap<String, String>> k: indicador.entrySet()) {
				tramaPorcionada = "";
				for(Map.Entry<String, String> j:  k.getValue().entrySet()) {
					if(j.getKey() == "caracteres") {
						acumulador += Integer.parseInt(j.getValue());
						tramaPorcionada = s.substring(acumuladorAnterior, acumulador);
						acumuladorAnterior = acumulador;
					}
					if(j.getKey() == "valor") {
						j.setValue(tramaPorcionada);
					}
				}
			}
			System.out.println("Caracteres viejos ------------------");
			System.out.println(indicador);

			
			
						/* Parsing */
				int  	NumeroIdentificacion 	= 0; 
				double  Latitud2 	  			= 0.0;
				double  longitude2				= 0.0;
				double  altitudeM				= 0.0; 
				double 	headingDeg				= 0.0; 
				double  speedKPH2    			= 0.0;
				double  odomKM2      			= 0.0;
				int     numSats        			= 0;	
				double  batteryLevel2			= 0.0;
				double  batteryVolts2 			= 0.0;
				
				double  signalStrength2			= 0;
				int  	satelliteCount2			= 0;
				int		mobileCountryCode2 		= 0;
				int 	mobileNetworkCode2 		= 0;
				int  	cellTowerID2   			= 0;
				int 	locationAreaCode2		= 0;
				
			System.out.println("aca llego 1");
			
			String mobileID 	= "" ;
			long    fixtime   = 0 ;

			for(Map.Entry<String, LinkedHashMap<String, String>> a: indicador.entrySet()) {
				for(Map.Entry<String, String> j:  a.getValue().entrySet()) {
					if (j.getKey() == "valor" && a.getKey() == "Atributos") {
						System.out.println( a.getKey() + " -> " +j.getValue());
						auxiliarLlenadoEnetros = Integer.parseInt(j.getValue(),16);
						j.setValue(Integer.toString(auxiliarLlenadoEnetros));
					}
					if (j.getKey() == "valor" && a.getKey() == "NumeroIdentificacion") {
						System.out.println( a.getKey() + " -> " +j.getValue());
						mobileID = j.getValue();
					}
					if (j.getKey() == "valor" && a.getKey() == "MensajeSerie") {
						System.out.println( a.getKey() + " -> " + j.getValue());
						auxiliarLlenadoEnetros = Integer.parseInt(j.getValue(),16);
						j.setValue(Integer.toString(auxiliarLlenadoEnetros));
					}
					if (j.getKey() == "valor" && a.getKey() == "Estado") {
						System.out.println( a.getKey() + " -> " + j.getValue());
					}
					if (j.getKey() == "valor" && a.getKey() == "Latitud") {
						System.out.println( a.getKey() + " -> " +j.getValue());
						auxiliarLlenadoFlotantes = Integer.parseInt(j.getValue(),16);
						j.setValue(Float.toString(auxiliarLlenadoFlotantes/1000000));
						Latitud2 = Double.parseDouble(j.getValue());
					}
					if (j.getKey() == "valor" && a.getKey() == "Longitud") {
						System.out.println( a.getKey() + " -> " + j.getValue());
						auxiliarLlenadoFlotantes = Integer.parseInt(j.getValue(),16);
						j.setValue(Float.toString(-auxiliarLlenadoFlotantes/1000000));
						longitude2 = Double.parseDouble(j.getValue());
					}
					if (j.getKey() == "valor" && a.getKey() == "Direccion") {
						System.out.println( a.getKey() + " -> " + j.getValue());
						auxiliarLlenadoEnetros = Integer.parseInt(j.getValue(),16);
						j.setValue(Integer.toString(auxiliarLlenadoEnetros));
						headingDeg = auxiliarLlenadoEnetros ;
					}
					if (j.getKey() == "valor" && a.getKey() == "Altitud") {
						System.out.println( a.getKey() + " -> " + j.getValue());
						auxiliarLlenadoEnetros = Integer.parseInt(j.getValue(),16);
						j.setValue(Integer.toString(auxiliarLlenadoEnetros));
						altitudeM = auxiliarLlenadoEnetros ;
					}
					if (j.getKey() == "valor" && a.getKey() == "Velocidad") {
						System.out.println( a.getKey() + " -> " + j.getValue());
						auxiliarLlenadoEnetros = Integer.parseInt(j.getValue(),16);
						j.setValue(Integer.toString(auxiliarLlenadoEnetros));
						speedKPH2 = auxiliarLlenadoEnetros ; 
					}
					if (j.getKey() == "valor" && a.getKey() == "Bateria") {
						System.out.println( a.getKey() + " -> " + j.getValue());
						auxiliarLlenadoEnetros = Integer.parseInt(j.getValue().substring(4),16);
						j.setValue(Integer.toString(auxiliarLlenadoEnetros));
						batteryLevel2 = auxiliarLlenadoEnetros; 
					}
					if (j.getKey() == "valor" && a.getKey() == "BateriaVoltage") {
						System.out.println( a.getKey() + " -> " + j.getValue());
						auxiliarLlenadoFlotantes = Integer.parseInt(j.getValue().substring(5),16);
						j.setValue(Float.toString(auxiliarLlenadoFlotantes/100));
						batteryVolts2 = Double.parseDouble(j.getValue());
					}
					if (j.getKey() == "valor" && a.getKey() == "intensidad") {
						System.out.println( a.getKey() + " -> " + j.getValue());
						auxiliarLlenadoEnetros = Integer.parseInt(j.getValue().substring(4),16);
						j.setValue(Integer.toString(auxiliarLlenadoEnetros));
						signalStrength2 = auxiliarLlenadoEnetros;
					}
					if (j.getKey() == "valor" && a.getKey() == "NumeroSatelites") {
						System.out.println( a.getKey() + " -> " + j.getValue());
						auxiliarLlenadoEnetros = Integer.parseInt(j.getValue().substring(4),16);
						j.setValue(Integer.toString(auxiliarLlenadoEnetros));
						satelliteCount2 = auxiliarLlenadoEnetros ; 
					}
					if (j.getKey() == "valor" && a.getKey() == "Odometro") {
						System.out.println( a.getKey() + " -> " + j.getValue());
						auxiliarLlenadoEnetros = Integer.parseInt(j.getValue().substring(4),16);
						j.setValue(Integer.toString(auxiliarLlenadoEnetros));
						odomKM2 = auxiliarLlenadoEnetros ; 
					}
					if (j.getKey() == "valor" && a.getKey() == "InformacionCelda") {
						System.out.println( a.getKey() + " -> " + j.getValue());
						mobileCountryCode2 = Integer.parseInt(j.getValue().substring(5,8),16);
						mobileNetworkCode2 = Integer.parseInt(j.getValue().substring(8,10),16);
						cellTowerID2 = Integer.parseInt(j.getValue().substring(10,18),16);
						locationAreaCode2 = Integer.parseInt(j.getValue().substring(18,22),16);
						j.setValue((mobileCountryCode2+"_"+mobileNetworkCode2+"_"+cellTowerID2+"_"+locationAreaCode2));
					}
				}
			}
			
		System.out.println("_____________________");
		System.out.println("Latitud2 ->  "+Latitud2);
		System.out.println("longitude2 ->  " + longitude2 );
		System.out.println("altitudeM -> " + altitudeM);
		System.out.println("speedKPH2 -> "+ speedKPH2);
		System.out.println("headingDeg -> " + headingDeg);
		System.out.println("batteryLevel2 -> "+ batteryLevel2);
		System.out.println("batteryVolts2 -> " + batteryVolts2);
		System.out.println("signalStrength2 -> " + signalStrength2);
		System.out.println("satelliteCount2 -> "  + satelliteCount2);
		System.out.println("odomKM2 -> " + odomKM2);
		System.out.println("InformacionCelda -> " + 1);
		
		System.out.println("\n");
		System.out.println(indicador);
		
		return null; 
	}	
}
