package SPS1620;

/*
 *  java - statement definition and processing
 *
 *  IBM 1620 Jr Project, Computer History Museum, 2017-2024
 *
 *  To recreate the experience (visual, auditory, tactile, visceral) of running historic software on a 1960s-era computer.
 *
 *   Dave Babcock     - project lead, software, firmware, library
 *   John M. Bohn Jr. - software
 *   David Brock      - CHM sponsor
 *   Steve Casner     - hardware, software
 *   Joe Fredrick     - hardware, firmware
 *   Len Shustek      - CHM advisor
 *   Dag Spicer       - CHM advisor
 *   David Wise       - IBM 1620 expert
 */

import java.util.HashMap;

public class SPSStatement {
	
	// Constants
	
	public static final int P_NONE      = -1;
	public static final int P_REFERENCE = -2;
	public static final int P_REF_EVEN  = -3;
	public static final int P_REF_ODD   = -4;
	public static final int P_VALUE     = -5;

	public static final int Q_NONE      = -1;
	public static final int Q_REFERENCE = -2;
	public static final int Q_REF_BIT   = -3;
	public static final int Q_IMMEDIATE = -4;
	public static final int Q_IMM_INDEX = -5;
	public static final int Q_VALUE     = -6;
	public static final int Q_SKIP      = -7;
	public static final int Q_SKAP      = -8;
	public static final int Q_SPIM      = -9;
	public static final int Q_SPAP      = -10;
	
	// Enumerations
	
	public static enum StatementClass {UNKNOWN, COMMENT, DECLARATIVE, IMPERATIVE, CONTROL}

	public static enum StatementType {/* UNKNOWN */     UNKNOWN,
									  /* COMMENT */     COMMENT,
									  /* CONTROL */     DEND, DORG, HEAD, SEND, TCD, TRA,
									  /* DECLARATIVE */ DAC, DAS, DC, DDA, DGM, DNB, DOT, DS, DSA, DSAC, DSB, DSC, DSS, DVLC,
									  /* IMPERATIVE */  INSTRUCTION};

	public static enum StatementListing {UNKNOWN, COMMENT, CONTROL, ADDRESS, INSTRUCTION, INSTR_2, INSTR_7, DATA, RESERVE_1, RESERVE_2};

	public static enum StatementCheck {NONE, ADDRESS, ADDR_EVEN, ADDR_ODD, REFERENCE, REF_EVEN, REF_ODD, VALUE, VAL_POSITIVE, VAL_DOUBLE};
	
	// Data
	
	public StatementClass Class;
	public StatementType Type;
	public SPSData.SystemType Model;
	public byte OpCode;
	public byte OpCnt;
	public int PField;
	public int QField;
	public StatementListing Listing;
	
	private static HashMap<String, SPSStatement> statementTable = new HashMap<String, SPSStatement>(SPSData.SIZE_OP_TABLE);
	
	// Constructors
	
	public SPSStatement(StatementClass sclass, StatementType type, SPSData.SystemType model, int opCode, int opCnt, int pField,
						int qField, StatementListing listing) {
		this.Class = sclass;
		this.Type = type;
		this.Model = model;
		this.OpCode = (byte)opCode;
		this.OpCnt = (byte)opCnt;
		this.PField = pField;
		this.QField = qField;
		this.Listing = listing;
	}
	
	public SPSStatement(SPSStatement entry) {
		if (entry != null) {
			this.Class = entry.Class;
			this.Type = entry.Type;
			this.Model = entry.Model;
			this.OpCode = entry.OpCode;
			this.OpCnt = entry.OpCnt;
			this.PField = entry.PField;
			this.QField = entry.QField;
			this.Listing = entry.Listing;
		} else {
			this.Class = StatementClass.UNKNOWN;
			this.Type = StatementType.UNKNOWN;
			this.Model = SPSData.SystemType.ANY;
			this.OpCode = 0;
			this.OpCnt = 0;
			this.PField = P_NONE;
			this.QField = Q_NONE;
			this.Listing = StatementListing.UNKNOWN;
		}
	}
	
	// Methods

