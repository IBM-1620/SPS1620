package SPS1620;

/*
 *  SPSMain.java - main program and command-line processing
 *
 *  IBM 1620 Jr Project, Computer History Museum, 2017-2023
 *
 *  To recreate the experience (visual, auditory, tactile, visceral) of running historic software on a 1960s-era computer.
 *
 *    Dave Babcock     - project lead, software, library
 *    John M. Bohn Jr. - software
 *    David Brock      - CHM sponsor
 *    Steve Casner     - hardware, software
 *    Joe Fredrick     - hardware, firmware
 *    Len Shustek      - CHM advisor
 *    Dag Spicer       - CHM advisor
 *    David Wise       - IBM 1620 expert
 *
 *  Revision History:
 *
 *     1.00   9/29/2023   DJB   Initial release version.
 *     1.01  10/23/2023   DJB   Correct indirect addresses in DSA.
 *     1.02  10/31/2023   DJB   Correct flagged zero in DSC and add ']' to DAC.
 *     1.03  12/15/2023   DJB   Correct zero-length data abort.
 */

public class SPSMain {
	
	// Methods
	
	public static void main(String[] args) {
		String base;
		
		System.out.println("IBM 1620 Jr. SPS Assembler (v" + SPSData.VERSION + ")");

		SPSStatement.Initialize();

		ParseCommandLine(args);

		SPSData.sourceMultipleFiles = SPSData.sourceFileCount > 1;

		if (SPSData.sourceFileCount == 0) {
			SPSOutput.ReportError("no source file");
			System.exit(1);
		}
		for (int i = 0; i < SPSData.sourceFileCount; ++i) {
			if (!SPSData.sourceFilenames[i].endsWith(".sps")) {
				SPSData.sourceFilenames[i] += ".sps";
			}
		}
		base = SPSData.sourceFilenames[0].substring(0, SPSData.sourceFilenames[0].length() - 4);

		if (SPSData.lstOption && (SPSData.lstFilename == null)) {	
			SPSData.lstFilename = base + ".lst";
		}

		if (SPSData.cmemOption && (SPSData.cmemFilename == null)) {	
			SPSData.cmemFilename = base + ".cmem";
		}

		if (SPSData.crdOption && (SPSData.crdFilename == null)) {	
			SPSData.crdFilename = base + ".crd";
		}

		if (SPSData.ptOption && (SPSData.ptFilename == null)) {	
			SPSData.ptFilename = base + ".pt";
		}

		if (SPSData.sourceFileCount == 1) {
			System.out.println("\nSource file:  " + SPSData.sourceFilenames[0]);
		} else {
			System.out.println("\nSource files: 1. " + SPSData.sourceFilenames[0]);
			for (int i = 1; i < SPSData.sourceFileCount; ++i) {
				System.out.println("              " + (i + 1) + ". " + SPSData.sourceFilenames[i]);
			}
			System.out.println("");
		}
		System.out.println("Lst file:     " + (SPSData.lstOption ? SPSData.lstFilename : "<none>"));
		System.out.println("Cmem file:    " + (SPSData.cmemOption ? SPSData.cmemFilename : "<none>"));
		System.out.println("Crd file:     " + (SPSData.crdOption ? SPSData.crdFilename : "<none>"));
		System.out.println("Pt file:      " + (SPSData.ptOption ? SPSData.ptFilename : "<none>") + "\n");

		System.out.println("Source format:       " + ((SPSData.sourceFormat == SPSData.SourceFormat.FIXED) ? "fixed" : "freeform"));
		System.out.println("System type:         " + ((SPSData.systemType == SPSData.SystemType.MODEL_1) ? "model 1" : "model 2"));
		System.out.println("Memory size:         " + SPSData.memorySize);
		System.out.println("Include tables:      " + (SPSData.includeTables ? "yes" : "no"));
		System.out.println("Load halt:           " + (SPSData.loadHalt ? "yes" : "no"));
		System.out.println("Produce warnings:    " + (SPSData.produceWarnings ? "yes" : "no"));
		System.out.println("Print pass 1 errors: " + (SPSData.pass1Errors ? "yes" : "no"));
		System.out.println("Symbol divide:       " + (SPSData.symbolDivide ? "yes" : "no"));
		System.out.print  ("Tab stops:           ");
			if (SPSData.sourceFormat == SPSData.SourceFormat.FIXED) {
				System.out.println("n/a");
			} else {
				for (int i = 0; i < SPSData.tabTable.length; ++i) {
					if (i > 0) System.out.print(",");
					System.out.print(SPSData.tabTable[i]);
				}
				System.out.println("");
			}
		System.out.println("");

		if (SPSData.errorCount == 0) {
			
			SPSPasses.Pass1();
			System.out.println("\nEnd of Pass 1, " + SPSData.lineCount + " lines, " + SPSData.errorCount + " error(s), " +
							   SPSData.warningCount + " warning(s)");

			SPSPasses.Pass2();
			System.out.println("\nEnd of Pass 2, " + SPSData.lineCount + " lines, " + SPSData.errorCount + " error(s), " +
							   SPSData.warningCount + " warning(s)");
		}

		System.out.println("\nEnd of assembly");
	}
	
