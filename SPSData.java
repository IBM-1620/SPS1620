package SPS1620;

/*
 *  SPSData.java - global constants, data, and tables
 *
 *  IBM 1620 Jr Project, Computer History Museum, 2017-2018
 *
 *  To recreate the experience (visual, auditory, tactile, visceral) of running historic software on a 1960s-era computer.
 *
 *   Dave Babcock     - project lead, software, library
 *   John M. Bohn Jr. - software
 *   David Brock      - CHM sponsor
 *   Steve Casner     - hardware, software
 *   Joe Fredrick     - hardware, firmware
 *   Len Shustek      - CHM advisor
 *   Dag Spicer       - CHM advisor
 *   David Wise       - IBM 1620 expert
 */

import java.io.*;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.regex.Pattern;

public class SPSData {

	// Constants
	
	public static final String VERSION = "1.00";
	
	public static final int SIZE_OP_TABLE     = 400;
	public static final int SIZE_SOURCE_TABLE = 16;
	public static final int SIZE_LINE         = 80;
	public static final int SIZE_EXPR_STACK   = 80;
	public static final int SIZE_TAB_TABLE    = 80;
	public static final int SIZE_XREF_LINE    = 8;
	
	public static final byte DEFAULT_TABS = 8;
	
	public static final byte MASK_FLAG        = 0x10;
	public static final byte MASK_DIGIT       = 0x0f;
	public static final byte MASK_FLAG_DIGIT  = 0x1f;
	public static final byte MASK_CHARACTER   = 0x7f;
	public static final int  MASK_LINE_NUMBER = 0x3ffff;
	
	public static final byte MEMORY_RM       = (byte)0x0a;
	public static final byte MEMORY_RGM      = (byte)0x0b;
	public static final byte MEMORY_NB       = (byte)0x0c;
	public static final byte MEMORY_GM       = (byte)0x0f;
	public static final byte MEMORY_FLAG_RM  = (byte)0x1a;
	public static final byte MEMORY_FLAG_RGM = (byte)0x1b;
	public static final byte MEMORY_FLAG_NB  = (byte)0x1c;
	public static final byte MEMORY_FLAG_GM  = (byte)0x1f;
	public static final byte MEMORY_UNDEF    = (byte)0xe0;
	
	public static final int FIELD_OP      = 1;
	public static final int FIELD_P       = 6;
	public static final int FIELD_Q       = 11;
	public static final int FIELD_DRIVE   = 0;
	public static final int FIELD_SECTOR  = 5;
	public static final int FIELD_COUNT   = 8;
	public static final int FIELD_ADDRESS = 13;
	
	public static final byte CHAR_EOL = (byte)0x80;
	
	public static final SimpleDateFormat FORMAT_DATE = new SimpleDateFormat("M/dd/yyyy @ HH:mm");
	
	public static final String VALID_HEAD   = " ABCDEFGHIJKLMNOPQRSTUVWXY0123456789";
	
	public static final Pattern PATTERN_FREEFORM = Pattern.compile("^(\\S*)\\s+(\\S+)(\\s(.*))?$");
	public static final Pattern PATTERN_SPLIT    = Pattern.compile(",");
	public static final Pattern PATTERN_LINE     = Pattern.compile("^[ .)+$*\\-/,(=@A-Z0-9]*$");
	public static final Pattern PATTERN_LABEL    = Pattern.compile("^[A-Z0-9=/@.]{1,6}$");
	public static final Pattern PATTERN_LABELX   = Pattern.compile("^[A-Z0-9=@.]{1,6}$");
	public static final Pattern PATTERN_SYMBOL   = Pattern.compile("^[A-Z0-9=/@.]{1,6}$");
	public static final Pattern PATTERN_SYMBOLX  = Pattern.compile("^[A-Z0-9=@.]{1,6}$");
	public static final Pattern PATTERN_SYMBOLH  = Pattern.compile("^[A-Z0-9]?\\$[A-Z0-9=/@.]{1,5}$");
	public static final Pattern PATTERN_SYMBOLHX = Pattern.compile("^[A-Z0-9]?\\$[A-Z0-9=@.]{1,5}$");
	public static final Pattern PATTERN_NUMBER   = Pattern.compile("^[0-9]+$");
	public static final Pattern PATTERN_NUMBERX  = Pattern.compile("^[0-9I-R]+$");
	public static final Pattern PATTERN_ALPHA    = Pattern.compile("^[A-Z0-9 .)+$*\\-/,(=@]*$");
	public static final Pattern PATTERN_INDEX    = Pattern.compile("^\\([AB]?[0-7]\\)$");
	public static final Pattern PATTERN_FLAGS    = Pattern.compile("^0?1?2?3?4?5?6?7?8?9?(10)?(11)?$");
	
