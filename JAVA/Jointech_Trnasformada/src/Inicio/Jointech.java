package Inicio;

public class Jointech {

	
	// ----------------------------------------------------------------------------
	// Copyright TrackLocator Rastreo Satelital
	// All rights reserved
	// ----------------------------------------------------------------------------
	//
	// This source module is PROPRIETARY and CONFIDENTIAL.
	// NOT INTENDED FOR PUBLIC RELEASE.
	//
	// Use of this software is subject to the terms and conditions outlined in
	// the 'Commercial' license provided with this software.  If you did not obtain
	// a copy of the license with this software please request a copy from the
	// Software Provider.
	//
	// Unless required by applicable law or agreed to in writing, software
	// distributed under the License is distributed on an "AS IS" BASIS,
	// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
	// See the License for the specific language governing permissions and
	// limitations under the License.
	//
	// ----------------------------------------------------------------------------
	// Description:
	//  Server Initialization
	// ----------------------------------------------------------------------------
	// This module is to support devices: Jointech GL100, Jointech GL200, Jointech GV100, Jointech GV200, Jointech GV300, Jointech GT500
//	                                    Enduro, SIMCom eLoc, Quectel, Spark Nano, GlobalTrack TRACER T-1000 / GTX11
	// ----------------------------------------------------------------------------
	package org.opengts.servers.jointech;
	import java.lang.*;
	import java.util.*;
	import java.io.*;
	import java.net.*;
	import java.sql.*;

	import org.opengts.util.*;
	import org.opengts.dbtools.*;
	import org.opengts.dbtypes.*;
	import org.opengts.db.*;
	import org.opengts.db.tables.*;
	import java.math.BigInteger;
	import java.lang.Math.*;