	static void ParseCommandLine(String[] args) {
		String tabs = null;
		
		for (String arg: args) {
			int len = arg.length();
			String larg = arg.toLowerCase();
			if ((len == 6) && larg.equals("-fixed")) {
				SPSData.sourceFormat = SPSData.SourceFormat.FIXED;
			} else if ((len == 9) && larg.equals("-freeform")) {
				SPSData.sourceFormat = SPSData.SourceFormat.FREEFORM;
				
			} else if ((len == 7) && larg.equals("-model1")) {
				SPSData.systemType = SPSData.SystemType.MODEL_1;
				SPSData.indexOk = false;
			} else if ((len == 7) && larg.equals("-model2")) {
				SPSData.systemType = SPSData.SystemType.MODEL_2;
				SPSData.indexOk = true;
				
			} else if ((len == 3) && arg.equals("-20")) {
				SPSData.memorySize = 20000;
			} else if ((len == 3) && arg.equals("-40")) {
				SPSData.memorySize = 40000;
			} else if ((len == 3) && arg.equals("-60")) {
				SPSData.memorySize = 60000;
			} else if ((len == 3) && arg.equals("-80")) {
				SPSData.memorySize = 80000;
			} else if ((len == 4) && arg.equals("-100")) {
				SPSData.memorySize = 100000;
				
			} else if ((len == 6) && larg.equals("-nolst")) {
				SPSData.lstOption = false;
				SPSData.lstFilename = null;
			} else if ((len == 4) && larg.equals("-lst")) {
				SPSData.lstOption = true;
			} else if ((len > 5) && larg.startsWith("-lst=")) {
				SPSData.lstOption = true;
				SPSData.lstFilename = arg.substring(5);	
				
			} else if ((len == 7) && larg.equals("-nocmem")) {
				SPSData.cmemOption = false;
				SPSData.cmemFilename = null;
			} else if ((len == 5) && larg.equals("-cmem")) {
				SPSData.cmemOption = true;
			} else if ((len > 6) && larg.startsWith("-cmem=")) {
				SPSData.cmemOption = true;
				SPSData.cmemFilename = arg.substring(6);
				
			} else if ((len == 6) && larg.equals("-nocrd")) {
				SPSData.crdOption = false;
				SPSData.crdFilename = null;
			} else if ((len == 4) && larg.equals("-crd")) {
				SPSData.crdOption = true;
			} else if ((len > 5) && larg.startsWith("-crd=")) {
				SPSData.crdOption = true;
				SPSData.crdFilename = arg.substring(5);
				
			} else if ((len == 5) && larg.equals("-nopt")) {
				SPSData.ptOption = false;
				SPSData.ptFilename = null;
			} else if ((len == 3) && larg.equals("-pt")) {
				SPSData.ptOption = true;
			} else if ((len > 4) && larg.startsWith("-pt=")) {
				SPSData.ptOption = true;
				SPSData.ptFilename = arg.substring(4);
				
			} else if ((len == 7) && larg.equals("-tables")) {
				SPSData.includeTables = true;
			} else if ((len == 9) && larg.equals("-notables")) {
				SPSData.includeTables = false;
				
			} else if ((len == 5) && larg.equals("-halt")) {
				SPSData.loadHalt = true;
			} else if ((len == 7) && larg.equals("-nohalt")) {
				SPSData.loadHalt = false;
				
			} else if ((len == 5) && larg.equals("-warn")) {
				SPSData.produceWarnings = true;
			} else if ((len == 7) && larg.equals("-nowarn")) {
				SPSData.produceWarnings = false;
				
			} else if ((len == 12) && larg.equals("-pass1errors")) {
				SPSData.pass1Errors = true;
			} else if ((len == 14) && larg.equals("-nopass1errors")) {
				SPSData.pass1Errors = false;
				
			} else if ((len == 13) && larg.equals("-symboldivide")) {
				SPSData.symbolDivide = true;
			} else if ((len == 15) && larg.equals("-nosymboldivide")) {
				SPSData.symbolDivide = false;
				
			} else if ((len > 6) && larg.startsWith("-tabs=")) {
				tabs = arg.substring(6);
			
			} else if ((len == 5) && larg.equals("-help")) {
				PrintHelp();
				System.exit(0);
				
			} else if ((len > 0) && (arg.charAt(0) == '-')) {
				SPSOutput.ReportError("invalid option (" + arg + ")");
				
			} else {
				if (SPSData.sourceFileCount == (SPSData.SIZE_SOURCE_TABLE - 1)) {
					SPSOutput.ReportError("too many source files");
					System.exit(0);
				}
				SPSData.sourceFilenames[SPSData.sourceFileCount++] = arg;
			}
		}
		
		if (tabs != null) {
			if (SPSData.sourceFormat == SPSData.SourceFormat.FIXED) {
				SPSOutput.ReportWarning("-tabs option only valid with -freeform");
			} else {
				ProcessTabs(tabs);
			}
		} else if (SPSData.sourceFormat == SPSData.SourceFormat.FREEFORM) {
			SPSData.tabTable = new byte[1];
			SPSData.tabTable[0] = SPSData.DEFAULT_TABS;
		}
	}
	
