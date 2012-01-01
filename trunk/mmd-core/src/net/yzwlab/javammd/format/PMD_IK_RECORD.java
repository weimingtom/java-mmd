package net.yzwlab.javammd.format;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import net.yzwlab.javammd.IReadBuffer;
import net.yzwlab.javammd.ReadException;

public class PMD_IK_RECORD implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	protected short parent;

	protected short to;

	protected byte NumLink;

	protected short count;

	protected float fact;

	protected List<Short> link;

	public PMD_IK_RECORD() {
		this.parent = 0;
		this.to = 0;
		this.NumLink = 0;
		this.count = 0;
		this.fact = 0.0f;
		this.link = new ArrayList<Short>();
	}

	public short getParent() {
		return parent;
	}

	public void setParent(short parent) {
		this.parent = parent;
	}

	public short getTo() {
		return to;
	}

	public void setTo(short to) {
		this.to = to;
	}

	public byte getNumLink() {
		return NumLink;
	}

	public void setNumLink(byte NumLink) {
		this.NumLink = NumLink;
	}

	public short getCount() {
		return count;
	}

	public void setCount(short count) {
		this.count = count;
	}

	public float getFact() {
		return fact;
	}

	public void setFact(float fact) {
		this.fact = fact;
	}

	public List<Short> getLink() {
		return link;
	}

	public void setLink(List<Short> link) {
		this.link = link;
	}

	public PMD_IK_RECORD Read(IReadBuffer buffer) throws ReadException {
		if (buffer == null) {
			throw new IllegalArgumentException();
		}
		// is.read((char*)&parent,sizeof(parent))
		this.parent = buffer.readShort();
		// is.read((char*)&to,sizeof(to))
		this.to = buffer.readShort();
		// is.read((char*)&num_link,sizeof(num_link))
		this.NumLink = buffer.readByte();
		// is.read((char*)&count,sizeof(count))
		this.count = buffer.readShort();
		// is.read((char*)&fact,sizeof(fact))
		this.fact = buffer.readFloat();
		// link.resize(num_link)
		// is.read((char*)&link[0],num_link*sizeof(WORD))
		this.link.clear();
		for (int i = 0; i < NumLink; i++) {
			this.link.add(buffer.readShort());
		}
		return this;
	}
}