	// Enumerations
	
	public static enum SourceFormat {FIXED, FREEFORM};
	
	public static enum SystemType {ANY, MODEL_1, MODEL_2};
	
	public static enum DendStatus {NOT_SEEN, SEEN, WARNING};
	
	// Constant Arrays
	
	public static final byte[] multiplyTable =
		{0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x01, 0x00, 0x02, 0x00, 0x03, 0x00, 0x04, 0x00,
		 0x00, 0x00, 0x02, 0x00, 0x04, 0x00, 0x06, 0x00, 0x08, 0x00, 0x00, 0x00, 0x03, 0x00, 0x06, 0x00, 0x09, 0x00, 0x02, 0x01,
		 0x00, 0x00, 0x04, 0x00, 0x08, 0x00, 0x02, 0x01, 0x06, 0x01, 0x00, 0x00, 0x05, 0x00, 0x00, 0x01, 0x05, 0x01, 0x00, 0x02,
		 0x00, 0x00, 0x06, 0x00, 0x02, 0x01, 0x08, 0x01, 0x04, 0x02, 0x00, 0x00, 0x07, 0x00, 0x04, 0x01, 0x01, 0x02, 0x08, 0x02,
		 0x00, 0x00, 0x08, 0x00, 0x06, 0x01, 0x04, 0x02, 0x02, 0x03, 0x00, 0x00, 0x09, 0x00, 0x08, 0x01, 0x07, 0x02, 0x06, 0x03,
		 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x05, 0x00, 0x06, 0x00, 0x07, 0x00, 0x08, 0x00, 0x09, 0x00,
		 0x00, 0x01, 0x02, 0x01, 0x04, 0x01, 0x06, 0x01, 0x08, 0x01, 0x05, 0x01, 0x08, 0x01, 0x01, 0x02, 0x04, 0x02, 0x07, 0x02,
		 0x00, 0x02, 0x04, 0x02, 0x08, 0x02, 0x02, 0x03, 0x06, 0x03, 0x05, 0x02, 0x00, 0x03, 0x05, 0x03, 0x00, 0x04, 0x05, 0x04,
		 0x00, 0x03, 0x06, 0x03, 0x02, 0x04, 0x08, 0x04, 0x04, 0x05, 0x05, 0x03, 0x02, 0x04, 0x09, 0x04, 0x06, 0x05, 0x03, 0x06,
		 0x00, 0x04, 0x08, 0x04, 0x06, 0x05, 0x04, 0x06, 0x02, 0x07, 0x05, 0x04, 0x04, 0x05, 0x03, 0x06, 0x02, 0x07, 0x01, 0x08};
	public static final byte[] addTable =
		{0x00, 0x01, 0x02, 0x03, 0x04, 0x05, 0x06, 0x07, 0x08, 0x09, 0x01, 0x02, 0x03, 0x04, 0x05, 0x06, 0x07, 0x08, 0x09, 0x10,
		 0x02, 0x03, 0x04, 0x05, 0x06, 0x07, 0x08, 0x09, 0x10, 0x11, 0x03, 0x04, 0x05, 0x06, 0x07, 0x08, 0x09, 0x10, 0x11, 0x12,
		 0x04, 0x05, 0x06, 0x07, 0x08, 0x09, 0x10, 0x11, 0x12, 0x13, 0x05, 0x06, 0x07, 0x08, 0x09, 0x10, 0x11, 0x12, 0x13, 0x14,
		 0x06, 0x07, 0x08, 0x09, 0x10, 0x11, 0x12, 0x13, 0x14, 0x15, 0x07, 0x08, 0x09, 0x10, 0x11, 0x12, 0x13, 0x14, 0x15, 0x16,
		 0x08, 0x09, 0x10, 0x11, 0x12, 0x13, 0x14, 0x15, 0x16, 0x17, 0x09, 0x10, 0x11, 0x12, 0x13, 0x14, 0x15, 0x16, 0x17, 0x18};

