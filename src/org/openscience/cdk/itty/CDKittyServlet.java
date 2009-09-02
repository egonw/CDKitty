package org.openscience.cdk.itty;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.openscience.cdk.interfaces.IMolecularFormula;
import org.openscience.cdk.nonotify.NoNotificationChemObjectBuilder;
import org.openscience.cdk.tools.manipulator.AtomContainerManipulator;
import org.openscience.cdk.tools.manipulator.MolecularFormulaManipulator;

import com.google.wave.api.AbstractRobotServlet;
import com.google.wave.api.Blip;
import com.google.wave.api.Event;
import com.google.wave.api.EventType;
import com.google.wave.api.Range;
import com.google.wave.api.RobotMessageBundle;
import com.google.wave.api.TextView;
import com.google.wave.api.Wavelet;

@SuppressWarnings("serial")
public class CDKittyServlet extends AbstractRobotServlet {

	Pattern pattern = Pattern.compile("mwOf:\\w[\\w|\\d]+");

	@Override
	public void processEvents(RobotMessageBundle bundle) {
		Wavelet wavelet = bundle.getWavelet();
        
	    if (bundle.wasSelfAdded()) {
	      Blip blip = wavelet.appendBlip();
	      TextView textView = blip.getDocument();
	      textView.append("Prefix a molecular formula with 'mwOf:' to gets it mass." +
	    		  "If the formula is not recognized an error will be thrown.");
	    }
	            
	    for (Event e: bundle.getEvents()) {
	      if (e.getType() == EventType.BLIP_SUBMITTED ||
	    	  e.getType() == EventType.BLIP_VERSION_CHANGED) {
	    	  Blip blip = e.getBlip();
	    	  if (!blip.getCreator().equals("CDKitty")) {
	    		  TextView textView = blip.getDocument();
	    		  while (true) {
	    			  Matcher matcher = pattern.matcher(textView.getText());
	    			  if (matcher.find()) {
	    				  String match = matcher.group();
	    				  int start = matcher.start();
	    				  int end = matcher.end();
	    				  
	    				  String formula = match.substring(5);
	    				  IMolecularFormula mf = MolecularFormulaManipulator.getMolecularFormula(
	    					  formula,
	    					  NoNotificationChemObjectBuilder.getInstance()
	    			      );
	    				  
	    				  textView.replace(
	    				      new Range(start, end),
	    				      "" + AtomContainerManipulator.getNaturalExactMass(
	    				    	  MolecularFormulaManipulator.getAtomContainer(mf)
	    				      )
	    				  );
	    			  } else {
	    				  // OK, nothing more found, so return
	    				  return;
	    			  }
	    		  }
	    	  }
	      }
	    }
	}
	
}
