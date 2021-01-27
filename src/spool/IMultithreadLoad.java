package spool;

public interface IMultithreadLoad extends IMultithreadProcess {
	public void setFilename(String filename);
	
	public String getFilename();
	
	public void process();
	
	public boolean returnsData();
}
