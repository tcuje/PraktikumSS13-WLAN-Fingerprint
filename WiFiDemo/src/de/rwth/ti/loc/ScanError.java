package de.rwth.ti.loc;
import de.rwth.ti.db.Scan;
public class ScanError{
	private Scan scan;
	private double error;
	
	public Scan getScan(){
		return scan;
	}
	public double getError(){
		return error;
	}
	public void setScan(Scan scan){
		this.scan=scan;
	}
	public void setError(double error){
		this.error=error;
	}
	public void setScanError(Scan scan, double error){
		this.scan=scan;
		this.error=error;
	}
	
}