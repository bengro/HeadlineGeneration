package filesystem;

import java.io.File;
import java.util.HashMap;
import java.util.Map.Entry;

import config.Config;

public class FileExplorer extends Thread {

	/**
	 * List of all topic directories found in the docs directory.
	 */
	File[] topics;
	
	/**
	 * Models for each document. Key: document filename.
	 */
	HashMap<String, File[]> models = new HashMap<String, File[]>();
	
	/**
	 * Documents for each topic. Key: topic folder.
	 */
	HashMap<String, File[]> documents = new HashMap<String, File[]>();
	
	/**
	 * Peers for each document. Key: document filename.
	 */
	HashMap<String, File> peers = new HashMap<String, File>();
	
	
	public FileExplorer() {}

	public void process() {	
		readTopics();
		readDocuments();
		readModels();
	}

	private void readTopics() {
		File pathToTopics = new File(Config.ducDirectory + "/docs/");
		File[] topics = pathToTopics.listFiles(new java.io.FileFilter() {
			@Override
			public boolean accept(File pathname) {
				if(pathname.getName().matches("d([0-9]*)t")) {
					return true;
				} else {
					return false;
				}
			}
		});
		
		this.topics = topics;
	}
	
	/**
	 * This method returns all documents (path to document) for every topic.
	 * @return
	 */
	private void readDocuments() {
		
		// for each topic load documents
		for(File directory : topics) {
			File[] documents = directory.listFiles(new java.io.FileFilter() {
				@Override
				public boolean accept(File pathname) {
					if(pathname.getName().matches("(NYT|APW).*\\.[0-9]{4}")) {
						return true;
					} else {
						return false;
					}
				}
			});
			
			this.documents.put(directory.getAbsolutePath(), documents);
		}	
	}
	
	private void readModels() {
		
		// get topic
		for(File topic : this.topics) {
			
			// get document
			for(final File document : this.documents.get(topic.getAbsolutePath())) {
				
				// get TOPIC_PREFIX ... DOCUMENT_SUFFIX
				final String topicPrefix = topic.getName().substring(1, 6);
				
				// collect models for each document, in each topic
				File modelDirectory = new File(Config.ducDirectory + "/eval/models/1/");
				File[] models = modelDirectory.listFiles(new java.io.FileFilter() {
					@Override
					public boolean accept(File pathname) {
						if(pathname.getName().matches("D" + topicPrefix + "(.*)" + document.getName() + "$")) {
							return true;
						} else {
							return false;
						}
					}
				});
	
				this.models.put(document.getAbsolutePath(), models);
			}
			
		}
		
	}
	
	public File[] getTopics() {
		return topics;
	}

	public HashMap<String, File[]> getModels() {
		return models;
	}

	public HashMap<String, File[]> getDocuments() {
		return documents;
	}
	
	public HashMap<String, File> getPeer() {
		return peers;
	}

	public void checkSize() {
		int numberOfModels = 0;
		for(Entry<String, File[]> model : this.models.entrySet()) {
			numberOfModels += model.getValue().length;
		}
		
		int numberOfDocs = 0;
		for(Entry<String, File[]> document : this.documents.entrySet()) {
			numberOfDocs += document.getValue().length;
		}
		
		System.out.println("Topics: " + this.topics.length);
		System.out.println("Documents: " + numberOfDocs);
		System.out.println("Models: " + numberOfModels);
		System.out.println("Peers: " + this.peers.size());
	}

	public void addPeer(File document, File peerFile) {
		peers.put(document.getAbsolutePath(), peerFile);
	}

}
