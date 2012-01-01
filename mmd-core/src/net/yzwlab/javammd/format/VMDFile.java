package net.yzwlab.javammd.format;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import net.yzwlab.javammd.IReadBuffer;
import net.yzwlab.javammd.ReadException;
import net.yzwlab.javammd.model.DataUtils;

public class VMDFile implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public static final byte[] c_hdr_string = "Vocaloid Motion Data 0002"
			.getBytes();

	public static final byte[] c_actor_v2 = "miku".getBytes();
	public static final byte[] c_actor_v3 = "初音ミク".getBytes();

	public static final byte[] c_hokan_data = new byte[] { 0x14, 0x14, 0x14,
			0x14, 0x14, 0x14, 0x14, 0x14, 0x6B, 0x6B, 0x6B, 0x6B, 0x6B, 0x6B,
			0x6B, 0x6B, 0x14, 0x14, 0x14, 0x14, 0x14, 0x14, 0x14, 0x6B, 0x6B,
			0x6B, 0x6B, 0x6B, 0x6B, 0x6B, 0x6B, 0x01, 0x14, 0x14, 0x14, 0x14,
			0x14, 0x14, 0x6B, 0x6B, 0x6B, 0x6B, 0x6B, 0x6B, 0x6B, 0x6B, 0x01,
			0x00, 0x14, 0x14, 0x14, 0x14, 0x14, 0x6B, 0x6B, 0x6B, 0x6B, 0x6B,
			0x6B, 0x6B, 0x6B, 0x01, 0x00, 0x00 };
	public static final byte[] c_hokan_data2 = new byte[] { 0x14, 0x6B, 0x14,
			0x6B, 0x14, 0x6B, 0x14, 0x6B, 0x14, 0x6B, 0x14, 0x6B, 0x14, 0x6B,
			0x14, 0x6B, 0x14, 0x6B, 0x14, 0x6B, 0x14, 0x6B, 0x14, 0x6B, 0x23,
			0x00, 0x00, 0x00, 0x00 };

	protected VMD_HEADER m_vmd_header;

	protected VMD_V2_HEADER m_vmd_v2_header;

	protected VMD_V3_HEADER m_vmd_v3_header;

	protected List<VMD_MOTION_RECORD> m_motion_chunk;

	protected List<VMD_MORP_RECORD> m_morp_chunk;

	protected List<VMD_CAMERA_RECORD> m_camera_chunk;

	protected List<VMD_LIGHT_RECORD> m_light_chunk;

	public VMDFile() {
		this.m_light_chunk = new ArrayList<VMD_LIGHT_RECORD>();
		this.m_camera_chunk = new ArrayList<VMD_CAMERA_RECORD>();
		this.m_morp_chunk = new ArrayList<VMD_MORP_RECORD>();
		this.m_motion_chunk = new ArrayList<VMD_MOTION_RECORD>();
		this.m_vmd_v3_header = null;
		this.m_vmd_v2_header = null;
		this.m_vmd_header = null;
	}

	public void dispose() {
	}

	/**
	 * バッファからデータを開きます。
	 * 
	 * @param fs
	 *            ファイル。nullは不可。
	 * @return データ。
	 * @throws ReadException
	 *             読み込み関係のエラー。
	 */
	public boolean open(IReadBuffer fs) throws ReadException {
		if (fs == null) {
			throw new IllegalArgumentException();
		}
		int size = 0;
		;
		m_vmd_header = (new VMD_HEADER()).Read(fs);
		if (Arrays.equals(DataUtils.getStringData(GetVersion()), new byte[] {
				(byte) 102, (byte) 105, (byte) 108, (byte) 101 })) {
			m_vmd_v2_header = (new VMD_V2_HEADER()).Read(fs);
		} else if (Arrays.equals(DataUtils.getStringData(GetVersion()),
				new byte[] { (byte) 48, (byte) 48, (byte) 48, (byte) 50 })) {
			m_vmd_v3_header = (new VMD_V3_HEADER()).Read(fs);
		} else {
			;
			return false;
		}
		size = fs.readInteger();
		if (size != 0) {
			SetMotionChunkSize(size);
			List<VMD_MOTION_RECORD> buff = GetMotionChunk();
			for (int ai = 0; ai < size; ai++) {
				buff.get(ai).Read(fs);
			}
			;
			Collections.sort(buff, new Comparator<VMD_MOTION_RECORD>() {
				@Override
				public int compare(VMD_MOTION_RECORD o1, VMD_MOTION_RECORD o2) {
					return SortByBoneNameAndFrameNo(o1, o2);
				}
			});
		}
		size = fs.readInteger();
		if (size != 0) {
			SetMorpChunkSize(size);
			List<VMD_MORP_RECORD> buff = GetMorpChunk();
			for (int ai = 0; ai < size; ai++) {
				buff.get(ai).Read(fs);
			}
			;
			Collections.sort(buff, new Comparator<VMD_MORP_RECORD>() {
				@Override
				public int compare(VMD_MORP_RECORD o1, VMD_MORP_RECORD o2) {
					return SortByMorpNameAndFrameNo(o1, o2);
				}
			});
		}
		size = fs.readInteger();
		if (size != 0) {
			SetCameraChunkSize(size);
			List<VMD_CAMERA_RECORD> buff = GetCameraChunk();
			for (int ai = 0; ai < size; ai++) {
				buff.get(ai).Read(fs);
			}
			;
			Collections.sort(buff, new Comparator<VMD_CAMERA_RECORD>() {
				@Override
				public int compare(VMD_CAMERA_RECORD o1, VMD_CAMERA_RECORD o2) {
					return SortByCameraFrameNo(o1, o2);
				}
			});
		}
		// TODO
		// size = fs.readInteger();
		// if (size != 0) {
		// SetLightChunkSize(size);
		// List<VMD_LIGHT_RECORD> buff = GetLightChunk();
		// for (int ai = 0; ai < size; ai++) {
		// buff.get(ai).Read(fs);
		// }
		// ;
		// Collections.sort(buff, new Comparator<VMD_LIGHT_RECORD>() {
		// @Override
		// public int compare(VMD_LIGHT_RECORD o1, VMD_LIGHT_RECORD o2) {
		// return SortByLightFrameNo(o1, o2);
		// }
		// });
		// }
		;
		return true;
	}

	public byte[] GetVersion() {
		return DataUtils.offsetBytes(m_vmd_header.hdr_string, 0x15);
	}

	public VMD_HEADER GetHeader() {
		return m_vmd_header;
	}

	public int GetMotionChunkSize() {
		return m_motion_chunk.size();
	}

	public void SetMotionChunkSize(int size) {
		{
			m_motion_chunk.clear();
			for (int ai = 0; ai < size; ai++) {
				m_motion_chunk.add(new VMD_MOTION_RECORD());
			}
		}
	}

	public List<VMD_MOTION_RECORD> GetMotionChunk() {
		return m_motion_chunk;
	}

	public int GetMorpChunkSize() {
		return m_morp_chunk.size();
	}

	public void SetMorpChunkSize(int size) {
		{
			m_morp_chunk.clear();
			for (int ai = 0; ai < size; ai++) {
				m_morp_chunk.add(new VMD_MORP_RECORD());
			}
		}
	}

	public List<VMD_MORP_RECORD> GetMorpChunk() {
		return m_morp_chunk;
	}

	public int GetCameraChunkSize() {
		return m_camera_chunk.size();
	}

	public void SetCameraChunkSize(int size) {
		{
			m_camera_chunk.clear();
			for (int ai = 0; ai < size; ai++) {
				m_camera_chunk.add(new VMD_CAMERA_RECORD());
			}
		}
	}

	public List<VMD_CAMERA_RECORD> GetCameraChunk() {
		return m_camera_chunk;
	}

	public int GetLightChunkSize() {
		return m_light_chunk.size();
	}

	public void SetLightChunkSize(int size) {
		{
			m_light_chunk.clear();
			for (int ai = 0; ai < size; ai++) {
				m_light_chunk.add(new VMD_LIGHT_RECORD());
			}
		}
	}

	public List<VMD_LIGHT_RECORD> GetLightChunk() {
		return m_light_chunk;
	}

	public int SortByBoneNameAndFrameNo(VMD_MOTION_RECORD rec1,
			VMD_MOTION_RECORD rec2) {
		return DataUtils.compare(rec1.name, rec2.name);
	}

	public int SortByMorpNameAndFrameNo(VMD_MORP_RECORD rec1,
			VMD_MORP_RECORD rec2) {
		if (rec1.frame_no < rec2.frame_no) {
			return -1;
		}
		if (rec1.frame_no > rec2.frame_no) {
			return 1;
		}
		return 0;
	}

	public int SortByCameraFrameNo(VMD_CAMERA_RECORD rec1,
			VMD_CAMERA_RECORD rec2) {
		if (rec1.frame_no < rec2.frame_no) {
			return -1;
		}
		if (rec1.frame_no > rec2.frame_no) {
			return 1;
		}
		return 0;
	}

	public int SortByLightFrameNo(VMD_LIGHT_RECORD rec1, VMD_LIGHT_RECORD rec2) {
		if (rec1.frame_no < rec2.frame_no) {
			return -1;
		}
		if (rec1.frame_no > rec2.frame_no) {
			return 1;
		}
		return 0;
	}

}
