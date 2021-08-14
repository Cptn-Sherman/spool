package spool;


public class AssetThread extends Thread {
    public volatile boolean running = true;
    public int id = 0;

    public AssetThread (int id) {
        this.id = id;
    }

    @Override
    public void run() {
        System.out.println("[asset|thread]: starting thread: ["+ id +"]...");

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
