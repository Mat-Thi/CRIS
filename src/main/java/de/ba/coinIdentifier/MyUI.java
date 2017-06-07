package de.ba.coinIdentifier;

import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import javax.servlet.annotation.WebServlet;

import org.opencv.core.Mat;

import com.vaadin.annotations.Theme;
import com.vaadin.annotations.VaadinServletConfiguration;
import com.vaadin.annotations.Widgetset;
import com.vaadin.server.ExternalResource;
import com.vaadin.server.FileResource;
import com.vaadin.server.StreamResource;
import com.vaadin.server.StreamResource.StreamSource;
import com.vaadin.server.VaadinRequest;
import com.vaadin.server.VaadinServlet;
import com.vaadin.ui.Button;
import com.vaadin.ui.Embedded;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.NativeSelect;
import com.vaadin.ui.Notification;
import com.vaadin.ui.Table;
import com.vaadin.ui.UI;
import com.vaadin.ui.Upload.Receiver;
import com.vaadin.ui.Upload.SucceededEvent;
import com.vaadin.ui.Upload.SucceededListener;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;

/**
 * @author Matthias Jostock
 * 
 * Dieses Programm wurde implentiert im Rahmen der 
 * Bachelorarbeit:
 * 
 * Automatische Muenzerkennung mit OpenCV
 * 
 * 
 * This UI is the application entry point. A UI may either represent a browser window 
 * (or tab) or some part of a html page where a Vaadin application is embedded.
 */
@Theme("mytheme")
@Widgetset("de.ba.coinIdentifier.MyAppWidgetset")
public class MyUI extends UI implements Receiver, SucceededListener, StreamSource{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	List<Coin> list;
	final VerticalLayout layout = new VerticalLayout();
	final HorizontalLayout header = new HorizontalLayout();
	final HorizontalLayout content = new HorizontalLayout();
    final VerticalLayout left = new VerticalLayout();
    final VerticalLayout mid = new VerticalLayout();
    
    final Embedded imageOBV = new Embedded("");
    final Embedded imageREV = new Embedded("");
    
    Label datasetLabel = new Label("Kein Dataset geladen!");
    Table table = new Table();
    Table matchTable = new Table();
    
    Button compare = new Button("Starte Vergleich");
    
    public File file;
    
    Coin source = new Coin();
    Statistic statistic = new Statistic();
    
    ByteArrayOutputStream imagebuffer = null;
    BufferedImage imageCompare = null;
    
    String[][] stats;
    List<Coin> coins = new ArrayList<Coin>();
    long time;
	
