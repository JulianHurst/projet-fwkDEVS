package main;

import java.util.LinkedHashSet;
import java.util.Set;

import devs.DevsCouple;
import devs.Port;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Spinner;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import util.Util;

/**
 * Fournit une fenêtre pour la modification d'un état.
 * @author juju
 *
 */
public class EditStateStage extends Stage{

	EditStateStage(MainGui main,DevsCouple state){
				BorderPane BP=new BorderPane();
				VBox Fields;
				HBox Name,Input,Output;
				Text windowTitle=new Text("Edition de l'état");
				HBox titleBox = new HBox();
				Label name,in,out;
				TextField textField = new TextField();
				Button OK,Cancel;
				Spinner<Integer> spIn;
				Spinner<Integer> spOut;
				HBox comboBox=new HBox();

				OK=new Button("OK");
				Cancel=new Button("Annuler");
				Fields=new VBox(10);
				Name=new HBox();
				Input=new HBox();
				Output=new HBox();
				windowTitle.setFont(new Font(40));
				titleBox.getChildren().add(windowTitle);
				titleBox.setAlignment(Pos.CENTER);
				name=new Label("Nom de l'état : ");
				in=new Label("Nombre de ports d'entrée : ");
				out=new Label("Nombre de ports de sortie : ");
				spIn=new Spinner<Integer>(1, 5, state.getInputPorts().size());
				spOut=new Spinner<Integer>(1, 5, state.getOutputPorts().size());
				
				textField.setText(state.getName().getText());
				textField.setOnKeyPressed(event->{
					if(event.getCode().equals(KeyCode.ENTER))
						OK.fire();
				});
				
				spIn.setOnKeyPressed(event->{
					if(event.getCode().equals(KeyCode.ENTER))
						OK.fire();
				});
				
				spOut.setOnKeyPressed(event->{
					if(event.getCode().equals(KeyCode.ENTER))
						OK.fire();
				});

				OK.setOnAction(event->{
					state.setName(textField.getText());
					main.setTitle("fwkDevs ("+state.getName().getText()+")");
					//main.drawName(state);
					Set<Port> newPorts=new LinkedHashSet<>();
					for(int i=0;i<spIn.getValue();i++){
						newPorts.add(new Port(state,"in"+i,Port.Type.INPUT));
					}
					for(int i=0;i<spOut.getValue();i++){
						newPorts.add(new Port(state,"out"+i,Port.Type.OUTPUT));
					}
					int inSize=state.getInputPorts().size();
					int outSize=state.getOutputPorts().size();

					Set<Port> changedPorts=new LinkedHashSet<>();
					for(Port port : state.getPorts()){
						if(!Util.includes(newPorts, port))
							changedPorts.add(port);
					}
					
					//main.removePorts(state,changedPorts);
					
					if(spIn.getValue()!=inSize){
						main.removeInPorts(state);
						state.updateInPorts(newPorts);
						main.drawInPorts(state);
					}
					if(spOut.getValue()!=outSize){
						main.removeOutPorts(state);
						state.updateOutPorts(newPorts);
						main.drawOutPorts(state);
					}
					/*main.removePorts(state);
					state.setPorts(newPorts);
					main.drawPorts(state);*/
					main.updatePortTranslations(state);
					close();
				});
				
				Cancel.setOnAction(event->{
					close();
				});
				
				Name.getChildren().add(name);
				Name.getChildren().add(textField);
				Name.setAlignment(Pos.CENTER);
				Input.getChildren().add(in);
				Input.getChildren().add(spIn);
				Input.setAlignment(Pos.CENTER);
				Output.getChildren().add(out);
				Output.getChildren().add(spOut);
				Output.setAlignment(Pos.CENTER);
				Fields.getChildren().add(Name);
				Fields.getChildren().add(Input);
				Fields.getChildren().add(Output);
				Fields.setAlignment(Pos.CENTER);

				comboBox.getChildren().add(OK);
				comboBox.getChildren().add(Cancel);
				comboBox.setAlignment(Pos.BOTTOM_RIGHT);
				BP.setTop(titleBox);
				BP.setCenter(Fields);
				BP.setBottom(comboBox);
				Scene scene= new Scene(BP, 600, 300, Color.BLACK);
				this.setTitle("Edition d'état");
				setScene(scene);
	}

}