	public static void Initialize() {

		/*  UNKNOWN  */

		statementTable.put("????", new SPSStatement(StatementClass.UNKNOWN,     StatementType.UNKNOWN,     SPSData.SystemType.ANY,      0,  0, 0, 0, StatementListing.UNKNOWN));
	
		/*  COMMENT  */

		statementTable.put("*",    new SPSStatement(StatementClass.COMMENT,     StatementType.COMMENT,     SPSData.SystemType.ANY,      0,  0, 0, 0, StatementListing.COMMENT));
	
		/*  CONTROL  */

		statementTable.put("DEND", new SPSStatement(StatementClass.CONTROL,     StatementType.DEND,        SPSData.SystemType.ANY,      0,  1, 0, 0, StatementListing.ADDRESS));
		statementTable.put("DORG", new SPSStatement(StatementClass.CONTROL,     StatementType.DORG,        SPSData.SystemType.ANY,      0,  1, 0, 0, StatementListing.ADDRESS));
		statementTable.put("HEAD", new SPSStatement(StatementClass.CONTROL,     StatementType.HEAD,        SPSData.SystemType.ANY,      0,  1, 0, 0, StatementListing.CONTROL));
		statementTable.put("SEND", new SPSStatement(StatementClass.CONTROL,     StatementType.SEND,        SPSData.SystemType.ANY,      0,  0, 0, 0, StatementListing.CONTROL));
		statementTable.put("TCD",  new SPSStatement(StatementClass.CONTROL,     StatementType.TCD,         SPSData.SystemType.ANY,      0,  1, 0, 0, StatementListing.ADDRESS));
		statementTable.put("TRA",  new SPSStatement(StatementClass.CONTROL,     StatementType.TRA,         SPSData.SystemType.ANY,      0,  0, 0, 0, StatementListing.CONTROL));

		/*  DECLARATIVE  */

		statementTable.put("DAC",  new SPSStatement(StatementClass.DECLARATIVE, StatementType.DAC,         SPSData.SystemType.ANY,      0,  3, 0, 0, StatementListing.DATA));
		statementTable.put("DAS",  new SPSStatement(StatementClass.DECLARATIVE, StatementType.DAS,         SPSData.SystemType.ANY,      0,  2, 0, 0, StatementListing.RESERVE_1));
		statementTable.put("DC",   new SPSStatement(StatementClass.DECLARATIVE, StatementType.DC,          SPSData.SystemType.ANY,      0,  3, 0, 0, StatementListing.DATA));
		statementTable.put("DDA",  new SPSStatement(StatementClass.DECLARATIVE, StatementType.DDA,         SPSData.SystemType.ANY,      0,  5, 0, 0, StatementListing.DATA));
		statementTable.put("DGM",  new SPSStatement(StatementClass.DECLARATIVE, StatementType.DGM,         SPSData.SystemType.ANY,      0,  1, 0, 0, StatementListing.DATA));
		statementTable.put("DNB",  new SPSStatement(StatementClass.DECLARATIVE, StatementType.DNB,         SPSData.SystemType.ANY,      0,  2, 0, 0, StatementListing.DATA));
		statementTable.put("DOT",  new SPSStatement(StatementClass.DECLARATIVE, StatementType.DOT,         SPSData.SystemType.MODEL_2,  0,  2, 0, 0, StatementListing.DATA));
		statementTable.put("DS",   new SPSStatement(StatementClass.DECLARATIVE, StatementType.DS,          SPSData.SystemType.ANY,      0,  2, 0, 0, StatementListing.RESERVE_1));
		statementTable.put("DSA",  new SPSStatement(StatementClass.DECLARATIVE, StatementType.DSA,         SPSData.SystemType.ANY,      0, 10, 0, 0, StatementListing.DATA));
		statementTable.put("DSAC", new SPSStatement(StatementClass.DECLARATIVE, StatementType.DSAC,        SPSData.SystemType.ANY,      0,  3, 0, 0, StatementListing.DATA));
		statementTable.put("DSB",  new SPSStatement(StatementClass.DECLARATIVE, StatementType.DSB,         SPSData.SystemType.ANY,      0,  3, 0, 0, StatementListing.RESERVE_2));
		statementTable.put("DSC",  new SPSStatement(StatementClass.DECLARATIVE, StatementType.DSC,         SPSData.SystemType.ANY,      0,  3, 0, 0, StatementListing.DATA));
		statementTable.put("DSS",  new SPSStatement(StatementClass.DECLARATIVE, StatementType.DSS,         SPSData.SystemType.ANY,      0,  2, 0, 0, StatementListing.RESERVE_1));
		statementTable.put("DVLC", new SPSStatement(StatementClass.DECLARATIVE, StatementType.DVLC,        SPSData.SystemType.ANY,      0, 41, 0, 0, StatementListing.DATA));

		/*  IMPERATIVE (ALPHA)  */

		statementTable.put("A",    new SPSStatement(StatementClass.IMPERATIVE,  StatementType.INSTRUCTION, SPSData.SystemType.ANY,     21,  3, P_REFERENCE, Q_REFERENCE, StatementListing.INSTRUCTION));
		statementTable.put("AM",   new SPSStatement(StatementClass.IMPERATIVE,  StatementType.INSTRUCTION, SPSData.SystemType.ANY,     11,  3, P_REFERENCE, Q_IMMEDIATE, StatementListing.INSTRUCTION));
		statementTable.put("ANDF", new SPSStatement(StatementClass.IMPERATIVE,  StatementType.INSTRUCTION, SPSData.SystemType.MODEL_2, 93,  3, P_REFERENCE, Q_REFERENCE, StatementListing.INSTRUCTION));
		statementTable.put("B",    new SPSStatement(StatementClass.IMPERATIVE,  StatementType.INSTRUCTION, SPSData.SystemType.ANY,     49,  3, P_REF_EVEN, Q_VALUE, StatementListing.INSTRUCTION));
		statementTable.put("B7",   new SPSStatement(StatementClass.IMPERATIVE,  StatementType.INSTRUCTION, SPSData.SystemType.ANY,     49,  3, P_REF_EVEN, Q_NONE, StatementListing.INSTR_7));
		statementTable.put("BA",   new SPSStatement(StatementClass.IMPERATIVE,  StatementType.INSTRUCTION, SPSData.SystemType.ANY,     46,  3, P_REF_EVEN, 1900, StatementListing.INSTRUCTION));
		statementTable.put("BANS", new SPSStatement(StatementClass.IMPERATIVE,  StatementType.INSTRUCTION, SPSData.SystemType.MODEL_2, 47,  3, P_REF_EVEN, 3100, StatementListing.INSTRUCTION));
		statementTable.put("BB",   new SPSStatement(StatementClass.IMPERATIVE,  StatementType.INSTRUCTION, SPSData.SystemType.ANY,     42,  3, P_VALUE, Q_VALUE, StatementListing.INSTRUCTION));
		statementTable.put("BB2",  new SPSStatement(StatementClass.IMPERATIVE,  StatementType.INSTRUCTION, SPSData.SystemType.ANY,     42,  3, P_NONE, Q_NONE, StatementListing.INSTR_2));
		statementTable.put("BBAS", new SPSStatement(StatementClass.IMPERATIVE,  StatementType.INSTRUCTION, SPSData.SystemType.MODEL_2, 46,  3, P_REF_EVEN, 3100, StatementListing.INSTRUCTION));
		statementTable.put("BBBS", new SPSStatement(StatementClass.IMPERATIVE,  StatementType.INSTRUCTION, SPSData.SystemType.MODEL_2, 46,  3, P_REF_EVEN, 3200, StatementListing.INSTRUCTION));
		statementTable.put("BBNS", new SPSStatement(StatementClass.IMPERATIVE,  StatementType.INSTRUCTION, SPSData.SystemType.MODEL_2, 47,  3, P_REF_EVEN, 3200, StatementListing.INSTRUCTION));
		statementTable.put("BBT",  new SPSStatement(StatementClass.IMPERATIVE,  StatementType.INSTRUCTION, SPSData.SystemType.MODEL_2, 90,  4, P_REF_EVEN, Q_REF_BIT, StatementListing.INSTRUCTION));
		statementTable.put("BC1",  new SPSStatement(StatementClass.IMPERATIVE,  StatementType.INSTRUCTION, SPSData.SystemType.ANY,     46,  3, P_REF_EVEN, 100, StatementListing.INSTRUCTION));
		statementTable.put("BC2",  new SPSStatement(StatementClass.IMPERATIVE,  StatementType.INSTRUCTION, SPSData.SystemType.ANY,     46,  3, P_REF_EVEN, 200, StatementListing.INSTRUCTION));
		statementTable.put("BC3",  new SPSStatement(StatementClass.IMPERATIVE,  StatementType.INSTRUCTION, SPSData.SystemType.ANY,     46,  3, P_REF_EVEN, 300, StatementListing.INSTRUCTION));
		statementTable.put("BC4",  new SPSStatement(StatementClass.IMPERATIVE,  StatementType.INSTRUCTION, SPSData.SystemType.ANY,     46,  3, P_REF_EVEN, 400, StatementListing.INSTRUCTION));
		statementTable.put("BCH9", new SPSStatement(StatementClass.IMPERATIVE,  StatementType.INSTRUCTION, SPSData.SystemType.ANY,     46,  3, P_REF_EVEN, 3300, StatementListing.INSTRUCTION));
		statementTable.put("BCOV", new SPSStatement(StatementClass.IMPERATIVE,  StatementType.INSTRUCTION, SPSData.SystemType.ANY,     46,  3, P_REF_EVEN, 3400, StatementListing.INSTRUCTION));
		statementTable.put("BCX",  new SPSStatement(StatementClass.IMPERATIVE,  StatementType.INSTRUCTION, SPSData.SystemType.MODEL_2, 63,  3, P_REF_EVEN, Q_REFERENCE, StatementListing.INSTRUCTION));
		statementTable.put("BCXM", new SPSStatement(StatementClass.IMPERATIVE,  StatementType.INSTRUCTION, SPSData.SystemType.MODEL_2, 64,  3, P_REF_EVEN, Q_IMM_INDEX, StatementListing.INSTRUCTION));
		statementTable.put("BD",   new SPSStatement(StatementClass.IMPERATIVE,  StatementType.INSTRUCTION, SPSData.SystemType.ANY,     43,  3, P_REF_EVEN, Q_REFERENCE, StatementListing.INSTRUCTION));
		statementTable.put("BE",   new SPSStatement(StatementClass.IMPERATIVE,  StatementType.INSTRUCTION, SPSData.SystemType.ANY,     46,  3, P_REF_EVEN, 1200, StatementListing.INSTRUCTION));
		statementTable.put("BEBS", new SPSStatement(StatementClass.IMPERATIVE,  StatementType.INSTRUCTION, SPSData.SystemType.MODEL_2, 47,  3, P_REF_EVEN, 3000, StatementListing.INSTRUCTION));
		statementTable.put("BH",   new SPSStatement(StatementClass.IMPERATIVE,  StatementType.INSTRUCTION, SPSData.SystemType.ANY,     46,  3, P_REF_EVEN, 1100, StatementListing.INSTRUCTION));
		statementTable.put("BI",   new SPSStatement(StatementClass.IMPERATIVE,  StatementType.INSTRUCTION, SPSData.SystemType.ANY,     46,  3, P_REF_EVEN, Q_VALUE, StatementListing.INSTRUCTION));
		statementTable.put("BKTY", new SPSStatement(StatementClass.IMPERATIVE,  StatementType.INSTRUCTION, SPSData.SystemType.MODEL_2, 34,  3, P_VALUE, 103, StatementListing.INSTRUCTION));
		statementTable.put("BL",   new SPSStatement(StatementClass.IMPERATIVE,  StatementType.INSTRUCTION, SPSData.SystemType.ANY,     47,  3, P_REF_EVEN, 1300, StatementListing.INSTRUCTION));
		statementTable.put("BLC",  new SPSStatement(StatementClass.IMPERATIVE,  StatementType.INSTRUCTION, SPSData.SystemType.ANY,     46,  3, P_REF_EVEN, 900, StatementListing.INSTRUCTION));
		statementTable.put("BLX",  new SPSStatement(StatementClass.IMPERATIVE,  StatementType.INSTRUCTION, SPSData.SystemType.MODEL_2, 65,  3, P_REF_EVEN, Q_REFERENCE, StatementListing.INSTRUCTION));
		statementTable.put("BLXM", new SPSStatement(StatementClass.IMPERATIVE,  StatementType.INSTRUCTION, SPSData.SystemType.MODEL_2, 66,  3, P_REF_EVEN, Q_IMM_INDEX, StatementListing.INSTRUCTION));
		statementTable.put("BMK",  new SPSStatement(StatementClass.IMPERATIVE,  StatementType.INSTRUCTION, SPSData.SystemType.MODEL_2, 91,  4, P_REF_EVEN, Q_REF_BIT, StatementListing.INSTRUCTION));
		statementTable.put("BN",   new SPSStatement(StatementClass.IMPERATIVE,  StatementType.INSTRUCTION, SPSData.SystemType.ANY,     47,  3, P_REF_EVEN, 1300, StatementListing.INSTRUCTION));
		statementTable.put("BNA",  new SPSStatement(StatementClass.IMPERATIVE,  StatementType.INSTRUCTION, SPSData.SystemType.ANY,     47,  3, P_REF_EVEN, 1900, StatementListing.INSTRUCTION));
		statementTable.put("BNBS", new SPSStatement(StatementClass.IMPERATIVE,  StatementType.INSTRUCTION, SPSData.SystemType.MODEL_2, 46,  3, P_REF_EVEN, 3000, StatementListing.INSTRUCTION));
		statementTable.put("BNC1", new SPSStatement(StatementClass.IMPERATIVE,  StatementType.INSTRUCTION, SPSData.SystemType.ANY,     47,  3, P_REF_EVEN, 100, StatementListing.INSTRUCTION));
		statementTable.put("BNC2", new SPSStatement(StatementClass.IMPERATIVE,  StatementType.INSTRUCTION, SPSData.SystemType.ANY,     47,  3, P_REF_EVEN, 200, StatementListing.INSTRUCTION));
		statementTable.put("BNC3", new SPSStatement(StatementClass.IMPERATIVE,  StatementType.INSTRUCTION, SPSData.SystemType.ANY,     47,  3, P_REF_EVEN, 300, StatementListing.INSTRUCTION));
		statementTable.put("BNC4", new SPSStatement(StatementClass.IMPERATIVE,  StatementType.INSTRUCTION, SPSData.SystemType.ANY,     47,  3, P_REF_EVEN, 400, StatementListing.INSTRUCTION));
		statementTable.put("BNE",  new SPSStatement(StatementClass.IMPERATIVE,  StatementType.INSTRUCTION, SPSData.SystemType.ANY,     47,  3, P_REF_EVEN, 1200, StatementListing.INSTRUCTION));
		statementTable.put("BNF",  new SPSStatement(StatementClass.IMPERATIVE,  StatementType.INSTRUCTION, SPSData.SystemType.ANY,     44,  3, P_REF_EVEN, Q_REFERENCE, StatementListing.INSTRUCTION));
		statementTable.put("BNG",  new SPSStatement(StatementClass.IMPERATIVE,  StatementType.INSTRUCTION, SPSData.SystemType.ANY,     55,  3, P_REF_EVEN, Q_REFERENCE, StatementListing.INSTRUCTION));
		statementTable.put("BNH",  new SPSStatement(StatementClass.IMPERATIVE,  StatementType.INSTRUCTION, SPSData.SystemType.ANY,     47,  3, P_REF_EVEN, 1100, StatementListing.INSTRUCTION));
		statementTable.put("BNI",  new SPSStatement(StatementClass.IMPERATIVE,  StatementType.INSTRUCTION, SPSData.SystemType.ANY,     47,  3, P_REF_EVEN, Q_VALUE, StatementListing.INSTRUCTION));
		statementTable.put("BNL",  new SPSStatement(StatementClass.IMPERATIVE,  StatementType.INSTRUCTION, SPSData.SystemType.ANY,     46,  3, P_REF_EVEN, 1300, StatementListing.INSTRUCTION));
		statementTable.put("BNLC", new SPSStatement(StatementClass.IMPERATIVE,  StatementType.INSTRUCTION, SPSData.SystemType.ANY,     47,  3, P_REF_EVEN, 900, StatementListing.INSTRUCTION));
		statementTable.put("BNN",  new SPSStatement(StatementClass.IMPERATIVE,  StatementType.INSTRUCTION, SPSData.SystemType.ANY,     46,  3, P_REF_EVEN, 1300, StatementListing.INSTRUCTION));
		statementTable.put("BNP",  new SPSStatement(StatementClass.IMPERATIVE,  StatementType.INSTRUCTION, SPSData.SystemType.ANY,     47,  3, P_REF_EVEN, 1100, StatementListing.INSTRUCTION));
		statementTable.put("BNR",  new SPSStatement(StatementClass.IMPERATIVE,  StatementType.INSTRUCTION, SPSData.SystemType.ANY,     45,  3, P_REF_EVEN, Q_REFERENCE, StatementListing.INSTRUCTION));
		statementTable.put("BNV",  new SPSStatement(StatementClass.IMPERATIVE,  StatementType.INSTRUCTION, SPSData.SystemType.ANY,     47,  3, P_REF_EVEN, 1400, StatementListing.INSTRUCTION));
		statementTable.put("BNXV", new SPSStatement(StatementClass.IMPERATIVE,  StatementType.INSTRUCTION, SPSData.SystemType.ANY,     47,  3, P_REF_EVEN, 1500, StatementListing.INSTRUCTION));
		statementTable.put("BNZ",  new SPSStatement(StatementClass.IMPERATIVE,  StatementType.INSTRUCTION, SPSData.SystemType.ANY,     47,  3, P_REF_EVEN, 1200, StatementListing.INSTRUCTION));
		statementTable.put("BP",   new SPSStatement(StatementClass.IMPERATIVE,  StatementType.INSTRUCTION, SPSData.SystemType.ANY,     46,  3, P_REF_EVEN, 1100, StatementListing.INSTRUCTION));
		statementTable.put("BS",   new SPSStatement(StatementClass.IMPERATIVE,  StatementType.INSTRUCTION, SPSData.SystemType.MODEL_2, 60,  3, P_REFERENCE, Q_VALUE, StatementListing.INSTRUCTION));
		statementTable.put("BSBA", new SPSStatement(StatementClass.IMPERATIVE,  StatementType.INSTRUCTION, SPSData.SystemType.MODEL_2, 60,  3, P_REFERENCE, 1, StatementListing.INSTRUCTION));
		statementTable.put("BSBB", new SPSStatement(StatementClass.IMPERATIVE,  StatementType.INSTRUCTION, SPSData.SystemType.MODEL_2, 60,  3, P_REFERENCE, 2, StatementListing.INSTRUCTION));
		statementTable.put("BSIA", new SPSStatement(StatementClass.IMPERATIVE,  StatementType.INSTRUCTION, SPSData.SystemType.MODEL_2, 60,  3, P_REFERENCE, 9, StatementListing.INSTRUCTION));
		statementTable.put("BSNI", new SPSStatement(StatementClass.IMPERATIVE,  StatementType.INSTRUCTION, SPSData.SystemType.MODEL_2, 60,  3, P_REFERENCE, 8, StatementListing.INSTRUCTION));
		statementTable.put("BSNX", new SPSStatement(StatementClass.IMPERATIVE,  StatementType.INSTRUCTION, SPSData.SystemType.MODEL_2, 60,  3, P_REFERENCE, Q_VALUE, StatementListing.INSTRUCTION));
		statementTable.put("BSX",  new SPSStatement(StatementClass.IMPERATIVE,  StatementType.INSTRUCTION, SPSData.SystemType.MODEL_2, 67,  3, P_REF_EVEN, Q_REFERENCE, StatementListing.INSTRUCTION));
		statementTable.put("BT",   new SPSStatement(StatementClass.IMPERATIVE,  StatementType.INSTRUCTION, SPSData.SystemType.ANY,     27,  3, P_REF_EVEN, Q_REFERENCE, StatementListing.INSTRUCTION));
		statementTable.put("BTA",  new SPSStatement(StatementClass.IMPERATIVE,  StatementType.INSTRUCTION, SPSData.SystemType.MODEL_2, 20,  3, P_REF_EVEN, Q_REFERENCE, StatementListing.INSTRUCTION));
		statementTable.put("BTAM", new SPSStatement(StatementClass.IMPERATIVE,  StatementType.INSTRUCTION, SPSData.SystemType.MODEL_2, 10,  3, P_REF_EVEN, Q_IMMEDIATE, StatementListing.INSTRUCTION));
		statementTable.put("BTFL", new SPSStatement(StatementClass.IMPERATIVE,  StatementType.INSTRUCTION, SPSData.SystemType.ANY,      7,  3, P_REF_EVEN, Q_REFERENCE, StatementListing.INSTRUCTION));
		statementTable.put("BTM",  new SPSStatement(StatementClass.IMPERATIVE,  StatementType.INSTRUCTION, SPSData.SystemType.ANY,     17,  3, P_REF_EVEN, Q_IMMEDIATE, StatementListing.INSTRUCTION));
		statementTable.put("BV",   new SPSStatement(StatementClass.IMPERATIVE,  StatementType.INSTRUCTION, SPSData.SystemType.ANY,     46,  3, P_REF_EVEN, 1400, StatementListing.INSTRUCTION));
		statementTable.put("BX",   new SPSStatement(StatementClass.IMPERATIVE,  StatementType.INSTRUCTION, SPSData.SystemType.MODEL_2, 61,  3, P_REF_EVEN, Q_REFERENCE, StatementListing.INSTRUCTION));
		statementTable.put("BXM",  new SPSStatement(StatementClass.IMPERATIVE,  StatementType.INSTRUCTION, SPSData.SystemType.MODEL_2, 62,  3, P_REF_EVEN, Q_IMM_INDEX, StatementListing.INSTRUCTION));
		statementTable.put("BXV",  new SPSStatement(StatementClass.IMPERATIVE,  StatementType.INSTRUCTION, SPSData.SystemType.ANY,     46,  3, P_REF_EVEN, 1500, StatementListing.INSTRUCTION));
		statementTable.put("BZ",   new SPSStatement(StatementClass.IMPERATIVE,  StatementType.INSTRUCTION, SPSData.SystemType.ANY,     46,  3, P_REF_EVEN, 1200, StatementListing.INSTRUCTION));
		statementTable.put("C",    new SPSStatement(StatementClass.IMPERATIVE,  StatementType.INSTRUCTION, SPSData.SystemType.ANY,     24,  3, P_REFERENCE, Q_REFERENCE, StatementListing.INSTRUCTION));
		statementTable.put("CDGN", new SPSStatement(StatementClass.IMPERATIVE,  StatementType.INSTRUCTION, SPSData.SystemType.ANY,     36,  3, P_REF_EVEN, 701, StatementListing.INSTRUCTION));
		statementTable.put("CDN",  new SPSStatement(StatementClass.IMPERATIVE,  StatementType.INSTRUCTION, SPSData.SystemType.ANY,     36,  3, P_REF_EVEN, 703, StatementListing.INSTRUCTION));
		statementTable.put("CF",   new SPSStatement(StatementClass.IMPERATIVE,  StatementType.INSTRUCTION, SPSData.SystemType.ANY,     33,  3, P_REFERENCE, Q_VALUE, StatementListing.INSTRUCTION));
		statementTable.put("CM",   new SPSStatement(StatementClass.IMPERATIVE,  StatementType.INSTRUCTION, SPSData.SystemType.ANY,     14,  3, P_REFERENCE, Q_IMMEDIATE, StatementListing.INSTRUCTION));
		statementTable.put("CPLF", new SPSStatement(StatementClass.IMPERATIVE,  StatementType.INSTRUCTION, SPSData.SystemType.MODEL_2, 94,  3, P_REFERENCE, Q_REFERENCE, StatementListing.INSTRUCTION));
		statementTable.put("CTGN", new SPSStatement(StatementClass.IMPERATIVE,  StatementType.INSTRUCTION, SPSData.SystemType.ANY,     36,  3, P_REF_EVEN, 705, StatementListing.INSTRUCTION));
		statementTable.put("CTN",  new SPSStatement(StatementClass.IMPERATIVE,  StatementType.INSTRUCTION, SPSData.SystemType.ANY,     36,  3, P_REF_EVEN, 707, StatementListing.INSTRUCTION));
		statementTable.put("D",    new SPSStatement(StatementClass.IMPERATIVE,  StatementType.INSTRUCTION, SPSData.SystemType.ANY,     29,  3, P_REFERENCE, Q_REFERENCE, StatementListing.INSTRUCTION));
		statementTable.put("DM",   new SPSStatement(StatementClass.IMPERATIVE,  StatementType.INSTRUCTION, SPSData.SystemType.ANY,     19,  3, P_REFERENCE, Q_IMMEDIATE, StatementListing.INSTRUCTION));
		statementTable.put("DN",   new SPSStatement(StatementClass.IMPERATIVE,  StatementType.INSTRUCTION, SPSData.SystemType.ANY,     35,  3, P_REFERENCE, Q_VALUE, StatementListing.INSTRUCTION));
		statementTable.put("DNCD", new SPSStatement(StatementClass.IMPERATIVE,  StatementType.INSTRUCTION, SPSData.SystemType.ANY,     35,  3, P_REFERENCE, 400, StatementListing.INSTRUCTION));
		statementTable.put("DNPT", new SPSStatement(StatementClass.IMPERATIVE,  StatementType.INSTRUCTION, SPSData.SystemType.ANY,     35,  3, P_REFERENCE, 200, StatementListing.INSTRUCTION));
		statementTable.put("DNTY", new SPSStatement(StatementClass.IMPERATIVE,  StatementType.INSTRUCTION, SPSData.SystemType.ANY,     35,  3, P_REFERENCE, 100, StatementListing.INSTRUCTION));
		statementTable.put("DTO",  new SPSStatement(StatementClass.IMPERATIVE,  StatementType.INSTRUCTION, SPSData.SystemType.MODEL_2, 97,  3, P_REFERENCE, Q_REFERENCE, StatementListing.INSTRUCTION));
		statementTable.put("EORF", new SPSStatement(StatementClass.IMPERATIVE,  StatementType.INSTRUCTION, SPSData.SystemType.MODEL_2, 95,  3, P_REFERENCE, Q_REFERENCE, StatementListing.INSTRUCTION));
		statementTable.put("FADD", new SPSStatement(StatementClass.IMPERATIVE,  StatementType.INSTRUCTION, SPSData.SystemType.ANY,      1,  3, P_REFERENCE, Q_REFERENCE, StatementListing.INSTRUCTION));
		statementTable.put("FDIV", new SPSStatement(StatementClass.IMPERATIVE,  StatementType.INSTRUCTION, SPSData.SystemType.ANY,      9,  3, P_REFERENCE, Q_REFERENCE, StatementListing.INSTRUCTION));
		statementTable.put("FMUL", new SPSStatement(StatementClass.IMPERATIVE,  StatementType.INSTRUCTION, SPSData.SystemType.ANY,      3,  3, P_REFERENCE, Q_REFERENCE, StatementListing.INSTRUCTION));
		statementTable.put("FSL",  new SPSStatement(StatementClass.IMPERATIVE,  StatementType.INSTRUCTION, SPSData.SystemType.ANY,      5,  3, P_REFERENCE, Q_REFERENCE, StatementListing.INSTRUCTION));
		statementTable.put("FSR",  new SPSStatement(StatementClass.IMPERATIVE,  StatementType.INSTRUCTION, SPSData.SystemType.ANY,      8,  3, P_REFERENCE, Q_REFERENCE, StatementListing.INSTRUCTION));
		statementTable.put("FSUB", new SPSStatement(StatementClass.IMPERATIVE,  StatementType.INSTRUCTION, SPSData.SystemType.ANY,      2,  3, P_REFERENCE, Q_REFERENCE, StatementListing.INSTRUCTION));
		statementTable.put("H",    new SPSStatement(StatementClass.IMPERATIVE,  StatementType.INSTRUCTION, SPSData.SystemType.ANY,     48,  3, P_VALUE, Q_VALUE, StatementListing.INSTRUCTION));
		statementTable.put("IXTY", new SPSStatement(StatementClass.IMPERATIVE,  StatementType.INSTRUCTION, SPSData.SystemType.MODEL_2, 34,  3, P_VALUE, 104, StatementListing.INSTRUCTION));
		statementTable.put("K",    new SPSStatement(StatementClass.IMPERATIVE,  StatementType.INSTRUCTION, SPSData.SystemType.ANY,     34,  3, P_VALUE, Q_VALUE, StatementListing.INSTRUCTION));
		statementTable.put("LD",   new SPSStatement(StatementClass.IMPERATIVE,  StatementType.INSTRUCTION, SPSData.SystemType.ANY,     28,  3, P_REFERENCE, Q_REFERENCE, StatementListing.INSTRUCTION));
		statementTable.put("LDM",  new SPSStatement(StatementClass.IMPERATIVE,  StatementType.INSTRUCTION, SPSData.SystemType.ANY,     18,  3, P_REFERENCE, Q_IMMEDIATE, StatementListing.INSTRUCTION));
		statementTable.put("M",    new SPSStatement(StatementClass.IMPERATIVE,  StatementType.INSTRUCTION, SPSData.SystemType.ANY,     23,  3, P_REFERENCE, Q_REFERENCE, StatementListing.INSTRUCTION));
		statementTable.put("MA",   new SPSStatement(StatementClass.IMPERATIVE,  StatementType.INSTRUCTION, SPSData.SystemType.MODEL_2, 70,  3, P_REFERENCE, Q_REFERENCE, StatementListing.INSTRUCTION));
		statementTable.put("MF",   new SPSStatement(StatementClass.IMPERATIVE,  StatementType.INSTRUCTION, SPSData.SystemType.ANY,     71,  3, P_REFERENCE, Q_REFERENCE, StatementListing.INSTRUCTION));
		statementTable.put("MM",   new SPSStatement(StatementClass.IMPERATIVE,  StatementType.INSTRUCTION, SPSData.SystemType.ANY,     13,  3, P_REFERENCE, Q_IMMEDIATE, StatementListing.INSTRUCTION));
		statementTable.put("NOP",  new SPSStatement(StatementClass.IMPERATIVE,  StatementType.INSTRUCTION, SPSData.SystemType.ANY,     41,  3, P_VALUE, Q_VALUE, StatementListing.INSTRUCTION));
		statementTable.put("ORF",  new SPSStatement(StatementClass.IMPERATIVE,  StatementType.INSTRUCTION, SPSData.SystemType.MODEL_2, 92,  3, P_REFERENCE, Q_REFERENCE, StatementListing.INSTRUCTION));
		statementTable.put("OTD",  new SPSStatement(StatementClass.IMPERATIVE,  StatementType.INSTRUCTION, SPSData.SystemType.MODEL_2, 96,  3, P_REFERENCE, Q_REFERENCE, StatementListing.INSTRUCTION));
		statementTable.put("PRA",  new SPSStatement(StatementClass.IMPERATIVE,  StatementType.INSTRUCTION, SPSData.SystemType.ANY,     39,  3, P_REF_ODD, 900, StatementListing.INSTRUCTION));
		statementTable.put("PRAS", new SPSStatement(StatementClass.IMPERATIVE,  StatementType.INSTRUCTION, SPSData.SystemType.ANY,     39,  3, P_REF_ODD, 901, StatementListing.INSTRUCTION));
		statementTable.put("PRD",  new SPSStatement(StatementClass.IMPERATIVE,  StatementType.INSTRUCTION, SPSData.SystemType.ANY,     35,  3, P_REFERENCE, 900, StatementListing.INSTRUCTION));
		statementTable.put("PRDS", new SPSStatement(StatementClass.IMPERATIVE,  StatementType.INSTRUCTION, SPSData.SystemType.ANY,     35,  3, P_REFERENCE, 901, StatementListing.INSTRUCTION));
		statementTable.put("PRN",  new SPSStatement(StatementClass.IMPERATIVE,  StatementType.INSTRUCTION, SPSData.SystemType.ANY,     38,  3, P_REFERENCE, 900, StatementListing.INSTRUCTION));
		statementTable.put("PRNS", new SPSStatement(StatementClass.IMPERATIVE,  StatementType.INSTRUCTION, SPSData.SystemType.ANY,     38,  3, P_REFERENCE, 901, StatementListing.INSTRUCTION));
		statementTable.put("RA",   new SPSStatement(StatementClass.IMPERATIVE,  StatementType.INSTRUCTION, SPSData.SystemType.ANY,     37,  3, P_REF_ODD, Q_VALUE, StatementListing.INSTRUCTION));
		statementTable.put("RACD", new SPSStatement(StatementClass.IMPERATIVE,  StatementType.INSTRUCTION, SPSData.SystemType.ANY,     37,  3, P_REF_ODD, 500, StatementListing.INSTRUCTION));
		statementTable.put("RAPT", new SPSStatement(StatementClass.IMPERATIVE,  StatementType.INSTRUCTION, SPSData.SystemType.ANY,     37,  3, P_REF_ODD, 300, StatementListing.INSTRUCTION));
		statementTable.put("RATY", new SPSStatement(StatementClass.IMPERATIVE,  StatementType.INSTRUCTION, SPSData.SystemType.ANY,     37,  3, P_REF_ODD, 100, StatementListing.INSTRUCTION));
		statementTable.put("RBPT", new SPSStatement(StatementClass.IMPERATIVE,  StatementType.INSTRUCTION, SPSData.SystemType.MODEL_2, 37,  3, P_REF_ODD, 3300, StatementListing.INSTRUCTION));
		statementTable.put("RCTY", new SPSStatement(StatementClass.IMPERATIVE,  StatementType.INSTRUCTION, SPSData.SystemType.ANY,     34,  3, P_VALUE, 102, StatementListing.INSTRUCTION));
		statementTable.put("RDGN", new SPSStatement(StatementClass.IMPERATIVE,  StatementType.INSTRUCTION, SPSData.SystemType.ANY,     36,  3, P_REF_EVEN, 700, StatementListing.INSTRUCTION));
		statementTable.put("RDN",  new SPSStatement(StatementClass.IMPERATIVE,  StatementType.INSTRUCTION, SPSData.SystemType.ANY,     36,  3, P_REF_EVEN, 702, StatementListing.INSTRUCTION));
		statementTable.put("RN",   new SPSStatement(StatementClass.IMPERATIVE,  StatementType.INSTRUCTION, SPSData.SystemType.ANY,     36,  3, P_REFERENCE, Q_VALUE, StatementListing.INSTRUCTION));
		statementTable.put("RNCD", new SPSStatement(StatementClass.IMPERATIVE,  StatementType.INSTRUCTION, SPSData.SystemType.ANY,     36,  3, P_REFERENCE, 500, StatementListing.INSTRUCTION));
		statementTable.put("RNPT", new SPSStatement(StatementClass.IMPERATIVE,  StatementType.INSTRUCTION, SPSData.SystemType.ANY,     36,  3, P_REFERENCE, 300, StatementListing.INSTRUCTION));
		statementTable.put("RNTY", new SPSStatement(StatementClass.IMPERATIVE,  StatementType.INSTRUCTION, SPSData.SystemType.ANY,     36,  3, P_REFERENCE, 100, StatementListing.INSTRUCTION));
		statementTable.put("RTGN", new SPSStatement(StatementClass.IMPERATIVE,  StatementType.INSTRUCTION, SPSData.SystemType.ANY,     36,  3, P_REF_EVEN, 704, StatementListing.INSTRUCTION));
		statementTable.put("RTN",  new SPSStatement(StatementClass.IMPERATIVE,  StatementType.INSTRUCTION, SPSData.SystemType.ANY,     36,  3, P_REF_EVEN, 706, StatementListing.INSTRUCTION));
		statementTable.put("S",    new SPSStatement(StatementClass.IMPERATIVE,  StatementType.INSTRUCTION, SPSData.SystemType.ANY,     22,  3, P_REFERENCE, Q_REFERENCE, StatementListing.INSTRUCTION));
		statementTable.put("SF",   new SPSStatement(StatementClass.IMPERATIVE,  StatementType.INSTRUCTION, SPSData.SystemType.ANY,     32,  3, P_REFERENCE, Q_REFERENCE, StatementListing.INSTRUCTION));
		statementTable.put("SK",   new SPSStatement(StatementClass.IMPERATIVE,  StatementType.INSTRUCTION, SPSData.SystemType.ANY,     34,  3, P_VALUE, 701, StatementListing.INSTRUCTION));
		statementTable.put("SKIP", new SPSStatement(StatementClass.IMPERATIVE,  StatementType.INSTRUCTION, SPSData.SystemType.ANY,     34,  3, P_VALUE, Q_SKIP, StatementListing.INSTRUCTION));
		statementTable.put("SKAP", new SPSStatement(StatementClass.IMPERATIVE,  StatementType.INSTRUCTION, SPSData.SystemType.ANY,     34,  3, P_VALUE, Q_SKAP, StatementListing.INSTRUCTION));
		statementTable.put("SM",   new SPSStatement(StatementClass.IMPERATIVE,  StatementType.INSTRUCTION, SPSData.SystemType.ANY,     12,  3, P_REFERENCE, Q_IMMEDIATE, StatementListing.INSTRUCTION));
		statementTable.put("SPAP", new SPSStatement(StatementClass.IMPERATIVE,  StatementType.INSTRUCTION, SPSData.SystemType.ANY,     34,  3, P_VALUE, Q_SPAP, StatementListing.INSTRUCTION));
		statementTable.put("SPIM", new SPSStatement(StatementClass.IMPERATIVE,  StatementType.INSTRUCTION, SPSData.SystemType.ANY,     34,  3, P_VALUE, Q_SPIM, StatementListing.INSTRUCTION));
		statementTable.put("SPTY", new SPSStatement(StatementClass.IMPERATIVE,  StatementType.INSTRUCTION, SPSData.SystemType.ANY,     34,  3, P_VALUE, 101, StatementListing.INSTRUCTION));
		statementTable.put("TBTY", new SPSStatement(StatementClass.IMPERATIVE,  StatementType.INSTRUCTION, SPSData.SystemType.ANY,     34,  3, P_VALUE, 108, StatementListing.INSTRUCTION));
		statementTable.put("TD",   new SPSStatement(StatementClass.IMPERATIVE,  StatementType.INSTRUCTION, SPSData.SystemType.ANY,     25,  3, P_REFERENCE, Q_REFERENCE, StatementListing.INSTRUCTION));
		statementTable.put("TDM",  new SPSStatement(StatementClass.IMPERATIVE,  StatementType.INSTRUCTION, SPSData.SystemType.ANY,     15,  3, P_REFERENCE, Q_VALUE, StatementListing.INSTRUCTION));
		statementTable.put("TF",   new SPSStatement(StatementClass.IMPERATIVE,  StatementType.INSTRUCTION, SPSData.SystemType.ANY,     26,  3, P_REFERENCE, Q_REFERENCE, StatementListing.INSTRUCTION));
		statementTable.put("TFL",  new SPSStatement(StatementClass.IMPERATIVE,  StatementType.INSTRUCTION, SPSData.SystemType.ANY,      6,  3, P_REFERENCE, Q_REFERENCE, StatementListing.INSTRUCTION));
		statementTable.put("TFM",  new SPSStatement(StatementClass.IMPERATIVE,  StatementType.INSTRUCTION, SPSData.SystemType.ANY,     16,  3, P_REFERENCE, Q_IMMEDIATE, StatementListing.INSTRUCTION));
		statementTable.put("TNF",  new SPSStatement(StatementClass.IMPERATIVE,  StatementType.INSTRUCTION, SPSData.SystemType.ANY,     73,  3, P_REF_ODD, Q_REFERENCE, StatementListing.INSTRUCTION));
		statementTable.put("TNS",  new SPSStatement(StatementClass.IMPERATIVE,  StatementType.INSTRUCTION, SPSData.SystemType.ANY,     72,  3, P_REF_ODD, Q_REFERENCE, StatementListing.INSTRUCTION));
		statementTable.put("TR",   new SPSStatement(StatementClass.IMPERATIVE,  StatementType.INSTRUCTION, SPSData.SystemType.ANY,     31,  3, P_REFERENCE, Q_REFERENCE, StatementListing.INSTRUCTION));
		statementTable.put("TRNM", new SPSStatement(StatementClass.IMPERATIVE,  StatementType.INSTRUCTION, SPSData.SystemType.MODEL_2, 30,  3, P_REFERENCE, Q_REFERENCE, StatementListing.INSTRUCTION));
		statementTable.put("WA",   new SPSStatement(StatementClass.IMPERATIVE,  StatementType.INSTRUCTION, SPSData.SystemType.ANY,     39,  3, P_REF_ODD, Q_VALUE, StatementListing.INSTRUCTION));
		statementTable.put("WACD", new SPSStatement(StatementClass.IMPERATIVE,  StatementType.INSTRUCTION, SPSData.SystemType.ANY,     39,  3, P_REF_ODD, 400, StatementListing.INSTRUCTION));
		statementTable.put("WAPT", new SPSStatement(StatementClass.IMPERATIVE,  StatementType.INSTRUCTION, SPSData.SystemType.ANY,     39,  3, P_REF_ODD, 200, StatementListing.INSTRUCTION));
		statementTable.put("WATY", new SPSStatement(StatementClass.IMPERATIVE,  StatementType.INSTRUCTION, SPSData.SystemType.ANY,     39,  3, P_REF_ODD, 100, StatementListing.INSTRUCTION));
		statementTable.put("WBPT", new SPSStatement(StatementClass.IMPERATIVE,  StatementType.INSTRUCTION, SPSData.SystemType.MODEL_2, 39,  3, P_REF_ODD, 3200, StatementListing.INSTRUCTION));
		statementTable.put("WDGN", new SPSStatement(StatementClass.IMPERATIVE,  StatementType.INSTRUCTION, SPSData.SystemType.ANY,     38,  3, P_REF_EVEN, 700, StatementListing.INSTRUCTION));
		statementTable.put("WDN",  new SPSStatement(StatementClass.IMPERATIVE,  StatementType.INSTRUCTION, SPSData.SystemType.ANY,     38,  3, P_REF_EVEN, 702, StatementListing.INSTRUCTION));
		statementTable.put("WN",   new SPSStatement(StatementClass.IMPERATIVE,  StatementType.INSTRUCTION, SPSData.SystemType.ANY,     38,  3, P_REFERENCE, Q_VALUE, StatementListing.INSTRUCTION));
		statementTable.put("WNCD", new SPSStatement(StatementClass.IMPERATIVE,  StatementType.INSTRUCTION, SPSData.SystemType.ANY,     38,  3, P_REFERENCE, 400, StatementListing.INSTRUCTION));
		statementTable.put("WNPT", new SPSStatement(StatementClass.IMPERATIVE,  StatementType.INSTRUCTION, SPSData.SystemType.ANY,     38,  3, P_REFERENCE, 200, StatementListing.INSTRUCTION));
		statementTable.put("WNTY", new SPSStatement(StatementClass.IMPERATIVE,  StatementType.INSTRUCTION, SPSData.SystemType.ANY,     38,  3, P_REFERENCE, 100, StatementListing.INSTRUCTION));
		statementTable.put("WTGN", new SPSStatement(StatementClass.IMPERATIVE,  StatementType.INSTRUCTION, SPSData.SystemType.ANY,     38,  3, P_REF_EVEN, 704, StatementListing.INSTRUCTION));
		statementTable.put("WTN",  new SPSStatement(StatementClass.IMPERATIVE,  StatementType.INSTRUCTION, SPSData.SystemType.ANY,     38,  3, P_REF_EVEN, 706, StatementListing.INSTRUCTION));
	
		/*  IMPERATIVE (NUMERIC)  */

		statementTable.put("01",   new SPSStatement(StatementClass.IMPERATIVE,  StatementType.INSTRUCTION, SPSData.SystemType.ANY,      1,  3, P_REFERENCE, Q_REFERENCE, StatementListing.INSTRUCTION));
		statementTable.put("02",   new SPSStatement(StatementClass.IMPERATIVE,  StatementType.INSTRUCTION, SPSData.SystemType.ANY,      2,  3, P_REFERENCE, Q_REFERENCE, StatementListing.INSTRUCTION));
		statementTable.put("03",   new SPSStatement(StatementClass.IMPERATIVE,  StatementType.INSTRUCTION, SPSData.SystemType.ANY,      3,  3, P_REFERENCE, Q_REFERENCE, StatementListing.INSTRUCTION));
		statementTable.put("05",   new SPSStatement(StatementClass.IMPERATIVE,  StatementType.INSTRUCTION, SPSData.SystemType.ANY,      5,  3, P_REFERENCE, Q_REFERENCE, StatementListing.INSTRUCTION));
		statementTable.put("06",   new SPSStatement(StatementClass.IMPERATIVE,  StatementType.INSTRUCTION, SPSData.SystemType.ANY,      6,  3, P_REFERENCE, Q_REFERENCE, StatementListing.INSTRUCTION));
		statementTable.put("07",   new SPSStatement(StatementClass.IMPERATIVE,  StatementType.INSTRUCTION, SPSData.SystemType.ANY,      7,  3, P_REF_EVEN, Q_REFERENCE, StatementListing.INSTRUCTION));
		statementTable.put("08",   new SPSStatement(StatementClass.IMPERATIVE,  StatementType.INSTRUCTION, SPSData.SystemType.ANY,      8,  3, P_REFERENCE, Q_REFERENCE, StatementListing.INSTRUCTION));
		statementTable.put("09",   new SPSStatement(StatementClass.IMPERATIVE,  StatementType.INSTRUCTION, SPSData.SystemType.ANY,      9,  3, P_REFERENCE, Q_REFERENCE, StatementListing.INSTRUCTION));
		statementTable.put("10",   new SPSStatement(StatementClass.IMPERATIVE,  StatementType.INSTRUCTION, SPSData.SystemType.ANY,     10,  3, P_REF_EVEN, Q_IMMEDIATE, StatementListing.INSTRUCTION));
		statementTable.put("11",   new SPSStatement(StatementClass.IMPERATIVE,  StatementType.INSTRUCTION, SPSData.SystemType.ANY,     11,  3, P_REFERENCE, Q_IMMEDIATE, StatementListing.INSTRUCTION));
		statementTable.put("12",   new SPSStatement(StatementClass.IMPERATIVE,  StatementType.INSTRUCTION, SPSData.SystemType.ANY,     12,  3, P_REFERENCE, Q_IMMEDIATE, StatementListing.INSTRUCTION));
		statementTable.put("13",   new SPSStatement(StatementClass.IMPERATIVE,  StatementType.INSTRUCTION, SPSData.SystemType.ANY,     13,  3, P_REFERENCE, Q_IMMEDIATE, StatementListing.INSTRUCTION));
		statementTable.put("14",   new SPSStatement(StatementClass.IMPERATIVE,  StatementType.INSTRUCTION, SPSData.SystemType.ANY,     14,  3, P_REFERENCE, Q_IMMEDIATE, StatementListing.INSTRUCTION));
		statementTable.put("15",   new SPSStatement(StatementClass.IMPERATIVE,  StatementType.INSTRUCTION, SPSData.SystemType.ANY,     15,  3, P_REFERENCE, Q_VALUE, StatementListing.INSTRUCTION));
		statementTable.put("16",   new SPSStatement(StatementClass.IMPERATIVE,  StatementType.INSTRUCTION, SPSData.SystemType.ANY,     16,  3, P_REFERENCE, Q_IMMEDIATE, StatementListing.INSTRUCTION));
		statementTable.put("17",   new SPSStatement(StatementClass.IMPERATIVE,  StatementType.INSTRUCTION, SPSData.SystemType.ANY,     17,  3, P_REF_EVEN, Q_IMMEDIATE, StatementListing.INSTRUCTION));
		statementTable.put("18",   new SPSStatement(StatementClass.IMPERATIVE,  StatementType.INSTRUCTION, SPSData.SystemType.ANY,     18,  3, P_REFERENCE, Q_IMMEDIATE, StatementListing.INSTRUCTION));
		statementTable.put("19",   new SPSStatement(StatementClass.IMPERATIVE,  StatementType.INSTRUCTION, SPSData.SystemType.ANY,     19,  3, P_REFERENCE, Q_IMMEDIATE, StatementListing.INSTRUCTION));
		statementTable.put("20",   new SPSStatement(StatementClass.IMPERATIVE,  StatementType.INSTRUCTION, SPSData.SystemType.ANY,     20,  3, P_REF_EVEN, Q_REFERENCE, StatementListing.INSTRUCTION));
		statementTable.put("21",   new SPSStatement(StatementClass.IMPERATIVE,  StatementType.INSTRUCTION, SPSData.SystemType.ANY,     21,  3, P_REFERENCE, Q_REFERENCE, StatementListing.INSTRUCTION));
		statementTable.put("22",   new SPSStatement(StatementClass.IMPERATIVE,  StatementType.INSTRUCTION, SPSData.SystemType.ANY,     22,  3, P_REFERENCE, Q_REFERENCE, StatementListing.INSTRUCTION));
		statementTable.put("23",   new SPSStatement(StatementClass.IMPERATIVE,  StatementType.INSTRUCTION, SPSData.SystemType.ANY,     23,  3, P_REFERENCE, Q_REFERENCE, StatementListing.INSTRUCTION));
		statementTable.put("24",   new SPSStatement(StatementClass.IMPERATIVE,  StatementType.INSTRUCTION, SPSData.SystemType.ANY,     24,  3, P_REFERENCE, Q_REFERENCE, StatementListing.INSTRUCTION));
		statementTable.put("25",   new SPSStatement(StatementClass.IMPERATIVE,  StatementType.INSTRUCTION, SPSData.SystemType.ANY,     25,  3, P_REFERENCE, Q_REFERENCE, StatementListing.INSTRUCTION));
		statementTable.put("26",   new SPSStatement(StatementClass.IMPERATIVE,  StatementType.INSTRUCTION, SPSData.SystemType.ANY,     26,  3, P_REFERENCE, Q_REFERENCE, StatementListing.INSTRUCTION));
		statementTable.put("27",   new SPSStatement(StatementClass.IMPERATIVE,  StatementType.INSTRUCTION, SPSData.SystemType.ANY,     27,  3, P_REF_EVEN, Q_REFERENCE, StatementListing.INSTRUCTION));
		statementTable.put("28",   new SPSStatement(StatementClass.IMPERATIVE,  StatementType.INSTRUCTION, SPSData.SystemType.ANY,     28,  3, P_REFERENCE, Q_REFERENCE, StatementListing.INSTRUCTION));
		statementTable.put("29",   new SPSStatement(StatementClass.IMPERATIVE,  StatementType.INSTRUCTION, SPSData.SystemType.ANY,     29,  3, P_REFERENCE, Q_REFERENCE, StatementListing.INSTRUCTION));
		statementTable.put("30",   new SPSStatement(StatementClass.IMPERATIVE,  StatementType.INSTRUCTION, SPSData.SystemType.MODEL_2, 30,  3, P_REFERENCE, Q_REFERENCE, StatementListing.INSTRUCTION));
		statementTable.put("31",   new SPSStatement(StatementClass.IMPERATIVE,  StatementType.INSTRUCTION, SPSData.SystemType.ANY,     31,  3, P_REFERENCE, Q_REFERENCE, StatementListing.INSTRUCTION));
		statementTable.put("32",   new SPSStatement(StatementClass.IMPERATIVE,  StatementType.INSTRUCTION, SPSData.SystemType.ANY,     32,  3, P_REFERENCE, Q_REFERENCE, StatementListing.INSTRUCTION));
		statementTable.put("33",   new SPSStatement(StatementClass.IMPERATIVE,  StatementType.INSTRUCTION, SPSData.SystemType.ANY,     33,  3, P_REFERENCE, Q_VALUE, StatementListing.INSTRUCTION));
		statementTable.put("34",   new SPSStatement(StatementClass.IMPERATIVE,  StatementType.INSTRUCTION, SPSData.SystemType.ANY,     34,  3, P_VALUE, Q_VALUE, StatementListing.INSTRUCTION));
		statementTable.put("35",   new SPSStatement(StatementClass.IMPERATIVE,  StatementType.INSTRUCTION, SPSData.SystemType.ANY,     35,  3, P_REFERENCE, Q_VALUE, StatementListing.INSTRUCTION));
		statementTable.put("36",   new SPSStatement(StatementClass.IMPERATIVE,  StatementType.INSTRUCTION, SPSData.SystemType.ANY,     36,  3, P_REFERENCE, Q_VALUE, StatementListing.INSTRUCTION));
		statementTable.put("37",   new SPSStatement(StatementClass.IMPERATIVE,  StatementType.INSTRUCTION, SPSData.SystemType.ANY,     37,  3, P_REF_ODD, Q_VALUE, StatementListing.INSTRUCTION));
		statementTable.put("38",   new SPSStatement(StatementClass.IMPERATIVE,  StatementType.INSTRUCTION, SPSData.SystemType.ANY,     38,  3, P_REFERENCE, Q_VALUE, StatementListing.INSTRUCTION));
		statementTable.put("39",   new SPSStatement(StatementClass.IMPERATIVE,  StatementType.INSTRUCTION, SPSData.SystemType.ANY,     39,  3, P_REF_ODD, Q_VALUE, StatementListing.INSTRUCTION));
		statementTable.put("41",   new SPSStatement(StatementClass.IMPERATIVE,  StatementType.INSTRUCTION, SPSData.SystemType.ANY,     41,  3, P_VALUE, Q_VALUE, StatementListing.INSTRUCTION));
		statementTable.put("42",   new SPSStatement(StatementClass.IMPERATIVE,  StatementType.INSTRUCTION, SPSData.SystemType.ANY,     42,  3, P_VALUE, Q_VALUE, StatementListing.INSTRUCTION));
		statementTable.put("43",   new SPSStatement(StatementClass.IMPERATIVE,  StatementType.INSTRUCTION, SPSData.SystemType.ANY,     43,  3, P_REF_EVEN, Q_REFERENCE, StatementListing.INSTRUCTION));
		statementTable.put("44",   new SPSStatement(StatementClass.IMPERATIVE,  StatementType.INSTRUCTION, SPSData.SystemType.ANY,     44,  3, P_REF_EVEN, Q_REFERENCE, StatementListing.INSTRUCTION));
		statementTable.put("45",   new SPSStatement(StatementClass.IMPERATIVE,  StatementType.INSTRUCTION, SPSData.SystemType.ANY,     45,  3, P_REF_EVEN, Q_REFERENCE, StatementListing.INSTRUCTION));
		statementTable.put("46",   new SPSStatement(StatementClass.IMPERATIVE,  StatementType.INSTRUCTION, SPSData.SystemType.ANY,     46,  3, P_REF_EVEN, Q_VALUE, StatementListing.INSTRUCTION));
		statementTable.put("47",   new SPSStatement(StatementClass.IMPERATIVE,  StatementType.INSTRUCTION, SPSData.SystemType.ANY,     47,  3, P_REF_EVEN, Q_VALUE, StatementListing.INSTRUCTION));
		statementTable.put("48",   new SPSStatement(StatementClass.IMPERATIVE,  StatementType.INSTRUCTION, SPSData.SystemType.ANY,     48,  3, P_VALUE, Q_VALUE, StatementListing.INSTRUCTION));
		statementTable.put("49",   new SPSStatement(StatementClass.IMPERATIVE,  StatementType.INSTRUCTION, SPSData.SystemType.ANY,     49,  3, P_REF_EVEN, Q_VALUE, StatementListing.INSTRUCTION));
		statementTable.put("55",   new SPSStatement(StatementClass.IMPERATIVE,  StatementType.INSTRUCTION, SPSData.SystemType.ANY,     55,  3, P_REF_EVEN, Q_REFERENCE, StatementListing.INSTRUCTION));
		statementTable.put("60",   new SPSStatement(StatementClass.IMPERATIVE,  StatementType.INSTRUCTION, SPSData.SystemType.MODEL_2, 60,  3, P_REFERENCE, Q_VALUE, StatementListing.INSTRUCTION));
		statementTable.put("61",   new SPSStatement(StatementClass.IMPERATIVE,  StatementType.INSTRUCTION, SPSData.SystemType.MODEL_2, 61,  3, P_REF_EVEN, Q_REFERENCE, StatementListing.INSTRUCTION));
		statementTable.put("62",   new SPSStatement(StatementClass.IMPERATIVE,  StatementType.INSTRUCTION, SPSData.SystemType.MODEL_2, 62,  3, P_REF_EVEN, Q_IMM_INDEX, StatementListing.INSTRUCTION));
		statementTable.put("63",   new SPSStatement(StatementClass.IMPERATIVE,  StatementType.INSTRUCTION, SPSData.SystemType.MODEL_2, 63,  3, P_REF_EVEN, Q_REFERENCE, StatementListing.INSTRUCTION));
		statementTable.put("64",   new SPSStatement(StatementClass.IMPERATIVE,  StatementType.INSTRUCTION, SPSData.SystemType.MODEL_2, 64,  3, P_REF_EVEN, Q_IMM_INDEX, StatementListing.INSTRUCTION));
		statementTable.put("65",   new SPSStatement(StatementClass.IMPERATIVE,  StatementType.INSTRUCTION, SPSData.SystemType.MODEL_2, 65,  3, P_REF_EVEN, Q_REFERENCE, StatementListing.INSTRUCTION));
		statementTable.put("66",   new SPSStatement(StatementClass.IMPERATIVE,  StatementType.INSTRUCTION, SPSData.SystemType.MODEL_2, 66,  3, P_REF_EVEN, Q_IMM_INDEX, StatementListing.INSTRUCTION));
		statementTable.put("67",   new SPSStatement(StatementClass.IMPERATIVE,  StatementType.INSTRUCTION, SPSData.SystemType.MODEL_2, 67,  3, P_REF_EVEN, Q_REFERENCE, StatementListing.INSTRUCTION));
		statementTable.put("70",   new SPSStatement(StatementClass.IMPERATIVE,  StatementType.INSTRUCTION, SPSData.SystemType.MODEL_2, 70,  3, P_REFERENCE, Q_REFERENCE, StatementListing.INSTRUCTION));
		statementTable.put("71",   new SPSStatement(StatementClass.IMPERATIVE,  StatementType.INSTRUCTION, SPSData.SystemType.ANY,     71,  3, P_REFERENCE, Q_REFERENCE, StatementListing.INSTRUCTION));
		statementTable.put("72",   new SPSStatement(StatementClass.IMPERATIVE,  StatementType.INSTRUCTION, SPSData.SystemType.ANY,     72,  3, P_REF_ODD, Q_REFERENCE, StatementListing.INSTRUCTION));
		statementTable.put("73",   new SPSStatement(StatementClass.IMPERATIVE,  StatementType.INSTRUCTION, SPSData.SystemType.ANY,     73,  3, P_REF_ODD, Q_REFERENCE, StatementListing.INSTRUCTION));
		statementTable.put("90",   new SPSStatement(StatementClass.IMPERATIVE,  StatementType.INSTRUCTION, SPSData.SystemType.MODEL_2, 90,  4, P_REF_EVEN, Q_REF_BIT, StatementListing.INSTRUCTION));
		statementTable.put("91",   new SPSStatement(StatementClass.IMPERATIVE,  StatementType.INSTRUCTION, SPSData.SystemType.MODEL_2, 91,  4, P_REF_EVEN, Q_REF_BIT, StatementListing.INSTRUCTION));
		statementTable.put("92",   new SPSStatement(StatementClass.IMPERATIVE,  StatementType.INSTRUCTION, SPSData.SystemType.MODEL_2, 92,  3, P_REFERENCE, Q_REFERENCE, StatementListing.INSTRUCTION));
		statementTable.put("93",   new SPSStatement(StatementClass.IMPERATIVE,  StatementType.INSTRUCTION, SPSData.SystemType.MODEL_2, 93,  3, P_REFERENCE, Q_REFERENCE, StatementListing.INSTRUCTION));
		statementTable.put("94",   new SPSStatement(StatementClass.IMPERATIVE,  StatementType.INSTRUCTION, SPSData.SystemType.MODEL_2, 94,  3, P_REFERENCE, Q_REFERENCE, StatementListing.INSTRUCTION));
		statementTable.put("95",   new SPSStatement(StatementClass.IMPERATIVE,  StatementType.INSTRUCTION, SPSData.SystemType.MODEL_2, 95,  3, P_REFERENCE, Q_REFERENCE, StatementListing.INSTRUCTION));
		statementTable.put("96",   new SPSStatement(StatementClass.IMPERATIVE,  StatementType.INSTRUCTION, SPSData.SystemType.MODEL_2, 96,  3, P_REFERENCE, Q_REFERENCE, StatementListing.INSTRUCTION));
		statementTable.put("97",   new SPSStatement(StatementClass.IMPERATIVE,  StatementType.INSTRUCTION, SPSData.SystemType.MODEL_2, 97,  3, P_REFERENCE, 100, StatementListing.INSTRUCTION));

	}
	
