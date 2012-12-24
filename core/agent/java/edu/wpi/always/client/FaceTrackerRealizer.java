package edu.wpi.always.client;

import edu.wpi.always.cm.perceptors.*;
import edu.wpi.always.cm.primitives.*;
import edu.wpi.always.cm.realizer.*;

public class FaceTrackerRealizer extends
		PrimitiveRealizerImplBase<FaceTrackBehavior> {
	public static final long FACE_TRACK_TIME_DAMPENING = 1000;

	private final ClientProxy proxy;
	private final EmotiveFacePerceptor perceptor;
	
	private AgentTurn lastDir;
	
	private AgentTurn nextDir;
	private long lastNewNext = 0;

	public FaceTrackerRealizer(FaceTrackBehavior params,
			EmotiveFacePerceptor perceptor, ClientProxy proxy) {
		super(params);
		this.proxy = proxy;
		this.perceptor = perceptor;
	}

	@Override
	public void run() {
		EmotiveFacePerception perception = perceptor.getLatest();
		if (perception != null)
		{
			AgentTurn dir = GazeRealizer.translateToAgentTurn(perception.getLocation());
			if(dir != lastDir) {
				if(dir!=nextDir){
					lastNewNext = System.currentTimeMillis();
					nextDir = dir;
				}
				if(System.currentTimeMillis()-lastNewNext>FACE_TRACK_TIME_DAMPENING){
					// System.out.println(dir+" - "+perception.getLocation().getX());
					proxy.gaze(dir);
					lastDir = dir;
				}
			}
			fireDoneMessage();
		}
	}

}