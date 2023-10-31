package SPS1620;

/*
 *  SPSValue.java - support routines for statement value
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

public class SPSValue {
	
	// Methods
	
	public static void SetField(int offset, int length, boolean flag, long value) {
		int off = offset;
		long val = Math.abs(value);

		if (length == 0) return;
		
		for (int i = 0; i < length; ++i) {
			SPSData.value[off--] = (byte)(val % 10L);
			val /= 10L;
		}

		if (value < 0) SPSData.value[offset] |= SPSData.MASK_FLAG;
		if (flag) SPSData.value[offset - length + 1] |= SPSData.MASK_FLAG;
	}
	
	public static void SetFlags(String flags) {
		if (SPSUtility.IsEmpty(flags)) return;
		
		if (!SPSUtility.IsValidFlags(flags)) {
			SPSOutput.ReportError("invalid flags (" + flags + ")");
			return;
		}
		
		for (int i = 0; i < flags.length(); ++i) {
			int flag = flags.charAt(i) - '0';
			if ((flag == 1) && (i < (flags.length() - 1))) {
				char flag2 = flags.charAt(i + 1);
				if (flag2 == '0') {
					flag = 10;
					++i;
				} else if (flag2 == '1') {
					flag = 11;
					++i;
				}
			}
			SPSData.value[flag] |= SPSData.MASK_FLAG;
		}
	}
	
	public static void SetIndexFlags(int offset) {
		if (SPSData.operandIndex == 0) return;
		if ((SPSData.operandIndex & 0x4) != 0) SPSData.value[offset - 3] |= SPSData.MASK_FLAG;
		if ((SPSData.operandIndex & 0x2) != 0) SPSData.value[offset - 2] |= SPSData.MASK_FLAG;
		if ((SPSData.operandIndex & 0x1) != 0) SPSData.value[offset - 1] |= SPSData.MASK_FLAG;
	}
	
	public static void SetNumValue(int length, boolean isDSC, String value) {
		boolean neg = SPSUtility.CharAt(value, 0) == '-';
		boolean rm = SPSUtility.CharAt(value, value.length() - 1) == '@';
		int first = neg ? 1 : 0;
		int pos = length - 1;
		int pos2 = value.length() - 1;
		
		SPSData.value = new byte[length];
		
		if (rm && (pos >= 0)) {
			SPSData.value[pos--] = SPSData.MEMORY_RM;
			--pos2;
		}		

		if ((first <= pos2) && !(isDSC ? SPSUtility.IsValidNumberX(value.substring(first, pos2 + 1))
									   : SPSUtility.IsValidNumber(value.substring(first, pos2 + 1)))) {
			SPSOutput.ReportError("invalid value (" + value.substring(first, pos2 + 1) + ")");
			return;
		}

		while ((pos >= 0) && (pos2 >= first)) {
			int val = value.charAt(pos2--);
			if ((val >= '0') && (val <= '9')) {
				SPSData.value[pos--] = (byte)(val - '0');
			} else if (val == ']') {
				SPSData.value[pos--] = (byte)(0 | SPSData.MASK_FLAG);
			} else {
				SPSData.value[pos--] = (byte)(val - 'I' | SPSData.MASK_FLAG);
			}
		}
		
		while (pos >= 0) {
			SPSData.value[pos--] = 0;
		}
		
		while (pos2 >= first) {
			if (value.charAt(pos2--) != '0') {
				SPSOutput.ReportError("value too large");
				break;
			}
		}
		
		if (neg) {
			if (!rm && (length > 0)) SPSData.value[length - 1] |= SPSData.MASK_FLAG;
			if (rm && (length > 1)) SPSData.value[length - 2] |= SPSData.MASK_FLAG;
		}
	}
	
	public static void SetAlphaValue(int length, String value) {
		boolean rm = SPSUtility.CharAt(value, value.length() - 1) == '@';
		int pos = 0;
		int pos2 = 0;
		int last = 2 * (length - (rm ? 2 : 1));
		int last2 = value.length() - (rm ? 2 : 1);
		
		SPSData.value = new byte[2 * length];
		
		if ((0 <= last2) && !SPSUtility.IsValidAlpha(value.substring(0, last2 + 1))) {
			SPSOutput.ReportError("invalid value (" + value + ")");
			return;
		}
		
		while ((pos <= last) && (pos2 <= last2)) {
			int chr = SPSData.alphaChar[value.charAt(pos2++) & SPSData.MASK_CHARACTER];
			SPSData.value[pos++] = (byte)(chr / 256);
			SPSData.value[pos++] = (byte)(chr % 256);
		}
		
		while (pos <= last) {
			SPSData.value[pos++] = 0;
			SPSData.value[pos++] = 0;
		}
		
		if (rm & (length > 0)) {
			SPSData.value[pos++] = 0;
			SPSData.value[pos++] = SPSData.MEMORY_RM;
		}
	}
	
	public static void StoreValue() {
		if ((SPSData.value == null) || (SPSData.value.length == 0)) return;
		
		int addr = SPSData.address;
		if ((addr < 0) || ((addr + SPSData.value.length) > SPSData.memorySize)) {
			SPSOutput.ReportError("invalid address (" + addr + ")");
			return;
		}
		
		for (int i = 0; i < SPSData.value.length; ++i) {
			SPSData.memory[addr++] = SPSData.value[i];
		}
	}
}