	public static SPSStatement Get(String operation) {
		return statementTable.get(operation);
	}

	public static SPSStatement Get(Integer opCode) {
		return new SPSStatement(StatementClass.IMPERATIVE, StatementType.INSTRUCTION, SPSData.SystemType.ANY, opCode, 3, P_REFERENCE, Q_REFERENCE, StatementListing.INSTRUCTION);
	}

	public static void Process() {
		int len;
		int lens[] = new int[20];
		int pos;
		int size;
		int last;
		int power;
		int temp;
		
		SPSData.labelAddress = 0;
		SPSData.address = 0;
		SPSData.length = 0;
		SPSData.count = 0;
		SPSData.value = null;
		
		switch (SPSData.inputStatement.Type) {
		
			/*  UNKNOWN  */

			case UNKNOWN:
				// Do nothing
				break;
			
			/*  COMMENT  */

			case COMMENT:
				// Do nothing
				break;
			
			/*  CONTROL  */

			case DEND:
				SPSData.dendStatus = SPSData.DendStatus.SEEN;
				if (!SPSUtility.IsEmpty(SPSData.inputLabel)) {
					SPSOutput.ReportWarning("unexpected label (" + SPSData.inputLabel + ")");
				}
				SPSData.labelAddress = (int)EvalOperand(SPSData.inputOperand[0], SPSData.lastAddress, false, true, StatementCheck.ADDR_EVEN);
				SPSData.address = SPSData.labelAddress;
				SPSData.dendAddress = SPSData.labelAddress;
				break;
			
			case DORG:
				SPSData.labelAddress = (int)EvalOperand(SPSData.inputOperand[0], SPSData.lastAddress, false, true, StatementCheck.ADDRESS);
				SPSData.addressCounter = SPSData.labelAddress;
				SPSData.address = SPSData.labelAddress;
				SPSData.lastAddress = SPSData.labelAddress - 1;
				if (SPSData.pass == 1) {
					SPSSymbol.Add(SPSData.inputLabel, SPSData.labelAddress, SPSData.lineNumber);
				} else {
					SPSSymbol.Verify(SPSData.inputLabel, SPSData.lineNumber);
				}
				break;
			
			case HEAD:
				if (!SPSUtility.IsEmpty(SPSData.inputLabel)) {
					SPSOutput.ReportWarning("unexpected label (" + SPSData.inputLabel + ")");
				}
				if (SPSUtility.IsEmpty(SPSData.inputOperand[0])) {
					SPSData.symbolHead = ' ';
				} else if ((SPSData.inputOperand[0].length() == 1) && SPSUtility.IsValidHead(SPSData.inputOperand[0].charAt(0))) {
					SPSData.symbolHead = SPSData.inputOperand[0].charAt(0);
				} else {
					SPSOutput.ReportError("invalid head (" + SPSData.inputOperand[0] + ")");
				}
				break;
			
			case SEND:
				if (!SPSUtility.IsEmpty(SPSData.inputLabel)) {
					SPSOutput.ReportWarning("unexpected label (" + SPSData.inputLabel + ")");
				}
				SPSOutput.ReportWarning("unsupported control operation (SEND)");
				break;
			
			case TCD:
				if (!SPSUtility.IsEmpty(SPSData.inputLabel)) {
					SPSOutput.ReportWarning("unexpected label (" + SPSData.inputLabel + ")");
				}
				SPSOutput.ReportWarning("unsupported control operation (TCD)");
				break;
			
			case TRA:
				SPSOutput.ReportWarning("unsupported control operation (TRA)");
				break;

			/*  DECLARATIVE  */

			case DAC:
				len = (int)EvalOperand(SPSData.inputOperand[0], SPSData.lastAddress, false, true, StatementCheck.VAL_POSITIVE);
				if (len > 50) {
					SPSOutput.ReportError("length greater than 50");
					len = 50;
				}
				SPSData.length = 2 * len;
				if (SPSUtility.IsEmpty(SPSData.inputOperand[2])) {
					if ((SPSData.addressCounter & 1) == 1) SPSData.addressCounter += 1;
					SPSData.address = SPSData.addressCounter;
					SPSData.labelAddress = SPSData.addressCounter + 1;
					SPSData.addressCounter += 2 * len;
					SPSData.lastAddress = SPSData.addressCounter - ((len > 0) ? 1 : 0);
				} else {
					SPSData.labelAddress = (int)EvalOperand(SPSData.inputOperand[2], SPSData.lastAddress, false, true, StatementCheck.ADDR_ODD);
					SPSData.address = SPSData.labelAddress - ((len > 0) ? 1 : 0);
				}
				if ((SPSData.address + SPSData.length) > SPSData.memorySize) {
					SPSOutput.ReportError("outside memory bounds");
				}

				if (SPSData.pass == 1) {
					SPSSymbol.Add(SPSData.inputLabel, SPSData.labelAddress, SPSData.lineNumber);
				} else {
					SPSSymbol.Verify(SPSData.inputLabel, SPSData.lineNumber);
					if (len > 0) {
						SPSValue.SetAlphaValue(len, SPSData.inputOperand[1]);
						SPSData.value[0] |= SPSData.MASK_FLAG;
					}	
				}
				break;
			
			case DAS:
				len = 2 * (int)EvalOperand(SPSData.inputOperand[0], SPSData.lastAddress, false, true, StatementCheck.VAL_POSITIVE);
				SPSData.length = len;
				if (SPSUtility.IsEmpty(SPSData.inputOperand[1])) {
					if ((SPSData.addressCounter & 1) == 1) SPSData.addressCounter += 1;
					SPSData.address = SPSData.addressCounter;
					SPSData.labelAddress = SPSData.addressCounter + 1;
					SPSData.addressCounter += len;
					SPSData.lastAddress = SPSData.addressCounter - ((len > 0) ? 1 : 0);
				} else {
					SPSData.labelAddress = (int)EvalOperand(SPSData.inputOperand[1], SPSData.lastAddress, false, true, StatementCheck.ADDR_ODD);
					SPSData.address = SPSData.labelAddress - ((len > 0) ? 1 : 0);
				}
				if ((SPSData.address + SPSData.length) > SPSData.memorySize) {
					SPSOutput.ReportError("outside memory bounds");
				}

				if (SPSData.pass == 1) {
					SPSSymbol.Add(SPSData.inputLabel, SPSData.labelAddress, SPSData.lineNumber);
				} else {
					SPSSymbol.Verify(SPSData.inputLabel, SPSData.lineNumber);
				}
				break;
			
			case DC:
				len = (int)EvalOperand(SPSData.inputOperand[0], SPSData.lastAddress, false, true, StatementCheck.VAL_POSITIVE);
				if (len > 50) {
					SPSOutput.ReportError("length greater than 50");
					len = 50;
				}
				SPSData.length = len;
				if (SPSUtility.IsEmpty(SPSData.inputOperand[2])) {
					SPSData.address = SPSData.addressCounter;
					SPSData.addressCounter += len;
					SPSData.lastAddress = SPSData.addressCounter - ((len > 0) ? 1 : 0);
					SPSData.labelAddress = SPSData.lastAddress;
				} else {
					SPSData.labelAddress = (int)EvalOperand(SPSData.inputOperand[2], SPSData.lastAddress, false, true, StatementCheck.ADDRESS);
					SPSData.address = SPSData.labelAddress - ((len > 0) ? len - 1 : 0);
				}
				if ((SPSData.address + SPSData.length) > SPSData.memorySize) {
					SPSOutput.ReportError("outside memory bounds");
				}

				if (SPSData.pass == 1) {
					SPSSymbol.Add(SPSData.inputLabel, SPSData.labelAddress, SPSData.lineNumber);
				} else {
					SPSSymbol.Verify(SPSData.inputLabel, SPSData.lineNumber);
					if (len > 0) SPSValue.SetNumValue(len, false, SPSData.inputOperand[1]);
					if ((len == 1) && (SPSData.value[0] != SPSData.MEMORY_RM)) SPSData.value[0] |= SPSData.MASK_FLAG;
					if (len > 1) SPSData.value[0] |= SPSData.MASK_FLAG;
				}
				break;
			
			case DDA:
				SPSData.length = 14;
				if (SPSUtility.IsEmpty(SPSData.inputOperand[0])) {
					if ((SPSData.addressCounter & 1) == 1) SPSData.addressCounter += 1;
					SPSData.address = SPSData.addressCounter;
					SPSData.labelAddress = SPSData.address;
					SPSData.addressCounter += 14;
					SPSData.lastAddress = SPSData.addressCounter - 1;
				} else {
					SPSData.labelAddress = (int)EvalOperand(SPSData.inputOperand[0], SPSData.lastAddress, false, true, StatementCheck.ADDR_EVEN);
					SPSData.address = SPSData.labelAddress;
				}
				if ((SPSData.address + SPSData.length) > SPSData.memorySize) {
					SPSOutput.ReportError("outside memory bounds");
				}

				if (SPSData.pass == 1) {
					SPSSymbol.Add(SPSData.inputLabel, SPSData.labelAddress, SPSData.lineNumber);
				} else {
					SPSSymbol.Verify(SPSData.inputLabel, SPSData.lineNumber);
					SPSData.value = new byte[14];
					SPSValue.SetField(SPSData.FIELD_DRIVE, 1, false, EvalOperand(SPSData.inputOperand[1], 0, false, false,
																				 StatementCheck.VAL_POSITIVE));
					int sector = (int)EvalOperand(SPSData.inputOperand[2], 0, false, false, StatementCheck.VAL_POSITIVE);
					if (sector > 79999) {
						SPSOutput.ReportError("sector address greater than 79999");
						sector = 79999;
					}
					SPSValue.SetField(SPSData.FIELD_SECTOR, 5, true, sector);
					int count = (int)EvalOperand(SPSData.inputOperand[3], 0, false, false, StatementCheck.VAL_POSITIVE);
					if (count == 0) {
						SPSOutput.ReportError("sector count zero");
						count = 1;
					} else if (count > 200) {
						SPSOutput.ReportError("sector count greater then 200");
						count = 200;
					}
					SPSValue.SetField(SPSData.FIELD_COUNT, 3, true, count);
					SPSValue.SetField(SPSData.FIELD_ADDRESS, 5, true, EvalOperand(SPSData.inputOperand[4], SPSData.lastAddress, false, true,
																				  StatementCheck.ADDR_EVEN));
				}
				break;
			
			case DGM:
				SPSData.length = 1;
				if (SPSUtility.IsEmpty(SPSData.inputOperand[0])) {
					SPSData.address = SPSData.addressCounter;
					SPSData.addressCounter += 1;
					SPSData.lastAddress = SPSData.addressCounter - 1;
					SPSData.labelAddress = SPSData.lastAddress;
				} else {
					SPSData.labelAddress = (int)EvalOperand(SPSData.inputOperand[0], SPSData.lastAddress, false, true, StatementCheck.ADDRESS);
					SPSData.address = SPSData.labelAddress;
				}
				if ((SPSData.address + SPSData.length) > SPSData.memorySize) {
					SPSOutput.ReportError("outside memory bounds");
				}

				if (SPSData.pass == 1) {
					SPSSymbol.Add(SPSData.inputLabel, SPSData.labelAddress, SPSData.lineNumber);
				} else {
					SPSSymbol.Verify(SPSData.inputLabel, SPSData.lineNumber);
					SPSData.value = new byte[1];
					SPSData.value[0] = SPSData.MEMORY_GM;
				}
				break;

			case DNB:
				len = (int)EvalOperand(SPSData.inputOperand[0], SPSData.lastAddress, false, true, StatementCheck.VAL_POSITIVE);
				if (len > 50) {
					SPSOutput.ReportError("length greater than 50");
					len = 50;
				}
				SPSData.length = len;
				if (SPSUtility.IsEmpty(SPSData.inputOperand[1])) {
					SPSData.address = SPSData.addressCounter;
					SPSData.addressCounter += len;
					SPSData.lastAddress = SPSData.addressCounter - ((len > 0) ? 1 : 0);
					SPSData.labelAddress = SPSData.lastAddress;
				} else {
					SPSData.labelAddress = (int)EvalOperand(SPSData.inputOperand[1], SPSData.lastAddress, false, true, StatementCheck.ADDRESS);
					SPSData.address = SPSData.labelAddress - ((len > 0) ? len - 1 : 0);
				}
				if ((SPSData.address + SPSData.length) > SPSData.memorySize) {
					SPSOutput.ReportError("outside memory bounds");
				}

				if (SPSData.pass == 1) {
					SPSSymbol.Add(SPSData.inputLabel, SPSData.labelAddress, SPSData.lineNumber);
				} else {
					SPSSymbol.Verify(SPSData.inputLabel, SPSData.lineNumber);
					if (len > 0) {
						SPSData.value = new byte[len];
						for (int i = 0; i < len; ++i) {
							SPSData.value[i] = SPSData.MEMORY_NB;
						}
					}
				}
				break;
			
			case DOT:
				power = (int)EvalOperand(SPSData.inputOperand[0], SPSData.lastAddress, false, true, StatementCheck.VAL_POSITIVE);
				if (power < 0) {
					SPSOutput.ReportError("power less than 0");
					power = 0;
				} else if (power > 13) {
					SPSOutput.ReportError("power greater than 13");
					power = 13;
				}
				SPSData.length = SPSData.dotLengths[power];
				if (SPSUtility.IsEmpty(SPSData.inputOperand[1])) {
					SPSData.address = SPSData.addressCounter;
					SPSData.addressCounter += SPSData.length;
					SPSData.lastAddress = SPSData.addressCounter - 1;
					SPSData.labelAddress = SPSData.lastAddress;
				} else {
					SPSData.labelAddress = (int)EvalOperand(SPSData.inputOperand[1], SPSData.lastAddress, false, true, StatementCheck.ADDRESS);
					SPSData.address = SPSData.labelAddress - SPSData.length + 1;
				}
				if ((SPSData.address + SPSData.length) > SPSData.memorySize) {
					SPSOutput.ReportError("outside memory bounds");
				}

				if (SPSData.pass == 1) {
					SPSSymbol.Add(SPSData.inputLabel, SPSData.labelAddress, SPSData.lineNumber);
				} else {
					SPSSymbol.Verify(SPSData.inputLabel, SPSData.lineNumber);
					SPSData.value = new byte[SPSData.length];
					pos = SPSData.dotTable.length - SPSData.length;
					for (int i = 0; i < SPSData.length; ++i) {
						SPSData.value[i] = SPSData.dotTable[pos++];
					}
				}
				break;

			case DS:
				len = (int)EvalOperand(SPSData.inputOperand[0], SPSData.lastAddress, false, true, StatementCheck.VAL_POSITIVE);
				SPSData.length = len;
				if (SPSUtility.IsEmpty(SPSData.inputOperand[1])) {
					SPSData.address = SPSData.addressCounter;
					SPSData.addressCounter += len;
					SPSData.lastAddress = SPSData.addressCounter - ((len > 0) ? 1 : 0);
					SPSData.labelAddress = SPSData.lastAddress;
				} else {
					SPSData.labelAddress = (int)EvalOperand(SPSData.inputOperand[1], SPSData.lastAddress, false, true, StatementCheck.ADDRESS);
					SPSData.address = SPSData.labelAddress - ((len > 0) ? len - 1 : 0);
				}
				if ((SPSData.address + SPSData.length) > SPSData.memorySize) {
					SPSOutput.ReportError("outside memory bounds");
				}

				if (SPSData.pass == 1) {
					SPSSymbol.Add(SPSData.inputLabel, SPSData.labelAddress, SPSData.lineNumber);
				} else {
					SPSSymbol.Verify(SPSData.inputLabel, SPSData.lineNumber);
				}
				break;
			
			case DSA:
				last = SPSData.lastAddress;
				len = 5 * SPSData.inputOperand.length;
				SPSData.length = len;
				SPSData.address = SPSData.addressCounter;
				SPSData.labelAddress = SPSData.addressCounter + ((len > 0) ? 4 : 0);
				SPSData.addressCounter += len;
				SPSData.lastAddress = SPSData.addressCounter - ((len > 0) ? 1 : 0);
				if ((SPSData.address + SPSData.length) > SPSData.memorySize) {
					SPSOutput.ReportError("outside memory bounds");
				}

				if (SPSData.pass == 1) {
					SPSSymbol.Add(SPSData.inputLabel, SPSData.labelAddress, SPSData.lineNumber);
				} else {
					SPSSymbol.Verify(SPSData.inputLabel, SPSData.lineNumber);
					SPSData.value = new byte[len];
					pos = 4;
					for (int i = 0; i < SPSData.inputOperand.length; ++i) {
						SPSValue.SetField(pos, 5, true, EvalOperand(SPSData.inputOperand[i], last, SPSData.indexOk, false, StatementCheck.REFERENCE));
						SPSValue.SetIndexFlags(pos);
						pos += 5;
					}
				}
				break;
			
			case DSAC:
				len = (int)EvalOperand(SPSData.inputOperand[0], SPSData.lastAddress, false, true, StatementCheck.VAL_POSITIVE);
				if (len > 50) {
					SPSOutput.ReportError("length greater than 50");
					len = 50;
				}
				SPSData.length = 2 * len;
				if (SPSUtility.IsEmpty(SPSData.inputOperand[2])) {
					if ((SPSData.addressCounter & 1) == 1) SPSData.addressCounter += 1;
					SPSData.address = SPSData.addressCounter;
					SPSData.addressCounter += 2 * len;
					SPSData.lastAddress = SPSData.addressCounter - ((len > 0) ? 1 : 0);
					SPSData.labelAddress = SPSData.lastAddress;
				} else {
					SPSData.labelAddress = (int)EvalOperand(SPSData.inputOperand[2], SPSData.lastAddress, false, true, StatementCheck.ADDR_ODD);
					SPSData.address = SPSData.labelAddress - ((len > 0) ? 2 * len - 1 : 0);
				}
				if ((SPSData.address + SPSData.length) > SPSData.memorySize) {
					SPSOutput.ReportError("outside memory bounds");
				}

				if (SPSData.pass == 1) {
					SPSSymbol.Add(SPSData.inputLabel, SPSData.labelAddress, SPSData.lineNumber);
				} else {
					SPSSymbol.Verify(SPSData.inputLabel, SPSData.lineNumber);
					if (len > 0) {
						SPSValue.SetAlphaValue(len, SPSData.inputOperand[1]);
						SPSData.value[0] |= SPSData.MASK_FLAG;
					}
				}
				break;

			case DSB:
				size = (int)EvalOperand(SPSData.inputOperand[0], SPSData.lastAddress, false, true, StatementCheck.VAL_POSITIVE);
				len =  size * (int)EvalOperand(SPSData.inputOperand[1], SPSData.lastAddress, false, true, StatementCheck.VAL_POSITIVE);
				SPSData.length = len;
				SPSData.address = SPSData.addressCounter;
				if (SPSUtility.IsEmpty(SPSData.inputOperand[2])) {
					SPSData.labelAddress = SPSData.addressCounter + ((len > 0) ? size - 1 : 0);
					SPSData.addressCounter += len;
					SPSData.lastAddress = SPSData.addressCounter - ((len > 0) ? 1 : 0);
				} else {
					SPSData.labelAddress = (int)EvalOperand(SPSData.inputOperand[2], SPSData.lastAddress, false, true, StatementCheck.ADDRESS);
				}
				if ((SPSData.address + SPSData.length) > SPSData.memorySize) {
					SPSOutput.ReportError("outside memory bounds");
				}

				if (SPSData.pass == 1) {
					SPSSymbol.Add(SPSData.inputLabel, SPSData.labelAddress, SPSData.lineNumber);
				} else {
					SPSSymbol.Verify(SPSData.inputLabel, SPSData.lineNumber);
				}
				break;
			
			case DSC:
				len = (int)EvalOperand(SPSData.inputOperand[0], SPSData.lastAddress, false, true, StatementCheck.VAL_POSITIVE);
				if (len > 50) {
					SPSOutput.ReportError("length greater than 50");
					len = 50;
				}
				SPSData.length = len;
				if (SPSUtility.IsEmpty(SPSData.inputOperand[2])) {
					SPSData.labelAddress = SPSData.addressCounter;
					SPSData.address = SPSData.labelAddress;
					SPSData.addressCounter += len;
					SPSData.lastAddress = SPSData.addressCounter - ((len > 0) ? 1 : 0);
				} else {
					SPSData.labelAddress = (int)EvalOperand(SPSData.inputOperand[2], SPSData.lastAddress, false, true, StatementCheck.ADDRESS);
					SPSData.address = SPSData.labelAddress;
				}
				if ((SPSData.address + SPSData.length) > SPSData.memorySize) {
					SPSOutput.ReportError("outside memory bounds");
				}

				if (SPSData.pass == 1) {
					SPSSymbol.Add(SPSData.inputLabel, SPSData.labelAddress, SPSData.lineNumber);
				} else {
					SPSSymbol.Verify(SPSData.inputLabel, SPSData.lineNumber);
					if (len > 0) SPSValue.SetNumValue(len, true, SPSData.inputOperand[1]);
				}
				break;
			
			case DSS:
				len = (int)EvalOperand(SPSData.inputOperand[0], SPSData.lastAddress, false, true, StatementCheck.VAL_POSITIVE);
				SPSData.length = len;
				if (SPSUtility.IsEmpty(SPSData.inputOperand[1])) {
					SPSData.labelAddress = SPSData.addressCounter;
					SPSData.address = SPSData.labelAddress;
					SPSData.addressCounter += len;
					SPSData.lastAddress = SPSData.addressCounter - ((len > 0) ? 1 : 0);
				} else {
					SPSData.labelAddress = (int)EvalOperand(SPSData.inputOperand[1], SPSData.lastAddress, false, true, StatementCheck.ADDRESS);
					SPSData.address = SPSData.labelAddress;
				}
				if ((SPSData.address + SPSData.length) > SPSData.memorySize) {
					SPSOutput.ReportError("outside memory bounds");
				}

				if (SPSData.pass == 1) {
					SPSSymbol.Add(SPSData.inputLabel, SPSData.labelAddress, SPSData.lineNumber);
				} else {
					SPSSymbol.Verify(SPSData.inputLabel, SPSData.lineNumber);
				}
				break;
			
			case DVLC:
				len = 0;
				for (int i = 1, j = 0; i < 40; i += 2) {
					temp = (int)EvalOperand(SPSData.inputOperand[i], SPSData.lastAddress, false, true, StatementCheck.VAL_POSITIVE);
					lens[j] = temp;
					len += temp;
					++j;
				}
				if (len > 50) {
					SPSOutput.ReportError("total length greater than 50");
					len = 50;
				}
				SPSData.length = len;
				if (SPSUtility.IsEmpty(SPSData.inputOperand[0])) {
					SPSData.address = SPSData.addressCounter;
					SPSData.addressCounter += len;
					SPSData.lastAddress = SPSData.addressCounter - ((len > 0) ? 1 : 0);
					SPSData.labelAddress = SPSData.address + lens[0] - ((lens[0] > 0) ? 1 : 0);
				} else {
					SPSData.labelAddress = (int)EvalOperand(SPSData.inputOperand[0], SPSData.lastAddress, false, true, StatementCheck.ADDRESS);
					SPSData.address = SPSData.labelAddress - ((lens[0] > 0) ? lens[0] - 1 : 0);
				}
				if ((SPSData.address + SPSData.length) > SPSData.memorySize) {
					SPSOutput.ReportError("outside memory bounds");
				}

				if (SPSData.pass == 1) {
					SPSSymbol.Add(SPSData.inputLabel, SPSData.labelAddress, SPSData.lineNumber);
				} else {
					SPSSymbol.Verify(SPSData.inputLabel, SPSData.lineNumber);
					SPSData.value = new byte[len];
					if (len > 0) {
						pos = -1;
						for (int i = 2, j = 0; i < 40; i += 2) {
							pos += lens[j];
							SPSValue.SetField(pos, lens[j], true, EvalOperand(SPSData.inputOperand[i], 0, false, false, StatementCheck.VAL_DOUBLE));
							++j;
						}
					}
				}
				break;
			

			/*  IMPERATIVE  */

			case INSTRUCTION:
				if ((SPSData.addressCounter & 1) == 1) SPSData.addressCounter += 1;
				SPSData.labelAddress = SPSData.addressCounter;
				SPSData.address = SPSData.addressCounter;
				if ((SPSData.inputStatement.OpCode == 42) && (SPSData.inputOperation.equals("BB2"))) {
					SPSData.length = 2;
				} else if ((SPSData.inputStatement.OpCode == 49) && (SPSData.inputOperation.equals("B7"))) {
					SPSData.length = 7;
				} else {
					SPSData.length = 12;
				}
				SPSData.addressCounter += SPSData.length;
				SPSData.lastAddress = SPSData.addressCounter - 1;
				if ((SPSData.address + SPSData.length) > SPSData.memorySize) {
					SPSOutput.ReportError("outside memory bounds");
				}

				if (SPSData.pass == 1) {
					SPSSymbol.Add(SPSData.inputLabel, SPSData.labelAddress, SPSData.lineNumber);
				} else {
					SPSSymbol.Verify(SPSData.inputLabel, SPSData.lineNumber);
					SPSData.value = new byte[12];
					SPSValue.SetField(SPSData.FIELD_OP, 2, false, SPSData.inputStatement.OpCode);
					SPSValue.SetField(SPSData.FIELD_P, 5, false, EvalPOperand());
					SPSValue.SetIndexFlags(SPSData.FIELD_P);
					if ((SPSData.inputStatement.QField == Q_IMMEDIATE) && SPSUtility.IsEmpty(SPSData.inputOperand[2])) {
						SPSValue.SetField(SPSData.FIELD_Q, 5, true, EvalQOperand());
					} else if (SPSData.inputStatement.QField == Q_IMM_INDEX) {
						SPSValue.SetField(SPSData.FIELD_Q, 5, true, EvalQOperand());
						SPSValue.SetIndexFlags(SPSData.FIELD_Q);
					} else {
						SPSValue.SetField(SPSData.FIELD_Q, 5, false, EvalQOperand());
						SPSValue.SetIndexFlags(SPSData.FIELD_Q);
					}
					if (SPSData.inputStatement.QField != Q_REF_BIT) {
						SPSValue.SetFlags(SPSData.inputOperand[2]);
					} else {
						SPSValue.SetFlags(SPSData.inputOperand[3]);
					}
				}
				break;
		}
	}

