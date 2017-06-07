package de.ba.coinIdentifier;

import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;
import java.util.LinkedList;
import java.util.List;

import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.MatOfByte;
import org.opencv.core.MatOfDMatch;
import org.opencv.core.MatOfKeyPoint;
import org.opencv.core.Scalar;
import org.opencv.features2d.DMatch;
import org.opencv.features2d.DescriptorExtractor;
import org.opencv.features2d.DescriptorMatcher;
import org.opencv.features2d.FeatureDetector;
import org.opencv.features2d.Features2d;
import org.opencv.highgui.Highgui;

/**
 * 
 * Klasse zum Vergleichen und finden von Keypoints
 * 
 * @author Matthes
 *
 */
public class Compare {
	
	public static Mat unknownCoinImg;
	public static Mat dbCoinImg;
	
	public Compare(){
		System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
		
	}
	
	/**
	 * Methode zum SIFT/SURF-Vergleich
	 * @param uCoin: unbekannt Muenze
	 * @param uCoin: Muenze aus Datenbank
	 * @param bool: welche Seite wird verglichen: Vorderseite true, Rueckseite false
	**/
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public Coin compare(Coin uCoin, Coin dbCoin, Boolean obv) {
		
	
		unknownCoinImg = null;
		dbCoinImg = null;
		
		
		MatOfKeyPoint uCoinKeyPoints = new MatOfKeyPoint();
		MatOfKeyPoint uCoinDescriptors = new MatOfKeyPoint();
		
		String uCoinURL;
		String dbCoinURL;
		if(obv == true){
			uCoinURL = uCoin.getObvURL();
			dbCoinURL = dbCoin.getObvURL();
			uCoinKeyPoints = uCoin.getObjectKeyPointsObv();
			uCoinDescriptors = uCoin.getObjectDescriptorsObv();
		}else{
			uCoinURL =uCoin.getRefURL();
			dbCoinURL = dbCoin.getRefURL();
			uCoinKeyPoints = uCoin.getObjectKeyPointsRev();
			uCoinDescriptors = uCoin.getObjectDescriptorsRev();
		}
	
		unknownCoinImg = Highgui.imread(uCoinURL, Highgui.CV_LOAD_IMAGE_COLOR);
		dbCoinImg = Highgui.imread(dbCoinURL, Highgui.CV_LOAD_IMAGE_COLOR);
		
	
		FeatureDetector featureDetector = FeatureDetector.create(FeatureDetector.SIFT);
		DescriptorExtractor descriptorExtractor = DescriptorExtractor.create(DescriptorExtractor.SIFT);

		Scalar newKeypointColor = new Scalar(255, 0, 0);
		
		
		/**
		 * Nicht verwendeter Teil, 
		 * dennoch interessant für Debug:
		 * Ausgabe eines Bildes der Input-Muenze
		 * mit gefundenen Keypoints
		 * 
		 * Bilder Finden Verwendung in der Ausarbeitung
		**/	
//		Mat uCoinKeypointImg = new Mat(unknownCoinImg.rows(), unknownCoinImg.cols(), Highgui.CV_LOAD_IMAGE_COLOR);
//		Features2d.drawKeypoints(unknownCoinImg, uCoinKeyPoints, uCoinKeypointImg, newKeypointColor, 0);
		
		MatOfKeyPoint dbCoinKeyPoints = new MatOfKeyPoint();
		MatOfKeyPoint dbCoinDescriptors = new MatOfKeyPoint();
		
		featureDetector.detect(dbCoinImg, dbCoinKeyPoints);
		descriptorExtractor.compute(dbCoinImg, dbCoinKeyPoints, dbCoinDescriptors);
		
		Mat detailMatchImg = new Mat(dbCoinImg.rows() * 2, dbCoinImg.cols() * 2, Highgui.CV_LOAD_IMAGE_COLOR);
		Scalar matchestColor = new Scalar(0, 0, 255);
		
		List matches = new LinkedList();
		DescriptorMatcher descriptorMatcher = DescriptorMatcher.create(DescriptorMatcher.FLANNBASED);

		descriptorMatcher.knnMatch(uCoinDescriptors, dbCoinDescriptors, matches, 2);
		

		LinkedList goodMatchesList = new LinkedList();
		
		//nndrRatio wird in der ersten Teststufe variiert
		float nndrRatio = 0.9f;
		
		for (int i = 0; i < matches.size(); i++){
		   MatOfDMatch matofDMatch = (MatOfDMatch) matches.get(i);
		   DMatch[] dmatcharray = matofDMatch.toArray();
		   DMatch m1 = dmatcharray[0];
		   DMatch m2 = dmatcharray[1];
		
		 
		   
		   if (m1.distance <= m2.distance * nndrRatio){
			   goodMatchesList.addLast(m1);
		
		   }
		}
		

		



		MatOfDMatch goodMatches = new MatOfDMatch();
		goodMatches.fromList(goodMatchesList);
		
		Features2d.drawMatches(unknownCoinImg, uCoinKeyPoints, dbCoinImg, dbCoinKeyPoints, goodMatches, detailMatchImg, matchestColor, newKeypointColor, new MatOfByte(), 2);

		   
		   
		   
		if(obv == true){
			   dbCoin.setMatchesObv(goodMatchesList.size());
			   dbCoin.setMatchesPic(detailMatchImg);
		}else{
			   dbCoin.setMatchesRev(goodMatchesList.size());
		}
		   
		   


		//Pfade werden geloescht, ansonsten koennen 
		//Probleme mit C++ auftreten: Pure Virtual Call
		unknownCoinImg = null;
		dbCoinImg = null;
		
		return dbCoin;
		
		
	}
	
