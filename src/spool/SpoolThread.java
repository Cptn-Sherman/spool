package spool;


public class SpoolThread extends Thread {
    public volatile boolean running = true;
    public int id = 0;

    public SpoolThread (int id) {
        this.id = id;
    }

    @Override
    public void run() {
        System.out.println("[spool]: starting thread: ["+ id +"]...");
        while(running) {
            try {
                IMultithreadProcess content;
                synchronized(Spool.contentQueue) {
                    while (Spool.getContentQueue().isEmpty() && running) {
                        Spool.getContentQueue().wait();
                    }
                    if(!running) continue;
                    content = Spool.getContentQueue().remove();
                }
                content.process();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
