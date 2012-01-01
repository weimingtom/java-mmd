package net.yzwlab.javammd.format;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import net.yzwlab.javammd.IReadBuffer;
import net.yzwlab.javammd.ReadException;

public class PMD_MORP_RECORD implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	protected byte[] name;

	protected int vnum;

	protected byte grp;

	protected List<PMD_MORP_VERTEX_RECORD> mv;

	public PMD_MORP_RECORD() {
		this.name = new byte[20];
		this.vnum = 0;
		this.grp = 0;
		this.mv = new ArrayList<PMD_MORP_VERTEX_RECORD>();
	}

	public PMD_MORP_RECORD(PMD_MORP_RECORD source) {
		this.name = new byte[20];
		this.vnum = 0;
		this.grp = 0;
		this.mv = new ArrayList<PMD_MORP_VERTEX_RECORD>();

		System.arraycopy(source.name, 0, this.name, 0, name.length);
		this.vnum = source.vnum;
		this.grp = source.grp;
		for (PMD_MORP_VERTEX_RECORD rec : source.mv) {
			this.mv.add(rec);
		}
	}

	public byte[] getName() {
		return name;
	}

	public void setName(byte[] name) {
		this.name = name;
	}

	public int getVnum() {
		return vnum;
	}

	public void setVnum(int vnum) {
		this.vnum = vnum;
	}

	public byte getGrp() {
		return grp;
	}

	public void setGrp(byte grp) {
		this.grp = grp;
	}

	public List<PMD_MORP_VERTEX_RECORD> getMv() {
		return mv;
	}

	public void setMv(List<PMD_MORP_VERTEX_RECORD> mv) {
		this.mv = mv;
	}

	public PMD_MORP_RECORD Read(IReadBuffer buffer) throws ReadException {
		if (buffer == null) {
			throw new IllegalArgumentException();
		}
		// is.read(name,sizeof(name))
		this.name = buffer.readByteArray(20);
		// is.read((char*)&vnum,sizeof(vnum))
		this.vnum = buffer.readInteger();
		// is.read((char*)&grp,sizeof(grp))
		this.grp = buffer.readByte();
		// mv.resize(vnum)
		// is.read((char*)&mv[0],vnum*sizeof(PMD_MORP_VERTEX_RECORD))
		this.mv.clear();
		for (int i = 0; i < vnum; i++) {
			this.mv.add((new PMD_MORP_VERTEX_RECORD()).Read(buffer));
		}
		return this;
	}
}
