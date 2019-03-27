/*******************************************************************************
 * Copyright (C) 2019 Mark Harkin
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 ******************************************************************************/
package xpra.util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.zip.DataFormatException;
import java.util.zip.Inflater;

import net.jpountz.lz4.LZ4Factory;
import net.jpountz.lz4.LZ4FastDecompressor;

public class CompressionUtil {

	public static byte[] decompressZlib(byte[] bytesToDecompress) {
		byte[] returnValues = null;

		Inflater inflater = new Inflater();

		int numberOfBytesToDecompress = bytesToDecompress.length;

		inflater.setInput(bytesToDecompress, 0, numberOfBytesToDecompress);

		int bufferSizeInBytes = numberOfBytesToDecompress;

		int numberOfBytesDecompressedSoFar = 0;
		List<Byte> bytesDecompressedSoFar = new ArrayList<Byte>();

		try {
			while (inflater.needsInput() == false) {
				byte[] bytesDecompressedBuffer = new byte[bufferSizeInBytes];

				int numberOfBytesDecompressedThisTime = inflater.inflate(bytesDecompressedBuffer);

				numberOfBytesDecompressedSoFar += numberOfBytesDecompressedThisTime;

				for (int b = 0; b < numberOfBytesDecompressedThisTime; b++) {
					bytesDecompressedSoFar.add(bytesDecompressedBuffer[b]);
				}
			}

			returnValues = new byte[bytesDecompressedSoFar.size()];
			for (int b = 0; b < returnValues.length; b++) {
				returnValues[b] = (bytesDecompressedSoFar.get(b));
			}

		} catch (DataFormatException dfe) {
			dfe.printStackTrace();
		}

		inflater.end();

		return returnValues;
	}

	public static byte[] decompressLZ4(byte[] bytesToDecompress) {
		LZ4Factory lz4Factory = LZ4Factory.fastestInstance();
		LZ4FastDecompressor decompressor = lz4Factory.fastDecompressor();

		// java unsigned byte nastiness
		int d0 = bytesToDecompress[0] & 0xFF;
		int d1 = bytesToDecompress[1] & 0xFF;
		int d2 = bytesToDecompress[2] & 0xFF;
		int d3 = bytesToDecompress[3] & 0xFF;
		int length = (d0 | (d1 << 8) | (d2 << 16) | (d3 << 24));

		byte[] lz4Data = Arrays.copyOfRange(bytesToDecompress, 4, bytesToDecompress.length - 1);
		if (length < 0) {
			// TODO log error calculating length
		}

		return decompressor.decompress(lz4Data, length);

	}

}