	public static long EvalOperand(String operand, int asterisk, boolean indexOk, boolean symbolCheck, StatementCheck valueCheck) {
		int errs = SPSData.errorCount;
		int pos = 0;
		int last = -1;
		boolean idx = false;
		int size = operand.length();
		char chr = 0;
		String term = "";
		long[] expr = new long[SPSData.SIZE_EXPR_STACK];
		
		SPSData.operandIndex = 0;
		
		if (SPSUtility.IsEmpty(operand)) return 0L;
		
		if (operand.charAt(0) == '-') {
			expr[++last] = 0L;
			expr[++last] = '-';
			++pos;
			if (pos == size) return 0L;
		}
		
		while (true) {
			term = Character.toString(operand.charAt(pos++));
			while (pos < size) {
				chr = operand.charAt(pos);
				if ((chr == '+') || (chr == '-') || (chr == '*') || ((chr == '/') && !SPSData.symbolDivide)) break;
				if (chr == '(') {
					idx = true;
					break;
				}
				term += chr;
				++pos;
			}
			if (term.equals("*")) {
				expr[++last] = asterisk;
			} else if (SPSUtility.IsValidNumber(term)) {
				expr[++last] = Long.parseLong(term);
			} else if (SPSUtility.IsValidSymbol(term)){
				expr[++last] = SPSSymbol.Lookup(term, SPSData.lineNumber, symbolCheck);
			} else {
				SPSOutput.ReportError("invalid operand (" + operand + ")");
				return 0L;
			}
			if ((pos == size) || idx) break;
			
			expr[++last] = chr;
			++pos;
			if (pos == size) break;
		}
		
		if (idx & !indexOk) {
			SPSOutput.ReportError("indexing not supported " + SPSUtility.Substring(operand, pos, size));
		}
		
		if ((last & 0x1) == 1) {
			SPSOutput.ReportError("invalid operand (" + operand + ")");
			return 0L;
		}

		int i = 1;
		while (i < last) {
			if (expr[i] == '*') {
				expr[i - 1] *= expr[i + 1];
				for (int j = i + 2; j <= last; ++j) expr[j - 2] = expr[j];
				last -= 2;
			} else if (expr[i] == '/') {
				if (expr[i + 1] == 0) {
					SPSOutput.ReportError("divide by zero");
					return 0L;
				}
				expr[i - 1] /= expr[i + 1];
				for (int j = i + 2; j <= last; ++j) expr[j - 2] = expr[j];
				last -= 2;
			} else {
				i += 2;
			}
		}
		
		for (int ii = 1; ii < last; ii += 2) {
			if (expr[ii] == '+') {
				expr[0] += expr[ii + 1];
			} else if (expr[ii] == '-'){
				expr[0] -= expr[ii + 1];
			}
		}
		
		switch (valueCheck) {

			case ADDRESS:

				if (expr[0] < 0L) {
					SPSOutput.ReportError("address must be positive (" + expr[0] + ")");
					expr[0] = -expr[0];
				}
				if (expr[0] >= SPSData.memorySize) {
					SPSOutput.ReportError("address too large (" + expr[0] + ")");
					expr[0] = 0L;
				}
				break;

			case ADDR_EVEN:

				if (expr[0] < 0L) {
					SPSOutput.ReportError("address must be positive (" + expr[0] + ")");
					expr[0] = -expr[0];
				}
				if ((expr[0] & 1L) != 0L) {
					SPSOutput.ReportError("address must be even (" + expr[0] + ")");
					expr[0] += 1L;
				}
				if (expr[0] >= SPSData.memorySize) {
					SPSOutput.ReportError("address too large (" + expr[0] + ")");
					expr[0] = 0L;
				}
				break;

			case ADDR_ODD:

				if (expr[0] < 0L) {
					SPSOutput.ReportError("address must be positive (" + expr[0] + ")");
					expr[0] = -expr[0];
				}
				if ((expr[0] & 1L) != 1L) {
					SPSOutput.ReportError("address must be odd (" + expr[0] + ")");
					expr[0] += 1L;
				}
				if (expr[0] >= SPSData.memorySize) {
					SPSOutput.ReportError("address too large (" + expr[0] + ")");
					expr[0] = 1L;
				}
				break;

			case REFERENCE:

				if (Math.abs(expr[0]) >= SPSData.memorySize) {
					SPSOutput.ReportWarning("address too large (" + expr[0] + ")");
				}
				break;

			case REF_EVEN:

				if ((expr[0] >= 0L) && (expr[0] & 1L) != 0L) {
					SPSOutput.ReportWarning("address must be even (" + expr[0] + ")");
				}
				if (Math.abs(expr[0]) >= SPSData.memorySize) {
					SPSOutput.ReportWarning("address too large (" + expr[0] + ")");
				}
				break;

			case REF_ODD:

				if ((expr[0] >= 0L) && (expr[0] & 1L) != 1L) {
					SPSOutput.ReportWarning("address must be odd (" + expr[0] + ")");
				}
				if (Math.abs(expr[0]) >= SPSData.memorySize) {
					SPSOutput.ReportWarning("address too large (" + expr[0] + ")");
				}
				break;

			case VALUE:

				if (Math.abs(expr[0]) > 99999L) {
					SPSOutput.ReportError("value too large (" + expr[0] + ")");
					expr[0] = 0L;
				}
				break;

			case VAL_POSITIVE:

				if (expr[0] < 0L) {
					SPSOutput.ReportError("value must be positive (" + expr[0] + ")");
					expr[0] = -expr[0];
				}
				if (Math.abs(expr[0]) > 99999L) {
					SPSOutput.ReportError("value too large (" + expr[0] + ")");
					expr[0] = 0L;
				}
				break;

			case VAL_DOUBLE:

				if (Math.abs(expr[0]) > 9999999999L) {
					SPSOutput.ReportError("value too large (" + expr[0] + ")");
					expr[0] = 0L;
				}
				break;

		}
		
		if (idx && indexOk) {
			String str = SPSUtility.Substring(operand, pos, size);
			if (SPSUtility.IsValidIndex(str))  {
				SPSData.operandIndex = SPSUtility.CharAt(operand, size - 2) - '0';
			} else {
				SPSOutput.ReportError("invalid index " + str);
			}
		}
		
		return expr[0];
	}

