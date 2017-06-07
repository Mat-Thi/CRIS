package de.ba.coinIdentifier;

import org.opencv.core.Mat;
import org.opencv.core.MatOfKeyPoint;

/**
 * Diese Klasse repraesentiert eine Münze
 * 
 * @author Matthias Jostock
 *
 */
public class Coin implements Comparable<Coin>{
	private String uri;
	private String obvURL;
	private String refURL;
	private String ric;
	private int matchesObv = 0;
	private int matchesRef = 0;
	private int matchsum = 0;
	private Mat pictureObv;
	private Mat pictureRev;
	private long time = 0;
	MatOfKeyPoint objectKeyPointsObv;
	MatOfKeyPoint objectKeyPointsRev;
	MatOfKeyPoint objectDescriptorsObv;
	MatOfKeyPoint objectDescriptorsRev;

	
	public Coin() {
    }

    public Coin(String uri, String obvURL, String refURL) {
    	this.uri = uri;
    	this.obvURL = obvURL;
    	this.refURL = refURL;
    }
        
    public String getURI() {
        return uri;
    }

    public void setURI(String uri) {
        this.uri = uri;
    }
    public String getObvURL() {
        return obvURL;
    }

    public void setObvURL(String obvURL) {
        this.obvURL = obvURL;
    }
    public String getRefURL() {
        return refURL;
    }
    
    public void setRefURL(String refURL) {
    	this.refURL = refURL;
    }
    

	public String getRIC() {
	    return ric;
	}
	
	public void setRic(String ric) {
		this.ric = ric;
	}

    public void setMatchesObv(int matches) {
        this.matchesObv = matches;
    }
    
    public int getMatchesObv() {
    	return matchesObv;
    }
    public void setMatchesRev(int matches) {
        this.matchesRef = matches;
    }
    
    public int getMatchesRev() {
    	return matchesRef;
    }
    
    public Mat getMatchesPicObv(){
    	return pictureObv;
    }
    
    public void setMatchesPic(Mat mat){
    	this.pictureObv = mat;
    }
    public Mat getMatchesPicRev(){
    	return pictureRev;
    }
    
    public void setMatchesPicRev(Mat mat){
    	this.pictureRev = mat;
    }
    public long getMatchTime() {
    	return time;
    }
    public void setMatchTime(long time) {
        this.time = time;
    }
    public MatOfKeyPoint getObjectKeyPointsObv() {
    	return objectKeyPointsObv;
    }
    public void setObjectKeyPointsObv(MatOfKeyPoint objectKeyPointsObv) {
        this.objectKeyPointsObv = objectKeyPointsObv;
    }
    public MatOfKeyPoint getObjectDescriptorsObv() {
    	return objectDescriptorsObv;
    }
    public void setObjectDescriptorsObv(MatOfKeyPoint objectDescriptorsObv) {
        this.objectDescriptorsObv = objectDescriptorsObv;
    }
    public MatOfKeyPoint getObjectKeyPointsRev() {
    	return objectKeyPointsRev;
    }
    public void setObjectKeyPointsRev(MatOfKeyPoint objectKeyPointsRev) {
        this.objectKeyPointsRev = objectKeyPointsRev;
    }
    public MatOfKeyPoint getObjectDescriptorsRev() {
    	return objectDescriptorsRev;
    }
    public void setObjectDescriptorsRev(MatOfKeyPoint objectDescriptorsRev) {
        this.objectDescriptorsRev = objectDescriptorsRev;
    }
    
    public int getMatchsum() {
		return matchsum;
	}

	public void setMatchsum(int matchsum) {
		this.matchsum = matchsum;
	}
	@Override
	public int compareTo(Coin o) {
		int compareQuantity = ((Coin) o).getMatchsum();
		return compareQuantity - this.getMatchsum();
	}

}
