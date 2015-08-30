package com.vjiazhi.shuiyinwang.utils;

import android.text.InputFilter;
import android.text.Spanned;

public class Utf8ByteLenFilter implements InputFilter {
	private final int mMaxBytes;

	public Utf8ByteLenFilter(int maxBytes) {
		mMaxBytes = maxBytes;
	}

	public CharSequence filter(CharSequence source, int start, int end,
			Spanned dest, int dstart, int dend) {
		int srcByteCount = 0;
		// count UTF-8 bytes in source substring
		for (int i = start; i < end; i++) {
			char c = source.charAt(i);
			srcByteCount += (c < (char) 0x0080) ? 1 : (c < (char) 0x0800 ? 2
					: 3);
		}
		int destLen = dest.length();
		int destByteCount = 0;
		// count UTF-8 bytes in destination excluding replaced section
		for (int i = 0; i < destLen; i++) {
			if (i < dstart || i >= dend) {
				char c = dest.charAt(i);
				destByteCount += (c < (char) 0x0080) ? 1
						: (c < (char) 0x0800 ? 2 : 3);
			}
		}
		int keepBytes = mMaxBytes - destByteCount;
		if (keepBytes <= 0) {
			return "";
		} else if (keepBytes >= srcByteCount) {
			return null; // use original dest string
		} else {
			// find end position of largest sequence that fits in keepBytes
			for (int i = start; i < end; i++) {
				char c = source.charAt(i);
				keepBytes -= (c < (char) 0x0080) ? 1 : (c < (char) 0x0800 ? 2
						: 3);
				if (keepBytes < 0) {
					return source.subSequence(start, i);
				}
			}
			// If the entire substring fits, we should have returned null
			// above, so this line should not be reached. If for some
			// reason it is, return null to use the original dest string.
			return null;
		}
	}
}