	/**
	 * Methode zur Berechung der der Keypoints und Deskriptoren einer einzelnen
	 * Muenze ohne den Vergleich von compare() ausfuehren zu muessen
	 * @param coin: Muenze, deren Keypoints berechnet werden sollen
	 * @param bool: Vorderseite true, Rueckseite false
	**/
	public Coin keyPoints(Coin coin, Boolean bool){
		Mat coinImage;
		if(bool==true){
			coinImage = Highgui.imread(coin.getObvURL(), Highgui.CV_LOAD_IMAGE_COLOR);
		}else{
			coinImage = Highgui.imread(coin.getRefURL(), Highgui.CV_LOAD_IMAGE_COLOR);
		}
		
		MatOfKeyPoint objectKeyPoints = new MatOfKeyPoint();
		FeatureDetector featureDetector = FeatureDetector.create(FeatureDetector.SIFT);
		featureDetector.detect(coinImage, objectKeyPoints);
		
		MatOfKeyPoint objectDescriptors = new MatOfKeyPoint();
		DescriptorExtractor descriptorExtractor = DescriptorExtractor.create(DescriptorExtractor.SIFT);
		descriptorExtractor.compute(coinImage, objectKeyPoints, objectDescriptors);
			
		
		/**
		 * Nicht verwendeter Teil, 
		 * dennoch interessant für Debugausgabe 
		 * eines Bildes der Input-Münze
		 * mit Keypoints
		**/
		//unbekannte Muenze mit eingezeichneten Keypoints
		//gespeichert und outputImage
		//Mat outputImage = new Mat(coinImage.rows(), coinImage.cols(), Highgui.CV_LOAD_IMAGE_COLOR);
		//Scalar newKeypointColor = new Scalar(0, 0, 255);
		//Features2d.drawKeypoints(coinImage, objectDescriptors, outputImage, newKeypointColor, 0);
		
		if(bool==true){
			coin.setObjectDescriptorsObv(objectDescriptors);
			coin.setObjectKeyPointsObv(objectKeyPoints);
		}else{
			coin.setObjectDescriptorsRev(objectDescriptors);
			coin.setObjectKeyPointsRev(objectKeyPoints);
		}
		
		return coin;
	}
	
	/**
	 * Hilfsmethode zur Umwandlung eines BufferedImage zu Mat
	**/
	public static Mat bufferedImageToMat(BufferedImage bi) {
		  Mat mat = new Mat(bi.getHeight(), bi.getWidth(), CvType.CV_8UC3);
		  byte[] data = ((DataBufferByte) bi.getRaster().getDataBuffer()).getData();
		  mat.put(0, 0, data);
		  return mat;
		}
}