	public static final int[] dotLengths = {3, 5, 7, 10, 14, 19, 25, 32, 40, 49, 59, 69, 80, 92};
	public static final byte[] dotTable =
		{0x15, 0x04, 0x09, 0x07, 0x05, 0x05, 0x08, 0x01, 0x03, 0x08, 0x08, 0x08,
		 0x16, 0x08, 0x07, 0x01, 0x09, 0x04, 0x07, 0x06, 0x07, 0x03, 0x06,
		 0x18, 0x05, 0x08, 0x09, 0x09, 0x03, 0x04, 0x05, 0x09, 0x02,
		 0x11, 0x00, 0x07, 0x03, 0x07, 0x04, 0x01, 0x08, 0x02, 0x04,
		 0x11, 0x03, 0x04, 0x02, 0x01, 0x07, 0x07, 0x02, 0x08,
		 0x11, 0x06, 0x07, 0x07, 0x07, 0x02, 0x01, 0x06,
		 0x12, 0x00, 0x09, 0x07, 0x01, 0x05, 0x02,
		 0x12, 0x06, 0x02, 0x01, 0x04, 0x04,
		 0x13, 0x02, 0x07, 0x06, 0x08,
		 0x14, 0x00, 0x09, 0x06,
		 0x15, 0x01, 0x02,
		 0x16, 0x04,
		 0x10, 0x08,
		 0x10, 0x01,
		 0x0a};
	
	public static final int[] skipQField = {971, 972, 973, 974, 975, 976, 977, 978, 979, 970, 933, 934};
	public static final int[] skapQField = {941, 942, 943, 944, 945, 946, 947, 948, 949, 940, 903, 904};
	public static final int[] spimQField = {951, 952, 953};
	public static final int[] spapQField = {921, 962, 963};
	
	public static final char[] flagChar =
		{' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ',
		 '_', '_', '_', '_', '_', '_', '_', '_', '_', '_', '_', '_', '_', '_', '_', '_'};
	
	public static final char[] listChar =
		{'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', '@', '?', '~', '?', '?', '#',
		 '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', '@', '?', '~', '?', '?', '#'};
	
	public static final char[] crdChar =
		{'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', '|', ' ', ')', ' ', ' ', '}',
		 ']', 'J', 'K', 'L', 'M', 'N', 'O', 'P', 'Q', 'R', '!', ' ', '*', ' ', ' ', '"'};
	