	public static long EvalPOperand() {
		long p = 0L;
		
		switch (SPSData.inputStatement.PField) {

			case P_NONE:
				if (!SPSUtility.IsEmpty(SPSData.inputOperand[0])) {
					SPSOutput.ReportWarning("P operand is ignored (" + SPSData.inputOperand[0] + ")");
				}
				break;

			case P_REFERENCE:
				p = EvalOperand(SPSData.inputOperand[0], SPSData.address, SPSData.indexOk, false, StatementCheck.REFERENCE);
				break;

			case P_REF_EVEN:
				if (!(SPSData.inputStatement.QField == Q_REF_BIT)) {
					if (SPSUtility.Contains(SPSData.inputOperand[2], "6")) {
						p = EvalOperand(SPSData.inputOperand[0], SPSData.address, SPSData.indexOk, false, StatementCheck.REFERENCE);
					} else {
						p = EvalOperand(SPSData.inputOperand[0], SPSData.address, SPSData.indexOk, false, StatementCheck.REF_EVEN);
					}
				} else {
					if (SPSUtility.Contains(SPSData.inputOperand[3], "6")) {
						p = EvalOperand(SPSData.inputOperand[0], SPSData.address, SPSData.indexOk, false, StatementCheck.REFERENCE);
					} else {
						p = EvalOperand(SPSData.inputOperand[0], SPSData.address, SPSData.indexOk, false, StatementCheck.REF_EVEN);
					}
				}
				break;

			case P_REF_ODD:
				if (!(SPSData.inputStatement.QField == Q_REF_BIT)) {
					if (SPSUtility.Contains(SPSData.inputOperand[2], "6")) {
						p = EvalOperand(SPSData.inputOperand[0], SPSData.address, SPSData.indexOk, false, StatementCheck.REFERENCE);
					} else {
						p = EvalOperand(SPSData.inputOperand[0], SPSData.address, SPSData.indexOk, false, StatementCheck.REF_ODD);
					}
				} else {
					if (SPSUtility.Contains(SPSData.inputOperand[3], "6")) {
						p = EvalOperand(SPSData.inputOperand[0], SPSData.address, SPSData.indexOk, false, StatementCheck.REFERENCE);
					} else {
						p = EvalOperand(SPSData.inputOperand[0], SPSData.address, SPSData.indexOk, false, StatementCheck.REF_ODD);
					}
				}
				break;

			case P_VALUE:
				p = EvalOperand(SPSData.inputOperand[0], SPSData.address, SPSData.indexOk, false, StatementCheck.VALUE);
				break;
		}

		return p;
	}

