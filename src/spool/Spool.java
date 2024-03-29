package spool;

import java.util.concurrent.ConcurrentLinkedQueue;

public class Spool {
	private static int threadCount = 0;
	private static SpoolThread[] contentThreadPool;
	protected volatile static ConcurrentLinkedQueue<IMultithreadProcess> contentQueue;
	protected volatile static ConcurrentLinkedQueue<IData> dataQueue;
	
	// Initializer will auto detect the number of supported threads.
	public static void initialize() {
		// get the supported thread pool count or set to one as a minimum, whichever is higher.
		threadCount = Math.max(Runtime.getRuntime().availableProcessors() - 1, 1);
		Spool.initialize(threadCount);
	}
	
	public static void initialize (int thread_count) {
		threadCount = thread_count;
		contentThreadPool = new SpoolThread[threadCount];
		// content loading queue and data processing queue.
		contentQueue = new ConcurrentLinkedQueue<IMultithreadProcess>();
		dataQueue = new ConcurrentLinkedQueue<IData>();
		// start up the thread pool, call start for each thread.
		for (int i = 0; i < threadCount; i++) {
			contentThreadPool[i] = new SpoolThread(i);
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
	
	public static  void stopThreads() {
		System.out.println("[spool]: shutting down " + contentThreadPool.length + " threads...");
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
	
	// returns the content out queue
	public static ConcurrentLinkedQueue<IMultithreadProcess> getContentQueue() {
		return contentQueue;
	}
	
	// returns the data return queue
	public static  ConcurrentLinkedQueue<IData> getDataQueue() {
		return dataQueue;
	}
	
	// sets all the signals running flag to false and notifies them to wake up.
	private static void signalThreadPoolToStop() {
		// shut down all the loader threads.
		for (SpoolThread thread : contentThreadPool) {
			thread.running = false;
		}
		// notify all the threads that are stuck waiting on new content.
		synchronized (contentQueue) {
			contentQueue.notifyAll();
		}
	}
}