	public static final int[] alphaChar =
		{0x0000, 0x0000, 0x0000, 0x0000, 0x0000, 0x0000, 0x0000, 0x0000, 0x0000, 0x0000, 0x0000, 0x0000, 0x0000, 0x0000, 0x0000, 0x0000,
		 0x0000, 0x0000, 0x0000, 0x0000, 0x0000, 0x0000, 0x0000, 0x0000, 0x0000, 0x0000, 0x0000, 0x0000, 0x0000, 0x0000, 0x0000, 0x0000,
		 0x0000, 0x0000, 0x0000, 0x0000, 0x0103, 0x0000, 0x0000, 0x0000, 0x0204, 0x0004, 0x0104, 0x0100, 0x0203, 0x0200, 0x0003, 0x0201,
		 0x0700, 0x0701, 0x0702, 0x0703, 0x0704, 0x0705, 0x0706, 0x0707, 0x0708, 0x0709, 0x0000, 0x0000, 0x0000, 0x0303, 0x0000, 0x0000,
		 0x0304, 0x0401, 0x0402, 0x0403, 0x0404, 0x0405, 0x0406, 0x0407, 0x0408, 0x0409, 0x0501, 0x0502, 0x0503, 0x0504, 0x0505, 0x0506,
		 0x0507, 0x0508, 0x0509, 0x0602, 0x0603, 0x0604, 0x0605, 0x0606, 0x0607, 0x0608, 0x0609, 0x0000, 0x0000, 0x0000, 0x0000, 0x0000,
		 0x0000, 0x0000, 0x0000, 0x0000, 0x0000, 0x0000, 0x0000, 0x0000, 0x0000, 0x0000, 0x0000, 0x0000, 0x0000, 0x0000, 0x0000, 0x0000,
		 0x0000, 0x0000, 0x0000, 0x0000, 0x0000, 0x0000, 0x0000, 0x0000, 0x0000, 0x0000, 0x0000, 0x0000, 0x0000, 0x0000, 0x0000, 0x0000};
	
	public static final char[] opCodeChar =
		{'0', '0', '0', '0', '0', '0', '0', '0', '0', '0', '0', '0', '0', '0', '0', '0',
		 '0', '0', '0', '0', '0', '0', '0', '0', '0', '0', '0', '0', '0', '0', '0', '0',
		 '0', '0', '0', '0', '3', '0', '0', '0', '4', '4', '4', '0', '3', '0', '3', '1',
		 '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', '0', '0', '0', '3', '0', '0',
		 '4', '1', '2', '3', '4', '5', '6', '7', '8', '9', '1', '2', '3', '4', '5', '6',
		 '7', '8', '9', '2', '3', '4', '5', '6', '7', '8', '9', '0', '0', '0', '0', '0',
		 '0', '0', '0', '0', '0', '0', '0', '0', '0', '0', '0', '0', '0', '0', '0', '0',
		 '0', '0', '0', '0', '0', '0', '0', '0', '0', '0', '0', '0', '0', '0', '0', '0'};
	
	public static final byte[] ptCode =
		{0x20, 0x01, 0x02, 0x13, 0x04, 0x15, 0x16, 0x07, 0x08, 0x19, 0x2A, 0x0B, 0x1C, 0x00, 0x00, 0x2F,
		 0x40, 0x51, 0x52, 0x43, 0x54, 0x45, 0x46, 0x57, 0x58, 0x49, 0x4A, 0x5B, 0x4C, 0x00, 0x00, 0x4F};
	
	// Initialization Constants
	
	public static final byte[] INIT_NOP = new byte[]{4, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0};
	public static final byte[] INIT_H   = new byte[]{4, 8, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0};
	public static final byte[] INIT_B   = new byte[]{4, 9, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0};