	import org.opengts.cellid.*;
	import org.opengts.cellid.opencellid.*;
	    public class TrackClientPacketHandler
	    extends AbstractClientPacketHandler
	    {
	    public static       boolean DEBUG_MODE                  = false;
	    // ------------------------------------------------------------------------
	    public static       String  UNIQUEID_PREFIX[]           = null;
	    public static       double  MINIMUM_SPEED_KPH           = Constants.MINIMUM_SPEED_KPH;
	    public static       boolean ESTIMATE_ODOMETER           = true;
	    public static       boolean SIMEVENT_GEOZONES           = true;
	    public static       long    SIMEVENT_DIGITAL_INPUTS     = 0xFFL;
	    public static       boolean XLATE_LOCATON_INMOTION      = true;
	    public static       double  MINIMUM_MOVED_METERS        = 0.0;
	    public static       boolean PACKET_LEN_END_OF_STREAM    = false;
	    public static       boolean BUFFER_REPORT_ENABLED       = true;
	    // ------------------------------------------------------------------------
		
	    /* convenience for converting knots to kilometers */
	    public static final double  KILOMETERS_PER_KNOT         = 1.85200000;
	    // ------------------------------------------------------------------------
		
	    /* GTS status codes for Input-On events */
	    private static final int InputStatusCodes_ON[] = new int[] {
	        StatusCodes.STATUS_INPUT_ON_00,
	        StatusCodes.STATUS_INPUT_ON_01,
	        StatusCodes.STATUS_INPUT_ON_02,
	        StatusCodes.STATUS_INPUT_ON_03,
	        StatusCodes.STATUS_INPUT_ON_04,
	        StatusCodes.STATUS_INPUT_ON_05,
	        StatusCodes.STATUS_INPUT_ON_06,
	        StatusCodes.STATUS_INPUT_ON_07,
	        StatusCodes.STATUS_INPUT_ON_08,
	        StatusCodes.STATUS_INPUT_ON_09,
	        StatusCodes.STATUS_INPUT_ON_10,
	        StatusCodes.STATUS_INPUT_ON_11,
	        StatusCodes.STATUS_INPUT_ON_12,
	        StatusCodes.STATUS_INPUT_ON_13,
	        StatusCodes.STATUS_INPUT_ON_14,
	        StatusCodes.STATUS_INPUT_ON_15
	    };
		
	    /* GTS status codes for Input-Off events */
	    private static final int InputStatusCodes_OFF[] = new int[] {
	        StatusCodes.STATUS_INPUT_OFF_00,
	        StatusCodes.STATUS_INPUT_OFF_01,
	        StatusCodes.STATUS_INPUT_OFF_02,
	        StatusCodes.STATUS_INPUT_OFF_03,
	        StatusCodes.STATUS_INPUT_OFF_04,
	        StatusCodes.STATUS_INPUT_OFF_05,
	        StatusCodes.STATUS_INPUT_OFF_06,
	        StatusCodes.STATUS_INPUT_OFF_07,
	        StatusCodes.STATUS_INPUT_OFF_08,
	        StatusCodes.STATUS_INPUT_OFF_09,
	        StatusCodes.STATUS_INPUT_OFF_10,
	        StatusCodes.STATUS_INPUT_OFF_11,
	        StatusCodes.STATUS_INPUT_OFF_12,
	        StatusCodes.STATUS_INPUT_OFF_13,
	        StatusCodes.STATUS_INPUT_OFF_14,
	        StatusCodes.STATUS_INPUT_OFF_15
	    };
	    // ------------------------------------------------------------------------
		
	    /* GMT/UTC timezone */
	    private static final TimeZone gmtTimezone               = DateTime.getGMTTimeZone();
	    // ------------------------------------------------------------------------
		
	    /* packet handler constructor */
	    public TrackClientPacketHandler()
	    {
	        super(Constants.DEVICE_CODE);
	    }
	    // ------------------------------------------------------------------------
		
	    /* callback when session is starting */
	    public void sessionStarted(InetAddress inetAddr, boolean isTCP, boolean isText)
	    {
	        super.sessionStarted(inetAddr, isTCP, isText);
	        super.clearTerminateSession();
	    }
	    /* callback when session is terminating */
	    public void sessionTerminated(Throwable err, long readCount, long writeCount)
	    {
	        super.sessionTerminated(err, readCount, writeCount);
	    }
	    // ------------------------------------------------------------------------
		
	    /* based on the supplied packet data, return the remaining bytes to read in the packet */
	    public int getActualPacketLength(byte packet[], int packetLen)
	    {
	        if (PACKET_LEN_END_OF_STREAM) {
	            return ServerSocketThread.PACKET_LEN_END_OF_STREAM;
	        } else {
	            return ServerSocketThread.PACKET_LEN_LINE_TERMINATOR;
	        }
	    }
	    // ------------------------------------------------------------------------
	    /* workhorse of the packet handler */	
	    public byte[] getHandlePacket(byte pktBytes[]){
				String hex = "";
				/*String valor = "7E0200003E7940110008670357000000000204008A0048882A046BEFBE09EC00000000220127193204D40134D502017F300118310106F9020006FE040000004EFD0902DC0A000AB2655528277E";
						byte[] b = valor.getBytes();
						System.out.println(b);
						String s = new String(b);
						System.out.println(s);
				*/
				
				//trama automatica
				hex = StringTools.toHexString(pktBytes);	
				String s = StringTools.toStringValue(hex.toCharArray()).trim();

				Print.logInfo("jointech[Bytes]: " + pktBytes);
				Print.logInfo("jointech[HEX]: " + hex);
				Print.logInfo("jointech[STRING]: " + s); 
				Print.logInfo("length: " + s.length()); 
				Print.logInfo("startsWith: " + s.startsWith("7E")); 

				if(s.length() == 154 && s.startsWith("7E")) {
					return this.parseInsertRecord_JOINTECH(s);
				}
					return null;
	    }

	  // -------------FUNCIONES PARA CONVERTIR VARIABLES-----------------------------------------------------
		//	fieldsParser.setTemp  (Double.valueOf(hex2decimal_Int(fld[idxTempHumedy].substring(0, 2))));
		private int hex2decimal_Int(String hexadecimal) {
			BigInteger decimal = new BigInteger(hexadecimal, 16);
			return decimal.intValue();
		}
		
	  private String hex2bin_Str(String hexadecimal){
			BigInteger decimal = new BigInteger(hexadecimal, 16);
			int numHex = decimal.intValue();
			String binary = Integer.toBinaryString(numHex);
			decimal = null;
			return binary;
		}
	    // ------------------------------------------------------------------------
		
		private LinkedHashMap<String, String> generateHashMapValues(String caracteres, String valor) {
			LinkedHashMap<String, String> valores = new LinkedHashMap<String, String>();
			valores.put("caracteres", caracteres);
			valores.put("valor", valor);
			return valores;
		}

	    /* Jointech: parse and insert data record */
		private byte[] parseInsertRecord_JOINTECH(String s){	
			if (s == null) {
					Print.logError("String is null \n");
					return null;
			}
			
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
			//System.out.println(indicador);

			//Fill the hasmap with the converted data
			/*
				Encabezado							=		{caracteres=2,valor=7E}
				Idmensaje								=		{caracteres=4,valor=0200}
				Atributos								=		{caracteres=4,valor=62}
				NumeroIdentificacion		=		{caracteres=12,valor=794011000867}
				MensajeSerie						=		{caracteres=4,valor=855}
				Alarma									=		{caracteres=8,valor=00000000}
				Dispositivo							=		{caracteres=8,valor=0204008A}
				Latitud									=		{caracteres=8,valor=-4.75345}
				Longitud								=		{caracteres=8,valor=74.182594}
				Altitud									=		{caracteres=4,valor=2540}
				Velocidad								=		{caracteres=4,valor=0000}
				Direccion								=		{caracteres=4,valor=0000}
				Fecha										=		{caracteres=6,valor=220127}
				Tiempo									=		{caracteres=6,valor=193204}
				Bateria									=		{caracteres=6,valor=52}
				BateriaVoltage					=		{caracteres=8,valor=3.83}
				intensidad							=		{caracteres=6,valor=24}
				NumeroSatelites					=		{caracteres=6,valor=6}
				VersionProtocolo				=		{caracteres=8,valor=F9020006}
				KilometrosRecorrido			=		{caracteres=12,valor=FE040000004E}
				Pais										=		{caracteres=22,valor=FD0902DC0A000AB2655528}
				empty										=		{caracteres=2,valor=27}
				FinTrama								=		{caracteres=2,valor=7E}}
			*/
			
						/* Parsing */ 
			double  	Latitud 	  					= 0.0;
			double  	longitude							= 0.0;
			double  	altitudeM							= 0.0; 
			double 		headingDeg						= 0.0; 
			double  	speedKPH2    					= 0.0;
			double  	odomKM2      					= 0.0;
			int     	numSats        				= 0;	
			double  	batteryLevel					= 0.0;
			double  	batteryVolts 					= 0.0;
			double  	signalStrength				= 0.0;
			int  			satelliteCount				= 0;
			int				mobileCountryCode2 		= 0;
			int 			mobileNetworkCode2 		= 0;
			int  			cellTowerID2   				= 0;
			int 			locationAreaCode2			= 0;
			int 			gpsFixStatus    			= 0 ; 	
						
			int     	statusCode 						= StatusCodes.STATUS_LOCATION;

			String 	numHex 					= "" ;

			System.out.println(" ------ CONVERTIR TRAMA Y GRUARDAR DE NUEVO EN INDICADOR LINKEDHASMAP  -------------------------");
			
			String Fecha = "";
			String Tiempo = "";
			String mobileID 	= "" ;
			long    fixtime   = 0 ;
			fixtime = DateTime.getCurrentTimeSec();

			for(Map.Entry<String, LinkedHashMap<String, String>> a: indicador.entrySet()) {
				for(Map.Entry<String, String> j:  a.getValue().entrySet()) {
					if (j.getKey() == "valor" && a.getKey() == "Atributos") {
						//System.out.println( a.getKey() + " -> " +j.getValue());
						auxiliarLlenadoEnetros = Integer.parseInt(j.getValue(),16);
						j.setValue(Integer.toString(auxiliarLlenadoEnetros));
					}
					if (j.getKey() == "valor" && a.getKey() == "NumeroIdentificacion") {
						//System.out.println( a.getKey() + " -> " +j.getValue());
						mobileID = j.getValue();
					}
					if (j.getKey() == "valor" && a.getKey() == "MensajeSerie") {
						//System.out.println( a.getKey() + " -> " + j.getValue());
						auxiliarLlenadoEnetros = Integer.parseInt(j.getValue(),16);
						j.setValue(Integer.toString(auxiliarLlenadoEnetros));
					}
					if (j.getKey() == "valor" && a.getKey() == "Estado") {
						//System.out.println( a.getKey() + " -> " + j.getValue());
						numHex = j.getValue().substring(7,8);
						j.setValue(numHex);
					}
					if (j.getKey() == "valor" && a.getKey() == "Latitud") {
						//System.out.println( a.getKey() + " -> " +j.getValue());
						auxiliarLlenadoFlotantes = Integer.parseInt(j.getValue(),16);
						j.setValue(Float.toString(auxiliarLlenadoFlotantes/1000000));
						Latitud = Double.parseDouble(j.getValue());
					}
					if (j.getKey() == "valor" && a.getKey() == "Longitud") {
						//System.out.println( a.getKey() + " -> " + j.getValue());
						auxiliarLlenadoFlotantes = Integer.parseInt(j.getValue(),16);
						j.setValue(Float.toString(-auxiliarLlenadoFlotantes/1000000));
						longitude = Double.parseDouble(j.getValue());
					}
					if (j.getKey() == "valor" && a.getKey() == "Direccion") {
						//System.out.println( a.getKey() + " -> " + j.getValue());
						auxiliarLlenadoEnetros = Integer.parseInt(j.getValue(),16);
						j.setValue(Integer.toString(auxiliarLlenadoEnetros));
						headingDeg = auxiliarLlenadoEnetros ;
					}
					if (j.getKey() == "valor" && a.getKey() == "Fecha") {
						System.out.println( a.getKey() + " -> " + j.getValue());
						Fecha = j.getValue() ;
					}
					if (j.getKey() == "valor" && a.getKey() == "Tiempo") {
						System.out.println( a.getKey() + " -> " + j.getValue());
						Tiempo = j.getValue(); 
					}
					if (j.getKey() == "valor" && a.getKey() == "Altitud") {
						//System.out.println( a.getKey() + " -> " + j.getValue());
						auxiliarLlenadoEnetros = Integer.parseInt(j.getValue(),16);
						j.setValue(Integer.toString(auxiliarLlenadoEnetros));
						altitudeM = auxiliarLlenadoEnetros ;
					}
					if (j.getKey() == "valor" && a.getKey() == "Velocidad") {
						//System.out.println( a.getKey() + " -> " + j.getValue());
						auxiliarLlenadoEnetros = Integer.parseInt(j.getValue(),16);
						j.setValue(Integer.toString(auxiliarLlenadoEnetros));
						speedKPH2 = auxiliarLlenadoEnetros * 0.1 ; 
					}
					if (j.getKey() == "valor" && a.getKey() == "Bateria") {
						//System.out.println( a.getKey() + " -> " + j.getValue());
						auxiliarLlenadoEnetros = Integer.parseInt(j.getValue().substring(4),16);
						j.setValue(Integer.toString(auxiliarLlenadoEnetros));
						batteryLevel = auxiliarLlenadoEnetros; 
					}
					if (j.getKey() == "valor" && a.getKey() == "BateriaVoltage") {
						//System.out.println( a.getKey() + " -> " + j.getValue());
						auxiliarLlenadoFlotantes = Integer.parseInt(j.getValue().substring(5),16);
						j.setValue(Float.toString(auxiliarLlenadoFlotantes/100));
						batteryVolts = Double.parseDouble(j.getValue());
					}
					if (j.getKey() == "valor" && a.getKey() == "intensidad") {
						//System.out.println( a.getKey() + " -> " + j.getValue());
						auxiliarLlenadoFlotantes = Integer.parseInt(j.getValue().substring(4),16);
						j.setValue(Float.toString(auxiliarLlenadoFlotantes));
						signalStrength = Double.parseDouble(j.getValue());
					}
					if (j.getKey() == "valor" && a.getKey() == "NumeroSatelites") {
						//System.out.println( a.getKey() + " -> " + j.getValue());
						auxiliarLlenadoEnetros = Integer.parseInt(j.getValue().substring(4),16);
						j.setValue(Integer.toString(auxiliarLlenadoEnetros));
						satelliteCount = auxiliarLlenadoEnetros ; 
					}
					if (j.getKey() == "valor" && a.getKey() == "Odometro") {
						//System.out.println( a.getKey() + " -> " + j.getValue());
						auxiliarLlenadoEnetros = Integer.parseInt(j.getValue().substring(4),16);
						j.setValue(Integer.toString(auxiliarLlenadoEnetros));
						odomKM2 = auxiliarLlenadoEnetros ; 
					}
					if (j.getKey() == "valor" && a.getKey() == "InformacionCelda") {
						//System.out.println( a.getKey() + " -> " + j.getValue());
						mobileCountryCode2 = Integer.parseInt(j.getValue().substring(5,8),16);
						mobileNetworkCode2 = Integer.parseInt(j.getValue().substring(8,10),16);
						cellTowerID2 = Integer.parseInt(j.getValue().substring(10,18),16);
						locationAreaCode2 = Integer.parseInt(j.getValue().substring(18,22),16);
						j.setValue((mobileCountryCode2+"_"+mobileNetworkCode2+"_"+cellTowerID2+"_"+locationAreaCode2));
					}
				}
			}

			String rawData = s;
			System.out.println("rawdata -> "+ rawData);
			System.out.println("statusCode - > "+ statusCode);
			

			System.out.println("fixtime -> " + fixtime);
			System.out.println("VARIABLES ----------------------------- ");

			int numHex2 = Integer.parseInt(numHex, 16);
			String binary = Integer.toBinaryString(numHex2);
			
			String[] strarray = binary.split("");
			
			int Auxgpslongitude= Integer.parseInt(strarray[0]);
			int AuxgpsLatitud = Integer.parseInt(strarray[1]);
			int AuxgpsFixstatus= Integer.parseInt(strarray[2]);
			
			
			if (Auxgpslongitude == 1) {
				longitude = -longitude ; 
			}if (AuxgpsLatitud == 1) {
				Latitud = -Latitud ; 
			}

			String[] TimeArray = Tiempo.split("");
				String[] DateArray = Fecha.split("");
				String AuxFecha = "20" ; 
				//FECHA+HORA = 20220211113742  -> converitr 2022-02-11 11:37:42
				
				int contador = 0;
				int sumatoria = 1;
				
				for (int i = 0; i < 6; i++) {
					AuxFecha =  AuxFecha + DateArray [i];
								if (contador == sumatoria) {
									if( i == 5 ) {
										AuxFecha = AuxFecha + " ";	
									}else {
										AuxFecha = AuxFecha + "-";
									}
									sumatoria = sumatoria +2;
								}
								contador ++ ;  
						}
				
				for (int i = 0; i < 6; i++) {
					AuxFecha =  AuxFecha + TimeArray[i];
								if (contador == sumatoria) {
									if( i == 5 ) {
										AuxFecha = AuxFecha + "";
									}else {
										AuxFecha = AuxFecha + ":";
									}
									sumatoria = sumatoria +2;
								}
								contador ++ ;
						}
				
				//Convertir fecha en timesatp para guardar en campo creationTime
			
			java.sql.Timestamp ts1 =  java.sql.Timestamp.valueOf(AuxFecha);  
			System.out.println("Timestamp : "+ts1);  
			Long val=ts1.getTime();  
			String StringcreationTime = Long.toString(val);
			System.out.println("creationTime : "+ StringcreationTime.substring(0,10));
			int creationTime = Integer.parseInt(StringcreationTime.substring(0,10));
			System.out.println("\n");

			gpsFixStatus = AuxgpsFixstatus;

			System.out.println("_____________________");
			System.out.println("Latitud ->  "+Latitud);
			System.out.println("longitude ->  " + longitude );
			System.out.println("altitudeM -> " + altitudeM);
			System.out.println("speedKPH2 -> "+ speedKPH2);
			System.out.println("headingDeg -> " + headingDeg);
			System.out.println("batteryLevel -> "+ batteryLevel);
			System.out.println("batteryVolts -> " + batteryVolts);
			System.out.println("signalStrength -> " + signalStrength);
			System.out.println("satelliteCount -> "  + satelliteCount);
			System.out.println("odomKM2 -> " + odomKM2 +"\n");
			
			System.out.println("mobileCountryCode2 -> " + mobileCountryCode2);
			System.out.println("mobileNetworkCode2 -> " + mobileNetworkCode2);
			System.out.println("cellTowerID2 -> " + cellTowerID2);
			System.out.println("locationAreaCode2 -> " + locationAreaCode2);
			System.out.println("gpsFixStatus -> " + gpsFixStatus);
			System.out.println("creationTime -> " + creationTime );

			System.out.println("\n");
			//System.out.println(indicador);
			/*
			if ((latitude== 0.0) && (longitude== 0.0) ){
				statusCode = StatusCodes.STATUS_HEARTBEAT;	
				//Print.logError("ubicaion no existe");	
			}
			*/

			System.out.println("mobileID -> " + mobileID);

			Device device = DCServerConfig.loadDeviceUniqueID(Main.getServerConfig(), mobileID);
			if (device == null) {
				Print.logError("'DEVICE' IS NULL \n");
				return null; 
			}

			String 		accountID 	= device.getAccountID();
			String 		deviceID  	= device.getDeviceID();
			String 		uniqueID  	= device.getUniqueID();
			GeoPoint 	geoPoint 		= device.getLastValidLocation();

			System.out.println("accountID = "	+	accountID);
			System.out.println("deviceID =  "	+	deviceID);
			System.out.println("uniqueID =  "	+	uniqueID);
			System.out.println("geoPoint =  "	+	geoPoint);
			System.out.println(" ********  ENVIAMOS LOS DATOS CAPTURADOS ************************");

			this.insertEventRecord(device, fixtime, statusCode , geoPoint , speedKPH2 ,  Latitud , longitude ,  altitudeM ,
											odomKM2 ,batteryLevel ,  batteryVolts ,satelliteCount ,signalStrength , mobileCountryCode2 ,mobileNetworkCode2 , cellTowerID2, locationAreaCode2 , 
											gpsFixStatus,rawData);

			return null; 
		}
			// ------------------------------------------------------------------------
	    // ------------------------------------------------------------------------
	    // ------------------------------------------------------------------------

	    private EventData createEventRecord(Device device, long gpsTime,  int statusCode, 
																			GeoPoint geoPoint, double speedKPH2 ,double Latitud , 
																			double longitude , double altitudeM,	double odomKM2, 
																			double batteryLevel, double batteryVolts, int satelliteCount, 
																			double signalStrength , int mobileCountryCode2 , int mobileNetworkCode2 , 
																			int cellTowerID2, int locationAreaCode2, int gpsFixStatus,String rawData){

				String accountID    = device.getAccountID();
				String deviceID     = device.getDeviceID();
				EventData.Key evKey = new EventData.Key(accountID, deviceID, gpsTime, statusCode);
				EventData evdb      = evKey.getDBRecord();
				evdb.setGeoPoint(geoPoint);
				evdb.setSpeedKPH(speedKPH2);
				evdb.setLatitude(Latitud);
				evdb.setLongitude(longitude);
				evdb.setAltitude(altitudeM);
				evdb.setOdometerKM(odomKM2);
				evdb.setBatteryLevel(batteryLevel);
				evdb.setBatteryVolts(batteryVolts);
				evdb.setSatelliteCount(satelliteCount);
				evdb.setSignalStrength(signalStrength);
				evdb.setMobileCountryCode(mobileCountryCode2);
				evdb.setMobileNetworkCode(mobileNetworkCode2);
				evdb.setCellTowerID(cellTowerID2);
				evdb.setLocationAreaCode(locationAreaCode2);
				evdb.setGpsFixStatus(gpsFixStatus);
				evdb.setRawData(rawData);

				System.out.println(" salimos FUNCION createEventRecord ------------------------------------------------------------ \n");

				System.out.println(batteryLevel);
				System.out.println(batteryVolts);
				System.out.println(signalStrength);
				return evdb;
	    }

			// method insertEventRecord(Device,long,int,<null>,GeoPoint,int,double,int,double,double,double,double,double,double,String)

	    private void insertEventRecord(Device device, long gpsTime,  int statusCode, 
																			GeoPoint geoPoint, double speedKPH2 ,double Latitud , 
																			double longitude , double altitudeM,	double odomKM2, 
																			double batteryLevel, double batteryVolts,  int satelliteCount,  double signalStrength ,
																			int mobileCountryCode2, int mobileNetworkCode2 , int cellTowerID2, int locationAreaCode2,
																			int gpsFixStatus , String rawData){
				
				System.out.println("legamos FUNCION insertEventRecord ------------------------------------------------------------");
				System.out.println(" gpsTime  -> " + gpsTime + " statusCode  -> " + statusCode + " geoPoint  -> " + geoPoint 
													+ " speedKPH2  -> " + speedKPH2 + " Latitud2  -> " + Latitud + " longitude  -> " + longitude 
													+ " altitudeM  -> " + altitudeM  + " odomKM  -> " + odomKM2 + " batteryLevel  -> " + batteryLevel 
													+ " batteryV  -> " + batteryVolts + "satelliteCount -> " + satelliteCount + " signalStrength " + signalStrength
													+" rawData  -> " + rawData + " mobileCountryCode2  -> " + mobileCountryCode2 + " mobileNetworkCode2 ->"+ mobileNetworkCode2 
													+" cellTowerID2 -> " + cellTowerID2 + " locationAreaCode2 ->" + locationAreaCode2 + " gpsFixStatus -> " + gpsFixStatus);

				EventData evdb = createEventRecord(device, gpsTime, statusCode , geoPoint  , speedKPH2 , Latitud , longitude,
				altitudeM , odomKM2 , batteryLevel ,batteryVolts ,satelliteCount ,signalStrength , mobileCountryCode2 ,mobileNetworkCode2 ,
				cellTowerID2, locationAreaCode2 , gpsFixStatus,rawData) ;

				// this will display an error if it was unable to store the event
				Print.logInfo("Event: [0x" + StringTools.toHexString(statusCode,16) + "] " + StatusCodes.GetDescription(statusCode,null));
				Print.logInfo(" EVE -> " +evdb); 
				device.insertEventData(evdb);
				this.incrementSavedEventCount();
				
	    }

	    // ------------------------------------------------------------------------
	    // ------------------------------------------------------------------------
	    // ------------------------------------------------------------------------

	    public static void configInit(){
				DCServerConfig dcsc = Main.getServerConfig();
				if (dcsc != null) {
						UNIQUEID_PREFIX          = dcsc.getUniquePrefix();
						MINIMUM_SPEED_KPH        = dcsc.getMinimumSpeedKPH(MINIMUM_SPEED_KPH);
						ESTIMATE_ODOMETER        = dcsc.getEstimateOdometer(ESTIMATE_ODOMETER);
						SIMEVENT_GEOZONES        = dcsc.getSimulateGeozones(SIMEVENT_GEOZONES);
						SIMEVENT_DIGITAL_INPUTS  = dcsc.getSimulateDigitalInputs(SIMEVENT_DIGITAL_INPUTS) & 0xFFL;
						XLATE_LOCATON_INMOTION   = dcsc.getStatusLocationInMotion(XLATE_LOCATON_INMOTION);
						MINIMUM_MOVED_METERS     = dcsc.getMinimumMovedMeters(MINIMUM_MOVED_METERS);
						PACKET_LEN_END_OF_STREAM = dcsc.getBooleanProperty(Constants.CFG_packetLenEndOfStream, PACKET_LEN_END_OF_STREAM);
				}
	        
	    }
	//Print.logError(" trackClientPackethandler.java (insertEventRecord)  Mario "+fld[idxFixtime]);	
	}
	
		
}
