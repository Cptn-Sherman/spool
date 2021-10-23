package spool;

public abstract class SpoolAsset {
	private long assetID;
	private boolean referenceCounted;
	private int references;
	protected boolean isReady;
	protected String filename;
	
	public SpoolAsset(boolean referenceCounted) {
		this.referenceCounted = referenceCounted;
		references = 0;
	}
	
	public boolean isReferencedCounted() {
		return referenceCounted;
	}
	
	public int getReferenceCount() {
		return references;
	}
	
	public void updateReferenceCount(int val) {
		references += val;
	}

	public long getAssetID() {
		return assetID;
	}
	
	public void setAssetID(long id) {
		assetID = id;
	}
	
	public String getFilename() {
		return filename;
	}
	
	public void setFilename(String name) {
		filename = name;
	}

	public abstract boolean isReady();
	
	public void setReady(boolean ready) {
		this.isReady = ready;
	}
}