	public static final String INIT_CMEM_1 = "// ---------------------------------------------------------------\n" +
											 "// Startup\n";
	public static final String INIT_CMEM_2 = "\n" +
											 "// Multiply table\n" +
											 "00100: 00 00 00 00 00 00 00 00 00 00 00 00 01 00 02 00 03 00 04 00\n" +
											 "00120: 00 00 02 00 04 00 06 00 08 00 00 00 03 00 06 00 09 00 02 01\n" +
											 "00140: 00 00 04 00 08 00 02 01 06 01 00 00 05 00 00 01 05 01 00 02\n" +
											 "00160: 00 00 06 00 02 01 08 01 04 02 00 00 07 00 04 01 01 02 08 02\n" +
											 "00180: 00 00 08 00 06 01 04 02 02 03 00 00 09 00 08 01 07 02 06 03\n" +
											 "00200: 00 00 00 00 00 00 00 00 00 00 05 00 06 00 07 00 08 00 09 00\n" +
											 "00220: 00 01 02 01 04 01 06 01 08 01 05 01 08 01 01 02 04 02 07 02\n" +
											 "00240: 00 02 04 02 08 02 02 03 06 03 05 02 00 03 05 03 00 04 05 04\n" +
											 "00260: 00 03 06 03 02 04 08 04 04 05 05 03 02 04 09 04 06 05 03 06\n" +
											 "00280: 00 04 08 04 06 05 04 06 02 07 05 04 04 05 03 06 02 07 01 08\n";
	public static final String INIT_CMEM_3 = "\n" +
											 "// Add table\n" +
											 "00300: 00 01 02 03 04 05 06 07 08 09 01 02 03 04 05 06 07 08 09 10\n" +
											 "00320: 02 03 04 05 06 07 08 09 10 11 03 04 05 06 07 08 09 10 11 12\n" +
											 "00340: 04 05 06 07 08 09 10 11 12 13 05 06 07 08 09 10 11 12 13 14\n" +
											 "00360: 06 07 08 09 10 11 12 13 14 15 07 08 09 10 11 12 13 14 15 16\n" +
											 "00380: 08 09 10 11 12 13 14 15 16 17 09 10 11 12 13 14 15 16 17 18\n";
	public static final String INIT_CMEM_4 = "\n" +
											 "// Record mark\n" +
											 "00400: 0A\n" +
											 "// ---------------------------------------------------------------\n\n\n";
	
	public static final String INIT_CRD_1 = "41000000050036001100050026000470011925001090000026000660011431000000012000000000\n";
	public static final String INIT_CRD_2 = "]0072]0108260009000119250000000109490001200000|000000000000000000000000000000000\n";
	public static final String INIT_CRD_3 = "]0028]01093600160005003600080005001600001000L6490000004900028|000000000000000000\n";
	
	// Data
	
	public static String dateTime = FORMAT_DATE.format(new Date());
	
	public static SourceFormat sourceFormat = SourceFormat.FIXED;
	public static SystemType systemType = SystemType.MODEL_1;
	public static boolean indexOk = false;
	public static int memorySize = 60000;
	public static boolean includeTables = true;
	public static boolean loadHalt = true;
	public static boolean produceWarnings = true;
	public static boolean pass1Errors = false;
	public static byte[] tabTable = null;
	public static boolean symbolDivide = true;
	
	public static boolean sourceMultipleFiles = false;
	public static int sourceFileCount = 0;
	public static int sourceCurrentFile = -1;
	public static String[] sourceFilenames = new String[SIZE_SOURCE_TABLE];
	public static File sourceFile = null;
	public static BufferedReader sourceReader = null;
	
	public static boolean lstOption = true;
	public static String lstFilename = null;
	public static File lstFile = null;
	public static PrintWriter lstWriter = null;
	
	public static boolean cmemOption = false;
	public static String cmemFilename = null;
	public static File cmemFile = null;
	public static PrintWriter cmemWriter = null;
	
	public static boolean crdOption = false;
	public static String crdFilename = null;
	public static File crdFile = null;
	public static PrintWriter crdWriter = null;
	
	public static boolean ptOption = false;
	public static String ptFilename = null;
	public static File ptFile = null;
	public static FileOutputStream ptStream = null;
	
	public static int pass = 0;
	public static int lineNumber = 0;
	public static int lineCount = 0;
	public static int errorCount = 0;
	public static int warningCount = 0;
	public static int addressCounter = 0;
	public static int lastAddress = 0;
	public static char symbolHead = ' ';
	public static DendStatus dendStatus = DendStatus.NOT_SEEN;
	public static int dendAddress = 0;
	
	public static int operandIndex = 0;
	
	public static String inputLine = null;
	public static String inputLabel = null;
	public static String inputOperation = null;
	public static String inputOperands = null;
	public static String[] inputOperand = null;
	public static SPSStatement inputStatement = null;
	public static String inputMessages = null;
	
	public static int labelAddress = 0;
	public static int address = 0;
	public static int length = 0;
	public static int count = 0;
	public static byte[] value = null;
	
	public static byte[] memory = new byte[memorySize];
}

