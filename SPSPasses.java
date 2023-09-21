package SPS1620;

/*
 *  SPSPasses.java - pass 1 & 2 processing
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

public class SPSPasses {
	
	// Methods
	
	public static void Pass1() {
		
		SPSData.pass = 1;
		SPSData.sourceCurrentFile = -1;
		SPSData.lineCount = 0;
		SPSData.errorCount = 0;
		SPSData.warningCount = 0;
		SPSData.addressCounter = 402;
		SPSData.lastAddress = 0;
		SPSData.symbolHead = ' ';
		SPSData.dendStatus = SPSData.DendStatus.NOT_SEEN;
		
		SPSInput.OpenSource();
		
		while (SPSInput.ReadSource()) {
			
			if (SPSData.inputStatement.Class == SPSStatement.StatementClass.COMMENT) {
				continue;
			} else if (SPSData.dendStatus == SPSData.DendStatus.WARNING) {
				continue;
			} else if (SPSData.dendStatus == SPSData.DendStatus.SEEN) {
				SPSOutput.ReportError("statement(s) beyond DEND");
				SPSData.dendStatus = SPSData.DendStatus.WARNING;
				continue;
			}
			
			SPSStatement.Process();
		}
		
		if (SPSData.dendStatus == SPSData.DendStatus.NOT_SEEN) {
			SPSOutput.ReportError("DEND statement missing");
		}
			
		SPSInput.CloseSource();
	}
	
	public static void Pass2() {
		
		SPSData.pass = 2;
		SPSData.sourceCurrentFile = -1;
		SPSData.lineCount = 0;
		SPSData.errorCount = 0;
		SPSData.warningCount = 0;
		SPSData.addressCounter = 402;
		SPSData.lastAddress = 0;
		SPSData.symbolHead = ' ';
		SPSData.dendStatus = SPSData.DendStatus.NOT_SEEN;
		
		for (int i = 0; i < SPSData.memorySize; ++i) {
			SPSData.memory[i] = SPSData.MEMORY_UNDEF;
		}
		
		SPSData.address = 0;
		SPSData.value = SPSData.INIT_NOP;
		SPSValue.StoreValue();
		SPSData.address = 12;
		if (SPSData.loadHalt) {
		    SPSData.value = SPSData.INIT_H;
		} else {
		    SPSData.value = SPSData.INIT_NOP;
		}
		SPSValue.StoreValue();
		SPSData.address = 24;
		SPSData.value = SPSData.INIT_B;
		SPSValue.SetField(SPSData.FIELD_P, 5, false, SPSData.dendAddress);
		SPSValue.StoreValue();
		
		if (SPSData.includeTables) {
			for (int i = 0; i < SPSData.multiplyTable.length; ++i) {
				SPSData.memory[i + 100] = SPSData.multiplyTable[i];
			}
			if (SPSData.systemType == SPSData.SystemType.MODEL_1) {
				for (int i = 0; i < SPSData.addTable.length; ++i) {
					SPSData.memory[i + 300] = SPSData.addTable[i];
				}
			}
			SPSData.memory[400] = SPSData.MEMORY_RM;
		}
		
		if (SPSData.lstOption) {
			SPSOutput.OpenList();
			SPSData.lstWriter.write("IBM 1620 Jr. SPS Assembler (v" + SPSData.VERSION + ")    Source: " + SPSData.sourceFilenames[0] +
									((SPSData.sourceFileCount == 1) ? "" : ",...") + "    Assembled: " + SPSData.dateTime + "\n\n");
		}
		
		if (SPSData.cmemOption) { 
			SPSOutput.OpenCmem();
			SPSData.cmemWriter.write("// IBM 1620 Jr. SPS Assembler (v" + SPSData.VERSION + ")\n");
			SPSData.cmemWriter.write("// Source: " + SPSData.sourceFilenames[0] + ((SPSData.sourceFileCount == 1) ? "" : ",...") + "\n");
			SPSData.cmemWriter.write("// Assembled: " + SPSData.dateTime + "\n\n");
			SPSOutput.WriteLowCore();
		}
		
		SPSInput.OpenSource();
		
		while (SPSInput.ReadSource()) {
			
			if (SPSData.inputStatement.Class == SPSStatement.StatementClass.COMMENT) {
				SPSOutput.WriteList();
				if (SPSData.errorCount == 0) SPSOutput.WriteCmem();
				continue;
			} else if (SPSData.dendStatus == SPSData.DendStatus.WARNING) {
				SPSData.inputStatement.Listing = SPSStatement.StatementListing.UNKNOWN;
				SPSOutput.WriteList();
				continue;
			} else if (SPSData.dendStatus == SPSData.DendStatus.SEEN) {
				SPSOutput.ReportError("statement(s) beyond DEND");
				SPSData.inputStatement.Listing = SPSStatement.StatementListing.UNKNOWN;
				SPSOutput.WriteList();
				SPSData.dendStatus = SPSData.DendStatus.WARNING;
				continue;
			}
			
			SPSStatement.Process();
			SPSValue.StoreValue();
			SPSOutput.WriteList();
			if (SPSData.errorCount == 0) SPSOutput.WriteCmem();
		}
		
		if (SPSData.dendStatus == SPSData.DendStatus.NOT_SEEN) {
			SPSOutput.ReportError("DEND statement missing");
			SPSOutput.PrintMessages();
		}
		SPSSymbol.Print();
		
		if (SPSData.errorCount == 0) {
			if (SPSData.crdOption) SPSOutput.DumpCrd();
			if (SPSData.ptOption) SPSOutput.DumpPt();
		}
		
		SPSInput.CloseSource();
		
		if (SPSData.lstOption) SPSOutput.CloseList();
		if (SPSData.cmemOption) SPSOutput.CloseCmem();
		
		if (SPSData.errorCount != 0) {
			SPSOutput.DeleteCmem();
			SPSOutput.DeleteCrd();
			SPSOutput.DeletePt();
		}
	}
}
