package jp.silverbullet.core.sequncer;

import java.util.concurrent.BlockingQueue;

import jp.silverbullet.core.dependency2.CommitListener;
import jp.silverbullet.core.dependency2.DepenendencyRequest;
import jp.silverbullet.core.dependency2.Id;
import jp.silverbullet.core.sequncer.Sequencer.Actor;

public class MyThread {

	public static final String DEFAULT = "DEFAULT";
	
	private BlockingQueue<DepenendencyRequest> queue;
	private Thread thread;

	public MyThread(BlockingQueue<DepenendencyRequest> dependencyQueue, Thread thread) {
		this.queue = dependencyQueue;
		this.thread = thread;
	}

	public void requestDependency(Id id, String value, boolean forceChange,
			CommitListener commitListener, Actor actor) {
		queue.add(new DepenendencyRequest(id, value, forceChange, commitListener, actor));
	}

}
