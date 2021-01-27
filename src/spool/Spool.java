package spool;

import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

public class Spool {
	private static int threadCount = 0;
	private static AssetThread[] contentThreadPool;
	protected volatile static Queue<IMultithreadProcess> contentQueue;
	protected volatile static Queue<IData> dataQueue;
	
	
	public static void init (int thread_count) {
		threadCount = thread_count;
		contentThreadPool = new AssetThread[threadCount];
		// content loading queue and data processing queue.
		contentQueue = new ConcurrentLinkedQueue<IMultithreadProcess>();
		dataQueue = new ConcurrentLinkedQueue<IData>();

		// start up the thread pool, call start for each thread.
		for (int i = 0; i < threadCount; i++) {
			contentThreadPool[i] = new AssetThread(i);
			contentThreadPool[i].start();
		}
	}
	
	// adds the provided MultithreadProcess or MultithreadLoadable to the content
	// queue to be completed by the thread pool.
	public static void addMultithreadProcess(IMultithreadProcess process) {
		synchronized (contentQueue) {
			contentQueue.add(process);
			contentQueue.notify();
		}
	}
	
	// adds the data to the return queue to be finalized when the content manager is
	// polled.
	public static void addReturnData(IData data) {
		synchronized (dataQueue) {
			dataQueue.add(data);
		}
	}
	
	public static  void stop() {
		System.out.println("[spool]: shutting down threads...");
		signalThreadPoolToStop();
	}
	
	// returns true if any of the threads in the thread pool is still alive, and
	// returns false if they are all no longer alive.
	public static boolean threadPoolIsActive() {
		for (int i = 0; i < contentThreadPool.length; i++) {
			if (contentThreadPool[i].isAlive()) {
				return true;
			}
		}
		return false;
	}
	
	public static Queue<IMultithreadProcess> getContentQueue() {
		return contentQueue;
	}
	
	public static  Queue<IData> getDataQueue() {
		return dataQueue;
	}
	
	// sets all the signals running flag to false and notifies them to wake up.
	private static void signalThreadPoolToStop() {
		// shut down all the loader threads.
		for (AssetThread thread : contentThreadPool) {
			thread.running = false;
		}
		// notify all the threads that are stuck waiting on new content.
		synchronized (contentQueue) {
			contentQueue.notifyAll();
		}
	}
}
