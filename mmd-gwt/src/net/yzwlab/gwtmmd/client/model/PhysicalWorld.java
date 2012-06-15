package net.yzwlab.gwtmmd.client.model;

import java.util.ArrayList;
import java.util.List;

import net.yzwlab.javammd.IGL;
import net.yzwlab.javammd.IGLObject;
import net.yzwlab.javammd.IGLTextureProvider;
import net.yzwlab.javammd.IGLTextureProvider.Handler;
import net.yzwlab.javammd.ReadException;
import net.yzwlab.javammd.model.Cube;
import net.yzwlab.javax.vecmath.Vector3f;

import com.bulletphysics.collision.broadphase.BroadphaseInterface;
import com.bulletphysics.collision.broadphase.DbvtBroadphase;
import com.bulletphysics.collision.dispatch.CollisionDispatcher;
import com.bulletphysics.collision.dispatch.DefaultCollisionConfiguration;
import com.bulletphysics.collision.shapes.BoxShape;
import com.bulletphysics.collision.shapes.CollisionShape;
import com.bulletphysics.collision.shapes.StaticPlaneShape;
import com.bulletphysics.dynamics.DiscreteDynamicsWorld;
import com.bulletphysics.dynamics.RigidBody;
import com.bulletphysics.dynamics.RigidBodyConstructionInfo;
import com.bulletphysics.dynamics.constraintsolver.SequentialImpulseConstraintSolver;
import com.bulletphysics.linearmath.DefaultMotionState;
import com.bulletphysics.linearmath.Transform;

/**
 * 物理的な処理を行う世界です。
 */
public class PhysicalWorld implements IGLObject {

	private BroadphaseInterface broadphase;
	private CollisionDispatcher dispatcher;
	private DefaultCollisionConfiguration collisionConfiguration;

	private DiscreteDynamicsWorld dynamicsWorld;

	private List<RigidBodyCube> bodies;

	private float frameNo;

	public PhysicalWorld() {
		this.broadphase = new DbvtBroadphase();
		this.bodies = new ArrayList<RigidBodyCube>();
		this.frameNo = -1.0f;

		collisionConfiguration = new DefaultCollisionConfiguration();
		dispatcher = new CollisionDispatcher(collisionConfiguration);

		SequentialImpulseConstraintSolver solver = new SequentialImpulseConstraintSolver();

		dynamicsWorld = new DiscreteDynamicsWorld(dispatcher, broadphase,
				solver, collisionConfiguration);

		dynamicsWorld.setGravity(new Vector3f(0, -10, 0));

		CollisionShape groundShape = new StaticPlaneShape(
				new Vector3f(0, 1, 0), 1);

		Transform groundTransform = new Transform();
		groundTransform.setIdentity();
		groundTransform.origin.set(0, -1, 0);

		DefaultMotionState groundMotionState = new DefaultMotionState(
				groundTransform);
		RigidBodyConstructionInfo groundRigidBodyCI = new RigidBodyConstructionInfo(
				0, groundMotionState, groundShape, new Vector3f(0, 0, 0));
		RigidBody groundRigidBody = new RigidBody(groundRigidBodyCI);
		dynamicsWorld.addRigidBody(groundRigidBody);

		Cube cube = new Cube();
		cube.setScale(0.1f);
		cube.setColor(0.0f, 0.0f, 1.0f, 1.0f);
		bodies.add(new RigidBodyCube(groundRigidBody, cube));

		for (int y = 0; y < 5; y++) {
			for (int x = 0; x < 5; x++) {
				for (int z = 0; z < 5; z++) {
					int index = x + y + z;

					Transform fallTransform = new Transform();
					fallTransform.setIdentity();
					fallTransform.origin.set(x * 1.0f, 20 + y * 1.0f, z * 1.0f);

					DefaultMotionState fallMotionState = new DefaultMotionState(
							new Transform(fallTransform));
					float mass = 1;
					Vector3f fallInertia = new Vector3f(0, 0, 0);

					CollisionShape fallShape = new BoxShape(new Vector3f(0.5f,
							0.5f, 0.5f));
					fallShape.calculateLocalInertia(mass, fallInertia);
					RigidBodyConstructionInfo fallRigidBodyCI = new RigidBodyConstructionInfo(
							mass, fallMotionState, fallShape, fallInertia);
					RigidBody fallRigidBody = new RigidBody(fallRigidBodyCI);
					dynamicsWorld.addRigidBody(fallRigidBody);

					cube = new Cube();
					if (index % 2 == 0) {
						cube.setColor(1.0f, 0.0f, 0.0f, 1.0f);
					} else {
						cube.setColor(0.0f, 0.0f, 1.0f, 1.0f);
					}
					cube.setScale(0.5f);
					bodies.add(new RigidBodyCube(fallRigidBody, cube));
				}
			}
		}
	}

	@Override
	public void update(float frameNo) {
		if (this.frameNo + 0.0001 >= frameNo) {
			return;
		}
		dynamicsWorld.stepSimulation(1 / 60.f, 10);
		this.frameNo = frameNo;
	}

	@Override
	public void prepare(IGLTextureProvider pTextureProvider, Handler handler)
			throws ReadException {
		if (pTextureProvider == null) {
			throw new IllegalArgumentException();
		}
		;
	}

	@Override
	public void draw(IGL gl) {
		if (gl == null) {
			throw new IllegalArgumentException();
		}
		for (RigidBodyCube body : bodies) {
			body.draw(gl);
		}
	}

	/**
	 * ログ出力を行います。
	 * 
	 * @param message
	 *            メッセージ。nullは不可。
	 */
	public native void log(String message) /*-{
		console.log(message);
	}-*/;

	private class RigidBodyCube {

		private RigidBody body;

		private Cube cube;

		public RigidBodyCube(RigidBody body, Cube cube) {
			if (body == null || cube == null) {
				throw new IllegalArgumentException();
			}
			this.body = body;
			this.cube = cube;
		}

		public void draw(IGL gl) {
			if (gl == null) {
				throw new IllegalArgumentException();
			}
			Transform trans = new Transform();
			body.getMotionState().getWorldTransform(trans);

			cube.setTranslate(trans.origin.x, trans.origin.y, trans.origin.z);
			cube.draw(gl);
		}

	}

}
