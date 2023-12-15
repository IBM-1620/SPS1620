package SPS1620;

/*
 *  SPSOutput.java - output routines for listing, punched card, paper tape, and core memory files
 *
 *  IBM 1620 Jr Project, Computer History Museum, 2017-2023
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

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;

public class SPSOutput {
	
	// Methods
	
	public static void OpenList() {
		
		SPSData.lstFile = new File(SPSData.lstFilename);
		
		try {
			SPSData.lstFile.createNewFile();
		} catch (IOException e1) {
			SPSData.lstFile = null;
			ReportError("cannot create list file (" + SPSData.lstFilename + ")");
			return;
		}

		try {
			SPSData.lstWriter = new PrintWriter(SPSData.lstFile);
		} catch (IOException e) {
			SPSData.lstFile = null;
			ReportError("cannot open list file (" + SPSData.lstFilename + ")");
			return;
		}
	}
	
	public static void WriteList() {
		String instr;
		String flags;
		
		if (SPSData.lstFile == null) return;
		
		switch (SPSData.inputStatement.Listing) {
				
			case UNKNOWN:
			case COMMENT:
			case CONTROL:
				SPSData.lstWriter.write(LineNumber(SPSData.lineNumber) + "                        " + FormattedLine() + "\n");
				break;
			
			case ADDRESS:
				SPSData.lstWriter.write(String.format("%s  %05d                 %s\n", LineNumber(SPSData.lineNumber), SPSData.labelAddress,
													  FormattedLine()));
				break;

			case INSTRUCTION:
				instr = new String(new char[]{SPSData.listChar[SPSData.value[0]], SPSData.listChar[SPSData.value[1]], ' ', 
											  SPSData.listChar[SPSData.value[2]], SPSData.listChar[SPSData.value[3]],
											  SPSData.listChar[SPSData.value[4]], SPSData.listChar[SPSData.value[5]],
											  SPSData.listChar[SPSData.value[6]], ' ',
											  SPSData.listChar[SPSData.value[7]], SPSData.listChar[SPSData.value[8]],
											  SPSData.listChar[SPSData.value[9]], SPSData.listChar[SPSData.value[10]],
											  SPSData.listChar[SPSData.value[11]]});
				flags = new String(new char[]{SPSData.flagChar[SPSData.value[0]], SPSData.flagChar[SPSData.value[1]], ' ', 
												 SPSData.flagChar[SPSData.value[2]], SPSData.flagChar[SPSData.value[3]],
												 SPSData.flagChar[SPSData.value[4]], SPSData.flagChar[SPSData.value[5]],
												 SPSData.flagChar[SPSData.value[6]], ' ',
												 SPSData.flagChar[SPSData.value[7]], SPSData.flagChar[SPSData.value[8]],
					  							 SPSData.flagChar[SPSData.value[9]], SPSData.flagChar[SPSData.value[10]],
					  							 SPSData.flagChar[SPSData.value[11]]});
				if (!SPSUtility.IsEmpty(flags)) SPSData.lstWriter.write(String.format("               %-14s\n", SPSUtility.RTrim(flags)));
				SPSData.lstWriter.write(String.format("%s  %05d %-14s  %s\n", LineNumber(SPSData.lineNumber), SPSData.address, instr,
													  FormattedLine()));
				break;
			
			case INSTR_2:
				instr = new String(new char[]{SPSData.listChar[SPSData.value[0]], SPSData.listChar[SPSData.value[1]]});
				flags = new String(new char[]{SPSData.flagChar[SPSData.value[0]], SPSData.flagChar[SPSData.value[1]]});
				if (!SPSUtility.IsEmpty(flags)) SPSData.lstWriter.write(String.format("               %-2s\n", SPSUtility.RTrim(flags)));
				SPSData.lstWriter.write(String.format("%s  %05d %-2s              %s\n", LineNumber(SPSData.lineNumber), SPSData.address, instr,
													  FormattedLine()));
				break;
			
			case INSTR_7:
				instr = new String(new char[]{SPSData.listChar[SPSData.value[0]], SPSData.listChar[SPSData.value[1]], ' ', 
											  SPSData.listChar[SPSData.value[2]], SPSData.listChar[SPSData.value[3]],
											  SPSData.listChar[SPSData.value[4]], SPSData.listChar[SPSData.value[5]],
											  SPSData.listChar[SPSData.value[6]]});
				flags = new String(new char[]{SPSData.flagChar[SPSData.value[0]], SPSData.flagChar[SPSData.value[1]], ' ', 
												 SPSData.flagChar[SPSData.value[2]], SPSData.flagChar[SPSData.value[3]],
												 SPSData.flagChar[SPSData.value[4]], SPSData.flagChar[SPSData.value[5]],
												 SPSData.flagChar[SPSData.value[6]]});
				if (!SPSUtility.IsEmpty(flags)) SPSData.lstWriter.write(String.format("               %-8s\n", SPSUtility.RTrim(flags)));
				SPSData.lstWriter.write(String.format("%s  %05d %-8s        %s\n", LineNumber(SPSData.lineNumber), SPSData.address, instr,
													  FormattedLine()));
				break;

			case DATA:
				String data = "";
				String dflags = "";
				if (SPSData.value != null) {
					if (SPSData.value.length <= 14) {
						for (int i = 0; i < SPSData.value.length; ++i) {
							data += SPSData.listChar[SPSData.value[i]];
							dflags += SPSData.flagChar[SPSData.value[i]];
						}
					} else {
						for (int i = 0; i < 6; ++i) {
							data += SPSData.listChar[SPSData.value[i]];
							dflags += SPSData.flagChar[SPSData.value[i]];
						}
						data += "..";
						dflags += "  ";
						for (int i = (SPSData.value.length - 6); i < SPSData.value.length; ++i) {
							data += SPSData.listChar[SPSData.value[i]];
							dflags += SPSData.flagChar[SPSData.value[i]];
						}
					}
				}
				if (!SPSUtility.IsEmpty(dflags)) SPSData.lstWriter.write(String.format("               %-14s\n", SPSUtility.RTrim(dflags)));
				SPSData.lstWriter.write(String.format("%s  %05d %-14s  %s\n", LineNumber(SPSData.lineNumber), SPSData.address, data,
													  FormattedLine()));
				break;
			
			case RESERVE_1:
				SPSData.lstWriter.write(String.format("%s  %05d    %05d        %s\n", LineNumber(SPSData.lineNumber), SPSData.labelAddress,
													  SPSData.length, FormattedLine()));
				break;
			
			case RESERVE_2:
				SPSData.lstWriter.write(String.format("%s  %05d    %05d %05d  %s\n", LineNumber(SPSData.lineNumber), SPSData.labelAddress,
													  SPSData.length, SPSData.count, FormattedLine()));
				break;
				
			default:
				break;
		}
		
		PrintMessages();
	}
	
	public static void PrintFile() {
		if (SPSData.lstFile != null) {
			SPSData.lstWriter.write(String.format("\n  ------ %d: %s --------------------------------------------------\n\n",
												  SPSData.sourceCurrentFile + 1, SPSData.sourceFilenames[SPSData.sourceCurrentFile]));
		}
	}
	
	public static void PrintMessages() {
		if ((SPSData.lstFile != null) && !SPSUtility.IsEmpty(SPSData.inputMessages)) {
			SPSData.lstWriter.write(SPSData.inputMessages);
		}
	}
	
	public static void CloseList() {
		if (SPSData.lstFile != null) {
			SPSData.lstWriter.close();
			SPSData.lstFile = null;
		}
	}
	
	public static void DeleteList() {
		if (SPSData.lstFilename != null) {
			(new File(SPSData.lstFilename)).delete();
		}
	}
	
	public static void OpenCmem() {
		
		SPSData.cmemFile = new File(SPSData.cmemFilename);
		
		try {
			SPSData.cmemFile.createNewFile();
		} catch (IOException e1) {
			SPSData.cmemFile = null;
			ReportError("cannot create cmem file (" + SPSData.cmemFilename + ")");
			return;
		}

		try {
			SPSData.cmemWriter = new PrintWriter(SPSData.cmemFile);
		} catch (IOException e) {
			SPSData.cmemFile = null;
			ReportError("cannot open cmem file (" + SPSData.cmemFilename + ")");
			return;
		}
	}
	
	public static void WriteCmem() {
		
		if (SPSData.cmemFile == null) return;
		
		switch (SPSData.inputStatement.Listing) {
				
			case UNKNOWN:
			case COMMENT:
				if (SPSData.sourceFormat == SPSData.SourceFormat.FIXED) {
					SPSData.cmemWriter.write("                                                                    //" +
											 SPSUtility.Substring(SPSData.inputLine, 5) + "\n");
				} else {	
					SPSData.cmemWriter.write("                                                                    //" +
											 SPSUtility.Substring(SPSData.inputLine, 0) + "\n");
				}
				break;

			case CONTROL:
				if (SPSData.sourceFormat == SPSData.SourceFormat.FIXED) {
					SPSData.cmemWriter.write("                                                                    //   " +
											 SPSUtility.Substring(SPSData.inputLine, 5) + "\n");
				} else {	
					SPSData.cmemWriter.write("                                                                    //   " +
											 SPSUtility.Substring(SPSData.inputLine, 0) + "\n");
				}
				break;
			
			case INSTRUCTION:
				String instr;
				if (SPSData.length == 2) {
					instr = String.format("%02X %02X                                                    ",
										  SPSData.value[0], SPSData.value[1]);
				} else if (SPSData.length == 7) {
					instr = String.format("%02X %02X  %02X %02X %02X %02X %02X                          ",
											 SPSData.value[0], SPSData.value[1], SPSData.value[2], SPSData.value[3],
											 SPSData.value[4], SPSData.value[5], SPSData.value[6]);
				} else {
					instr = String.format("%02X %02X  %02X %02X %02X %02X %02X  %02X %02X %02X %02X %02X",
										  SPSData.value[0], SPSData.value[1], SPSData.value[2], SPSData.value[3],
										  SPSData.value[4], SPSData.value[5], SPSData.value[6], SPSData.value[7],
										  SPSData.value[8], SPSData.value[9], SPSData.value[10], SPSData.value[11]);
				}
				SPSData.cmemWriter.write(String.format("%05d: %-59s  // %-6s %-4s %s\n", SPSData.address, instr, SPSData.inputLabel,
										 SPSData.inputOperation, SPSData.inputOperands));
				break;
			
			case DATA:
				int addr = SPSData.address;
				int len = (SPSData.value != null) ? SPSData.value.length : 0;
				if (len > 0) {
					for (int i = 0; i < len; i += 20) {
						String value = "";
						for (int j = i; j < Math.min(i + 20, len); ++j) {
							value += String.format("%02X ",  SPSData.value[j]);
						}
						if (i == 0) {
							SPSData.cmemWriter.write(String.format("%05d: %-60s // %-6s %-4s %s\n", addr, value, SPSData.inputLabel,
																   SPSData.inputOperation, SPSData.inputOperands));
						} else {
							SPSData.cmemWriter.write(String.format("%05d: %-60s\n", addr, value));
						}
						addr += 20;
					}
				} else {
					SPSData.cmemWriter.write(String.format("                                                                    // %-6s %-4s %s\n",
														   SPSData.inputLabel, SPSData.inputOperation, SPSData.inputOperands));
				}
				break;
				
			case ADDRESS:
			case RESERVE_1:
			case RESERVE_2:
				SPSData.cmemWriter.write(String.format("                                                                    // %-6s %-4s %s\n",
													   SPSData.inputLabel, SPSData.inputOperation, SPSData.inputOperands));
				break;
				
			default:
				break;
		}
	}
	
	public static void WriteLowCore() {
		SPSData.cmemWriter.write(SPSData.INIT_CMEM_1);
		
		for (int i = 0; i < 36; i += 12) {
			String instr = String.format("%02X %02X  %02X %02X %02X %02X %02X  %02X %02X %02X %02X %02X",
										 SPSData.memory[i],     SPSData.memory[i + 1], SPSData.memory[i + 2],  SPSData.memory[i + 3],
										 SPSData.memory[i + 4], SPSData.memory[i + 5], SPSData.memory[i + 6],  SPSData.memory[i + 7],
										 SPSData.memory[i + 8], SPSData.memory[i + 9], SPSData.memory[i + 10], SPSData.memory[i + 11]);
			SPSData.cmemWriter.write(String.format("%05d: %-37s\n", i, instr));
		}
		
		SPSData.cmemWriter.write(SPSData.INIT_CMEM_2);
		if (SPSData.systemType == SPSData.SystemType.MODEL_1) { 
			SPSData.cmemWriter.write(SPSData.INIT_CMEM_3);
		}
		SPSData.cmemWriter.write(SPSData.INIT_CMEM_4);
	}
	
	public static void CloseCmem() {
		if (SPSData.cmemFile != null) {
			SPSData.cmemWriter.close();
			SPSData.cmemFile = null;
		}
	}
	
	public static void DeleteCmem() {
		if (SPSData.cmemFilename != null) {
			(new File(SPSData.cmemFilename)).delete();
		}
	}
	
	public static void DumpCrd() {
		int addr1;
		int addr2;
		int end;
		int next;
		String str;
		boolean seen;
		
		SPSData.crdFile = new File(SPSData.crdFilename);
		
		try {
			SPSData.crdFile.createNewFile();
		} catch (IOException e1) {
			SPSData.crdFile = null;
			ReportError("cannot create crd file (" + SPSData.crdFilename + ")");
			return;
		}

		try {
			SPSData.crdWriter = new PrintWriter(SPSData.crdFile);
		} catch (IOException e) {
			SPSData.crdFile = null;
			ReportError("cannot open crd file (" + SPSData.crdFilename + ")");
			return;
		}

		SPSData.crdWriter.write(SPSData.INIT_CRD_1);
		SPSData.crdWriter.write(SPSData.INIT_CRD_2);
	
		addr1 = 240;
		while (addr1 < SPSData.memorySize) {
			while ((addr1 < SPSData.memorySize) && (SPSData.memory[addr1] == SPSData.MEMORY_UNDEF)) {
				++addr1;
			}
			if (addr1 >= SPSData.memorySize) break;
			
			str = "";
			seen = false;
			end = Math.min(addr1 + 60, SPSData.memorySize);
			for (next = addr1; next < end; ++next) {
				byte mem = SPSData.memory[next];
				if (mem == SPSData.MEMORY_UNDEF) {
					str += ' ';
				} else {
					str += SPSData.crdChar[mem & SPSData.MASK_FLAG_DIGIT];
					if (SPSUtility.IsRMGM(mem)) {
						seen = true;
						++next;
						break;
					}
				}
			}
			
			str = SPSUtility.RTrim(str);
			addr2 = (addr1 + str.length()) % SPSData.memorySize;
			if (!seen) str += '|';
			SPSData.crdWriter.write(String.format("%5s%5s%s\n", AddressField(addr1), AddressField(addr2), str));
			
			addr1 = next;
		}

		SPSData.crdWriter.write(SPSData.INIT_CRD_3);

		str = "";
		for (int i = 160; i < 240; ++i) {
			str += SPSData.crdChar[SPSData.memory[i] & SPSData.MASK_FLAG_DIGIT];
		}
		SPSData.crdWriter.write(str + "\n");

		str = "";
		for (int i = 80; i < 160; ++i) {
			str += SPSData.crdChar[SPSData.memory[i] & SPSData.MASK_FLAG_DIGIT];
		}
		SPSData.crdWriter.write(str + "\n");

		str = "";
		for (int i = 0; i < 80; ++i) {
			str += SPSData.crdChar[SPSData.memory[i] & SPSData.MASK_FLAG_DIGIT];
		}
		SPSData.crdWriter.write(str + "\n");
		
		if (SPSData.crdFile != null) {
			SPSData.crdWriter.close();
			SPSData.crdFile = null;
		}
	}
	
	private static String AddressField(int address) {
		String str = new String(new char[]{SPSData.crdChar[(address / 10000) | SPSData.MASK_FLAG],
										   SPSData.crdChar[(address / 1000) % 10],
										   SPSData.crdChar[(address / 100) % 10],
										   SPSData.crdChar[(address / 10) % 10],
										   SPSData.crdChar[address % 10]});
		return str;
	}
	
	public static void DeleteCrd() {
		if (SPSData.crdFilename != null) {
			(new File(SPSData.crdFilename)).delete();
		}
	}
	
	public static void DumpPt() {
		
		int lastAddr;
		
		SPSData.ptFile = new File(SPSData.ptFilename);
		
		try {
			SPSData.ptFile.createNewFile();
		} catch (IOException e1) {
			SPSData.ptFile = null;
			ReportError("cannot create pt file (" + SPSData.ptFilename + ")");
			return;
		}

		try {
			SPSData.ptStream = new FileOutputStream(SPSData.ptFile);
		} catch (IOException e) {
			SPSData.ptFile = null;
			ReportError("cannot open pt file (" + SPSData.ptFilename + ")");
			return;
		}
	
		for (lastAddr = SPSData.memorySize - 1; lastAddr >= 0; --lastAddr) {
			if (SPSData.memory[lastAddr] != SPSData.MEMORY_UNDEF) break;
		}
		if (lastAddr >= 0) {
			for (int i = 0; i <= lastAddr; ++i) {
				try {
					SPSData.ptStream.write(SPSData.ptCode[SPSData.memory[i] & SPSData.MASK_FLAG_DIGIT]);
				} catch (IOException e) {
					ReportError("cannot write pt file (" + SPSData.ptFilename + ")");
					break;
				}
			}
			try {
				SPSData.ptStream.write(SPSData.CHAR_EOL);
			} catch (IOException e) {
				ReportError("cannot write pt file (" + SPSData.ptFilename + ")");
			}
		}
		
		if (SPSData.ptFile != null) {
			try {
				SPSData.ptStream.close();
			} catch (IOException e) {
				// ignore error
			}
			SPSData.ptFile = null;
		}
	}
	
	public static void DeletePt() {
		if (SPSData.ptFilename != null) {
			(new File(SPSData.ptFilename)).delete();
		}
	}
	
	public static void ReportError(String error) {
		
		++SPSData.errorCount;
		
		if (SPSData.pass == 0) {
			System.out.println("*** Error: " + error);
			
		} else if (SPSData.pass == 1){
			if (SPSData.pass1Errors) { 
				System.out.println("*** Error: " + error + ", line " + ErrorLineNumber());
			}
			
		} else if (SPSData.lstFile == null) {
			System.out.println("*** Error: " + error + ", line " + ErrorLineNumber());
			
		} else {
			SPSData.inputMessages += (SPSData.sourceMultipleFiles ? "  " : "") + "                               ^^^ Error: " + error + "\n";
		}
	}
	
	public static void ReportWarning(String warning) {
		
		if (!SPSData.produceWarnings) return; 
		
		++SPSData.warningCount;
		
		if (SPSData.pass == 0) {
			System.out.println("*** Warning: " + warning);
			
		} else if (SPSData.pass == 1){
			if (SPSData.pass1Errors) { 
				System.out.println("*** Warning: " + warning + ", line " + ErrorLineNumber());
			}
			
		} else if (SPSData.lstFile == null) {
			System.out.println("*** Warning: " + warning + ", line " + ErrorLineNumber());
			
		} else {
			SPSData.inputMessages += (SPSData.sourceMultipleFiles ? "  " : "") + "                               ^^^ Warning: " + warning + "\n";
		}
	}
	
	public static String ErrorLineNumber() {
		if (SPSData.sourceMultipleFiles) {
			return String.format("%s:%d", SPSData.sourceFile.getName(), (SPSData.lineNumber & SPSData.MASK_LINE_NUMBER));
		} else {
			return String.format("%d", (SPSData.lineNumber & SPSData.MASK_LINE_NUMBER));
		}
	}
	
	public static String LineNumber(int lineNumber) {
		if (SPSData.sourceMultipleFiles) {
			return String.format("%2d:%-4d", (lineNumber >> 18), (lineNumber & SPSData.MASK_LINE_NUMBER));
		} else {
			return String.format("%7d", (lineNumber & SPSData.MASK_LINE_NUMBER));
		}
	}

	public static String FormattedLine() {
		String str = SPSData.inputLine;
		String str2;
		if (SPSData.sourceFormat == SPSData.SourceFormat.FIXED) {
			if (SPSData.inputStatement.Listing == SPSStatement.StatementListing.COMMENT) {
					str2 = String.format("%-5s %-70s  %s", SPSUtility.Substring(str, 0, 5), SPSUtility.Substring(str, 5, 75), SPSUtility.Substring(str, 75));
			} else {
				str2 = String.format("%-5s %-6s %-4s %s", SPSUtility.Substring(str, 0, 5), SPSUtility.Substring(str, 5, 11), SPSUtility.Substring(str, 11, 15),
									 SPSUtility.Substring(str, 15));
			}
		} else {
			str2 = SPSData.inputLine;
		}
		return SPSUtility.RTrim(str2);
	}
}