	/**
	 * GUI wird geladen
	 */
	@Override
    protected void init(VaadinRequest vaadinRequest) {
        
		//DEBUG: Verzeichnisse, der verwendetetn Javabibliotheken
		//Hier muss auch OpenCV enthalten sein.
		//System.out.println(System.getProperty("java.library.path"));
        
		
		compare.setVisible(false);
        compare.setEnabled(false);
        compare.addClickListener( e -> {
        	try {
				compare1();
			} catch (Exception e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
        });
       

        loadLeft();
         left.addComponent(compare);
      
        header.addComponent(new Label("CRIS - Coin Recognition and Identification System"));
        
        left.setMargin(true);
        left.setSpacing(true);
        mid.setMargin(true);
        mid.setSpacing(true);
        
        
        content.setMargin(true);
        content.setSpacing(true);
        content.setSizeFull();
        content.addComponent(left);
        content.addComponent(mid);

        
        layout.addComponent(header);
        layout.addComponent(content);
        
        layout.setMargin(true);
        layout.setSpacing(true);
        
        left.setWidth("40%");
        mid.setWidth("60%");
        
        layout.setWidth("100%");
        
        setContent(layout);
        
        
        // Aufruf, wenn EVALUIERT werden soll.
        try {
			compareForStats();
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
    }


    
    /**
     * Vergleich wird durchgeführt, 
     * Ergebnis wird in einer Tabelle in der GUI angezeigt.
     * @throws IOException
     */
    public void compare1() throws IOException{
    	Notification.show("Vergleich wird durchgeführt",
                "Dieser Vorgang kann einen Augenblick dauern",
                Notification.Type.HUMANIZED_MESSAGE);
    	
    	Compare sc = new Compare();
    	sc.keyPoints(source, true);
    	sc.keyPoints(source, false);
    	
//    	mid.removeComponent(matchTable);
    	
    	matchTable.addContainerProperty("Matches SUM", Integer.class, null);
    	matchTable.addContainerProperty("Matches Obv", Integer.class, null);
    	matchTable.addContainerProperty("Bild Obv",  Embedded.class, null);
    	matchTable.addContainerProperty("Matches Rev", Integer.class, null);
    	matchTable.addContainerProperty("Bild Rev",  Embedded.class, null);
    	matchTable.addContainerProperty("RIC", String.class, null);
    	matchTable.addContainerProperty("URI", String.class, null);
    	matchTable.addContainerProperty("Coin", Coin.class, null);
    	matchTable.setSelectable(true);
    	int itemID = 0;
    	
    	File folder = new File("C:/database");
 		File[] fileArray = folder.listFiles();
 		for(int i=0;i<fileArray.length;i++){
 			File authority = new File(fileArray[i].toString());
 			File[] authorityArray = authority.listFiles();
 			for(int j=0;j<authorityArray.length;j++){
 				File ric = new File(authorityArray[j].toString());
 				File[] ricArray = ric.listFiles();
 				for(int k=0;k<ricArray.length;k++){
 					
 					
 					
 					File id = new File(ricArray[k].toString());
 					File[] idArray = id.listFiles();
 					
 					String[] coinPath = ricArray[k].toString().split("\\\\");
 					
 					Coin compare = new Coin();
 					compare.setObvURL(idArray[0].toString());
 					compare.setRefURL(idArray[1].toString());
 					compare.setRic(coinPath[3]);
 					compare.setURI(ricArray[k].toString());
 					
 					final long startTime = System.currentTimeMillis();
 					
 					if(compare != null){
	 					compare = sc.compare(source, compare, true);
	 					compare = sc.compare(source, compare, false);
 					}
 					
 					final long finishTime = System.currentTimeMillis();
 					
 					compare.setMatchsum(compare.getMatchesObv() + compare.getMatchesRev());
 					
 					compare.setMatchTime((finishTime-startTime));
 					System.out.println("Teste: "+coinPath[3]+"/"+coinPath[4]);
 					System.out.println(compare.getMatchsum()+" Matches in "+(finishTime-startTime)+"ms");
 					
 					if(compare.getMatchsum()>20){
 						Embedded imgOBV = new Embedded();
 						Embedded imgREV = new Embedded();
 						imgOBV.setSource(new FileResource(new File(compare.getObvURL())));
 						imgREV.setSource(new FileResource(new File(compare.getRefURL())));
 						
 						imgOBV.setWidth("100%");
 						imgREV.setWidth("100%");
 						
 						matchTable.addItem(new Object[]{compare.getMatchsum(),compare.getMatchesObv(),imgOBV,compare.getMatchesRev(),imgREV,compare.getRIC(),coinPath[3]+"/"+coinPath[4],compare},itemID);
 						itemID++;
 					}	
 				}
 			}	
 		}
 		
 		//Tabelle wird angezeigt, Listner hinzugefügt
 		matchTable.setVisibleColumns(new Object[] {"Matches SUM", "Matches Obv","Bild Obv","Matches Rev","Bild Rev","RIC"});
 		matchTable.setSortContainerPropertyId("Matches SUM");
    	matchTable.setSortAscending(false);
    	matchTable.addValueChangeListener(e -> {
    		if(matchTable.getContainerProperty(matchTable.getValue(), "Coin").getValue()!=null){
    			showDetail((Coin) matchTable.getContainerProperty(matchTable.getValue(), "Coin").getValue());
    		}
    	}
    	);
    	
    	Window tableWindow = new Window("Ergebnis");
        VerticalLayout tableContent = new VerticalLayout();
        tableContent.setMargin(true);
        tableWindow.setContent(tableContent);
        
        tableContent.addComponent(matchTable);
    	
        tableWindow.center();

        addWindow(tableWindow);
    	
    }
    
    /**
     * Startet die Evaluation
     * Loop für die Testmenge
     * @throws IOException
     */
    public void compareForStats() throws IOException{
    	File folder = new File("C:/databaseHD");
 		File[] fileArray = folder.listFiles();
 		for(int i=0;i<fileArray.length;i++){
 			File authority = new File(fileArray[i].toString());
 			File[] authorityArray = authority.listFiles();
 			for(int j=0;j<authorityArray.length;j++){
 				File ric = new File(authorityArray[j].toString());
 				File[] ricArray = ric.listFiles();
 				for(int k=0;k<ricArray.length;k++){
 					compareForStatsLoop(ricArray[k].getPath());
 				}
 				
 			}
 		}
        
    }
    
    /**
     * 
     * Fuer jede Muenze in der Testmenge wird hier der Vergleich
     * fuer jede Muenze in der Datenbank aufgerufen
     * 
     * @param path Pfad der unbekannten Muenze zur Evaluatio
     * @throws IOException
     */
    public void compareForStatsLoop(String path) throws IOException{
    	System.out.println("überprüfe: "+path);
    	Coin test = new Coin();
    	File id = new File(path);
		File[] idArray = id.listFiles();
		Compare sc = new Compare();
        
		test.setObvURL(idArray[0].toString());
		test.setRefURL(idArray[1].toString());
		test = sc.keyPoints(test, true);
    	test = sc.keyPoints(test, false);
    	
    	//File folder = new File("D:/SD");
    	File folder = new File("C:/databaseHD");
 		File[] fileArray = folder.listFiles();
 		for(int i=0;i<fileArray.length;i++){
 			File authority = new File(fileArray[i].toString());
 			File[] authorityArray = authority.listFiles();
 			for(int j=0;j<authorityArray.length;j++){
 				File ric = new File(authorityArray[j].toString());
 				File[] ricArray = ric.listFiles();
 				for(int k=0;k<ricArray.length;k++){
 					
 					File idLoop = new File(ricArray[k].toString());
 					File[] idArrayLoop = idLoop.listFiles();
 					
 					
 					
 					String[] coinPath = ricArray[k].toString().split("\\\\");
 					Coin compare = new Coin();
 					compare.setObvURL(idArrayLoop[0].toString());
 					compare.setRefURL(idArrayLoop[1].toString());
 					compare.setRic(coinPath[3]);
 					compare.setURI(ricArray[k].toString());
 					
 					Compare sc1 = new Compare();
 					
 					long timeStart = System.currentTimeMillis();
 					
 					if(compare != null && test!= null){
	 					compare = sc1.compare(test, compare, true);
	 					compare = sc1.compare(test, compare, false);
 					}
 					
 					long timeEnd = System.currentTimeMillis();
 					sc1 = null;
 					
 					compare.setMatchsum(compare.getMatchesObv() + compare.getMatchesRev());
 				
 					coins.add(compare);
 					
 					time = time+(timeEnd-timeStart);
 					
 					
 				}
 			}
    
 		}
 		makeStats(test);
    }
    
    /**
     * 
     * für die Auswertung der Muenzvergleiche aus
     * @param test ist die unbekannte Muenze
     */
    public void makeStats(Coin test){
    	Collections.sort(coins);
    	statistic.counter++;
    	
    	Boolean classifed = false;
    	Boolean identified = false;
    	
    	System.out.print(coins.get(1).getRIC() + "; ");
    	System.out.print(coins.get(2).getRIC() + "; ");
    	System.out.print(coins.get(3).getRIC() + "; ");
    	System.out.print(coins.get(4).getRIC() + "; ");
    	System.out.println(coins.get(5).getRIC());
    	System.out.print(coins.get(6).getRIC() + "; ");
    	System.out.print(coins.get(7).getRIC() + "; ");
    	System.out.print(coins.get(8).getRIC() + "; ");
    	System.out.print(coins.get(9).getRIC() + "; ");
    	System.out.println(coins.get(10).getRIC());
    		
    	for(int count = 1;count<=5; count++){
    		
    		String quellenRic = coins.get(0).getRIC();
    		String datenRic = coins.get(count).getRIC();
    		String[] array1 = quellenRic.split("\\.");
    		String[] array2 = datenRic.split("\\.");
    		
    		if(coins.get(count).getRIC().equals(coins.get(0).getRIC())){
    			identified = true;
    			
    		}if(array1[2].equals(array2[2])){
    			classifed = true;
    		}
    	}
    	if(classifed == true){
    			statistic.clasIn5++;		
    	}
    	if(identified == true){
    			statistic.identIn5++;
    			
    	}
    	Boolean classifed10 = false;
    	Boolean identified10 = false;
    	for(int count = 1;count<=10; count++){
    		String quellenRic = coins.get(0).getRIC();
    		String datenRic = coins.get(count).getRIC();
    		String[] array1 = quellenRic.split("\\.");
    		String[] array2 = datenRic.split("\\.");
    	
    		if(coins.get(count).getRIC().equals(coins.get(0).getRIC())){
    			identified10 = true;
    			
    		}if(array1[2].equals(array2[2])){
    			classifed10 = true;
    		}
    	}
    	if(classifed10 == true){
    			statistic.clasIn10++;		
    	}
    	if(identified10 == true){
    			statistic.identIn10++;
    			
    	}
    	
    	coins.clear();
    	System.out.println("Durchläufe: "+statistic.counter);
    	System.out.println("Top 5");
    	System.out.println("Identifiziert: "+statistic.identIn5);
    	System.out.println("Klassifiziert: "+statistic.clasIn5);
    	System.out.println("Top 10");
    	System.out.println("Identifiziert: "+statistic.identIn10);
    	System.out.println("Klassifiziert: "+statistic.clasIn10);
    	System.out.println("Durchschnittliche Berechnungszeit: "+(time/statistic.counter));
    }
    
    /**
     * Vaadin Servlet Klasse
     * Legt u.a. URL Patterns fest
     * 
     * @author Matthes
     *
     */
    @WebServlet(urlPatterns = "/*", name = "MyUIServlet", asyncSupported = true)
    @VaadinServletConfiguration(ui = MyUI.class, productionMode = true)
    public static class MyUIServlet extends VaadinServlet {
	
		private static final long serialVersionUID = 1L;
		
    }
    
    /**
     * Hilfmethode
     * Wandelt ein Mat in ein BufferedImage um
     * @param m OpenCV Mat, welches umgewandelt werden soll
     * @return ein BufferedImage
     */
    public BufferedImage toBufferedImage(Mat m) {
		int type = BufferedImage.TYPE_BYTE_GRAY;
		if (m.channels() > 1) {
			type = BufferedImage.TYPE_3BYTE_BGR;
		}
		int bufferSize = m.channels() * m.cols() * m.rows();
		byte[] b = new byte[bufferSize];
		m.get(0, 0, b); // get all the pixels
		BufferedImage image = new BufferedImage(m.cols(), m.rows(), type);
		final byte[] targetPixels = ((DataBufferByte) image.getRaster()
				.getDataBuffer()).getData();
		System.arraycopy(b, 0, targetPixels, 0, b.length);
		
		return image;

	}
    
    /**
     * 
     * Hilfsmethode
     * Zeigt ein Vaadin Embedded zum Anzeigen eines Bildes anhand einer 
     * URL oder eines Absoluten Pfades an
     * @param url URL oder Pfad eines Bildes
     * @return ein Bild als Embedded
     * @throws IOException
     */
    public Embedded showPicfromURL(String url) throws IOException{
    	ExternalResource img = new ExternalResource (url);
    	Embedded image1 = new Embedded(null, img);
    	image1.setWidth("100%");

    	return image1;
    	
    }
    
    /**
     * Hilfsmethode
     * Wandelt ein OpenCV Mat in ein Vaadin Embedded
     * @param mat Bildformat von OpenCV
     * @return Embedded: Bildformat der vaadin GUI
     * @throws IOException
     */
    public Embedded showPicfromMat(Mat mat) throws IOException{
    	
		imageCompare = toBufferedImage(mat);
    	
		StreamSource imagesource = new MyImageSource();
		((MyImageSource) imagesource).setImage(imageCompare);
		StreamResource resource = new StreamResource(imagesource, mat.toString()+".jpg");
		Embedded imageMat = new Embedded(null, resource);
		
		
		return imageMat;
    	
    }

    /**
     * Vorbereitung um ein beliebiges Bild einer Muenze hochzuladen
     * 
     * Nicht in der Aufgabenstellung gefordert, 
     * dennoch recht interessant, für spaetere Anwendung
     * 
     * Auskommentiert, da interface schon implementiert
     * 
     *
	@Override
	public void uploadSucceeded(SucceededEvent event) {
        imageOBV.setVisible(true);
        imageOBV.setSource(new FileResource(file));
        Compare sc = new Compare();
        System.out.println(file.getAbsolutePath());
    
        Mat matCompare = sc.keyPoints(file.getAbsolutePath());
    	try {
			Embedded keyPic = showPicfromMat(matCompare);
			left.addComponent(keyPic);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}

	@Override
	public OutputStream receiveUpload(String filename, String mimeType) {
		// Create upload stream
        FileOutputStream fos = null; // Stream to write to
        try {
            // Open the file for writing.
            file = new File(filename);
            fos = new FileOutputStream(file);
        } catch (final java.io.FileNotFoundException e) {
            new Notification("Could not open file<br/>",
                             e.getMessage(),
                             Notification.Type.ERROR_MESSAGE)
                .show(Page.getCurrent());
            return null;
        }
        return fos; // Return the output stream to write to
	}

	@Override
	public InputStream getStream() {
		try {
            // Write the image to a buffer. 
            imagebuffer = new ByteArrayOutputStream();
            ImageIO.write(imageCompare, "png", imagebuffer);

            // Return a stream from the buffer. 
            return new ByteArrayInputStream(
                         imagebuffer.toByteArray());
        } catch (IOException e) {
            return null;
		
        }
	}
	*/
	
	
	/**
	 * Die linke Seite der GUI mit Auswahl 
	 * der Testdaten in einem DropDown wird
	 * geladen.
	 */
	public void loadLeft(){
		NativeSelect sample = new NativeSelect("");
 
        
        int itemID = 0;
        File folder = new File("C:/database");
		File[] fileArray = folder.listFiles();
		for(int i=0;i<fileArray.length;i++){
			File authority = new File(fileArray[i].toString());
			File[] authorityArray = authority.listFiles();
			for(int j=0;j<authorityArray.length;j++){
				File ric = new File(authorityArray[j].toString());
				File[] ricArray = ric.listFiles();
				for(int k=0;k<ricArray.length;k++){
					sample.addItem(ricArray[k]);
					String[] ricPath = ricArray[k].toString().split("\\\\");
					sample.setItemCaption(ricArray[k], itemID+ ": "+ricPath[3]+"/"+ricPath[4]);
					itemID++;
				}
			}
			
		}
        
        sample.setNullSelectionAllowed(false);
        sample.setValue(0);
        sample.setImmediate(true);
 
        left.addComponent(sample);
 
        sample.addValueChangeListener(e -> showPic(String.valueOf(e.getProperty().getValue())));
        
		
	}
	
	/**
	 * Zeigt das bild an, welches der Methode anhand des Pfades übergeben wird 
	 * @param path Pfad des Bildes
	 */
	public void showPic(String path){
		File id = new File(path);
		File[] idArray = id.listFiles();
		
		imageOBV.setVisible(true);
        imageOBV.setSource(new FileResource(new File(idArray[0].toString())));
        imageREV.setVisible(true);
        imageREV.setSource(new FileResource(new File(idArray[1].toString())));
        
        source.setObvURL(idArray[0].toString());
        source.setRefURL(idArray[1].toString());
        
		left.addComponents(imageOBV, imageREV);
		
		compare.setEnabled(true);
		compare.setVisible(true);
	}
	
	/**
	 * Zeigt die Detailfenster bei Auswahl in der Tabelle an
	 * Angezeigt wir berechnungszeit und ein Bild der Matches
	 * @param coin in der Tabelle ausgewaehlte Muenze
	 */
	public void showDetail(Coin coin) {
		
		Window subWindow = new Window("Details");
        VerticalLayout subContent = new VerticalLayout();
        subContent.setMargin(true);
        subWindow.setContent(subContent);
        
        subContent.addComponent(new Label("Münze wurde in "+coin.getMatchTime()+ "ms verglichen."));
        
        if(coin.getMatchesPicObv()!=null){
        	try {
				subContent.addComponent(showPicfromMat(coin.getMatchesPicObv()));
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
        }else{
        	subContent.addComponent(new Label("Zu wenig Übereinstimmung:"));
        	subContent.addComponent(new Label("Es wurde kein Bild erzeugt"));
        }

        // Tabelle wird zentriert
        subWindow.center();

        // Tabelle wir angezeigt
        addWindow(subWindow);
		
	}




	/**
	 * Klassen für das Interface Reciever.
	 * Automatisch hinzugefügt.
	 */
	@Override
	public InputStream getStream() {
		// TODO Auto-generated method stub
		return null;
	}




	@Override
	public void uploadSucceeded(SucceededEvent event) {
		// TODO Auto-generated method stub
		
	}




	@Override
	public OutputStream receiveUpload(String filename, String mimeType) {
		// TODO Auto-generated method stub
		return null;
	}

}