	public static long EvalQOperand() {
		int q;
		int q2;
		int d;
		
		switch (SPSData.inputStatement.QField) {

			case Q_NONE:
				if (!SPSUtility.IsEmpty(SPSData.inputOperand[1])) {
					SPSOutput.ReportWarning("Q operand is ignored (" + SPSData.inputOperand[1] + ")");
				}
				break;

			case Q_REFERENCE:
				return EvalOperand(SPSData.inputOperand[1], SPSData.address, SPSData.indexOk, false, StatementCheck.REFERENCE);

			case Q_REF_BIT:
				q = (int)EvalOperand(SPSData.inputOperand[1], SPSData.address, false, false, StatementCheck.NONE);
				d = (int)EvalOperand(SPSData.inputOperand[2], 0, false, false, StatementCheck.VAL_POSITIVE) % 10;
				q2 = Math.abs(q);
				if (q2 < 10000) {
					return (long)(((q >= 0) ? 1: -1) * (10000 * d + q2));
				} else {
					SPSOutput.ReportError("address greater than 9999 (" + q + ")");
				}
				break;

			case Q_IMMEDIATE:
				return EvalOperand(SPSData.inputOperand[1], SPSData.address, false, false, StatementCheck.VALUE);

			case Q_IMM_INDEX:
				return EvalOperand(SPSData.inputOperand[1], SPSData.address, SPSData.indexOk, false, StatementCheck.VALUE);

			case Q_VALUE:
				return EvalOperand(SPSData.inputOperand[1], SPSData.address, SPSData.indexOk, false, StatementCheck.VALUE);
		
			case Q_SKIP:
				q = (int)EvalOperand(SPSData.inputOperand[1], SPSData.address, false, false, StatementCheck.NONE);
				if ((q >= 1) && (q <= 12)) {
					return (long)SPSData.skipQField[q - 1];
				} else {
					SPSOutput.ReportError("skip not in range 1 - 12 (" + q + ")");
				}
				break;
				
			case Q_SKAP:
				q = (int)EvalOperand(SPSData.inputOperand[1], SPSData.address, false, false, StatementCheck.NONE);
				if ((q >= 1) && (q <= 12)) {
					return (long)SPSData.skapQField[q - 1];
				} else {
					SPSOutput.ReportError("skip not in range 1 - 12 (" + q + ")");
				}
				break;
				
			case Q_SPIM:
				q = (int)EvalOperand(SPSData.inputOperand[1], SPSData.address, false, false, StatementCheck.NONE);
				if ((q >= 1) && (q <= 3)) {
					return (long)SPSData.spimQField[q - 1];
				} else {
					SPSOutput.ReportError("space not in range 1 - 3 (" + q + ")");
				}
				break;
				
			case Q_SPAP:
				q = (int)EvalOperand(SPSData.inputOperand[1], SPSData.address, false, false, StatementCheck.NONE);
				if ((q >= 1) && (q <= 3)) {
					return (long)SPSData.spapQField[q - 1];
				} else {
					SPSOutput.ReportError("space not in range 1 - 13 (" + q + ")");
				}
				break;

			default:
				if (!SPSUtility.IsEmpty(SPSData.inputOperand[1])) {
					SPSOutput.ReportWarning("Q operand overrides default(" + SPSData.inputOperand[1] + ")");
					return EvalOperand(SPSData.inputOperand[1], SPSData.address, false, false, StatementCheck.NONE);
				} else {
					return SPSData.inputStatement.QField;
				}
		}
		
		return 0L;
	}
}
