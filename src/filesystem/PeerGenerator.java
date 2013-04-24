package filesystem;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import config.Config;

public class PeerGenerator {

	File peerFile;
	
	public void createPeer(String peerText, String topic, String document) throws IOException {
		
		// D30001.P.10.T.1.APW19981016.0240
		File newPeer = new File(Config.outputDirectory + topic + ".P.10.T.1." + document);
		peerFile = newPeer;
		
		FileWriter fileWriter = new FileWriter(newPeer);
		fileWriter.write(peerText);
		fileWriter.close();
		
	}

	public File getPeerFile() {
		return peerFile;
	}
	
	
}
