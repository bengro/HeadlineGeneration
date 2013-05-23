package ui.pages;

import edu.stanford.nlp.ling.CoreLabel;
import edu.stanford.nlp.trees.Tree;
import edu.stanford.nlp.trees.TypedDependency;
import edu.stanford.nlp.util.CoreMap;
import generators.DependencyBaseline;

import java.io.File;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.ResourceBundle;
import java.util.Scanner;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.VBox;
import main.Generator;
import ui.ScreensController;

class Topic {
	
	File topic;
	
	public String toString() {
		return topic.getName();
	}
	
	public void setTopic(File topic) {
		this.topic = topic;
	}
	
	public File getTopic() {
		return topic;
	}
}

class Document {
	
	File document;
	
	public String toString() {
		return document.getName();
	}
	
	public void setDocument(File document) {
		this.document = document;
	}
	
	public File getDocument() {
		return document;
	}
}

public class MainApplicationController implements Initializable, IPage {
	
	// TODO:
	// 1. headline generator class combobox
	// show byte length of peer
	// generate rouge xml (save as file)
	
	ScreensController myController;
	
	@FXML 
	private ListView<Topic> topics;
	
	@FXML 
	private ListView<Document> documents;
	
	@FXML 
	private TextArea articleText;
	
	@FXML
	private ComboBox<String> headlineGenerators;
	
	@FXML
	private VBox modelContainer;
	
	@FXML
	private TextField peerText;
	
	@FXML
	private Label statusBox;

	@FXML
	private Button rougeXMLButton;
	
	@FXML
	private Label headlineLength;
	
	@FXML
	private TextArea firstSentence;
	
	@FXML
	private TextArea pennTree;
	
	@FXML
	private TextArea treeDependencies;
	
	
	private Document currentDocument;
	
	@Override
	public void setScreenParent(ScreensController screenController) {
		myController = screenController;
	}
	
	@Override
	public void initialize(URL location, ResourceBundle resources) {	

		final ObservableList<String> classes = FXCollections.observableArrayList();
		classes.add("DependencyBaseline.class");

		headlineGenerators.setItems(classes);
		headlineGenerators.setDisable(true);
		headlineGenerators.getSelectionModel().selectFirst();
		headlineGenerators.valueProperty().addListener(new ChangeListener<String>() {
			@Override
			public void changed(ObservableValue<? extends String> arg0, String arg1, String arg2) {
				if(currentDocument != null) {			
					populateContent(currentDocument);
				}
			}
		});
	}
	
	public void populateTopics() {

		// populate topics
		File[] topicsItems = Generator.fileExplorer.getTopics();
		
		final ObservableList<Topic> filenames = FXCollections.observableArrayList();

		for(File topic : topicsItems) {
			Topic newTopic = new Topic();
			newTopic.setTopic(topic);
			filenames.add(newTopic);
		}
		
		topics.setItems(filenames);
		topics.setOnMouseClicked(new EventHandler<MouseEvent>() {
			@Override
			public void handle(MouseEvent event) {
				// load corresponding documents
				Topic selectedItem = topics.getSelectionModel().getSelectedItem();
				populateDocuments(selectedItem);
			}
		});
	}

	protected void populateDocuments(Topic selectedItem) {
		
		File[] docs = Generator.fileExplorer.getDocuments().get(selectedItem.getTopic().getAbsolutePath());
		
		final ObservableList<Document> filenames = FXCollections.observableArrayList();

		for(File doc : docs) {
			Document newDocument = new Document();
			newDocument.setDocument(doc);
			filenames.add(newDocument);
		}
		
		documents.setItems(filenames);
		documents.setOnMouseClicked(new EventHandler<MouseEvent>() {
			@Override
			public void handle(MouseEvent arg0) {
				Document selectedDocument = documents.getSelectionModel().getSelectedItem();
				populateContent(selectedDocument);
			}
		});
		
	}

	protected void populateContent(Document selectedDocument) {
		this.currentDocument = selectedDocument;
		
		Result result = generateHeadline(DependencyBaseline.class, selectedDocument.getDocument()); 
		
		// TAB 1
		peerText.setText(result.getPeer());
		headlineLength.setText("Bytes: " + result.getPeer().length());

		articleText.setText(result.getArticle());
		
		modelContainer.getChildren().clear();
		for(String model : result.getModels()) {
			TextField textField = new TextField();
			textField.setText(model);
			modelContainer.getChildren().add(textField);
		}
		
		peerText.setText(result.getPeer());
		headlineLength.setText("Bytes: " + result.getPeer().length());
		
		headlineGenerators.setDisable(false);
		
		// TAB 2
		firstSentence.setText(result.getFirstSentence());
		
		treeDependencies.clear();
		for(TypedDependency dependency : result.getDependencies()) {
			treeDependencies.appendText(dependency.toString() + "\n");
		}
		
		pennTree.setText(result.getDependencyTree().toString());
		
	}

	@Override
	public void reactivateScreen() {
		// load topics and documents
		try {
			populateTopics();
		} catch(NullPointerException ex) {
			
		}
	}
	
	class Result {
		
		String article;
		String peer;
		String firstSentence;
		ArrayList<String> models = new ArrayList<String>();
		Collection<TypedDependency> dependencies;
		Tree dependencyTree;
		List<CoreLabel> nerLabels;
		
		public List<CoreLabel> getNerLabels() {
			return nerLabels;
		}
		public void setNerLabels(List<CoreLabel> nerLabels) {
			this.nerLabels = nerLabels;
		}
		public String getArticle() {
			return article;
		}
		public void setArticle(String article) {
			this.article = article;
		}
		public String getPeer() {
			return peer;
		}
		public void setPeer(String peer) {
			this.peer = peer;
		}
		public ArrayList<String> getModels() {
			return models;
		}
		public void setModels(ArrayList<String> models) {
			this.models = models;
		}
		public void setFirstSentence(CoreMap coreMap) {
			this.firstSentence = coreMap.toString();
		}
		public String getFirstSentence() {
			return firstSentence;
		}
		public void setDependencies(Collection<TypedDependency> dependencies) {
			this.dependencies = dependencies;
		}
		public void setDependencyTree(Tree dependencyTree) {
			this.dependencyTree = dependencyTree; 
		}
		public Collection<TypedDependency> getDependencies() {
			return dependencies;
		}
		public Tree getDependencyTree() {
			return dependencyTree;
		}
	}
	
	
	// copied from Runner...
	public<T> Result generateHeadline(Class<T> headlineClass, File article) {
		
		Result result = new Result();
		
		try {
			// run headline generation
			DependencyBaseline baseline = new DependencyBaseline(article);
			
			// get article text
			result.setArticle(baseline.extractArticle(article));
			
			// return headlines - just one in this case.
			String peerText = baseline.returnHeadline();
			result.setPeer(peerText);
			
			// show models
			File[] models = Generator.fileExplorer.getModels().get(article.getAbsolutePath());
			for(File model : models) {
				Scanner modelFile = new Scanner(model);
				String modelHeadline = "";
				while(modelFile.hasNext()) {
					modelHeadline += modelFile.next() + " ";
				}
				modelFile.close();
				result.getModels().add(modelHeadline);
			}
			
			// extract first sentence
			result.setFirstSentence(baseline.getFirstSentence());
			
			// get tree
			result.setDependencyTree(baseline.getDependencyTree());
			
			// extract dependencies
			result.setDependencies(baseline.getDependencies());
			
		} catch(Exception e) {
			e.printStackTrace();
		}
		
		return result;
		
	}
	
}
