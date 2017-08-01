// -*- Mode: Java; indent-tabs-mode: t; tab-width: 4 -*-
// ---------------------------------------------------------------------------
// Multi-Phasic Applications: SquirrelJME
//     Copyright (C) Stephanie Gawroriski <xer@multiphasicapps.net>
//     Copyright (C) Multi-Phasic Applications <multiphasicapps.net>
// ---------------------------------------------------------------------------
// SquirrelJME is under the GNU General Public License v3+, or later.
// See license.mkd for licensing and copyright information.
// ---------------------------------------------------------------------------

package net.multiphasicapps.zip.streamreader;

import java.io.Flushable;
import java.io.InputStream;
import java.io.IOException;
import net.multiphasicapps.io.crc32.CRC32Calculator;
import net.multiphasicapps.io.dynhistin.DynamicHistoryInputStream;
import net.multiphasicapps.zip.ZipCompressionType;
import net.multiphasicapps.zip.ZipCRCConstants;
import net.multiphasicapps.zip.ZipException;

/**
 * This provides an interface to interact with a single entry within a ZIP
 * stream.
 *
 * This class is not thread safe.
 *
 * @since 2016/07/19
 */
public final class ZipStreamEntry
	extends InputStream
{
	/** The maximum size the data descriptor can be (if there is one). */
	private static final int _MAX_DESCRIPTOR_SIZE =
		16;
	
	/** The owning stream reader. */
	protected final ZipStreamReader zipreader;
	
	/** The name of the file. */
	protected final String filename;
	
	/** The compression method. */
	protected final ZipCompressionType method;
	
	/** The lower stream. */
	private final __LowerStream__ _lower;
	
	/** The higher stream. */
	private final __HigherStream__ _higher;
	
	/** The expected CRC. */
	private volatile int _wantcrc;
	
	/** Has this been closed? */
	private volatile boolean _closed;
	
	/**
	 * Initializes the entry.
	 *
	 * @param __zsr The owning stream reader.
	 * @param __fn The name of the entry.
	 * @param __undef Is the size and CRC undefined?
	 * @param __crc The expected CRC.
	 * @param __comp The compressed size.
	 * @param __uncomp The uncompressed size.
	 * @param __method The compression method.
	 * @param __ins The input data source.
	 * @throws IOException If the decompressor could not be initialized.
	 * @throws NullPointerException On null arguments.
	 * @since 2016/07/19
	 */
	ZipStreamEntry(ZipStreamReader __zsr, String __fn, boolean __undef,
		int __crc, int __comp, int __uncomp, ZipCompressionType __method,
		DynamicHistoryInputStream __ins)
		throws IOException, NullPointerException
	{
		// Check
		if (__zsr == null || __fn == null || __method == null ||
			__ins == null)
			throw new NullPointerException("NARG");
		
		// Set
		this.zipreader = __zsr;
		this.filename = __fn;
		this.method = __method;
		
		// Set expected CRC
		if (!__undef)
			this._wantcrc = __crc;
		
		// Setup lower stream
		this._lower = new __LowerStream__(__ins, __undef, __comp);
		
		// Setup higher stream
		this._higher = new __HigherStream__(__method.inputStream(this._lower),
			__undef, __uncomp, __crc);
	}
	
	/**
	 * {@inheritDoc}
	 * @since 2016/07/19
	 */
	@Override
	public void close()
		throws IOException
	{
		if (!this._closed)
		{
			// Mark closed
			this._closed = true;
			
			// Read all the remaining bytes
			while (read() >= 0)
				;
			
			// Calculate the CRC
			__HigherStream__ higher = this._higher;
			higher.flush();
			
			// Close the higher end
			this._higher.close();
			
			// {@squirreljme.error BG03 The expected CRC and the actual
			// CRC do not match, the data is corrupt. (The expected CRC;
			// The actual CRC)}
			int wantcrc, wascrc;
			if ((wantcrc = this._wantcrc) !=
				(wascrc = higher.crccalc.checksum()))
				throw new ZipException(String.format("BG03 %08x %08x",
					wantcrc, wascrc));
			
			// Clear it
			this.zipreader.__closeEntry(this);
		}
	}
	
	/**
	 * Returns the compression type that the entry uses.
	 *
	 * @return The compression type.
	 * @since 2016/07/19
	 */
	public ZipCompressionType compressionType()
	{
		return this.method;
	}
	
	/**
	 * Returns the name of the entry.
	 *
	 * @return The entry name.
	 * @since 2016/07/19
	 */
	public String name()
	{
		return this.filename;
	}
	
	/**
	 * {@inheritDoc}
	 * @since 2016/07/19
	 */
	@Override
	public int read()
		throws IOException
	{
		return this._higher.read();
	}
	
	/**
	 * {@inheritDoc}
	 * @since 2016/07/19
	 */
	@Override
	public int read(byte[] __b, int __o, int __l)
		throws IndexOutOfBoundsException, IOException, NullPointerException
	{
		// Check
		if (__b == null)
			throw new NullPointerException("NARG");
		int n = __b.length;
		if (__o < 0 || __l < 0 || (__o + __l) > n)
			throw new IndexOutOfBoundsException("IOOB");
		
		return this._higher.read(__b, __o, __l);
	}
	
	/**
	 * This is the higher stream which exposes the uncompressed data and
	 * can calculate the CRC on it.
	 *
	 * @since 2016/07/19
	 */
	private final class __HigherStream__
		extends InputStream
		implements Flushable
	{
		/** CRC calculation. */
		protected final CRC32Calculator crccalc =
			new CRC32Calculator(ZipCRCConstants.CRC_REFLECT_DATA,
				ZipCRCConstants.CRC_REFLECT_REMAINDER,
				ZipCRCConstants.CRC_POLYNOMIAL, ZipCRCConstants.CRC_REMAINDER,
				ZipCRCConstants.CRC_FINALXOR);
		
		/** The source stream. */
		protected final InputStream input;
	
		/** Is the CRC and size set undefined? */
		protected final boolean undefined;
	
		/** The CRC. */
		protected final int crc;
	
		/** The uncompressed size. */
		protected final int uncompressedsize;
		
		/** Has this stream been closed? */
		private volatile boolean _closed;
		
		/** Has EOF been reached? */
		private volatile boolean _eof;
		
		/** The current file size. */
		private volatile int _size;
		
		/**
		 * Initializes the higher stream.
		 *
		 * @param __ins The input stream for decompressed data.
		 * @param __undef If {@code true} then the uncompressed size is
		 * unknown.
		 * @param __uncomp The uncompressed size.
		 * @param __crc The CRC that the data must match.
		 * @throws NullPointerException On null arguments.
		 * @since 2016/07/19
		 */
		private __HigherStream__(InputStream __ins, boolean __undef,
			int __uncomp, int __crc)
			throws NullPointerException
		{
			// Check
			if (__ins == null)
				throw new NullPointerException("NARG");
			
			// Set
			this.input = __ins;
			this.undefined = __undef;
			this.crc = (__undef ? 0 : __crc);
			this.uncompressedsize = (__undef ? -1 : __uncomp);
		}
		
		/**
		 * {@inheritDoc}
		 * @since 2016/07/19
		 */
		@Override
		public final void close()
			throws IOException
		{
			InputStream input = this.input;
			
			// Handle closing by reading the rest of the stream
			if (!this._closed)
			{
				// Do not do it again
				this._closed = true;
				
				// Read until EOF
				while (read() >= 0)
					;
				
				// Close the lower stream
				ZipStreamEntry.this._lower.close();
			}
			
			// Close the input
			input.close();
		}
		
		/**
		 * {@inheritDoc}
		 * @since 2016/07/20
		 */
		@Override
		public void flush()
			throws IOException
		{
		}
		
		/**
		 * {@inheritDoc}
		 * @since 2016/07/19
		 */
		@Override
		public final int read()
			throws IOException
		{
			InputStream input = this.input;
			
			// if EOF read, read nothing
			if (this._eof)
				return -1;
			
			// Read a single byte from the input
			int rv = input.read();
			
			// EOF?
			if (rv < 0)
			{
				// Tell lower stream that EOF was reached in the data
				if (!this._eof)
				{	
					this._eof = true;
					ZipStreamEntry.this._lower.__eof();
				}
				
				// EOF
				return -1;
			}
			
			// Calculate CRC
			crccalc.offer((byte)rv);
			this._size += 1;
			return rv;
		}
		
		/**
		 * {@inheritDoc}
		 * @since 2016/07/19
		 */
		@Override
		public final int read(byte[] __b, int __o, int __l)
			throws IndexOutOfBoundsException, IOException, NullPointerException
		{
			// Check
			if (__b == null)
				throw new NullPointerException("NARG");
			int n = __b.length;
			if (__o < 0 || __l < 0 || (__o + __l) > n)
				throw new IndexOutOfBoundsException("IOOB");
			
			InputStream input = this.input;
			
			// Read bytes from the input
			int rc = input.read(__b, __o, __l);
			
			// EOF?
			if (rc < 0)
			{
				ZipStreamEntry.this._lower.__eof();
				return -1;
			}
			
			// Calculate CRC
			crccalc.offer(__b, __o, rc);
			this._size += rc;
			return rc;
		}
	}
	
	/**
	 * This is the lower stream which reads directory from the source data.
	 *
	 * @since 2016/07/19
	 */
	private final class __LowerStream__
		extends InputStream
	{
		/** The input source for bytes (and where to detect EOF). */
		protected final DynamicHistoryInputStream input;
		
		/** Is the CRC and size set undefined? */
		protected final boolean undefined;
	
		/** The compressed size. */
		protected final int compressedsize;
	
		/** If the size is undefined, then this is temporarily used. */
		private final byte[] _descbuf;
		
		/** The number of read bytes. */
		private volatile int _count;
		
		/** Was this closed? */
		private volatile boolean _closed;
		
		/** EOF reached in higher stream? */
		private volatile boolean _eof;
		
		/**
		 * Initializes the lower stream which detects the end of the actual
		 * uncompressed data.
		 *
		 * @param __ins The input stream to source bytes from.
		 * @param __undef If {@code true} then the compressed size is not
		 * known.
		 * @param __comp The compressed size.
		 * @throws NullPointerException On null arguments.
		 * @since 2016/07/19
		 */
		private __LowerStream__(DynamicHistoryInputStream __ins,
			boolean __undef, int __comp)
			throws NullPointerException
		{
			// Check
			if (__ins == null)
				throw new NullPointerException("NARG");
			
			// Set
			this.input = __ins;
			this.undefined = __undef;
			this.compressedsize = (__undef ? -1 : __comp);
			
			// Setup descriptor array if undefined
			if (__undef)
				this._descbuf = new byte[_MAX_DESCRIPTOR_SIZE];
			else
				this._descbuf = null;
		}
		
		/**
		 * {@inheritDoc}
		 * @since 2016/07/19
		 */
		@Override
		public final void close()
			throws IOException
		{
			// If not closed, seek to the end
			if (!this._closed)
			{
				// Mark closed
				this._closed = true;
				
				// Seek to the end
				byte[] descbuf = this._descbuf;
				if (descbuf == null)
					descbuf = new byte[64];
				for (;;)
				{
					int rc = read(descbuf);
					
					// EOF reached
					if (rc < 0)
						break;
				}
				
				// Call upper close
				ZipStreamEntry.this.close();
			}
		}
		
		/**
		 * {@inheritDoc}
		 * @since 2016/07/19
		 */
		@Override
		public final int read()
			throws IOException
		{
			// Higher stream EOFed
			if (this._eof)
				return -1;
			
			// Undefined size, have to guess
			if (this.undefined)
			{
				int count = this._count;
				byte[] descbuf = this._descbuf;
				DynamicHistoryInputStream input = this.input;
				
				// Read ahead to detect the descriptor
				int act = input.peek(0, descbuf);
				
				// {@squirreljme.error BG04 Truncated ZIP file.}
				if (act < _MAX_DESCRIPTOR_SIZE)
					throw new ZipException("BG04");
				
				// Is this the descriptor magic number? If so then stop
				// if the current read number of bytes matches the current
				// size.
				if (ZipStreamReader.__readInt(descbuf, 0) == 0x08074B50)
					if (ZipStreamReader.__readInt(descbuf, 8) == count)
					{
						// Read the desired CRC
						ZipStreamEntry.this._wantcrc =
							ZipStreamReader.__readInt(descbuf, 4);
						
						// No more bytes left
						this._eof = true;
						return -1;
					}
				
				// Read normal byte otherwise
				int rv = input.read();
				
				// {@squirreljme.error BG05 Expected a byte and not EOF
				// while reading an unknown size compressed entry.}
				if (rv < 0)
					throw new ZipException("BG05");
				
				// Increase count
				this._count = count + 1;
				
				// Return
				return rv;
			}
			
			// Size is known
			else
			{
				// Get current count
				int count = this._count;
				
				// Reached end?
				if (count >= this.compressedsize)
					return -1;
				
				// Read otherwise
				int rv = this.input.read();
				
				// {@squirreljme.error BG02 Expected a byte and not EOF
				// while reading compressed entry data.}
				if (rv < 0)
					throw new ZipException("BG02");
				
				// Increase count
				this._count = count + 1;
				
				// Return it
				return rv;
			}
		}
		
		/**
		 * {@inheritDoc}
		 * @since 2016/07/19
		 */
		@Override
		public final int read(byte[] __b, int __o, int __l)
			throws IndexOutOfBoundsException, IOException, NullPointerException
		{
			// Check
			if (__b == null)
				throw new NullPointerException("NARG");
			int n = __b.length;
			if (__o < 0 || __l < 0 || (__o + __l) > n)
				throw new IndexOutOfBoundsException("IOOB"); 
			
			// Higher stream EOFed
			if (this._eof)
				return -1;	
			
			// For simplicity, use single byte read because the end may
			// either be defined or undefined
			int c = 0;
			boolean eofed = false;
			for (int i = 0, o = __o; i < __l; i++, o++)
			{
				int v = read();
				
				// EOF?
				if ((eofed = (v < 0)))
					break;
				
				__b[o] = (byte)v;
				c++;
			}
			
			// Return the read count
			if (eofed && c <= 0)
				return -1;
			return c;
		}
		
		/**
		 * Marks that the end of the stream has been reached, the upper layer
		 * calls this. If the descriptor matches, that also calls this.
		 *
		 * @since 2016/07/20
		 */
		final void __eof()
		{
			this._eof = true;
		}
	}
}

