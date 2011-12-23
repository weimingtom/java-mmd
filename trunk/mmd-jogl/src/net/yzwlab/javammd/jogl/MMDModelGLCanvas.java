package net.yzwlab.javammd.jogl;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.IOException;

import javax.media.opengl.GLCapabilities;
import javax.media.opengl.GLProfile;
import javax.media.opengl.awt.GLCanvas;
import javax.swing.JFrame;
import javax.swing.JPanel;

import net.yzwlab.javammd.jogl.io.FileBuffer;
import net.yzwlab.javammd.model.MMDModel;

/**
 * A minimal program that draws with JOGL in a Swing JFrame using the AWT
 * GLCanvas.
 * 
 * @author Wade Walker
 */
public class MMDModelGLCanvas {

	static {
		// setting this true causes window events not to get sent on Linux if
		// you run from inside Eclipse
		GLProfile.initSingleton(false);
	}

	public static void main(String[] args) throws Exception {
		GLProfile glprofile = GLProfile.getDefault();
		GLCapabilities glcapabilities = new GLCapabilities(glprofile);
		JPanel panel = new JPanel(new FlowLayout());
		final float[] offset = new float[] { 0.2f, -0.2f };
		long baseTime = System.currentTimeMillis();
		MMDModel model = new MMDModel();
		File f = new File(args[0]);
		try {
			model.OpenPMD(new FileBuffer(f.getPath()));
			model.OpenVMD(new FileBuffer("resources/mmd/azunyan207.vmd"));
		} catch (IOException e) {
			e.printStackTrace();
		}

		for (int j = 0; j < model.GetFaceCount(); j++) {
			System.out.println("Face #" + String.valueOf(j + 1) + ": "
					+ model.GetFaceName(j));
		}

		for (int j = 0; j < model.GetBoneCount(); j++) {
			System.out.println("Bone #" + String.valueOf(j + 1) + ": "
					+ model.GetBoneName(j));
		}

		for (int j = 0; j < model.GetIKCount(); j++) {
			String name = model.GetIKTargetName(j);
			System.out.println("IK #" + String.valueOf(j + 1) + ": " + name);
			if (name.indexOf("˜r") > 0) {
				model.SetIKEnabled(j, false);
			}
		}

		for (int i = 0; i < offset.length; i++) {
			final GLCanvas glcanvas = new GLCanvas(glcapabilities);
			glcanvas.addGLEventListener(new MMDDrawer(f.getParentFile(),
					glcanvas, offset, i, model, baseTime));
			glcanvas.setSize(new Dimension((int) (320 * 1.5), (int) (240 * 1.5)));
			panel.add(glcanvas);
		}
		final JFrame jframe = new JFrame("MMD on JOGL");
		jframe.addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent windowevent) {
				jframe.dispose();
				System.exit(0);
			}
		});

		jframe.getContentPane().add(panel, BorderLayout.CENTER);
		jframe.setSize(640, 480);
		jframe.setVisible(true);
	}

}