	static void ProcessTabs(String tabs) {
		String[] temp = SPSData.PATTERN_SPLIT.split(tabs, -1);
		int size = temp.length;
		int last = 0;
		
		SPSData.tabTable = new byte[size];
		for (int i = 0; i < size; ++i) {
			if (SPSUtility.IsValidNumber(temp[i])) {
				int val = Integer.parseInt(temp[i]);
				if ((val >= 1) && (val <= SPSData.SIZE_LINE) && (val > last)) {
					SPSData.tabTable[i] = (byte)val;
					last = val;
				} else {
					SPSOutput.ReportError("invalid tabs value (" + tabs + ")");
					SPSData.tabTable = new byte[1];
					SPSData.tabTable[0] = SPSData.DEFAULT_TABS;
					break;
				}
			} else {
				SPSOutput.ReportError("invalid tabs value (" + tabs + ")");
				SPSData.tabTable = new byte[1];
				SPSData.tabTable[0] = SPSData.DEFAULT_TABS;
				break;
			}
		}
	}
	
	static void PrintHelp() {
		
		System.out.println("Command line: SPS1620 [options] <sourcefile1>[.sps] ...\n");
	
		System.out.println("Options:\n");

		System.out.println("  -lst               - listing file <sourcefile1>.lst [default]");
		System.out.println("  -lst=<filename>    - listing file");
		System.out.println("  -nolst             - no listing file\n");

		System.out.println("  -cmem              - output cmem file <sourcefile1>.cmem");
		System.out.println("  -cmem=<filename>   - output cmem file");
		System.out.println("  -nocmem            - no cmem file [default]\n");

		System.out.println("  -crd               - output crd file <sourcefile1>.crd");
		System.out.println("  -crd=<filename>    - output crd file");
		System.out.println("  -nocrd             - no crd file [default]\n");

		System.out.println("  -pt                - output pt file <sourcefile1>.pt");
		System.out.println("  -pt=<filename>     - output pt file");
		System.out.println("  -nopt              - no pt file [default]\n");
		
		System.out.println("  -fixed             - strict columns (6,12,16) source file [default]");
		System.out.println("  -freeform          - freeform source file\n");
		
		System.out.println("  -model1            - IBM 1620 model 1 [default]");
		System.out.println("  -model2            - IBM 1620 model 2\n");
		
		System.out.println("  -20                - 20,000 memory size");
		System.out.println("  -40                - 40,000 memory size");
		System.out.println("  -60                - 60,000 memory size [default]\n");
		
		System.out.println("  -tables            - include arithmetic table(s) [default]");
		System.out.println("  -notables          - do not include arithmetic table(s)\n");
		
		System.out.println("  -halt              - generate a halt instruction at end of load [default]");
		System.out.println("  -nohalt            - do not generate a halt at end of load\n");
		
		System.out.println("  -warn              - produce warning messages [default]");
		System.out.println("  -nowarn            - do not produce warning messages\n");
		
		System.out.println("  -pass1errors       - print pass 1 errors");
		System.out.println("  -nopass1errors     - do not print pass 1 errors [default]\n");

		System.out.println("  -symboldivide      - allow / in symbols, no divide in expressions [default]");
		System.out.println("  -nosymboldivide    - do not allow / in symbols, divide allowed in expressions\n");
		
		System.out.println("  -tabs=<n>          - set tabs to n spaces [default = " + SPSData.DEFAULT_TABS + "]");
		System.out.println("  -tabs=<n1,n2,...>  - set tabs at columns n1, n2, ...\n");
	
		System.out.println("  -help              - this command help\n");
	}
}
