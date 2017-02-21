package devs;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import javafx.scene.text.Text;

/**
 * Un état DEVS.
 * @author Julian
 *
 */
public class DevsState{
	/**
	 * Le nom de l'état
	 */
	private Text name;
	/**
	 * L'ensemble des transitions sans répétitions.
	 */
	private Set<Transition> transitions;
	/**
	 * Le rectangle qui représente l'état graphiquement.
	 */
	private StateRect rect;

	public DevsState(String name,StateRect rect){
		this.name=new Text(name);
		this.rect=rect;
		transitions=new LinkedHashSet<>();
	}

	/**
	 * Ajoute une transition si elle n'existe pas déjà où si il n'y en a pas avec le même évènement.
	 * @param transition La transition à ajouter.
	 * @return True si l'ajout est effectuée avec succès, false sinon.
	 */
	public boolean addTransition(Transition transition){
		for(Transition trans : transitions)
			if(transition.getEvent().toString().equals(trans.getEvent().toString()))
				return false;
		return transitions.add(transition);
	}
	
	public Text getName() {
		return name;
	}

	public void setName(Text name) {
		this.name = name;
	}

	public StateRect getRect() {
		return rect;
	}

	public void setRect(StateRect rect) {
		this.rect = rect;
	}

	public Set<Transition> getTransitions() {
		return transitions;
	}

	public void setTransitions(Set<Transition> transitions) {
		this.transitions = transitions;
	}

}
