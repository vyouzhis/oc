package org.ppl.plug.R;

import org.ppl.etc.globale_config;
import org.rosuda.REngine.Rserve.RConnection;
import org.rosuda.REngine.Rserve.RserveException;

public class Rlan {
	public Rlan() {
		// TODO Auto-generated constructor stub
		try {
			globale_config.rcoonnect = new RConnection();
		} catch (RserveException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
